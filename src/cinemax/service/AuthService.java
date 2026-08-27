/*
 * Autori:
 * Davide Gallorini - Matricola: 766972 - Sede: VA
 * Lorenzo Guidi - Matricola: 766939 - Sede: VA
 * Alberto Medizza - Matricola: 765253 - Sede: VA
 */

package cinemax.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import cinemax.model.Cliente;
import cinemax.model.Utente;
import cinemax.utils.HashUtils;
import cinemax.persistence.UtenteRepository;

/**
 * Gestisce le operazioni di autenticazione e registrazione
 * degli utenti dell'applicazione CineMax.
 */
public class AuthService {

	private final UtenteRepository repository;
    private final List<Utente> utenti;

    /**
     * Costruisce il servizio di autenticazione.
     *
     * @param repository repository degli utenti
     */
    public AuthService(UtenteRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Il repository non può essere null."
            );
        }

        this.repository = repository;
        this.utenti = new ArrayList<>(
                repository.caricaTutti()
        );
    }

    /**
     * Effettua il login tramite username e password.
     *
     * @param username username inserito
     * @param password password inserita in chiaro
     * @return utente autenticato oppure null
     */
    public Utente login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        for (Utente utente : utenti) {
            boolean stessoUsername =
                    utente.getUsername()
                            .equalsIgnoreCase(username.trim());

            boolean passwordCorretta =
                    HashUtils.verificaPassword(
                            password,
                            utente.getPasswordHash()
                    );

            if (stessoUsername && passwordCorretta) {
                return utente;
            }
        }

        return null;
    }

    /**
     * Registra un nuovo cliente.
     *
     * Lo username deve essere univoco e la password
     * viene salvata sotto forma di hash SHA-256.
     *
     * @param nome nome del cliente
     * @param cognome cognome del cliente
     * @param username username scelto
     * @param password password in chiaro
     * @param dataNascita data di nascita obbligatoria
     * @param domicilio domicilio del cliente
     * @return cliente registrato oppure null
     */
    public Cliente registraCliente(
            String nome,
            String cognome,
            String username,
            String password,
            LocalDate dataNascita,
            String domicilio) {

        if (!datiRegistrazioneValidi(
                nome,
                cognome,
                username,
                password,
                domicilio)) {

            return null;
        }
        
        if (dataNascita == null
                || dataNascita.isAfter(LocalDate.now())) {

            return null;
        }

        if (usernameEsistente(username)) {
            return null;
        }

        String passwordHash =
                HashUtils.generaHash(password);

        Cliente cliente = new Cliente(
                nome.trim(),
                cognome.trim(),
                username.trim(),
                passwordHash,
                dataNascita,
                domicilio.trim()
        );

        boolean salvato =
                repository.aggiungi(cliente);

        if (!salvato) {
            return null;
        }

        utenti.add(cliente);

        return cliente;
    }

    /**
     * Verifica se uno username è già utilizzato.
     *
     * @param username username da controllare
     * @return true se esiste già
     */
    public boolean usernameEsistente(String username) {
        if (username == null) {
            return false;
        }

        for (Utente utente : utenti) {
            if (utente.getUsername()
                    .equalsIgnoreCase(username.trim())) {

                return true;
            }
        }

        return false;
    }

    /**
     * Controlla la validità dei dati obbligatori
     * utilizzati durante la registrazione.
     *
     * @param nome nome
     * @param cognome cognome
     * @param username username
     * @param password password
     * @param domicilio domicilio
     * @return true se i dati sono validi
     */
    private boolean datiRegistrazioneValidi(
            String nome,
            String cognome,
            String username,
            String password,
            String domicilio) {

        if (nome == null
                || cognome == null
                || username == null
                || password == null
                || domicilio == null) {

            return false;
        }

        if (nome.isBlank()
                || cognome.isBlank()
                || username.isBlank()
                || password.isBlank()
                || domicilio.isBlank()) {

            return false;
        }

        return password.length() >= 6;
    }

    /**
     * Restituisce una copia della lista degli utenti.
     *
     * @return lista degli utenti
     */
    public List<Utente> getUtenti() {
        return new ArrayList<>(utenti);
    }
}