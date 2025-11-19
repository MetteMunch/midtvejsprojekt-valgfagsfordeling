# Fordelingsalgoritme med en form for fairness (Greedy with fairness)
Jeg har med denne applikation ville prøve at lave en metode, som på en retfærdig måde, kan hjælpe med at fordele elever ud på deres valgfag afhængig af deres prioritering.
Processen (distributionGreedyWithFairness()) kan i korte træk beskrives som vist nedenstående. 
Algoritmen er lavet ud fra følgende forudsætninger: 
- der er 8 forskellige valgfag.
- hveert valgfag skal som udgangspunkt have minimum 8 og max 35 elever. Tanken er dog, at ved den efterfølgende manuelle tjek og restfordeling, så vil dette kunne overrules (ikke implementeret på nuværende tidspunkt).
- hver elev skal have 3 valgfag.
- hver elev indgiver 5 prioriteter, rangerende fra 1. - 5. med nummer 1 som det valgfag vedkommende helst vil have.
## Beskrivelse algoritmens proces:
- der vælges en tilfældig elev ud fra alle de elever som har indgivet prioriteter til valgfag
- eleven får tildelt det første valgfag som det er muligt i forhold til plads på holdet, startende med tjek af prioritet 1.
- Når en elev har fået tildelt ét valgfag, så fjernes eleven fra listen og puttes i en ny liste, som er afhængig af om eleven fik den ønskede prioritet opfyldt eller om det var nødvendigt at gå længere ned i prioriteter. Hvis ikke eleven fik den prioritet opfyldt, som vi er kommet til i processen, men en lavere prioritet, så kommer eleven altså i en pulje, hvorfra der trækkes først i næste rundes håndteringer (dog stadig random udvælgelse i puljen).
- Sådan forgår det i tre runder for hver elev. Herefter vil nogen være blevet tilknyttet 3 valgfag, men der vil også være nogen, som er endt på en liste til manuel håndtering, fordi de mangler at få tildelt et eller flere valgfag. De vil så herefter manuelt skulle tildeles på valgfag, så de har samlet 3 valgfag, ligesom de elever der slet ikke har indleveret nogle prioriteringer også skal fordeles manuelt til sidst.
<img width="1335" height="824" alt="image" src="https://github.com/user-attachments/assets/facdb0c5-ae45-4879-9977-d30c259ffc33" />

  Eftersom applikationen vælger random elev fra de forskellige lister, så er det netop ArrayList, som jeg anvender til de forskellige elev lister.
  Jeg starter dog i fordelingsalgoritmen med at hente alle valgfag, så der ikke skal laves opslag ned i databasen for hver elev der håndteres, når der skal tjekkes for plads på holdet. Disse valgfag er gemt i HashMap, hvor key er courseId, så der hurtigt kan laves opslag. Eftersom der godt nok kun er 8 forskellige valgfag, har HashMap contra ArrayList nok ikke den store performance betydning her. Men at alle valgfag er hentet inden, og der ikke skal laves database kald minimum tre gange pr elev, det har stor betydning for performance.
  Som I kan se, så har jeg en stor samlet service-klasse (AdministrationService), hvori fordelingsalgoritmen også findes. Almindeligvis ville jeg opdele i flere service-klasser alt efter hvad metoderne skulle behandle, så det ikke bliver så uoverskueligt. Dette startede jeg også med, men fik så problemer i forhold til at benytte @Transactional og mulighederne i Jpa med at gemme direkte i DB når der var ændring i en Entity. For at undgå at skulle tilføre manuel håndtering af save() i metoderne (og de flere DB kald), så valgte jeg at indarbejde det hele i én service-klasse. Jeg har prøvet at lave opdeling med overskrifter og forskellige kommentarer for overblikket skyld.

# Køretidskompleksitet - fordelingsalgoritmen
Her vil jeg prøve at gennemgå de forskellige metoder, som udgør fordelingsalgoritmen.
Jeg arbejder med følgende variabler:
n = antal elever
p = antal prioriteter pr. elev
c = antal valgfag

<u>preloadAllCourses()</u>
Alle valgfag hentes én gang og gemmes i HashMap => O(c) = c er meget lille (8), så det svarer i praksis til O(1), hvilket er konstant tid uanset input.

<u>allStudentsWithPriorities()</u>
Jeg laver en stream på allStudents listen, så jeg kan filtrere dem fra, som ikke har indgivet prioriteter => O(n) = lineær tid afhængig af input

<u>distributionGreedyWithFairness() – selve fordelingsalgoritmen</u>
ProcessRound()-delen:
Hver elev håndteres 3 gange (runder), hvor der sker følgende:
Eleven fjernes fra den oprindelige liste, hvilket sker i getRandom funktionen => O(n)
Tjek af hvilken prioritet, at vi er kommet til for at finde korrekt valgfag til tjek (for-loop) => O(p), eftersom p er konstant, så O(1)
checkIfAvailable(), som er opslag i HashMap => O(1)
setFulfilled(), som ændrer boolean variabel på prioritet => O(1)
incrementHandlingCount(), hvor det tælles op på variablen, der viser hvor mange gange eleven er blevet håndteret => O(1)
addCount(), hvor der tælles op på variablen, der gemmer hvor mange der deltager på valgfaget => O(1)
Eleven tilføjes til rette liste til næste runde eller manuel håndtering  => O(1)
Samlet kompleksitet for denne del er O(n), og det gør vi for n elever, dvs. samlet = O(n^2)
Så algoritmen har egentligt kvadratisk vækst, men eftersom n (antal elever) er forholdsvis lille (omkring 90-100), så er runtime stadig hurtig og algoritmen dermed effektiv.

# Køretidskompleksitet – statistikmetoder
I applikationen er det muligt at køre fordelingen flere gange, hvilket jo vil give forskellige resultater (da det er random rækkefølge eleverne behandles i). Jeg har derfor lavet en kvantificeringsmetode og forskellige andre hjælpemetoder, som skal gøre det nemmere for studieadministratoren at vælge den rette fordeling.

<u>getTotalQuantificationScore()</u>
Denne laver en stream på allStudents listen, og for hver elev køres metoden calculateStudentScore(), som gennemgår hver elevs prioritetsliste for at tjekke hvor mange prioriteter, der er blevet fulfilled = true. Ud fra dette gives en score, som sammentælles og returneres. Jo tættere på 0 jo bedre score (dvs. flest mulige elever, har fået mange af deres første prioriteter opfyldt).
Samlet kompleksitet her er O(1)*O(n) = O(n) så lineær

<u>getDistributionStats()</u>
I denne metode ønsker jeg at returnere data, der viser hvor mange elever, der har fået alle tre valgfag tildelt, hvor mange der mangler at få 1 valgfag tildelt, hvor mange der mangler at få 2 valgfag tildelt og hvor mange der mangler at få alle 3 valgfag tildelt (af de behandlede elever).
Jeg laver i denne fire streams, som alle gennemløber alle elver, for at kunne filtrere og lave count. Denne ville nok kunne optimeres til en enkel stream med en switch. Selvom metoden udfører fire gennemløb af listen, reduceres O(4n) til O(n), køretiden stiger altså lineært i takt med antal elever.
