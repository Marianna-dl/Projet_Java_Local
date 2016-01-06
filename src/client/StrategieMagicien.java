package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Magicien;
import serveur.element.Paladin;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieMagicien extends StrategiePersonnage{

	public StrategieMagicien(String ipArene, int port, String ipConsole, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, int nbTours, Point position, LoggerProjet logger) {
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
		// TODO Auto-generated constructor stub
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Magicien(caracts), nbTours, position, logger);
			logger.info("lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// TODO Auto-generated method stub
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
					int refCible = Calculs.chercheElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= 5) { // si suffisamment proches
						interagir(arene,refRMI, refCible, elemPlusProche, distPlusProche );
						if(elemPlusProche instanceof Personnage)
							arene.teleportation(refRMI);
						
					} else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
						arene.deplace(refRMI, refCible);
						refCible = Calculs.chercheElementProche(position, voisins);
						elemPlusProche = arene.elementFromRef(refCible);
						if(distPlusProche <= 5) {
							interagir(arene,refRMI, refCible, elemPlusProche,distPlusProche );
						}
					}
				}
	}
	
public void interagir(IArene arene, int refRMI, int refCible, Element elemPlusProche, int distPlusProche ) throws RemoteException{
	
	// j'interagis directement
	if(elemPlusProche instanceof Potion) { // potion
		// ramassage
		if(distPlusProche<=Constantes.DISTANCE_MIN_INTERACTION){
			console.setPhrase("Je ramasse une potion");
			arene.ramassePotion(refRMI, refCible);
		}else{
			console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
			arene.deplace(refRMI, refCible);
		}

	} else { // personnage
		// duel
		console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
		arene.lanceAttaqueMagicien(refRMI, refCible);
	}

	}
}
