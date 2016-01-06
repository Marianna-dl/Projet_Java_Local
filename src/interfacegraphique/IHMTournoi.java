package interfacegraphique;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.Timer;

import interfacegraphique.interfacetournoi.ControleJPanel;
import interfacegraphique.interfacetournoi.FenetreCreationPotion;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import serveur.vuelement.VueElement;

/**
 * Interface graphique pour le tournoi :
 * rajoute les fonctionnalites d'admin a l'IHM de base.
 */
public class IHMTournoi extends IHM {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Mot de passe saisi.
	 */
	private String motDePasse;

	/**
	 * Vrai si le mot de passe saisi est le bon.
	 */
	private boolean motDePasseOK = false;

	/**
	 * Panneau de controle.
	 */
	private ControleJPanel controlePanel;

	/**
	 * Fenetre de creation de potion.
	 */
	private FenetreCreationPotion fenetrePotion;

	/**
	 * Timer indiquant le compte a rebours initial, avant le lancement de la 
	 * partie.
	 */
	private Timer timer;
	
	/**
	 * Initialise l'IHM de controle.
	 * @param port port de communication avec l'arene
	 * @param ipArene IP de communication avec l'arene
	 * @param logger gestionnaire de log
	 */
	public IHMTournoi(int port, String ipArene, LoggerProjet logger) {
		super(port, ipArene, logger);
		controlePanel = new ControleJPanel(this);
		
		// ajout de panneau de controle au panneau de gauche
		gauchePanel.add(controlePanel, BorderLayout.SOUTH);
		gauchePanel.repaint();
		gauchePanel.invalidate();
		gauchePanel.validate();	

		fenetrePotion = new FenetreCreationPotion(this);
		 
		// ajout d'un listener de clic sur l'arene permettant l'envoi de potion dynamiquement
		arenePanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (fenetrePotion != null && fenetrePotion.isVisible()) {
					fenetrePotion.setPosition(arenePanel.getPositionArene(e.getPoint()));
					if (fenetrePotion.estClicPourPoserSelectionne()) {
						fenetrePotion.lancePotion();
					}
				}
			}
		});
		
		// timer correspondant au compte a rebours affiche avant la partie
		// (5 secondes)
		timer = new Timer(5000, new ActionListener() {			
			public void actionPerformed(ActionEvent ev) {
				lancePartie();
			}
		});
				
	}	

	@Override
	public void connecte() {
		super.connecte();
		demandeMotDePasse();
	}

	/**
	 * Affiche la fenetre de demande de mot de passe
	 * Si il correspond a celui du serveur, on deverrouille le panneau de controle
	 * Sinon, on le verrouille
	 */
	public void demandeMotDePasse() {
		// initialisation de la fenetre de demande de mot de passe
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Mot de passe du serveur :");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		pass.requestFocusInWindow();
		String[] options = new String[] { "Annuler", "OK" };
		int option = JOptionPane.showOptionDialog(null, panel,
				"Veuillez entrer le mot de passe", JOptionPane.NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		
		// traitement de la reponse
		if (option == 1) {
			 // clic sur le bouton OK
			try {
				if (arene.verifieMotDePasse(pass.getPassword())) {
					this.motDePasse = "";
					for (int i = 0; i < pass.getPassword().length; i++) {
						this.motDePasse += pass.getPassword()[i];
					}
					
					motDePasseOK = true;
					deverrouilleControle();
					
				} else {
					JOptionPane.showMessageDialog(null,
							"Ce n'est pas le bon mot de passe.", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					
					verrouilleControle();
					demandeMotDePasse();
				}
			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}
	}
	
	/**
	 * Lance le compte a rebours.
	 */
	public void lanceCompteARebours() {
		if (!motDePasseOK) {
			demandeMotDePasse();
			
		} else {
			arenePanel.lanceCompteARebours();
			timer.start();
			controlePanel.getLancerPartieButton().setVisible(false);
		}		
	}
	
	/**
	 * Lance la partie.
	 */
	private void lancePartie() {
		try {
			arene.commencePartie(motDePasse);
			timer.stop();
			
			if(arene.estPartieCommencee()) {
				controlePanel.getLancerPartieButton().setVisible(false);
			}
			
		} catch (RemoteException e) {
			erreurConnexion(e);
		}		
	}
	
	/**
	 * Ejecte l'element selectionne du serveur.
	 */
	public void ejecteSelectionne() {
		if (!motDePasseOK) {
			demandeMotDePasse();			
		} else {
			if(elementSelectionne != null) {
				try {
					arene.ejectePersonnage(elementSelectionne.getRefRMI(), motDePasse);
				} catch (RemoteException e) {
					erreurConnexion(e);
				}
			}
		}		
	}

	/**
	 * Affiche la fenetre de creation de potion.
	 */
	public void afficheFenetrePotion() {
		if (!motDePasseOK) {
			demandeMotDePasse();
		} else {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int x = ((int) screenSize.getWidth() / 2 ) - (fenetrePotion.getWidth() / 2);
			int y = ((int) screenSize.getHeight() / 2 ) - (fenetrePotion.getHeight() / 2);
			Point point = new Point(x,y);
			fenetrePotion.setLocation(point);
			fenetrePotion.setVisible(true);
		}		
	}
	
	/**
	 * Lance une potion sur le serveur.
	 * @param nom nom de la potion
	 * @param ht caracteristiques de la potion
	 * @param position position de la potion
	 */
	public void lancePotion(String nom, HashMap<Caracteristique, Integer> ht, Point position) {
		if (!motDePasseOK) {
			demandeMotDePasse();
		} else {
			try {
				arene.lancePotion(new Potion(nom, "Arene", ht), position, motDePasse);
			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}
	}
	
	/**
	 * Modifie l'element selectionne dans le tableau des elements.
	 */
	@Override
	public void setElementSelectionne(VueElement<?> vue) {
		super.setElementSelectionne(vue);

		metAJourControleUI();		
	}

	/**
	 * Met a jour du panneau de controle.
	 */
	private void metAJourControleUI() {
		// MAJ selon si un element est selectionne ou non
		if(getElementSelectionne() == null) {
			controlePanel.getEjecterButton().setEnabled(false);
			controlePanel.getDetaillerButton().setEnabled(false);
			
		} else {
			controlePanel.getDetaillerButton().setEnabled(true);
			controlePanel.getEjecterButton().setEnabled(true);
		}
		
		// MAJ selon si la partie est commencee ou non
		try {
			if(arene.estPartieCommencee()) {
				controlePanel.getLancerPartieButton().setVisible(false);
			}
		} catch (RemoteException e) {
			erreurConnexion(e);
		}
	}	

	/**
	 * Verouille le panneau de controle.
	 */
	private void verrouilleControle() {
		controlePanel.verouille();
	}

	/**
	 * Deverouille le panneau de controle.
	 */
	private void deverrouilleControle() {
		controlePanel.deverouille();
		metAJourControleUI();
	}

}