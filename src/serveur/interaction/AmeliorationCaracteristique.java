package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map.Entry;

import serveur.Arene;
import serveur.ClientPersonnage;
import serveur.element.Caracteristique;
import serveur.element.Element;

public class AmeliorationCaracteristique {

	private ClientPersonnage client;
	private HashMap<Caracteristique, Integer> caracts;
	private Arene arene;

	public AmeliorationCaracteristique(Arene arene, ClientPersonnage client,
			HashMap<Caracteristique, Integer> caracts) {
		this.client = client;
		this.caracts = caracts;
		this.arene = arene;
	}

	/**
	 * Lance l'amelioration
	 * @return true si l'amelioration a fonctionne, false si elle a echoue
	 * @throws RemoteException
	 */
	public boolean ameliorer() throws RemoteException {
		int prix = Caracteristique.calculerPrix(caracts);
		Element element = client.getElement();
		if (prix > element.getCaract(Caracteristique.ARGENT)){
			return false;
		} else {
			ajouterCaracteristiques();
			arene.debiter(client, prix);
			return true;
		}
	}	
	
	/**
	 * Ajoute les caracteristiques au beneficiaire
	 * Si les valeurs des caracteristiques depassent leurs limites respectives,
	 * elles sont capees mais leur prix est quand meme preleve 
	 * @throws RemoteException
	 */
	private void ajouterCaracteristiques() throws RemoteException {
		for (Entry<Caracteristique, Integer> entryCaract : caracts.entrySet()){
			int prixUnitaire = entryCaract.getKey().getPrix();
			if (prixUnitaire >= 0 && entryCaract.getValue() >= 0){
				arene.ajouterCaractElement(client, entryCaract.getKey(), entryCaract.getValue());
			}
		}
	}	
}
