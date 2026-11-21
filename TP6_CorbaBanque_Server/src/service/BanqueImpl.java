package service;

import java.util.Map;
import java.util.TreeMap;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;

import corbaBanque.Compte;
import corbaBanque.IBanqueRemotePOA;

/**
 * Servant CORBA : implementation du service bancaire.
 * Herite de IBanqueRemotePOA (squelette genere par idlj) qui implemente
 * l'interface IBanqueRemoteOperations.
 *
 * Les comptes sont conserves en memoire dans une table (code -> compte).
 * Les erreurs metier sont signalees au client par une exception CORBA
 * BAD_PARAM dont le "minor code" indique la cause (voir constantes ERR_*).
 */
public class BanqueImpl extends IBanqueRemotePOA {

    // Codes d'erreur transmis au client (champ "minor" de l'exception)
    public static final int ERR_COMPTE_EXISTANT = 1;
    public static final int ERR_COMPTE_INTROUVABLE = 2;
    public static final int ERR_MONTANT_INVALIDE = 3;
    public static final int ERR_SOLDE_INSUFFISANT = 4;

    // Taux de conversion fixe : 1 Euro = 3.40 Dinars Tunisiens
    private static final double TAUX_EUR_DT = 3.40;

    // Table des comptes, triee par code
    private final Map<Integer, Compte> comptes = new TreeMap<Integer, Compte>();

    /** Affiche l'erreur cote serveur et fabrique l'exception a lever. */
    private static BAD_PARAM erreur(String message, int codeErreur) {
        System.out.println("[Serveur] Erreur : " + message);
        return new BAD_PARAM(message, codeErreur, CompletionStatus.COMPLETED_NO);
    }

    /** Retourne le compte de ce code, ou leve une erreur s'il n'existe pas. */
    private Compte chercher(int code) {
        Compte c = comptes.get(Integer.valueOf(code));
        if (c == null) {
            throw erreur("Compte " + code + " introuvable", ERR_COMPTE_INTROUVABLE);
        }
        return c;
    }

    @Override
    public synchronized void creerCompte(Compte cpte) {
        if (comptes.containsKey(Integer.valueOf(cpte.code))) {
            throw erreur("Le compte " + cpte.code + " existe deja", ERR_COMPTE_EXISTANT);
        }
        if (cpte.solde < 0) {
            throw erreur("Solde initial negatif : " + cpte.solde, ERR_MONTANT_INVALIDE);
        }
        comptes.put(Integer.valueOf(cpte.code), new Compte(cpte.code, cpte.solde));
        System.out.println("[Serveur] Compte " + cpte.code + " cree avec un solde de " + cpte.solde);
    }

    @Override
    public synchronized void verser(float mt, int code) {
        Compte c = chercher(code);
        if (mt <= 0) {
            throw erreur("Montant a verser invalide : " + mt, ERR_MONTANT_INVALIDE);
        }
        c.solde += mt;
        System.out.println("[Serveur] Versement de " + mt + " sur le compte " + code
                + " -> nouveau solde : " + c.solde);
    }

    @Override
    public synchronized void retirer(float mt, int code) {
        Compte c = chercher(code);
        if (mt <= 0) {
            throw erreur("Montant a retirer invalide : " + mt, ERR_MONTANT_INVALIDE);
        }
        if (mt > c.solde) {
            throw erreur("Solde insuffisant sur le compte " + code + " (solde : " + c.solde
                    + ", retrait demande : " + mt + ")", ERR_SOLDE_INSUFFISANT);
        }
        c.solde -= mt;
        System.out.println("[Serveur] Retrait de " + mt + " sur le compte " + code
                + " -> nouveau solde : " + c.solde);
    }

    @Override
    public synchronized Compte getCompte(int code) {
        Compte c = chercher(code);
        System.out.println("[Serveur] Consultation du compte " + code);
        // on renvoie une copie : le client ne doit pas modifier l'original
        return new Compte(c.code, c.solde);
    }

    @Override
    public synchronized Compte[] getComptes() {
        Compte[] resultat = new Compte[comptes.size()];
        int i = 0;
        for (Compte c : comptes.values()) {
            resultat[i++] = new Compte(c.code, c.solde);
        }
        System.out.println("[Serveur] Consultation de la liste des comptes (" + resultat.length + ")");
        return resultat;
    }

    @Override
    public double conversion(float mt) {
        double dt = mt * TAUX_EUR_DT;
        System.out.println("[Serveur] Conversion de " + mt + " EUR -> " + dt + " DT");
        return dt;
    }
}
