package interfaceGraphique;

import interfaceGraphique.uiSimple.AreneJPanel;
import interfaceGraphique.uiSimple.FenetreClassement;
import interfaceGraphique.uiSimple.FenetreDetail;
import interfaceGraphique.uiSimple.InfosJPanel;
import interfaceGraphique.uiSimple.components.VictoryScreen;
import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import serveur.IArene;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

/**
 * Gestionnaire de l'interface graphique
 */
public class IHM extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	/**
	 * Port par defaut pour communiquer avec l'arene;
	 */
	private int port = 5099;
	/**
	 * Adresse IP de la machine hebergeant l'arene.
	 */
	private String ipArene = "localhost";

	/**
	 * Enumeration des etats de l'interface.
	 */
	enum State {
		INIT, PLAYING
	};

	/**
	 * Etat de l'interface.
	 */
	private State state = State.INIT;
	/**
	 * Serveur.
	 */
	protected IArene serveur;
	/**
	 * Thread de connexion au serveur.
	 */
	private Thread connexion = null;
	/**
	 * Vrai si erreur de connexion.
	 */
	private boolean cnxError = false;
	/**
	 * Le gestionnaire des logs
	 */
	private MyLogger myLogger;
	/**
	 * VueElement correspondant a l'element selectionnee dans le tableau
	 */
	private VueElement elementSelectionne;

	/*
	 * ELEMENTS DE L'UI
	 */
	protected AreneJPanel arenePanel;
	protected InfosJPanel infosPanel;
	protected JPanel leftPanel;

	private JLabel timerLabel;

	public static Color grisFonce = new Color(115, 115, 115);
	public static Color noir = new Color(33, 33, 33);
	public static Color grisClair = new Color(200, 200, 200);

	/**
	 * Initialise l'IHM
	 * 
	 * @param port port de communication avec l'arene
	 * @param ipArene ip de communication avec l'arene
	 * @param logger gestionnaire de log
	 */
	public IHM(int port, String ipArene, MyLogger logger) {
		myLogger = logger;
		this.port = port;
		this.ipArene = ipArene;
		initComponents();
	}

	private void initComponents() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

		int fenHeight = 2 * screenSize.height / 3;
		int fenWidth = 3 * screenSize.width / 4;

		// personnalise et positionne la fenetre par rapport a l'ecran
		setPreferredSize(new Dimension(fenWidth, fenHeight));
		setLocation(screenSize.width / 10, screenSize.height / 10);

		// cree un titre de la fenetre
		String titre = "Arene";
		setTitle(titre);

		// ajout une operation si le bouton de fermeture de la fenetre est cliquee
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initMenuBar();

		timerLabel = new TimerLabel();
		arenePanel = new AreneJPanel();

		leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(timerLabel, BorderLayout.NORTH);
		leftPanel.add(arenePanel, BorderLayout.CENTER);

		infosPanel = new InfosJPanel(this);

		JSplitPane jSplitPane = new JSplitPane();
		int dividerLocation = fenWidth / 2;
		jSplitPane.setDividerLocation(dividerLocation);
		jSplitPane.setLeftComponent(leftPanel);
		jSplitPane.setRightComponent(infosPanel);

		setVisible(true);

		getContentPane().add(jSplitPane);

		pack();

	}

	/**
	 * Initialise la JMenuBar
	 */
	private void initMenuBar() {
		// creation d'un menu Fichier avec deux options - quitter et a propos
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Fichier");
		
		// ajout d'une action pour afficher la fenetre "a propos"
		Action aboutAction = new AbstractAction("A propos") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				JOptionPane.showMessageDialog(null,
						"Arene\nInspiree des TP de L3\nRemise au goût du jout par les soins de\nClement Chaumel,\nValentin Chevalier,\n et Christophe Claustre\n", "a propos",
						JOptionPane.PLAIN_MESSAGE);
			}
		};
		fileMenu.add(aboutAction);

		// ajout d'une action pour arreter l'execution de l'interface graphique
		Action exitAction = new AbstractAction("Quitter") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		};
		
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		JMenu affichageMenu = new JMenu("Affichage");

		JCheckBoxMenuItem affichageJauge = new JCheckBoxMenuItem("Jauge de vie");
		affichageJauge.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
				arenePanel.setAffichageJauge(cb.isSelected());
			}
		});


		affichageMenu.add(affichageJauge);
		// affichageMenu.add(controleAction);
		menuBar.add(affichageMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * Methode appele a tous les tours de jeu
	 */
	public void repaint() {
		if ((state == State.INIT) || (cnxError)) {
			// affiche le message correspondant
			if (!cnxError) {
				arenePanel.afficherMessage("Connexion en cours sur le serveur Arene...");
			} else {
				arenePanel.afficherMessage("Erreur de connexion !");
			}
			
			// verifie si la connexion a ete realisee
			// isAlive (Thread)==true si on est en cours de connexion
			if ((connexion != null) && (!connexion.isAlive())) {
				// met a jour l'etat de l'arene
				state = State.PLAYING;
				// remet la connexion a null pour une autre execution
				connexion = null;
			}
		} else {
			try {
				// met a jour la liste des elements de l'arene
				List<VuePersonnage> personnages = serveur.getPersonnages();
				List<VuePersonnageDeconnecte> deconnected = serveur.getHell();
				List<VueElement> objetsEnAttente = serveur.getObjetsEnAttente();
				List<VueElement> objets = serveur.getObjets();

				infosPanel.setElements(personnages, objets, deconnected,
						objetsEnAttente);
				arenePanel.updateWorld(personnages, objets);

				// MAJ du timer
				int tempsRestant = serveur.getNbToursRestants();
				int nbTour = serveur.getNbTour();
				timerLabel.setText("Duree de la partie : "
						+ Calculs.timerToString(nbTour)
						+ "   -   Temps restant : "
						+ Calculs.timerToString(tempsRestant));

				if (!isPartieCommencee())
					arenePanel
							.afficherMessage("La partie n'a pas encore commence");

			} catch (RemoteException e) {
				erreurConnexion(e);
			}
		}

		super.repaint();
	}

	private boolean isPartieCommencee() {
		try {
			return serveur.isPartieCommencee();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Traitement a realiser lors d'une erreur de connexion
	 * @param e exception ayant entrainer l'erreur de connexion
	 * @param message message a afficher
	 */
	protected void erreurConnexion(Exception e) {
		// en cas de deconnexion ou erreur du serveur
		// remet l'etat de l'arene a jour
		state = State.INIT;
		String message = "Impossible de se connecter au serveur sur le port "
				+ port + " !\n(le serveur ne doit pas etre actif...)";
		// "Erreur de connexion !\nLe serveur ne doit plus etre actif."
		// affiche un dialog avec le message d'erreur
		JOptionPane.showMessageDialog(this,
				message + "\n\nRaison : " + e.getMessage(),
				"Erreur de connexion au serveur", JOptionPane.ERROR_MESSAGE);
		cnxError = true;
		e.printStackTrace();
		myLogger.info("IHM", "Erreur de connexion : " + e.getMessage());

	}

	/**
	 * Renvoie la vue correspondant a l'element selectionne dans l'IHM
	 * @return vue selectionnee
	 */
	public VueElement getSelected() {
		return elementSelectionne;
	}

	/**
	 * Definit la vue correspondant a l'element selectionne dans l'IHM
	 * @param vue
	 */
	public void setSelectedElement(VueElement vue) {
		this.elementSelectionne = vue;
	}

	/**
	 * Affiche la fenetre de detail de l'element selectionne positionne la
	 * fenetre au point donne
	 * @param point
	 */
	public void detaillerSelected(Point point) {
		if (getSelected() != null) {
			FenetreDetail fenetre = new FenetreDetail(getSelected());
			if (point == null) {
				// Centrage de la fenetre
				Toolkit kit = Toolkit.getDefaultToolkit();
				Dimension screenSize = kit.getScreenSize();
				int x = ((int) screenSize.getWidth() / 2)
						- (fenetre.getWidth() / 2);
				int y = ((int) screenSize.getHeight() / 2)
						- (fenetre.getHeight() / 2);
				point = new Point(x, y);
			}
			fenetre.setLocation(point);
			fenetre.setVisible(true);
			fenetre.go();
			fenetre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	/**
	 * Lance une connexion au serveur dans un thread separe
	 */
	public void connect() {
		connexion = new Thread() {
			public void run() {
				try {
					serveur = (IArene) Naming.lookup("rmi://" + ipArene + ":"
							+ port + "/Arene");
				} catch (Exception e) {
					erreurConnexion(e);
				}
			}
		};
		connexion.start();
	}

	/**
	 * JLabel pour afficher le timer
	 */
	private class TimerLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public TimerLabel() {
			super(" ");
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setVerticalAlignment(JLabel.CENTER);
			this.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
			this.setForeground(grisFonce);
			this.setBackground(grisClair);
			this.setOpaque(true);
			this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
					grisFonce));
			this.setPreferredSize(new Dimension(0, 50));
		}
	}

	/**
	 * Recharge l'IHM toutes les 0,5 secondes
	 */
	@Override
	public void run() {
		try {
			while (state == State.INIT || !serveur.isPartieFinieRMI()) {
				repaint();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (serveur.isPartieFinieRMI()) {
				finDePartie();
			}
		} catch (RemoteException e) {
			erreurConnexion(e);
		}
	}

	private void finDePartie() {
		try {
			List<VuePersonnage> classement = serveur.getClassementVuesRMI();
			new FenetreClassement(classement);

			this.setGlassPane(new VictoryScreen(serveur.getVueGagnant()));
			((JPanel) this.getGlassPane()).setOpaque(false);
			((JPanel) this.getGlassPane()).setVisible(true);
		} catch (RemoteException e) {
			erreurConnexion(e);
		}
	}

	/**
	 * Lance l'actualisation automatique de l'IHM
	 */
	public void start() {
		new Thread(this).start();
	}

}
