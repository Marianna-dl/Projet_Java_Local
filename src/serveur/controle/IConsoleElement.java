package serveur.controle;

import interfaceGraphique.view.VueElement;
import modele.Personnage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;

import client.StrategiePersonnage;
import serveur.IArene;

/**
 * Represente un 
 */
public interface IConsoleElement extends Remote {
	/**
	 * Deconnexion du controleur du serveur.
	 * @param cause le message a afficher comme cause de la deconnexion
	 * @throws RemoteException
	 */
	public void shutDown(String cause) throws RemoteException;
	
	/**
	 * Execute le thread de l'element
	 * @throws RemoteException
	 */
	public void run() throws RemoteException;
			
	/**
	 * Renvoie l'element associe a la console
	 * @throws RemoteException
	 */
	public StrategiePersonnage getPersonnage() throws RemoteException;
	
	/**
	 * Renvoie la ref RMI de la console 
	 * @throws RemoteException
	 */
	public int getRefRMI() throws RemoteException;
	
	/**
	 * Renvoi l'arene a laquelle est rattachee la console
	 * @throws RemoteException
	 */
	public IArene getArene() throws RemoteException;
	
	/**
	 * Defini la phrase que la console vas dire
	 * @param s nouvelle phrase
	 * @throws RemoteException
	 */
	public void setPhrase(String s) throws RemoteException;
	
	/**
	 * Construit l'adresse de la console
	 * @return adresse de la console en String
	 * @throws RemoteException
	 */
	public String adrToString() throws RemoteException;
	
	/**
	 * Ecris dans le log
	 */
	public void log(Level level, String prefixe, String msg) throws RemoteException;
	
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
}







