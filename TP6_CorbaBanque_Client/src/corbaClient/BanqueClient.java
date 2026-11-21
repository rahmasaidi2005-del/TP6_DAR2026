package corbaClient;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.SystemException;

import corbaBanque.Compte;
import corbaBanque.IBanqueRemote;
import corbaBanque.IBanqueRemoteHelper;

/**
 * Client CORBA du service bancaire : teste chaque methode distante via le Stub.
 */
public class BanqueClient {

    // Nom de l'objet distant dans l'annuaire (le meme que celui du serveur)
    private static final String NOM_SERVICE = "BK";

    // Petite interface pour executer chaque test et gerer les erreurs au meme endroit
    private interface Action {
        void executer() throws Exception;
    }

    private static void tester(String titre, Action action) {
        System.out.println("--- " + titre);
        try {
            action.executer();
        } catch (SystemException e) {
            System.out.println("    ERREUR : " + messageErreur(e));
        } catch (Exception e) {
            System.out.println("    ERREUR : " + e);
        }
    }

    /** Traduit l'exception CORBA recue en message lisible (le "minor code" vient du serveur). */
    private static String messageErreur(SystemException e) {
        if (e instanceof BAD_PARAM) {
            switch (e.minor) {
                case 1: return "ce compte existe deja";
                case 2: return "compte introuvable";
                case 3: return "montant invalide";
                case 4: return "solde insuffisant";
                default: break;
            }
        }
        return e.toString();
    }

    private static void afficher(Compte c) {
        System.out.println("    Compte " + c.code + " : solde = " + c.solde);
    }

    public static void main(String[] args) {
        try {
            // a. Proprietes de l'annuaire JNDI (fichier jndi.properties)
            Context ctx = new InitialContext();

            // b. Recuperation de la reference de l'objet distant
            org.omg.CORBA.Object reference = (org.omg.CORBA.Object) ctx.lookup(NOM_SERVICE);

            // c. Conversion de la reference en Stub (narrow)
            final IBanqueRemote banque = IBanqueRemoteHelper.narrow(reference);
            System.out.println("Connecte au service bancaire (stub obtenu).\n");

            // d. Appel distant de chaque methode
            tester("creerCompte(code=1, solde=1000)", () -> banque.creerCompte(new Compte(1, 1000f)));
            tester("creerCompte(code=2, solde=500)", () -> banque.creerCompte(new Compte(2, 500f)));
            tester("creerCompte(code=3, solde=2500)", () -> banque.creerCompte(new Compte(3, 2500f)));

            tester("verser(250, compte 1)", () -> banque.verser(250f, 1));
            tester("retirer(100, compte 2)", () -> banque.retirer(100f, 2));

            tester("getCompte(1)", () -> afficher(banque.getCompte(1)));
            tester("getCompte(2)", () -> afficher(banque.getCompte(2)));

            tester("getComptes()", () -> {
                Compte[] liste = banque.getComptes();
                System.out.println("    " + liste.length + " compte(s) :");
                for (Compte c : liste) {
                    afficher(c);
                }
            });

            tester("conversion(100 EUR)", () ->
                    System.out.println("    100.0 EUR = " + banque.conversion(100f) + " DT"));

            // Cas d'erreur volontaires
            System.out.println("\n=== Cas d'erreur ===");
            tester("retirer(10000, compte 2)  [solde insuffisant]", () -> banque.retirer(10000f, 2));
            tester("getCompte(99)             [compte inexistant]", () -> afficher(banque.getCompte(99)));
            tester("verser(-5, compte 1)      [montant invalide]", () -> banque.verser(-5f, 1));
            tester("creerCompte(code=1, 50)   [compte deja existant]", () -> banque.creerCompte(new Compte(1, 50f)));

        } catch (Exception e) {
            System.err.println("Impossible de contacter le service : " + e);
            System.err.println("Verifiez que l'annuaire (tnameserv) et le serveur sont bien lances.");
            e.printStackTrace();
        }
    }
}
