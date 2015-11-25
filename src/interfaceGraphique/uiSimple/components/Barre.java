package interfaceGraphique.uiSimple.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Barre permettant de representer une caracteristique
 * Cette barre charge de maniere progressive
 *
 */
public class Barre extends JProgressBar implements ActionListener {

	private static final long serialVersionUID = 6257843410493768176L;

	private int finalValue;
	private Timer declencheur;
	private int actualValue;

	public Barre(int finalValue, Color finalColor) {
		actualValue = 0;
		this.finalValue = finalValue;
		declencheur = new Timer(30, this);
		this.setForeground(finalColor);
	}

	public void smoothLoad() {
		declencheur.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.setValue(actualValue);
		actualValue++;
		if (actualValue > finalValue) {
			declencheur.stop();
		}
	}
}
