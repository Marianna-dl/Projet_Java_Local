package interfaceGraphique.tableModel;


import interfaceGraphique.view.VueElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import serveur.element.Caracteristique;

/**
 * Table model des objets
 */
public class ObjetTableModel extends AbstractElementTableModel <VueElement>{
	
	private static final long serialVersionUID = 1L;

	private int ref;
	private int type;
	private int nom;
	private int groupe;
	private int debutCaract;
	private int finCaract;
	private int phrase;

	private List<VueElement> enAttente = new ArrayList<VueElement>();

    
	

	public ObjetTableModel() {
		ref = -1;
		type = 0;
		nom = type + 1;
		groupe 	= nom + 1;
		debutCaract = groupe + 1;
		finCaract = debutCaract + Caracteristique.nbCaract() - 1;
		phrase = finCaract + 1;
	}

	@Override 
    public int getRowCount() {
        return super.getRowCount() + enAttente.size();
    }
	
    @Override
    public int getColumnCount() {
        // Nom, Groupe, Phrase, Type 4 caracteristiques
        return 4 + Caracteristique.nbCaract();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VueElement vue;
        if (isWaiting(rowIndex))
        	vue = enAttente.get(rowIndex - getVues().size());
        else
        	vue = getVues().get(rowIndex);
        
        if (columnIndex == ref)
            return vue.getRefRMI();
        if (columnIndex == nom)
            return vue.getNom();
        if (columnIndex == groupe)
            return vue.getGroupe();
        if (columnIndex == phrase)
            return vue.getPhrase();
        if (columnIndex == type)
        	return vue.getType().nom;
        if (columnIndex >= debutCaract && columnIndex <= finCaract){
        	Caracteristique[] caracts = Caracteristique.values();
            return vue.getCaract(caracts[columnIndex - debutCaract]);
        }
        return null;
    }
    
    @Override
    public int getColumnWidth(int columnIndex) {       

        if (columnIndex == ref)
            return 40;
        if (columnIndex == nom)
            return 0;
        if (columnIndex == groupe)
            return 0;
        if (columnIndex == phrase)
            return 200; 
        if (columnIndex == type)
        	return 60;
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract){
        	return 40; 
        }       
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
        if (columnIndex == type)
        	return "Type";
        
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
        if (columnIndex == type)
            return String.class;
        
        if (columnIndex >= debutCaract && columnIndex <= finCaract)
            return Integer.class;

        return String.class;
	}
	
	@Override
	public Color getColor(int row) {
		if (isWaiting(row))
			return new Color(112,112,112);
		else
			return super.getColor(row);
	}
	
	/**
	 * Permet de savoir si une l'element correspondant a une ligne est en attente
	 * @param row ligne pour laquelle on souhaite savoir si l'element est en attente
	 * @return true si la ligne est en attente, false sinon
	 */
	public boolean isWaiting(int row){
		return row >= getVues().size();
	}
	
	/**
	 * Defini les elements en attente
	 * @param waitingObject liste des elements en attente
	 */
	public void setWaiting(List<VueElement> waitingObject) {
		this.enAttente = waitingObject;
	}

	@Override
	public VueElement getVue(int row) {		
		if (isWaiting(row))
			return enAttente.get(row - getVues().size());
		else
			return super.getVue(row);
	} 
	
	@Override
	public boolean isSelected(int row) {
		if (isWaiting(row))
			return enAttente.get(row - getVues().size()).isSelected();
		else
			return super.isSelected(row);
	}

	public int getNom() {
		return nom;
	}
   
}
