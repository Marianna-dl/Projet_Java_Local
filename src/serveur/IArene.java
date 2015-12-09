package serveur;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import serveur.controle.IConsole;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;

/**
 * Definit les methodes qui pourront s'appliquer a travers l'arene en RMI (toutes celles qui levent RemoteException)
 */
public interface IArene extends Remote {
	
	
	/**************************************************************************
	 * Connexion et deconnexion.
	 **************************************************************************/
	
	/**
	 * Retourne une reference RMI libre pour un element.
	 * @return reference RMI inutilisee
	 */
	public int allocateRefRMI() throws RemoteException;
	
	/**
	 * Connecte un personnage a l'arene.
	 * @param refRMI reference RMI de l'element a connecter
	 * @param ipConsole ip de la console correspondant au personnage
	 * @param personnage personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position courante
	 * @return vrai si l'element a ete connecte, faux sinon
	 * @throws RemoteException
	 */
	public boolean connecte(int refRMI, String ipConsole, 
			Personnage personnage, long nbTours, Point position) throws RemoteException;
	
	/**
	 * Deconnecte un element du serveur.
	 * @param console console correspondant au personnage a deconnecter
	 * @param cause cause de la deconnexion
	 * @throws RemoteException
	 */
	public void deconnecteConsole(IConsole console, String cause) throws RemoteException;
	
	/**
	 * Verifie le mot de passe administrateur (mode tournoi). 
	 * @param motDePasse mot de passe a verifier
	 * @return true si le mot de passe est ok, false sinon
	 * @throws RemoteException
	 */
	public boolean verifieMotDePasse(char[] motDePasse) throws RemoteException;

	/**
	 * Teste si la partie est finie.
	 * @return true si la partie est finie, false sinon
	 * @throws RemoteException
	 */
	public boolean isPartieFinie() throws RemoteException;

	/**
	 * Permet de renvoyer un joueur de la partie (mode tournoi). 
	 * @param personnage personnage
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void ejectePersonnage(VuePersonnage personnage, String motDePasse) throws RemoteException;

	/**
	 * Permet de lancer la partie (mode tournoi).
	 * @param motDePasse mot de passe administrateur
	 * @throws RemoteException
	 */
	public void commencerPartie(String motDePasse) throws RemoteException;

	/**
	 * Teste si la partie a commence.
	 * @return vrai si la partie a commence, faux sinon
	 * @throws RemoteException
	 */
	boolean isPartieCommencee() throws RemoteException;

	/**
	 * Permet d'ajouter une potion dans l'arene a n'importe quel moment en mode 
	 * arene libre, et d'ajouter un potion dans l'arene avant la partie en mode 
	 * tournoi.
	 * @param potion potion
	 * @throws RemoteException
	 */
	public void ajoutePotion(Potion potion) throws RemoteException;
	
	/**
	 * Permet d'ajouter une potion en attente dans l'arene a n'importe quel 
	 * moment, en fournissant le mot de passe (mode tournoi).
	 * @param potion potion
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException	
	 */
	public void ajoutePotionSecurisee(Potion potion, Point position, String mdp) throws RemoteException;

	/**
	 * Permet de lancer une potion en attente dans la partie (mode tournoi). 
	 * @param vuePotion potion a lancer
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancePotionEnAttente(VuePotion vuePotion, String mdp) throws RemoteException;
	
	

	/**************************************************************************
	 * Accesseurs sur les elements du serveur. 
	 **************************************************************************/
	/**
	 * Permet de connaitre le nombre de tours restants
	 * @return nombre de tours restant
	 * @throws RemoteException
	 */
	public int getNbToursRestants() throws RemoteException ;

	/**
	 * Permet de savoir le nombre de tours ecoules
	 * @return nombre de tour ecoules
	 * @throws RemoteException
	 */
	public int getTour() throws RemoteException;

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
	 * Renvoie la liste des potions en attente de connexion (mode tournoi).
	 * @return liste des potions en attente
	 * @throws RemoteException
	 */
	public List<VuePotion> getPotionsEnAttente() throws RemoteException;

	/**
	 * Calcule la liste les voisins d'une console de l'arene.
	 * @param console console dont on veut les voisins
	 * @return map des couples reference/coordonnees des voisins
	 * @throws RemoteException
	 */
	public HashMap<Integer, Point> getVoisins(IConsole console) throws RemoteException;
	
	/**
	 * Renvoie la liste des personnages tries pour le classement final.
	 * @return liste des personnages ordonnes pour le classement final
	 * @throws RemoteException
	 */
	public List<VuePersonnage> getClassement() throws RemoteException;
	
	/**
	 * Recupere la vue du personnage gagnant de la partie.
	 * @return vue du personnage gagnant
	 * @throws RemoteException
	 */
	public VuePersonnage getGagnant() throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la reference 
	 * RMI.
	 * @param refRMI reference RMI
	 * @return copie de l'element correspondant a la reference RMI donnee
	 * @throws RemoteException
	 */
	public Element elementFromRef(int refRMI) throws RemoteException;

	/**
	 * Permet de recuperer une copie de l'element correspondant a la console.
	 * @param console console
	 * @return copie de l'element correspondant a la console donnee
	 * @throws RemoteException
	 */
	public Element elementFromConsole(IConsole console) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la reference RMI donnee.
	 * @param refRMI reference RMI
	 * @return vue correspondante
	 */
	public VueElement vueFromRef(int refRMI) throws RemoteException;
	
	/**
	 * Renvoie la vue correspondant a la console donnee.
	 * @param console console
	 * @return vue correspondante
	 * @throws RemoteException
	 */
	public VueElement vueFromConsole(IConsole console) throws RemoteException;

	/**
	 * Permet de savoir la position d'un element
	 * @param refRMI reference de l'element
	 * @return position de l'element
	 * @throws RemoteException
	 */
	public Point getPosition(int refRMI) throws RemoteException;

	/**
	 * Permet de savoir si un element est en attente.
	 * @param refRMI reference RMI de l'element
	 * @return vrai si l'element est en attente, faux sinon
	 * @throws RemoteException
	 */
	public boolean isEnAttente(int refRMI) throws RemoteException;
	

	/**************************************************************************
	 * Gestion des interactions.
	 **************************************************************************/

	/**
	 * Execute le ramassage d'une potion par un personnage, si possible.
	 * Le ramassage echoue si une action a deja ete executee ce tour par ce 
	 * personnage, ou si la potion est trop loin du personnage.
	 * @param console personnage voulant ramasser une potion
	 * @param refPotion reference RMI de la potion qui doit etre ramasse
	 * @return vrai si l'action a ete effectuee, faux sinon
	 * @throws RemoteException
	 */
	public boolean ramassePotion(IConsole console, int refPotion) throws RemoteException;
	
	/**
	 * Execute un duel entre le personnage correspondant a la console donnee 
	 * et l'adversaire correspondant a la reference RMI donnee.
	 * Le duel echoue si une action a deja ete executee a ce tour par 
	 * l'attaquant, si les personnages sont trop eloignes, si l'un des deux 
	 * n'est plus actif (mort)
	 * @param console attaquant, qui demande un duel
	 * @param refAdv reference RMI du defenseur
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean lanceAttaque(IConsole console, int refAdv) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers l'element 
	 * correspondant a la reference RMI cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param console personnage voulant se deplacer
	 * @param refCible reference RMI de l'element vers lequel on veut se 
	 * deplacer, ou 0 si on veut se deplacer aleatoirement
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplacer(IConsole console, int refCible) throws RemoteException;
	
	/**
	 * Deplace le personnage correspondant a la console donne vers le point 
	 * cible.
	 * Le deplacement echoue si une action a deja ete executee a ce tour par 
	 * ce personnage.
	 * @param console personnage voulant se deplacer
	 * @param objectif point vers lequel on veut se deplacer
	 * @return vrai si l'action a bien eu lieu, faux sinon
	 * @throws RemoteException
	 */
	public boolean deplacer(IConsole console, Point objectif) throws RemoteException;
	
	/**
	 * Modifie la phrase du personnage correspondant a la console donnee.
	 * @param console console
	 * @param s nouvelle phrase
	 * @throws RemoteException
	 */
	public void setPhrase(IConsole console, String s) throws RemoteException;
	
	
}

