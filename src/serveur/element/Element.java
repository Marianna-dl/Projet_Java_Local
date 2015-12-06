package serveur.element;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Un element du jeu (potion ou personnage). 
 *
 */
public abstract class Element implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Nom de l'element.
	 */
	protected String nom;
	
	/**
	 * Groupe a l'origine de l'element. 
	 */
	protected String groupe;

	/**
	 * Caracteristiques de l'element (au moins sa vie).
	 */
	protected HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique,Integer>();
	
	/**
	 * Cree un element avec son nom, son groupe et ses caracteristiques. 
	 * @param nom nom
	 * @param groupe groupe
	 * @param caracts caracteristiques
	 */
	public Element(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {	
		this.nom = nom;
		this.groupe = groupe;
		this.caracts = caracts;
	}
	
	/**
	 * Retourne la valeur associee a la caracteristique specifiee.
	 * @param c caracterisique
	 * @return valeur correspondant a la caracteristique, ou null si elle 
	 * n'existe pas
	 */
	public Integer getCaract(Caracteristique c) {
		return caracts.get(c);
	}

	/**
	 * Renvoie vrai l'element est present sur l'arene :
	 * pour un personnage, sa vie doit etre superieure a 0, 
	 * pour une potion, elle ne doit pas etre ramassee. 
	 * @return vrai si le personnage doit interagir dans l'arene
	 */
	public abstract boolean estActif();

	public String getNom() {
		return nom;
	}
	
	public String getGroupe() {
		return groupe;
	}
	
	public String getNomGroupe() {
		return nom + "_" + groupe;
	}
	
	public HashMap<Caracteristique, Integer> getCaracts() {
		return caracts;
	}

	@Override
	public String toString() {
		return getNomGroupe() + " " + caracts;
	}
}
