/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
 */

package cinemax.model;

import java.time.LocalDate;

/**
 * Rappresenta un proiezionista registrato nell'applicazione.
 */
public class Proiezionista extends Utente {

    /**
     * Costruisce un nuovo proiezionista.
     *
     * @param nome nome
     * @param cognome cognome
     * @param username username
     * @param passwordHash password cifrata
     * @param dataNascita data di nascita
     * @param domicilio domicilio
     */
    public Proiezionista(
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
     * Restituisce il ruolo del proiezionista.
     *
     * @return ruolo proiezionista
     */
    @Override
    public String getRuolo() {
        return "proiezionista";
    }
}