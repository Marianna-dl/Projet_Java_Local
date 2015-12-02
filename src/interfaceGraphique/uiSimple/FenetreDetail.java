package interfaceGraphique.uiSimple;

import interfaceGraphique.uiSimple.components.DetailCaracteristique;
import interfaceGraphique.uiSimple.components.Portrait;
import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import modele.Caracteristique;

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

public class FenetreDetail extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Vue de l'element a detailler
	 */
	private VueElement vue;
	
	/**
	 * Liste des panels affichant les differentes caracteristiques
	 */
	private List<DetailCaracteristique> caractPanels;

	
	public FenetreDetail(VueElement vue) {
    	this.vue = vue;
        initComponents();
    }
	
	/**
	 * Lance le chargement des jauges
	 */
	public void go() {
		for (DetailCaracteristique detail : caractPanels) {
			detail.go();
		}
    }
	
	/**
	 * Initialise les composants
	 */
	private void initComponents() {

    	setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());


        JLabel nom = new JLabel();
        nom.setPreferredSize(new Dimension(250, 40));
        nom.setFont(new Font("Tahoma", 1, 36));
        nom.setText(vue.getNom());

        JLabel groupe = new JLabel();
        groupe.setPreferredSize(new Dimension(250, 40));
        groupe.setFont(new Font("Tahoma", 1, 36));
        groupe.setText(vue.getGroupe());
        

        JPanel panelNomGroupe = new JPanel();
        panelNomGroupe.setPreferredSize(new Dimension(250, 80));
        panelNomGroupe.add(nom);
        panelNomGroupe.add(groupe);            

		
        Portrait portrait = new Portrait(vue.getColor(), vue instanceof VuePersonnage);
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
			DetailCaracteristique caractPanel = new DetailCaracteristique(caract, vue.getCaract(caract), Color.BLUE);
			caractPanels.add(caractPanel);
			panelCaracts.add(caractPanel);
		}	
		
		this.add(panelCaracts, BorderLayout.CENTER);
		
		
		pack();
	}
}
