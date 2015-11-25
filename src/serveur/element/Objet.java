package serveur.element;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Un element du jeu : un personnage ou une potion, avec un nom, une liste 
 * d'autres elements qu'il connait et ses caracteristiques (au moins le nombre 
 * de vies).
 */
public abstract class Objet extends Element implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * vrai si l'element est ramasse
	 */
	protected boolean taken;
	
	/**
	 * Constructeur (le nombre de vies est par defaut initialise a 1)
	 * @param nom le nom de l'element a creer
	 */
	public Objet(String nom, String groupe, Hashtable<Caracteristique, Integer> ht) {	
		super(nom, groupe, ht);
		taken = false;
	}
}
