package utilitaires;
import java.awt.Point;
import java.util.Comparator;

/**
 * Definit les coordonnees de points dans l'arene et les outils (distance, comparaison)
 */
public class PointComp extends Point implements Comparator<Point> {

	private static final long serialVersionUID = 1L;

	/**
	 * Cree un point a partir de ses coordonnees.
	 * @param x l'abscisse
	 * @param y ordonnee
	 */
	public PointComp(int x, int y) {
		super(x, y);
	}

	/**
	 * Cree un point a partir d'un autre point.
	 * @param p p
	 */
	public PointComp(Point p) {
		super(p.x, p.y);
	}

	/**
	 * Calcule la distance euclidienne entre le point courant et le point cible.
	 * @param cible point cible
	 * @return distance euclidienne
	 */
	private Integer distanceEuclidienne(Point cible) {
		int xDiff = x - cible.x;
		int yDiff = y - cible.y;
		
		return (int) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	/**
	 * Compare la distance du point courant a deux autres points.
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return nombre negatif si le premier point est plus proche, 
	 * 0 si les points sont a la meme distance et 
	 * 1 si le deuxieme est plus proche
	 */
	public int compare(Point p1, Point p2) {
		return distanceEuclidienne(p1).compareTo(distanceEuclidienne(p2));
	}
}
