package interfaceGraphique.tableModel;

import interfaceGraphique.view.VuePersonnage;

import java.awt.Color;

import serveur.element.Caracteristique;

public class ClassementTableModel extends AbstractElementTableModel<VuePersonnage> {

	private static final long serialVersionUID = -476363672825620047L;
	
	/**
	 * Rangs correspondant aux colonnes
	 */
	private int nom;
	private int groupe;
	private int debutCaract;
	private int finCaract;
	private int position;
	private int leader;
	private int equipe;
    
	public ClassementTableModel() {
		position = 0;
		nom = 1;
		groupe 	= 2;
		debutCaract = 3;
		finCaract = debutCaract + Caracteristique.nbCaract() - 1;
		leader = finCaract + 1;
		equipe	= leader + 1;
	}

    @Override
    public int getColumnCount() {
        // position, Nom, Groupe, leader, equipe + nombre de caracteristiques
        return 5 + Caracteristique.nbCaract();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    	VuePersonnage vue = getVues().get(rowIndex);	        

        if (columnIndex == position)
            return rowIndex+1;
        if (columnIndex == nom)
            return vue.getNom();
        if (columnIndex == groupe)
            return vue.getGroupe();
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

        if (columnIndex == position)
            return 60; 
        if (columnIndex == nom)
            return 0;
        if (columnIndex == groupe)
            return 0;
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

        if (columnIndex == position)
            return "Classmt";
        if (columnIndex == nom)
            return "Nom";
        if (columnIndex == groupe)
            return "Groupe";
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

        if (columnIndex == position)
            return String.class;
        if (columnIndex == nom)
            return String.class;
        if (columnIndex == groupe)
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
        return super.getColor(rowIndex);
	}

	public int getNom() {
		return nom;
	}


}
