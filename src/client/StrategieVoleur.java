package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Potion;
import serveur.element.Voleur;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Strategie d'un personnage. 
 */
public class StrategieVoleur extends StrategiePersonnage{
	protected Console console;
	
	ArrayList<Integer> potionsEmpoisonnees = new ArrayList<Integer>(); 
	public StrategieVoleur(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger) {
		
		super(ipArene, port,ipConsole,  nom, groupe, caracts, nbTours, position, logger);
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Voleur(caracts), nbTours, position, logger);
			logger.info("lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	// TODO etablir une strategie afin d'evoluer dans l'arene de combat
	// une proposition de strategie (simple) est donnee ci-dessous
	/** 
	 * Decrit la strategie.
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
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
			arene.teleportation(refRMI); 
			
		} else {
			int refCible = Calculs.chercherElementProche(position, voisins);
			boolean verifie = potionsEmpoisonnees.contains(refCible);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);

			if(!verifie){
				console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
				arene.deplace(refRMI, refCible);
				refCible = Calculs.chercherElementProche(position, voisins);
				elemPlusProche = arene.elementFromRef(refCible);
				distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
				if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) {
					interagir(arene,refRMI, refCible, elemPlusProche );
				}
					
			}
			else{
				console.setPhrase("J'erre...");
				arene.teleportation(refRMI); 
			}
		}
	}
	
public void interagir(IArene arene, int refRMI, int refCible, Element elemPlusProche) throws RemoteException{
		
		// j'interagis directement
		if(elemPlusProche instanceof Potion) { // potion
			// ramassage
			if(arene.verifierPotion(refCible)){
				console.setPhrase("Je ramasse une potion");
				arene.ramassePotion(refRMI, refCible);
			}
			else{
				console.setPhrase("C'est du poison !");
				potionsEmpoisonnees.add(refCible);
			}


		} else { // personnage
			// duel
			console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
			arene.lanceAttaque(refRMI, refCible);
		}
	}
	
}
