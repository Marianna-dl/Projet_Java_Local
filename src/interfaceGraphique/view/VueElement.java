package interfaceGraphique.view;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Hashtable;

import serveur.element.Caracteristique;

/**
 * Definit la representation d'un element par sa vue : coordonnees, reference dans l'annuaire RMI, console, message... 
 */
public class VueElement implements Serializable {
		
	private static final long serialVersionUID = 6528222745182073351L;
	
	private int refRMI;
	private Point position;
	private String nom;
	private String groupe;
	private Color color;
	private Hashtable<Caracteristique, Integer> caracts = new Hashtable<Caracteristique,Integer>();
	
	private boolean selected = false;
	private boolean enAttente = false;

	private String phrase;

	private TypeElement type;
	
	
	public VueElement(int refRMI, Point position, String nom, String groupe,
			Hashtable<Caracteristique, Integer> caracts, Color color, String phrase, TypeElement type) {
		this.refRMI = refRMI;
		this.position = position;
		this.nom = nom;
		this.groupe = groupe;
		this.color = color;
		this.caracts = caracts;
		this.phrase = phrase;
		this.type = type;
	}
	
	/**
	 * Renvoie la reference de l'element sur le serveur
	 * @return reference RMI de l'element
	 */
	public int getRefRMI() {
		return refRMI;
	}
	
	/**
	 * Renvoie le point sur lequel l'element se trouve
	 * @return position de l'element
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Renvoie le message communique par l'element
	 */
	public String getPhrase() {
		return phrase;
	}
	/**
	 * Permet de changer le message que doit communiquer l'element 
	 * @param phrase phrase que doit communiquer l'element
	 */
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}	

	/**
	 * Renvoi le nom de l'element
	 * @return nom de l'element
	 */
	public String getNom() {
		return nom;
	}
    
	/**
	 * Renvoi le groupe de l'element
	 * @return groupe de l'element
	 */
	public String getGroupe() {
		return groupe;
	}
	
	/**
	 * Renvoi la couleur de l'element
	 * @return couleur de l'element
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Renvoi la valeur de la caracteristique donne
	 * @param car caracteristique voulu
	 * @return valeur de la caracteristique
	 */
	public int getCaract(Caracteristique car) {
		if (caracts.containsKey(car))
			return caracts.get(car);
		else
			return 0;
	}
	
	/**
	 * Permet de savoir si l'element est selectionne dans l'IHM
	 * @return vrai si la vue est selectionne dans le tableau, faux sinon
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Defini si l'element est selectionne ou pas
	 * @param selected selectionne ou pas
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Permet de savoir si l'element courant est en attente
	 * @return vrai si il est en attente, faux sinon
	 */
	public boolean isEnAttente() {
		return enAttente;
	}

	public void setEnAttente(boolean enAttente) {
		this.enAttente = enAttente;
	}

	public TypeElement getType() {
		return type;
	}
	
}
