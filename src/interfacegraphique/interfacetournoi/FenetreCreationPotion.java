package interfacegraphique.interfacetournoi;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import interfacegraphique.IHMTournoi;
import interfacegraphique.interfacetournoi.components.SaisieCaracteristique;
import interfacegraphique.interfacetournoi.components.SaisiePosition;
import interfacegraphique.interfacetournoi.exceptionSaisie.CaractNonValideException;
import interfacegraphique.interfacetournoi.exceptionSaisie.NomNonValideException;
import interfacegraphique.interfacetournoi.exceptionSaisie.PositionNonValideException;
import serveur.element.Caracteristique;
import utilitaires.Constantes;

/**
 * Fenetre permettant de creer une potion.
 *
 */
public class FenetreCreationPotion extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * TextField permettant d'entrer le nom de la potion.
	 */
	private JTextField valueNom;
	
	/**
	 * Liste des panels de saisie des caracteristiques.
	 */
	private List<SaisieCaracteristique> caractPanels;
	
	/**
	 * Panel de saisie de position.
	 */
	private SaisiePosition positionPanel;

	/**
	 * Panel contenant le bouton d'ajout de potion.
	 */
	private JPanel panelBouton;
	
	/**
	 * Bouton pour lancer la potion.
	 */
	private JButton lancePotion;	

	/**
	 * Checkbox a cocher pour pouvoir poser la potion sur l'arene en cliquant.
	 */
	private JCheckBox clicPourPoser;

	/**
	 * IHM de tournoi, contient les methodes de communication avec le serveur.
	 */
	private IHMTournoi ihmTournoi;

	/**
	 * Cree une fenetre de creation de potion.
	 * @param ihm IHM de tournoi
	 */
    public FenetreCreationPotion(IHMTournoi ihmControle) {
    	this.ihmTournoi = ihmControle;
        initComponents();
    }
    
    /**
     * Initialise les composants de la fenetre.
     */
    private void initComponents() {
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(460, 425));
        setResizable(false);
        setAlwaysOnTop(true);
        
        // grid layout de 1 colonne et de nbCaract() + 4 lignes (nom, position, bouton)
        getContentPane().setLayout(new GridLayout(Caracteristique.nbCaracts() + 3, 1, 0, 0));        

    	// nom
    	JPanel panelNom = new JPanel();
    	
    	JLabel labelNom = new JLabel("Nom de la potion");
    	panelNom.add(labelNom);    	

    	valueNom = new JTextField();
    	valueNom.setPreferredSize(new Dimension(150,28));
    	panelNom.add(valueNom);
    	
        getContentPane().add(panelNom); 

        // caracteristiques
        caractPanels = new ArrayList<SaisieCaracteristique>();
        
        for (Caracteristique c : Caracteristique.values()) {
    		caractPanels.add(new SaisieCaracteristique(c));
        }           
        
        for (SaisieCaracteristique cPanel : caractPanels)
            getContentPane().add(cPanel);       

        // position
        positionPanel = new SaisiePosition();          
        getContentPane().add(positionPanel);

        // bouton
        panelBouton = new JPanel();
        clicPourPoser = new JCheckBox();
        lancePotion = new JButton(); 
        

        clicPourPoser.setText("Cliquer sur l'arene pour poser la potion");
        clicPourPoser.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				
				if(cb.isSelected()) {
					positionPanel.desactiveAleatoire();
				}
			}
		});
        
        panelBouton.add(clicPourPoser);
        
        lancePotion.setText("Lancer la potion");
        panelBouton.add(lancePotion);
        lancePotion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				lancePotion();
			}
		});

        getContentPane().add(panelBouton);

        pack();
    }
    
    /**
     * Test si les valeurs saisies sont valides.
     * Si oui, lance la potion correspondante,
     * si non, affiche un message d'erreur et ne lance pas la potion.
     */
	public void lancePotion() {
		List<String> erreurMessage = new ArrayList<String>();
		Point position = null;
		String nom = null;
		HashMap<Caracteristique, Integer> caracts = null;
		boolean validValues = true;
		
		// gestion des erreurs de saisie
		try {
			nom = getNom();
			
		} catch (NomNonValideException e) {
			validValues = false;
			erreurMessage.add("Le nom saisi est invalide.");
		}

		try {
			caracts = getCaracts();
			
		} catch (CaractNonValideException e) {
			validValues = false;
			erreurMessage.add("Les caracteristiques suivantes ne sont pas valides : <br>"
					+ e.afficheCaracts());					
		}
		
		try {
			position = getPosition();	
			
		} catch (PositionNonValideException e) {
			validValues = false;
			erreurMessage.add("La position saisie est invalide. Contraintes : " + 
					Constantes.XMIN_ARENE + " <= X <= " + Constantes.XMAX_ARENE + ", " +
					Constantes.YMIN_ARENE + " <= X <= " + Constantes.YMAX_ARENE);
		}
		
		if (validValues) {
			ihmTournoi.lancePotion(nom, caracts, position);
		} else {
			afficheMessageErreur(erreurMessage);
		}
	}


	/**
	 * Affiche une liste de messages d'erreur dans une JOptionPane.
	 * @param messages messages d'erreur
	 */
	private void afficheMessageErreur(List<String> messages) {
		String s = "<html><body><div width='300px' align='center'>";
		
		for (String msg : messages) {
			s += "<p>" + msg + "</p><br>";
		}
		
		JOptionPane.showMessageDialog(this, s, "Erreur de saisie", JOptionPane.ERROR_MESSAGE); 
	}
    
	/**
	 * Recupere le nom saisi.
	 * Declenche une exception si le nom n'est pas valide.
	 * @return nom nom saisi
	 * @throws NomNonValideException
	 */
    public String getNom() throws NomNonValideException {
		String nom = valueNom.getText();
		
		if (nom.equals("")) {
			throw new NomNonValideException();
		}
		
    	return nom;
	}   

    /**
     * Recupere les caracteristique saisies.
	 * Declenche une exception si au moins une caracteristique n'est pas valide.
     * @return map caracteristiques/valeurs
     * @throws CaractNonValideException
     */
    public HashMap<Caracteristique, Integer> getCaracts() throws CaractNonValideException {
    	HashMap<Caracteristique, Integer> res = new HashMap<Caracteristique, Integer>();
    	List<Caracteristique> listErreur = new ArrayList<Caracteristique>();
    	boolean error = false;
    	
    	for (SaisieCaracteristique cPanel : caractPanels) {
    		try {
    			res.put(cPanel.getCaracteristique(), cPanel.getValeur());
    			
    		} catch (NumberFormatException e) {
    			listErreur.add(cPanel.getCaracteristique());
    			error = true;
    		}
    	}
    	
    	if (error) {
    		throw new CaractNonValideException(listErreur);
    	}
    	
    	return res;
    }    


    /**
     * Recupere la position saisie.
	 * Declenche une exception si la position n'est pas valide.
     * @return point correspondant a la position saisie
     * @throws PositionNonValideException
     */
    private Point getPosition() throws PositionNonValideException {
    	return positionPanel.getPosition();
    }
    
    public void setPosition(Point p) {
    	positionPanel.setPosition(p);
    }
    
    /**
     * Teste si la checkbox permettant de poser la potion en cliquant sur 
     * l'arene est selectionnee.
     * @return vrai si la checkbox est selectionnee
     */
    public boolean estClicPourPoserSelectionne() {
    	return clicPourPoser.isSelected();
    }
    
    
}
