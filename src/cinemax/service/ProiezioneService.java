/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
 */

package cinemax.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import cinemax.persistence.ProiezioneRepository;
import cinemax.model.Proiezione;
import cinemax.utils.CodiceUtils;

/**
 * Gestisce la consultazione e la ricerca delle proiezioni.
 */
public class ProiezioneService {

    private final List<Proiezione> proiezioni;
    private final ProiezioneRepository repository;

    /**
     * Costruisce il servizio caricando le proiezioni dal repository.
     *
     * @param repository repository delle proiezioni
     */
    public ProiezioneService(ProiezioneRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Il repository non può essere null."
            );
        }

        this.repository = repository;
        this.proiezioni = new ArrayList<>(
                repository.caricaTutte()
        );

        ordinaPerData();
    }

    /**
     * Restituisce tutte le proiezioni.
     *
     * @return copia della lista delle proiezioni
     */
    public List<Proiezione> getProiezioni() {
        return new ArrayList<>(proiezioni);
    }

    /**
     * Cerca una proiezione tramite il suo codice.
     *
     * @param codice codice della proiezione
     * @return proiezione trovata oppure null
     */
    public Proiezione cercaPerCodice(String codice) {
        if (codice == null) {
            return null;
        }

        for (Proiezione proiezione : proiezioni) {
            if (proiezione.getCodice()
                    .equalsIgnoreCase(codice.trim())) {

                return proiezione;
            }
        }

        return null;
    }

    /**
     * Cerca le proiezioni utilizzando una combinazione
     * di criteri.
     *
     * Un criterio null o vuoto viene ignorato.
     *
     * @param titolo titolo anche parziale
     * @param genere genere del film
     * @param dataInizio data iniziale inclusa
     * @param dataFine data finale inclusa
     * @param prezzoMinimo prezzo minimo incluso
     * @param prezzoMassimo prezzo massimo incluso
     * @return lista delle proiezioni corrispondenti
     */
    public List<Proiezione> cercaProiezioni(
            String titolo,
            String genere,
            LocalDate dataInizio,
            LocalDate dataFine,
            Double prezzoMinimo,
            Double prezzoMassimo) {

        List<Proiezione> risultati = new ArrayList<>();

        for (Proiezione proiezione : proiezioni) {
            if (!corrispondeTitolo(proiezione, titolo)) {
                continue;
            }

            if (!corrispondeGenere(proiezione, genere)) {
                continue;
            }

            if (!corrispondeIntervalloDate(
                    proiezione,
                    dataInizio,
                    dataFine)) {

                continue;
            }

            if (!corrispondePrezzo(
                    proiezione,
                    prezzoMinimo,
                    prezzoMassimo)) {

                continue;
            }

            risultati.add(proiezione);
        }

        risultati.sort(
                Comparator.comparing(Proiezione::getDataOra)
        );

        return risultati;
    }

    /**
     * Cerca proiezioni per titolo anche parziale.
     *
     * @param titolo titolo da cercare
     * @return lista dei risultati
     */
    public List<Proiezione> cercaPerTitolo(String titolo) {
        return cercaProiezioni(
                titolo,
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Cerca proiezioni per genere.
     *
     * @param genere genere da cercare
     * @return lista dei risultati
     */
    public List<Proiezione> cercaPerGenere(String genere) {
        return cercaProiezioni(
                null,
                genere,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Cerca proiezioni comprese tra due date.
     *
     * @param dataInizio data iniziale inclusa
     * @param dataFine data finale inclusa
     * @return lista dei risultati
     */
    public List<Proiezione> cercaPerIntervalloDate(
            LocalDate dataInizio,
            LocalDate dataFine) {

        return cercaProiezioni(
                null,
                null,
                dataInizio,
                dataFine,
                null,
                null
        );
    }

    /**
     * Cerca proiezioni in una fascia di prezzo.
     *
     * @param prezzoMinimo prezzo minimo incluso
     * @param prezzoMassimo prezzo massimo incluso
     * @return lista dei risultati
     */
    public List<Proiezione> cercaPerPrezzo(
            Double prezzoMinimo,
            Double prezzoMassimo) {

        return cercaProiezioni(
                null,
                null,
                null,
                null,
                prezzoMinimo,
                prezzoMassimo
        );
    }

    /**
     * Verifica la corrispondenza del titolo.
     *
     * @param proiezione proiezione da controllare
     * @param titolo titolo cercato
     * @return true se corrisponde
     */
    private boolean corrispondeTitolo(
            Proiezione proiezione,
            String titolo) {

        if (titolo == null || titolo.isBlank()) {
            return true;
        }

        String titoloFilm = proiezione
                .getFilm()
                .getTitolo()
                .toLowerCase();

        return titoloFilm.contains(
                titolo.trim().toLowerCase()
        );
    }

    /**
     * Verifica la corrispondenza del genere.
     *
     * @param proiezione proiezione da controllare
     * @param genere genere cercato
     * @return true se corrisponde
     */
    private boolean corrispondeGenere(
            Proiezione proiezione,
            String genere) {

        if (genere == null || genere.isBlank()) {
            return true;
        }

        String genereFilm = proiezione
                .getFilm()
                .getGenere()
                .toLowerCase();

        return genereFilm.contains(
                genere.trim().toLowerCase()
        );
    }

    /**
     * Verifica che la proiezione sia compresa
     * nell'intervallo di date indicato.
     *
     * @param proiezione proiezione da controllare
     * @param dataInizio data iniziale
     * @param dataFine data finale
     * @return true se la data è valida
     */
    private boolean corrispondeIntervalloDate(
            Proiezione proiezione,
            LocalDate dataInizio,
            LocalDate dataFine) {

        LocalDate dataProiezione =
                proiezione.getDataOra().toLocalDate();

        if (dataInizio != null
                && dataProiezione.isBefore(dataInizio)) {

            return false;
        }

        if (dataFine != null
                && dataProiezione.isAfter(dataFine)) {

            return false;
        }

        return true;
    }

    /**
     * Verifica che il prezzo sia compreso
     * nell'intervallo richiesto.
     *
     * @param proiezione proiezione da controllare
     * @param prezzoMinimo prezzo minimo
     * @param prezzoMassimo prezzo massimo
     * @return true se il prezzo è valido
     */
    private boolean corrispondePrezzo(
            Proiezione proiezione,
            Double prezzoMinimo,
            Double prezzoMassimo) {

        double prezzo =
                proiezione.getPrezzoBiglietto();

        if (prezzoMinimo != null
                && prezzo < prezzoMinimo) {

            return false;
        }

        if (prezzoMassimo != null
                && prezzo > prezzoMassimo) {

            return false;
        }

        return true;
    }

    /**
     * Verifica se una nuova proiezione si sovrappone
     * con una già esistente.
     *
     * Si considera anche la durata del film.
     *
     * @param nuovaProiezione nuova proiezione
     * @param proiezioneDaIgnorare eventuale proiezione
     *        da ignorare durante una modifica
     * @return true se esiste una sovrapposizione
     */
    public boolean esisteSovrapposizione(
            Proiezione nuovaProiezione,
            Proiezione proiezioneDaIgnorare) {

        if (nuovaProiezione == null) {
            return true;
        }

        LocalDateTime nuovoInizio =
                nuovaProiezione.getDataOra();

        LocalDateTime nuovaFine =
                nuovoInizio.plusMinutes(
                        nuovaProiezione
                                .getFilm()
                                .getDurata()
                );

        for (Proiezione esistente : proiezioni) {
            if (esistente == proiezioneDaIgnorare) {
                continue;
            }

            LocalDateTime inizioEsistente =
                    esistente.getDataOra();

            LocalDateTime fineEsistente =
                    inizioEsistente.plusMinutes(
                            esistente
                                    .getFilm()
                                    .getDurata()
                    );

            boolean sovrapposta =
                    nuovoInizio.isBefore(fineEsistente)
                    && nuovaFine.isAfter(inizioEsistente);

            if (sovrapposta) {
                return true;
            }
        }

        return false;
    }

    /**
     * Aggiunge una nuova proiezione se non si sovrappone
     * con quelle esistenti.
     *
     * @param proiezione proiezione da aggiungere
     * @return true se è stata aggiunta e salvata
     */
    public boolean aggiungiProiezione(Proiezione proiezione) {
        if (proiezione == null) {
            return false;
            
        }
        if (!proiezione.getDataOra().isAfter(
                LocalDateTime.now())) {

            return false;
        }

        if (esisteSovrapposizione(proiezione, null)) {
            return false;
        }

        proiezioni.add(proiezione);
        ordinaPerData();

        boolean salvata = repository.salvaTutte(proiezioni);

        if (!salvata) {
            proiezioni.remove(proiezione);
            return false;
        }

        return true;
    }
    /**
     * Modifica data, ora e prezzo di una proiezione.
     *
     * La modifica è permessa solo se:
     * - i nuovi dati sono validi;
     * - la proiezione non possiede prenotazioni;
     * - la nuova fascia oraria non si sovrappone
     *   a un'altra proiezione;
     * - il salvataggio sul file riesce.
     *
     * @param proiezione proiezione da modificare
     * @param nuovaDataOra nuova data e ora
     * @param nuovoPrezzo nuovo prezzo del biglietto
     * @param prenotazioneService servizio delle prenotazioni
     * @return true se la modifica è riuscita
     */
    public boolean modificaProiezione(
            Proiezione proiezione,
            LocalDateTime nuovaDataOra,
            double nuovoPrezzo,
            PrenotazioneService prenotazioneService) {

        if (proiezione == null
                || nuovaDataOra == null
                || nuovoPrezzo < 0
                || prenotazioneService == null) {

            return false;
        }
        
        if (!nuovaDataOra.isAfter(LocalDateTime.now())) {
            return false;
        }

        /*
         * Una proiezione con prenotazioni non può
         * essere modificata.
         */
        if (prenotazioneService.haPrenotazioni(proiezione)) {
            return false;
        }

        LocalDateTime vecchiaDataOra =
                proiezione.getDataOra();

        double vecchioPrezzo =
                proiezione.getPrezzoBiglietto();

        proiezione.setDataOra(nuovaDataOra);
        proiezione.setPrezzoBiglietto(nuovoPrezzo);

        /*
         * Durante il controllo ignoriamo la stessa
         * proiezione che stiamo modificando.
         */
        if (esisteSovrapposizione(
                proiezione,
                proiezione)) {

            proiezione.setDataOra(vecchiaDataOra);
            proiezione.setPrezzoBiglietto(vecchioPrezzo);

            return false;
        }

        ordinaPerData();

        boolean salvata =
                repository.salvaTutte(proiezioni);

        /*
         * Se il salvataggio fallisce, ripristiniamo
         * i valori precedenti.
         */
        if (!salvata) {
            proiezione.setDataOra(vecchiaDataOra);
            proiezione.setPrezzoBiglietto(vecchioPrezzo);
            ordinaPerData();

            return false;
        }

        return true;
    }
    /**
     * Elimina una proiezione.
     *
     * L'eliminazione è permessa soltanto se non
     * esistono prenotazioni associate.
     *
     * @param proiezione proiezione da eliminare
     * @param prenotazioneService servizio delle prenotazioni
     * @return true se la proiezione è stata eliminata
     */
    public boolean eliminaProiezione(
            Proiezione proiezione,
            PrenotazioneService prenotazioneService) {

        if (proiezione == null
                || prenotazioneService == null) {

            return false;
        }

        /*
         * Una proiezione prenotata non può essere eliminata.
         */
        if (prenotazioneService.haPrenotazioni(proiezione)) {
            return false;
        }

        int posizione = proiezioni.indexOf(proiezione);

        if (posizione < 0) {
            return false;
        }

        proiezioni.remove(posizione);

        boolean salvata =
                repository.salvaTutte(proiezioni);

        /*
         * Se il file non viene aggiornato, rimettiamo
         * la proiezione nella lista.
         */
        if (!salvata) {
            proiezioni.add(posizione, proiezione);
            ordinaPerData();

            return false;
        }

        return true;
    }
    
    /**
     * Genera il prossimo codice disponibile per una proiezione.
     *
     * @return nuovo codice nel formato PRO000001
     */
    public String generaNuovoCodice() {
        int massimo = 0;

        for (Proiezione proiezione : proiezioni) {
            String codice = proiezione.getCodice();

            if (codice != null && codice.startsWith("PRO")) {
                try {
                    int numero = Integer.parseInt(
                            codice.substring(3)
                    );

                    if (numero > massimo) {
                        massimo = numero;
                    }

                } catch (NumberFormatException e) {
                    // Un codice non valido viene ignorato.
                }
            }
        }

        return CodiceUtils.generaCodiceProiezione(massimo + 1);
    }

    /**
     * Ordina le proiezioni in ordine cronologico.
     */
    private void ordinaPerData() {
        proiezioni.sort(
                Comparator.comparing(Proiezione::getDataOra)
        );
    }
}