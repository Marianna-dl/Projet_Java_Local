package lanceur;

import java.io.IOException;

import serveur.IArene;
import serveur.element.Caracteristique;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

public class LancePotion {
	
	private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ nom_arene ] ]";

	public static void main(String[] args) {
		// init des variables
		String nom = "Anduril";
		String groupe = "B" + Calculs.randomNumber(0,99); // REMPLACER CETTE LIGNE PAR VOTRE NUMERO DE GROUPE
					// vous ne pourrez pas participer au tournoi si ce n'est pas fait
		
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
			logger = new MyLogger(true, "potion_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup("rmi://"+ipArene+":"+port+"/Arene");

			logger.info("lanceur", "Lancement de la potion sur le serveur...");
			arene.ajouterPotion(
					nom, groupe,
					Caracteristique.caracteristiquesPotion(
							Calculs.randomNumber(-100, 100),
							Calculs.randomNumber(-100, 100),
							Calculs.randomNumber(-100, 100)));
			logger.info("lanceur", "Lancement de la potion reussi");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(Erreur.suivant);
		}
	}
}
