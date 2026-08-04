/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cinemax.model.Film;
import cinemax.model.Proiezione;
import cinemax.utils.CodiceUtils;

/**
 * Gestisce la lettura e la scrittura delle proiezioni
 * nel file data/proiezioni.csv.
 */
public class ProiezioneRepository {

    private static final Path FILE_PROIEZIONI =
            Paths.get("data", "proiezioni.csv");

    private static final DateTimeFormatter FORMATO_DATA_ORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String INTESTAZIONE =
            "data_ora_proiezione,"
            + "titolo_film,"
            + "genere,"
            + "regista,"
            + "anno,"
            + "durata_minuti,"
            + "eta_minima,"
            + "prezzo_biglietto";

    /**
     * Costruisce il repository e verifica l'esistenza
     * della cartella data.
     */
    public ProiezioneRepository() {
        creaCartellaData();
    }

    /**
     * Carica tutte le proiezioni presenti nel file CSV.
     *
     * @return lista delle proiezioni caricate
     */
    public List<Proiezione> caricaTutte() {
        List<Proiezione> proiezioni = new ArrayList<>();

        if (!Files.exists(FILE_PROIEZIONI)) {
            creaFileVuoto();
            return proiezioni;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FILE_PROIEZIONI,
                StandardCharsets.UTF_8)) {

            String riga;
            int numeroRiga = 0;
            int numeroProiezione = 1;

            while ((riga = reader.readLine()) != null) {
                numeroRiga++;

                if (numeroRiga == 1 && riga.startsWith("data_ora")) {
                    continue;
                }

                if (riga.isBlank()) {
                    continue;
                }

                try {
                    Proiezione proiezione =
                            creaProiezioneDaRiga(
                                    riga,
                                    numeroProiezione
                            );

                    proiezioni.add(proiezione);
                    numeroProiezione++;

                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Riga " + numeroRiga
                            + " del file proiezioni non valida: "
                            + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {
            System.err.println(
                    "Errore durante la lettura delle proiezioni: "
                    + e.getMessage()
            );
        }

        return proiezioni;
    }

    /**
     * Salva l'intera lista delle proiezioni nel file CSV.
     *
     * Il contenuto precedente viene sostituito.
     *
     * @param proiezioni lista da salvare
     * @return true se il salvataggio è riuscito
     */
    public boolean salvaTutte(List<Proiezione> proiezioni) {
        if (proiezioni == null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PROIEZIONI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write(INTESTAZIONE);
            writer.newLine();

            for (Proiezione proiezione : proiezioni) {
                writer.write(convertiInRigaCsv(proiezione));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante il salvataggio delle proiezioni: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Aggiunge una singola proiezione al file CSV.
     *
     * @param proiezione proiezione da aggiungere
     * @return true se l'aggiunta è riuscita
     */
    public boolean aggiungi(Proiezione proiezione) {
        if (proiezione == null) {
            return false;
        }

        if (!Files.exists(FILE_PROIEZIONI)) {
            creaFileVuoto();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PROIEZIONI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertiInRigaCsv(proiezione));
            writer.newLine();

            return true;

        } catch (IOException e) {
            System.err.println(
                    "Errore durante l'aggiunta della proiezione: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Crea una proiezione partendo da una riga CSV.
     *
     * @param riga riga del file
     * @param numeroProiezione numero progressivo
     * @return proiezione creata
     */
    private Proiezione creaProiezioneDaRiga(
            String riga,
            int numeroProiezione) {

        List<String> campi = separaCampiCsv(riga);

        if (campi.size() != 8) {
            throw new IllegalArgumentException(
                    "numero di campi errato: " + campi.size()
            );
        }

        try {
            LocalDateTime dataOra = LocalDateTime.parse(
                    campi.get(0),
                    FORMATO_DATA_ORA
            );

            String titolo = campi.get(1);
            String genere = campi.get(2);
            String regista = campi.get(3);
            int anno = Integer.parseInt(campi.get(4));
            int durata = Integer.parseInt(campi.get(5));
            int etaMinima = Integer.parseInt(campi.get(6));

            double prezzo = Double.parseDouble(
                    campi.get(7).replace(',', '.')
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
                    CodiceUtils.generaCodiceProiezione(
                            numeroProiezione
                    );

            return new Proiezione(
                    codice,
                    film,
                    dataOra,
                    prezzo
            );

        } catch (NumberFormatException
                | DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "valori non validi: " + riga,
                    e
            );
        }
    }

    /**
     * Converte una proiezione in una riga CSV.
     *
     * @param proiezione proiezione da convertire
     * @return riga CSV
     */
    private String convertiInRigaCsv(Proiezione proiezione) {
        Film film = proiezione.getFilm();

        return formattaCampoCsv(
                proiezione.getDataOra().format(FORMATO_DATA_ORA)
        )
                + "," + formattaCampoCsv(film.getTitolo())
                + "," + formattaCampoCsv(film.getGenere())
                + "," + formattaCampoCsv(film.getRegista())
                + "," + film.getAnno()
                + "," + film.getDurata()
                + "," + film.getEtaMinima()
                + "," + String.format(
                        Locale.US,
                        "%.2f",
                        proiezione.getPrezzoBiglietto()
                );
    }

    /**
     * Inserisce le virgolette intorno a un campo quando necessario.
     *
     * @param valore valore da salvare
     * @return campo CSV formattato
     */
    private String formattaCampoCsv(String valore) {
        if (valore == null) {
            return "";
        }

        boolean richiedeVirgolette =
                valore.contains(",")
                || valore.contains("\"")
                || valore.contains("\n");

        String valoreCorretto =
                valore.replace("\"", "\"\"");

        if (richiedeVirgolette) {
            return "\"" + valoreCorretto + "\"";
        }

        return valoreCorretto;
    }

    /**
     * Divide una riga CSV rispettando le virgolette.
     *
     * @param riga riga da dividere
     * @return campi trovati
     */
    private List<String> separaCampiCsv(String riga) {
        List<String> campi = new ArrayList<>();
        StringBuilder campoCorrente = new StringBuilder();

        boolean dentroVirgolette = false;

        for (int i = 0; i < riga.length(); i++) {
            char carattere = riga.charAt(i);

            if (carattere == '"') {
                if (dentroVirgolette
                        && i + 1 < riga.length()
                        && riga.charAt(i + 1) == '"') {

                    campoCorrente.append('"');
                    i++;

                } else {
                    dentroVirgolette = !dentroVirgolette;
                }

            } else if (carattere == ',' && !dentroVirgolette) {
                campi.add(campoCorrente.toString().trim());
                campoCorrente.setLength(0);

            } else {
                campoCorrente.append(carattere);
            }
        }

        campi.add(campoCorrente.toString().trim());

        return campi;
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
     * Crea il file delle proiezioni con la sola intestazione.
     */
    private void creaFileVuoto() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PROIEZIONI,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write(INTESTAZIONE);
            writer.newLine();

        } catch (IOException e) {
            System.err.println(
                    "Impossibile creare il file proiezioni: "
                    + e.getMessage()
            );
        }
    }
}