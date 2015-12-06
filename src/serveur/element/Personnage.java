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
	 * @return vrai si le personnage est mort a la suite de cet ajout de 
	 * caracteristiques
	 */
	public boolean ajouterCaract(Caracteristique c, int val) {
		boolean actifAvant = estActif();
		
		caracts.put(c, Calculs.caperCarac(c, val));
		
		return actifAvant && !estActif();
	} // TODO incrementerCaract plutot que ajouterCaract

	@Override
	public boolean estActif() {
		Integer vie = caracts.get(Caracteristique.VIE);
		return vie != null && vie <= 0;
	}
}
