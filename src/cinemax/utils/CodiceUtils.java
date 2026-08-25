/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.utils;

import java.util.UUID;

/**
 * Contiene metodi per generare codici identificativi univoci.
 */
public final class CodiceUtils {

    /**
     * Costruttore privato.
     */
    private CodiceUtils() {
    }

    /**
     * Genera un codice univoco per una prenotazione.
     *
     * Esempio: PRE-A1B2C3D4
     *
     * @return codice della prenotazione
     */
    public static String generaCodicePrenotazione() {
        String parteCasuale = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        return "PRE-" + parteCasuale;
    }

    /**
     * Genera il codice di una proiezione a partire
     * dalla sua posizione nel file.
     *
     * Esempio: PRO000001
     *
     * @param numero numero progressivo della proiezione
     * @return codice della proiezione
     */
    public static String generaCodiceProiezione(int numero) {
        return String.format("PRO%06d", numero);
    }
}