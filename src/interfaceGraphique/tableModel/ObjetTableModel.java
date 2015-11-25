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

	private int REF;
	private int TYPE;
	public int NOM;
	private int GROUPE;
	private int DEBUT_CARACT;
	private int FIN_CARACT;
	private int PHRASE;

	private List<VueElement> enAttente = new ArrayList<VueElement>();

    
	

	public ObjetTableModel() {
		REF = -1;
		TYPE = 0;
		NOM = TYPE + 1;
		GROUPE 	= NOM + 1;
		DEBUT_CARACT = GROUPE + 1;
		FIN_CARACT = DEBUT_CARACT + Caracteristique.nbCaract() - 1;
		PHRASE = FIN_CARACT + 1;
	}

	@Override 
    public int getRowCount() {
        return super.getRowCount() + enAttente.size();
    }
	
    @Override
    public int getColumnCount() {
        // Nom, Groupe, Phrase, Type 4 caractéristiques
        return 4 + Caracteristique.nbCaract();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VueElement vue;
        if (isWaiting(rowIndex))
        	vue = enAttente.get(rowIndex - getVues().size());
        else
        	vue = getVues().get(rowIndex);
        
        if (columnIndex == REF)
            return vue.getRefRMI();
        if (columnIndex == NOM)
            return vue.getNom();
        if (columnIndex == GROUPE)
            return vue.getGroupe();
        if (columnIndex == PHRASE)
            return vue.getPhrase();
        if (columnIndex == TYPE)
        	return vue.getType().nom;
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT){
        	Caracteristique[] caracts = Caracteristique.values();
            return vue.getCaract(caracts[columnIndex - DEBUT_CARACT]);
        }
        return null;
    }
    
    @Override
    public int getColumnWidth(int columnIndex) {       

        if (columnIndex == REF)
            return 40;
        if (columnIndex == NOM)
            return 0;
        if (columnIndex == GROUPE)
            return 0;
        if (columnIndex == PHRASE)
            return 200; 
        if (columnIndex == TYPE)
        	return 60;
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT){
        	return 40; 
        }       
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
        if (columnIndex == TYPE)
        	return "Type";
        
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
        if (columnIndex == TYPE)
            return String.class;
        
        if (columnIndex >= DEBUT_CARACT && columnIndex <= FIN_CARACT)
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
	 * Permet de savoir si une l'élément correspondant à une ligne est en attente
	 * @param row ligne pour laquelle on souhaite savoir si l'élément est en attente
	 * @return true si la ligne est en attente, false sinon
	 */
	public boolean isWaiting(int row){
		return row >= getVues().size();
	}
	
	/**
	 * Défini les éléments en attente
	 * @param waitingObject liste des éléments en attente
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
   
}
