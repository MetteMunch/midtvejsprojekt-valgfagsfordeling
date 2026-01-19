const navAdmBtn = document.getElementById("nav-button-adm")
const navElevBtn = document.getElementById("nav-button-elev")
const navUnderviserBtn = document.getElementById("nav-button-underviser")

const contentArea = document.getElementById("app-content")

navAdmBtn.addEventListener("click", () => {
    setActiveNav("nav-button-adm")
    loadAdminDashboard()
})

navElevBtn.addEventListener("click", () => {
    setActiveNav("nav-button-elev")
    contentArea.innerHTML = "<h2>Elev-side</h2>"
})

navUnderviserBtn.addEventListener("click", () => {
    setActiveNav("nav-button-underviser")
    contentArea.innerHTML = "<h2>Underviser-side</h2>"
})

// ------------- HJÆLPEMETODER ------------------------

function setActiveNav(id) {
    document.querySelectorAll("nav button").forEach(btn => btn.classList.remove("active"))
    document.getElementById(id).classList.add("active")
}

// -------------- LOAD ADMIN DASHBOARD ----------------------

function loadAdminDashboard() {

    // HTML skabelon
    const template = `
        <div id="page-header">Administration – Dashboard</div>

        <div id="content">
            <aside id="side-menu">
                <ul>
                    <li class="active">Fordeling</li>
                    <li>Manuel håndtering</li>
                </ul>
            </aside>

            <section id="main-content">
            <button id="fordelings-button">Lav fordeling</button>
            <br><br>
            
                <h2>Fordelingsoversigt</h2>

                <div class="stats-grid">

                    <div class="stat-box">
                        <h3>Samlet antal behandlede elever</h3>
                        <p id="stat-processed">...</p>
                    </div>

                    <div class="stat-box">
                        <h3>Beregnet samlet fairness</h3>
                        <p id="stat-quant">...</p>
                        <br>
                        <br>
                        <p class="info-tekst">Fairness-score, jo lavere tal = mere fair</p>
                        <p class="info-tekst">Beregnet ud fra spredning / varians i elev tilfredshed</p>
                    </div>

                    <div class="stat-box">
                        <h3>Fordeling</h3>
                         <table id="stat-distribution-table" class="stat-table">
                            <tbody></tbody>
                         </table>
                    </div>

                    <div class="stat-box">
                        <h3>Valgfag tildeling</h3>
                        <table id="stat-courses-table" class="stat-table">
                            <thead>
                            <tr><th>Valgfag</th><th>Antal deltagere</th></tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>

                </div>

                <h2 class="mt20">Elever uden prioriteringer</h2>
                <ul id="missing-prios-list"></ul>
            </section>
        </div>
    `

    contentArea.innerHTML = template

    const fordelingsBtn = document.getElementById("fordelings-button")

    fordelingsBtn.addEventListener("click", () => {
        startDistributionAndReloadDashboard(fordelingsBtn)
    })
}

// ===== Start fordelingsalgoritmen =====
function startDistributionAndReloadDashboard(button) {

    // Disable button to prevent double-click
    button.disabled = true
    button.textContent = "Fordeling kører..."

    fetch("/reset", { method: "POST" })
        .then(res => {
            if (!res.ok) throw new Error("Fejl i reset endpoint")
            console.log("Reset gennemført")
        })
        .then(() => fetch("/start", { method: "POST" }))
        .then(res => {
            if (!res.ok) throw new Error("Fejl i fordelingsendpoint");
            console.log("Fordeling gennemført");
        })
        // 3) Hent nye data
        .then(() => fetchDataToDashboard())
        .catch(err => console.error("Fejl:", err))
        .finally(() => {
            button.disabled = false;
            button.textContent = "Lav fordeling";
        });
}

function fetchDataToDashboard() {
    // Hent dashboard data fra controller endpoint
    fetch("/dashboard")
        .then(res => res.json())
        .then(data => renderDashboard(data))
        .catch(err => console.error("Fejl i dashboard fetch:", err))
}

// ===== Renderer dashboard data =====
function renderDashboard(data) {

    // Samlet antal behandlede elever
    document.getElementById("stat-processed").textContent = data.processedStudents

    // Total kvantificering
    const quant = document.getElementById("stat-quant");
    const value = data.fairnessScore;
    console.log("value", value);

    quant.textContent = Number(value).toFixed(3);

    // Fjern evt. gamle klasser
    quant.classList.remove("green", "orange", "red");

    if (value <= (data.processedStudents / 54)) {
        quant.classList.add("green");
    } else if (value <= (data.processedStudents / 48)) {
        quant.classList.add("orange");
    } else {
        quant.classList.add("red");
    }

    // ------------------ FORDELING (TABEL) ------------------
    const distributionLabels = {
        threeCourses: "Antal elever som har fået alle tre valgfag tildelt",
        twoCourses: "Antal elever der mangler at få ét valgfag tildelt",
        oneCourse: "Antal elever der mangler at få to valgfag tildelt",
        zeroCourses: "Antal elever der mangler at få tre valgfag tildelt"
    }

    const orderedKeys = ["threeCourses", "twoCourses", "oneCourse", "zeroCourses"]

    const distTable = document.querySelector("#stat-distribution-table tbody")
    distTable.innerHTML = ""

    orderedKeys.forEach(key => {
        const value = data.stats[key]

        const row = document.createElement("tr")

        const labelCell = document.createElement("td")
        labelCell.textContent = distributionLabels[key]

        const valueCell = document.createElement("td")
        valueCell.textContent = value

        row.appendChild(labelCell)
        row.appendChild(valueCell)

        distTable.appendChild(row)
    })

    // ------------------ VALGFAG TILDELING (TABEL) ------------------
    const courseTable = document.querySelector("#stat-courses-table tbody")
    courseTable.innerHTML = ""

    data.courseStats.forEach(c => {
        const row = document.createElement("tr")

        const nameCell = document.createElement("td")
        nameCell.textContent = c.courseName

        const countCell = document.createElement("td")
        countCell.textContent = c.participantCount

        row.appendChild(nameCell)
        row.appendChild(countCell)

        courseTable.appendChild(row)
    })

    // ------------------ ELEVER UDEN PRIORITERINGER ------------------
    const missingList = document.getElementById("missing-prios-list")
    missingList.innerHTML = ""
    data.studentsWithoutPriorities.forEach(s => {
        const li = document.createElement("li")
        li.textContent = `${s.fullName} (${s.email})`
        missingList.appendChild(li)
    })
}

