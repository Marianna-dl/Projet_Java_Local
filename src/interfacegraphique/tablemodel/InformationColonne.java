package interfacegraphique.tablemodel;

import serveur.vuelement.VueElement;

/**
 * Contient les informations relatives a chaque colonne d'un TableModel :
 * etant donnee la vue correspondante, la valeur a afficher,
 * la largeur, l'intitule et la classe de la valeur.
 *
 * @param <V> type de vues affichees (personnages ou potions).
 */
public class InformationColonne<V extends VueElement<?>> {

	/**
	 * Nom de la colonne.
	 */
	private String nom;
	
	/**
	 * Largeur de la colonne.
	 */
	private int largeur;
	
	/**
	 * Classe de la colonne.
	 */
	private Class<?> classe;
	
	/**
	 * Classe contenant la methode affichant la valeur correspondant a cette
	 * colonne.
	 */
	private IValeurColonne<V> valeur;
	
	/**
	 * Cree une colonne avec sa largeur, son nom, la classe de ses elements et
	 * la methode pour afficher la valeur d'une vue.
	 * @param n nom
	 * @param lg largeur
	 * @param cl classe
	 * @param val valeur
	 */
	public InformationColonne(String n, int lg, Class<?> cl, IValeurColonne<V> val) {
		nom = n;
		largeur = lg;
		classe = cl;
		valeur = val;
	}

	public int getLargeur() {
		return largeur;
	}

	public String getNom() {
		return nom;
	}

	public Class<?> getClasse() {
		return classe;
	}
	
	public Object getValeur(int rowIndex, V vue) {
		return valeur.valeurColonne(rowIndex, vue);
	}

}
