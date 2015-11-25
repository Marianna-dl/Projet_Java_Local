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
	 * rangs correspondant aux colonnes
	 */
	private int REF;
	public int NOM;
	private int GROUPE;
	private int DEBUT_CARACT;
	private int FIN_CARACT;
	private int PHRASE;
	private int LEADER;
	private int EQUIPE;

	/**
	 * Liste des personnages déconnectés
	 */
	private List<VuePersonnageDeconnecte> deconnected = new ArrayList<VuePersonnageDeconnecte>();
    
	public PersonnageTableModel() {
		REF = 0;
		NOM = REF + 1;
		GROUPE 	= NOM + 1;
		DEBUT_CARACT = GROUPE + 1;
		FIN_CARACT = DEBUT_CARACT + Caracteristique.nbCaract() - 1;
		PHRASE = FIN_CARACT + 1;
		LEADER 	= PHRASE + 1;
		EQUIPE 	= LEADER + 1;
	}
    
    @Override 
    public int getRowCount() {
        return super.getRowCount() + deconnected.size();
    }

    @Override
    public int getColumnCount() {
        // Ref, Nom, Groupe, Phrase, leader, equipe + nombre de caractéristiques
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
        
        if (columnIndex == REF)
            return vue.getRefRMI();
        if (columnIndex == NOM)
            return vue.getNom();
        if (columnIndex == GROUPE)
            return vue.getGroupe();
        if (columnIndex == PHRASE)
            return vue.getPhrase();
        if (columnIndex == EQUIPE)
            return vue.equipeToString(); 
        if (columnIndex == LEADER)
            return vue.leaderToString();  
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT){
        	Caracteristique[] caracts = Caracteristique.values();
            return vue.getCaract(caracts[columnIndex - DEBUT_CARACT]);
        }
        return null;
    }
    
    public int getColumnWidth(int columnIndex) {            
        if (columnIndex == REF)
            return 40;
        if (columnIndex == NOM)
            return 0;
        if (columnIndex == GROUPE)
            return 0;
        if (columnIndex == PHRASE)
            return 200; 
        if (columnIndex == EQUIPE)
            return 150;
        if (columnIndex == LEADER) 
            return 47;
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT)
        	return 40;
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex){
        if (columnIndex == REF)
            return "Ref";
        if (columnIndex == NOM)
            return "Nom";
        if (columnIndex == GROUPE)
            return "Groupe";
        if (columnIndex == PHRASE)
            return "Phrase";
        if (columnIndex == EQUIPE)
            return "Equipe";
        if (columnIndex == LEADER)
            return "Leader";
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT){
        	Caracteristique[] caracts = Caracteristique.values();
            return caracts[columnIndex - DEBUT_CARACT].toString();
        }
        return "";
    }

	@Override
	public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == REF)
            return Integer.class;
        if (columnIndex == NOM)
            return String.class;
        if (columnIndex == GROUPE)
            return String.class;
        if (columnIndex == PHRASE)
            return String.class;
        if (columnIndex == EQUIPE)
            return String.class;
        if (columnIndex == LEADER)
            return String.class; 
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT)
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
	 * Permet de savoir si une l'élément correspondant à une ligne est connecté
	 * @param row ligne pour laquelle on souhaite savoir si l'élément est connecté
	 * @return true si la ligne est en attente, false sinon
	 */
	public boolean isConnected(int row) {
		return row < getVues().size();
	}

	/**
	 * Definis la liste des déconnectés
	 * @param deconnected liste des éléments déconnectés
	 */
	public void setDeconnected(List<VuePersonnageDeconnecte> deconnected) {
		this.deconnected = deconnected;
	}
    
    
}
