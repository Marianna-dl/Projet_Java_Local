package interfaceGraphique.tableModel;


import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import serveur.element.Caracteristique;

/**
 * Table model des personnages
 */
public class PersonnageTableModel extends AbstractElementTableModel <VuePersonnage> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Rangs correspondant aux colonnes
	 */
	private int ref;
	private int nom;
	private int groupe;
	private int debutCaract;
	private int finCaract;
	private int phrase;
	private int leader;
	private int equipe;

	/**
	 * Liste des personnages deconnectes
	 */
	private List<VuePersonnageDeconnecte> deconnected = new ArrayList<VuePersonnageDeconnecte>();
    
	public PersonnageTableModel() {
		ref = 0;
		nom = ref + 1;
		groupe 	= nom + 1;
		debutCaract = groupe + 1;
		finCaract = debutCaract + Caracteristique.nbCaract() - 1;
		phrase = finCaract + 1;
		leader 	= phrase + 1;
		equipe 	= leader + 1;
	}
    
    @Override 
    public int getRowCount() {
        return super.getRowCount() + deconnected.size();
    }

    @Override
    public int getColumnCount() {
        // Ref, Nom, Groupe, Phrase, leader, equipe + nombre de caracteristiques
        return 6 + Caracteristique.nbCaract();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	VuePersonnage vue;
        if (isConnected(rowIndex)){
        	vue = getVues().get(rowIndex);	        
        } else {
        	vue = deconnected.get(rowIndex - getVues().size());
        }
        
        if (columnIndex == ref)
            return vue.getRefRMI();
        if (columnIndex == nom)
            return vue.getNom();
        if (columnIndex == groupe)
            return vue.getGroupe();
        if (columnIndex == phrase)
            return vue.getPhrase();
        if (columnIndex == equipe)
            return vue.equipeToString(); 
        if (columnIndex == leader)
            return vue.leaderToString();  
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract){
        	Caracteristique[] caracts = Caracteristique.values();
            return vue.getCaract(caracts[columnIndex - debutCaract]);
        }
        return null;
    }
    
    public int getColumnWidth(int columnIndex) {            
        if (columnIndex == ref)
            return 40;
        if (columnIndex == nom)
            return 0;
        if (columnIndex == groupe)
            return 0;
        if (columnIndex == phrase)
            return 200; 
        if (columnIndex == equipe)
            return 150;
        if (columnIndex == leader) 
            return 47;
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract)
        	return 40;
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex){
        if (columnIndex == ref)
            return "Ref";
        if (columnIndex == nom)
            return "Nom";
        if (columnIndex == groupe)
            return "Groupe";
        if (columnIndex == phrase)
            return "Phrase";
        if (columnIndex == equipe)
            return "Equipe";
        if (columnIndex == leader)
            return "Leader";
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract){
        	Caracteristique[] caracts = Caracteristique.values();
            return caracts[columnIndex - debutCaract].toString();
        }
        return "";
    }

	@Override
	public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == ref)
            return Integer.class;
        if (columnIndex == nom)
            return String.class;
        if (columnIndex == groupe)
            return String.class;
        if (columnIndex == phrase)
            return String.class;
        if (columnIndex == equipe)
            return String.class;
        if (columnIndex == leader)
            return String.class; 
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract)
            return Integer.class;
        
        return String.class;
	}
	
	@Override
	public Color getColor(int rowIndex) {
        if (isConnected(rowIndex))
			return super.getColor(rowIndex);
        else
        	return deconnected.get(rowIndex - getVues().size()).getColor();
	}

	/**
	 * Permet de savoir si une l'element correspondant a une ligne est connecte
	 * @param row ligne pour laquelle on souhaite savoir si l'element est connecte
	 * @return true si la ligne est en attente, false sinon
	 */
	public boolean isConnected(int row) {
		return row < getVues().size();
	}

	/**
	 * Definis la liste des deconnectes
	 * @param deconnected liste des elements deconnectes
	 */
	public void setDeconnected(List<VuePersonnageDeconnecte> deconnected) {
		this.deconnected = deconnected;
	}

	public int getNom() {
		return nom;
	}
    
    
}
