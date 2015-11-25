package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.ClientPersonnage;
import serveur.element.Caracteristique;
import serveur.element.PersonnageServeur;

public class Extorsion extends EntreElement <ClientPersonnage> {
	
	int extorsion;
	
	public Extorsion(Arene arene, ClientPersonnage attaquant, ClientPersonnage defenseur, int extorsion) {
		super(arene, attaquant, defenseur);
		this.extorsion = extorsion;
		logs(Level.INFO, "Extorsion de " + Arene.nomRaccourciClient(attaquant) + " sur " + Arene.nomRaccourciClient(defenseur) + " :");
	}
	
	public void interagir() throws RemoteException {
		try {
			extorquer();
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une extorsion : "+e.toString());
		}
	}
	
	private void extorquer() throws RemoteException {
		PersonnageServeur pAttaquant = (PersonnageServeur) attaquant.getElement();
		PersonnageServeur pDefenseur = (PersonnageServeur) defenseur.getElement();
		
		int argentDefenseur = pDefenseur.getCaract(Caracteristique.ARGENT);
		int argentAttaquant = pAttaquant.getCaract(Caracteristique.ARGENT);
		
		// calcul de l'argent extorque
		int incrementArgent = (argentDefenseur<=extorsion)?argentDefenseur:extorsion;
		
		// extorsion
		arene.ajouterCaractElement(defenseur, Caracteristique.ARGENT, - incrementArgent);
		arene.ajouterCaractElement(attaquant, Caracteristique.ARGENT, + incrementArgent);
		
		// ajout dans l'equipe ?
		if (argentDefenseur - incrementArgent < argentAttaquant + incrementArgent && pDefenseur.getLeader() == -1) {
			arene.ajouterPersonnageEquipe(attaquant, defenseur.getRef());
		}
	}
}
