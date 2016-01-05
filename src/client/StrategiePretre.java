package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.element.Pretre;
import utilitaires.Calculs;
import utilitaires.Constantes;


public class StrategiePretre extends StrategiePersonnage{
	protected Console console;
	ArrayList<Integer> personnesSoignees = new ArrayList<Integer>(); 
	
	public StrategiePretre(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger) {
			super(ipArene, port,ipConsole,  nom, groupe, caracts, nbTours, position, logger);
			try {
				console = new Console(ipArene, port, ipConsole, this, 
						new Pretre(caracts), nbTours, position, logger);
				logger.info("lanceur", "Creation de la console reussie");
				
			} catch (Exception e) {
				logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
				e.printStackTrace();
			}
	
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
			boolean verifie = personnesSoignees.contains(refCible);
		
			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				interagir(arene,refRMI, refCible, elemPlusProche );
			
			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche
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
					console.setPhrase("Je fuis !!");
					arene.fuir(refRMI);
				}
			}
		}
	}
	
public void interagir(IArene arene, int refRMI, int refCible, Element elemPlusProche) throws RemoteException{
	boolean verifie = personnesSoignees.contains(refCible);
	// j'interagis directement
	if(elemPlusProche instanceof Potion) { // potion
		// ramassage
		console.setPhrase("Je ramasse une potion");
		arene.ramassePotion(refRMI, refCible);

	} else { // personnage
		// duel
		if(verifie){
			arene.fuir(refRMI);
		}
		else{
			console.setPhrase("Je vais aider " + elemPlusProche.getNom());
			arene.lanceSoin(refRMI, refCible);
			personnesSoignees.add(refCible);
		}
	}
	
	
}
	

}
