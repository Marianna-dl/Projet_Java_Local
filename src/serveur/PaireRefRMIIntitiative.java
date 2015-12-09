package serveur;

import utilitaires.Calculs;

/**
 * Paire de deux entiers : la reference RMI d'un personnage et son initiative.
 * Permet de classer les references dans l'ordre des initiatives au demarrage 
 * d'un tour. 
 *
 */
public class PaireRefRMIIntitiative implements Comparable<PaireRefRMIIntitiative> {

	/**
	 * Reference RMI. 
	 */
	private int ref;
	
	/**
	 * Valeur d'initiative du personnage correspondant. 
	 */
	private int initiative;
	
	/**
	 * Cree une paire reference RMI/initiative.
	 * @param ref reference RMI 
	 * @param initiative initiative
	 */
	public PaireRefRMIIntitiative(int ref, int initiative) {
		this.ref = ref;
		this.initiative = initiative;
	}
	
	public int getRef() {
		return ref;
	}
	
	/**
	 * Compare deux paires reference RMI/initiative a l'aide des valeurs 
	 * d'initiative. 
	 */
	@Override
	public int compareTo(PaireRefRMIIntitiative p2) {
		int res = p2.initiative - this.initiative;
		
		if (res == 0) {
			res = Calculs.nombreAleatoire(-100, 100);
		}
		
		return res;
	}
}
