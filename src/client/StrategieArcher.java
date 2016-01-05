		package client;

	import java.awt.Point;
	import java.rmi.RemoteException;
	import java.util.HashMap;

	import logger.LoggerProjet;
	import serveur.IArene;
	import serveur.element.Caracteristique;
	import serveur.element.Element;
	import serveur.element.Potion;
	import utilitaires.Calculs;
	import utilitaires.Constantes;

	public class StrategieArcher extends StrategiePersonnage{

		public StrategieArcher(String ipArene, int port, String ipConsole, String nom, String groupe,
				HashMap<Caracteristique, Integer> caracts, long nbTours, Point position, LoggerProjet logger) {
			super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
			// TODO Auto-generated constructor stub
		}
		
		public void strategie(HashMap<Integer, Point> voisins) throws RemoteException {
			// arene
			IArene arene = console.getArene();
			
			// reference RMI de l'element courant
			int refRMI = 0;
			
			// position de l'element courant
			Point position = null;
			
			try {
				refRMI = console.getRefRMI();
				position = arene.getPosition(refRMI);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
				console.setPhrase("J'erre...");
				arene.deplace(refRMI, 0); 
				
			} else {
				int refCible = Calculs.chercherElementProche(position, voisins);
				int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

				Element elemPlusProche = arene.elementFromRef(refCible);
				if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION)
				 { // si suffisamment proches
					// j'interagis directement
					if(elemPlusProche instanceof Potion) { // potion
						// ramassage
						console.setPhrase("Je ramasse une potion");
						arene.ramassePotion(refRMI, refCible);

					} else if(distPlusProche <= 10){ // personnage
						// duel
						console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
						arene.lanceAttaqueArcher(refRMI, refCible);
					}
					
				} else { // si voisins, mais plus eloignes
					// je vais vers le plus proche
					console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
			}
		}

	}

	
