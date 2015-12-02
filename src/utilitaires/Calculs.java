package utilitaires;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import modele.Caracteristique;

/**
 * Classe regroupant quelques methodes utiles pour l'arene (distance, case vide,
 * elements voisins...).
 */
public class Calculs {

	private static long token; // TODO supprimer

	/**
	 * Renvoie la distance Chebyshev entre deux points
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return un entier representant la distance
	 */
	public static int distanceChebyshev(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
	}

	/**
	 * Verifie si un element parmi les voisins occupe la position donnee. 
	 * @param p une position   
	 * @param voisins des elements (Point)
	 * @return true si la case est vide et false si la case est occupe
	 */
	public static boolean caseVide(Point p, HashMap<Integer, Point> voisins) {
		boolean trouve = false;
		Point pAux = null;
		Iterator<Point> it = voisins.values().iterator();
		
		while (!trouve && it.hasNext()) {
			pAux = it.next();
			trouve = p.equals(pAux); 
		}
		
		return !trouve;
	}
	
	/** 
	 * Renvoie le meilleur point a occuper par l'element courant dans la direction de la cible
	 * @param depart le point sur lequel se trouve l'element courant
	 * @param objectif le point sur lequel se trouve la cible
	 * @param voisins le positionement des autres elements dans l'arene
	 * @return le meilleur point libre dans la direction de la cible
	 */
	public static Point meilleurPoint(Point depart, Point objectif, HashMap<Integer, Point> voisins) {
		//liste contenant tous les positions vers lesquelles l'element peut avancer
		ArrayList<Point> listePossibles = new ArrayList<Point>();		
		//pour chaque de 8 cases autour de lui
		for (int i=-1;i<=1;i++) {
			for (int j=-1;j<=1;j++) {
				if ((i!=0) || (j!=0))  {
 					//on ajoute la position (en valeur absolue pour eviter de sortir du cadre)
					listePossibles.add(new Point(Math.abs(depart.x+i),Math.abs(depart.y+j)));
				}
			}
		}
		//organise les points de la liste du plus pres vers le plus eloigne de la cible
		Collections.sort(listePossibles,new PointComp(objectif));		
		//cherche la case vide la plus proche de la cible
		boolean ok = false;
		int i=0;
		Point res=null;
		while (!ok & i<listePossibles.size()) {
			res = listePossibles.get(i);
			ok = caseVide(res, voisins);
			i++;
		}
		//renvoie cette case
		return res;
	}

	/**
	 * Cherche l'element le plus proche vers lequel se didiger
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 */
	public static int chercherElementProche(Point origine, HashMap<Integer, Point> voisins) {
		int distPlusProche = 100;
		int refPlusProche = 0;
		for(Integer refVoisin : voisins.keySet()) {
			Point target = voisins.get(refVoisin);
			if (Calculs.distanceChebyshev(origine, target)<distPlusProche) {
				distPlusProche = Calculs.distanceChebyshev(origine, target);
				refPlusProche = refVoisin;
			}
		}		
		return refPlusProche;
	}
	
	/**
	 * Genere un entier correpondant a une caracteristique
	 * @param c caracteristique pour laquelle on souhaite une valeur
	 * @return valeur generee
	 */
	public static int randomCarac(Caracteristique c) {
		return randomNumber(c.getMin(), c.getMax());
	}
	
	/**
	 * Genere un entier dans un interval
	 * @param min borne inferieure de l'interval
	 * @param max borne superieure de l'interval
	 * @return valeur generee
	 */
	public static int randomNumber(int min, int max) {
		Random r = new Random(System.currentTimeMillis() + token);
		if (max < 0) {
			return r.nextInt(500-min)+min;
		}
		int res = r.nextInt(max-min)+min;
		token = res;
		return res;
	}
	
	/**
	 * Cape une valeur correspondant a une caracteristique
	 * @param c caracteristique pour laquelle on souhaite caper une valeur
	 * @param val valeur a caper
	 * @return valeur capee
	 */
	public static int caperCarac(Caracteristique c, int val) {		
		return caperNumber(c.getMin(), c.getMax(), val);
	}

	/**
	 * Cape une valeur dans un intervalle
	 * @param min borne inferieure de l'intervalle
	 * @param max borne superieure de l'intervalle
	 * @param val valeur a caper
	 * @return valeur capee
	 */
	public static int caperNumber(int min, int max, int val) {
		if (max < 0) {
			return Math.max(val, min);
		}		
		return Math.min(Math.max(val, min), max);
	}
	
	public static Point caperPositionArene(Point position) {
		int xMin = Constantes.XMIN_ARENE;
		int xMax = Constantes.XMAX_ARENE;
		int yMin = Constantes.YMIN_ARENE;
		int yMax = Constantes.YMAX_ARENE;
		
		return new Point(caperNumber(xMin, xMax, position.x), caperNumber(yMin, yMax, position.y));
	}

	public static Point randomPosition() {
		return new Point(
				Calculs.randomNumber(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE), 
				Calculs.randomNumber(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE));
	}

	/**
	 * Transforme une duree en Chaine de caractere de type H:M:S
	 * @param duree
	 * @return la duree sous forme de chaine H:M:S
	 */
	public static String timerToString(int duree) {
		if (duree < 0) {
			return "illimite";
		}
		
		int heure, minute, seconde;
		seconde = duree % 60;
		minute = duree / 60;
		heure = minute / 60;
		minute = minute % 60;
		
		String res;
		if (heure == 0) {
			res = minute + ":" + ((seconde<10) ? "0" : "") + seconde ;
		} else {
			res = heure + ":" + ((minute<10) ? "0" : "") + minute + ":" + ((seconde<10) ? "0" : "") + seconde;				
		}
		return res;
	}
}
