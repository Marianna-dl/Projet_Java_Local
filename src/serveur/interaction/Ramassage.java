package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import modele.Caracteristique;
import serveur.Arene;
import serveur.infosclient.ClientElement;
import serveur.infosclient.ClientPersonnage;

public class Ramassage extends EntreElement <ClientElement> {

	/**
	 * Constructeur parametre
	 * @param arene l'arene sur lequel a lieu le ramassage
	 * @param ramasseur le ramasseur de l'objet
	 * @param objet l'objet qui est ramasse
	 */
	public Ramassage(Arene arene, ClientPersonnage ramasseur, ClientElement objet) {
		super(arene, ramasseur, objet);
		logs(Level.INFO, Arene.nomRaccourciClient(ramasseur) + " essaye de rammasser " + Arene.nomRaccourciClient(objet));
	}

	@Override
	public void interagir() throws RemoteException {
		// effectue le ramassage
		// si le personnage est vivant et la potion non encore ramassee
		if(attaquant.getElement().isAlive() && defenseur.getElement().isAlive()) {
			ramasserObjet();
			
			logs(Level.INFO, "Potion bu !");
			if (! attaquant.getElement().isAlive()) {
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
	private void ramasserObjet() throws RemoteException {
		HashMap<Caracteristique, Integer> valeursObjet = defenseur.getElement().getCaracts();
		
		for (Entry<Caracteristique, Integer> caractEntry : valeursObjet.entrySet())
			arene.ajouterCaractElement(attaquant, caractEntry.getKey(), caractEntry.getValue());

		if (! attaquant.getElement().isAlive())
			arene.setPhrase(attaquant, "Je me suis empoisonne, je meurs ");
				
		// Deconnecte l'objet
		arene.ejecterObjet(defenseur);
	}
}
