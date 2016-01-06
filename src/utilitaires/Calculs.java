package utilitaires;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import static utilitaires.Constantes.*;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;

/**
 * Classe regroupant quelques methodes utiles pour l'arene (distance, case vide,
 * elements voisins...).
 */
public class Calculs {

	/**
	 * Renvoie la distance de Chebyshev entre deux points.
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return distance de Chebyshev
	 */
	public static int distanceChebyshev(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
	}

	/**
	 * Verifie si un element parmi les voisins occupe la position donnee. 
	 * @param p position   
	 * @param voisins voisins
	 * @return vrai si la case est vide, faux sinon
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
	 * Teste si le point donne est dans l'arene.
	 * @param p point
	 * @return vrai si le point est dans les limites de l'arene, faux sinon
	 */
	public static boolean estDansArene(Point p) {
		return XMIN_ARENE <= p.x && p.x <= XMAX_ARENE &&
				YMIN_ARENE <= p.y && p.y <= YMAX_ARENE;
	}
	
	/** 
	 * Renvoie le meilleur point a occuper par l'element courant dans la 
	 * direction de la cible.
	 * @param origine point sur lequel se trouve l'element courant
	 * @param objectif point sur lequel se trouve la cible
	 * @param voisins positions des elements proches 
	 * @return meilleur point libre a une distance de 1 dans la direction de la 
	 * cible, ou null s'il n'en existe aucun
	 */
	public static Point meilleurPoint(Point origine, Point objectif, 
			HashMap<Integer, Point> voisins) {
		
		// liste contenant tous les positions vers lesquelles l'element peut avancer :
		// les 8 cases autour de lui
		ArrayList<Point> listePossibles = new ArrayList<Point>();		
		
		Point tempPoint;
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((i != 0) || (j != 0))  { // pas le point lui-meme
					tempPoint = new Point(origine.x + i, origine.y + j);
					
					if(estDansArene(tempPoint)) {
						listePossibles.add(tempPoint);
					}
				}
			}
		}
		
		
		// organise les points de la liste du plus pres vers le plus eloigne de la cible
		Collections.sort(listePossibles, new PointComp(objectif));
		
		// cherche la case vide la plus proche de la cible
		boolean trouve = false;
		int i = 0;
		Point res = null;
		
		while (!trouve & i < listePossibles.size()) {
			res = listePossibles.get(i);
			trouve = caseVide(res, voisins);
			i++;
		}

		return res;
	}

	/**
	 * Cherche l'element le plus proche vers lequel se didiger, dans la limite
	 * de la vision du personnnage.
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 */
	public static int chercheElementProche(Point origine, HashMap<Integer, Point> voisins) {
		int distPlusProche = VISION;
		int refPlusProche = 0;
		
		for(int refVoisin : voisins.keySet()) {
			Point target = voisins.get(refVoisin);
			
			if (distanceChebyshev(origine, target) <= distPlusProche) {
				distPlusProche = Calculs.distanceChebyshev(origine, target);
				refPlusProche = refVoisin;
			}
		}
		
		return refPlusProche;
	}
	
	
	
	/**
	 * Genere un entier dans un intervalle donne.
	 * @param min borne inferieure de l'intervalle
	 * @param max borne superieure de l'intervalle
	 * @return valeur aleatoire generee
	 */
	public static int nombreAleatoire(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	/**
	 * Genere un valeur aleatoire pour une caracteristique donnee, entre min et
	 * max.
	 * @param c caracteristique
	 * @return valeur aleatoire generee
	 */
	public static int valeurCaracAleatoire(Caracteristique c) {
		return nombreAleatoire(c.getMin(), c.getMax());
	}

	/**
	 * Genere un valeur aleatoire pour une caracteristique donnee, entre -max
	 * et +max (pour les potions).
	 * @param c caracteristique
	 * @return valeur aleatoire generee
	 */
	public static int valeurCaracAleatoirePosNeg(Caracteristique c) {
		return nombreAleatoire(-c.getMax(), c.getMax());
	}
	
	/**
	 * Renvoie un point aleatoire de l'arene.
	 * @return position aleatoire
	 */
	public static Point positionAleatoireArene() {
		return new Point(
				Calculs.nombreAleatoire(XMIN_ARENE, XMAX_ARENE), 
				Calculs.nombreAleatoire(YMIN_ARENE, YMAX_ARENE));
	}

	/**
	 * Cape une valeur dans un intervalle donne.
	 * @param min borne inferieure de l'intervalle
	 * @param max borne superieure de l'intervalle
	 * @param val valeur a caper
	 * @return valeur capee
	 */
	public static int restreintNombre(int min, int max, int val) {
		return Math.min(Math.max(val, min), max);
	}

	/**
	 * Cape une valeur correspondant a une caracteristique donnee.
	 * @param c caracteristique 
	 * @param val valeur
	 * @return valeur capee
	 */
	public static int restreintCarac(Caracteristique c, int val) {		
		return restreintNombre(c.getMin(), c.getMax(), val);
	}

	public static Point restreintPositionArene(Point position) {		
		return new Point(restreintNombre(XMIN_ARENE, XMAX_ARENE, position.x), 
				restreintNombre(YMIN_ARENE, YMAX_ARENE, position.y));
	}

	/**
	 * Transforme une duree en seconde en une chaine de caracteres de type 
	 * H:M:S.
	 * @param duree en secondes
	 * @return duree en chaine sous la forme H:M:S
	 */
	public static String timerToString(int duree) {	
		String res;
		
		if (duree < 0) {
			res = "illimite";
		} else {
			int heure, minute, seconde;
			
			seconde = duree % 60;
			minute = duree / 60;
			heure = minute / 60;
			minute = minute % 60;
			
			if (heure == 0) {
				res = minute + ":" + ((seconde<10) ? "0" : "") + seconde ;
			} else {
				res = heure + ":" + ((minute<10) ? "0" : "") + minute + ":" + ((seconde<10) ? "0" : "") + seconde;				
			}
		}
		
		return res;
	}
}
