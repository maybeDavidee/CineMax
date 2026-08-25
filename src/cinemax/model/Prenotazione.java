/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una prenotazione effettuata da un cliente
 * per una determinata proiezione.
 */
public class Prenotazione {

    private String codice;
    private Cliente cliente;
    private Proiezione proiezione;
    private int numeroBiglietti;
    private LocalDateTime dataCreazione;

    /**
     * Costruisce una nuova prenotazione.
     *
     * @param codice codice univoco della prenotazione
     * @param cliente cliente che effettua la prenotazione
     * @param proiezione proiezione scelta
     * @param numeroBiglietti numero di biglietti richiesti
     * @param dataCreazione data e ora di creazione
     */
    public Prenotazione(
            String codice,
            Cliente cliente,
            Proiezione proiezione,
            int numeroBiglietti,
            LocalDateTime dataCreazione) {

        this.codice = codice;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroBiglietti = numeroBiglietti;
        this.dataCreazione = dataCreazione;
    }

    /**
     * Restituisce il codice univoco della prenotazione.
     *
     * @return codice della prenotazione
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Modifica il codice della prenotazione.
     *
     * @param codice nuovo codice
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * Restituisce il cliente associato alla prenotazione.
     *
     * @return cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Modifica il cliente della prenotazione.
     *
     * @param cliente nuovo cliente
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Restituisce la proiezione associata.
     *
     * @return proiezione
     */
    public Proiezione getProiezione() {
        return proiezione;
    }

    /**
     * Modifica la proiezione associata.
     *
     * Dopo la modifica viene ricalcolato il costo totale.
     *
     * @param proiezione nuova proiezione
     */
    public void setProiezione(Proiezione proiezione) {
        if (proiezione == null) {
            throw new IllegalArgumentException(
                    "La proiezione non può essere null."
            );
        }

        this.proiezione = proiezione;
    }

    /**
     * Restituisce il numero di biglietti.
     *
     * @return numero di biglietti
     */
    public int getNumeroBiglietti() {
        return numeroBiglietti;
    }

    /**
     * Modifica il numero di biglietti.
     *
     * Dopo la modifica viene ricalcolato il costo totale.
     *
     * @param numeroBiglietti nuovo numero di biglietti
     */
    public void setNumeroBiglietti(int numeroBiglietti) {
        if (numeroBiglietti <= 0) {
            throw new IllegalArgumentException(
                    "Il numero di biglietti deve essere positivo."
            );
        }

        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * Calcola il costo totale della prenotazione.
     *
     * @return costo totale
     */
    public double getCostoTotale() {
        return numeroBiglietti
                * proiezione.getPrezzoBiglietto();
    }

    /**
     * Restituisce la data e l'ora di creazione.
     *
     * @return data di creazione
     */
    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    /**
     * Modifica la data e l'ora di creazione.
     *
     * @param dataCreazione nuova data di creazione
     */
    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    

    /**
     * Restituisce una descrizione completa della prenotazione.
     *
     * @return informazioni della prenotazione
     */
    @Override
    public String toString() {
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return "Codice prenotazione: " + codice
                + "\nCliente: " + cliente.getNomeCompleto()
                + "\nFilm: "
                + proiezione.getFilm().getTitolo()
                + "\nData proiezione: "
                + proiezione.getDataOra().format(formato)
                + "\nNumero biglietti: " + numeroBiglietti
                + "\nCosto unitario: "
                + String.format(
                        "%.2f €",
                        proiezione.getPrezzoBiglietto()
                )
                + "\nCosto totale: "
                + String.format("%.2f €", getCostoTotale())
                + "\nPrenotazione creata il: "
                + dataCreazione.format(formato);
    }
}