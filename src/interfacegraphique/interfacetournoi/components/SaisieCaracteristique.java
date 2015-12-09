package interfacegraphique.interfacetournoi.components;

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
 * Panel permettant la saisie d'une caracteristique donnee.
 *
 */
public class SaisieCaracteristique extends JPanel {

	private static final long serialVersionUID = 1L;		

	/**
	 * Caracteristique concernee. 
	 */
	private Caracteristique caracteristique;

	/**
	 * Champ de saisie de la caracteristique.
	 */
	private JTextField valeurCaract;
	
	/**
	 * CheckBox permettant de choisir une valeur aleatoire pour la 
	 * caracteristique.
	 */
	private JCheckBox aleatoireCaract;
	
	/**
	 * ComboBox permettant de selectionner l'intervalle de l'aleatoire.
	 */
	@SuppressWarnings("rawtypes") // warning dans java 8
	private JComboBox aleatoireIntervalleCaract;

	/**
	 * Label affichant le nom de la caracteristique.
	 */
	private JLabel labelCaract;

	/**
	 * Label affichant la valeur max de la caracteristique.
	 */
	private JLabel maxCaract;

	/**
	 * Cree un panel de saisie de caracteristique.
	 * @param c caracteristique
	 */
	public SaisieCaracteristique(Caracteristique c) {			
		this.caracteristique = c;
		initComposants();	        
	}

	/**
	 * Initialise les composants du panel.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) // warning dans java 8
	private void initComposants() {
		labelCaract = new JLabel();
		valeurCaract = new JTextField();
		maxCaract = new JLabel();
		aleatoireCaract = new JCheckBox();
		aleatoireIntervalleCaract = new JComboBox();
	
		labelCaract.setHorizontalAlignment(SwingConstants.CENTER);
		labelCaract.setText(caracteristique.name());
		labelCaract.setPreferredSize(new java.awt.Dimension(80, 16));
		this.add(labelCaract);
	
		valeurCaract.setPreferredSize(new java.awt.Dimension(50, 28));
		this.add(valeurCaract);
	
		maxCaract.setHorizontalAlignment(SwingConstants.LEFT);
		maxCaract.setText("/" + caracteristique.getMax());
		maxCaract.setPreferredSize(new Dimension(31,16));
		this.add(maxCaract);
	
		aleatoireCaract.setText("Aleatoire");
		aleatoireCaract.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				valeurCaract.setEnabled(!cb.isSelected());
				aleatoireIntervalleCaract.setEnabled(cb.isSelected());
			}
		});
		
		this.add(aleatoireCaract);
		
		ComboBoxModel cbm = new DefaultComboBoxModel(new String[] { 
					"[0," + caracteristique.getMax() + "]",
					"[-" + caracteristique.getMax() + ",0]", 
					"[-" + caracteristique.getMax() + "," + caracteristique.getMax() + "]" 
			});
		
		aleatoireIntervalleCaract.setModel(cbm);
		aleatoireIntervalleCaract.setEnabled(aleatoireCaract.isSelected());
		aleatoireIntervalleCaract.setPreferredSize(new Dimension(124, 27));
		this.add(aleatoireIntervalleCaract);
	}

	/**
	 * Renvoie le minimum de l'intervalle selectionne pour l'aleatoire.
	 * @return minimum de l'intervalle
	 */
	private int getMinRange() {
		switch (aleatoireIntervalleCaract.getSelectedIndex()) {
		case 1:
		case 2:
			return -caracteristique.getMax();
		default:
			return 0;
		}
	}

	/**
	 * Renvoie le maximum de l'intervalle selectionne pour l'aleatoire.
	 * @return maximum de l'intervalle
	 */
	private int getMaxRange() {
		switch (aleatoireIntervalleCaract.getSelectedIndex()) {
		case 0:
		case 2:
			return caracteristique.getMax();
		default:
			return 0;
		}
	}

	public Caracteristique getCaracteristique() {
		return caracteristique;
	}

	/**
	 * Permet de recuperer la valeur saisie ou generee aleatoirement.
	 * @return valeur 
	 */
	public int getValeur() {
		int value;
		
		if (!isEnabled()) {
			value = 0;
		}
		
		if (aleatoireCaract.isSelected()) {
			value = Calculs.nombreAleatoire(getMinRange(), getMaxRange());
		} else {
			value = Integer.parseInt(valeurCaract.getText());				
		}
		
		return value;
	}

	/**
	 * Active ou desactive le panel.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		valeurCaract.setEnabled(enabled);
		valeurCaract.setEditable(enabled);
		aleatoireCaract.setEnabled(enabled);
		
		if (enabled == false || aleatoireCaract.isSelected() )
			aleatoireIntervalleCaract.setEnabled(enabled);
		
		labelCaract.setEnabled(enabled);
		maxCaract.setEnabled(enabled);
	}
	
	
}