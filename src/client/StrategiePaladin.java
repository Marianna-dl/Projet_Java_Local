package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Paladin;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategiePaladin extends StrategiePersonnage{
	protected Console console;
	public StrategiePaladin(String ipArene, int port, String ipConsole, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, int nbTours, Point position, LoggerProjet logger) {
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Paladin(caracts), nbTours, position, logger);
			logger.info("lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// TODO Auto-generated method stub
		// arene
				IArene arene = console.getArene();
				
				// reference RMI de l'element courant
				int refRMI = 0;
				int distPlusProche;
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
					
				} else { //un ou plusieurs voisins
					//int refCible = Calculs.chercherElementProche(position, voisins);
					int refCible = arene.chercherElementFaible(refRMI, voisins);
					distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
					Element elemPlusFaible = arene.elementFromRef(refCible);
						
						if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
							interagir(arene,refRMI, refCible, elemPlusFaible );
							
						} else { // si voisins, mais plus eloignes
							// je vais vers le plus proche
							console.setPhrase("Je vais vers mon voisin " + elemPlusFaible.getNom());
							arene.deplace(refRMI, refCible);
							refCible = arene.chercherElementFaible(refRMI, voisins);
							elemPlusFaible = arene.elementFromRef(refCible);
							if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) {
								interagir(arene,refRMI, refCible, elemPlusFaible );
							}
						}				
					}

				
	}

	public void interagir(IArene arene, int refRMI, int refCible, Element elemPlusFaible ) throws RemoteException{
		
		// j'interagis directement
		if(elemPlusFaible instanceof Potion) { // potion
			// ramassage
			console.setPhrase("Je ramasse une potion");
			arene.ramassePotion(refRMI, refCible);

		} else { // personnage
			// duel
			console.setPhrase("Je fais un duel avec " + elemPlusFaible.getNom());
			arene.lanceAttaque(refRMI, refCible);
		}

		}	
	
	
}
