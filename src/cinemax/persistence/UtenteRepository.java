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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import cinemax.model.Bigliettaio;
import cinemax.model.Cliente;
import cinemax.model.Proiezionista;
import cinemax.model.Utente;

/**
 * Gestisce la lettura e la scrittura degli utenti
 * nel file data/utenti.txt.
 */
public class UtenteRepository {

    private static final Path FILE_UTENTI =
            Paths.get("data", "utenti.txt");

    /**
     * Costruisce il repository e verifica che la cartella
     * data e il file utenti esistano.
     */
    public UtenteRepository() {
        creaCartellaData();
        creaFileSeNecessario();
    }

    /**
     * Carica tutti gli utenti presenti nel file.
     *
     * Il formato previsto è:
     * nome;cognome;username;passwordHash;dataNascita;domicilio;ruolo
     *
     * @return lista degli utenti caricati
     */
    public List<Utente> caricaTutti() {
        List<Utente> utenti = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                FILE_UTENTI,
                StandardCharsets.UTF_8)) {

            String riga;
            int numeroRiga = 0;

            while ((riga = reader.readLine()) != null) {
                numeroRiga++;

                if (riga.isBlank()) {
                    continue;
                }

                try {
                    Utente utente = creaUtenteDaRiga(riga);
                    utenti.add(utente);

                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Utente non valido alla riga "
                            + numeroRiga + ": "
                            + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "Errore durante la lettura degli utenti: "
                    + e.getMessage()
            );
        }

        return utenti;
    }

    /**
     * Salva l'intera lista degli utenti.
     *
     * Il contenuto precedente viene sostituito.
     *
     * @param utenti lista da salvare
     * @return true se il salvataggio è riuscito
     */
    public boolean salvaTutti(List<Utente> utenti) {
        if (utenti == null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_UTENTI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Utente utente : utenti) {
                writer.write(convertiInRiga(utente));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante il salvataggio degli utenti: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Aggiunge un singolo utente in fondo al file.
     *
     * @param utente utente da aggiungere
     * @return true se l'aggiunta è riuscita
     */
    public boolean aggiungi(Utente utente) {
        if (utente == null) {
            return false;
        }

        try {
            boolean serveNuovaRiga =
                    Files.exists(FILE_UTENTI)
                    && Files.size(FILE_UTENTI) > 0
                    && !terminaConNuovaRiga();

            try (BufferedWriter writer = Files.newBufferedWriter(
                    FILE_UTENTI,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                if (serveNuovaRiga) {
                    writer.newLine();
                }

                writer.write(convertiInRiga(utente));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante l'aggiunta dell'utente: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
    /**
     * Verifica se il file termina già con un carattere
     * di nuova riga.
     *
     * @return true se il file termina con una nuova riga
     * @throws IOException se il file non può essere letto
     */
    private boolean terminaConNuovaRiga() throws IOException {
        byte[] contenuto = Files.readAllBytes(FILE_UTENTI);

        if (contenuto.length == 0) {
            return true;
        }

        byte ultimoByte = contenuto[contenuto.length - 1];

        return ultimoByte == '\n' || ultimoByte == '\r';
    }

    /**
     * Cerca un utente tramite username.
     *
     * @param username username da cercare
     * @return utente trovato oppure null
     */
    public Utente cercaPerUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        List<Utente> utenti = caricaTutti();

        for (Utente utente : utenti) {
            if (utente.getUsername()
                    .equalsIgnoreCase(username.trim())) {

                return utente;
            }
        }

        return null;
    }

    /**
     * Verifica se uno username è già utilizzato.
     *
     * @param username username da controllare
     * @return true se esiste già
     */
    public boolean usernameEsistente(String username) {
        return cercaPerUsername(username) != null;
    }

    /**
     * Crea un utente partendo da una riga del file.
     *
     * @param riga riga da convertire
     * @return utente creato
     */
    private Utente creaUtenteDaRiga(String riga) {
        String[] campi = riga.split(";", -1);

        if (campi.length != 7) {
            throw new IllegalArgumentException(
                    "sono richiesti esattamente 7 campi"
            );
        }

        String nome = campi[0].trim();
        String cognome = campi[1].trim();
        String username = campi[2].trim();
        String passwordHash = campi[3].trim();
        String dataTesto = campi[4].trim();
        String domicilio = campi[5].trim();
        String ruolo = campi[6].trim().toLowerCase();

        if (nome.isEmpty()
                || cognome.isEmpty()
                || username.isEmpty()
                || passwordHash.isEmpty()
                || domicilio.isEmpty()
                || ruolo.isEmpty()) {

            throw new IllegalArgumentException(
                    "uno o più campi obbligatori sono vuoti"
            );
        }

        LocalDate dataNascita = null;

        if (!dataTesto.isEmpty()) {
            try {
                dataNascita = LocalDate.parse(dataTesto);

            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "data di nascita non valida: "
                        + dataTesto
                );
            }
        }

        switch (ruolo) {
            case "cliente":
                return new Cliente(
                        nome,
                        cognome,
                        username,
                        passwordHash,
                        dataNascita,
                        domicilio
                );

            case "bigliettaio":
                return new Bigliettaio(
                        nome,
                        cognome,
                        username,
                        passwordHash,
                        dataNascita,
                        domicilio
                );

            case "proiezionista":
                return new Proiezionista(
                        nome,
                        cognome,
                        username,
                        passwordHash,
                        dataNascita,
                        domicilio
                );

            default:
                throw new IllegalArgumentException(
                        "ruolo non riconosciuto: " + ruolo
                );
        }
    }

    /**
     * Converte un utente in una riga di testo.
     *
     * @param utente utente da convertire
     * @return riga pronta per il file
     */
    private String convertiInRiga(Utente utente) {
        String dataNascita = utente.getDataNascita() == null
                ? ""
                : utente.getDataNascita().toString();

        return pulisciCampo(utente.getNome()) + ";"
                + pulisciCampo(utente.getCognome()) + ";"
                + pulisciCampo(utente.getUsername()) + ";"
                + pulisciCampo(utente.getPasswordHash()) + ";"
                + dataNascita + ";"
                + pulisciCampo(utente.getDomicilio()) + ";"
                + pulisciCampo(utente.getRuolo());
    }

    /**
     * Evita che il carattere separatore venga inserito nei campi.
     *
     * @param valore valore da pulire
     * @return valore senza punto e virgola
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
     * Crea il file utenti se non esiste.
     */
    private void creaFileSeNecessario() {
        if (Files.exists(FILE_UTENTI)) {
            return;
        }

        try {
            Files.createFile(FILE_UTENTI);

        } catch (IOException e) {
            System.err.println(
                    "Impossibile creare il file utenti: "
                    + e.getMessage()
            );
        }
    }
}