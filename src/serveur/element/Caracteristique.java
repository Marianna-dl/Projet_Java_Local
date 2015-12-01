package serveur.element;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Caracteristiques possibles pour les personnages et les objets. 
 *
 */
public enum Caracteristique {
	
	/**
	 * Points de vie.
	 */
	VIE("Vie", "VIE2", 0, 100, 100, 1),
	
	/**
	 * Force : indique les degats infliges. 
	 */
	FORCE("Force", "FOR", 0, 100, 0, 1),
	
	/**
	 * Vitesse : indique le nombre de cases maximum parcourues a chaque 
	 * deplacement.
	 */
	VITESSE("Vitesse", "VIT", 1, 4, 1, 30),
	
	/**
	 * Nombre de pieces possedees. 
	 */
	ARGENT("Argent", "ARG", 0, Integer.MAX_VALUE, 100, -1),
	
	/**
	 * Definit l'ordre d'action des personnages lors d'un tour de jeu. 
	 */
	INITIATIVE("Initiative", "INIT", 0, 1000, 50, -1);
	
	
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
	 * Prix d'achat d'un point dans la caracteristique courante. 
	 */
	private int prix;

	/**
	 * Cree une caracteristique.
	 * @param nomComplet nom complet
	 * @param nomCourt nom raccourci
	 * @param min valeur minimale
	 * @param max valeur maximale
	 * @param init valeur initiale
	 * @param prix prix d'achat
	 */
	private Caracteristique(String nomComplet, String nomCourt, int min, int max, int init, int prix) {
		this.nomComplet = nomComplet;
		this.nomCourt = nomCourt;
		this.min = min;
		this.max = max;
		this.init = init;
		this.prix = prix;
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
		caractsValues.put(VITESSE, vitesse);
		
		return caractsValues;
	}

	/**
	 * Cree un map de caracteristiques specifiques a un tresor : argent.
	 * @param argent quantite d'argent
	 * @return map caracteristique/valeur contenant la valeur donnee
	 */
	public static HashMap<Caracteristique, Integer> caracteristiquesTresor(int argent) {
		HashMap<Caracteristique, Integer> caractsValues = new HashMap<Caracteristique, Integer>();
		caractsValues.put(ARGENT, argent);
		
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

	public int getPrix() {
		return prix;
	}
	
	/**
	 * Calcule le prix d'un map de caracteristiques/quantite. 
	 * @param caracts caracteristiques
	 * @return prix total
	 */
	public static int calculerPrix(HashMap<Caracteristique, Integer> caracts) {
		int prix = 0;
		
		for (Entry<Caracteristique, Integer> entryCaract : caracts.entrySet()) {
			int prixUnitaire = entryCaract.getKey().getPrix();
			int quantite = entryCaract.getValue();
			
			if (prixUnitaire >= 0 && quantite > 0) {
				prix += quantite * prixUnitaire;
			}
		}
		
		return prix;		
	}
}
