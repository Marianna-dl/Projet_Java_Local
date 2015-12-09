/**
 * 
 */
package serveur.element;

import java.util.HashMap;

import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 * 
 */
public class Personnage extends Element {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Cree un personnage avec un nom et un groupe.
	 * @param nom du personnage
	 * @param groupe d'etudiants du personnage
	 * @param caracts caracteristiques du personnage
	 */
	public Personnage(String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts);
	}
	
	/**
	 * Incremente la caracteristique donnee de la valeur donnee.
	 * Si la caracteristique n'existe pas, elle sera cree avec la valeur 
	 * donnee.
	 * @param c caracteristique
	 * @param inc increment (peut etre positif ou negatif)
	 * @return vrai si le personnage est toujours vivant apres l'ajout
	 * de l'increment
	 */
	public boolean incrementeCaract(Caracteristique c, int inc) {		
		if(caracts.containsKey(c)) {
			caracts.put(c, Calculs.restreindreCarac(c, caracts.get(c) + inc));
		} else {
			caracts.put(c, Calculs.restreindreCarac(c, inc));
		}
		
		return estVivant();
	}

	@Override
	public boolean estVivant() {
		Integer vie = caracts.get(Caracteristique.VIE);
		return vie != null && vie > 0;
	}
}
