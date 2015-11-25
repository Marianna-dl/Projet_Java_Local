package serveur.element;

import java.io.Serializable;
import java.util.Hashtable;

import serveur.element.Patronyme;

public abstract class Element implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Patronyme patronyme;

	/**
	 * est ce que l'element est toujours vivant ??
	 */
	protected boolean alive;
	
	/**
	 * Caracteristiques de l'element (au moins HP).
	 */
	protected Hashtable<Caracteristique, Integer> caract = new Hashtable<Caracteristique,Integer>();
	
	public Element (String nom, String groupe, Hashtable<Caracteristique, Integer> ht) {	
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
	 * Retourne la valeur associee a la caracteristique specifiee.
	 * @param c caracterisique
	 * @return valeur correspondant a la caracteristique, ou null si elle n'existe pas
	 */
	public Integer getCaract(Caracteristique c) {
		return caract.get(c);
	}

	/** 
	 * Retourne toute la table des caracteristiques.
	 * @return the caract
	 */
	public Hashtable<Caracteristique, Integer> getCaracts() {
		return caract;
	}
}
