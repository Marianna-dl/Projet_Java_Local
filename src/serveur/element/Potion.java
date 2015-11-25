package serveur.element;

import java.util.Hashtable;

/**
 * Une potion: un element donnant des bonus aux caracteristiques de celui qui
 * le ramasse.
 */
public class Potion extends Objet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur d'une potion avec un nom et des caracteristiques
	 * (qui sont celles ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom
	 * @param groupe
	 * @param ht
	 */
	public Potion(String nom, String groupe, Hashtable<Caracteristique, Integer> ht) {
		super(nom,groupe,ht);
	}
	
	
	@Override
	public String toString(){
		return super.toString() + "[" + Caracteristique.hachageToString(caract) + "]";
	}
}
