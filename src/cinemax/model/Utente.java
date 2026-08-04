/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
 */

package cinemax.model;

import java.time.LocalDate;

/**
 * Rappresenta un utente generico dell'applicazione CineMax.
 *
 * È la classe base da cui derivano Cliente, Bigliettaio
 * e Proiezionista.
 */
public abstract class Utente {

    private String nome;
    private String cognome;
    private String username;
    private String passwordHash;
    private LocalDate dataNascita;
    private String domicilio;

    /**
     * Costruisce un nuovo utente.
     *
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param username username usato per il login
     * @param passwordHash password cifrata
     * @param dataNascita data di nascita, può essere null
     * @param domicilio luogo del domicilio
     */
    public Utente(
            String nome,
            String cognome,
            String username,
            String passwordHash,
            LocalDate dataNascita,
            String domicilio) {

        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordHash = passwordHash;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
    }

    /**
     * Restituisce il nome.
     *
     * @return nome dell'utente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Modifica il nome.
     *
     * @param nome nuovo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome.
     *
     * @return cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Modifica il cognome.
     *
     * @param cognome nuovo cognome
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce lo username.
     *
     * @return username dell'utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Modifica lo username.
     *
     * @param username nuovo username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password cifrata.
     *
     * @return hash della password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Modifica la password cifrata.
     *
     * @param passwordHash nuovo hash della password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Restituisce la data di nascita.
     *
     * @return data di nascita oppure null
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * Modifica la data di nascita.
     *
     * @param dataNascita nuova data di nascita
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce il domicilio.
     *
     * @return domicilio dell'utente
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * Modifica il domicilio.
     *
     * @param domicilio nuovo domicilio
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Restituisce il ruolo specifico dell'utente.
     *
     * @return ruolo dell'utente
     */
    public abstract String getRuolo();

    /**
     * Restituisce nome e cognome dell'utente.
     *
     * @return nome completo
     */
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    /**
     * Restituisce una descrizione testuale dell'utente.
     *
     * La password non viene mai mostrata.
     *
     * @return dati principali dell'utente
     */
    @Override
    public String toString() {
        String data = dataNascita == null
                ? "Non specificata"
                : dataNascita.toString();

        return "Nome: " + nome
                + "\nCognome: " + cognome
                + "\nUsername: " + username
                + "\nData di nascita: " + data
                + "\nDomicilio: " + domicilio
                + "\nRuolo: " + getRuolo();
    }
}