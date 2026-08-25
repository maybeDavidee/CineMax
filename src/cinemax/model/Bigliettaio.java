/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.model;

import java.time.LocalDate;

/**
 * Rappresenta un bigliettaio registrato nell'applicazione.
 */
public class Bigliettaio extends Utente {

    /**
     * Costruisce un nuovo bigliettaio.
     *
     * @param nome nome
     * @param cognome cognome
     * @param username username
     * @param passwordHash password cifrata
     * @param dataNascita data di nascita
     * @param domicilio domicilio
     */
    public Bigliettaio(
            String nome,
            String cognome,
            String username,
            String passwordHash,
            LocalDate dataNascita,
            String domicilio) {

        super(
                nome,
                cognome,
                username,
                passwordHash,
                dataNascita,
                domicilio
        );
    }

    /**
     * Restituisce il ruolo del bigliettaio.
     *
     * @return ruolo bigliettaio
     */
    @Override
    public String getRuolo() {
        return "bigliettaio";
    }
}