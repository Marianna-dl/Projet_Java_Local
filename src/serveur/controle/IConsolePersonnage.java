package serveur.controle;

import interfaceGraphique.view.VueElement;
import modele.Personnage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;

import client.StrategiePersonnage;
import serveur.IArene;

/**
 * Contient les methodes RMI associees au controle d'un personnage,
 * comme l'ajout d'une phrase ou l'execution de sa strategie. 
 */
public interface IConsolePersonnage extends Remote {
	
	/**
	 * Execute le thread de l'element (joue la strategie). 
	 * @throws RemoteException
	 */
	public void run() throws RemoteException;
			
	/**
	 * Deconnexion du serveur.
	 * @param cause cause de la deconnexion
	 * @throws RemoteException
	 */
	public void shutDown(String cause) throws RemoteException;

	/**
	 * Renvoie l'arene (le serveur) avec laquelle communique la console. 
	 * @throws RemoteException
	 */
	public IArene getArene() throws RemoteException;

	/**
	 * Renvoie la reference RMI de la console.
	 * @return reference RMI
	 * @throws RemoteException
	 */
	public int getRefRMI() throws RemoteException;

	/**
	 * Renvoie la strategie associee au personnage.
	 * @return strategie du personnage
	 * @throws RemoteException
	 */
	public StrategiePersonnage getStrategiePer() throws RemoteException;
	
	/**
	 * Recupere le personnage serveur de la console
	 * @return personnage serveur
	 * @throws RemoteException
	 */
	public Personnage getPersonnageServeur() throws RemoteException;

	/**
	 * Recupere la VueElement de la console
	 * @return VueElement
	 * @throws RemoteException
	 */
	public VueElement getVueElement() throws RemoteException;

	/**
	 * Definit la phrase que la console va dire (et qui sera affichee dans 
	 * l'IHM). 
	 * @param s phrase
	 * @throws RemoteException
	 */
	public void setPhrase(String s) throws RemoteException;
	
	/**
	 * Construit l'adresse de la console a partir de son IP et de sa reference.
	 * @return adresse de la console
	 * @throws RemoteException
	 */
	public String adrToString() throws RemoteException;
	
	/**
	 * Ecrit dans le log.
	 * @param level niveau d'importance
	 * @param prefixe prefixe au message
	 * @param msg message
	 * @throws RemoteException
	 * 
	 */
	public void log(Level level, String prefixe, String msg) throws RemoteException;
}







