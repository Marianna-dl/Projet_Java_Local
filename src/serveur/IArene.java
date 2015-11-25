package serveur;

import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

import client.controle.IConsole;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.PersonnageServeur;

/**
 * Definit les methodes qui pourront s'appliquer a travers l'arene en RMI (toutes celles qui levent RemoteException)
 */
public interface IArene extends Remote {
	
	/**
	 * Alloue une référence
	 * @return reference de la prochaine console
	 * @throws RemoteException
	 */
	public int allocateRef() throws RemoteException;
	
	/**
	 * Connecte un élément au serveur
	 * @param refRMI referecne de l'élément à connecter
	 * @param ipConsole ip de la console
	 * @param pers personnage correspondant à l'élément
	 * @param position position de l'élément
	 * @return true si l'élémént à été connecté, false sinon
	 * @throws RemoteException
	 */
	public boolean connect(int refRMI, String ipConsole, PersonnageServeur pers, Point position) throws RemoteException;
	
	/**
	 * Deconnecte un élément du serveur
	 * @param console console à deconnecter
	 * @param cause cause de la cdéconnexion
	 * @throws RemoteException
	 */
	public void deconnecterConsole(IConsole console, String cause) throws RemoteException;
	
	/**
	 * Calcule la liste de toutes les representations de personnages présents dans l'arene. 
	 * @return liste des representations
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getPersonnages() throws RemoteException;
	
	/**
	 * Calcules la liste de toutes les representations d'objets présents dans l'arène
	 * @return liste des representations
	 * @throws RemoteException
	 */
	public List<VueElement> getObjets() throws RemoteException;
	
	/**
	 * Renvoi la liste des éléments deconnectés
	 * @return liste des éléments déconnectés
	 * @throws RemoteException
	 */
	public List<VuePersonnageDeconnecte> getHell() throws RemoteException;
	
	/**
	 * Liste les voisins d'une console de l'arene
	 * @param console console dont on veut les voisins
	 * @return table des couples reference/coordonnées des voisins
	 * @throws RemoteException
	 */
	public Hashtable<Integer, Point> voisins(IConsole console) throws RemoteException;

	
	/**
	 * Renvoi les vuesPersonnages trié par classement de la partie
	 * @return liste des VuePersonnage trié par ordre de classement
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getClassementVuesRMI() throws RemoteException;

	/* ***************************************************
	 * accesseurs sur les ElementServeur
	 */
	
	/**
	 * Permet de récuperer une copie de l'ElementServeur correspondant à la console
	 * Normalement seul la machine hebergeant le serveur ou la machine faisant tourner le client passe en parametre peuvent executer cette fonction
	 * @param console
	 * @return une copie de l'ElementServeur correspondant à la console de référence refRMI
	 * @throws RemoteException
	 */
	public Element getMyElement(IConsole console) throws RemoteException;
	
	/**
	 * Permet de récuperer une copie de l'ElementServeur correspondant à la console de référence refRMI
	 * N'importe qui peut executer cette fonction là ! il faut faire attention aux données que l'ont accepte de rendre accessible
	 * @param refRMI
	 * @return une copie de l'ElementServeur correspondant à la console de référence refRMI
	 * @throws RemoteException
	 */
	public Element getAnElement(int refRMI) throws RemoteException;

	/* ***************************************************
	 * gestions des interactions
	 */
	
	
	/**
	 * permet d'ajouter une potion dans l'arene à n'importe qu'elle moment en mode arene libre
	 * et permet d'ajouter un potion dans l'arene avant la partie en mode tournoi
	 * @param nom le nom de la potion
	 * @param groupe le groupe de la potion
	 * @param carac les données de la potion
	 * @throws RemoteException
	 */
	public void ajouterPotion(String nom, String groupe, Hashtable<Caracteristique,Integer> carac) throws RemoteException;
		
	
	public void ajouterTresor(String nom, String groupe, int montant) throws RemoteException;

	
	/**
	 * Demande au serveur d'ameliorer les caracteristique
	 * @param console celui qui veut s'ameliorer
	 * @param caracts amelioration
	 * @return true si l'amelioration a eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean ameliorerCaracteristique(IConsole console, Hashtable<Caracteristique, Integer> caracts) throws RemoteException;
	
	/**
	 * demander au serveur de ramasser une potion
	 * @param console celui qui veut ramasser une potion
	 * @param refObjet la potion qui doit être ramassé
	 * @return true si l'action a bien eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean ramasserObjet(IConsole console, int refObjet) throws RemoteException;
	
	/**
	 * demander au serveur de lancer un duel
	 * @param console celui qui demande un duel
	 * @param refAdv l'adversaire de ref au cours de ce duel
	 * @return true si l'action a bien eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean lancerUneAttaque(IConsole console, int refAdv) throws RemoteException;
	
	/**
	 * demander au serveur de lancer un duel
	 * @param console celui qui demande un duel
	 * @param refAdv l'adversaire de ref au cours de ce duel
	 * @return true si l'action a bien eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean lancerUneExtorsion(IConsole console, int refAdv, int argentDemande) throws RemoteException;
	
	/**
	 * demander au serveur un deplacement d'un element
	 * @param console la console qui veut se deplacer
	 * @param refCible l'element vers lequel on veut se deplacer, 0 si on veut se deplacer aléatoirement
	 * @return true si l'action a bien eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean deplacer(IConsole console, int refCible) throws RemoteException;
	
	/**
	 * demander au serveur un deplacement d'un element (attention le point cible sera caper aux limites de la map)
	 * @param console la console qui veut se deplacer
	 * @param objectif le point vers lequel on veut se deplacer
	 * @return true si l'action a bien eu lieu, false sinon
	 * @throws RemoteException
	 */
	public boolean deplacer(IConsole console, Point objectif) throws RemoteException;
	
	/* ***************************************************
	 * accesseurs sur les VueElement
	 */
	
	public void setPhrase(IConsole console, String s) throws RemoteException;
	
	
	/**
	 * Permet de récuperer une copie de la VueElement correspondant à la console
	 * @param console
	 * @return une copie de la VueElement correspondant à la console de référence refRMI
	 * @throws RemoteException
	 */
	public VueElement getMyVueElement(IConsole console) throws RemoteException;
	
	/**
	 * Permet de récuperer une copie de la VueElement correspondant à la console de référence refRMI
	 * @param refRMI
	 * @return une copie de la VueElement correspondant à la console de référence refRMI
	 * @throws RemoteException
	 */
	public VueElement getAnVueElement(int refRMI) throws RemoteException;
	
	/**
	 * Permet de savoir la position d'un élément
	 * @param refRMI reference de l'élément
	 * @return position de l'élément
	 * @throws RemoteException
	 */
	public Point getPosition(int refRMI) throws RemoteException;
	
	/**
	 * Permet de connaitre le nombre de tours restants
	 * @return nombre de tours restant
	 * @throws RemoteException
	 */
	public int getNbToursRestants() throws RemoteException ;

	/**
	 * Permet de savoir le nombre de tours écoulés
	 * @return nombre de tour écoulés
	 * @throws RemoteException
	 */
	public int getNbTour() throws RemoteException;

	/**
	 * 
	 * @return true si la partie à commencé, false sinon
	 * @throws RemoteException
	 */
	boolean isPartieCommencee() throws RemoteException;
	
	/*
	 * METHODES UNIQUEMENT DESTINÉES A L'ARÈNE TOURNOI
	 */

	/**
	 * Renvoi la liste des objets en attente de connexion
	 * @return liste des éléments en attente
	 * @throws RemoteException
	 */
	public List<VueElement> getObjetsEnAttente() throws RemoteException;	
	
	/**
	 * permet d'ajouter une potion dans l'arene a n'importe qu'elle moment
	 * mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param carac les données de la potion
	 * @param position la position ou la potion doit être déposé
	 * @param mdp mot de passe d'administrateur
	 * @throws RemoteException
	 */
	public void ajouterPotionSecurisee(String nom, Hashtable<Caracteristique,Integer> carac, Point position, String mdp) throws RemoteException;
	
	/**
	 * permet d'ajouter un tresor dans l'arene a n'importe qu'elle moment
	 * mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param montant montant du tresor
	 * @param position la position ou la potion doit être déposé
	 * @param mdp mot de passe d'administrateur
	 * @throws RemoteException
	 */
	public void ajouterTresorSecurisee(String nom, int montant, Point position, String mdp)	throws RemoteException;
	
	/**
	 * Permet de lancer un objet en attente dans la partie
	 * @param ref reference de l'objet à lancer
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancerObjetEnAttente(int ref, String mdp) throws RemoteException ;
	
	/**
	 * Permet de lancer la partie
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void commencerPartie(String motDePasse) throws RemoteException;
	
	/**
	 * Permet de renvoyer un joueur de la partie
	 * @param joueur VueElement du joueur
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void renvoyer(VueElement joueur, String motDePasse) throws RemoteException;
	
	/**
	 * Permet de vérifier un mot de passe
	 * @param motDePasse mot de passe à verifier
	 * @return true si le mot de passe est ok, false sinon
	 * @throws RemoteException
	 */
	public boolean verifMotDePasse(char[] motDePasse) throws RemoteException;

	/**
	 * Permet de savoir si un element est en attente
	 * @param refRMI reference de l'élément
	 * @return true si l'élément est en attente, false sinon
	 * @throws RemoteException
	 */
	public boolean isEnAttente(int refRMI) throws RemoteException;

	/**
	 * Permet de savoir si la partie est finie
	 * @return true si la partie est finie, false sinon
	 * @throws RemoteException
	 */
	public boolean isPartieFinieRMI() throws RemoteException;

	/**
	 * Permet de recuperer la vue de l'élément gagnant de la partie
	 * @return vueElement du gagnant
	 * @throws RemoteException
	 */
	public VueElement getVueGagnant() throws RemoteException;
	
	
}

