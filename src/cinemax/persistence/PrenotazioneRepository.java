/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import cinemax.model.Cliente;
import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.model.Utente;

/**
 * Gestisce la lettura e la scrittura delle prenotazioni
 * nel file data/prenotazioni.txt.
 */
public class PrenotazioneRepository {

    private static final Path FILE_PRENOTAZIONI =
            Paths.get("data", "prenotazioni.txt");

    /**
     * Costruisce il repository e verifica che la cartella
     * data e il file delle prenotazioni esistano.
     */
    public PrenotazioneRepository() {
        creaCartellaData();
        creaFileSeNecessario();
    }

    /**
     * Carica tutte le prenotazioni presenti nel file.
     *
     * Per ricostruire ogni prenotazione sono necessarie
     * le liste degli utenti e delle proiezioni già caricate.
     *
     * @param utenti utenti presenti nel sistema
     * @param proiezioni proiezioni presenti nel sistema
     * @return lista delle prenotazioni caricate
     */
    public List<Prenotazione> caricaTutte(
            List<Utente> utenti,
            List<Proiezione> proiezioni) {

        List<Prenotazione> prenotazioni = new ArrayList<>();

        if (utenti == null || proiezioni == null) {
            return prenotazioni;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FILE_PRENOTAZIONI,
                StandardCharsets.UTF_8)) {

            String riga;
            int numeroRiga = 0;

            while ((riga = reader.readLine()) != null) {
                numeroRiga++;

                if (riga.isBlank()) {
                    continue;
                }

                try {
                    Prenotazione prenotazione =
                            creaPrenotazioneDaRiga(
                                    riga,
                                    utenti,
                                    proiezioni
                            );

                    prenotazioni.add(prenotazione);

                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Prenotazione non valida alla riga "
                            + numeroRiga + ": "
                            + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "Errore durante la lettura delle prenotazioni: "
                    + e.getMessage()
            );
        }

        return prenotazioni;
    }

    /**
     * Salva l'intera lista delle prenotazioni.
     *
     * Il contenuto precedente del file viene sostituito.
     *
     * @param prenotazioni lista da salvare
     * @return true se il salvataggio è riuscito
     */
    public boolean salvaTutte(
            List<Prenotazione> prenotazioni) {

        if (prenotazioni == null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PRENOTAZIONI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Prenotazione prenotazione : prenotazioni) {
                writer.write(convertiInRiga(prenotazione));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante il salvataggio delle prenotazioni: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Aggiunge una singola prenotazione in fondo al file.
     *
     * @param prenotazione prenotazione da aggiungere
     * @return true se il salvataggio è riuscito
     */
    public boolean aggiungi(Prenotazione prenotazione) {
        if (prenotazione == null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PRENOTAZIONI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRiga(prenotazione));
            writer.newLine();

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante l'aggiunta della prenotazione: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Crea una prenotazione leggendo una riga del file.
     *
     * @param riga riga da convertire
     * @param utenti utenti presenti nel sistema
     * @param proiezioni proiezioni presenti nel sistema
     * @return prenotazione creata
     */
    private Prenotazione creaPrenotazioneDaRiga(
            String riga,
            List<Utente> utenti,
            List<Proiezione> proiezioni) {

        String[] campi = riga.split(";", -1);

        if (campi.length != 5) {
            throw new IllegalArgumentException(
                    "sono richiesti esattamente 5 campi"
            );
        }

        String codice = campi[0].trim();
        String username = campi[1].trim();
        String codiceProiezione = campi[2].trim();
        String numeroTesto = campi[3].trim();
        String dataTesto = campi[4].trim();

        if (codice.isEmpty()
                || username.isEmpty()
                || codiceProiezione.isEmpty()
                || numeroTesto.isEmpty()
                || dataTesto.isEmpty()) {

            throw new IllegalArgumentException(
                    "uno o più campi sono vuoti"
            );
        }

        Cliente cliente = cercaCliente(
                username,
                utenti
        );

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "cliente non trovato: " + username
            );
        }

        Proiezione proiezione = cercaProiezione(
                codiceProiezione,
                proiezioni
        );

        if (proiezione == null) {
            throw new IllegalArgumentException(
                    "proiezione non trovata: "
                    + codiceProiezione
            );
        }

        try {
            int numeroBiglietti =
                    Integer.parseInt(numeroTesto);

            LocalDateTime dataCreazione =
                    LocalDateTime.parse(dataTesto);

            if (numeroBiglietti <= 0) {
                throw new IllegalArgumentException(
                        "numero di biglietti non valido"
                );
            }

            return new Prenotazione(
                    codice,
                    cliente,
                    proiezione,
                    numeroBiglietti,
                    dataCreazione
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "numero di biglietti non valido: "
                    + numeroTesto,
                    e
            );

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "data di creazione non valida: "
                    + dataTesto,
                    e
            );
        }
    }

    /**
     * Cerca un cliente tramite username.
     *
     * @param username username da cercare
     * @param utenti lista degli utenti
     * @return cliente trovato oppure null
     */
    private Cliente cercaCliente(
            String username,
            List<Utente> utenti) {

        for (Utente utente : utenti) {
            if (utente instanceof Cliente
                    && utente.getUsername()
                            .equalsIgnoreCase(username)) {

                return (Cliente) utente;
            }
        }

        return null;
    }

    /**
     * Cerca una proiezione tramite codice.
     *
     * @param codice codice della proiezione
     * @param proiezioni lista delle proiezioni
     * @return proiezione trovata oppure null
     */
    private Proiezione cercaProiezione(
            String codice,
            List<Proiezione> proiezioni) {

        for (Proiezione proiezione : proiezioni) {
            if (proiezione.getCodice()
                    .equalsIgnoreCase(codice)) {

                return proiezione;
            }
        }

        return null;
    }

    /**
     * Converte una prenotazione in una riga di testo.
     *
     * @param prenotazione prenotazione da convertire
     * @return riga pronta per il file
     */
    private String convertiInRiga(
            Prenotazione prenotazione) {

        return pulisciCampo(prenotazione.getCodice())
                + ";"
                + pulisciCampo(
                        prenotazione
                                .getCliente()
                                .getUsername()
                )
                + ";"
                + pulisciCampo(
                        prenotazione
                                .getProiezione()
                                .getCodice()
                )
                + ";"
                + prenotazione.getNumeroBiglietti()
                + ";"
                + prenotazione.getDataCreazione();
    }

    /**
     * Elimina eventuali punti e virgola dai campi.
     *
     * @param valore valore da pulire
     * @return valore pulito
     */
    private String pulisciCampo(String valore) {
        if (valore == null) {
            return "";
        }

        return valore.replace(";", ",").trim();
    }

    /**
     * Crea la cartella data se non esiste.
     */
    private void creaCartellaData() {
        try {
            Files.createDirectories(Paths.get("data"));

        } catch (IOException e) {
            System.err.println(
                    "Impossibile creare la cartella data: "
                    + e.getMessage()
            );
        }
    }

    /**
     * Crea il file delle prenotazioni se non esiste.
     */
    private void creaFileSeNecessario() {
        if (Files.exists(FILE_PRENOTAZIONI)) {
            return;
        }

        try {
            Files.createFile(FILE_PRENOTAZIONI);

        } catch (IOException e) {
            System.err.println(
                    "Impossibile creare il file prenotazioni: "
                    + e.getMessage()
            );
        }
    }
}