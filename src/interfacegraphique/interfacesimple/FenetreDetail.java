package interfacegraphique.interfacesimple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import interfacegraphique.interfacesimple.components.DetailCaracteristique;
import interfacegraphique.interfacesimple.components.Portrait;
import serveur.element.Caracteristique;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;

/**
 * Gere la fenetre d'affichage des details d'un element (personnage ou potion). 
 *
 */
public class FenetreDetail extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Vue de l'element a detailler.
	 */
	private VueElement<?> vue;
	
	/**
	 * Liste des panels affichant les differentes caracteristiques.
	 */
	private List<DetailCaracteristique> caractPanels;

	/**
	 * Cree une fenetre d'affichage des details d'un element.
	 * @param vue vue de l'element
	 */
	public FenetreDetail(VueElement<?> vue) {
    	this.vue = vue;
        initComposants();
    }
	
	/**
	 * Initialise les composants.
	 */
	private void initComposants() {
		setResizable(false);
	    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
	
	
	    JLabel nom = new JLabel();
	    nom.setPreferredSize(new Dimension(250, 40));
	    nom.setFont(new Font("Tahoma", 1, 36));
	    nom.setText(vue.getElement().getNom());
	
	    JLabel groupe = new JLabel();
	    groupe.setPreferredSize(new Dimension(250, 40));
	    groupe.setFont(new Font("Tahoma", 1, 36));
	    groupe.setText(vue.getElement().getGroupe());
	    
	
	    JPanel panelNomGroupe = new JPanel();
	    panelNomGroupe.setPreferredSize(new Dimension(250, 80));
	    panelNomGroupe.add(nom);
	    panelNomGroupe.add(groupe);            
	
		
	    Portrait portrait = new Portrait(vue.getCouleur(), vue instanceof VuePersonnage);
	    portrait.setBackground(new Color(255, 255, 255));
	    portrait.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, true));
	    portrait.setPreferredSize(new Dimension(150, 150));
	
	    JPanel jPanelNorth = new JPanel();
	    jPanelNorth.setLayout(new BoxLayout(jPanelNorth, BoxLayout.LINE_AXIS));
	
	    jPanelNorth.add(panelNomGroupe);
	    jPanelNorth.add(portrait);
	    
	    
	    this.add(jPanelNorth, BorderLayout.NORTH);
		
		JPanel panelCaracts = new JPanel(new GridLayout(Caracteristique.nbCaracts(), 1));
		caractPanels =  new ArrayList<DetailCaracteristique>();
		for (Caracteristique caract : Caracteristique.values()) {
			DetailCaracteristique caractPanel = new DetailCaracteristique(caract, vue.getElement().getCaract(caract));
			caractPanels.add(caractPanel);
			panelCaracts.add(caractPanel);
		}	
		
		this.add(panelCaracts, BorderLayout.CENTER);
		
		pack();
	}

	/**
	 * Lance le chargement des jauges.
	 */
	public void lanceChargementJauges() {
		for (DetailCaracteristique detail : caractPanels) {
			detail.lanceChargement();
		}
    }
}
