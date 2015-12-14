package serveur.vuelement;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Random;

import serveur.element.Element;
import utilitaires.Calculs;

/**
 * Donnees dont le serveur a besoin sur un element : l'element lui-meme, sa 
 * position dans l'arene, sa reference...
 * Ces vues ne devraient pas etre utilisees dans le client pour le personnage, 
 * mais seulement dans le serveur et dans l'IHM. 
 * 
 * @param <T> type de l'element
 */
public class VueElement<T extends Element> implements Serializable {
	
	private static final long serialVersionUID = 1750601856220885598L;

	/**
	 * Reference RMI.
	 */
	protected final int refRMI;

	/**
	 * Element.
	 */
	protected T element;
	
	/**
	 * Position dans l'arene.
	 */
	protected Point position;
	
	/**
	 * Couleur de l'element.
	 */
	protected Color couleur;
	
	/**
	 * Phrase dite par l'element.
	 */
	protected String phrase;
	
	/**
	 * Vrai si l'element est selectionne sur l'IHM.
	 */
	protected boolean selectionne = false;
	
	/**
	 * Cree un element pour le serveur.
	 * @param element element correspondant
	 * @param position position courante
	 * @param ref reference
	 */
	public VueElement(T element, Point position, int ref) {
		this.element = element;
		this.position = position;
		this.refRMI = ref;
		
		Random r = new Random(ref);
		couleur = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), 200);
		
		phrase = "";
	}

	public int getRefRMI() {
		return refRMI;
	}

	public T getElement() {
		return element;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = Calculs.restreintPositionArene(position);
	}
	
	public Color getCouleur() {
		return couleur;
	}
	
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	
	public boolean isSelectionne() {
		return selectionne;
	}

	public void setSelectionne(boolean selectionne) {
		this.selectionne = selectionne;
	}
}
