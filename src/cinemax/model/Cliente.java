/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.model;

import java.time.LocalDate;

/**
 * Rappresenta un cliente registrato nell'applicazione.
 */
public class Cliente extends Utente {

    /**
     * Costruisce un nuovo cliente.
     *
     * @param nome nome
     * @param cognome cognome
     * @param username username
     * @param passwordHash password cifrata
     * @param dataNascita data di nascita
     * @param domicilio domicilio
     */
    public Cliente(
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
     * Restituisce il ruolo del cliente.
     *
     * @return ruolo cliente
     */
    @Override
    public String getRuolo() {
        return "cliente";
    }
}