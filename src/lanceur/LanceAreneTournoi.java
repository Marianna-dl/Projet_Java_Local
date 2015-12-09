package lanceur;

import java.io.IOException;
import java.net.InetAddress;

import logger.LoggerProjet;
import serveur.AreneTournoi;
import utilitaires.Constantes;

/**
 * Lancement de l'arene, version pour le tournoi. A lancer en premier. 
 * Arguments : numero de port et duree de vie (en nombre de tours).
 */
public class LanceAreneTournoi {
	
	private static String usage = "USAGE : java " + LanceAreneTournoi.class.getName() + " [ port [ nbTours ] ]";

	public static void main(String[] args) {
		String ipArene = null;

		// init des arguments
		int port = Constantes.PORT_DEFAUT;

		// nombre de tours du serveur, sachant qu'un tour dure environ 1 seconde
		// si negatif, duree illimite
		long duree = Constantes.NB_TOURS_DEFAUT;
		
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
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement du serveur
		try {
			ipArene = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("lanceur", "Creation du registre RMI sur le port "+port+"...");
			java.rmi.registry.LocateRegistry.createRegistry(port);
			logger.info("lanceur", "Creation du registre RMI reussie");

			logger.info("lanceur", "Creation du serveur sur le port "+port+"...");
			new AreneTournoi(port, ipArene, duree, logger);
			logger.info("lanceur", "Creation du serveur reussie");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
