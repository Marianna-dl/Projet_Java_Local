package interfaceGraphique.uiControle.components;

import interfaceGraphique.uiControle.exceptionSaisie.PositionNotValidException;

import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import serveur.Arene;
import utilitaires.Calculs;

/**
 * Panel permettant la saisie d'une position
 *
 */
public class SaisiePosition extends JPanel{
    	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Champ de saisie correspondant a la valeur X d'une position
	 */
	private JTextField xPosition;
	/**
	 * Champ de saisie correspondant a la valeur Y d'une position
	 */
	private JTextField yPosition;
	/**
	 * CheckBox permettant la selection de l'aleatoire
	 */
	private JCheckBox randomPosition;
	
	
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
     * Recupere la position saisie
	 * Declenche une exception si la position n'est pas valide
     * @return Point correspondant a la position saisie
     * @throws PositionNotValidException
     */
	public Point getPosition() throws PositionNotValidException{
		// Si aleatoire activee
		if (randomPosition.isSelected()){
			// Generation d'une position
			int x = Calculs.randomNumber(Arene.XMIN, Arene.XMAX);
			int y = Calculs.randomNumber(Arene.YMIN, Arene.YMAX);
			return new Point(x,y);
		} else {
			// Recuperation de la saisie
			try {
	    		int x = Integer.parseInt(xPosition.getText());
	    		int y = Integer.parseInt(yPosition.getText());
	    		if (x > Arene.XMAX || x < Arene.XMIN || y < Arene.YMIN || y > Arene.YMAX){
	    			throw new PositionNotValidException();
	    		}
	    		return new Point(x, y);
			} catch (NumberFormatException e){
				throw new PositionNotValidException();
			}
		}		
	}
	
	/**
	 * Affiche une position donnee dans les champs de saisie
	 * @param p
	 */
	public void setPosition(Point p){
		xPosition.setText(String.valueOf((int) p.getX()));
		yPosition.setText(String.valueOf((int) p.getY()));
	}

	public void disableRandom() {
		randomPosition.setSelected(false);
	}
	
}