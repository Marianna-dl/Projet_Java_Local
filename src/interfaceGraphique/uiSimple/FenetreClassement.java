package interfaceGraphique.uiSimple;

import interfaceGraphique.IHM;
import interfaceGraphique.tableModel.ClassementTableModel;
import interfaceGraphique.tableRenderer.HeaderRenderer;
import interfaceGraphique.tableRenderer.NormalRenderer;
import interfaceGraphique.view.VuePersonnage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class FenetreClassement extends JFrame {

	private static final long serialVersionUID = 2957443074830244549L;
	
	List<VuePersonnage> classement;

	private JTable tableClassement;

	public FenetreClassement(List<VuePersonnage> classement){
		this.classement = classement;
		initComponents();
	}

	private void initComponents() {
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

		int fenHeight = 2 * screenSize.height / 3;
		int fenWidth = 3 * screenSize.width / 8;

		// personnalise et positionne la fenetre par rapport a l'ecran
		setPreferredSize(new Dimension(fenWidth, fenHeight));
		setLocation((screenSize.width - fenWidth) / 2 , (screenSize.height - fenHeight) / 2);
		
		setTitle("Classement terminal");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		tableClassement = new JTable();
        
        // mise en place du model
        ClassementTableModel model = new ClassementTableModel(); 
        
        tableClassement.setModel(model);

        model.setVues(classement);
        
        // ajustement de la taille des colonnes
        for (int i = 0; i < model.getColumnCount(); i++){
        	int width = model.getColumnWidth(i);
        	if (width != 0){
        		tableClassement.getColumnModel().getColumn(i).setPreferredWidth(width);
        		tableClassement.getColumnModel().getColumn(i).setMinWidth(width);
        	}
        }
                
        
        tableClassement.setDefaultRenderer(Object.class, new NormalRenderer(IHM.grisClair, IHM.noir));
        tableClassement.setDefaultRenderer(Integer.class, new NormalRenderer(IHM.grisClair, IHM.noir));              
        
        tableClassement.setIntercellSpacing(new Dimension(0, 0));
        tableClassement.setRowHeight(35);
		
        tableClassement.setTableHeader(new JTableHeader(tableClassement.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = 35;
				return d;
			}
		});
		
        tableClassement.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setBackground(IHM.grisFonce);
        
        scrollPane.setViewportView(tableClassement);
     
        this.add(scrollPane);

		setVisible(true);
        this.pack();
	}
	
	

}
