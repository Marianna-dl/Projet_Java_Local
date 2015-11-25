package interfaceGraphique.tableModel;

import interfaceGraphique.view.VueElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Classe abstraite de Table Model affichant une liste de vueElement
 *
 */
public abstract class AbstractElementTableModel <V extends VueElement> extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Liste des vues a afficher dans le tableau
	 */
	private List<V> vues = new ArrayList<V>();  
    
	
    @Override
    public int getRowCount() {
        return vues.size();
    }

	@Override
    public boolean isCellEditable(int rowIndex, int colIndex){
        // Aucunes cellules ne sont editables
		return false;
    }	

	/**
	 * Recupere la reference correspondant a une ligne donnee
	 * @param row ligne pour laquelle on souhaite recuperer la reference
	 * @return reference de l'element de la ligne, -1 si la ligne ne correspond a aucun element
	 */
	public int getRef(int row) {
		if (row < vues.size())		
			return vues.get(row).getRefRMI();
		else
			return -1;
	}
	
	/**
	 * Recupere la VueElement correspondant a une ligne donnee
	 * @param row ligne pour laquelle on souhaite recuperer la VueElement
	 * @return VueElement de l'element de la ligne, null si la ligne ne correspond a aucun element
	 */
	public V getVue(int row) {
		if (row < vues.size())		
			return vues.get(row);
		else
			return null;
	} 
	
	/**
	 * Recupere la couleur correspondant a une ligne donnee
	 * @param row ligne pour laquelle on souhaite recuperer la couleur
	 * @return Couleur de l'element de la ligne, null si la ligne ne correspond a aucun element
	 */
	public Color getColor(int row) {
        if (row < vues.size())
        	return vues.get(row).getColor();
        else
        	return null;
    }

	/**
	 * Permet de savoir si une ligne est selectionnee
	 * @param row ligne pour laquelle on souhaite savoir si elle est selectionnee
	 * @return true si la ligne est selectionnee, false sinon
	 */
	public boolean isSelected(int row) {
		if (row < vues.size())
			return vues.get(row).isSelected();
		else
			return false;
	}	

	/**
	 * Permet de recuperer toutes les VueElement du tableau
	 * @return liste de toutes les VueElement
	 */
    public List<V> getVues() {
		return vues;
	}
    
    /**
     * Defini les vues de la classe
     * @param vues nouvelles vues
     */
	public void setVues(List<V> vues) {
		this.vues = vues;
	}
	
	
	/*
	 * METHODES ABSTRAITES
	 */
	/**
	 * Permet de connaitre la largeur d'une colone
	 * @param column rang de la colonne
	 * @return largeur de la colonne
	 */
    public abstract int getColumnWidth(int column);

    @Override
    public abstract int getColumnCount();
    
    @Override
    public abstract Object getValueAt(int rowIndex, int columnIndex);
    
    @Override
    public abstract String getColumnName(int columnIndex);

	@Override
	public abstract Class<?> getColumnClass(int columnIndex);
}
