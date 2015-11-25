package lanceur;

import interfaceGraphique.IHMControle;

import java.io.IOException;

import utilitaires.logger.MyLogger;

/**
 * Test de l'interface graphique qui se connecte a l'Arene (apres lancement Arene, avant les Consoles)
 */
public class LanceIHMControle {
	
	private static String USAGE = "USAGE : java " + LanceIHMControle.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {		
		// init des arguments
		int port = 5099;
		String ipArene = "localhost";
		
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
			
			if (args.length == 2) {
				ipArene = args[1];
			}
		}
		
		// création du logger
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
			logger.info("lanceur", "Connexion de l'IHM au serveur réussie");
			ihmc.start();
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
