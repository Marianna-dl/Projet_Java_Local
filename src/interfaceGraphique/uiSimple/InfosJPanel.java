
package interfaceGraphique.uiSimple;

import interfaceGraphique.IHM;
import interfaceGraphique.tableModel.ObjetTableModel;
import interfaceGraphique.tableModel.PersonnageTableModel;
import interfaceGraphique.tableRenderer.HeaderRenderer;
import interfaceGraphique.tableRenderer.NormalRenderer;
import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

/**
 * Panneau contenant les tableaux des elements de la partie
 */
public class InfosJPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;


	/**
	 * Hauteur du header des tableaux
	 */
	protected static final int HEADER_HEIGHT = 35;

    	
    /**
     * IHM a laquelle appartient ce panneau
     */
	private IHM ihm;
	
	/**
	 * Modele de la table des personnages
	 */
	private PersonnageTableModel modelTablePersos;
	/**
	 * Modele de la table des objets
	 */
	private ObjetTableModel modelTableObjets;
	
	/*
	 * ELEMENTS DE L'UI
	 */
	private JScrollPane jScrollPaneObjets;
    private JScrollPane jScrollPanePersos;
    private JSplitPane jSplitPane;
    private JTable jTableObjets;
    private JTable jTablePersos;
	
	private JPopupMenu rightClicMenu;
	private JMenuItem detail;
    
    

    public InfosJPanel(IHM ihm) {    
    	this.ihm = ihm;    	
    	initComponents();    	
    }
    
    /**
     * Initialise les composant du panneau
     */
    private void initComponents() {
    	
    	setPreferredSize(new Dimension(800,600));
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        // initialisation des tables
        initTablePersos();        
        initTableObjets();
        
        // initialisation du menu de clic droit
        initMenuClickDroit();

        // creation et ajout du listener de clic sur les tables
        MouseListener listener = new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				clickOnTable(e);
			}		
			
		};
        jTablePersos.addMouseListener(listener); 
        jTableObjets.addMouseListener(listener);
		
		// ajout des composants
        jSplitPane = new JSplitPane();
        jSplitPane.setDividerLocation(350);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        
        jSplitPane.setTopComponent(jScrollPanePersos);
        jSplitPane.setBottomComponent(jScrollPaneObjets);        
        
        add(jSplitPane);
        
	}

    /**
     * Initialise le menu du clic droit
     */
    private void initMenuClickDroit() {
        detail = new JMenuItem();
        rightClicMenu = new JPopupMenu();
        detail.setText("Afficher la vue detaillee");
        rightClicMenu.add(detail);
        detail.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ihm.getElementSelectionne() != null) {
					ihm.detailleSelectionne(MouseInfo.getPointerInfo().getLocation()); 
				}
			}
		});
	}

	/**
     * Initialise l'UI de la table des personnages
     */
	private void initTablePersos() {
		jTablePersos = new JTable();

        // mise en place du model
        modelTablePersos = new PersonnageTableModel();         
        jTablePersos.setModel(modelTablePersos);
        
        // ajustement de la taille des colonnes
        for (int i = 0; i < modelTablePersos.getColumnCount(); i++) {
        	int width = modelTablePersos.getColumnWidth(i);
        	if (width != 0) {
        		jTablePersos.getColumnModel().getColumn(i).setMaxWidth(width);
        		jTablePersos.getColumnModel().getColumn(i).setPreferredWidth(width);
        	}
        }
        
        
        jTablePersos.setDefaultRenderer(Object.class, new NormalRenderer(IHM.grisClair, IHM.noir));
        jTablePersos.setDefaultRenderer(Integer.class, new NormalRenderer(IHM.grisClair, IHM.noir));              
        
        jTablePersos.setIntercellSpacing(new Dimension(0, 0));
        jTablePersos.setRowHeight(35);
		
        jTablePersos.setTableHeader(new JTableHeader(jTablePersos.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = HEADER_HEIGHT;
				return d;
			}
		});
		
        jTablePersos.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        
        
        jScrollPanePersos = new JScrollPane();
        jScrollPanePersos.getViewport().setBackground(new Color (115,115,115));
        jScrollPanePersos.setBorder(BorderFactory.createTitledBorder(
        		null, 
        		"Personnages", 
        		TitledBorder.CENTER, 
        		TitledBorder.DEFAULT_POSITION, 
        		new Font("Helvetica Neue", 0, 14), 
        		new Color(0, 0, 0)));
        
        jScrollPanePersos.setViewportView(jTablePersos);
	}


    /**
     * Initialise l'UI de la table des objets
     */
	private void initTableObjets() {
		jTableObjets = new JTable();
        
        // mise en place du model
        modelTableObjets = new ObjetTableModel();         
        jTableObjets.setModel(modelTableObjets);
        
        // ajustement de la taille des colonnes
        for (int i = 0; i < modelTableObjets.getColumnCount(); i++) {
        	int width = modelTableObjets.getColumnWidth(i);
        	if (width != 0) {
        		jTableObjets.getColumnModel().getColumn(i).setMaxWidth(width);
        		jTableObjets.getColumnModel().getColumn(i).setPreferredWidth(width);
        	}
        }
                
        
        jTableObjets.setDefaultRenderer(Object.class, new NormalRenderer(IHM.noir, IHM.grisClair));
        jTableObjets.setDefaultRenderer(Integer.class, new NormalRenderer(IHM.noir, IHM.grisClair));              
        
        jTableObjets.setIntercellSpacing(new Dimension(0, 0));
        jTableObjets.setRowHeight(35);
		
        jTableObjets.setTableHeader(new JTableHeader(jTableObjets.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = HEADER_HEIGHT;
				return d;
			}
		});
		
        jTableObjets.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jScrollPaneObjets = new JScrollPane();
        jScrollPaneObjets.getViewport().setBackground(IHM.grisFonce);
        jScrollPaneObjets.setBorder(BorderFactory.createTitledBorder(
        		null, 
        		"Objets", 
        		TitledBorder.CENTER, 
        		TitledBorder.DEFAULT_POSITION, 
        		new Font("Helvetica Neue", 0, 14), 
        		new Color(0, 0, 0)));
        
        jScrollPaneObjets.setViewportView(jTableObjets);
	}


	/**
	 * Traitement a realiser lors du clic sur une table
	 * @param e
	 */
	private void clickOnTable(MouseEvent e) {
		// vue clique
		VueElement newSelect = null;
		// vue deja selectionne
		VueElement prevSelect = ihm.getElementSelectionne();
		
		// recuperation de la vue clique
		JTable table = ((JTable) e.getSource());
		int selectedLine = table.rowAtPoint(e.getPoint());
		if (selectedLine != -1) {
			if (table.getModel() instanceof PersonnageTableModel) {
				newSelect = ((PersonnageTableModel) table.getModel()).getVue(selectedLine);
			} else {
				newSelect = ((ObjetTableModel) table.getModel()).getVue(selectedLine);
			}
		}
		// selection dans l'ihm de la vue clique 
		ihm.setElementSelectionne(newSelect);
		
		int buttonDown = e.getButton();
		
		// clic gauche
		if (buttonDown == MouseEvent.BUTTON1) {
			// si selection de l'element deja selectionne,
			// on le deselectionne dans l'ihm
			if(prevSelect != null && newSelect != null && newSelect.getRefRMI() == prevSelect.getRefRMI())
				ihm.setElementSelectionne(null);
			
		} else if(buttonDown == MouseEvent.BUTTON3) {
			// si clic droit, on affiche le menu contextuel
	    	rightClicMenu.show(table, e.getX(), e.getY());
	    }
	}

	/**
	 * Defini les elements de la partie
	 * @param persos personnage present dans l'arene
	 * @param objets objets presents dans l'arene
	 * @param persosDeconnected personnages deconnectes de l'arene
	 * @param objetsEnAttente objets en attente de rentrer sur l'arene
	 */
	public void setElements(List<VuePersonnage> persos, List<VueElement> objets, 
			List<VuePersonnageDeconnecte> persosDeconnected, List<VueElement> objetsEnAttente) {
				
		// comparator permettant le tri des vueElement selon leur refRMI
		Comparator<VueElement> vueComparator = new Comparator<VueElement>() {
			@Override
			public int compare(VueElement v1, VueElement v2) {
				return v1.getRefRMI() - v2.getRefRMI();
			}
		};
		
		// tri des objets et des personnages
		Collections.sort(persos, vueComparator);
		Collections.sort(objets, vueComparator);
		
		// personnages deconnectes
		Comparator<VuePersonnageDeconnecte> deconnectedComparator = new Comparator<VuePersonnageDeconnecte>() {
			@Override
			public int compare(VuePersonnageDeconnecte v1, VuePersonnageDeconnecte v2) {
				Integer tour1, tour2;
				tour1 = v1.getTourDeconnexion();
				tour2 = v2.getTourDeconnexion();
				return tour2.compareTo(tour1);
			}			
		};
		Collections.sort(persosDeconnected, deconnectedComparator);
		
		if (ihm.getElementSelectionne() != null) {
			/* Recherche de l'element selectionne */
			for (VuePersonnage vp : persos) {
				if (vp.getRefRMI() == ihm.getElementSelectionne().getRefRMI())
					vp.setSelected(true);					
			}
			for (VueElement ve : objets) {
				if (ve.getRefRMI() == ihm.getElementSelectionne().getRefRMI())
					ve.setSelected(true);					
			}
			for (VueElement ve : objetsEnAttente) {
				if (ve.getRefRMI() == ihm.getElementSelectionne().getRefRMI())
					ve.setSelected(true);					
			}
			for (VuePersonnageDeconnecte vpd : persosDeconnected) {
				if (vpd.getRefRMI() == ihm.getElementSelectionne().getRefRMI())
					vpd.setSelected(true);					
			}
		}		
		
    	modelTablePersos.setVues(persos);
    	modelTablePersos.setDeconnectes(persosDeconnected);
    	modelTablePersos.fireTableDataChanged();
    	
    	modelTableObjets.setVues(objets);
    	modelTableObjets.setEnAttente(objetsEnAttente);
    	modelTableObjets.fireTableDataChanged();    	
    }              
}
