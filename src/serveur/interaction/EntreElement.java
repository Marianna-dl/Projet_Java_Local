package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.infosclient.VueElement;
import serveur.infosclient.VuePersonnage;

public abstract class EntreElement<T extends VueElement> {
	
	/**
	 * Arene.
	 */
	protected Arene arene;
	/**
	 * Reference de l'attaquant.
	 */
	protected VuePersonnage attaquant; 
	/**
	 * Reference du defenseur.
	 */
	protected T defenseur;
	
	
	
	/**
	 * Constructeur
	 * @param arene arene
	 * @param attaquant la reference de l'attaquant
	 * @param defenseur la reference du defenseur
	 * @throws RemoteException
	 */
	public EntreElement(Arene arene, VuePersonnage attaquant, T defenseur) {
		this.arene = arene;
		this.attaquant = attaquant;
		this.defenseur = defenseur;
	}
	
	public abstract void interagir()  throws RemoteException;
	
	protected void logs(Level level, String msg) {
		try {
			arene.log(Level.INFO, this.getClass().toString(), msg);
			arene.logClient(attaquant, Level.INFO, this.getClass().toString(), msg);
			arene.logClient(defenseur, Level.INFO, this.getClass().toString(), msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
