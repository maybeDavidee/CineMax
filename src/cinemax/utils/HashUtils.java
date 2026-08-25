/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contiene metodi di utilità per la cifratura delle password.
 *
 * Le password non vengono salvate direttamente, ma trasformate
 * tramite l'algoritmo SHA-256.
 */
public final class HashUtils {

    /**
     * Costruttore privato.
     *
     * La classe contiene solamente metodi statici e non deve
     * essere istanziata.
     */
    private HashUtils() {
    }

    /**
     * Genera l'hash SHA-256 di una password.
     *
     * @param password password da cifrare
     * @return hash esadecimale della password
     * @throws IllegalArgumentException se la password è null
     */
    public static String generaHash(String password) {
        if (password == null) {
            throw new IllegalArgumentException(
                    "La password non può essere null."
            );
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder risultato = new StringBuilder();

            for (byte valore : hash) {
                risultato.append(
                        String.format("%02x", valore)
                );
            }

            return risultato.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Algoritmo SHA-256 non disponibile.",
                    e
            );
        }
    }

    /**
     * Verifica che una password corrisponda a un hash salvato.
     *
     * @param password password inserita dall'utente
     * @param hashSalvato hash presente nel file degli utenti
     * @return true se la password è corretta, false altrimenti
     */
    public static boolean verificaPassword(
            String password,
            String hashSalvato) {

        if (password == null || hashSalvato == null) {
            return false;
        }

        String hashPassword = generaHash(password);

        return hashPassword.equalsIgnoreCase(hashSalvato);
    }
    
   
    
}