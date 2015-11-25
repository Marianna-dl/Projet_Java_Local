package lanceur;

import java.io.IOException;
import java.net.InetAddress;

import serveur.AreneTournoi;
import utilitaires.logger.MyLogger;

/**
 * Lancement de l'Arene. A lancer en premier. Arguments : numero de port et duree de vie (en nombre de tour)
 */
public class LanceAreneTournoi {
	
	private static String usage = "USAGE : java " + LanceAreneTournoi.class.getName() + " [ port [ TTL_serveur ] ]";

	public static void main(String[] args) {
		// init des variables
		String ipNameArene = null;

		// init des arguments
		int port = 5099;
		long duree = 60 * 30; // tours to Live du serveur
							// en sachant qu'un tour dure environ 1 seconde,
							// on se retrouve avec un temps par defaut de 30 min
							// si negatif, duree illimite
		
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
			
			try {
				duree = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				Erreur.INCORRECT_TTL.erreur(usage);
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
			//ipNameArene = "localhost";
			ipNameArene = InetAddress.getLocalHost().getHostAddress();
			// le nom de la machine sur laquelle tourne l'arene
			
			logger.info("lanceur", "Creation du registre RMI sur le port "+port+"...");
			java.rmi.registry.LocateRegistry.createRegistry(port);
			logger.info("lanceur", "Creation du registre RMI reussie");

			logger.info("lanceur", "Creation du serveur sur le port "+port+"...");
			new AreneTournoi(port, ipNameArene, duree, logger);
			logger.info("lanceur", "Creation du serveur reussie");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
