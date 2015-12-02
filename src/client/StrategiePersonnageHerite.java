package client;

import java.awt.Point;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.MyLogger;
import modele.Element;
import modele.Personnage;
import serveur.IArene;
import serveur.interaction.EntreElement;
import utilitaires.Calculs;

public class StrategiePersonnageHerite extends StrategiePersonnage {

	public StrategiePersonnageHerite(String nom, String groupe, Point position, int port,
			String ipArene, String ipConsole, MyLogger logger)
			throws IOException {
		super(nom, groupe, position, port, ipArene, ipConsole, logger);
	}

	/**
	 * --------------------------------------------------------------------
	 * public void strategie(VueElement ve, Hashtable<Integer,VueElement>
	 * voisins, Integer refRMI) throws RemoteException
	 * --------------------------------------------------------------------
	 * Classe a modifier pour la strategie a adopter par votre personnage.
	 * -------------------------------------------------------------------- Met
	 * en place la strategie. On ne peut utiliser que les methodes de la classe
	 * Arene.
	 * 
	 * @param voisins
	 *            element voisins de cet element
	 * @throws RemoteException
	 */
	@Override
	public void strategie(HashMap<Integer, Point> voisins)
			throws RemoteException {

		IArene arene = console.getArene();

		int refRMI;
		Point position = null;
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (0 == voisins.size()) {
			// je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplacer(console, 0); // errer
		} else {
			int refElemPlusProche = Calculs.chercherElementProche(position,
					voisins);
			Element elemPlusProche = arene.getAnElement(refElemPlusProche);
			int distPlusProche = Calculs.distanceChebyshev(position,
					arene.getPosition(refElemPlusProche));

			// Si mon voisin le plus proche n'est pas un personnage
			if (!(elemPlusProche instanceof Personnage)) {
				if (distPlusProche <= EntreElement.DISTANCE_MIN_INTERACTION) {
					console.setPhrase("Je ramasse une potion");
					arene.ramasserObjet(console, refElemPlusProche);
				} else {
					console.setPhrase("Je vais vers la potion "
							+ arene.getAnElement(refElemPlusProche).getNom());
					arene.deplacer(console, refElemPlusProche);
				}
			} else {
				if (distPlusProche <= EntreElement.DISTANCE_MIN_INTERACTION) {
					console.setPhrase("J'attaque "
							+ arene.getAnElement(refElemPlusProche).getNom());
					arene.lancerUneAttaque(console, refElemPlusProche);
				} else {
					console.setPhrase("Je vais vers  "
							+ arene.getAnElement(refElemPlusProche).getNom());
					arene.deplacer(console, refElemPlusProche);
				}

			}
		}
	}
}
