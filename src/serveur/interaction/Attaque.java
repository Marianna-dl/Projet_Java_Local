package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import serveur.Arene;
import serveur.ClientPersonnage;
import serveur.element.Caracteristique;
import serveur.element.PersonnageServeur;
import utilitaires.Calculs;

public class Attaque extends EntreElement <ClientPersonnage> {
	
	public Attaque(Arene arene, ClientPersonnage attaquant, ClientPersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	public void interagir() throws RemoteException {
		PersonnageServeur pAttaquant = (PersonnageServeur) attaquant.getElement();
		PersonnageServeur pDefenseur = (PersonnageServeur) defenseur.getElement();		
		
		try {
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int forceDisponibleAttaquant = forceAttaquant;
			
			int argentDefenseur = pDefenseur.getCaract(Caracteristique.ARGENT);
			
			int perteArgent = 0;
			int perteVie = 0;
			
			// Calcule de la quantité d'argent et de vie que vas perdre l'adversaire
			// Fait perdre autant de pièce que possible avec la force disponible
			// Fait perdre la force restante en vie (10%)
			if (forceDisponibleAttaquant > argentDefenseur){
				perteArgent = argentDefenseur;
				forceDisponibleAttaquant-=perteArgent;
				perteVie = (int) (0.1 * forceDisponibleAttaquant);
			} else {
				perteArgent = forceDisponibleAttaquant;
			}

			Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), forceAttaquant);

			// Ejection du défenseur
			defenseur.setPosition(positionEjection);

			Point positionArgent = positionPerteArgent(positionEjection, attaquant.getPosition(), forceAttaquant);

			// Chute d'argent
			if (perteArgent > 0) {
				arene.faireTomberTresor(defenseur, perteArgent, positionArgent);
				logs(Level.INFO, Arene.nomRaccourciClient(attaquant) +" pousse violement "+ Arene.nomRaccourciClient(defenseur) +". "+ perteArgent +" tombé par terre.");
			}

			// Blessure
			if (perteVie > 0) {
				arene.ajouterCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				logs(Level.INFO, Arene.nomRaccourciClient(attaquant) + " colle une beigne ("
						+ perteVie +" points de dégats) à " + Arene.nomRaccourciClient(defenseur));
			}
			
			incrementerInitiative(defenseur);
			decrementerInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : "+e.toString());
		}
	}

	private void incrementerInitiative(ClientPersonnage defenseur) throws RemoteException{
		arene.ajouterCaractElement(defenseur, Caracteristique.INITIATIVE, 10);
	}
	private void decrementerInitiative(ClientPersonnage attaquant) throws RemoteException{
		arene.ajouterCaractElement(attaquant, Caracteristique.INITIATIVE, -10);
	}

	private Point positionPerteArgent(Point positionEjection, Point positionCoup, int forceAttaquant) {
		int distance = forceVersDistance(forceAttaquant) - 1;
		List<Point> listePositionPossible = new ArrayList<Point>();
		
		int xMin = positionEjection.x - distance;
		int xMax = positionEjection.x + distance;
		int yMin = positionEjection.y - distance;
		int yMax = positionEjection.y + distance;
		
		for (int x = xMin; x <= xMax; x++){
			for (int y = yMin; y <= yMax; y++){
				Point p = new Point(x,y);
				if (Calculs.distanceChebyshev(p, positionCoup) == distance && Calculs.distanceChebyshev(p, positionEjection) == distance)
					listePositionPossible.add(p);				
			}
		}
		Collections.shuffle(listePositionPossible);		
		
		return listePositionPossible.get(0);
	}

	/**
	 * Permet de trouver la position ou le personnage sera ejecté
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
		
		return Calculs.caperPositionArene(new Point(x,y));
	}
	
	/**
	 * Calcule la distance à laquelle est projeté quelqu'un suite à un coup
	 * @param force force du coup
	 * @return distance de projection
	 */
	private int forceVersDistance(int force) {
		int distance;
		int max = Caracteristique.FORCE.max;
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
