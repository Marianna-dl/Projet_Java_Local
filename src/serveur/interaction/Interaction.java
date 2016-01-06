package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

/**
 * Represente une interaction entre un personnage et un autre element :
 * un duel ou un ramassage.
 *
 * @param <T> vue de l'autre element
 */
public abstract class Interaction<T extends VueElement<?>> {
	
	/**
	 * Arene (serveur).
	 */
	protected Arene arene;
	
	/**
	 * Vue de l'attaquant, c'est-a-dire le personnage faisant l'action.
	 */
	protected VuePersonnage attaquant; 
	
	/**
	 * Vue du defenseur : personnage en cas d'attaque, potion en cas de 
	 * ramassage.
	 */
	protected T defenseur;
	
	/**
	 * Cree une interaction entre un personnage et un element.
	 * @param arene arene
	 * @param attaquant vue de l'attaquant
	 * @param defenseur vue du defenseur
	 * @throws RemoteException
	 */
	public Interaction(Arene arene, VuePersonnage attaquant, T defenseur) {
		this.arene = arene;
		this.attaquant = attaquant;
		this.defenseur = defenseur;
	}
	
	/**
	 * Realise l'interaction.
	 */
	public abstract void interagit();
	
	/**
	 * Remplit le log de l'arene et des deux clients. 
	 * @param level niveau de log
	 * @param msg message
	 */
	protected void logs(Level level, String msg) {
		try {
			arene.getLogger().log(Level.INFO, Constantes.nomClasse(this), msg);
			arene.logClient(attaquant, Level.INFO, Constantes.nomClasse(this), msg);
			arene.logClient(defenseur, Level.INFO, Constantes.nomClasse(this), msg);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
