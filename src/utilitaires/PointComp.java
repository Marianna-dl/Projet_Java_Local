package utilitaires;
import java.awt.Point;
import java.util.Comparator;

/**
 * Definit les coordonnees de points dans l'arene et les outils (distance, comparaison)
 */
public class PointComp extends Point implements Comparator<Point> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructeur
	 * @param x la valeur de l'ordonnee
	 * @param y la valeur de l'abscisse
	 */
	public PointComp(int x, int y) {
		super(x, y);
	}

	/**
	 * Constructeur
	 * @param objectif le point
	 */
	public PointComp(Point objectif) {
		super(objectif.x,objectif.y);
	}

	/**
	 * Calcule la distance classique entre le point courant et le point cible
	 * @param paux le point cible
	 * @return un entier representant la distance
	 */
	private Integer distance(Point paux) {
		return (int) Math.sqrt(Math.pow(x-paux.x, 2)+Math.pow(y-paux.y,2));
	}
	
	/**
	 * Compare la distance du point courant a deux autres points
	 * @param o1 le premier point
	 * @param o2 le deuxieme point
	 * @return <0 si le premier point est plus proche, 0 si les points sont a la meme distance et 1 si le deuxieme est plus proche
	 */
	public int compare(Point o1, Point o2) {
		return distance(o1).compareTo(distance(o2));
	}
}
