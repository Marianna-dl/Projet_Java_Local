package interfacegraphique.interfacesimple.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import serveur.element.Caracteristique;

/**
 * Affiche le detail d'une caracteristique : sa barre avec un label.
 *
 */
public class DetailCaracteristique extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Barre de la caracteristique.
	 */
	private Barre barre;
	
	/**
	 * Label de la caracteristique.
	 */
	private JLabel label;
	
	/**
	 * Cree un panel d'affichage de detail d'une caracteristique.
	 * @param caract caracteristique
	 * @param valeur valeur de la caracteristique
	 */
	public DetailCaracteristique(Caracteristique caract, int valeur) {
		Color couleur = null;
		
		switch(caract) {
		case FORCE: 
			couleur = Color.RED; 
			break;
		case VIE: 
			couleur = Color.GREEN; 
			break;
		case INITIATIVE: 
			couleur = Color.BLUE;
			break;
		}
		
		// valeur ramenee sur 100, en fonction du max de la caracteristique
		int valeurUtilisee = (int) ((valeur * 100) / (float) caract.getMax()); 
		
		barre = new Barre(valeurUtilisee, couleur);		
		barre.setMaximum(100);
		barre.setMinimum(caract.getMin());
        barre.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        barre.setPreferredSize(new Dimension(250,20));
        label = new JLabel();
        
        label.setText(caract.getNomComplet() + " : " + valeur + "/" + caract.getMax());
        label.setPreferredSize(new Dimension(150,20));
        
        this.add(barre);
        this.add(label);
		
	}

	/**
	 * Lance le chargement de la barre.
	 */
	public void lanceChargement() {
		barre.lanceChargement();
	}
}
