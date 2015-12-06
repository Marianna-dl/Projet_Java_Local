package interfacegraphique.tablemodel;

import java.awt.Color;
import java.util.ArrayList;

import serveur.element.Caracteristique;
import serveur.infosclient.VuePotion;

/**
 * TableModel des potions, envoyees ou en attente.
 * 
 */
public class PotionTableModel extends ElementTableModel<VuePotion> {
	
	private static final long serialVersionUID = 1L;

	
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
        return super.getRowCount();
    }

	@Override
	public Color getColor(int rowIndex) {
		Color res;
		
		if (vues.get(rowIndex).isEnAttente()) {
			res = new Color(112, 112, 112);
		} else {
			res = super.getColor(rowIndex);
		}
		
		return res;
	}
   
}
