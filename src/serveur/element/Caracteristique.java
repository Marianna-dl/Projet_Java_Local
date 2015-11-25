package serveur.element;

import java.util.Hashtable;
import java.util.Map.Entry;

public enum Caracteristique {
	VIE("Vie", "VIE", 0, 100, 100, 1),
	FORCE("Force", "FOR", 0, 100, 0, 1),
	VITESSE("Vitesse", "VIT", 1, 4, 1, 30),
	ARGENT("Argent", "ARG", 0, 10000, 100, -1),
	INITIATIVE("Initiative", "INIT", 0, 1000, 50, -1);
	
	private final String smallName;
	private final String fullName;
	public final int min;
	public final int max;
	// Valeur initiale de la caractéristique
	public int init;
	private int prix;

	private Caracteristique(String fullName, String smallName, int min, int max, int init, int prix) {
		this.smallName = smallName;
		this.fullName = fullName;
		this.min = min;
		this.max = max;
		this.init = init;
		this.prix = prix;
	}

	public String toString() {
		return smallName;
	}

	public static Hashtable<Caracteristique,Integer> hachageDePotion(int vie, int force, int vitesse) {
		Hashtable<Caracteristique, Integer> caractsValues = new Hashtable<Caracteristique, Integer>();
		caractsValues.put(VIE, vie);
		caractsValues.put(FORCE, force);
		caractsValues.put(VITESSE, vitesse);
		return caractsValues;
	}

	public static Hashtable<Caracteristique, Integer> hachageDeTresor(int montant) {
		Hashtable<Caracteristique, Integer> caractsValues = new Hashtable<Caracteristique, Integer>();
		caractsValues.put(ARGENT, montant);
		return caractsValues;
	}
	
	public static Hashtable<Caracteristique,Integer> hachageDeCaracInitPerso() {
		Hashtable<Caracteristique, Integer> caractsValues = new Hashtable<Caracteristique, Integer>();
		for (Caracteristique caract : values()){
			caractsValues.put(caract, caract.init);
		}
		return caractsValues;
	}

	public static String hachageToString(Hashtable<Caracteristique,Integer> caract) {
		String car = "";
		for (Caracteristique c : values()){
			if (caract.containsKey(c)){
				car += c.toString()+":"+caract.get(c)+" ";
			}
		}
		return car;
	}

	public static int nbCaract() {
		return values().length;
	}
	
	public String fullName(){
		return fullName;
	}

	public int getPrix() {
		return prix;
	}
	
	/**
	 * Calcule le prix de quantites de caracteristiques
	 * @param caracts liste de Caract -> quantite
	 * @return prix total
	 */
	public static int calculerPrix(Hashtable<Caracteristique, Integer> caracts) {
		int prix = 0;
		for (Entry<Caracteristique, Integer> entryCaract : caracts.entrySet()){
			int prixUnitaire = entryCaract.getKey().getPrix();
			int quantite = entryCaract.getValue();
			if (prixUnitaire >= 0 && quantite > 0)
				prix += quantite * prixUnitaire;
		}
		return prix;		
	}
}
