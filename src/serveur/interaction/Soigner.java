package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.Voleur;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente un duel entre deux personnages.
 *
 */
public class Soigner extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Soigner(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	@Override
	public void interagir() {
		try {
			Personnage pAttaquant = (Personnage) attaquant.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie = forceAttaquant;		
			
			// soin
			if (perteVie <100) {
				Personnage pDefenseur = (Personnage) defenseur.getElement();
				int vie = pDefenseur.getCaract(Caracteristique.VIE);
				int soin = Calculs.nombreAleatoire(0,100-vie);				
				arene.ajouterCaractElement(defenseur, Caracteristique.VIE, +soin);
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " soigne ("
						+ soin + " points de vie) a " + Constantes.nomRaccourciClient(defenseur));
				
				if(soin != 0){
					int init = pDefenseur.getCaract(Caracteristique.INITIATIVE);
					arene.ajouterCaractElement(defenseur, Caracteristique.INITIATIVE, -init);
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " soigne ("
							+ soin + " points de vie) a " + Constantes.nomRaccourciClient(defenseur));
					
					
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " enlève en contrepartie ("
							+ init + " points d'initiative) a " + Constantes.nomRaccourciClient(defenseur));
				}
			}
			// initiative
			decrementerInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
		
	}

	/**
	 * Incremente l'initiative du defenseur en cas de succes de l'attaque. 
	 * @param defenseur defenseur
	 * @throws RemoteException
	 */
	private void incrementerInitiative(VuePersonnage defenseur) throws RemoteException {
		arene.ajouterCaractElement(defenseur, Caracteristique.INITIATIVE, 
				Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementerInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.ajouterCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}

}
