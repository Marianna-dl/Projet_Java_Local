package serveur.infosclient;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import interfaceGraphique.view.TypeElement;
import interfaceGraphique.view.VueElement;
import modele.Element;
import utilitaires.Calculs;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 * @author cricri
 */
// TODO remove and use VueElement? Problems with serializable?
public class ClientElement<T extends Element> {
	
	/**
	 * les caracteristique de l'element
	 */
	protected T elem;
	
	/**
	 * Position
	 */
	protected Point position;
	
	/**
	 * La reference
	 */
	protected int ref;
	
	/**
	 * Couleur de l'element
	 */
	protected Color color;
	
	/**
	 * Phrase de l'element
	 */
	private String phrase = "";
	
	/**
	 * Constructeur de CLientElement
	 * @param elem element correspondant au client
	 * @param position position du client
	 * @param ref reference du client
	 */
	public ClientElement(T elem, Point position, int ref) {
		this.elem = elem;
		this.position = position;
		this.ref = ref;
		Random r = new Random(ref);
		color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), 200);
	}

	/* *******************
	 * Accesseurs
	 * *******************/

	public Element getElement() {
		return elem;
	}

	public VueElement getVue() {
		TypeElement type = TypeElement.POTION;
		
		VueElement ve = new VueElement(
				ref, getPosition(), elem.getNom(), 
				elem.getGroupe(), elem.getCaracts(), color, phrase, type);
		return ve;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = Calculs.caperPositionArene(position);
	}
	
	public int getRef() {
		return ref;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	
	public String getPhrase() {
		return phrase;
	}
}
