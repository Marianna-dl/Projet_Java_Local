package interfaceGraphique.tableModel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;
import serveur.element.Caracteristique;

/**
 * TableModel des personnages, connectes ou deconnectes.
 * 
 */
public class PersonnageTableModel extends AbstractElementTableModel<VuePersonnage> {

	private static final long serialVersionUID = 1L;

	/**
	 * Liste des personnages deconnectes.
	 */
	private List<VuePersonnageDeconnecte> deconnectes = new ArrayList<VuePersonnageDeconnecte>();
    
	
	public PersonnageTableModel() {
		colonnes = new ArrayList<InformationColonne<VuePersonnage>>();
		indexNom = 1;
		
		// reference RMI
		colonnes.add(new InformationColonne<VuePersonnage>("Ref", 40, Integer.class, 
				new IValeurColonne<VuePersonnage>() {
					@Override
					public Object valeurColonne(int rowIndex, VuePersonnage vue) {
						return vue.getRefRMI();
					}
				}
		)); 
		
		// nom du personnage (index 1)
		colonnes.add(new InformationColonne<VuePersonnage>("Nom", 0, String.class, new ValeurColonneNom())); 
		
		// groupe du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Groupe", 0, String.class, new ValeurColonneGroupe()));
		
		// caracteristiques
		for(Caracteristique car : Caracteristique.values()) {
			colonnes.add(new InformationColonne<VuePersonnage>(car.toString(), 40, Integer.class, new ValeurColonneCaract(car)));
		}
		
		// phrase du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Phrase", 200, String.class, new ValeurColonnePhrase())); 
		
		// leader du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Leader", 47, String.class, new ValeurColonneLeader())); 
		
		// equipe du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Equipe", 150, String.class, new ValeurColonneEquipe())); 
	}
    
    @Override 
    public int getRowCount() {
        return super.getRowCount() + deconnectes.size();
    }

    @Override
	public Color getColor(int rowIndex) {
		Color res;
		
	    if (isConnected(rowIndex)) {
	    	res = super.getColor(rowIndex);
	    } else {
	    	res = deconnectes.get(rowIndex - getVues().size()).getColor();
	    }
	    
	    return res;
	}
    
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = null;
    	VuePersonnage vue;
    	
        if (isConnected(rowIndex)) {
        	vue = getVue(rowIndex);	        
        } else {
        	vue = deconnectes.get(rowIndex - getVues().size());
        }
    	
    	if(columnIndex < getColumnCount()) {
    		res = colonnes.get(columnIndex).getValeur(rowIndex, vue);
    	}
    	
    	return res;
    }

	/**
	 * Permet de savoir si l'element a la ligne donnee est connecte.
	 * @param rowIndex ligne de l'element
	 * @return true si la ligne est connectee, false sinon
	 */
	public boolean isConnected(int rowIndex) {
		return rowIndex < getVues().size();
	}

	/**
	 * Modifie la liste des deconnectes.
	 * @param deconnectes liste des elements deconnectes
	 */
	public void setDeconnectes(List<VuePersonnageDeconnecte> deconnectes) {
		this.deconnectes = deconnectes;
	}
    
}
