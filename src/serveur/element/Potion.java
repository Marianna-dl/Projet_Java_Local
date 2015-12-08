package serveur.element;

import java.util.HashMap;

/**
 * Une potion: un element donnant des bonus aux caracteristiques de celui qui
 * le ramasse.
 */
public class Potion extends Element {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Vrai si la potion a ete ramassee.
	 */
	private boolean ramassee;
	
	/**
	 * Constructeur d'une potion avec un nom, le groupe qui l'a envoyee et ses 
	 * caracteristiques (ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom nom
	 * @param groupe groupe
	 * @param ht caracteristiques
	 */
	public Potion(String nom, String groupe, HashMap<Caracteristique, Integer> ht) {
		super(nom, groupe, ht);
		ramassee = true;
	}
	
	/**
	 * Ramasser cette potion. 
	 */
	public void ramasser() {
		ramassee = false;
	}

	@Override
	public boolean estVivant() {
		return !ramassee;
	}
}
