package serveur;

import interfaceGraphique.view.VuePersonnage;

import java.awt.Point;

import serveur.element.Caracteristique;
import serveur.element.PersonnageServeur;

/**
 * Toutes les données que le serveur doit conserver sur chacun de ces clients
 * @author cricri
 */
public class ClientPersonnage extends ClientElement {

	private static final int NB_TOUR_INCREMENT_INITIATIVE = 10;

	/**
	 * adresse ip du client
	 */
	private String ipAddress = "localhost";
	
	/**
	 * Le booléen spécifiant si une action a été executée ce tour-ci
	 */
	private boolean actionExecutee;
	
	private int tourSonne;
	
	/**
	 * Time To Live
	 */
	private int TTL;
	/**
	 * Compteur permettant de savoir le nombre de tour restant avant increment
	 */
	private int cptTourAvantIncrement = NB_TOUR_INCREMENT_INITIATIVE;
	
	public ClientPersonnage(String ipAddress, PersonnageServeur pers, int TTL, Point position, int ref) {
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

	public PersonnageServeur getPersServeur() {
		return (PersonnageServeur) getElement();
	}
	
	public void setPersServeur(PersonnageServeur pers) {
		this.elem = pers;
	}

	public VuePersonnage getVue() {
		PersonnageServeur perso = getPersServeur();
		VuePersonnage vp = new VuePersonnage(
				getRef(), getPosition(), perso.getNom(), 
				perso.getGroupe(), perso.getCaracts(), getColor(), 
				getPhrase(), perso.getEquipe(), perso.getLeader());
		
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
	 * Action a faire à la fin de ce tour
	 */
	public void finTour() {
		TTL--;
		incrementerInitiative();
		actionExecutee = false;
		decrTourSonne();
	}

	/**
	 * Increment l'initiative de la valeur de la vitesse
	 */
	private void incrementerInitiative() {
		if (cptTourAvantIncrement <= 0){
			PersonnageServeur pers = getPersServeur();
			int increment = pers.getCaract(Caracteristique.VITESSE);
			int initiative = pers.getCaract(Caracteristique.INITIATIVE) + increment;
			pers.ajouterCaract(Caracteristique.INITIATIVE, initiative);
			cptTourAvantIncrement = NB_TOUR_INCREMENT_INITIATIVE;
		} else {
			cptTourAvantIncrement--;
		}
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
