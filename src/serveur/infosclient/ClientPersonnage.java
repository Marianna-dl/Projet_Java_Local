package serveur.infosclient;

import interfaceGraphique.view.VuePersonnage;
import modele.Personnage;

import java.awt.Point;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 */
public class ClientPersonnage extends ClientElement {


	/**
	 * adresse ip du client
	 */
	private String ipAddress = "localhost";
	
	/**
	 * booleen specifiant si une action a ete executee ce tour-ci
	 */
	private boolean actionExecutee;
	
	private int tourSonne;
	
	/**
	 * Time To Live
	 */
	private int TTL;
	
	
	public ClientPersonnage(String ipAddress, Personnage pers, int TTL, Point position, int ref) {
		super(pers, position, ref);
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
		this.elem = pers;
	}

	public VuePersonnage getVue() {
		Personnage perso = getPersServeur();
		VuePersonnage vp = new VuePersonnage(
				getRef(), getPosition(), perso.getNom(), 
				perso.getGroupe(), perso.getCaracts(), getColor(), getPhrase());
		
		return vp;
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
}
