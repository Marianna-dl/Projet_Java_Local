package serveur;

import java.rmi.RemoteException;
import java.util.List;

import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;

/**
 * Definit les methodes de l'arene utilisables a travers le reseau specifiques 
 * a l'IHM.
 *
 */
public interface IAreneIHM extends IArene {

	/**
	 * Recupere la liste de toutes les representations de personnages presents 
	 * dans l'arene. 
	 * @return liste des personnages
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getPersonnages() throws RemoteException;

	/**
	 * Recupere la liste de toutes les representations de personnages morts.
	 * @return liste des personnages morts
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getPersonnagesMorts() throws RemoteException;
	
	/**
	 * Calcule la liste de toutes les potions presentes dans l'arene.
	 * @return liste des potions
	 * @throws RemoteException
	 */
	public List<VuePotion> getPotions() throws RemoteException;
	
	/**
	 * Renvoie la liste des personnages tries pour le classement final.
	 * @return liste des personnages ordonnes pour le classement final
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getClassement() throws RemoteException;
	
	/**
	 * Recupere la vue du personnage gagnant de la partie.
	 * @return vue du personnage gagnant, ou null s'il n'y en a pas (aucun 
	 * personnage ou plusieurs personnages en vie)
	 * @throws RemoteException
	 */
	public VuePersonnage getGagnant() throws RemoteException;

}
