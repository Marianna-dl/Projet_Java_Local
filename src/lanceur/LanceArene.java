package lanceur;

import java.io.IOException;
import java.net.InetAddress;

import logger.MyLogger;
import serveur.Arene;
import utilitaires.Constantes;

/**
 * Lancement de l'arene. A lancer en premier. 
 * Arguments : numero de port et duree de vie (en nombre de tours).
 */
public class LanceArene {
	
	private static String usage = "USAGE : java " + LanceArene.class.getName() + " [ port [ TTL_serveur ] ]";

	public static void main(String[] args) {
		// init des variables
		String ipNameArene = null;

		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		
		long duree = 60 * 1; // tours to live du serveur
							 // sachant qu'un tour dure environ 1 seconde
							 // si negatif, duree illimite
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				Erreur.help(usage);
			}
			
			if (args.length > 2) {
				Erreur.TROP_ARGS.erreur(usage);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				Erreur.PORT_NAN.erreur(usage);
			}
			
			try {
				duree = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				Erreur.TTL_INCORRECT.erreur(usage);
			}
		}
		
		// creation du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement du serveur
		try {
			ipNameArene = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("lanceur", "Creation du registre RMI sur le port " + port + "...");
			java.rmi.registry.LocateRegistry.createRegistry(port);
			logger.info("lanceur", "Creation du registre RMI reussie");

			logger.info("lanceur", "Creation du serveur sur le port " + port + "...");
			new Arene(port, ipNameArene, duree, logger);
			logger.info("lanceur", "Creation du serveur reussie");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
