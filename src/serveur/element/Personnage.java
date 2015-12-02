/**
 * 
 */
package serveur.element;

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
	 * @param groupe groupe du personnage
	 */
	public Personnage(String nom, String groupe) {
		super(nom, groupe, Caracteristique.mapCaracteristiquesDefaut());
	}
	
	/**
	 * Ajoute la caracteristique specifiee avec la valeur specifiee. Si la 
	 * caracteristique existe deja, la valeur sera ecrasee.
	 * @param c caracteristique
	 * @param val valeur
	 */
	public void ajouterCaract(Caracteristique c, int val) {
		if (c == Caracteristique.VIE && val <= 0) {
			setAlive(false);
		}
		
		caracts.put(c, Calculs.caperCarac(c, val));
	} // TODO incrementerCaract plutot que ajouterCaract
}
