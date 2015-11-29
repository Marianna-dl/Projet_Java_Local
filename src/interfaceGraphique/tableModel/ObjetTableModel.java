package interfaceGraphique.tableModel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import interfaceGraphique.view.VueElement;
import serveur.element.Caracteristique;

/**
 * TableModel des objets, envoyes ou en attente.
 * 
 */
public class ObjetTableModel extends AbstractElementTableModel<VueElement> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Liste des objets en attente d'etre envoyes.
	 */
	private List<VueElement> enAttente = new ArrayList<VueElement>();

	
	public ObjetTableModel() {
		colonnes = new ArrayList<InformationColonne<VueElement>>();
		indexNom = 1;
		
		// type de l'objet
		colonnes.add(new InformationColonne<VueElement>("Type", 60, String.class, 
				new IValeurColonne<VueElement>() {
					@Override
					public Object valeurColonne(int rowIndex, VueElement vue) {
						return vue.getType().getNom();
					}
				}
		)); 
		
		// nom de l'objet (index 1)
		colonnes.add(new InformationColonne<VueElement>("Nom", 0, String.class, new ValeurColonneNom())); 
		
		// groupe de l'objet
		colonnes.add(new InformationColonne<VueElement>("Groupe", 0, String.class, new ValeurColonneGroupe()));
		
		// caracteristiques
		for(Caracteristique car : Caracteristique.values()) {
			colonnes.add(new InformationColonne<VueElement>(car.toString(), 40, Integer.class, new ValeurColonneCaract(car)));
		}
		
		// phrase du personnage
		colonnes.add(new InformationColonne<VueElement>("Phrase", 200, String.class, new ValeurColonnePhrase())); 
	}

	@Override 
    public int getRowCount() {
        return super.getRowCount() + enAttente.size();
    }
	
    @Override
	public VueElement getVue(int rowIndex) {
    	VueElement res;
    	
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
        VueElement vue = getVue(rowIndex);
        
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
	public boolean estEnAttente(int rowIndex) {
		return rowIndex >= getVues().size();
	}
	
	/**
	 * Modifie les elements en attente.
	 * @param enAttente liste des elements en attente
	 */
	public void setEnAttente(List<VueElement> enAttente) {
		this.enAttente = enAttente;
	}
   
}
