package lanceur;

	import java.io.IOException;
	import java.util.HashMap;

	import logger.LoggerProjet;
	import serveur.IArene;
	import serveur.element.Caracteristique;
	import serveur.element.Potion;
	import utilitaires.Calculs;
	import utilitaires.Constantes;

	public class LancerPotionSurprise {
		
		private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ ipArene ] ]";

		public static void main(String[] args) {
			String nom = "Potion surprise";
			
			// TODO remplacer la ligne suivante par votre numero de groupe
			String groupe = "G24"; 
			
			// init des arguments
			int port = Constantes.PORT_DEFAUT;
			String ipArene = Constantes.IP_DEFAUT;
			
			if (args.length > 0) {
				if (args[0].equals("--help") || args[0].equals("-h")) {
					ErreurLancement.help(usage);
				}
				
				if (args.length > 2) {
					ErreurLancement.TROP_ARGS.erreur(usage);
				}
				
				try {
					port = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					ErreurLancement.PORT_NAN.erreur(usage);
				}
				
				if (args.length > 1) {
					ipArene = args[1];
				}
			}
			
			// creation du logger
			LoggerProjet logger = null;
			try {
				logger = new LoggerProjet(true, "potion_"+nom+groupe);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(ErreurLancement.suivant);
			}
			
			// lancement de la potion
			try {
				IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));

				logger.info("lanceur", "Lancement de la potion sur le serveur...");
				
				// caracteristiques de la potion
				HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();

				caractsPotion.put(Caracteristique.VIE, Calculs.nombreAleatoire(-30, 30));
				caractsPotion.put(Caracteristique.FORCE, 0);
				caractsPotion.put(Caracteristique.INITIATIVE, 0);
				
				// ajout de la potion
				arene.ajoutePotion(new Potion(nom, groupe, caractsPotion));
				logger.info("lanceur", "Lancement de la potion reussi");
				
			} catch (Exception e) {
				logger.severe("lanceur", "Erreur lancement :\n" + e.getCause());
				e.printStackTrace();
				System.exit(ErreurLancement.suivant);
			}
		}
	}


