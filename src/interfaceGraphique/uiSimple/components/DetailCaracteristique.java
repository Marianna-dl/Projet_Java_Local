package interfaceGraphique.uiSimple.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import serveur.element.Caracteristique;

public class DetailCaracteristique extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Barre de la caracteristique
	 */
	private Barre barre;
	/**
	 * Label de la caracteristique
	 */
	private JLabel label;
	

	public DetailCaracteristique(Caracteristique caract, int valeur, Color color) {
		
		Color couleur = color;
		int valeurUtilisee = valeur; // cette valeur sert a ramener toutes les valeurs sur 100 pour que les barres chargent a la meme vitesse
		switch(caract){
			case FORCE : couleur = Color.RED; 
					     break;
			case VIE : couleur = Color.GREEN; 
					   break;
			case INITIATIVE : couleur = Color.CYAN;
							  valeurUtilisee = valeur / 10; // on ramene a 100
							  break;

		}
		
		barre = new Barre(valeurUtilisee, couleur);		
		barre.setMaximum(100);
		barre.setMinimum(caract.getMin());
        barre.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        barre.setPreferredSize(new Dimension(250,20));
        label = new JLabel();
        
        label.setText(caract.getNomComplet()+" : " + valeur + "/"+caract.getMax());
        label.setPreferredSize(new Dimension(150,20));
        
        this.add(barre);
        this.add(label);
		
	}

	public void go() {
		barre.smoothLoad();
	}
}
