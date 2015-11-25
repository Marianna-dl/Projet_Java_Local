package lanceur;

import interfaceGraphique.IHM;

import java.io.IOException;

import utilitaires.logger.MyLogger;

/**
 * Test de l'interface graphique qui se connecte a l'Arene (apres lancement Arene, avant les Consoles)
 */
public class LanceIHM {
	
	private static String USAGE = "USAGE : java " + LanceIHM.class.getName() + " [ port [ nom_arene ] ]";

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
		
		// creation du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(true,"IHM");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		try {
			logger.info("lanceur", "Création de l'IHM...");
			IHM ihm = new IHM(port, ipArene, logger);
			logger.info("lanceur", "Création de l'IHM réussie");

			logger.info("lanceur", "Connexion de l'IHM au serveur...");
			ihm.connect();
			logger.info("lanceur", "Connexion de l'IHM au serveur réussie");
			ihm.start();
			logger.info("lanceur", "Mise en route de l'IHM réussie");
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
