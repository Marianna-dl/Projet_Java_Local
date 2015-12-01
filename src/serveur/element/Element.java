package serveur.element;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Element implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Patronyme patronyme;

	/**
	 * vrai si l'element est toujours vivant
	 */
	protected boolean alive;
	
	/**
	 * caracteristiques de l'element (au moins HP)
	 */
	protected HashMap<Caracteristique, Integer> caract = new HashMap<Caracteristique,Integer>();
	
	public Element (String nom, String groupe, HashMap<Caracteristique, Integer> ht) {	
		patronyme = new Patronyme(nom, groupe);
		caract = ht;
		alive = true;
	}
	
	public String getNom() {
		return patronyme.getNom();
	}
	
	public String getGroupe() {
		return patronyme.getGroupe();
	}
	
	public String getNomGroupe() {
		return patronyme.getNom()+"_"+patronyme.getGroupe();
	}
	
	@Override
	public String toString() {
		return patronyme.toString();
	}

	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * Retourne la valeur associee a la caracteristique specifiee
	 * @param c caracterisique
	 * @return valeur correspondant a la caracteristique, ou null si elle n'existe pas
	 */
	public Integer getCaract(Caracteristique c) {
		return caract.get(c);
	}

	/** 
	 * Retourne toute la table des caracteristiques
	 * @return caracteristiques
	 */
	public HashMap<Caracteristique, Integer> getCaracts() {
		return caract;
	}
}
