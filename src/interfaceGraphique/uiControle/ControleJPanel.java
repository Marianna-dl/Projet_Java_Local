package interfaceGraphique.uiControle;

import interfaceGraphique.IHM;
import interfaceGraphique.IHMControle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControleJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton kickerButton;
	private JButton detaillerButton;
	private JButton lancerPartieButton;
	private JButton ajouterObjetButton;
	private JButton envoyerObjetButton;
	private JButton deverrouillerButton;

	private final IHMControle ihmControle;


	
	public ControleJPanel(IHMControle ihm){
		this.ihmControle = ihm;	

		this.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, IHM.GRIS_FONCE));

		this.setBackground(IHM.GRIS_CLAIR);
		this.setOpaque(true);
		
		kickerButton = new JButton("Renvoyer le joueur");
		detaillerButton = new JButton("Vue detaillee");
		lancerPartieButton = new JButton("Lancer la partie");
		ajouterObjetButton = new JButton("Ajouter un objet");
		envoyerObjetButton = new JButton("Envoyer l'objet");
		deverrouillerButton = new JButton("Déverouiller");	
		
		/*
		 * Ajout des listeners sur chacun des boutons
		 */
		lancerPartieButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmControle.lancerCompteARebours();	
            }
        });		
		
		kickerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmControle.renvoyerSelected();
            }
        });
		kickerButton.setEnabled(false);
		
		detaillerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmControle.detaillerSelected(null);
            }
        });
		detaillerButton.setEnabled(false);
		
		ajouterObjetButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				ihmControle.afficherFenetrePotion();
			}			
		});
		
		envoyerObjetButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				ihmControle.envoyerPotionSelected();
			}			
		});
		envoyerObjetButton.setEnabled(false);
		
		deverrouillerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihmControle.demanderMotDePasse();				
			}
		});
				
		this.add(detaillerButton);
		this.add(kickerButton);
		this.add(lancerPartieButton);
		this.add(ajouterObjetButton);
		this.add(envoyerObjetButton);
		this.add(deverrouillerButton);
		
		lock();
		
		this.setVisible(true);
		
	}

	/**
	 * Verrouille le panneau de controle
	 */
	public void lock() {
		detaillerButton.setVisible(false);
		kickerButton.setVisible(false);
		lancerPartieButton.setVisible(false);
		ajouterObjetButton.setVisible(false);
		envoyerObjetButton.setVisible(false);
		
		deverrouillerButton.setVisible(true);		
	}

	/**
	 * Deverrouille le panneau de controle
	 */
	public void unlock() {
		detaillerButton.setVisible(true);
		kickerButton.setVisible(true);
		lancerPartieButton.setVisible(true);
		ajouterObjetButton.setVisible(true);
		envoyerObjetButton.setVisible(true);
		
		deverrouillerButton.setVisible(false);		
	}
	
	/*
	 * Getter sur les boutons
	 */
	public JButton getKickerButton() {
		return kickerButton;
	}

	public JButton getLancerPartieButton() {
		return lancerPartieButton;
	}

	public JButton getDetaillerButton() {
		return detaillerButton;
	}

	public JButton getEnvoyerPotionButton() {
		return envoyerObjetButton;
	}


}
