/*
 * Autori:
 * Davide Gallorini - Matricola: DA INSERIRE - Sede: VA
 * Lorenzo Guidi - Matricola: DA INSERIRE - Sede: VA
 * Alberto Medizza - Matricola: DA INSERIRE - Sede: VA
 */

package cinemax.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Contiene metodi di utilità per leggere e validare
 * i dati inseriti dall'utente tramite terminale.
 */
public final class InputUtils {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_DATA_ORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Costruttore privato.
     */
    private InputUtils() {
    }

    /**
     * Legge una stringa non vuota.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio messaggio mostrato all'utente
     * @return stringa non vuota
     */
    public static String leggiStringaNonVuota(
            Scanner scanner,
            String messaggio) {

        while (true) {
            System.out.print(messaggio);
            String valore = scanner.nextLine().trim();

            if (!valore.isEmpty()) {
                return valore;
            }

            System.out.println(
                    "Il valore non può essere vuoto."
            );
        }
    }

    /**
     * Legge un numero intero compreso tra minimo e massimo.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio messaggio mostrato all'utente
     * @param minimo valore minimo accettato
     * @param massimo valore massimo accettato
     * @return numero intero valido
     */
    public static int leggiIntero(
            Scanner scanner,
            String messaggio,
            int minimo,
            int massimo) {

        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();

            try {
                int valore = Integer.parseInt(input);

                if (valore >= minimo && valore <= massimo) {
                    return valore;
                }

                System.out.println(
                        "Inserisci un numero compreso tra "
                        + minimo + " e " + massimo + "."
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "Devi inserire un numero intero valido."
                );
            }
        }
    }

    /**
     * Legge un numero decimale positivo.
     *
     * Sono accettati sia il punto sia la virgola.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio messaggio mostrato all'utente
     * @return valore decimale positivo
     */
    public static double leggiDoublePositivo(
            Scanner scanner,
            String messaggio) {

        while (true) {
            System.out.print(messaggio);

            String input = scanner.nextLine()
                    .trim()
                    .replace(',', '.');

            try {
                double valore = Double.parseDouble(input);

                if (valore >= 0) {
                    return valore;
                }

                System.out.println(
                        "Il valore non può essere negativo."
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "Devi inserire un numero valido."
                );
            }
        }
    }

    /**
     * Legge una data nel formato gg/MM/aaaa.
     *
     * È possibile lasciare il campo vuoto se la data è facoltativa.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio messaggio mostrato all'utente
     * @param facoltativa indica se il campo può essere vuoto
     * @return data inserita oppure null
     */
    public static LocalDate leggiData(
            Scanner scanner,
            String messaggio,
            boolean facoltativa) {

        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();

            if (facoltativa && input.isEmpty()) {
                return null;
            }

            try {
                return LocalDate.parse(input, FORMATO_DATA);

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Data non valida. Usa il formato gg/MM/aaaa."
                );
            }
        }
    }

    /**
     * Legge una data e ora nel formato gg/MM/aaaa HH:mm.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio messaggio mostrato all'utente
     * @return data e ora valide
     */
    public static LocalDateTime leggiDataOra(
            Scanner scanner,
            String messaggio) {

        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();

            try {
                return LocalDateTime.parse(
                        input,
                        FORMATO_DATA_ORA
                );

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Data e ora non valide. "
                        + "Usa il formato gg/MM/aaaa HH:mm."
                );
            }
        }
    }

    /**
     * Chiede all'utente una conferma sì/no.
     *
     * @param scanner scanner usato per l'input
     * @param messaggio domanda mostrata all'utente
     * @return true per sì, false per no
     */
    public static boolean leggiConferma(
            Scanner scanner,
            String messaggio) {

        while (true) {
            System.out.print(messaggio + " (s/n): ");
            String risposta = scanner.nextLine()
                    .trim()
                    .toLowerCase();

            if (risposta.equals("s")
                    || risposta.equals("si")
                    || risposta.equals("sì")) {
                return true;
            }

            if (risposta.equals("n")
                    || risposta.equals("no")) {
                return false;
            }

            System.out.println(
                    "Risposta non valida. Inserisci s oppure n."
            );
        }
    }
}