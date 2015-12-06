package interfacegraphique.interfacesimple.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Barre permettant de representer une caracteristique.
 * Cette barre se charge de maniere progressive.
 *
 */
public class Barre extends JProgressBar implements ActionListener {

	private static final long serialVersionUID = 6257843410493768176L;

	/**
	 * Valeur finale a atteindre.
	 */
	private int valeurFinale;
	
	/**
	 * Valeur courante (en chargement). 
	 */
	private int valeurCourante;

	/**
	 * Timer permettant le chargement progressif de la barre.
	 */
	private Timer declencheur;
	
	/**
	 * Cree une barre avec une valeur et une couleur.
	 * @param valeurFinale valeur
	 * @param couleurFinale couleur
	 */
	public Barre(int valeurFinale, Color couleurFinale) {
		valeurCourante = 0;
		this.valeurFinale = valeurFinale;
		this.setForeground(couleurFinale);
		declencheur = new Timer(30, this);
	}

	/**
	 * Demarre le chargement. 
	 */
	public void lanceChargement() {
		declencheur.start();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		this.setValue(valeurCourante);
		valeurCourante++;
		
		if (valeurCourante > valeurFinale) {
			declencheur.stop();
		}
	}
}
