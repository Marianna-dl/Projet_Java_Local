package serveur.interaction;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map.Entry;

import serveur.Arene;
import serveur.ClientPersonnage;
import serveur.element.Caracteristique;
import serveur.element.Element;

public class AmeliorationCaracteristique {

	private ClientPersonnage client;
	private Hashtable<Caracteristique, Integer> caracts;
	private Arene arene;

	public AmeliorationCaracteristique(Arene arene, ClientPersonnage client,
			Hashtable<Caracteristique, Integer> caracts) {
		this.client = client;
		this.caracts = caracts;
		this.arene = arene;
	}

	/**
	 * Lance l'amelioration
	 * @return true si l'amelioration a fonctionné, false si elle a échoué
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
	 * elles sont capées mais leur prix est quand même prelevé 
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
