# Inteligentni sistem za detekciju i obradu prevara u transakcijama kreditnim karticama

Ovaj projekat predstavlja inteligentni sistem za detekciju i obradu potencijalno prevarnih transakcija kreditnim karticama.

Sistem koristi neuronsku mrežu za procenu verovatnoće da je određena transakcija prevarna. Pored samog modela mašinskog učenja, razvijena je i kompletna aplikacija koja omogućava obradu transakcija, automatsko kreiranje fraud slučajeva i podršku radu fraud analitičara kroz pregled, filtriranje, analizu i ažuriranje slučajeva.

Projekat se sastoji iz tri glavna dela:

- AI servis razvijen u Python-u pomoću FastAPI-ja, koji učitava prethodno trenirani model neuronske mreže i koristi ga za predikciju potencijalnih prevara.
- Backend aplikacija razvijena u Spring Boot-u, koja komunicira sa AI servisom, čuva transakcije i fraud slučajeve u PostgreSQL bazu i izlaže REST API endpoint-e.
- Frontend aplikacija razvijena u React-u, koja omogućava korisniku pregled dashboard-a, obradu fraud slučajeva i analitički prikaz statistike.

## Sadržaj

1. [Opis problema](#1-opis-problema)
2. [Cilj projekta](#2-cilj-projekta)
3. [Korišćene tehnologije](#3-korišćene-tehnologije)
4. [Struktura projekta](#4-struktura-projekta)
5. [Dataset](#5-dataset)
6. [Preprocesiranje podataka](#6-preprocesiranje-podataka)
7. [Arhitektura neuronske mreže](#7-arhitektura-neuronske-mreže)
8. [Trening modela](#8-trening-modela)
9. [Evaluacija modela](#9-evaluacija-modela)
10. [Arhitektura sistema](#10-arhitektura-sistema)
11. [AI servis](#11-ai-servis)
12. [Backend aplikacija](#12-backend-aplikacija)
13. [Frontend aplikacija](#13-frontend-aplikacija)
14. [Baza podataka](#14-baza-podataka)
15. [Pokretanje projekta](#15-pokretanje-projekta)
16. [Primer korišćenja aplikacije](#16-primer-korišćenja-aplikacije)
17. [Zaključak](#17-zaključak)
18. [Licenca](#18-licenca)

## 1. Opis problema

Prevare u transakcijama kreditnim karticama predstavljaju ozbiljan problem u finansijskim sistemima. Zbog velikog broja transakcija koje se izvršavaju svakodnevno, ručna analiza svake pojedinačne transakcije nije praktična. Zbog toga se koriste sistemi zasnovani na mašinskom učenju koji mogu automatski da prepoznaju obrasce ponašanja koji ukazuju na potencijalnu prevaru.

Problem je formulisan kao problem binarne klasifikacije, gde je cilj da se za svaku transakciju odredi da li je regularna ili potencijalno prevarna.

Klase su:

- 0 – regularna transakcija
- 1 – prevarna transakcija

Poseban izazov kod ovog problema je neuravnoteženost podataka, jer je broj regularnih transakcija znatno veći od broja prevarnih transakcija. Zbog toga se pored tačnosti modela analiziraju i metrike kao što su precision, recall, F1-score, ROC-AUC i PR-AUC.

## 2. Cilj projekta

Cilj projekta je razvoj inteligentnog sistema koji pruža podršku fraud analitičaru u detekciji i obradi potencijalno prevarnih transakcija.

Sistem omogućava:

- generisanje i obradu novih transakcija,
- procenu verovatnoće prevare pomoću neuronske mreže,
- automatsko izdvajanje sumnjivih transakcija,
- kreiranje fraud case-ova za dalju obradu,
- pregled i filtriranje slučajeva po statusu,
- promenu statusa slučaja,
- unos komentara analitičara,
- blokiranje kartice ili transakcije,
- označavanje slučaja kao lažni alarm,
- prikaz osnovne i detaljne statistike kroz kartice i grafikone.

Na ovaj način, neuronska mreža se ne koristi izolovano, već kao inteligentna komponenta šire aplikacije za podršku odlučivanju.

## 3. Korišćene tehnologije

U projektu su korišćene sledeće tehnologije:

### AI servis

- Python
- TensorFlow / Keras
- Scikit-learn
- Pandas
- NumPy
- FastAPI
- Uvicorn
- Joblib
- APScheduler

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven

### Frontend

- React
- Vite
- JavaScript
- Axios
- React Router
- Recharts
- CSS

### Alati

- Git
- GitHub
- Visual Studio Code
- Postman / browser / curl za testiranje API-ja

## 4. Struktura projekta

Projekat je organizovan u više foldera, pri čemu svaki deo sistema ima svoju odgovornost.

```text
credit-card-fraud/
│
├── ai-service/
│   ├── data/
│   ├── saved_model/
│   ├── main.py
│   └── requirements.txt
│
├── fraud-detection-backend/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
│
├── fraud-detection-frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
│
├── README.md
└── LICENSE
```

Opis glavnih foldera:

- ai-service
  - Sadrži FastAPI servis i kod za AI komponentu sistema.
  - U ovom delu se učitava trenirani model, generišu nove transakcije i vrši predikcija potencijalnih prevara.
  - Folder saved_model sadrži sačuvani model, scaler i konfiguracioni fajl.

- fraud-detection-backend
  - Sadrži Spring Boot backend aplikaciju.
  - Backend komunicira sa AI servisom, čuva podatke u PostgreSQL bazu i izlaže REST API endpoint-e za frontend.
  - U okviru ovog dela nalaze se model, repository, DTO, service i controller slojevi.

- fraud-detection-frontend
  - Sadrži React frontend aplikaciju.
  - Frontend omogućava korisniku da pokrene obradu transakcija, pregleda fraud case-ove, ažurira slučajeve i prati statistiku kroz kartice i grafikone.

- README.md
  - Sadrži opis projekta, arhitekturu sistema, način pokretanja i objašnjenje funkcionalnosti.

- LICENSE
  - Sadrži open source licencu projekta.

## 5. Dataset

U projektu je korišćen javno dostupan dataset Credit Card Fraud Detection, preuzet sa platforme Kaggle.

Dataset sadrži transakcije kreditnim karticama i njihove karakteristike koje se mogu koristiti za detekciju potencijalnih prevara. Svaka transakcija je opisana numeričkim i binarnim atributima, dok ciljna promenljiva označava da li je transakcija regularna ili prevarna.

Ciljna promenljiva je:

- fraud = 0 – regularna transakcija
- fraud = 1 – prevarna transakcija

Korišćene karakteristike transakcije su:

| Karakteristika | Opis |
|---|---|
| distance_from_home | Udaljenost transakcije od korisnikove uobičajene lokacije |
| distance_from_last_transaction | Udaljenost u odnosu na prethodnu transakciju |
| ratio_to_median_purchase_price | Odnos iznosa transakcije prema uobičajenoj vrednosti kupovine korisnika |
| repeat_retailer | Da li je transakcija izvršena kod prodavca kod kog je korisnik ranije kupovao |
| used_chip | Da li je prilikom transakcije korišćen chip |
| used_pin_number | Da li je korišćen PIN |
| online_order | Da li je transakcija izvršena online |
| fraud | Oznaka da li je transakcija prevarna |

Dataset je neuravnotežen, jer je broj regularnih transakcija znatno veći od broja prevarnih transakcija. Ovo je čest slučaj kod problema detekcije prevara, jer su prevare u realnim finansijskim sistemima ređe od regularnih transakcija.

Zbog neuravnoteženosti podataka, sama accuracy metrika nije dovoljna za procenu kvaliteta modela. Zato su u evaluaciji korišćene dodatne metrike kao što su precision, recall, F1-score, ROC-AUC i PR-AUC.

## 6. Preprocesiranje podataka

Pre treniranja neuronske mreže izvršeno je preprocesiranje podataka kako bi ulazne vrednosti bile pogodne za model.

Pošto originalni dataset ne sadrži eksplicitnu kolonu za iznos transakcije, u projektu je dodata sintetički generisana karakteristika amount. Ona je generisana na osnovu odnosa prema uobičajenoj kupovini korisnika, odnosno na osnovu vrednosti ratioToMedianPurchasePrice.

Na taj način je omogućeno da aplikacija prikazuje iznos transakcije i da fraud analitičar može lakše da proceni finansijski rizik određenog slučaja.

Numeričke karakteristike korišćene u modelu su:

- amount
- distanceFromHome
- distanceFromLastTransaction
- ratioToMedianPurchasePrice

Binarne karakteristike korišćene u modelu su:

- repeatRetailer
- usedChip
- usedPinNumber
- onlineOrder

Za numeričke karakteristike primenjena je logaritamska transformacija pomoću log1p, kako bi se smanjio uticaj ekstremno velikih vrednosti. Nakon toga je primenjena standardizacija pomoću StandardScaler-a.

Standardizacija podrazumeva da se numeričke vrednosti transformišu tako da imaju približno srednju vrednost 0 i standardnu devijaciju 1. Ovo je važno za neuronske mreže jer stabilizuje i ubrzava proces treniranja.

Podaci su podeljeni na trening i test skup, uz očuvanje odnosa klasa pomoću stratifikacije. Stratifikacija je korišćena kako bi i trening i test skup imali približno isti odnos regularnih i prevarnih transakcija kao originalni dataset.

Tok preprocesiranja može se opisati sledećim koracima:

1. Učitavanje dataset-a.
2. Generisanje dodatne karakteristike amount.
3. Razdvajanje ulaznih karakteristika i ciljne promenljive fraud.
4. Podela podataka na trening i test skup uz stratifikaciju.
5. Primena log1p transformacije nad numeričkim karakteristikama.
6. Fitovanje StandardScaler-a samo nad trening skupom.
7. Transformacija trening i test skupa pomoću istog scaler-a.
8. Treniranje neuronske mreže nad pripremljenim podacima.

## 7. Arhitektura neuronske mreže

Za detekciju prevara korišćen je sekvencijalni Keras model. Model je napravljen kao potpuno povezana neuronska mreža.

Ulazne karakteristike modela su:

- amount – iznos transakcije,
- distanceFromHome – udaljenost transakcije od korisnikove uobičajene lokacije,
- distanceFromLastTransaction – udaljenost u odnosu na prethodnu transakciju,
- ratioToMedianPurchasePrice – odnos iznosa transakcije prema uobičajenoj vrednosti kupovine korisnika,
- repeatRetailer – informacija o tome da li je korisnik ranije kupovao kod tog prodavca,
- usedChip – informacija o tome da li je korišćen chip,
- usedPinNumber – informacija o tome da li je korišćen PIN,
- onlineOrder – informacija o tome da li je transakcija izvršena online.

Arhitektura modela po slojevima:

- Ulazni sloj
  - Prima pripremljene karakteristike transakcije.
  - Ulaz čini kombinacija numeričkih i binarnih karakteristika.

- Prvi skriveni Dense sloj
  - Sadrži 128 neurona.
  - Koristi ReLU aktivacionu funkciju.
  - Ovaj sloj uči osnovne obrasce iz ulaznih podataka.

- Batch Normalization nakon prvog sloja
  - Stabilizuje vrednosti koje prolaze kroz mrežu.
  - Pomaže da trening bude stabilniji i efikasniji.

- Dropout nakon prvog sloja
  - Nasumično isključuje deo neurona tokom treninga.
  - Smanjuje rizik od overfitting-a.

- Drugi skriveni Dense sloj
  - Sadrži 64 neurona.
  - Koristi ReLU aktivacionu funkciju.
  - Uči složenije odnose između karakteristika transakcije.

- Batch Normalization nakon drugog sloja
  - Dodatno stabilizuje proces učenja.
  - Pomaže modelu da bolje generalizuje.

- Dropout nakon drugog sloja
  - Smanjuje zavisnost modela od pojedinačnih neurona.
  - Pomaže u sprečavanju preteranog prilagođavanja trening podacima.

- Treći skriveni Dense sloj
  - Sadrži 32 neurona.
  - Koristi ReLU aktivacionu funkciju.
  - Dodatno obrađuje prethodno naučene obrasce i priprema ih za konačnu klasifikaciju.

- Batch Normalization nakon trećeg sloja
  - Stabilizuje izlaze poslednjeg skrivenog sloja.

- Dropout nakon trećeg sloja
  - Dodatno smanjuje rizik od overfitting-a.

- Izlazni sloj
  - Sadrži 1 neuron.
  - Koristi sigmoid aktivacionu funkciju.
  - Vraća vrednost između 0 i 1, koja predstavlja verovatnoću da je transakcija prevarna.

Vrednost bliža 0 ukazuje na regularnu transakciju, dok vrednost bliža 1 ukazuje na veću verovatnoću prevare.

## 8. Trening modela

Model je treniran kao binarni klasifikator, sa ciljem da za svaku transakciju proceni da li je regularna ili potencijalno prevarna.

Za funkciju gubitka korišćena je binary_crossentropy, jer problem ima dve klase. Kao optimizator korišćen je Adam optimizator sa learning rate vrednošću 0.001, koji omogućava stabilno i efikasno podešavanje težina neuronske mreže tokom treninga.

Zbog neuravnoteženosti dataset-a korišćene su class weights vrednosti. Na taj način je prevarnim transakcijama data veća važnost tokom treninga, kako model ne bi favorizovao regularne transakcije samo zato što ih ima znatno više.

Tokom treninga praćene su sledeće metrike:

- precision,
- recall,
- ROC-AUC,
- PR-AUC.

Kod detekcije prevara nije dovoljno posmatrati samo ukupnu tačnost modela, jer dataset nije balansiran. Zbog toga su posebno važne precision, recall i PR-AUC metrike, koje bolje pokazuju koliko model uspešno prepoznaje prevarne transakcije i koliko često pravi lažne alarme.

Korišćen je i Early Stopping, koji zaustavlja trening kada se performanse na validacionom skupu više ne poboljšavaju. U ovom projektu praćena je metrika val_pr_auc, jer je PR-AUC posebno korisna kod neuravnoteženih podataka.

Nakon treninga sačuvani su sledeći elementi:

- Trenirani Keras model
  - Sadrži naučenu arhitekturu neuronske mreže, težine i bias vrednosti.
  - Koristi se u FastAPI servisu za predikciju verovatnoće prevare kod novih transakcija.
  - Zahvaljujući sačuvanom modelu, nije potrebno ponovo trenirati neuronsku mrežu pri svakom pokretanju aplikacije.

- StandardScaler
  - Sadrži parametre skaliranja naučene nad trening skupom.
  - Koristi se da se nove transakcije obrade na isti način kao podaci korišćeni tokom treninga.
  - Ovo je važno jer model očekuje ulazne numeričke vrednosti u istom formatu u kom su bile tokom treniranja.

- Konfiguracioni fajl
  - Sadrži informacije o ulaznim karakteristikama, numeričkim i binarnim kolonama, threshold vrednosti i načinu preprocesiranja.
  - Omogućava AI servisu da zna kojim redosledom treba pripremiti podatke pre slanja u model.
  - Time se smanjuje mogućnost greške između trening faze i faze predikcije.

## 9. Evaluacija modela

Evaluacija modela izvršena je nad test skupom, odnosno nad podacima koje model nije koristio tokom treninga. Cilj evaluacije je bio da se proveri koliko dobro model razlikuje regularne i potencijalno prevarne transakcije.

Pošto je dataset neuravnotežen, za procenu modela korišćeno je više metrika:

- Precision
  - Pokazuje koliki deo transakcija koje je model označio kao prevarne zaista pripada prevarnim transakcijama.
  - Visoka precision vrednost znači da model pravi manje lažnih alarma.

- Recall
  - Pokazuje koliki deo stvarnih prevarnih transakcija je model uspešno pronašao.
  - Visok recall je važan jer je cilj da se što manje stvarnih prevara propusti.

- F1-score
  - Predstavlja balans između precision i recall metrika.
  - Koristan je kada je potrebno istovremeno posmatrati i broj pronađenih prevara i broj lažnih alarma.

- ROC-AUC
  - Pokazuje sposobnost modela da razlikuje regularne i prevarne transakcije kroz različite pragove klasifikacije.

- PR-AUC
  - Posebno je korisna kod neuravnoteženih skupova podataka.
  - Fokusira se na odnos između precision i recall metrika i zbog toga je važna za problem detekcije prevara.

Pored metrika, analizirana je i matrica konfuzije, koja prikazuje četiri vrste predikcija:

- True Negative (TN) – regularna transakcija ispravno označena kao regularna,
- False Positive (FP) – regularna transakcija pogrešno označena kao prevarna,
- False Negative (FN) – prevarna transakcija pogrešno označena kao regularna,
- True Positive (TP) – prevarna transakcija ispravno označena kao prevarna.

U kontekstu detekcije prevara, posebno su važni false negative i false positive slučajevi. False negative znači da je stvarna prevara propuštena, što može izazvati finansijski gubitak. False positive znači da je regularna transakcija označena kao sumnjiva, što povećava broj slučajeva koje fraud analitičar mora ručno da proveri.

Za konačnu klasifikaciju korišćen je threshold, odnosno prag iznad kog se transakcija označava kao potencijalno prevarna. Model kao izlaz daje verovatnoću između 0 i 1, a threshold određuje od koje vrednosti se ta verovatnoća pretvara u klasu 1.

U aplikaciji se čuva verovatnoća prevare koju je model izračunao, kao i konačna AI predikcija. Na osnovu te predikcije backend kreira fraud case za dalju obradu od strane analitičara.

Evaluacija modela pokazuje da neuronska mreža uspešno prepoznaje obrasce koji ukazuju na potencijalnu prevaru i da može da se koristi kao inteligentna komponenta sistema za podršku radu fraud analitičara.

## 10. Arhitektura sistema

Projekat je organizovan kao višeslojna aplikacija koja se sastoji od AI servisa, backend aplikacije, frontend aplikacije i baze podataka.

Glavni delovi sistema su:

- AI servis
  - Razvijen je u Python-u pomoću FastAPI-ja.
  - Učitava prethodno trenirani model neuronske mreže, scaler i konfiguracioni fajl.
  - Generiše nove transakcije, primenjuje isto preprocesiranje kao u fazi treninga i računa verovatnoću prevare.
  - Vraća backend-u samo transakcije koje su označene kao potencijalno prevarne, zajedno sa metrikama obrade.

- Backend aplikacija
  - Razvijena je u Java Spring Boot-u.
  - Komunicira sa AI servisom preko HTTP zahteva.
  - Prima rezultate AI predikcije i čuva sumnjive transakcije u PostgreSQL bazu.
  - Za svaku sumnjivu transakciju kreira fraud case.
  - Izlaže REST API endpoint-e koje frontend koristi za prikaz, filtriranje, ažuriranje i statistiku.

- Frontend aplikacija
  - Razvijena je u React-u.
  - Omogućava korisniku da pokrene dnevnu obradu transakcija.
  - Prikazuje dashboard sa osnovnim statistikama.
  - Omogućava pregled fraud case-ova, filtriranje po statusu i sortiranje po iznosu ili verovatnoći prevare.
  - Omogućava obradu pojedinačnog slučaja kroz promenu statusa, unos komentara, blokiranje kartice ili transakcije i označavanje lažnog alarma.
  - Prikazuje detaljnu statistiku kroz kartice i grafikone.

- PostgreSQL baza podataka
  - Čuva transakcije koje su označene kao potencijalno sumnjive.
  - Čuva fraud case-ove koji su kreirani za dalju obradu.
  - Omogućava trajno čuvanje statusa slučajeva, komentara analitičara i informacija o blokiranju kartice ili transakcije.

Tok obrade u sistemu:

1. Korisnik na frontend aplikaciji pokreće dnevnu obradu transakcija.
2. Frontend šalje zahtev Spring Boot backend-u.
3. Backend poziva FastAPI AI servis.
4. AI servis generiše i analizira nove transakcije pomoću neuronske mreže.
5. AI servis vraća backend-u rezultate predikcije i sumnjive transakcije.
6. Backend čuva sumnjive transakcije u bazu.
7. Backend kreira fraud case za svaku transakciju označenu kao potencijalno prevarnu.
8. Frontend prikazuje nove slučajeve fraud analitičaru.
9. Fraud analitičar pregleda slučaj i donosi odluku o daljoj obradi.


## 11. AI servis

AI servis je razvijen u Python-u pomoću FastAPI framework-a. Njegova uloga je da poveže prethodno trenirani model neuronske mreže sa ostatkom aplikacije i omogući predikciju potencijalno prevarnih transakcija.

Pri pokretanju, AI servis učitava sačuvani Keras model, StandardScaler i konfiguracioni fajl. Na osnovu toga servis može odmah da obrađuje nove transakcije, bez ponovnog treniranja modela pri svakom pokretanju aplikacije.

Glavne funkcionalnosti AI servisa su:

* generisanje novih transakcija za dnevnu obradu,
* priprema transakcija u formatu koji model očekuje,
* izračunavanje verovatnoće prevare pomoću neuronske mreže,
* primena threshold vrednosti za dobijanje konačne AI predikcije,
* izdvajanje transakcija označenih kao potencijalno prevarne,
* vraćanje rezultata Spring Boot backend-u.

Kada backend pošalje zahtev za dnevnu obradu, AI servis generiše zadati broj transakcija, obrađuje ih pomoću neuronske mreže i za svaku računa verovatnoću prevare. Model kao izlaz vraća vrednost između 0 i 1, a na osnovu definisanog threshold-a određuje se da li će transakcija biti označena kao potencijalno prevarna.

AI servis backend-u vraća zbirne informacije o obradi, metrike modela i listu transakcija koje su označene kao sumnjive. Backend zatim te transakcije čuva u bazu i za njih kreira fraud case-ove.

Pored dnevne predikcije, AI servis podržava i retreniranje modela. Retraining endpoint omogućava ponovno treniranje modela nad novim generisanim podacima. Nakon retreniranja, ažurirani model, scaler i konfiguracija se ponovo čuvaju i koriste za naredne predikcije.

### Automatsko retreniranje modela

AI servis koristi scheduler za automatsko retreniranje modela. Automatsko retreniranje je podešeno da se izvršava periodično, na svakih 7 dana.

Cilj automatskog retreniranja je da se model vremenom može prilagođavati novim podacima i promenama u obrascima ponašanja transakcija. Nakon svakog retreniranja, nova verzija modela, scaler i konfiguracioni fajl zamenjuju prethodno sačuvane fajlove.

Na ovaj način sistem ne zavisi samo od početno istreniranog modela, već ima mogućnost periodičnog osvežavanja AI komponente.

## 12. Backend aplikacija

Backend aplikacija je razvijena u Java Spring Boot-u i predstavlja centralni deo sistema koji povezuje AI servis, bazu podataka i frontend aplikaciju.

Glavne uloge backend-a su:

- komunikacija sa FastAPI AI servisom,
- prijem rezultata AI predikcije,
- čuvanje sumnjivih transakcija u PostgreSQL bazu,
- automatsko kreiranje fraud case-ova,
- omogućavanje pregleda i filtriranja fraud case-ova,
- ažuriranje statusa i komentara analitičara,
- čuvanje informacija o blokiranju kartice ili transakcije,
- izračunavanje osnovne i detaljne statistike za frontend.

Backend je organizovan u više slojeva:

- Model sloj
  - Sadrži entity klase koje predstavljaju tabele u bazi.
  - Transaction predstavlja transakciju označenu kao potencijalno sumnjivu.
  - FraudCase predstavlja slučaj koji analitičar obrađuje.
  - CaseStatus definiše moguće statuse slučaja: NEW, IN_REVIEW, CONFIRMED_FRAUD i FALSE_ALERT.

- Repository sloj
  - Koristi Spring Data JPA za komunikaciju sa bazom.
  - TransactionRepository omogućava rad sa transakcijama.
  - FraudCaseRepository omogućava rad sa fraud case-ovima, uključujući filtriranje i brojanje po statusu.

- DTO sloj
  - Sadrži klase koje služe za prenos podataka između AI servisa, backend-a i frontend-a.
  - DTO klase omogućavaju da se ne izlažu direktno svi interni objekti aplikacije, već samo podaci potrebni za određeni zahtev ili odgovor.

- Service sloj
  - Sadrži glavnu poslovnu logiku aplikacije.
  - AiServiceClient poziva FastAPI AI servis.
  - DailyProcessingService obrađuje odgovor AI servisa, čuva sumnjive transakcije i kreira fraud case-ove.
  - StatisticsService računa osnovne dashboard statistike.
  - FraudAnalyticsStatisticsService računa detaljnu statistiku za grafike i analitički prikaz.

- Controller sloj
  - Izlaže REST API endpoint-e koje frontend koristi.
  - Controller-i primaju HTTP zahteve, pozivaju odgovarajuće service ili repository metode i vraćaju JSON odgovore frontendu.

Najvažniji backend endpoint-i su:

- POST /api/daily-processing
  - Pokreće dnevnu obradu transakcija.
  - Backend poziva AI servis, čuva sumnjive transakcije i kreira fraud case-ove.

- GET /api/fraud-cases
  - Vraća sve fraud case-ove.
  - Može se koristiti i sa status filterom, na primer GET /api/fraud-cases?status=NEW.

- GET /api/fraud-cases/{id}
  - Vraća detalje jednog fraud case-a.

- PUT /api/fraud-cases/{id}
  - Ažurira status, komentar i informacije o blokiranju kartice ili transakcije.

- GET /api/statistics
  - Vraća osnovnu statistiku za dashboard.

- GET /api/fraud-analytics-statistics
  - Vraća detaljnu statistiku za Statistics stranicu.

Backend koristi PostgreSQL bazu za trajno čuvanje podataka. Na taj način se statusi slučajeva, komentari i odluke analitičara ne gube nakon gašenja aplikacije.

## 13. Frontend aplikacija

Frontend aplikacija je razvijena u React-u pomoću Vite alata. Njena uloga je da omogući korisniku jednostavan i pregledan rad sa sistemom za detekciju i obradu prevara.

Frontend komunicira sa Spring Boot backend-om preko REST API-ja. Za slanje HTTP zahteva koristi se Axios, dok se za navigaciju između stranica koristi React Router.

Glavne stranice frontend aplikacije su:

- Dashboard
  - Prikazuje osnovni pregled sistema.
  - Sadrži kartice sa brojem fraud case-ova, novih slučajeva i slučajeva koji su u obradi.
  - Omogućava pokretanje dnevne obrade transakcija klikom na dugme.
  - Nakon pokretanja obrade, frontend šalje zahtev backend-u, koji zatim poziva AI servis.

- Fraud Cases
  - Prikazuje listu svih fraud case-ova.
  - Omogućava filtriranje slučajeva po statusu.
  - Omogućava sortiranje po iznosu transakcije i verovatnoći prevare.
  - Svaki slučaj može da se otvori radi detaljne analize.

- Fraud Case Details
  - Prikazuje detalje jedne sumnjive transakcije i povezanog fraud case-a.
  - Prikazuje iznos transakcije, verovatnoću prevare i karakteristike transakcije.
  - Omogućava analitičaru da promeni status slučaja.
  - Omogućava unos komentara.
  - Omogućava blokiranje kartice ili transakcije.
  - Omogućava označavanje slučaja kao lažni alarm.

- Statistics
  - Prikazuje detaljnu statistiku o fraud case-ovima.
  - Sadrži kartice sa brojem slučajeva po statusima i finansijskim pokazateljima.
  - Prikazuje grafikone raspodele statusa, korišćenja PIN-a, chip-a, online transakcija i prodavaca.
  - Prikazuje grafikone za kategorije udaljenosti i odnosa iznosa transakcije prema uobičajenoj kupovini korisnika.

Frontend aplikacija je organizovana u više delova:

- components
  - Sadrži komponente koje se koriste na više mesta u aplikaciji.
  - Primeri su sidebar, status badge, loading komponenta i kartice za prikaz statistike.

- pages
  - Sadrži glavne stranice aplikacije.
  - Svaka stranica predstavlja jednu celinu korisničkog interfejsa.

- services
  - Sadrži funkcije za komunikaciju sa backend API-jem.
  - API pozivi su izdvojeni iz komponenti kako bi kod bio pregledniji i lakši za održavanje.

- utils
  - Sadrži pomoćne funkcije, kao što su formatiranje datuma, iznosa, procenata i numeričkih vrednosti.

Na ovaj način frontend aplikacija omogućava fraud analitičaru da ne radi direktno sa bazom ili API endpoint-ima, već kroz pregledan korisnički interfejs može da prati, analizira i obrađuje sumnjive transakcije.

## 14. Baza podataka

Za trajno čuvanje podataka korišćena je PostgreSQL baza podataka. Backend aplikacija komunicira sa bazom pomoću Spring Data JPA i Hibernate-a.

Baza čuva podatke koji nastaju tokom rada sistema, odnosno transakcije koje su označene kao potencijalno sumnjive i fraud case-ove koji su kreirani za njihovu dalju obradu.

Glavne tabele u bazi su:

- transactions
  - Čuva transakcije koje je AI model označio kao potencijalno sumnjive.
  - Svaka transakcija sadrži karakteristike koje su korišćene za procenu rizika.
  - Čuva se i verovatnoća prevare koju je model izračunao, kao i konačna AI predikcija.

- fraud_cases
  - Čuva slučajeve koje fraud analitičar obrađuje.
  - Svaki fraud case je povezan sa jednom transakcijom.
  - Sadrži status obrade, komentar analitičara i informacije o tome da li su kartica ili transakcija blokirane.

Veza između tabela je organizovana tako da jedan fraud case pripada jednoj transakciji. U tabeli fraud_cases postoji kolona transaction_id, koja pokazuje na odgovarajuću transakciju iz tabele transactions.

Status fraud case-a može imati jednu od sledećih vrednosti:

- NEW – novi slučaj koji još nije obrađen,
- IN_REVIEW – slučaj je u obradi,
- CONFIRMED_FRAUD – analitičar je potvrdio prevaru,
- FALSE_ALERT – slučaj je označen kao lažni alarm.

## 15. Pokretanje projekta

Za pokretanje projekta potrebno je pokrenuti tri dela sistema: AI servis, backend aplikaciju i frontend aplikaciju. Pre pokretanja potrebno je da PostgreSQL baza bude aktivna i da postoji baza podataka koju koristi backend aplikacija.

### 1. Pokretanje AI servisa

AI servis se nalazi u folderu ai-service.

Komande za pokretanje:

```bash
cd ai-service
source venv/bin/activate
uvicorn main:app --reload --port 8000
```

AI servis se pokreće na adresi:

'http://localhost:8000' 

Status servisa može se proveriti na:

'http://localhost:8000/status' 

### 2. Pokretanje backend aplikacije

Backend aplikacija se nalazi u folderu fraud-detection-backend.

Komande za pokretanje:

```bash
cd fraud-detection-backend
./mvnw spring-boot:run
```

Backend aplikacija se pokreće na adresi:

'http://localhost:8080' 

Primer backend endpoint-a za proveru:

'http://localhost:8080/api/statistics' 

### 3. Pokretanje frontend aplikacije

Frontend aplikacija se nalazi u folderu fraud-detection-frontend.

Komande za pokretanje:

```bash
cd fraud-detection-frontend
npm install
npm run dev
```

Frontend aplikacija se pokreće na adresi:

'http://localhost:5173' 

### Redosled pokretanja

Preporučeni redosled pokretanja je:

1. PostgreSQL baza podataka,
2. AI servis,
3. Spring Boot backend,
4. React frontend.

Nakon pokretanja svih delova sistema, korisnik može otvoriti frontend aplikaciju u browser-u i pokrenuti dnevnu obradu transakcija preko dugmeta na Dashboard stranici.

## 16. Primer korišćenja aplikacije

Nakon pokretanja svih delova sistema, korisnik otvara frontend aplikaciju u browser-u.

Tipičan tok korišćenja aplikacije je:

1. Korisnik otvara Dashboard stranicu.
2. Klikom na dugme za dnevnu obradu pokreće se analiza novih transakcija.
3. Frontend šalje zahtev backend-u.
4. Backend poziva AI servis.
5. AI servis generiše i analizira transakcije pomoću neuronske mreže.
6. Transakcije označene kao potencijalno prevarne vraćaju se backend-u.
7. Backend čuva sumnjive transakcije u bazu i kreira fraud case-ove.
8. Novi slučajevi postaju vidljivi na Fraud Cases stranici.
9. Fraud analitičar može da filtrira slučajeve po statusu i sortira ih po iznosu ili verovatnoći prevare.
10. Otvaranjem pojedinačnog slučaja analitičar vidi detalje transakcije i procenu modela.
11. Analitičar može da doda komentar, promeni status slučaja, blokira karticu ili transakciju, ili označi slučaj kao lažni alarm.
12. Sve izmene se čuvaju u PostgreSQL bazi.
13. Na Statistics stranici korisnik može da prati osnovne i detaljne pokazatelje kroz kartice i grafikone.

Ovaj tok pokazuje kako se neuronska mreža koristi kao deo šireg sistema za podršku odlučivanju, dok konačnu odluku o slučaju donosi fraud analitičar.

## 17. Zaključak

U ovom projektu razvijen je inteligentni sistem za detekciju i obradu potencijalno prevarnih transakcija kreditnim karticama. Sistem povezuje neuronsku mrežu, backend aplikaciju, bazu podataka i frontend interfejs u jednu funkcionalnu celinu.

Neuronska mreža se koristi za procenu verovatnoće prevare, dok FastAPI servis omogućava da se model koristi kao posebna AI komponenta. Spring Boot backend povezuje AI servis sa PostgreSQL bazom i frontend aplikacijom, čuva sumnjive transakcije i automatski kreira fraud case-ove. React frontend omogućava fraud analitičaru pregled slučajeva, filtriranje, ažuriranje statusa, unos komentara, blokiranje kartice ili transakcije i praćenje statistike kroz kartice i grafikone.

Projekat pokazuje kako se model mašinskog učenja može primeniti u okviru šire aplikacije za podršku odlučivanju. Umesto da se rezultat modela posmatra izolovano, sistem omogućava da se AI predikcija koristi kao početna procena rizika, dok konačnu odluku donosi fraud analitičar.

Dalji razvoj sistema mogao bi da obuhvati povezivanje sa realnim izvorom transakcija, dodatnu autentifikaciju korisnika, naprednije upravljanje korisničkim ulogama, detaljnije audit logove i unapređenje modela pomoću novih podataka.

## 18. Licenca

Ovaj projekat je objavljen pod MIT licencom. Detalji licence nalaze se u fajlu 'LICENSE'.

