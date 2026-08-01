# Credit Card Fraud Detection

Sistem za otkrivanje potencijalno prevarnih transakcija platnim karticama pomoću neuronskih mreža. Projekat obuhvata React korisnički interfejs, glavni Spring Boot backend, PostgreSQL bazu podataka i izdvojeni DeepNetts AI servis. U repozitorijumu se nalazi i alternativna implementacija AI servisa napravljena pomoću Keras/TensorFlow biblioteka.

## Sadržaj

- [Cilj projekta](#cilj-projekta)
- [Arhitektura sistema](#arhitektura-sistema)
- [Servisi](#servisi)
  - [Glavni backend servis](#glavni-backend-servis)
  - [DeepNetts AI servis](#deepnetts-ai-servis)
  - [Keras AI servis](#keras-ai-servis)
- [Tok obrade transakcija](#tok-obrade-transakcija)
- [DeepNetts model](#deepnetts-model)
  - [Skup podataka i ulazne karakteristike](#skup-podataka-i-ulazne-karakteristike)
  - [Arhitektura neuronske mreže](#arhitektura-neuronske-mreže)
  - [Treniranje i čuvanje modela](#treniranje-i-čuvanje-modela)
  - [Predikcija](#predikcija)
- [Frontend aplikacija](#frontend-aplikacija)
- [Baza podataka](#baza-podataka)
- [Struktura projekta](#struktura-projekta)
- [Korišćene tehnologije](#korišćene-tehnologije)
- [Pokretanje projekta](#pokretanje-projekta)


## Cilj projekta

Cilj projekta je razvoj inteligentnog sistema koji pruža podršku fraud analitičaru u detekciji i obradi potencijalno prevarnih transakcija.

Sistem omogućava:

generisanje i obradu novih transakcija,
procenu verovatnoće prevare pomoću neuronske mreže,
automatsko izdvajanje sumnjivih transakcija,
kreiranje fraud case-ova za dalju obradu,
pregled i filtriranje slučajeva po statusu,
promenu statusa slučaja,
unos komentara analitičara,
blokiranje kartice ili transakcije,
označavanje slučaja kao lažni alarm,
prikaz osnovne i detaljne statistike kroz kartice i grafikone.
Na ovaj način, neuronska mreža se ne koristi izolovano, već kao inteligentna komponenta šire aplikacije za podršku odlučivanju.


## Arhitektura sistema

Sistem je organizovan kao skup odvojenih komponenti:

```text
React frontend (Vite)
        |
        | HTTP / REST
        v
Glavni Spring Boot backend :8080  <---->  PostgreSQL
        |
        | POST /process-daily-transactions
        v
DeepNetts AI servis :8081
        |
        v
DeepNetts model + dataset
```

Frontend komunicira samo sa glavnim backendom preko ruta sa prefiksom `/api`. Backend sadrži poslovnu logiku i pristup bazi, dok zahtev za grupnu AI obradu prosleđuje DeepNetts servisu na portu `8081`.

Pored ovog glavnog toka postoji i Keras AI servis. On predstavlja alternativnu Python/FastAPI implementaciju predikcije i nije istovremeno potreban za standardni tok koji koristi DeepNetts servis.

## Servisi

### Glavni backend servis

Direktorijum `fraud-detection-backend` sadrži centralni Spring Boot servis koji se pokreće na portu `8080`. Njegove glavne odgovornosti su:

- izlaganje REST API-ja frontendu;
- komunikacija sa izdvojenim DeepNetts servisom;
- mapiranje AI odgovora u domenske modele;
- čuvanje transakcija i fraud slučajeva;
- računanje statističkih podataka;
- pregled i ažuriranje slučajeva prevare.

Kada backend dobije rezultat dnevne obrade, svaku vraćenu sumnjivu transakciju čuva u tabeli transakcija. Ako je vrednost `aiPrediction` jednaka `1`, automatski se kreira povezani `FraudCase`.

Backend poseduje i direktnu DeepNetts predikciju kroz `/api/deepnetts/predict`. Ova ruta koristi modelsku biblioteku `deepnetts-model` kao Maven zavisnost, dok se grupna dnevna obrada obavlja pozivom izdvojenog AI servisa.

### DeepNetts AI servis

Direktorijum `deepnetts-ai-service` predstavlja zaseban Spring Boot servis na portu `8081`. Servis učitava prethodno istrenirani `.dnet` model i konfiguraciju preprocessinga, generiše transakcije iz dataseta i nad svakom izvršava predikciju.

Glavna ruta servisa je:

```http
POST /process-daily-transactions
```

Pri jednom pozivu servis obrađuje 100 generisanih transakcija. U odgovor uključuje broj obrađenih transakcija i listu onih za koje je model vratio pozitivnu predikciju. Svaka vraćena transakcija sadrži originalne karakteristike, stvarnu oznaku iz dataseta, izračunatu verovatnoću i AI odluku.

Odvajanje ovog servisa od glavnog backenda omogućava da AI obrada i poslovna logika ostanu jasno razdvojene. Glavni backend ne mora da zna detalje generisanja podataka, normalizacije i rada neuronske mreže — dobija već pripremljen rezultat preko REST poziva.

### Keras AI servis

Direktorijum `keras-ai-service` sadrži alternativnu AI implementaciju napravljenu u Pythonu pomoću FastAPI-ja, TensorFlow/Keras-a, pandas-a i scikit-learn-a. Servis učitava sačuvani Keras model, `StandardScaler` i konfiguraciju modela.

Podržava:

- proveru statusa i učitanog modela;
- informacije i analizu dataseta;
- generisanje sintetičkih transakcija;
- predikciju pojedinačne transakcije;
- grupnu dnevnu obradu;
- ručno i automatsko retreniranje modela na svakih sedam dana;
- metrike kao što su accuracy, precision, recall, F1, ROC AUC, PR AUC i matrica konfuzije.

Ovaj servis koristi prag iz `model_config.json` za pretvaranje verovatnoće u konačnu binarnu odluku. Predstavlja drugu AI varijantu u repozitorijumu, dok je glavni backend trenutno konfigurisan da za dnevnu obradu komunicira sa DeepNetts servisom.

## Tok obrade transakcija

1. Korisnik u React aplikaciji pokreće dnevnu obradu.
2. Frontend šalje `POST` zahtev glavnom backendu na `/api/daily-processing`.
3. `DailyProcessingService` preko `AiServiceClient` klase poziva DeepNetts servis.
4. DeepNetts servis uzima uzorak transakcija iz dataseta i priprema svih osam ulaznih vrednosti.
5. Vrednosti se normalizuju istim maksimumima koji su korišćeni prilikom treniranja.
6. Neuronska mreža izračunava verovatnoću prevare i konačnu predikciju.
7. DeepNetts servis vraća transakcije označene kao sumnjive.
8. Glavni backend čuva vraćene transakcije u PostgreSQL bazi.
9. Za svaku pozitivnu AI predikciju kreira se novi fraud slučaj.
10. Frontend osvežava dashboard, statistiku i listu slučajeva.

## DeepNetts model

Model je izdvojen u Maven modulu `deepnetts-model`. Modul sadrži pripremu dataseta, generisanje iznosa transakcije, definiciju i treniranje mreže, čuvanje modela i klasu za izvršavanje predikcija.

### Skup podataka i ulazne karakteristike

Polazni dataset nalazi se u `datasets/card_transdata.csv`. Njegova proširena verzija, koja uključuje generisani iznos transakcije, čuva se kao `datasets/card_transdata_with_amount.csv`.

Model koristi osam ulaznih karakteristika:

1. `distanceFromHome` — udaljenost transakcije od mesta stanovanja;
2. `distanceFromLastTransaction` — udaljenost od prethodne transakcije;
3. `ratioToMedianPurchasePrice` — odnos cene i medijane uobičajene kupovine;
4. `repeatRetailer` — da li je trgovac već korišćen;
5. `usedChip` — da li je korišćen čip kartice;
6. `usedPinNumber` — da li je korišćen PIN;
7. `onlineOrder` — da li je kupovina obavljena onlajn;
8. `amount` — iznos transakcije.

Ciljna kolona je `fraud`, gde `0` predstavlja regularnu, a `1` prevarnu transakciju.



Redosled karakteristika i maksimalne vrednosti čuvaju se u `preprocessing_config.json`. Ista konfiguracija mora da se koristi i tokom predikcije, jer bi drugačiji redosled ili način skaliranja proizveo neispravan ulaz za model.

### Arhitektura neuronske mreže

DeepNetts model je potpuno povezana feed-forward neuronska mreža:

```text
8 ulaznih neurona
        |
128 neurona, ReLU
        |
64 neurona, ReLU
        |
1 izlazni neuron, Sigmoid
```

Izlaz sigmoid funkcije predstavlja vrednost između `0` i `1`, odnosno procenjenu verovatnoću da je transakcija prevarna. Kao funkcija greške koristi se `CROSS_ENTROPY`.

### Treniranje i čuvanje modela

Trening je podešen sa sledećim parametrima:

- learning rate: `0.001`;
- broj epoha: `30`;
- batch size: `64`;
- podela dataseta: `80/20`.

Nakon treninga mreža se evaluira nad test skupom. Istrenirani model se čuva u `saved_model/fraud_model.dnet`, a parametri preprocessinga u `saved_model/preprocessing_config.json`. Kopije potrebne AI servisu nalaze se i u njegovom `src/main/resources/models` direktorijumu.

### Predikcija

Prediktor učitava sačuvanu mrežu i konfiguraciju skaliranja. Vrednosti nove transakcije raspoređuju se tačno određenim redosledom, skaliraju, a zatim prosleđuju mreži. Dobijena izlazna vrednost koristi se kao verovatnoća prevare, dok se primenom praga dobija konačna odluka `fraud`/`not fraud`, odnosno `1`/`0`.

## Frontend aplikacija

Frontend se nalazi u direktorijumu `fraud-detection-frontend` i razvijen je pomoću React-a i Vite-a. Axios klijent komunicira sa glavnim backendom na adresi `http://localhost:8080/api`.

Aplikacija sadrži sledeće prikaze:

- **Dashboard** — pregled ključnih pokazatelja i pokretanje dnevne obrade;
- **Statistics** — detaljniji statistički i grafički prikaz;
- **Fraud Cases** — lista pronađenih slučajeva i filtriranje po statusu;
- **Fraud Case Details** — detalji transakcije, komentar, status i akcije blokiranja.

Za grafikone se koristi Recharts, dok React Router upravlja navigacijom između stranica.

## Baza podataka

Glavni backend koristi PostgreSQL bazu `fraud_detection_db`. Spring Data JPA i Hibernate obavljaju mapiranje Java entiteta i automatsko ažuriranje šeme preko podešavanja `spring.jpa.hibernate.ddl-auto=update`.

Dva centralna modela su:

- `Transaction` — ulazne karakteristike transakcije, stvarna oznaka, AI verovatnoća i predikcija;
- `FraudCase` — slučaj formiran za sumnjivu transakciju, sa statusom, komentarom i informacijama o blokiranju kartice i transakcije.

`FraudCase` je povezan sa transakcijom koja je dovela do njegovog kreiranja. Status omogućava praćenje slučaja tokom naknadne provere.


## Struktura projekta

```text
credit-card-fraud/
├── datasets/                    # Originalni i prošireni CSV dataset
├── deepnetts-model/             # Treniranje i predikcija DeepNetts modela
├── deepnetts-ai-service/        # Izdvojeni Spring Boot AI servis
├── fraud-detection-backend/     # Glavni Spring Boot REST servis
├── fraud-detection-frontend/    # React/Vite korisnički interfejs
├── keras-ai-service/            # Alternativni FastAPI/Keras AI servis
├── LICENSE
└── README.md
```

## Korišćene tehnologije

- **Java 21 i Spring Boot 3** — glavni backend i DeepNetts AI servis;
- **DeepNetts** — izrada, treniranje i izvršavanje neuronske mreže;
- **Maven** — upravljanje Java modulima i zavisnostima;
- **PostgreSQL** — trajno čuvanje transakcija i slučajeva;
- **Spring Data JPA / Hibernate** — pristup podacima;
- **React 19 i Vite** — frontend aplikacija;
- **Axios** — HTTP komunikacija frontenda;
- **Recharts** — grafikoni i vizualizacija statistike;
- **Python i FastAPI** — alternativni Keras servis;
- **TensorFlow/Keras, pandas i scikit-learn** — alternativni model, priprema podataka i metrike.

## Pokretanje projekta

### Preduslovi

Potrebno je instalirati:

- JDK 21;
- Maven;
- PostgreSQL;
- Node.js i npm;
- Python 3.11, samo ako se pokreće alternativni Keras servis.


### Pokretanje DeepNetts AI servisa

U novom terminalu:

```bash
cd deepnetts-ai-service
./mvnw spring-boot:run
```

Servis će biti dostupan na `http://localhost:8081`.

###  Pokretanje glavnog backenda

U novom terminalu:

```bash
cd fraud-detection-backend
./mvnw spring-boot:run
```

Backend će biti dostupan na `http://localhost:8080`.

### Pokretanje frontenda

U novom terminalu:

```bash
cd fraud-detection-frontend
npm install
npm run dev
```

Vite će u terminalu prikazati lokalnu adresu frontend aplikacije, uobičajeno `http://localhost:5173`.

### Opciono: pokretanje Keras AI servisa

Keras servis nije potreban kada se koristi standardni DeepNetts tok. Za njegovo zasebno pokretanje:

```bash
cd keras-ai-service
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload
```


