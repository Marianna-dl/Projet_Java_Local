/**
 * 
 */
package client.personnage;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.Hashtable;

import client.controle.Console;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.PersonnageServeur;
import serveur.element.Potion;
import serveur.interaction.EntreElement;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 */
public class Personnage {
	
	protected Console console;

	/**
	 * Constructeur d'un personnage avec un nom, un groupe et une position
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom nom du personnage
	 * @param groupe groupe de l'etudiant
	 * @param position position du personnage
	 * @param port port de communication avec l'arene
	 * @param ipArene ip de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param logger gestionnaire de log
	 */
	public Personnage(String nom, String groupe, Point position, 
			int port, String ipArene, String ipConsole, MyLogger logger) {
		
		logger.info("lanceur", "Creation de la console...");
		try {
			console = new Console(this, new PersonnageServeur(nom, groupe), position, port, ipArene, ipConsole, logger);
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
	public void strategie(Hashtable<Integer, Point> voisins) throws RemoteException {
		//TODO etablir une strategie afin d'evoluer dans l'arene de combat
		
		IArene arene = console.getArene();
		
		PersonnageServeur pers = null;
		int refRMI;
		Point position = null;
		try {
			pers = console.getPersonnageServeur();
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		int leader = pers.getLeader();
		
		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplacer(console, 0); //errer
		} else {
			int refCible = Calculs.chercherElementProche(position, voisins);

			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.getAnElement(refCible);

			// dans la meme equipe ?
			boolean memeEquipe = false;

			if(elemPlusProche instanceof PersonnageServeur) {
				PersonnageServeur persPlusProche = (PersonnageServeur) elemPlusProche;
				memeEquipe = (leader != -1 && leader == persPlusProche.getLeader()) || // meme leader
						leader == refCible || // cible est le leader de this
						persPlusProche.getLeader() == console.getRefRMI(); // this est le leader de cible
			}

			if(distPlusProche <= EntreElement.distanceMinInteraction) { // si suffisamment proches
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramasserObjet(console, refCible);

				} else { // personnage
					if(!memeEquipe) { // duel seulement si pas dans la meme equipe (pas de coup d'etat possible dans ce cas)
						// duel
						console.setPhrase("Je fais un duel avec " + arene.getAnElement(refCible).getNom());
						console.getArene().lancerUneAttaque(console, refCible);
					} else {
						console.setPhrase("J'erre...");
						arene.deplacer(console, 0); //errer
					}
				}
			} else { // si voisins, mais plus eloignes
				if(!memeEquipe) { // potion ou enemmi 
					// je vais vers le plus proche
					
					console.setPhrase("Je vais vers mon voisin " + arene.getAnElement(refCible).getNom());
					arene.deplacer(console, refCible);

				} else {
					console.setPhrase("J'erre...");
					arene.deplacer(console, 0); //errer
				}
			}
		}
	}

	public boolean extorsion(int extorsion, Element element) {
		//TODO etablir une strategie qui determine l'accord ou le refus de rentrer dans une equipe
		return (element.getCaract(Caracteristique.ARGENT) > 300 && extorsion > 50);
	}
}
