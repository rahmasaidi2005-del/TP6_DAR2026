package corbaServer;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import service.BanqueImpl;

/**
 * Serveur CORBA du service bancaire.
 * Ordre d'execution : annuaire (tnameserv) -> BanqueServer -> BanqueClient.
 */
public class BanqueServer {

    // Nom sous lequel l'objet distant est publie dans l'annuaire
    public static final String NOM_SERVICE = "BK";

    public static void main(String[] args) {
        try {
            // a. Initialisation de l'ORB
            ORB orb = ORB.init(args, null);

            // b. Initialisation de l'adaptateur d'objets (POA)
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // c. Configuration de l'annuaire JNDI
            //    (jndi.properties fournit la fabrique et l'URL du Naming Service,
            //     on lui passe notre ORB pour que la reference soit celle de ce serveur)
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put("java.naming.corba.orb", orb);
            Context ctx = new InitialContext(env);

            // d. Creation du servant
            BanqueImpl servant = new BanqueImpl();

            // e. Enregistrement de la reference dans l'annuaire
            org.omg.CORBA.Object reference = rootPOA.servant_to_reference(servant);
            ctx.rebind(NOM_SERVICE, reference);

            System.out.println("Serveur Banque pret : objet \"" + NOM_SERVICE
                    + "\" enregistre dans l'annuaire.");
            System.out.println("En attente des requetes des clients...");

            // f. Mise en attente des requetes
            orb.run();

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e);
            e.printStackTrace();
        }
    }
}
