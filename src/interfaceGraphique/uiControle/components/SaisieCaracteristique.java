package interfaceGraphique.uiControle.components;

import java.awt.Dimension;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import serveur.element.Caracteristique;
import utilitaires.Calculs;

/**
 * Panel permettant la saisie d'une caracteristique donnee
 *
 */
public class SaisieCaracteristique extends JPanel{

	private static final long serialVersionUID = 1L;		

	/**
	 * Champ de saisie de la caracteristique
	 */
	private JTextField valueCaract;
	/**
	 * CheckBox permettant la selection de l'aleatoire
	 */
	private JCheckBox randomCaract;
	/**
	 * ComboBox permettant de selectionner l'interval pour l'aleatoire
	 */
	private JComboBox randomRangeCaract;

	/**
	 * Caracteristique a laquelle correspond le panel
	 */
	private Caracteristique caracteristique;

	private JLabel labelCaract;

	private JLabel maxCaract;


	public SaisieCaracteristique(Caracteristique c){			
		this.caracteristique = c;
		initComponents();	        
	}

	/**
	 * Renvoi la Caracteristique
	 * @return caracteristique correspondant au panel
	 */
	public Caracteristique getCaracteristique() {
		return caracteristique;
	}

	/**
	 * Permet de recuperer la valeur saisie ou generee aleatoirement
	 * @return valeur 
	 */
	public int getValue() {
		int value;
		if (!isEnabled()){
			return 0;
		}
		if (randomCaract.isSelected()){
			value = Calculs.randomNumber(getMinRange(), getMaxRange());
		} else {
			value = Integer.parseInt(valueCaract.getText());				
		}
		return value;
	}

	/**
	 * Initialise les composant du panel
	 */
	private void initComponents() {
		labelCaract = new JLabel();
		valueCaract = new JTextField();
		maxCaract = new JLabel();
		randomCaract = new JCheckBox();
		randomRangeCaract = new JComboBox();

		labelCaract.setHorizontalAlignment(SwingConstants.CENTER);
		labelCaract.setText(caracteristique.name());
		labelCaract.setPreferredSize(new java.awt.Dimension(80, 16));
		this.add(labelCaract);

		valueCaract.setPreferredSize(new java.awt.Dimension(50, 28));
		this.add(valueCaract);

		maxCaract.setHorizontalAlignment(SwingConstants.LEFT);
		if (caracteristique.max < 0)
			maxCaract.setText("/∞");
		else
			maxCaract.setText("/"+caracteristique.max);
			
		maxCaract.setPreferredSize(new Dimension(31,16));
		this.add(maxCaract);

		randomCaract.setText("Aleatoire");
		/*randomCaract.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				valueCaract.setEnabled(!cb.isSelected());
				randomRangeCaract.setEnabled(cb.isSelected());
			}
		});*/
		randomCaract.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				valueCaract.setEnabled(!cb.isSelected());
				randomRangeCaract.setEnabled(cb.isSelected());
			}
		});
		this.add(randomCaract);
		
		ComboBoxModel cbm;
		if (caracteristique.max < 0)
			cbm = new DefaultComboBoxModel(new String[] { 
					"[0,+∞]",
					"[-∞,0]", 
					"[-∞,+∞]" 
			});
		else
			cbm = new DefaultComboBoxModel(new String[] { 
					"[0,"+caracteristique.max+"]",
					"[-"+caracteristique.max+",0]", 
					"[-"+caracteristique.max+","+caracteristique.max+"]" 
			});
		randomRangeCaract.setModel(cbm);
		randomRangeCaract.setEnabled(randomCaract.isSelected());
		randomRangeCaract.setPreferredSize(new Dimension(124, 27));
		this.add(randomRangeCaract);
	}

	/**
	 * Renvoi le minimum de l'interval selectionne pour l'aleatoire		
	 * @return minimum de l'interval
	 */
	private int getMinRange(){
		switch (randomRangeCaract.getSelectedIndex()) {
		case 1:
		case 2:
			return - caracteristique.max;
		default:
			return 0;
		}
	}
	
	/**
	 * Renvoi le maximum de l'interval selectionne pour l'aleatoire		
	 * @return maximum de l'interval
	 */
	private int getMaxRange(){
		switch (randomRangeCaract.getSelectedIndex()) {
		case 0:
		case 2:
			return caracteristique.max;
		default:
			return 0;
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		valueCaract.setEnabled(enabled);
		valueCaract.setEditable(enabled);
		randomCaract.setEnabled(enabled);
		
		if (enabled == false || randomCaract.isSelected() )
			randomRangeCaract.setEnabled(enabled);
		
		labelCaract.setEnabled(enabled);
		maxCaract.setEnabled(enabled);
	
	
	}
	
	
}