package lanceur;

import interfaceGraphique.IHMControle;

import java.io.IOException;

import utilitaires.logger.MyLogger;

/**
 * Test de l'interface graphique qui se connecte a l'Arene (apres lancement Arene, avant les Consoles)
 */
public class LanceIHMControle {
	
	private static String usage = "USAGE : java " + LanceIHMControle.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {		
		// init des arguments
		int port = 5099;
		String ipArene = "localhost";
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				Erreur.help(usage);
			}
			
			if (args.length > 2) {
				Erreur.TOO_MUCH_ARGS.erreur(usage);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				Erreur.PORT_NAN.erreur(usage);
			}
			
			if (args.length == 2) {
				ipArene = args[1];
			}
		}
		
		// creation du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(true,"IHM");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		try {
			logger.info("lanceur", "Creation de l'IHM...");
			IHMControle ihmc = new IHMControle(port, ipArene, logger);
			logger.info("lanceur", "Creation de l'IHM reussie");

			logger.info("lanceur", "Connexion de l'IHM au serveur...");
			ihmc.connect();
			logger.info("lanceur", "Connexion de l'IHM au serveur reussie");
			ihmc.start();
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
