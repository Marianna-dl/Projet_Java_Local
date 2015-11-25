package lanceur;

import java.io.IOException;

import serveur.IArene;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

public class LanceTresor {
	
	private static String USAGE = "USAGE : java " + LanceTresor.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {
		// init des variables
		String nom = "Trésor";
		String groupe = "B"+Calculs.randomNumber(0,99); // REMPLACER CETTE LIGNE PAR VOTRE NUMERO DE GROUPE
					// vous ne pourrez pas participer au tournoi si ce n'est pas fait
		
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
			logger = new MyLogger(true, "tresor_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup("rmi://"+ipArene+":"+port+"/Arene");

			logger.info("lanceur", "Lancement du tresor sur le serveur...");
			arene.ajouterTresor(nom,groupe,1000);
			logger.info("lanceur", "Lancement du trésor réussi");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
