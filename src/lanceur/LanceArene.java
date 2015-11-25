package lanceur;

import java.io.IOException;
import java.net.InetAddress;

import serveur.Arene;
import utilitaires.logger.MyLogger;

/**
 * Lancement de l'Arene. A lancer en premier. Arguments : numero de port et duree de vie (en nombre de tour)
 */
public class LanceArene {
	
	private static String USAGE = "USAGE : java " + LanceArene.class.getName() + " [ port [ TTL_serveur ] ]";

	public static void main(String[] args) {
		// init des variables
		String ipNameArene = null;

		// init des arguments
		int port = 5099;
		long duree = 60 * 30; // Tour To Live du serveur
							// en sachant qu'un tour dure environ 1 seconde,
							// on se retrouve avec un temps par défaut de 30min
							// Si negatif, durée illimité
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				Erreur.help(USAGE);
			}
			
			if (args.length > 2) {
				Erreur.too_much_arg.erreur(USAGE);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				Erreur.port_NaN.erreur(USAGE);
			}
			
			try {
				duree = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				Erreur.ttl_incorrect.erreur(USAGE);
			}
		}
		
		// création du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement du serveur
		try {
			//ipNameArene = "localhost";
			ipNameArene = InetAddress.getLocalHost().getHostAddress();
			// le nom de la machine sur laquelle tourne l'arene
			
			logger.info("lanceur", "Creation du registre RMI sur le port "+port+"...");
			java.rmi.registry.LocateRegistry.createRegistry(port);
			logger.info("lanceur", "Creation du registre RMI reussie");

			logger.info("lanceur", "Creation du serveur sur le port "+port+"...");
			new Arene(port, ipNameArene, duree, logger);
			logger.info("lanceur", "Creation du serveur reussie");
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
