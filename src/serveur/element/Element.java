package serveur.element;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Element du jeu : un objet ou un personnage. 
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
	 * Vrai si l'element est toujours vivant (present sur l'arene).
	 */
	protected boolean alive;
	
	/**
	 * Caracteristiques de l'element (au moins vie).
	 */
	protected HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique,Integer>();
	
	/**
	 * Cree un element avec son nom, son groupe et ses caracteristiques. 
	 * @param nom nom
	 * @param groupe groupe
	 * @param caracts caracteristiques
	 */
	public Element (String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {	
		this.nom = nom;
		this.groupe = groupe;
		this.caracts = caracts;
		alive = true;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getGroupe() {
		return groupe;
	}
	
	public String getNomGroupe() {
		return nom + "_" + groupe;
	}
	
	public boolean isAlive() {
		return alive;
	}

	@Override
	public String toString() {
		return getNomGroupe();
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
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
	 * Retourne toute la table des caracteristiques.
	 * @return caracteristiques
	 */
	public HashMap<Caracteristique, Integer> getCaracts() {
		return caracts;
	}
}
