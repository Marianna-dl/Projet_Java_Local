package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.Arene;
import utilitaires.Calculs;

public class Deplacements {
	
	/**
	 * Le serveur
	 */
	private Arene arene;	

	/**
	 * Reference de l'element 
	 */
	private int ref;
	
	/**
	 * Ref RMI et les vues des voisins.
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * Position de l'element
	 */
	private Point position;
	
	
	public Deplacements(Arene arene, int ref, Point position, HashMap<Integer, Point> voisins) {
		this.arene = arene;
		this.ref = ref;
		this.position = position;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
		

	}

	/**
	 * Deplace ce sujet d'une case en direction du sujet dont la reference est donnee en parametre
	 * ref de soi-meme pour du sur-place, 0 pour errer et ref d'un voisin (s'il existe)
	 * On ne manipule que la VueElement
	 * @param refObjectif la reference de l'element cible
	 */    
	public void seDirigerVers(int refObjectif) throws RemoteException {
		Point pvers;

		// si la cible est l'element meme, il reste sur place
		if (refObjectif == ref) return;

		// la reference est nulle : le personnage erre
		if (refObjectif <= 0) { //initialisation aleatoire
			pvers = new Point(Calculs.randomNumber(Arene.XMIN, Arene.XMAX),
					Calculs.randomNumber(Arene.YMIN, Arene.YMAX));
		} else { //sinon la cible devient le point sur lequel se trouve l'element refObjectif
			pvers = voisins.get(refObjectif);
		}

		// si l'element n'existe plus (cas posible: deconnexion du serveur), le point reste sur place
		if (pvers == null) return;

		seDirigerVers(pvers);
	}

	/**
	 * Deplace ce sujet d'une case en direction de la case specifiee.
	 * On ne manipule que la VueElement
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seDirigerVers(Point objectif) throws RemoteException {
		Point cible = new Point(Calculs.caperNumber(Arene.XMIN, Arene.XMAX, objectif.x),
				Calculs.caperNumber(Arene.YMIN, Arene.YMAX, objectif.y));
		
		Point dest = position;
		// on fait un deplacement d'une case le nombre de fois qu'on a de vitesse
		// Sauf si on est arrive a destination
//		for (int i = 0; i < vitesse && !dest.equals(cible); i++) {
			dest = calculProchaineCase(dest, cible);
//		}

		// si le point destination est libre
		if (Calculs.caseVide(dest, voisins)) {
			// l'element courant se deplace
			arene.setPosition(ref, dest);
		} else {
			// cherche la case libre la plus proche dans la direction de la cible
			Point top = Calculs.meilleurPoint(position, dest, voisins);
			// deplace l'element courant sur celle-la
			arene.setPosition(ref, top);
		}
	}

	/**
	 * Calcule le point autour de dep pour aller vers arr
	 * @param dep point de depart
	 * @param arr point d'arrivee
	 * @return point de destination
	 */
	private Point calculProchaineCase(Point dep, Point arr) {
		// calcule la direction pour atteindre arr (+1/-1 par rapport a dep)
		int x = ((arr.x-dep.x)>0)?+1:-1;
		int y = ((arr.y-dep.y)>0)?+1:-1;

		// instancie le point destination
		Point dest = new Point(dep.x+x,dep.y+y);
		return dest;
	}
}
