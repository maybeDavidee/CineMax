/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
 */

package cinemax.model;

/**
 * Rappresenta un film disponibile nel palinsesto
 * dell'applicazione CineMax.
 */
public class Film {

    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int etaMinima;

    /**
     * Costruisce un nuovo film.
     *
     * @param titolo titolo del film
     * @param genere genere cinematografico
     * @param regista nome del regista
     * @param anno anno di uscita
     * @param durata durata in minuti
     * @param etaMinima età minima del pubblico
     */
    public Film(
            String titolo,
            String genere,
            String regista,
            int anno,
            int durata,
            int etaMinima) {

        setTitolo(titolo);
        setGenere(genere);
        setRegista(regista);
        setAnno(anno);
        setDurata(durata);
        setEtaMinima(etaMinima);
    }

    /**
     * Restituisce il titolo del film.
     *
     * @return titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Modifica il titolo.
     *
     * @param titolo nuovo titolo
     */
    public void setTitolo(String titolo) {
        if (titolo == null || titolo.isBlank()) {
            throw new IllegalArgumentException(
                    "Il titolo non può essere vuoto."
            );
        }

        this.titolo = titolo.trim();
    }

    /**
     * Restituisce il genere del film.
     *
     * @return genere
     */
    public String getGenere() {
        return genere;
    }

    /**
     * Modifica il genere.
     *
     * @param genere nuovo genere
     */
    public void setGenere(String genere) {
        if (genere == null || genere.isBlank()) {
            throw new IllegalArgumentException(
                    "Il genere non può essere vuoto."
            );
        }

        this.genere = genere.trim();
    }

    /**
     * Restituisce il nome del regista.
     *
     * @return regista
     */
    public String getRegista() {
        return regista;
    }

    /**
     * Modifica il nome del regista.
     *
     * @param regista nuovo regista
     */
    public void setRegista(String regista) {
        if (regista == null || regista.isBlank()) {
            throw new IllegalArgumentException(
                    "Il regista non può essere vuoto."
            );
        }

        this.regista = regista.trim();
    }

    /**
     * Restituisce l'anno di uscita.
     *
     * @return anno
     */
    public int getAnno() {
        return anno;
    }

    /**
     * Modifica l'anno di uscita.
     *
     * @param anno nuovo anno
     */
    public void setAnno(int anno) {
        if (anno < 1888 || anno > 2100) {
            throw new IllegalArgumentException(
                    "Anno del film non valido."
            );
        }

        this.anno = anno;
    }

    /**
     * Restituisce la durata in minuti.
     *
     * @return durata
     */
    public int getDurata() {
        return durata;
    }

    /**
     * Modifica la durata del film.
     *
     * @param durata nuova durata in minuti
     */
    public void setDurata(int durata) {
        if (durata <= 0) {
            throw new IllegalArgumentException(
                    "La durata deve essere maggiore di zero."
            );
        }

        this.durata = durata;
    }

    /**
     * Restituisce l'età minima.
     *
     * @return età minima
     */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * Modifica l'età minima.
     *
     * @param etaMinima nuova età minima
     */
    public void setEtaMinima(int etaMinima) {
        if (etaMinima < 0 || etaMinima > 21) {
            throw new IllegalArgumentException(
                    "Età minima non valida."
            );
        }

        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce una descrizione testuale del film.
     *
     * @return informazioni del film
     */
    @Override
    public String toString() {
        return "Titolo: " + titolo
                + "\nGenere: " + genere
                + "\nRegista: " + regista
                + "\nAnno: " + anno
                + "\nDurata: " + durata + " minuti"
                + "\nEtà minima: " + etaMinima + " anni";
    }
}