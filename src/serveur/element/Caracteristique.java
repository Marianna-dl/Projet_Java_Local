package serveur.element;

import java.util.HashMap;

/**
 * Caracteristiques possibles pour les personnages et les objets. 
 *
 */
public enum Caracteristique {
	
	/**
	 * Points de vie.
	 */
	VIE("Vie", "VIE", 0, 100, 100),
	
	/**
	 * Force : indique les degats infliges. 
	 */
	FORCE("Force", "FOR", 0, 100, 0),
		
	/**
	 * Definit l'ordre d'action des personnages lors d'un tour de jeu. 
	 */
	INITIATIVE("Initiative", "INIT", 0, 1000, 50);
	
	/**
	 * Nom complet.
	 */
	private final String nomComplet;
	
	/**
	 * Nom court. 
	 */
	private final String nomCourt;
	
	/**
	 * Valeur minimale.
	 */
	public final int min;
	
	/**
	 * Valeur maximale.
	 */
	public final int max;
	
	/**
	 * Valeur initiale par defaut. 
	 */
	public int init;
	
	/**
	 * Cree une caracteristique.
	 * @param nomComplet nom complet
	 * @param nomCourt nom raccourci
	 * @param min valeur minimale
	 * @param max valeur maximale
	 * @param init valeur initiale
	 * @param prix prix d'achat
	 */
	private Caracteristique(String nomComplet, String nomCourt, int min, int max, int init) {
		this.nomComplet = nomComplet;
		this.nomCourt = nomCourt;
		this.min = min;
		this.max = max;
		this.init = init;
	}

	public String toString() {
		return nomCourt;
	}

	/**
	 * Cree un map de caracteristiques specifiques a une potion : vie, force
	 * et vitesse.
	 * @param vie quantite de vie
	 * @param force quantite de force
	 * @param vitesse quantite de vitesse
	 * @return map caracteristique/valeur contenant les valeurs donnees
	 */
	public static HashMap<Caracteristique, Integer> caracteristiquesPotion(int vie, int force, int vitesse) {
		HashMap<Caracteristique, Integer> caractsValues = new HashMap<Caracteristique, Integer>();
		caractsValues.put(VIE, vie);
		caractsValues.put(FORCE, force);
		
		return caractsValues;
	}


	/**
	 * Cree un map de caracteristiques contenant toutes les caracteristiques 
	 * avec leur valeur d'initialisation par defaut. 
	 * @return map caracteristique/valeur contenant les valeurs par defaut
	 */
	public static HashMap<Caracteristique,Integer> caracteristiquesDefaut() {
		HashMap<Caracteristique, Integer> caractsValues = new HashMap<Caracteristique, Integer>();
		for (Caracteristique caract : values()){
			caractsValues.put(caract, caract.init);
		}
		
		return caractsValues;
	}

	/**
	 * Retourne une chaine de caracteres representant les caracteristiques 
	 * donnees.
	 * @param caract caracteristiques
	 * @return string des caracteristiques
	 */
	public static String caracteristiquesToString(HashMap<Caracteristique, Integer> caract) {
		String res = "";
		
		for (Caracteristique c : values()) {
			if (caract.containsKey(c)) {
				res += c.toString() + ":" + caract.get(c) + " ";
			}
		}
		return res;
	}

	/**
	 * Compte le nombre total de caracteristiques.
	 * @return nombre de caracteristiques
	 */
	public static int nbCaracts() {
		return values().length;
	}
	
	public String getNomComplet() {
		return nomComplet;
	}


	
}
