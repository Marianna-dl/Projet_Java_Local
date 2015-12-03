package interfaceGraphique.tableModel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import interfaceGraphique.view.VuePotion;
import modele.Caracteristique;

/**
 * TableModel des potions, envoyees ou en attente.
 * 
 */
public class PotionTableModel extends AbstractElementTableModel<VuePotion> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Liste des potions en attente d'etre envoyees.
	 */
	private List<VuePotion> enAttente = new ArrayList<VuePotion>();

	
	public PotionTableModel() {
		colonnes = new ArrayList<InformationColonne<VuePotion>>();
		indexNom = 1;
		
		// type de la potion
		colonnes.add(new InformationColonne<VuePotion>("Ref", 40, Integer.class, new ValeurColonneRefRMI())); 
		
		// nom de la potion (index 1)
		colonnes.add(new InformationColonne<VuePotion>("Nom", 0, String.class, new ValeurColonneNom())); 
		
		// groupe de la potion
		colonnes.add(new InformationColonne<VuePotion>("Groupe", 0, String.class, new ValeurColonneGroupe()));
		
		// caracteristiques
		for(Caracteristique car : Caracteristique.values()) {
			colonnes.add(new InformationColonne<VuePotion>(car.toString(), 40, Integer.class, new ValeurColonneCaract(car)));
		}
		
		// phrase du personnage
		colonnes.add(new InformationColonne<VuePotion>("Phrase", 300, String.class, new ValeurColonnePhrase())); 
	}

	@Override 
    public int getRowCount() {
        return super.getRowCount() + enAttente.size();
    }
	
    @Override
	public VuePotion getVue(int rowIndex) {
    	VuePotion res;
    	
		if (estEnAttente(rowIndex)) {
			res = enAttente.get(rowIndex - getVues().size());
		} else {
			res = super.getVue(rowIndex);
		}
		
		return res;
	}

	@Override
	public Color getColor(int row) {
		Color res;
		
		if (estEnAttente(row)) {
			res = new Color(112, 112, 112);
		} else {
			res = super.getColor(row);
		}
		
		return res;
	}

	@Override
	public boolean isSelected(int row) {
		boolean res;
		
		if (estEnAttente(row)) {
			res = enAttente.get(row - getVues().size()).isSelected();
		} else {
			res = super.isSelected(row);
		}
		
		return res;
	}

    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = null;
        VuePotion vue = getVue(rowIndex);
        
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getValeur(rowIndex, vue);
    	}
    	
    	return res;
    }

	/**
	 * Permet de savoir si l'element a la ligne donnee est en attente/
	 * @param rowIndex ligne de l'element
	 * @return true si la ligne est en attente, false sinon
	 */
	private boolean estEnAttente(int rowIndex) {
		return rowIndex >= getVues().size();
	}
	
	/**
	 * Modifie les elements en attente.
	 * @param enAttente liste des elements en attente
	 */
	public void setEnAttente(List<VuePotion> enAttente) {
		this.enAttente = enAttente;
	}
   
}
