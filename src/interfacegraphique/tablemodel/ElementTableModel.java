package interfacegraphique.tablemodel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import serveur.element.Caracteristique;
import serveur.vuelement.VueElement;

/**
 * Classe abstraite de Table Model affichant une liste de vueElement.
 * 
 * @param <V> type de vue affichee (personnages ou potions).
 */
public abstract class ElementTableModel<V extends VueElement<?>> extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Liste des vues a afficher dans le tableau.
	 */
	protected List<V> vues = new ArrayList<V>();
	
	/**
	 * Liste des informations pour chaque colonne.
	 */
	protected List<InformationColonne<V>> colonnes;
	
	/**
	 * Index de la colonne affichant le nom. 
	 */
	protected int indexNom;
	
	/**
	 * Classes implementant la facon de recuperer l'element a afficher a une 
	 * ligne donnee pour la colonne correspondante.
	 * Les classes ci-dessous sont reutilisees plusieurs fois dans les classes
	 * filles de la classe actuelle. 
	 * On pourrait les declarer chacune dans un fichier separe; elles sont 
	 * declarees ici pour eviter les fichiers inutiles.	 * 
	 * Les comportements utilises une seule fois dans une classe fille ne sont 
	 * pas declares ici, mais directement dans la classe fille correspondante.
	 */
	
	
	/**
	 * Affiche la reference RMI.
	 * 
	 */
	protected class ValeurColonneRefRMI implements IValeurColonne<V> {
		@Override
		public Object valeurColonne(int rowIndex, V vue) {
			return vue.getRefRMI();
		}
	}
	
	/**
	 * Affiche le nom.
	 *
	 */
	protected class ValeurColonneNom implements IValeurColonne<V> {
		@Override
		public Object valeurColonne(int rowIndex, V vue) {
			return vue.getElement().getNom();
		}
	}

	/**
	 * Affiche le groupe.
	 *
	 */
	protected class ValeurColonneGroupe implements IValeurColonne<V> {
		@Override
		public Object valeurColonne(int rowIndex, V vue) {
			return vue.getElement().getGroupe();
		}
	}

	/**
	 * Affiche la phrase.
	 *
	 */
	protected class ValeurColonnePhrase implements IValeurColonne<V> {
		@Override
		public Object valeurColonne(int rowIndex, V vue) {
			return vue.getPhrase();
		}
	}

	/**
	 * Affiche une caracteristique donnee.
	 *
	 */
	protected class ValeurColonneCaract implements IValeurColonne<V> {
		private Caracteristique caract;
		
		public ValeurColonneCaract(Caracteristique car) {
			caract = car;
		}
		
		@Override
		public Object valeurColonne(int rowIndex, V vue) {
			return vue.getElement().getCaract(caract);
		}
	}

	
	

	@Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        // aucune cellule n'est editable
		return false;
    }
	
    @Override
    public int getRowCount() {
        return vues.size();
    }
	
	/**
	 * Recupere la VueElement correspondant a une ligne donnee.
	 * @param rowIndex ligne dont on souhaite recuperer la VueElement
	 * @return VueElement de l'element de la ligne, null si le numero de ligne ne correspond a aucun element
	 */
	public V getVue(int rowIndex) {
		if (rowIndex < vues.size()) {
			return vues.get(rowIndex);
		} else { 
			return null;
		}
	} 
	
	/**
	 * Recupere la couleur correspondant a une ligne donnee
	 * @param rowIndex ligne dont on souhaite recuperer la couleur
	 * @return couleur de l'element de la ligne, null si le numero de ligne ne correspond a aucun element
	 */
	public Color getColor(int rowIndex) {
        if (rowIndex < vues.size()) {
        	return vues.get(rowIndex).getColor();
        } else {
        	return null;
        }
    }

	/**
	 * Recupere toutes les VueElement du tableau.
	 * @return liste de toutes les VueElement
	 */
    public List<V> getVues() {
		return vues;
	}

    public void setVues(List<V> vues) {
		this.vues = vues;
	}

	@Override
    public int getColumnCount() {
    	return colonnes.size();
    }
    
    @Override
    public String getColumnName(int columnIndex) {
    	String res = "";
    	
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getNom();
    	}
    	
    	return res;
    }
	
	/**
	 * Retourne la largeur d'une colonne.
	 * @param columnIndex numero de la colonne
	 * @return largeur de la colonne
	 */
    public int getColumnWidth(int columnIndex) { 
    	int res = 0;
    	
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getLargeur();
    	}
    	
    	return res;
    }

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> res = String.class;
    	
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getClass();
    	}
    	
    	return res;
	}
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {  
    	Object res = null;
    	
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getValeur(rowIndex, getVue(rowIndex));
    	}
    	
    	return res;
    }
    
    /**
     * Retourne l'index de la colonne contenant le nom.
     * @return index
     */
	public int getIndexNom() {
		return indexNom;
	}
}
