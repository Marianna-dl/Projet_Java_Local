package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import serveur.infosclient.VuePersonnage;
import serveur.infosclient.VuePotion;

public class Ramassage extends EntreElement<VuePotion> {

	/**
	 * Constructeur parametre
	 * @param arene l'arene sur lequel a lieu le ramassage
	 * @param ramasseur le ramasseur de la potion
	 * @param potion la potion qui est ramasse
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
		logs(Level.INFO, Arene.nomRaccourciClient(ramasseur) + " essaye de rammasser " + Arene.nomRaccourciClient(potion));
	}

	@Override
	public void interagir() throws RemoteException {
		// effectue le ramassage
		// si le personnage est vivant et la potion non encore ramassee
		if(attaquant.getElement().estActif() && defenseur.getElement().estActif()) {
			ramasserPotion();
			
			logs(Level.INFO, "Potion bu !");
			if (! attaquant.getElement().estActif()) {
				logs(Level.INFO, Arene.nomRaccourciClient(attaquant) +" vient de boire un poison... Mort >_<");
			}
		} else {
			logs(Level.INFO, Arene.nomRaccourciClient(attaquant) + " ou " + Arene.nomRaccourciClient(defenseur) + "est deja mort... Rien ne se passe");
		}
	}

	/**
	 * Ramasse et utilise une potion sur un personnage specifiee. Ne doit pas 
	 * etre appele tel quel, mais est utilise dans {@code interagir}.
	 * @param potion potion
	 * @param per personnage
	 * @throws RemoteException
	 */
	private void ramasserPotion() throws RemoteException {
		HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
		
		for (Entry<Caracteristique, Integer> caractEntry : valeursPotion.entrySet())
			arene.ajouterCaractElement(attaquant, caractEntry.getKey(), caractEntry.getValue());

		if (! attaquant.getElement().estActif())
			arene.setPhrase(attaquant, "Je me suis empoisonne, je meurs ");
		
		((Potion) defenseur.getElement()).ramasser();
		
		// Deconnecte la potion
		arene.ejecterPotion(defenseur.getRefRMI());
	}
}
