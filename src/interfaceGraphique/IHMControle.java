package interfaceGraphique;

import interfaceGraphique.uiControle.ControleJPanel;
import interfaceGraphique.uiControle.FenetreCreationObjet;
import interfaceGraphique.view.VueElement;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.Timer;

import serveur.element.Caracteristique;
import utilitaires.logger.MyLogger;

/**
 * IHM de controle
 * Rajoute les fonctionnalites d'admin a l'IHM de base
 */
public class IHMControle extends IHM {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Mot de passe saisi
	 */
	private String motDePasse;

	/**
	 * Flag permettant de savoir si le mot de passe saisi est OK
	 */
	private boolean motDePasseOK = false;

	/**
	 * Panneau de controle
	 */
	private ControleJPanel controlePanel;

	/**
	 * Fenetre de creation dynamique de potion 
	 */
	private FenetreCreationObjet fenPotion;

	private Timer timer;
	
	/**
	 * Initialise l'IHM de controle
	 * @param port port de communication avec l'arene
	 * @param ipArene ip de communication avec l'arene
	 * @param mylogger gestionnaire de log
	 */
	public IHMControle(int port, String ipArene, MyLogger mylogger) {
		super(port, ipArene, mylogger);
		controlePanel = new ControleJPanel(this);
		
		// ajout de panneau de controle au panneau de gauche
		leftPanel.add(controlePanel, BorderLayout.SOUTH);
		leftPanel.repaint();
		leftPanel.invalidate();
		leftPanel.validate();	
		

		fenPotion = new FenetreCreationObjet(this);
		 
		// ajout d'un listener de clic sur l'arene permettant l'envoi de potion dynamiquement
		arenePanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (fenPotion != null && fenPotion.isVisible()){
					fenPotion.setPosition(arenePanel.getPositionArene(e.getPoint()));
					if (fenPotion.isClicToPoseSelect()){
						fenPotion.lancerObjet();
					}
				}
			}
		});
		
		// timer correspondant au compte a rebours affiche
		timer = new Timer(5000, new ActionListener(){			
			public void actionPerformed(ActionEvent arg0) {
				lancerPartie();
			}
		});
				
	}	


	@Override
	public void connect() {
		super.connect();
		demanderMotDePasse();
	}

	/**
	 * Affiche la fenetre de demande de mot de passe
	 * Si il correspond a celui du serveur, on deverrouille le panneau de controle
	 * Sinon, on le verrouille
	 */
	public void demanderMotDePasse() {
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
		if (option == 1){
			 // pressing OK button
			try {
				if (serveur.verifMotDePasse(pass.getPassword())) {
					this.motDePasse = "";
					for (int i = 0; i < pass.getPassword().length; i++) {
						this.motDePasse += pass.getPassword()[i];
					}
					motDePasseOK = true;
					unlockControle();
				} else {
					JOptionPane.showMessageDialog(null,
							"Ce n'est pas le bon mot de passe.", "Erreur",
							JOptionPane.ERROR_MESSAGE);
					lockControle();
					demanderMotDePasse();
				}
			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}
	}
	
	/**
	 * Lance le compte a rebours
	 */
	public void lancerCompteARebours(){
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			arenePanel.lancerCompteARebours();
			timer.start();
			controlePanel.getLancerPartieButton().setVisible(false);
		}		
	}
	
	/**
	 * Lance la partie
	 */
	public void lancerPartie(){
		try {
			serveur.commencerPartie(motDePasse);
			timer.stop();
			if(serveur.isPartieCommencee()){
				controlePanel.getLancerPartieButton().setVisible(false);
			}
		} catch (RemoteException e) {
			erreurConnexion(e);
		}		
	}
	
	/**
	 * Renvoie du serveur l'element selectionne
	 */
	public void renvoyerSelected(){
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			if(getSelected() != null){
				try {
					serveur.renvoyer(getSelected(), motDePasse);
				} catch (RemoteException e) {
					erreurConnexion(e);
				}
			}
		}		
	}

	/**
	 * Envoie la potion (en attente) selectionnee dans la partie
	 */
	public void envoyerPotionSelected() {
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			if (getSelected().isEnAttente()){
				try {
					serveur.lancerObjetEnAttente(getSelected().getRefRMI(), motDePasse);
				} catch (RemoteException e) {
					erreurConnexion(e);
				}
			}
		}
	}	

	/**
	 * Affiche la fenetre de creation de potion
	 */
	public void afficherFenetrePotion() {
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int x = ((int) screenSize.getWidth() / 2 ) - (fenPotion.getWidth() / 2);
			int y = ((int) screenSize.getHeight() / 2 ) - (fenPotion.getHeight() / 2);
			Point point = new Point(x,y);
			fenPotion.setLocation(point);
			fenPotion.setVisible(true);
		}		
	}
	
	/**
	 * Lance une potion sur le serveur
	 * @param nom nom de la potion
	 * @param ht caracteristiques de la potion
	 * @param position position de la potion
	 */
	public void lancerPotion(String nom, Hashtable<Caracteristique, Integer> ht, Point position){
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			try {
				serveur.ajouterPotionSecurisee(nom, ht, position, motDePasse);
			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}
	}
	
	/**
	 * Lance un tresor sur le serveur
	 * @param nom nom du tresor
	 * @param montant montant du tresor
	 * @param position position du tresor
	 */
	public void lancerTresor(String nom, int montant, Point position) {
		
		if (!motDePasseOK) {
			demanderMotDePasse();
		} else {
			try {
				serveur.ajouterTresorSecurise(nom, montant, position, motDePasse);
			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}
	}
	
	/**
	 * Definit l'element selectionne dans le tableau des elements
	 */
	public void setSelectedElement(VueElement vue){
		super.setSelectedElement(vue);

		updateControleUI();		
	}

	/**
	 * Met a jour du panneau de controle
	 */
	private void updateControleUI() {
		// MAJ selon qu'un element soit selectionne ou non
		if(getSelected() == null){
			controlePanel.getKickerButton().setEnabled(false);
			controlePanel.getDetaillerButton().setEnabled(false);
			controlePanel.getEnvoyerPotionButton().setEnabled(false);
		}else{
			controlePanel.getDetaillerButton().setEnabled(true);
			controlePanel.getKickerButton().setEnabled(true);
			if (getSelected().isEnAttente()){
				controlePanel.getEnvoyerPotionButton().setEnabled(true);
			} else {
				controlePanel.getEnvoyerPotionButton().setEnabled(false);
			}
		}
		// MAJ selon que la partie soit commencee ou non
		try {
			if(serveur.isPartieCommencee()){
				controlePanel.getLancerPartieButton().setVisible(false);
			}
		} catch (RemoteException e) {
			erreurConnexion(e);
		}
	}	

	/**
	 * Verouille le panneau de controle
	 */
	private void lockControle() {
		controlePanel.lock();
	}

	/**
	 * Deverouille le panneau de controle
	 */
	private void unlockControle() {
		controlePanel.unlock();
		updateControleUI();
	}

}