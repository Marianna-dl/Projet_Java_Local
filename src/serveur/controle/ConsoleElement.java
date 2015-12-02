package serveur.controle;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;

import client.StrategiePersonnage;
import interfaceGraphique.view.VueElement;
import logger.MyLogger;
import modele.Personnage;
import serveur.IArene;

/**
 * Implementation des methodes RMI associees a un element (personnage ou 
 * potion).
 * La strategie est executee depuis la methode run(). 
 *
 */
public class ConsoleElement extends UnicastRemoteObject implements IConsoleElement {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Adresse IP du serveur.
	 */
	private String ipArene;

	/**
	 * Port de communication avec l'arene.
	 */
	private int port;

	/**
	 * Adresse IP de la console, localhost si en local.
	 */
	private String ipConsole;

	/**
	 * Arene (serveur) avec lequel le controleur communique.
	 */
	private IArene arene = null;

	/**
	 * Element pour lequel le controleur est cree.
	 */
	private final StrategiePersonnage per;

	/**
	 * Reference attribuee par le serveur a la connexion.
	 */
	private int refRMI;

	/**
	 * Le gestionnaire de log
	 */
	private MyLogger myLogger;
	
	/**
	 * 
	 * @param per personnage de la console
	 * @param pers personnage serveur de la console
	 * @param position position de la console
	 * @param port port de communication avec l'arene
	 * @param ipArene ip de communication avec l'arene
	 * @param ipConsole ip de la console
	 * @param myLogger gestionnaire de log
	 * @throws RemoteException
	 */
	public ConsoleElement(StrategiePersonnage per, Personnage pers, Point position,
			int port, String ipArene, String ipConsole, MyLogger myLogger) throws RemoteException {
		//appel au constructeur de la super-classe -> il peut etre implicite
		super();
		this.port = port;
		this.ipArene = ipArene;
		this.per = per;
		this.ipConsole = ipConsole;
		//init du logger
		this.myLogger = myLogger;
		//initialisation de l'element pour lequel le controleur est cree
		try {
			//preparation connexion au serveur
			//handshake/enregistrement RMI
			myLogger.info(this.getClass().toString(), "Tentative de recuperation de l'arene...");
			arene = (IArene) java.rmi.Naming.lookup("rmi://"+this.ipArene+":"+this.port+"/Arene");
			myLogger.info(this.getClass().toString(), "Arene recupere");

			// initialisation de la reference du controleur sur le serveur
			// La console devient "serveur" pour les methodes de IConsole 
			// lancer l'annuaire rmi en tant que serveur. A faire une seule fois par serveur de console pour un port donne
			// doit rester "localhost"
			myLogger.info(this.getClass().toString(), "Demande d'allocation de port");
			this.refRMI = arene.allocateRef();
			int portServeur = this.port + refRMI;
			myLogger.info(this.getClass().toString(), "Port alloue : "+portServeur);
			java.rmi.registry.LocateRegistry.createRegistry(portServeur);
			Naming.rebind(adrToString(),this);
			
			//connexion a l'arene pour lui permettre d'utiliser les methodes de IConsole
			myLogger.info(this.getClass().toString(), "Demande de connexion avec l'adresse "+adrToString());
			
			boolean resultatConnexion = arene.connect(refRMI, ipConsole, pers, position);
			
			if (!resultatConnexion) {
				myLogger.severe(this.getClass().toString(), "Echec de connexion");
				System.exit(1);
			}
			setPhrase("Atterrissage ...");
			
			//affiche message si succes
			myLogger.info(this.getClass().toString(), "Connexion reussie");
 		} catch (Exception e) {
 			myLogger.severe(this.getClass().toString(), "Erreur : Impossible de creer la console :\n"+e.toString());
  			e.printStackTrace();
  			System.exit(1);
 		}
	}

	@Override
	public void run() throws RemoteException {
		//met a jour ses voisins 
		HashMap<Integer, Point> voisins = arene.voisins(this);
		//applique la strategie du personnage
		per.strategie(voisins);
	}


	@Override
	public void shutDown(String cause) throws RemoteException {
		myLogger.info(this.getClass().toString(), "Console deconnectee : "+cause);
		System.exit(0);
	}


	@Override
	public StrategiePersonnage getPersonnage() throws RemoteException {
		return per;
	}

	@Override
	public int getRefRMI() throws RemoteException{
		return refRMI;
	}

	@Override
	public IArene getArene() throws RemoteException {
		return arene;
	}

	@Override
	public void setPhrase(String s) throws RemoteException {
		arene.setPhrase(this, s);
	}

	@Override
	public String adrToString() throws RemoteException {
		return "rmi://"+ipConsole+":"+(port+getRefRMI())+"/Console"+getRefRMI();
	}

	@Override
	public void log(Level level, String prefixe, String msg) throws RemoteException {
		myLogger.log(level, prefixe,msg);
	}

	@Override
	public Personnage getPersonnageServeur() throws RemoteException {
		return (Personnage) arene.getMyElement(this);
	}

	@Override
	public VueElement getVueElement() throws RemoteException {
		return arene.getMyVueElement(this);
	}
}
