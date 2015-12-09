package interfacegraphique.interfacetournoi.components;

import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import interfacegraphique.interfacetournoi.exceptionSaisie.PositionNonValideException;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Panel permettant la saisie d'une position de l'arene. 
 *
 */
public class SaisiePosition extends JPanel{
    	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Champ de saisie correspondant a l'abscisse d'une position.
	 */
	private JTextField xPosition;
	
	/**
	 * Champ de saisie correspondant a l'ordonne d'une position.
	 */
	private JTextField yPosition;
	
	/**
	 * CheckBox permettant de choisir une valeur aleatoire pour la 
	 * position.
	 */
	private JCheckBox randomPosition;
	
	/**
	 * Cree un panel de saisie de position.
	 */
	public SaisiePosition() {
		this.setPreferredSize(new java.awt.Dimension(417, 42));

        JLabel positionLabel = new JLabel();
        xPosition = new JTextField();
        JLabel comma = new JLabel();
        yPosition = new JTextField();
        JLabel closeParenthese = new JLabel();
        randomPosition = new JCheckBox();
		
        positionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        positionLabel.setText("Position  (");
        this.add(positionLabel);

        xPosition.setPreferredSize(new java.awt.Dimension(50, 28));
        this.add(xPosition);

        comma.setHorizontalAlignment(SwingConstants.LEFT);
        comma.setText(",");
        this.add(comma);

        yPosition.setPreferredSize(new java.awt.Dimension(50, 28));
        this.add(yPosition);

        closeParenthese.setHorizontalAlignment(SwingConstants.LEFT);
        closeParenthese.setText(")");
        this.add(closeParenthese);
        
        randomPosition.setText("Aleatoire");
        
        randomPosition.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				xPosition.setEnabled(!cb.isSelected());
				yPosition.setEnabled(!cb.isSelected());
			}
		});
        
        this.add(randomPosition);
	}

    /**
     * Recupere la position saisie.
	 * Declenche une exception si la position n'est pas valide.
     * @return point correspondant a la position saisie
     * @throws PositionNonValideException
     */
	public Point getPosition() throws PositionNonValideException {
		Point res;
		
		// si aleatoire
		if (randomPosition.isSelected()) {
			// generation d'une position
			int x = Calculs.nombreAleatoire(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE);
			int y = Calculs.nombreAleatoire(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE);
			
			res = new Point(x,y);
		} else {
			// recuperation de la saisie
			try {
	    		int x = Integer.parseInt(xPosition.getText());
	    		int y = Integer.parseInt(yPosition.getText());
	    		
	    		if (x > Constantes.XMAX_ARENE || x < Constantes.XMIN_ARENE || 
	    				y < Constantes.YMIN_ARENE || y > Constantes.YMAX_ARENE) {
	    			throw new PositionNonValideException();
	    		}
	    		
	    		res = new Point(x,y);
	    		
			} catch (NumberFormatException e) {
				throw new PositionNonValideException();
			}
		}
		
		return res;
	}
	
	/**
	 * Affiche une position donnee dans les champs de saisie.
	 * @param p position
	 */
	public void setPosition(Point p) {
		xPosition.setText(String.valueOf((int) p.getX()));
		yPosition.setText(String.valueOf((int) p.getY()));
	}
	
	/**
	 * Desactive la checkbox permettant de choisir l'aleatoire. 
	 */
	public void desactiveAleatoire() {
		randomPosition.setSelected(false);
	}
	
}