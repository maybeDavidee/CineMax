# CineMax

CineMax è un'applicazione Java da riga di comando per la gestione di un cinema.

Il programma permette di consultare le proiezioni, effettuare e gestire prenotazioni e amministrare il palinsesto in base al ruolo dell'utente autenticato.

## Autori

* Davide Gallorini - Matricola: 766972 - Sede: VA
* Lorenzo Guidi - Matricola: 766939 - Sede: VA
* Alberto Medizza - Matricola: 765253 - Sede: VA

## Requisiti

* Java 21 o versione compatibile
* Eclipse IDE oppure un altro ambiente in grado di eseguire applicazioni Java

Il progetto non utilizza librerie esterne.

## Avvio del programma

Il punto di ingresso dell'applicazione è la classe:

`cinemax.CineMax`

Da Eclipse:

1. Aprire il progetto CineMax.
2. Aprire `src/cinemax/CineMax.java`.
3. Fare clic con il tasto destro sul file.
4. Selezionare `Run As -> Java Application`.

All'avvio vengono caricati automaticamente utenti, proiezioni e prenotazioni presenti nella cartella `data`.

### Avvio rapido su Windows

Fare doppio clic su `AvviaCineMax.bat`.

Il file avvia automaticamente `CineMax.jar` dalla cartella corretta,
permettendo al programma di trovare i file presenti in `data/`.

## Struttura del progetto

`src/`
Contiene il codice sorgente Java.

`src/cinemax/model/`
Contiene le classi che rappresentano gli oggetti principali del sistema, tra cui utenti, film, proiezioni e prenotazioni.

`src/cinemax/persistence/`
Contiene le classi responsabili della lettura e della scrittura dei dati su file.

`src/cinemax/service/`
Contiene la logica applicativa relativa ad autenticazione, proiezioni e prenotazioni.

`src/cinemax/utils/`
Contiene classi di utilità per input, generazione dei codici e gestione delle password.

`data/`
Contiene i file utilizzati per la persistenza dei dati.

`doc/`
Contiene la documentazione Javadoc generata dal codice sorgente.

## File dati

`data/utenti.txt`
Contiene gli utenti registrati nel sistema. Le password non vengono memorizzate in chiaro ma tramite hash SHA-256.

`data/proiezioni.csv`
Contiene le informazioni relative alle proiezioni cinematografiche e il relativo codice identificativo.

`data/prenotazioni.txt`
Contiene le prenotazioni effettuate dai clienti.

## Tipologie di utenti

### Guest

Un utente non autenticato può:

* visualizzare le proiezioni;
* cercare proiezioni per titolo.

### Cliente

Un cliente autenticato può:

* visualizzare e cercare le proiezioni;
* prenotare biglietti;
* visualizzare le proprie prenotazioni;
* modificare una prenotazione;
* eliminare una prenotazione quando consentito.

Durante la prenotazione vengono controllati la disponibilità dei posti, la data della proiezione e l'età minima richiesta dal film.

### Bigliettaio

Un bigliettaio può:

* visualizzare le prenotazioni relative alle proiezioni odierne;
* cercare una prenotazione tramite codice;
* cercare prenotazioni per cliente;
* cercare prenotazioni per titolo del film;
* cercare prenotazioni per intervallo di date.

### Proiezionista

Un proiezionista può:

* visualizzare le proiezioni;
* effettuare ricerche;
* eseguire ricerche avanzate combinando più criteri;
* aggiungere nuove proiezioni;
* modificare proiezioni;
* eliminare proiezioni quando non sono presenti prenotazioni associate.

Il programma controlla inoltre eventuali sovrapposizioni tra le proiezioni considerando la durata dei film.

## Persistenza

Le modifiche effettuate durante l'esecuzione vengono salvate nei file presenti nella cartella `data`.

I codici delle proiezioni e delle prenotazioni vengono mantenuti in modo da poter identificare correttamente gli elementi anche dopo la chiusura e il successivo riavvio dell'applicazione.

## Note

La capienza della sala è fissata a 200 posti.

Il programma utilizza un'interfaccia testuale tramite console.
