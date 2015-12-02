package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.MyLogger;
import modele.Element;
import modele.Personnage;
import modele.Potion;
import serveur.IArene;
import serveur.controle.ConsoleElement;
import serveur.interaction.EntreElement;
import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 */
public class StrategiePersonnage {
	
	protected ConsoleElement console;

	/**
	 * Constructeur d'un personnage avec un nom, un groupe et une position
	 * @param nom nom du personnage
	 * @param groupe groupe de l'etudiant
	 * @param position position du personnage
	 * @param port port de communication avec l'arene
	 * @param ipArene ip de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param logger gestionnaire de log
	 */
	public StrategiePersonnage(String nom, String groupe, Point position, 
			int port, String ipArene, String ipConsole, MyLogger logger) {
		
		logger.info("lanceur", "Creation de la console...");
		try {
			console = new ConsoleElement(this, new Personnage(nom, groupe), position, port, ipArene, ipConsole, logger);
			logger.info("lanceur", "Creation de la console reussie");
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n"+e.toString());
			e.printStackTrace();
		}
	}

	/** --------------------------------------------------------------------
	 *  Classe a modifier pour la strategie a adopter par votre personnage.
	 *  --------------------------------------------------------------------
	 * Met en place la strategie. On ne peut utiliser que les methodes de la 
	 * classe Arene.
	 * @param voisins element voisins de cet element
	 * @throws RemoteException
	 */
	public void strategie(HashMap<Integer, Point> voisins) throws RemoteException {
		//TODO etablir une strategie afin d'evoluer dans l'arene de combat
		
		IArene arene = console.getArene();
		
		int refRMI;
		Point position = null;
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplacer(console, 0); //errer
		} else {
			int refCible = Calculs.chercherElementProche(position, voisins);

			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.getAnElement(refCible);

			if(distPlusProche <= EntreElement.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramasserObjet(console, refCible);

				} else { // personnage
					// duel
					console.setPhrase("Je fais un duel avec " + arene.getAnElement(refCible).getNom());
					console.getArene().lancerUneAttaque(console, refCible);
				}
				
			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche
				
				console.setPhrase("Je vais vers mon voisin " + arene.getAnElement(refCible).getNom());
				arene.deplacer(console, refCible);
			}
		}
	}

	
}
