package interfaceGraphique.uiControle;


import interfaceGraphique.IHMControle;
import interfaceGraphique.uiControle.components.SaisieCaracteristique;
import interfaceGraphique.uiControle.components.SaisiePosition;
import interfaceGraphique.uiControle.exceptionSaisie.CaractNotValidException;
import interfaceGraphique.uiControle.exceptionSaisie.NomNotValidException;
import interfaceGraphique.uiControle.exceptionSaisie.PositionNotValidException;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import serveur.Arene;
import serveur.element.Caracteristique;

public class FenetreCreationObjet extends JFrame {

	private static final long serialVersionUID = 1L;
	/**
	 * Nom de l'objet
	 */
	private JTextField valueNom;
	
	/**
	 * Type de l'objet
	 */
	private ButtonGroup choixTypeObjet;
	private JRadioButton potionRadioButton;
	private JRadioButton tresorRadioButton;
	
	/**
	 * Liste des panels de caractéristique
	 */
	private List<SaisieCaracteristique> caractPanels;
	
	/**
	 * Panel de saisi de la position
	 */
	private SaisiePosition positionPanel;

	/**
	 * Bouton
	 */
	private JPanel panelBouton;
	private JButton lanceObjet;	


	private IHMControle ihmControle;

	private JCheckBox clicPourPoser;


	
    public FenetreCreationObjet(IHMControle ihmControle) {
    	this.ihmControle = ihmControle;
        initComponents();
    }
    
    
    private void initComponents() {
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(460, 425));
        setResizable(false);
        setAlwaysOnTop(true);
        // Grid layout de 1 colonne et de nbCaract - 1 (INITIATIVE) + 3 lignes (nom, type, position, bouton)
        getContentPane().setLayout(new GridLayout(Caracteristique.nbCaract() + 4, 1, 0, 0));        

    	
    	// Nom
    	JPanel panelNom = new JPanel();
    	
    	JLabel labelNom = new JLabel("Nom de l'objet");
    	panelNom.add(labelNom);    	

    	valueNom = new JTextField();
    	valueNom.setPreferredSize(new Dimension(150,28));
    	panelNom.add(valueNom);
    	
        getContentPane().add(panelNom);   
        
        choixTypeObjet = new ButtonGroup();
        potionRadioButton = new JRadioButton();
        tresorRadioButton = new JRadioButton();
        
        ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (potionRadioButton.isSelected())
					choixPotion();
				if (tresorRadioButton.isSelected())
					choixTresor();
				
			}
		};
		
		potionRadioButton.addActionListener(listener);
		tresorRadioButton.addActionListener(listener);

        choixTypeObjet.add(potionRadioButton);
        potionRadioButton.setText("Potion");
        potionRadioButton.setSelected(true);

        choixTypeObjet.add(tresorRadioButton);
        tresorRadioButton.setText("Trésor");
        
        
        JPanel panelType = new JPanel();
        JLabel labelType = new JLabel("Type de l'objet");
        panelType.add(labelType);
        panelType.add(potionRadioButton);
        panelType.add(tresorRadioButton);
        
        getContentPane().add(panelType);   

        // Caracteristiques
        caractPanels = new ArrayList<SaisieCaracteristique>();
        for (Caracteristique c : Caracteristique.values()){
        	if (!c.equals(Caracteristique.INITIATIVE))
            	caractPanels.add(new SaisieCaracteristique(c));         		
        	
        }           
        
        for (SaisieCaracteristique cPanel : caractPanels)
            getContentPane().add(cPanel);       

        // Position
        positionPanel = new SaisiePosition();          
        getContentPane().add(positionPanel);

        // Bouton
        panelBouton = new JPanel();
        clicPourPoser = new JCheckBox();
        lanceObjet = new JButton(); 
        

        clicPourPoser.setText("Cliquer sur l'arène pour poser l'objet");
        clicPourPoser.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected())
					positionPanel.disableRandom();
			}
		});
        panelBouton.add(clicPourPoser);
        
        lanceObjet.setText("Lancer l'objet");
        panelBouton.add(lanceObjet);
        lanceObjet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				lancerObjet();
			}
		});

        getContentPane().add(panelBouton);
        
        choixPotion();

        pack();
    }
    
    private void choixPotion(){
    	for (SaisieCaracteristique cPanel : caractPanels){
    		switch (cPanel.getCaracteristique()){
			case ARGENT:
    			cPanel.setEnabled(false);
				break;
			case FORCE:
    			cPanel.setEnabled(true);
				break;
			case INITIATIVE:
    			cPanel.setEnabled(false);
				break;
			case VIE:
    			cPanel.setEnabled(true);
				break;
			case VITESSE:
    			cPanel.setEnabled(true);
				break;
			default:
				break;    			
    		}
    	}
    }
    
    private void choixTresor(){
    	for (SaisieCaracteristique cPanel : caractPanels){
    		switch (cPanel.getCaracteristique()){
			case ARGENT:
    			cPanel.setEnabled(true);
				break;
			case FORCE:
    			cPanel.setEnabled(false);
				break;
			case INITIATIVE:
    			cPanel.setEnabled(false);
				break;
			case VIE:
    			cPanel.setEnabled(false);
				break;
			case VITESSE:
    			cPanel.setEnabled(false);
				break;
			default:
				break;    			
    		}
    	}
    }
    
    
    /**
     * Test si les valeurs saisies sont valides,
     * Si oui, lance la potion correspondante,
     * Si non, affiche un message d'erreur et ne lance pas la potion
     */
	public void lancerObjet() {
		List<String> erreurMessage = new ArrayList<String>();
		Point position = null;
		String nom = null;
		Hashtable<Caracteristique, Integer> caracts = null;
		int montant = 0;
		boolean validValues = true;
		
		// Gestion des erreurs de saisie
		try{
			nom = getNom();
		} catch (NomNotValidException e) {
			validValues = false;
			erreurMessage.add("Le nom saisi est invalide.");
		}

		if (potionRadioButton.isSelected()){
			try{
				caracts = getCaracts();
			} catch (CaractNotValidException e){
				validValues = false;
				erreurMessage.add("Les caractéristiques suivantes ne sont pas valides : <br>"
						+ e.afficherCaracts());					
			}
		}
		if (tresorRadioButton.isSelected()){
			try{
				montant = getMontant();
			} catch (CaractNotValidException e){
				validValues = false;
				erreurMessage.add("Les caractéristiques suivantes ne sont pas valides : <br>"
						+ e.afficherCaracts());					
			}
		}
		
		try {
			position = getPosition();			
		} catch (PositionNotValidException e) {
			validValues = false;
			erreurMessage.add("La position saisie est invalide (X et Y doivent être compris entre "+Arene.XMIN+" et "+Arene.XMAX+").");
		}
		
		if (validValues){
			if (potionRadioButton.isSelected())
				ihmControle.lancerPotion(nom, caracts, position);
			if (tresorRadioButton.isSelected())
				ihmControle.lancerTresor(nom, montant, position);
		} else {
			afficherMessageErreur(erreurMessage);
		}
	}


	/**
	 * Affiche une liste de messages d'erreur dans une JOptionPane
	 * @param messages
	 */
	private void afficherMessageErreur(List<String> messages) {
		String s = "<html><body><div width='300px' align='center'>";
		for (String message : messages){
			s+="<p>"+message+"</p><br>";
		}
		JOptionPane.showMessageDialog(this, s, "Erreur de saisie", JOptionPane.ERROR_MESSAGE); 
	}
    
	/**
	 * Recupere le nom saisi
	 * Déclenche une exception si le nom n'est pas valide
	 * @return nom 
	 * @throws NomNotValidException
	 */
    public String getNom() throws NomNotValidException {
		String nom = valueNom.getText();
		if (nom.equals(""))
			throw new NomNotValidException();
    	return nom;
	}   

    /**
     * Récupere les caractéristique saisies
	 * Déclenche une exception si au moins une caractéristique n'est pas valide
     * @return Hashtable de Caracteristique -> valeur
     * @throws CaractNotValidException
     */
    public Hashtable<Caracteristique, Integer> getCaracts() throws CaractNotValidException {
    	Hashtable<Caracteristique, Integer> ht = new Hashtable<Caracteristique, Integer>();
    	List<Caracteristique> listErreur = new ArrayList<Caracteristique>();
    	boolean error = false;
    	for (SaisieCaracteristique cPanel : caractPanels)	{
    		try {
    			ht.put(cPanel.getCaracteristique(), cPanel.getValue());
    		} catch (NumberFormatException e) {
    			listErreur.add(cPanel.getCaracteristique());
    			error = true;
    		}
    	}
    	if (error)
    		throw new CaractNotValidException(listErreur);
    	return ht;
    }    


	private int getMontant() throws CaractNotValidException  {
		int montant = 0;
    	List<Caracteristique> listErreur = new ArrayList<Caracteristique>();
    	boolean error = false;
    	
		for (SaisieCaracteristique cPanel : caractPanels)	{
    		if (cPanel.getCaracteristique().equals(Caracteristique.ARGENT)){
    			try {
        			montant = cPanel.getValue();
        		} catch (NumberFormatException e) {
        			listErreur.add(cPanel.getCaracteristique());
        			error = true;
        		}
    		}			
    	}
		if (error)
    		throw new CaractNotValidException(listErreur);
    	return montant;
	}
    
    /**
     * Récupere la position saisie
	 * Déclenche une exception si la position n'est pas valide
     * @return Point correspondant à la position saisie
     * @throws PositionNotValidException
     */
    private Point getPosition() throws PositionNotValidException{
    	return positionPanel.getPosition();
    }
    
    /**
     * Défini une nouvelle position
     * @param p
     */
    public void setPosition(Point p){
    	positionPanel.setPosition(p);
    }
    
    public boolean isClicToPoseSelect(){
    	return clicPourPoser.isSelected();
    }
    
    
}
