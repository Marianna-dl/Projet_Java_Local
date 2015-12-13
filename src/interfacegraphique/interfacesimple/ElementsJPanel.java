
package interfacegraphique.interfacesimple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
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

import interfacegraphique.IHM;
import interfacegraphique.tablemodel.PersonnageTableModel;
import interfacegraphique.tablemodel.PotionTableModel;
import interfacegraphique.tablerenderer.HeaderRenderer;
import interfacegraphique.tablerenderer.NormalRenderer;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;

/**
 * Panneau contenant les tableaux des elements de la partie.
 */
public class ElementsJPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Hauteur du header (premiere ligne) des tableaux.
	 */
	private static final int HEADER_HEIGHT = 35;
    	
    /**
     * IHM a laquelle appartient ce panneau.
     */
	private IHM ihm;
	
	/**
	 * Modele de la table des personnages.
	 */
	private PersonnageTableModel modelTablePersonnages;
	
	/**
	 * Modele de la table des potions.
	 */
	private PotionTableModel modelTablePotions;
	
	/**
	 * ScrollPane contenant les personnages.
	 */
	private JScrollPane jScrollPanePersonnages;

	/**
	 * ScrollPane contenant les potions.
	 */
	private JScrollPane jScrollPanePotions;
	
	/**
	 * SplitPane separant les personnages et les potions.
	 */
    private JSplitPane jSplitPane;
    
    /**
	 * Tableau des personnages.
	 */
	private JTable jTablePersonnages;

	/**
     * Tableau des potions.
     */
    private JTable jTablePotions;
    
    /**
     * Menu contextuel (clic droit). 
     */
    private JPopupMenu menuContextuel;
    
    /**
     * Permet d'afficher la vue detaillee d'un element.
     */
	private JMenuItem detail;
    
    /**
     * Cree un panel qui affiche les elements de la partie sous forme de 
     * tableaux.
     * @param ihm IHM
     */
    public ElementsJPanel(IHM ihm) {    
    	this.ihm = ihm;    	
    	initComposants();    	
    }
    
    /**
     * Initialise les composants.
     */
    private void initComposants() {
    	setPreferredSize(new Dimension(800,600));
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        // initialisation des tables
        initTablePersonnages();        
        initTablePotions();
        
        // initialisation du menu de clic droit
        initMenuClickDroit();

        // creation et ajout du listener de clic sur les tables
        MouseListener listener = new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				clicSurTable(e);
			}		
			
		};
		
        jTablePersonnages.addMouseListener(listener); 
        jTablePotions.addMouseListener(listener);
		
		// ajout des composants
        jSplitPane = new JSplitPane();
        jSplitPane.setDividerLocation(350);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        
        jSplitPane.setTopComponent(jScrollPanePersonnages);
        jSplitPane.setBottomComponent(jScrollPanePotions);        
        
        add(jSplitPane);
        
	}

    /**
     * Initialise le menu contextuel.
     */
    private void initMenuClickDroit() {
        detail = new JMenuItem();
        menuContextuel = new JPopupMenu();
        
        detail.setText("Afficher la vue detaillee");
        menuContextuel.add(detail);
        
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
     * Initialise la table des personnages.
     */
	private void initTablePersonnages() {
		jTablePersonnages = new JTable();

        // mise en place du modele
        modelTablePersonnages = new PersonnageTableModel();         
        jTablePersonnages.setModel(modelTablePersonnages);
        
        // ajustement de la taille des colonnes
        for (int i = 0; i < modelTablePersonnages.getColumnCount(); i++) {
        	int width = modelTablePersonnages.getColumnWidth(i);
        	if (width != 0) {
        		jTablePersonnages.getColumnModel().getColumn(i).setMaxWidth(width);
        		jTablePersonnages.getColumnModel().getColumn(i).setPreferredWidth(width);
        	}
        }
        
        jTablePersonnages.setDefaultRenderer(Object.class, new NormalRenderer(IHM.grisClair, IHM.noir));
        jTablePersonnages.setDefaultRenderer(Integer.class, new NormalRenderer(IHM.grisClair, IHM.noir));              
        
        jTablePersonnages.setIntercellSpacing(new Dimension(0, 0));
        jTablePersonnages.setRowHeight(35);
		
        jTablePersonnages.setTableHeader(new JTableHeader(jTablePersonnages.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = HEADER_HEIGHT;
				return d;
			}
		});
		
        jTablePersonnages.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        
        jScrollPanePersonnages = new JScrollPane();
        jScrollPanePersonnages.getViewport().setBackground(new Color (115,115,115));
        jScrollPanePersonnages.setBorder(BorderFactory.createTitledBorder(
        		null, 
        		"Personnages", 
        		TitledBorder.CENTER, 
        		TitledBorder.DEFAULT_POSITION, 
        		new Font("Helvetica Neue", 0, 14), 
        		new Color(0, 0, 0)));
        
        jScrollPanePersonnages.setViewportView(jTablePersonnages);
	}

    /**
     * Initialise la table des potions.
     */
	private void initTablePotions() {
		jTablePotions = new JTable();
        
        // mise en place du modele
        modelTablePotions = new PotionTableModel();         
        jTablePotions.setModel(modelTablePotions);
        
        // ajustement de la taille des colonnes
        for (int i = 0; i < modelTablePotions.getColumnCount(); i++) {
        	int width = modelTablePotions.getColumnWidth(i);
        	if (width != 0) {
        		jTablePotions.getColumnModel().getColumn(i).setMaxWidth(width);
        		jTablePotions.getColumnModel().getColumn(i).setPreferredWidth(width);
        	}
        }
        
        jTablePotions.setDefaultRenderer(Object.class, new NormalRenderer(IHM.noir, IHM.grisClair));
        jTablePotions.setDefaultRenderer(Integer.class, new NormalRenderer(IHM.noir, IHM.grisClair));              
        
        jTablePotions.setIntercellSpacing(new Dimension(0, 0));
        jTablePotions.setRowHeight(35);
		
        jTablePotions.setTableHeader(new JTableHeader(jTablePotions.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = HEADER_HEIGHT;
				return d;
			}
		});
		
        jTablePotions.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jScrollPanePotions = new JScrollPane();
        jScrollPanePotions.getViewport().setBackground(IHM.grisFonce);
        jScrollPanePotions.setBorder(BorderFactory.createTitledBorder(
        		null, 
        		"Potions", 
        		TitledBorder.CENTER, 
        		TitledBorder.DEFAULT_POSITION, 
        		new Font("Helvetica Neue", 0, 14), 
        		new Color(0, 0, 0)));
        
        jScrollPanePotions.setViewportView(jTablePotions);
	}


	/**
	 * Traitement a realiser lors du clic sur un tableau.
	 * @param ev evenement de la souris
	 */
	private void clicSurTable(MouseEvent ev) {
		// vue cliquee
		VueElement<?> newSelect = null;
		
		// vue deja selectionnee
		VueElement<?> prevSelect = ihm.getElementSelectionne();
		
		// recuperation de la vue clique
		JTable table = ((JTable) ev.getSource());
		int selectedLine = table.rowAtPoint(ev.getPoint());
		
		if (selectedLine != -1) {
			if (table.getModel() instanceof PersonnageTableModel) {
				newSelect = ((PersonnageTableModel) table.getModel()).getVue(selectedLine);
			} else {
				newSelect = ((PotionTableModel) table.getModel()).getVue(selectedLine);
			}
		}
		// selection dans l'ihm de la vue clique 
		ihm.setElementSelectionne(newSelect);
		
		int buttonDown = ev.getButton();
		
		// clic gauche
		if (buttonDown == MouseEvent.BUTTON1) {
			// si selection de l'element deja selectionne,
			// on le deselectionne dans l'ihm
			if(prevSelect != null && newSelect != null && newSelect.getRefRMI() == prevSelect.getRefRMI()) {
				ihm.setElementSelectionne(null);
			}
			
		} else if(buttonDown == MouseEvent.BUTTON3) {
			// si clic droit, on affiche le menu contextuel
	    	menuContextuel.show(table, ev.getX(), ev.getY());
	    }
	}

	/**
	 * Definit les elements de la partie.
	 * @param personnages personnages presents dans l'arene
	 * @param personnagesMorts personnages ayant ete connectes et maintenant 
	 * morts
	 * @param potions potions presentes dans l'arene
	 */
	public void setElements(List<VuePersonnage> personnages, 
			List<VuePersonnage> personnagesMorts, List<VuePotion> potions) {
		
		// tri des potions et des personnages (selon leur methode compareTo)
		List<VuePersonnage> personnagesTous = new ArrayList<VuePersonnage>(personnages);
		personnagesTous.addAll(personnagesMorts);

		List<VuePotion> potionsTous = new ArrayList<VuePotion>(potions);
		
		Collections.sort(personnagesTous);
		Collections.sort(potionsTous);
		
		if (ihm.getElementSelectionne() != null) {
			// recherche de l'element selectionne
			for (VuePersonnage vp : personnagesTous) {
				if (vp.getRefRMI() == ihm.getElementSelectionne().getRefRMI()) {
					vp.setSelectionne(true);
				}
			}
			
			for (VuePotion vp : potionsTous) {
				if (vp.getRefRMI() == ihm.getElementSelectionne().getRefRMI()) {
					vp.setSelectionne(true);					
				}
			}
		}
		
    	modelTablePersonnages.setVues(personnagesTous);
    	modelTablePersonnages.fireTableDataChanged();
    	
    	modelTablePotions.setVues(potionsTous);
    	modelTablePotions.fireTableDataChanged();    	
    }              
}
