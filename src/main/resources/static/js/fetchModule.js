//Denne bruges til GET requests
function fetchAnyUrl(url) {
    return fetch(url).then(response => response.json()).catch(error =>
        console.error("Handle" +
            "" +
            "d error: ", error))
    //fetch (og response) returnerer et promise, så denne metode skal kaldes i en async
    //metode med await
    //Hele HTTP-response (response) objektet bliver konverteret til JSON-format
    //(Streng/tekstformat som anvendes til at sende over netværk)
}

//Denne bruges til POST, PUT, DELETE requests
async function sendJsonRequest(url, object, httpVerbum) {

    const objectAsJsonString = JSON.stringify(object); //stringify konverterer vores objekt til en JSON-streng
    console.log("omdanner object til JSON streng", objectAsJsonString);
    const fetchOptions = { //Her definerer vi et objekt "fetchOptions" som beskriver hvordan vi vil sende data
        method: httpVerbum,
        headers: {
            "Content-Type": "application/json", //Vi fortæller serveren, at vi sender JSON
        },
        body: objectAsJsonString, //Det er vores objekt konverteret til en JSON-streng som vi sender som data (i body) eller empty body med {}
    };

    const response = await fetch(url, fetchOptions); //HTTP-request sendes med fetch til den angivne url.
    //fetch returnerer et promise til et starte med, men await gør at vi venter på korrekt svar (response)

    if (!response.ok) {
        const errorMessage = response.statusText;
        console.error("Dette er fejl i postObjectAsData", errorMessage); //Hvis serveren returnerer med en fejl, henter vi denne fejlbesked og udskriver den i konsollen
        console.log("Dette er fejl i postObjectAsData", errorMessage); //Hvis serveren returnerer med en fejl, henter vi denne fejlbesked og udskriver den i konsollen
    }
    return response
}

export {fetchAnyUrl, sendJsonRequest}