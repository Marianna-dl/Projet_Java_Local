package interfaceGraphique.view;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import serveur.element.Caracteristique;
import utilitaires.Calculs;


public class VuePersonnageDeconnecte extends VuePersonnage {

	private static final long serialVersionUID = 6432052249501146906L;
	
	private int tourDeconnexion;
	
	public VuePersonnageDeconnecte(int refRMI, Point position, String nom,
			String groupe, boolean personnage,
			HashMap<Caracteristique, Integer> caract, int tourDeconnexion) {
		super(refRMI, position, nom, groupe, caract, new Color(112,112,112), "");
		
		this.tourDeconnexion = tourDeconnexion;
	}

	@Override
	public String getPhrase() {
		return "Mort a " + Calculs.timerToString(getTourDeconnexion());
	}
	
	public int getTourDeconnexion() {
		return tourDeconnexion;
	}


}
