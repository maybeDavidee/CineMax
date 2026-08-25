/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import cinemax.model.Bigliettaio;
import cinemax.model.Cliente;
import cinemax.model.Proiezione;
import cinemax.model.Prenotazione;
import cinemax.model.Proiezionista;
import cinemax.model.Utente;
import cinemax.persistence.PrenotazioneRepository;
import cinemax.persistence.ProiezioneRepository;
import cinemax.persistence.UtenteRepository;
import cinemax.service.AuthService;
import cinemax.service.PrenotazioneService;
import cinemax.service.ProiezioneService;
import cinemax.utils.InputUtils;
import java.time.LocalDateTime;
import cinemax.model.Film;

/**
 * Classe principale dell'applicazione CineMax.
 *
 * Contiene il metodo main e gestisce la navigazione
 * iniziale tra login, registrazione e accesso guest.
 */
public class CineMax {

    private static Scanner scanner;

    private static AuthService authService;
    private static ProiezioneService proiezioneService;
    private static PrenotazioneService prenotazioneService;

    /**
     * Avvia l'applicazione CineMax.
     *
     * @param args argomenti della riga di comando
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        inizializzaApplicazione();

        System.out.println("==============================");
        System.out.println("         CINEMAX");
        System.out.println("==============================");

        menuPrincipale();

        scanner.close();

        System.out.println("\nApplicazione terminata.");
    }

    /**
     * Crea i repository e i servizi utilizzati
     * dall'applicazione.
     */
    private static void inizializzaApplicazione() {
        UtenteRepository utenteRepository =
                new UtenteRepository();

        ProiezioneRepository proiezioneRepository =
                new ProiezioneRepository();

        PrenotazioneRepository prenotazioneRepository =
                new PrenotazioneRepository();

        authService =
                new AuthService(utenteRepository);

        proiezioneService =
                new ProiezioneService(proiezioneRepository);

        prenotazioneService =
                new PrenotazioneService(
                        prenotazioneRepository,
                        authService,
                        proiezioneService
                );

        System.out.println(
                "Proiezioni caricate: "
                + proiezioneService
                        .getProiezioni()
                        .size()
        );

        System.out.println(
                "Prenotazioni caricate: "
                + prenotazioneService
                        .getPrenotazioni()
                        .size()
        );
    }

    /**
     * Mostra il menu iniziale dell'applicazione.
     */
    private static void menuPrincipale() {
        boolean applicazioneAttiva = true;

        while (applicazioneAttiva) {
            System.out.println("\n===== MENU PRINCIPALE =====");
            System.out.println("1. Login");
            System.out.println("2. Registrazione cliente");
            System.out.println("3. Continua come guest");
            System.out.println("0. Esci");

            int scelta = InputUtils.leggiIntero(
                    scanner,
                    "Scelta: ",
                    0,
                    3
            );

            switch (scelta) {
                case 1:
                    eseguiLogin();
                    break;

                case 2:
                    registraCliente();
                    break;

                case 3:
                    menuGuest();
                    break;

                case 0:
                    applicazioneAttiva = false;
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Richiede le credenziali ed effettua il login.
     */
    private static void eseguiLogin() {
        System.out.println("\n===== LOGIN =====");

        String username =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Username: "
                );

        String password =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Password: "
                );

        Utente utente =
                authService.login(username, password);

        if (utente == null) {
            System.out.println(
                    "Username o password non corretti."
            );

            return;
        }

        System.out.println(
                "\nAccesso effettuato correttamente."
        );

        System.out.println(
                "Benvenuto, "
                + utente.getNomeCompleto()
                + "!"
        );

        apriMenuUtente(utente);
    }

    /**
     * Apre il menu corrispondente al ruolo
     * dell'utente autenticato.
     *
     * @param utente utente autenticato
     */
    private static void apriMenuUtente(Utente utente) {
        if (utente instanceof Cliente) {
            menuCliente((Cliente) utente);

        } else if (utente instanceof Proiezionista) {
            menuProiezionista(
                    (Proiezionista) utente
            );

        } else if (utente instanceof Bigliettaio) {
            menuBigliettaio(
                    (Bigliettaio) utente
            );

        } else {
            System.out.println(
                    "Ruolo dell'utente non riconosciuto."
            );
        }
    }

    /**
     * Registra un nuovo cliente.
     */
    private static void registraCliente() {
        System.out.println(
                "\n===== REGISTRAZIONE CLIENTE ====="
        );

        String nome =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Nome: "
                );

        String cognome =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Cognome: "
                );

        String username =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Username: "
                );

        if (authService.usernameEsistente(username)) {
            System.out.println(
                    "Lo username scelto è già utilizzato."
            );

            return;
        }

        String password =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Password, almeno 6 caratteri: "
                );

        if (password.length() < 6) {
            System.out.println(
                    "La password deve contenere almeno "
                    + "6 caratteri."
            );

            return;
        }

        LocalDate dataNascita =
                InputUtils.leggiData(
                        scanner,
                        "Data di nascita (gg/MM/aaaa): ",
                        false
                );
        
        String domicilio =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Domicilio: "
                );

        Cliente cliente =
                authService.registraCliente(
                        nome,
                        cognome,
                        username,
                        password,
                        dataNascita,
                        domicilio
                );

        if (cliente == null) {
            System.out.println(
                    "Registrazione non riuscita."
            );

            return;
        }

        System.out.println(
                "\nRegistrazione completata correttamente."
        );

        System.out.println(
                "Ora puoi effettuare il login."
        );
    }

    /**
     * Mostra il menu disponibile senza autenticazione.
     */
    private static void menuGuest() {
        boolean menuAttivo = true;

        while (menuAttivo) {
            System.out.println("\n===== MENU GUEST =====");
            System.out.println(
                    "1. Cercare proiezioni per titolo"
            );
            System.out.println(
                    "2. Visualizzare tutte le proiezioni"
            );
            System.out.println("0. Torna indietro");

            int scelta = InputUtils.leggiIntero(
                    scanner,
                    "Scelta: ",
                    0,
                    2
            );

            switch (scelta) {
                case 1:
                    ricercaGuestPerTitolo();
                    break;

                case 2:
                    mostraProiezioni(
                            proiezioneService
                                    .getProiezioni()
                    );
                    break;

                case 0:
                    menuAttivo = false;
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Consente a un guest di cercare una proiezione
     * tramite titolo anche parziale.
     */
    private static void ricercaGuestPerTitolo() {
        String titolo =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Titolo o parte del titolo: "
                );

        List<Proiezione> risultati =
                proiezioneService
                        .cercaPerTitolo(titolo);

        mostraProiezioni(risultati);
    }

    /**
     * Mostra un elenco compatto di proiezioni.
     *
     * Per evitare di stampare migliaia di righe,
     * vengono mostrate al massimo le prime 30.
     *
     * @param proiezioni proiezioni da mostrare
     */
    private static void mostraProiezioni(
            List<Proiezione> proiezioni) {

        if (proiezioni == null
                || proiezioni.isEmpty()) {

            System.out.println(
                    "Nessuna proiezione trovata."
            );

            return;
        }

        int limite = Math.min(
                proiezioni.size(),
                30
        );

        System.out.println(
                "\nProiezioni trovate: "
                + proiezioni.size()
        );

        for (int i = 0; i < limite; i++) {
            Proiezione proiezione =
                    proiezioni.get(i);

            int postiDisponibili =
                    prenotazioneService
                            .getPostiDisponibili(
                                    proiezione
                            );

            System.out.println(
                    "\n------------------------------"
            );

            System.out.println(
                    "[" + (i + 1) + "] "
                    + proiezione.getCodice()
            );

            System.out.println(
                    proiezione.getFilm().getTitolo()
            );

            System.out.println(
                    "Data: "
                    + proiezione.getDataOra()
            );

            System.out.printf(
                    "Prezzo: %.2f €%n",
                    proiezione.getPrezzoBiglietto()
            );

            System.out.println(
                    "Posti disponibili: "
                    + postiDisponibili
                    + "/"
                    + Proiezione.CAPIENZA_SALA
            );
        }

        if (proiezioni.size() > limite) {
            System.out.println(
                    "\nSono mostrati soltanto i primi "
                    + limite
                    + " risultati."
            );

            System.out.println(
                    "Utilizza una ricerca più precisa."
            );
        }
    }

    /**
     * Menu provvisorio del cliente.
     *
     * Le funzionalità verranno aggiunte
     * nel passaggio successivo.
     *
     * @param cliente cliente autenticato
     */
    private static void menuCliente(Cliente cliente) {
    	boolean menuAttivo = true;

    	while (menuAttivo) {

    		while (menuAttivo) {

    		    System.out.println("\n===== MENU CLIENTE =====");
    		    System.out.println("1. Visualizza tutte le proiezioni");
    		    System.out.println("2. Cerca proiezione per titolo");
    		    System.out.println("3. Prenota biglietti");
    		    System.out.println("4. Le mie prenotazioni");
    		    System.out.println("5. Modifica prenotazione");
    		    System.out.println("6. Elimina prenotazione");
    		    System.out.println("0. Logout");

    		    int scelta = InputUtils.leggiIntero(
    		            scanner,
    		            "Scelta: ",
    		            0,
    		            6
    		    );

    		    switch (scelta) {

    		        case 1:
    		            mostraProiezioni(
    		                    proiezioneService.getProiezioni()
    		            );
    		            break;

    		        case 2:
    		            ricercaGuestPerTitolo();
    		            break;

    		        case 3:
    		            prenotaBiglietti(cliente);
    		            break;

    		        case 4:
    		            mostraPrenotazioniCliente(cliente);
    		            break;

    		        case 5:
    		            modificaPrenotazioneCliente(cliente);
    		            break;

    		        case 6:
    		            eliminaPrenotazioneCliente(cliente);
    		            break;

    		        case 0:
    		            menuAttivo = false;
    		            break;

    		        default:
    		            break;
    		    }
    		}
    	}
    }
    
    /**
     * Permette al cliente di eliminare una propria prenotazione.
     *
     * Secondo le specifiche attuali, l'eliminazione è consentita
     * soltanto se la proiezione è già passata.
     *
     * @param cliente cliente autenticato
     */
    private static void eliminaPrenotazioneCliente(
            Cliente cliente) {

        List<Prenotazione> prenotazioniCliente =
                prenotazioneService.cercaPerCliente(cliente);

        if (prenotazioniCliente.isEmpty()) {
            System.out.println(
                    "\nNon hai prenotazioni da eliminare."
            );
            return;
        }

        System.out.println(
                "\n===== ELIMINA PRENOTAZIONE ====="
        );

        for (int i = 0;
                i < prenotazioniCliente.size();
                i++) {

            Prenotazione prenotazione =
                    prenotazioniCliente.get(i);

            System.out.println(
                    (i + 1)
                    + ". "
                    + prenotazione.getCodice()
                    + " - "
                    + prenotazione
                            .getProiezione()
                            .getFilm()
                            .getTitolo()
                    + " - "
                    + prenotazione
                            .getProiezione()
                            .getDataOra()
            );
        }

        int sceltaPrenotazione =
                InputUtils.leggiIntero(
                        scanner,
                        "Seleziona la prenotazione: ",
                        1,
                        prenotazioniCliente.size()
                );

        Prenotazione prenotazioneScelta =
                prenotazioniCliente.get(
                        sceltaPrenotazione - 1
                );

        boolean conferma =
                InputUtils.leggiConferma(
                        scanner,
                        "Confermi l'eliminazione?"
                );

        if (!conferma) {
            System.out.println(
                    "Eliminazione annullata."
            );
            return;
        }

        boolean eliminata =
                prenotazioneService.eliminaPrenotazione(
                        prenotazioneScelta
                );

        if (!eliminata) {
            System.out.println(
                    "Eliminazione non riuscita."
            );

            System.out.println(
                    "La prenotazione può essere eliminata "
                    + "solo se la proiezione è già passata."
            );

            return;
        }

        System.out.println(
                "Prenotazione eliminata correttamente."
        );
    }

    	/**
    	 * Permette al cliente di effettuare una nuova prenotazione.
    	 *
    	 * @param cliente cliente autenticato
    	 */
    	private static void prenotaBiglietti(Cliente cliente) {

    	    System.out.println("\n===== NUOVA PRENOTAZIONE =====");

    	    String codice = InputUtils.leggiStringaNonVuota(
    	            scanner,
    	            "Codice proiezione: ");

    	    Proiezione proiezione =
    	            proiezioneService.cercaPerCodice(codice);

    	    if (proiezione == null) {
    	        System.out.println("Proiezione non trovata.");
    	        return;
    	    }

    	    System.out.println();
    	    System.out.println(proiezione);

    	    int disponibili =
    	            prenotazioneService.getPostiDisponibili(proiezione);

    	    System.out.println(
    	            "Posti disponibili: "
    	            + disponibili
    	            + "/"
    	            + Proiezione.CAPIENZA_SALA);
    	    
    	    if (disponibili <= 0) {
    	        System.out.println(
    	                "La proiezione è esaurita."
    	        );
    	        return;
    	    }

    	    int numeroBiglietti =
    	            InputUtils.leggiIntero(
    	                    scanner,
    	                    "Numero di biglietti: ",
    	                    1,
    	                    disponibili);

    	    if (numeroBiglietti > disponibili) {
    	        System.out.println(
    	                "Non ci sono abbastanza posti.");
    	        return;
    	    }

    	    Prenotazione prenotazione =
    	            prenotazioneService.creaPrenotazione(
    	                    cliente,
    	                    proiezione,
    	                    numeroBiglietti);

    	    if (prenotazione == null) {
    	        System.out.println(
    	                "Prenotazione non riuscita.");
    	        return;
    	    }

    	    System.out.println();
    	    System.out.println(
    	            "Prenotazione effettuata con successo!");

    	    System.out.println(
    	            "Codice prenotazione: "
    	            + prenotazione.getCodice());

    	    System.out.printf(
    	            "Totale: %.2f €%n",
    	            prenotazione.getCostoTotale());
    	}
    	
    	/**
    	 * Mostra tutte le prenotazioni effettuate
    	 * dal cliente autenticato.
    	 *
    	 * @param cliente cliente autenticato
    	 */
    	private static void mostraPrenotazioniCliente(
    	        Cliente cliente) {

    	    List<Prenotazione> prenotazioniCliente =
    	            prenotazioneService.cercaPerCliente(cliente);

    	    if (prenotazioniCliente.isEmpty()) {
    	        System.out.println(
    	                "\nNon hai ancora effettuato prenotazioni."
    	        );

    	        return;
    	    }

    	    System.out.println(
    	            "\n===== LE MIE PRENOTAZIONI ====="
    	    );

    	    for (int i = 0;
    	            i < prenotazioniCliente.size();
    	            i++) {

    	        Prenotazione prenotazione =
    	                prenotazioniCliente.get(i);

    	        System.out.println(
    	                "\n------------------------------"
    	        );

    	        System.out.println(
    	                "[" + (i + 1) + "]"
    	        );

    	        System.out.println(prenotazione);
    	    }
    	}
    	
    	/**
    	 * Permette al cliente di spostare una prenotazione
    	 * verso un'altra proiezione futura.
    	 *
    	 * @param cliente cliente autenticato
    	 */
    	private static void modificaPrenotazioneCliente(
    	        Cliente cliente) {

    	    List<Prenotazione> prenotazioniCliente =
    	            prenotazioneService.cercaPerCliente(cliente);

    	    if (prenotazioniCliente.isEmpty()) {
    	        System.out.println(
    	                "\nNon hai prenotazioni da modificare."
    	        );
    	        return;
    	    }

    	    System.out.println(
    	            "\n===== MODIFICA PRENOTAZIONE ====="
    	    );

    	    for (int i = 0;
    	            i < prenotazioniCliente.size();
    	            i++) {

    	        Prenotazione prenotazione =
    	                prenotazioniCliente.get(i);

    	        System.out.println(
    	                (i + 1)
    	                + ". "
    	                + prenotazione.getCodice()
    	                + " - "
    	                + prenotazione
    	                        .getProiezione()
    	                        .getFilm()
    	                        .getTitolo()
    	                + " - "
    	                + prenotazione
    	                        .getProiezione()
    	                        .getDataOra()
    	        );
    	    }

    	    int sceltaPrenotazione =
    	            InputUtils.leggiIntero(
    	                    scanner,
    	                    "Seleziona la prenotazione: ",
    	                    1,
    	                    prenotazioniCliente.size()
    	            );

    	    Prenotazione prenotazioneScelta =
    	            prenotazioniCliente.get(
    	                    sceltaPrenotazione - 1
    	            );

    	    String codiceNuovaProiezione =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Codice della nuova proiezione: "
    	            );

    	    Proiezione nuovaProiezione =
    	            proiezioneService.cercaPerCodice(
    	                    codiceNuovaProiezione
    	            );

    	    if (nuovaProiezione == null) {
    	        System.out.println(
    	                "La nuova proiezione non esiste."
    	        );
    	        return;
    	    }

    	    boolean modificata =
    	            prenotazioneService.modificaPrenotazione(
    	                    prenotazioneScelta,
    	                    nuovaProiezione
    	            );

    	    if (!modificata) {
    	        System.out.println(
    	                "Modifica non riuscita."
    	        );

    	        System.out.println(
    	                "Controlla che entrambe le proiezioni "
    	                + "siano future e che vi siano posti disponibili."
    	        );

    	        return;
    	    }

    	    System.out.println(
    	            "Prenotazione modificata correttamente."
    	    );
    	}
    	
    	/**
    	 * Mostra il menu riservato ai proiezionisti.
    	 *
    	 * @param proiezionista proiezionista autenticato
    	 */
    	private static void menuProiezionista(
    	        Proiezionista proiezionista) {

    	    boolean menuAttivo = true;

    	    while (menuAttivo) {
    	        System.out.println(
    	                "\n===== MENU PROIEZIONISTA ====="
    	        );

    	        System.out.println(
    	                "1. Visualizza tutte le proiezioni"
    	        );
    	        
    	        System.out.println(
    	                "2. Cerca proiezioni"
    	        );
    	        
    	        System.out.println(
    	                "3. Aggiungi una proiezione"
    	        );

    	        System.out.println(
    	                "4. Modifica una proiezione"
    	        );

    	        System.out.println(
    	                "5. Elimina una proiezione"
    	        );
    	        
    	        System.out.println(
    	                "6. Ricerca avanzata"
    	        );

    	        System.out.println("0. Logout");

    	        int scelta = InputUtils.leggiIntero(
    	                scanner,
    	                "Scelta: ",
    	                0,
    	                6
    	        );

    	        switch (scelta) {
    	            case 1:
    	                mostraProiezioni(
    	                        proiezioneService.getProiezioni()
    	                );
    	                break;
    	                
    	            case 2:
    	                cercaProiezioniProiezionista();
    	                break;
    	                
    	            case 3:
    	                aggiungiProiezione();
    	                break;

    	            case 4:
    	                modificaProiezione();
    	                break;

    	            case 5:
    	                eliminaProiezione();
    	                break;
    	                
    	            case 6:
    	                ricercaAvanzataProiezioni();
    	                break;

    	            case 0:
    	                menuAttivo = false;
    	                break;

    	            default:
    	                break;
    	        }
    	    }
    	}
    	
    	/**
    	 * Permette al proiezionista di aggiungere
    	 * una nuova proiezione al palinsesto.
    	 */
    	private static void aggiungiProiezione() {
    	    System.out.println(
    	            "\n===== AGGIUNGI PROIEZIONE ====="
    	    );

    	    String titolo =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Titolo del film: "
    	            );

    	    String genere =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Genere: "
    	            );

    	    String regista =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Regista: "
    	            );

    	    int anno = InputUtils.leggiIntero(
    	            scanner,
    	            "Anno di uscita: ",
    	            1888,
    	            2100
    	    );

    	    int durata = InputUtils.leggiIntero(
    	            scanner,
    	            "Durata in minuti: ",
    	            1,
    	            600
    	    );

    	    int etaMinima = InputUtils.leggiIntero(
    	            scanner,
    	            "Età minima: ",
    	            0,
    	            21
    	    );

    	    LocalDateTime dataOra =
    	            InputUtils.leggiDataOra(
    	                    scanner,
    	                    "Data e ora (gg/MM/aaaa HH:mm): "
    	            );

    	    double prezzo =
    	            InputUtils.leggiDoublePositivo(
    	                    scanner,
    	                    "Prezzo del biglietto: "
    	            );

    	    Film film = new Film(
    	            titolo,
    	            genere,
    	            regista,
    	            anno,
    	            durata,
    	            etaMinima
    	    );

    	    String codice =
    	            proiezioneService.generaNuovoCodice();

    	    Proiezione proiezione =
    	            new Proiezione(
    	                    codice,
    	                    film,
    	                    dataOra,
    	                    prezzo
    	            );

    	    boolean aggiunta =
    	            proiezioneService
    	                    .aggiungiProiezione(proiezione);

    	    if (!aggiunta) {
    	        System.out.println(
    	                "Aggiunta non riuscita."
    	        );

    	        System.out.println(
    	                "Controlla che la data sia futura "
    	                + "e che non esistano sovrapposizioni."
    	        );

    	        return;
    	    }

    	    System.out.println(
    	            "Proiezione aggiunta correttamente."
    	    );

    	    System.out.println(
    	            "Codice assegnato: " + codice
    	    );
    	}
    	
    	/**
    	 * Permette al proiezionista di cercare proiezioni
    	 * utilizzando diversi criteri.
    	 */
    	private static void cercaProiezioniProiezionista() {
    	    System.out.println(
    	            "\n===== CERCA PROIEZIONI ====="
    	    );

    	    System.out.println("1. Cerca per titolo");
    	    System.out.println("2. Cerca per genere");
    	    System.out.println("3. Cerca per intervallo di date");
    	    System.out.println("4. Cerca per prezzo");
    	    System.out.println("0. Torna indietro");

    	    int scelta = InputUtils.leggiIntero(
    	            scanner,
    	            "Scelta: ",
    	            0,
    	            4
    	    );

    	    List<Proiezione> risultati;

    	    switch (scelta) {
    	        case 1:
    	            String titolo =
    	                    InputUtils.leggiStringaNonVuota(
    	                            scanner,
    	                            "Titolo o parte del titolo: "
    	                    );

    	            risultati =
    	                    proiezioneService.cercaPerTitolo(
    	                            titolo
    	                    );

    	            mostraProiezioni(risultati);
    	            break;

    	        case 2:
    	            String genere =
    	                    InputUtils.leggiStringaNonVuota(
    	                            scanner,
    	                            "Genere: "
    	                    );

    	            risultati =
    	                    proiezioneService.cercaPerGenere(
    	                            genere
    	                    );

    	            mostraProiezioni(risultati);
    	            break;

    	        case 3:
    	            LocalDate dataInizio =
    	                    InputUtils.leggiData(
    	                            scanner,
    	                            "Data iniziale (gg/MM/aaaa): ",
    	                            false
    	                    );

    	            LocalDate dataFine =
    	                    InputUtils.leggiData(
    	                            scanner,
    	                            "Data finale (gg/MM/aaaa): ",
    	                            false
    	                    );

    	            if (dataFine.isBefore(dataInizio)) {
    	                System.out.println(
    	                        "La data finale non può precedere "
    	                        + "quella iniziale."
    	                );

    	                return;
    	            }

    	            risultati =
    	                    proiezioneService
    	                            .cercaPerIntervalloDate(
    	                                    dataInizio,
    	                                    dataFine
    	                            );

    	            mostraProiezioni(risultati);
    	            break;

    	        case 4:
    	            double prezzoMinimo =
    	                    InputUtils.leggiDoublePositivo(
    	                            scanner,
    	                            "Prezzo minimo: "
    	                    );

    	            double prezzoMassimo =
    	                    InputUtils.leggiDoublePositivo(
    	                            scanner,
    	                            "Prezzo massimo: "
    	                    );

    	            if (prezzoMassimo < prezzoMinimo) {
    	                System.out.println(
    	                        "Il prezzo massimo non può essere "
    	                        + "minore di quello minimo."
    	                );

    	                return;
    	            }

    	            risultati =
    	                    proiezioneService.cercaPerPrezzo(
    	                            prezzoMinimo,
    	                            prezzoMassimo
    	                    );

    	            mostraProiezioni(risultati);
    	            break;

    	        case 0:
    	            break;

    	        default:
    	            break;
    	    }
    	}
    	
    	/**
    	 * Permette al proiezionista di cercare proiezioni
    	 * combinando più criteri contemporaneamente.
    	 *
    	 * I campi lasciati vuoti vengono ignorati.
    	 */
    	private static void ricercaAvanzataProiezioni() {

    	    System.out.println(
    	            "\n===== RICERCA AVANZATA ====="
    	    );

    	    System.out.print(
    	            "Titolo o parte del titolo "
    	            + "(Invio per ignorare): "
    	    );
    	    String titolo = scanner.nextLine().trim();

    	    if (titolo.isEmpty()) {
    	        titolo = null;
    	    }

    	    System.out.print(
    	            "Genere "
    	            + "(Invio per ignorare): "
    	    );
    	    String genere = scanner.nextLine().trim();

    	    if (genere.isEmpty()) {
    	        genere = null;
    	    }

    	    LocalDate dataInizio =
    	            InputUtils.leggiData(
    	                    scanner,
    	                    "Data iniziale "
    	                    + "(gg/MM/aaaa, Invio per ignorare): ",
    	                    true
    	            );

    	    LocalDate dataFine =
    	            InputUtils.leggiData(
    	                    scanner,
    	                    "Data finale "
    	                    + "(gg/MM/aaaa, Invio per ignorare): ",
    	                    true
    	            );

    	    if (dataInizio != null
    	            && dataFine != null
    	            && dataFine.isBefore(dataInizio)) {

    	        System.out.println(
    	                "La data finale non può precedere "
    	                + "quella iniziale."
    	        );
    	        return;
    	    }

    	    Double prezzoMinimo =
    	            InputUtils.leggiDoubleFacoltativo(
    	                    scanner,
    	                    "Prezzo minimo "
    	                    + "(Invio per ignorare): "
    	            );

    	    Double prezzoMassimo =
    	            InputUtils.leggiDoubleFacoltativo(
    	                    scanner,
    	                    "Prezzo massimo "
    	                    + "(Invio per ignorare): "
    	            );

    	    if (prezzoMinimo != null
    	            && prezzoMassimo != null
    	            && prezzoMassimo < prezzoMinimo) {

    	        System.out.println(
    	                "Il prezzo massimo non può essere "
    	                + "minore di quello minimo."
    	        );
    	        return;
    	    }

    	    List<Proiezione> risultati =
    	            proiezioneService.cercaProiezioni(
    	                    titolo,
    	                    genere,
    	                    dataInizio,
    	                    dataFine,
    	                    prezzoMinimo,
    	                    prezzoMassimo
    	            );

    	    mostraProiezioni(risultati);
    	}
    		
    	/**
    	 * Permette al proiezionista di modificare
    	 * data, ora e prezzo di una proiezione.
    	 */
    	private static void modificaProiezione() {
    	    System.out.println(
    	            "\n===== MODIFICA PROIEZIONE ====="
    	    );

    	    String codice =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Codice della proiezione: "
    	            );

    	    Proiezione proiezione =
    	            proiezioneService.cercaPerCodice(codice);

    	    if (proiezione == null) {
    	        System.out.println(
    	                "Proiezione non trovata."
    	        );
    	        return;
    	    }

    	    System.out.println(
    	            "\nProiezione selezionata:"
    	    );
    	    System.out.println(proiezione);

    	    LocalDateTime nuovaDataOra =
    	            InputUtils.leggiDataOra(
    	                    scanner,
    	                    "Nuova data e ora "
    	                    + "(gg/MM/aaaa HH:mm): "
    	            );

    	    double nuovoPrezzo =
    	            InputUtils.leggiDoublePositivo(
    	                    scanner,
    	                    "Nuovo prezzo del biglietto: "
    	            );

    	    boolean modificata =
    	            proiezioneService.modificaProiezione(
    	                    proiezione,
    	                    nuovaDataOra,
    	                    nuovoPrezzo,
    	                    prenotazioneService
    	            );

    	    if (!modificata) {
    	        System.out.println(
    	                "Modifica non riuscita."
    	        );

    	        System.out.println(
    	                "La proiezione potrebbe avere prenotazioni, "
    	                + "essere nel passato oppure sovrapporsi "
    	                + "a un'altra proiezione."
    	        );

    	        return;
    	    }

    	    System.out.println(
    	            "Proiezione modificata correttamente."
    	    );
    	}
    	
    	/**
    	 * Permette al proiezionista di eliminare
    	 * una proiezione dal palinsesto.
    	 */
    	private static void eliminaProiezione() {
    	    System.out.println(
    	            "\n===== ELIMINA PROIEZIONE ====="
    	    );

    	    String codice =
    	            InputUtils.leggiStringaNonVuota(
    	                    scanner,
    	                    "Codice della proiezione: "
    	            );

    	    Proiezione proiezione =
    	            proiezioneService.cercaPerCodice(codice);

    	    if (proiezione == null) {
    	        System.out.println(
    	                "Proiezione non trovata."
    	        );
    	        return;
    	    }

    	    System.out.println(
    	            "\nProiezione selezionata:"
    	    );
    	    System.out.println(proiezione);

    	    boolean conferma =
    	            InputUtils.leggiConferma(
    	                    scanner,
    	                    "Confermi l'eliminazione?"
    	            );

    	    if (!conferma) {
    	        System.out.println(
    	                "Eliminazione annullata."
    	        );
    	        return;
    	    }

    	    boolean eliminata =
    	            proiezioneService.eliminaProiezione(
    	                    proiezione,
    	                    prenotazioneService
    	            );

    	    if (!eliminata) {
    	        System.out.println(
    	                "Eliminazione non riuscita."
    	        );

    	        System.out.println(
    	                "La proiezione potrebbe avere "
    	                + "prenotazioni associate."
    	        );

    	        return;
    	    }

    	    System.out.println(
    	            "Proiezione eliminata correttamente."
    	    );
    	}
    	
    /**
     * Mostra il menu riservato ai bigliettai.
     *
     * @param bigliettaio bigliettaio autenticato
     */
    private static void menuBigliettaio(
            Bigliettaio bigliettaio) {

        boolean menuAttivo = true;

        while (menuAttivo) {
            System.out.println(
                    "\n===== MENU BIGLIETTAIO ====="
            );

            System.out.println(
                    "1. Visualizza prenotazioni odierne"
            );
            
            System.out.println(
                    "2. Cerca prenotazione per codice"
            );

            System.out.println(
                    "3. Cerca per nome o cognome cliente"
            );

            System.out.println(
                    "4. Cerca per titolo del film"
            );

            System.out.println(
                    "5. Cerca per intervallo di date"
            );

            System.out.println("0. Logout");

            int scelta = InputUtils.leggiIntero(
                    scanner,
                    "Scelta: ",
                    0,
                    5
            );

            switch (scelta) {
                case 1:
                    mostraPrenotazioniOdierne();
                    break;

                case 2:
                    cercaPrenotazionePerCodice();
                    break;

                case 3:
                    cercaPrenotazioniPerCliente();
                    break;

                case 4:
                    cercaPrenotazioniPerTitolo();
                    break;

                case 5:
                    cercaPrenotazioniPerDate();
                    break;

                case 0:
                    menuAttivo = false;
                    break;

                default:
                    break;
            }
        }
    }
    
    /**
     * Visualizza un elenco completo di prenotazioni.
     *
     * @param prenotazioni prenotazioni da visualizzare
     */
    private static void mostraPrenotazioni(
            List<Prenotazione> prenotazioni) {

        if (prenotazioni == null
                || prenotazioni.isEmpty()) {

            System.out.println(
                    "\nNessuna prenotazione trovata."
            );

            return;
        }

        System.out.println(
                "\nPrenotazioni trovate: "
                + prenotazioni.size()
        );

        for (int i = 0; i < prenotazioni.size(); i++) {
            System.out.println(
                    "\n------------------------------"
            );

            System.out.println(
                    "[" + (i + 1) + "]"
            );

            System.out.println(
                    prenotazioni.get(i)
            );
        }
    }
    
    /**
     * Mostra le prenotazioni relative alle proiezioni
     * della data odierna.
     */
    private static void mostraPrenotazioniOdierne() {
        List<Prenotazione> prenotazioni =
                prenotazioneService
                        .cercaPrenotazioniOdierne();

        System.out.println(
                "\n===== PRENOTAZIONI ODIERNE ====="
        );

        mostraPrenotazioni(prenotazioni);
    }
    
    /**
     * Cerca una prenotazione tramite il suo codice univoco.
     */
    private static void cercaPrenotazionePerCodice() {
        String codice =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Codice prenotazione: "
                );

        Prenotazione prenotazione =
                prenotazioneService
                        .cercaPerCodice(codice);

        if (prenotazione == null) {
            System.out.println(
                    "Prenotazione non trovata."
            );

            return;
        }

        System.out.println(
                "\n===== DETTAGLI PRENOTAZIONE ====="
        );

        System.out.println(prenotazione);
    }
    
    /**
     * Cerca le prenotazioni tramite nome o cognome
     * del cliente.
     */
    private static void cercaPrenotazioniPerCliente() {
        String testo =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Nome o cognome del cliente: "
                );

        List<Prenotazione> risultati =
                prenotazioneService
                        .cercaPerNomeCliente(testo);

        mostraPrenotazioni(risultati);
    }
    
    /**
     * Cerca le prenotazioni tramite il titolo,
     * anche parziale, del film.
     */
    private static void cercaPrenotazioniPerTitolo() {
        String titolo =
                InputUtils.leggiStringaNonVuota(
                        scanner,
                        "Titolo o parte del titolo: "
                );

        List<Prenotazione> risultati =
                prenotazioneService
                        .cercaPerTitoloFilm(titolo);

        mostraPrenotazioni(risultati);
    }
    
    /**
     * Cerca le prenotazioni relative a proiezioni
     * comprese tra due date.
     */
    private static void cercaPrenotazioniPerDate() {
        System.out.println(
                "\n===== RICERCA PER DATE ====="
        );

        LocalDate dataInizio =
                InputUtils.leggiData(
                        scanner,
                        "Data iniziale (gg/MM/aaaa): ",
                        false
                );

        LocalDate dataFine =
                InputUtils.leggiData(
                        scanner,
                        "Data finale (gg/MM/aaaa): ",
                        false
                );

        if (dataFine.isBefore(dataInizio)) {
            System.out.println(
                    "La data finale non può precedere "
                    + "quella iniziale."
            );

            return;
        }

        List<Prenotazione> risultati =
                prenotazioneService
                        .cercaPerIntervalloDate(
                                dataInizio,
                                dataFine
                        );

        mostraPrenotazioni(risultati);
    }
}