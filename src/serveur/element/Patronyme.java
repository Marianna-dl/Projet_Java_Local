package serveur.element;

import java.io.Serializable;

public class Patronyme implements Serializable {
	
	private static final long serialVersionUID = 3216022584691588609L;

	private String nom;
	
	private String groupe;
	
	public Patronyme(String n, String g) {
		nom = n;
		groupe = g;
	}

	public String getNom() {
		return nom;
	}
	
	public String getGroupe() {
		return groupe;
	}
	
	@Override
	public String toString() {
		return nom+"_"+groupe;
	}
}
