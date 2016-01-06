package interfacegraphique.tablemodel;

import serveur.vuelement.VueElement;

/**
 * Comportement permettant de recuperer la valeur d'une vue dans une colonne.
 * Utilisation du design pattern "strategie", permettant de selectionner le 
 * comportement lors de l'execution. 
 *
 */
public interface IValeurColonne<V extends VueElement<?>> {
	/**
	 * Retourne la valeur a afficher pour la vue donnee (a l'index de ligne 
	 * donne) dans la colonne courante.
	 * @param rowIndex index de la ligne
	 * @param vue vue de l'element
	 * @return objet a afficher dans la case
	 */
	public Object valeurColonne(int rowIndex, V vue);
}
