package serveur.infosclient;

import java.awt.Point;

import serveur.element.Personnage;
import utilitaires.Constantes;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 */
public class VuePersonnage extends VueElement {
	
	private static final long serialVersionUID = 6775104377685248116L;

	/**
	 * adresse ip du client
	 */
	private String ipAddress = Constantes.IP_DEFAUT;
	
	/**
	 * booleen specifiant si une action a ete executee ce tour-ci
	 */
	private boolean actionExecutee;
	
	private int tourSonne;
	
	/**
	 * Time To Live
	 */
	private int TTL;
	
	/**
	 * Numero du tour ou le personnage est mort. 
	 * Egal a -1 s'il est vivant.
	 */
	private int tourMort = -1;
	
	
	public VuePersonnage(String ipAddress, Personnage pers, int TTL, Point position, int ref, boolean envoyeImm) {
		super(pers, position, ref, envoyeImm);
		this.ipAddress = ipAddress;
		this.actionExecutee = false;
		this.tourSonne = 0;
		this.TTL = TTL;
	}

	/* *******************
	 * Accesseurs
	 * *******************/
	public String getIpAddress() {
		return ipAddress;
	}

	public Personnage getPersServeur() {
		return (Personnage) getElement();
	}
	
	public void setPersServeur(Personnage pers) {
		this.element = pers;
	}

	public boolean isActionExecutee() {
		return actionExecutee;
	}
	
	/**
	 * Une action vient d'etre executee, on fait en sorte que plus aucune autre action
	 * ne sera executee avant la fin de ce tour
	 */
	public void actionExecutee() {
		actionExecutee = true;
	}

	public int getTTL() {
		return TTL;
	}
	
	/**
	 * Action a faire a la fin de ce tour
	 */
	public void finTour() {
		TTL--;
		actionExecutee = false;
		decrTourSonne();
	}


	
	private void decrTourSonne () {
		if (tourSonne > 0) tourSonne--;
	}
	
	public void sonne() {
		tourSonne++;
	}
	
	public int getTourSonne() {
		return tourSonne;
	}

	public int getTourMort() {
		return tourMort;
	}

	public void setTourMort(int tourMort) {
		this.tourMort = tourMort;
	}
}
