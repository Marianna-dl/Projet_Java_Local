package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;

import client.StrategiePersonnage;
import logger.MyLogger;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Lance une Console avec un Element sur l'Arene (apres lancement Arene). A lancer plusieurs fois.
 */
public class LancePersonnage {
	
	private static String usage = "USAGE : java " + LancePersonnage.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {
		// init des variables
		String nom = "Truc";
		
		// TODO remplacer la ligne suivante par votre numero de groupe
		String groupe = "B" + Calculs.randomNumber(0,99); 
		String ipConsole = null;
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
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
			
			if (args.length == 2) {
				ipArene = args[1];
			}
		}
		
		// creation du logger
		MyLogger logger = null;
		try {
			logger = new MyLogger(true, "personnage_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement du serveur
		try {
			
			ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("lanceur", "Creation du personnage...");
			Point position = new Point(Calculs.randomNumber(0,100), Calculs.randomNumber(0,100));
			
			new StrategiePersonnage(ipArene, port, ipConsole, nom, groupe, position, logger);
			logger.info("lanceur", "Creation du personnage reussie");
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
