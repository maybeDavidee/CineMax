/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cinemax.model.Cliente;
import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.persistence.PrenotazioneRepository;
import cinemax.utils.CodiceUtils;

/**
 * Gestisce tutte le operazioni relative alle prenotazioni.
 *
 * Permette di creare, cercare, modificare ed eliminare
 * le prenotazioni e di calcolare i posti disponibili
 * per ogni proiezione.
 */
public class PrenotazioneService {

    private final PrenotazioneRepository repository;
    private final List<Prenotazione> prenotazioni;

    /**
     * Costruisce il servizio e carica le prenotazioni dal file.
     *
     * @param repository repository delle prenotazioni
     * @param authService servizio degli utenti
     * @param proiezioneService servizio delle proiezioni
     */
    public PrenotazioneService(
            PrenotazioneRepository repository,
            AuthService authService,
            ProiezioneService proiezioneService) {

        if (repository == null
                || authService == null
                || proiezioneService == null) {

            throw new IllegalArgumentException(
                    "I servizi e il repository non possono essere null."
            );
        }

        this.repository = repository;
       

        this.prenotazioni = new ArrayList<>(
                repository.caricaTutte(
                        authService.getUtenti(),
                        proiezioneService.getProiezioni()
                )
        );

        ordinaPerDataProiezione();
    }

    /**
     * Restituisce una copia di tutte le prenotazioni.
     *
     * @return lista delle prenotazioni
     */
    public List<Prenotazione> getPrenotazioni() {
        return new ArrayList<>(prenotazioni);
    }

    /**
     * Calcola il numero di posti occupati
     * per una determinata proiezione.
     *
     * @param proiezione proiezione da controllare
     * @return numero di posti occupati
     */
    public int getPostiOccupati(Proiezione proiezione) {
        if (proiezione == null) {
            return 0;
        }

        int postiOccupati = 0;

        for (Prenotazione prenotazione : prenotazioni) {
            if (stessaProiezione(
                    prenotazione.getProiezione(),
                    proiezione)) {

                postiOccupati +=
                        prenotazione.getNumeroBiglietti();
            }
        }

        return postiOccupati;
    }

    /**
     * Calcola il numero di posti ancora disponibili.
     *
     * @param proiezione proiezione da controllare
     * @return posti disponibili
     */
    public int getPostiDisponibili(Proiezione proiezione) {
        int postiOccupati = getPostiOccupati(proiezione);

        return Proiezione.CAPIENZA_SALA - postiOccupati;
    }

    /**
     * Verifica se una proiezione possiede almeno
     * una prenotazione.
     *
     * @param proiezione proiezione da controllare
     * @return true se esistono prenotazioni
     */
    public boolean haPrenotazioni(Proiezione proiezione) {
        if (proiezione == null) {
            return false;
        }

        for (Prenotazione prenotazione : prenotazioni) {
            if (stessaProiezione(
                    prenotazione.getProiezione(),
                    proiezione)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Crea una nuova prenotazione.
     *
     * La prenotazione è possibile solamente se:
     * - il cliente e la proiezione sono validi;
     * - la proiezione non è già passata;
     * - il numero di biglietti è positivo;
     * - sono disponibili abbastanza posti.
     *
     * @param cliente cliente che prenota
     * @param proiezione proiezione scelta
     * @param numeroBiglietti numero di biglietti richiesti
     * @return prenotazione creata oppure null
     */
    public Prenotazione creaPrenotazione(
            Cliente cliente,
            Proiezione proiezione,
            int numeroBiglietti) {

        if (cliente == null
                || proiezione == null
                || numeroBiglietti <= 0) {

            return null;
        }

        if (!proiezione.getDataOra()
                .isAfter(LocalDateTime.now())) {

            return null;
        }
        
        if (cliente.getDataNascita() == null) {
            return null;
        }

        LocalDate dataProiezione =
                proiezione.getDataOra().toLocalDate();

        int etaCliente =
                Period.between(
                        cliente.getDataNascita(),
                        dataProiezione
                ).getYears();

        if (etaCliente < proiezione.getFilm().getEtaMinima()) {
            return null;
        }
        
        int postiDisponibili =
                getPostiDisponibili(proiezione);

        if (numeroBiglietti > postiDisponibili) {
            return null;
        }

        String codice = generaCodiceUnivoco();

        Prenotazione prenotazione =
                new Prenotazione(
                        codice,
                        cliente,
                        proiezione,
                        numeroBiglietti,
                        LocalDateTime.now()
                );

        boolean salvata =
                repository.aggiungi(prenotazione);

        if (!salvata) {
            return null;
        }

        prenotazioni.add(prenotazione);
        ordinaPerDataProiezione();

        return prenotazione;
    }

    /**
     * Cerca una prenotazione tramite codice.
     *
     * @param codice codice da cercare
     * @return prenotazione trovata oppure null
     */
    public Prenotazione cercaPerCodice(String codice) {
        if (codice == null || codice.isBlank()) {
            return null;
        }

        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getCodice()
                    .equalsIgnoreCase(codice.trim())) {

                return prenotazione;
            }
        }

        return null;
    }

    /**
     * Restituisce tutte le prenotazioni di un cliente.
     *
     * @param cliente cliente da cercare
     * @return prenotazioni del cliente
     */
    public List<Prenotazione> cercaPerCliente(
            Cliente cliente) {

        List<Prenotazione> risultati =
                new ArrayList<>();

        if (cliente == null) {
            return risultati;
        }

        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getCliente()
                    .getUsername()
                    .equalsIgnoreCase(
                            cliente.getUsername()
                    )) {

                risultati.add(prenotazione);
            }
        }

        risultati.sort(
                Comparator.comparing(
                        prenotazione ->
                                prenotazione
                                        .getProiezione()
                                        .getDataOra()
                )
        );

        return risultati;
    }

    /**
     * Cerca prenotazioni tramite nome e cognome
     * del cliente.
     *
     * La ricerca accetta anche valori parziali.
     *
     * @param testo nome, cognome o parte di essi
     * @return prenotazioni corrispondenti
     */
    public List<Prenotazione> cercaPerNomeCliente(
            String testo) {

        List<Prenotazione> risultati =
                new ArrayList<>();

        if (testo == null || testo.isBlank()) {
            return risultati;
        }

        String ricerca = testo.trim().toLowerCase();

        for (Prenotazione prenotazione : prenotazioni) {
            Cliente cliente =
                    prenotazione.getCliente();

            String nomeCompleto =
                    cliente.getNomeCompleto().toLowerCase();

            if (nomeCompleto.contains(ricerca)) {
                risultati.add(prenotazione);
            }
        }

        return risultati;
    }

    /**
     * Cerca prenotazioni tramite titolo del film,
     * anche parziale.
     *
     * @param titolo titolo da cercare
     * @return prenotazioni corrispondenti
     */
    public List<Prenotazione> cercaPerTitoloFilm(
            String titolo) {

        List<Prenotazione> risultati =
                new ArrayList<>();

        if (titolo == null || titolo.isBlank()) {
            return risultati;
        }

        String ricerca = titolo.trim().toLowerCase();

        for (Prenotazione prenotazione : prenotazioni) {
            String titoloFilm =
                    prenotazione
                            .getProiezione()
                            .getFilm()
                            .getTitolo()
                            .toLowerCase();

            if (titoloFilm.contains(ricerca)) {
                risultati.add(prenotazione);
            }
        }

        return risultati;
    }

    /**
     * Cerca le prenotazioni relative a proiezioni
     * comprese in un intervallo di date.
     *
     * @param dataInizio data iniziale inclusa
     * @param dataFine data finale inclusa
     * @return prenotazioni trovate
     */
    public List<Prenotazione> cercaPerIntervalloDate(
            LocalDate dataInizio,
            LocalDate dataFine) {

        List<Prenotazione> risultati =
                new ArrayList<>();

        for (Prenotazione prenotazione : prenotazioni) {
            LocalDate dataProiezione =
                    prenotazione
                            .getProiezione()
                            .getDataOra()
                            .toLocalDate();

            if (dataInizio != null
                    && dataProiezione.isBefore(dataInizio)) {

                continue;
            }

            if (dataFine != null
                    && dataProiezione.isAfter(dataFine)) {

                continue;
            }

            risultati.add(prenotazione);
        }

        return risultati;
    }

    /**
     * Restituisce le prenotazioni relative
     * alle proiezioni della data odierna.
     *
     * @return prenotazioni di oggi
     */
    public List<Prenotazione> cercaPrenotazioniOdierne() {
        List<Prenotazione> risultati = new ArrayList<>();
        LocalDate oggi = LocalDate.now();

        for (Prenotazione prenotazione : prenotazioni) {

            LocalDate dataProiezione =
                    prenotazione
                            .getProiezione()
                            .getDataOra()
                            .toLocalDate();

            if (dataProiezione.equals(oggi)) {
                risultati.add(prenotazione);
            }
        }

        return risultati;
    }
    /**
     * Modifica la proiezione associata a una prenotazione.
     *
     * La vecchia e la nuova proiezione devono essere future.
     * Nella nuova proiezione devono inoltre esserci
     * abbastanza posti disponibili.
     *
     * @param prenotazione prenotazione da modificare
     * @param nuovaProiezione nuova proiezione
     * @return true se la modifica è riuscita
     */
    public boolean modificaPrenotazione(
            Prenotazione prenotazione,
            Proiezione nuovaProiezione) {

        if (prenotazione == null
                || nuovaProiezione == null) {

            return false;
        }
        
        Proiezione vecchiaProiezione =
                prenotazione.getProiezione();

        LocalDateTime adesso = LocalDateTime.now();

        if (!vecchiaProiezione.getDataOra().isAfter(adesso)
                || !nuovaProiezione.getDataOra().isAfter(adesso)) {

            return false;
        }
		
        if (stessaProiezione(
                vecchiaProiezione,
                nuovaProiezione)) {

            return false;
        }

        int postiDisponibili =
                getPostiDisponibili(nuovaProiezione);

        if (prenotazione.getNumeroBiglietti()
                > postiDisponibili) {

            return false;
        }

        prenotazione.setProiezione(nuovaProiezione);

        boolean salvata =
                repository.salvaTutte(prenotazioni);

        if (!salvata) {
            prenotazione.setProiezione(
                    vecchiaProiezione
            );

            return false;
        }

        ordinaPerDataProiezione();

        return true;
    }

    /**
     * Elimina una prenotazione.
     *
     * La specifica del docente indica che la cancellazione
     * è permessa quando la data della proiezione è precedente
     * alla data odierna.
     *
     * @param prenotazione prenotazione da eliminare
     * @return true se l'eliminazione è riuscita
     */
    public boolean eliminaPrenotazione(
            Prenotazione prenotazione) {

        if (prenotazione == null) {
            return false;
        }

        LocalDate dataProiezione =
                prenotazione
                        .getProiezione()
                        .getDataOra()
                        .toLocalDate();

        if (!dataProiezione.isBefore(LocalDate.now())) {
            return false;
        }

        int posizione =
                prenotazioni.indexOf(prenotazione);

        if (posizione < 0) {
            return false;
        }

        prenotazioni.remove(posizione);

        boolean salvata =
                repository.salvaTutte(prenotazioni);

        if (!salvata) {
            prenotazioni.add(
                    posizione,
                    prenotazione
            );

            ordinaPerDataProiezione();

            return false;
        }

        return true;
    }

    /**
     * Verifica se due oggetti rappresentano
     * la stessa proiezione.
     *
     * @param prima prima proiezione
     * @param seconda seconda proiezione
     * @return true se hanno lo stesso codice
     */
    private boolean stessaProiezione(
            Proiezione prima,
            Proiezione seconda) {

        if (prima == null || seconda == null) {
            return false;
        }

        return prima.getCodice()
                .equalsIgnoreCase(seconda.getCodice());
    }

    /**
     * Genera un codice di prenotazione non ancora utilizzato.
     *
     * @return codice univoco
     */
    private String generaCodiceUnivoco() {
        String codice;

        do {
            codice =
                    CodiceUtils.generaCodicePrenotazione();

        } while (cercaPerCodice(codice) != null);

        return codice;
    }

    /**
     * Ordina le prenotazioni in base alla data
     * della relativa proiezione.
     */
    private void ordinaPerDataProiezione() {
        prenotazioni.sort(
                Comparator.comparing(
                        prenotazione ->
                                prenotazione
                                        .getProiezione()
                                        .getDataOra()
                )
        );
    }
}