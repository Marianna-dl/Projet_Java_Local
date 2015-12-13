package serveur.vuelement;

import java.awt.Point;

import serveur.element.Personnage;
import utilitaires.Constantes;

/**
 * Donnees que le serveur doit conserver sur chacun de ces clients personnages.
 */
public class VuePersonnage extends VueElement<Personnage> implements Comparable<VuePersonnage> {
	
	private static final long serialVersionUID = 6775104377685248116L;

	/**
	 * Adresse IP du client.
	 */
	private String adresseIp = Constantes.IP_DEFAUT;
	
	/**
	 * Vrai si ce personnage a execute une action ce tour-ci.
	 */
	private boolean actionExecutee;
	
	/**
	 * Nombre de tours que ce client peut passer sur l'arene.
	 * Si negatif, ce temps est illimite.
	 */
	private final long NB_TOURS;
	
	/**
	 * Numero de tour pour ce client.
	 */
	private int tour = 0;
	
	/**
	 * Numero du tour ou le personnage est mort. 
	 * Egal a -1 s'il est vivant.
	 */
	private int tourMort = -1;
	
	/**
	 * Cree une vue du personnage.
	 * @param adresseIp adresse IP de la console correspondant au personnage
	 * @param personnage personnage correspondant
	 * @param nbTours nombre de tours ou l'element est present sur 
	 * l'arene (si negatif, indefiniment)
	 * @param position position courante
	 * @param ref reference RMI
	 * @param envoyeImm vrai si l'element doit etre envoye immediatement
	 */
	public VuePersonnage(String adresseIp, Personnage personnage, long nbTours, 
			Point position, int ref, boolean envoyeImm) {
		
		super(personnage, position, ref, envoyeImm);
		this.adresseIp = adresseIp;
		this.actionExecutee = false;
		this.NB_TOURS = nbTours;
	}
	
	/**
	 * Note que ce personnage a deja execute une action a ce tour.
	 */
	public void executeAction() {
		actionExecutee = true;
	}

	/**
	 * Termine le tour de ce personnage : decremente le nombre de tours restants
	 * et note qu'aucune action n'a ete executee. 
	 */
	public void termineTour() {
		actionExecutee = false;
		tour++;
	}
	
	public boolean toursRestants() {
		return tour < NB_TOURS;
	}

	public String getAdresseIp() {
		return adresseIp;
	}

	public boolean isActionExecutee() {
		return actionExecutee;
	}
	
	public int getTourMort() {
		return tourMort;
	}

	public void setTourMort(int tourMort) {
		this.tourMort = tourMort;
	}

	@Override
	public int compareTo(VuePersonnage vp2) {
		int res;
		
		Personnage e1 = this.getElement();
		Personnage e2 = vp2.getElement();
		
		if(e1.estVivant()) {
			if(e2.estVivant()) {
				// tous les deux vivants : reference RMI
				res = vp2.getRefRMI() - this.getRefRMI();
			} else {
				// vivant avant mort
				res = -1;
			}
		} else {
			if(e2.estVivant()) {
				// vivant avant mort
				res = 1;
			} else {
				// tous les deux morts : celui mort le plus tard avant
				res = this.getTourMort() - vp2.getTourMort();
			}
		}
		
		
		return res;
	}
}















