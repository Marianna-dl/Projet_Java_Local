package interfaceGraphique.tableModel;

import interfaceGraphique.view.VuePersonnage;

import java.awt.Color;

import serveur.element.Caracteristique;

public class ClassementTableModel extends AbstractElementTableModel<VuePersonnage> {

	private static final long serialVersionUID = -476363672825620047L;
	/**
	 * rangs correspondant aux colonnes
	 */
	public int NOM;
	private int GROUPE;
	private int DEBUT_CARACT;
	private int FIN_CARACT;
	private int POSITION;
	private int LEADER;
	private int EQUIPE;
    
	public ClassementTableModel() {
		POSITION = 0;
		NOM = 1;
		GROUPE 	= 2;
		DEBUT_CARACT = 3;
		FIN_CARACT = DEBUT_CARACT + Caracteristique.nbCaract() - 1;
		LEADER = FIN_CARACT + 1;
		EQUIPE	= LEADER + 1;
	}

    @Override
    public int getColumnCount() {
        // position, Nom, Groupe, leader, equipe + nombre de caractéristiques
        return 5 + Caracteristique.nbCaract();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	VuePersonnage vue = getVues().get(rowIndex);	        

        if (columnIndex == POSITION)
            return rowIndex+1;
        if (columnIndex == NOM)
            return vue.getNom();
        if (columnIndex == GROUPE)
            return vue.getGroupe();
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

        if (columnIndex == POSITION)
            return 60; 
        if (columnIndex == NOM)
            return 0;
        if (columnIndex == GROUPE)
            return 0;
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

        if (columnIndex == POSITION)
            return "Classmt";
        if (columnIndex == NOM)
            return "Nom";
        if (columnIndex == GROUPE)
            return "Groupe";
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

        if (columnIndex == POSITION)
            return String.class;
        if (columnIndex == NOM)
            return String.class;
        if (columnIndex == GROUPE)
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
        return super.getColor(rowIndex);
	}


}
