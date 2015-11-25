package serveur;

import interfaceGraphique.view.TypeElement;
import interfaceGraphique.view.VueElement;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import serveur.element.Element;
import serveur.element.Potion;
import serveur.element.Tresor;
import utilitaires.Calculs;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 * @author cricri
 */
public class ClientElement {
	
	/**
	 * les caracteristique de l'element
	 */
	protected Element elem;
	
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
	public ClientElement(Element elem, Point position, int ref) {
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
		TypeElement type = TypeElement.OBJET;
		if (elem instanceof Potion){
			type = TypeElement.POTION;
		}
		if (elem instanceof Tresor){
			type = TypeElement.TRESOR;
		}
		
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
	
	public Color getColor(){
		return color;
	}
	
	public void setPhrase(String phrase){
		this.phrase = phrase;
	}
	
	public String getPhrase(){
		return phrase;
	}
}
