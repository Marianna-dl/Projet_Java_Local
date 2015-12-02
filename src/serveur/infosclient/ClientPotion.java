package serveur.infosclient;

import java.awt.Point;

import interfaceGraphique.view.VuePotion;
import modele.Potion;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 */
public class ClientPotion extends ClientElement<Potion> {
	
	
	public ClientPotion(Potion pers, Point position, int ref) {
		super(pers, position, ref);
	}

	@Override
	public VuePotion getVue() {
		VuePotion vp = new VuePotion(
				getRef(), getPosition(), elem.getNom(), 
				elem.getGroupe(), elem.getCaracts(), getColor(), getPhrase());
		
		return vp;
	}
}
