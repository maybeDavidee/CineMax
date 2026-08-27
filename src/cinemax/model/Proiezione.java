/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una proiezione cinematografica.
 *
 * Una proiezione associa un film a una data e ora
 * e al prezzo del biglietto.
 */
public class Proiezione {

    /**
     * Capienza massima della sala cinematografica.
     */
    public static final int RIGHE_SALA = 10;
    public static final int COLONNE_SALA = 20;
    public static final int CAPIENZA_SALA = RIGHE_SALA * COLONNE_SALA;

    /**
     * Restituisce i codici di tutti i posti della sala.
     *
     * @return posti ordinati per riga e colonna
     */
    public static List<String> getPostiSala() {
        List<String> posti = new ArrayList<>();

        for (int riga = 0; riga < RIGHE_SALA; riga++) {
            for (int colonna = 1; colonna <= COLONNE_SALA; colonna++) {
                posti.add(String.valueOf((char) ('A' + riga)) + colonna);
            }
        }

        return posti;
    }

    private String codice;
    private Film film;
    private LocalDateTime dataOra;
    private double prezzoBiglietto;

    /**
     * Costruisce una nuova proiezione.
     *
     * @param codice codice identificativo della proiezione
     * @param film film associato
     * @param dataOra data e ora della proiezione
     * @param prezzoBiglietto prezzo unitario del biglietto
     */
    public Proiezione(
            String codice,
            Film film,
            LocalDateTime dataOra,
            double prezzoBiglietto) {

        if (codice == null || codice.isBlank()) {
            throw new IllegalArgumentException(
                    "Il codice della proiezione non può essere vuoto."
            );
        }

        if (film == null) {
            throw new IllegalArgumentException(
                    "Il film non può essere null."
            );
        }

        if (dataOra == null) {
            throw new IllegalArgumentException(
                    "La data della proiezione non può essere null."
            );
        }

        if (prezzoBiglietto < 0) {
            throw new IllegalArgumentException(
                    "Il prezzo non può essere negativo."
            );
        }

        this.codice = codice.trim();
        this.film = film;
        this.dataOra = dataOra;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce il codice della proiezione.
     *
     * @return codice identificativo
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Modifica il codice della proiezione.
     *
     * @param codice nuovo codice
     */
    public void setCodice(String codice) {
        if (codice == null || codice.isBlank()) {
            throw new IllegalArgumentException(
                    "Il codice non può essere vuoto."
            );
        }

        this.codice = codice.trim();
    }

    /**
     * Restituisce il film associato.
     *
     * @return film della proiezione
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Modifica il film associato.
     *
     * @param film nuovo film
     */
    public void setFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException(
                    "Il film non può essere null."
            );
        }

        this.film = film;
    }

    /**
     * Restituisce data e ora della proiezione.
     *
     * @return data e ora
     */
    public LocalDateTime getDataOra() {
        return dataOra;
    }

    /**
     * Modifica data e ora della proiezione.
     *
     * @param dataOra nuova data e ora
     */
    public void setDataOra(LocalDateTime dataOra) {
        if (dataOra == null) {
            throw new IllegalArgumentException(
                    "La data non può essere null."
            );
        }

        this.dataOra = dataOra;
    }

    /**
     * Restituisce il prezzo unitario del biglietto.
     *
     * @return prezzo del biglietto
     */
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    /**
     * Modifica il prezzo del biglietto.
     *
     * @param prezzoBiglietto nuovo prezzo
     */
    public void setPrezzoBiglietto(double prezzoBiglietto) {
        if (prezzoBiglietto < 0) {
            throw new IllegalArgumentException(
                    "Il prezzo non può essere negativo."
            );
        }

        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce una descrizione testuale della proiezione.
     *
     * Il numero di posti disponibili non viene mostrato qui,
     * perché viene calcolato dal servizio delle prenotazioni.
     *
     * @return informazioni della proiezione
     */
    @Override
    public String toString() {
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return "Codice proiezione: " + codice
                + "\n" + film
                + "\nData e ora: " + dataOra.format(formato)
                + "\nPrezzo biglietto: "
                + String.format("%.2f \u20AC", prezzoBiglietto);
    }
}  