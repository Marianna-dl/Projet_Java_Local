package serveur;

import interfaceGraphique.view.VueElement;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import client.controle.IConsole;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Potion;
import serveur.element.Tresor;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;

/**
 * 
 * @author valentinchevalier
 *
 */
public class AreneTournoi extends Arene {

	private static final long serialVersionUID = 1L;
	
	private String motDePasse;
	
	/**
	 * Booleen permettant de savoir si la partie est commencée
	 */
	protected boolean partieCommencee;

	/**
	 * Repertoire des refRMI et des instances de la classe ClientObjet contenant toutes les donnees des objets qui devront être mis en jeu plus tard
	 */
	private Hashtable<Integer, ClientElement> clientObjetNonEnJeu = null;

	public AreneTournoi(int port, String ipName, long TTL, MyLogger logger) throws Exception {
		super(port, ipName, TTL, logger);
		synchronized (this) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Veuillez definir le mot de passe du serveur.");
			motDePasse = sc.nextLine();
			sc.close();
		}

		clientObjetNonEnJeu = new Hashtable<Integer, ClientElement>();
		partieCommencee = false;
	}
	
	public boolean isPartieCommencee() throws RemoteException {
		return partieCommencee;
	}
	
	@Override
	public void run() {
		// Tant que la partie n'est pas commencée, on attend
		while (!partieCommencee) {
			try {
				Thread.sleep(500);
			} catch(Exception e) {
				myLogger.severe(this.getClass().toString(), "Erreur : run\n" + e.toString());
				e.printStackTrace();
			}
		}
		super.run();
	}
	
	
	
	@Override
	public void updatePartieFinie(){
		super.updatePartieFinie();
		// La partie est terminée si il y a un seul personnage ou une seul equipe
		setPartieFinie(countPersonnages() <= 1 || isPartieFinie());
	}

	/**
	 * permet d'ajouter une potion dans l'arene a n'importe qu'elle moment, mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param carac les données de la potion
	 * @param position la position ou la potion doit etre depose
	 * @param mdp le mot de passe d'administrateur
	 * @throws RemoteException
	 */
	public synchronized void ajouterPotionSecurisee(String nom, Hashtable<Caracteristique,Integer> carac, Point position, String mdp) throws RemoteException {
		ajouterObjetSecurisee(nom, new Potion(nom, "Arene", carac), position, mdp);				
	}
	
	/**
	 * permet d'ajouter une potion dans l'arene a n'importe qu'elle moment, mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param montant montant du tresor
	 * @param position la position ou la potion doit etre depose
	 * @param mdp le mot de passe d'administrateur
	 * @throws RemoteException
	 */
	public synchronized void ajouterTresorSecurisee(String nom, int montant, Point position, String mdp) throws RemoteException {
		ajouterObjetSecurisee(nom, new Tresor(nom, "Arene", montant), position, mdp);			
	}
	
	private void ajouterObjetSecurisee(String nom, Element element, Point position, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			int ref = allocateRef();
			ClientElement client = new ClientElement(element, position, ref);
			client.getVue().setPhrase("");
			super.ajouterClientEnJeu(ref, client);
			String type = "de l'objet";
			if (element instanceof Potion)
				type = "de la potion";
			if (element instanceof Tresor)
				type = "du trésor";
			myLogger.info(this.getClass().toString(), "Ajout "+type+" "+ client.getElement().getNomGroupe()+" ("+ref+")");
			printElements();
		} else {
			myLogger.info("Tentative d'ajout d'objet avec mot de passe erroné");
		}
	}

	/**
	 * ajoute une potion dans la salle d'attente de l'arene tournoi
	 * @param nom le nom de la potion
	 * @param groupe le groupe de la potion
	 * @param carac les données de la potion
	 * @throws RemoteException
	 */
	public synchronized void ajouterPotion(String nom, String groupe, Hashtable<Caracteristique,Integer> carac) throws RemoteException {

		int ref = allocateRef();
		ClientElement client = new ClientElement(
				new Potion(nom, groupe, carac),
				new Point(Calculs.randomNumber(XMIN, XMAX), Calculs.randomNumber(YMIN, YMAX)), ref);
		clientObjetNonEnJeu.put(ref, client);
		myLogger.info(this.getClass().toString(), "Ajout de la potion "+ Arene.nomCompletClient(client) +" ("+ref+") dans la file d'attente");
		printElements();
	}
	
	/**
	 * ajoute une potion dans la salle d'attente de l'arene tournoi
	 * @param nom le nom de la potion
	 * @param groupe le groupe de la potion
	 * @param montant montant du tresor
	 * @throws RemoteException
	 */
	public synchronized void ajouterTresor(String nom, String groupe, int montant) throws RemoteException {
		ajouterObjet(nom, groupe, new Tresor(nom, groupe, montant));		
	}
	
	private void ajouterObjet(String nom, String groupe, Element element) throws RemoteException{
		int ref = allocateRef();
		ClientElement client = new ClientElement( element,
				new Point(Calculs.randomNumber(XMIN, XMAX), Calculs.randomNumber(YMIN, YMAX)), ref);
		clientObjetNonEnJeu.put(ref, client);
		String type = "de l'objet";
		if (element instanceof Potion)
			type = "de la potion";
		if (element instanceof Tresor)
			type = "du trésor";
		myLogger.info(this.getClass().toString(), "Ajout "+type+" "+ Arene.nomCompletClient(client) +" ("+ref+") dans la file d'attente");
		printElements();
	}

	/**
	 * Lance un objet de la salle d'attente dans l'arène
	 * @param ref reference de la potion à lancer
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancerObjetEnAttente(int ref, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			ClientElement client = clientObjetNonEnJeu.get(ref);
			if (client != null){
				clientObjetNonEnJeu.remove(ref);
				client.getVue().setPhrase("");
				ajouterClientEnJeu(ref, client);
			}			
			myLogger.info(this.getClass().toString(), "Lancement de la potion "+ client.getElement().getNomGroupe()+" ("+ref+") dans la partie");
			printElements();
		} else {
			myLogger.info("Tentative de lancement de potion en attente avec mot de passe erroné");			
		}
	}	


	/**
	 * Récupere tous les éléments en attente de connexion
	 */
	public List<VueElement> getObjetsEnAttente() throws RemoteException{
		ArrayList<VueElement> aux = new ArrayList<VueElement>();
		for(ClientElement client : clientObjetNonEnJeu.values()) {
			VueElement ve = client.getVue();
			ve.setPhrase("En attente!");
			ve.setEnAttente(true);
			aux.add(ve);
		}
		return aux;
	}
	
	public void commencerPartie(String motDePasse) throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			partieCommencee = true;
			myLogger.info("Demarrage de la partie");
		} else {
			myLogger.info("Tentative de lancement de partie avec mot de passe erroné");
		}
	}

	public void renvoyer(VueElement joueur, String motDePasse)
			throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			IConsole console = consoleFromRef(joueur.getRefRMI());
			if (console == null){
				ejecterObjet(getClientElement(joueur.getRefRMI()));
			} else {
				deconnecterConsole(console, "Vous avez été renvoyé du salon.");
			}
		} else {
			myLogger.info("Tentative d'exclusion de personnage avec mot de passe erroné");
		}

	}
	
	public boolean verifMotDePasse(char[] motDePasse) throws RemoteException{
		boolean retour = false;
		if (motDePasse != null)
			retour = motDePasse.length == this.motDePasse.length();
		for(int i = 0; i < motDePasse.length && retour; i++){
			retour = this.motDePasse.charAt(i) == motDePasse[i];
		}
		return retour;
	}

	@Override
	public String getPrintElementsMessage(){
		String msg = super.getPrintElementsMessage();
		for(ClientElement client : clientObjetNonEnJeu.values()) {
			msg += "\n"+Arene.nomCompletClient(client)+ "(en attente)";
		}
		return msg;
	}
	
	@Override
	protected ClientElement getClientElement(int ref) {
		ClientElement client = super.getClientElement(ref);
		if (client == null)
			client = clientObjetNonEnJeu.get(ref);
		return client;
	}

	@Override
	public Point getPosition(int ref) throws RemoteException {
		Point p = super.getPosition(ref);		
		if (p == null)
			p = clientObjetNonEnJeu.get(ref).getPosition();
		return new Point(p.x, p.y);
	}
	
	@Override
	public boolean isEnAttente(int refRMI) throws RemoteException {
		return clientObjetNonEnJeu.containsKey(refRMI);
	}
}
