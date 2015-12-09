package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class Attaque extends EntreElement <VuePersonnage> {
	
	public Attaque(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	public void interagir() throws RemoteException {
		Personnage pAttaquant = (Personnage) attaquant.getElement();
		
		try {
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie = forceAttaquant;
		

			Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), forceAttaquant);

			// Ejection du defenseur
			defenseur.setPosition(positionEjection);

			// Blessure
			if (perteVie > 0) {
				arene.ajouterCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " colle une beigne ("
						+ perteVie +" points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			
			incrementerInitiative(defenseur);
			decrementerInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : "+e.toString());
		}
	}

	private void incrementerInitiative(VuePersonnage defenseur) throws RemoteException{
		arene.ajouterCaractElement(defenseur, Caracteristique.INITIATIVE, 10);
	}
	private void decrementerInitiative(VuePersonnage attaquant) throws RemoteException{
		arene.ajouterCaractElement(attaquant, Caracteristique.INITIATIVE, -10);
	}

	
	/**
	 * Permet de trouver la position ou le personnage sera ejecte
	 * @param positionOrigine position d'origine du tresor
	 * @param positionCoup position du coup
	 * @param distance distance de projection
	 * @return position d'ejection du personnage
	 */
	private Point positionEjection(Point positionOrigine, Point positionCoup, int force) {		
		int distance = forceVersDistance(force);
		int dirX = positionOrigine.x-positionCoup.x;
		if (dirX > 0)
			dirX = distance;
		if (dirX < 0)
			dirX = -distance;
		
		int dirY = positionOrigine.y-positionCoup.y;
		if (dirY > 0)
			dirY = distance;
		if (dirY < 0)
			dirY = -distance;
		
		int x = positionOrigine.x + dirX;
		int y = positionOrigine.y + dirY;
		
		return Calculs.restreindrePositionArene(new Point(x,y));
	}
	
	/**
	 * Calcule la distance a laquelle est projete quelqu'un suite a un coup
	 * @param force force du coup
	 * @return distance de projection
	 */
	private int forceVersDistance(int force) {
		int distance;
		int max = Caracteristique.FORCE.getMax();
		if (force < max / 4)
			distance = 4;
		else if (force < max / 2)
			distance = 5;
		else if (force < (3*max) /4)
			distance = 6;
		else
			distance = 7;
		return distance;
	}
}
