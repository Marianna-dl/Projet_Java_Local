package serveur;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import logger.MyLogger;
import serveur.controle.IConsolePersonnage;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import serveur.infosclient.VueElement;
import serveur.infosclient.VuePotion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * 
 * @author valentinchevalier
 *
 */
public class AreneTournoi extends Arene {

	private static final long serialVersionUID = 1L;
	
	private String motDePasse;
	
	/**
	 * Booleen permettant de savoir si la partie est commencee
	 */
	protected boolean partieCommencee;

	public AreneTournoi(int port, String ipName, long TTL, MyLogger logger) throws Exception {
		super(port, ipName, TTL, logger);
		synchronized (this) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Veuillez definir le mot de passe du serveur.");
			motDePasse = sc.nextLine();
			sc.close();
		}

//		clientsPotionNonEnJeu = new Hashtable<Integer, VuePotion>();
		partieCommencee = false;
	}
	
	public boolean isPartieCommencee() throws RemoteException {
		return partieCommencee;
	}
	
	@Override
	public void run() {
		// Tant que la partie n'est pas commencee, on attend
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
	public void updatePartieFinie() {
		super.updatePartieFinie();
		// La partie est terminee si il y a un seul personnage
		setPartieFinie(countPersonnages() <= 1 || isPartieFinie());
	}

	/**
	 * permet d'ajouter une potion dans l'arene a n'importe qu'elle moment, mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param carac les donnees de la potion
	 * @param position la position ou la potion doit etre depose
	 * @param mdp le mot de passe d'administrateur
	 * @throws RemoteException
	 */
	public synchronized void ajouterPotionSecurisee(String nom, HashMap<Caracteristique,Integer> carac, Point position, String mdp) throws RemoteException {
		ajouterPotionSecurisee(nom, new Potion(nom, "Arene", carac), position, mdp);				
	}
	
	private void ajouterPotionSecurisee(String nom, Potion element, Point position, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			int ref = allocateRef();
			VuePotion client = new VuePotion(element, position, ref, true);
			client.setPhrase("");
			super.ajouterClientEnJeu(ref, client);
			String type = "de la potion";
			myLogger.info(this.getClass().toString(), "Ajout "+type+" "+ client.getElement().getNomGroupe()+" ("+ref+")");
			printElements();
		} else {
			myLogger.info("Tentative d'ajout de potion avec mot de passe errone");
		}
	}

	/**
	 * ajoute une potion dans la salle d'attente de l'arene tournoi
	 * @param nom le nom de la potion
	 * @param groupe le groupe de la potion
	 * @param carac les donnees de la potion
	 * @throws RemoteException
	 */
	public synchronized void ajouterPotion(String nom, String groupe, HashMap<Caracteristique,Integer> carac) throws RemoteException {

		int ref = allocateRef();
		VuePotion client = new VuePotion(
				new Potion(nom, groupe, carac),
				new Point(Calculs.randomNumber(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE), Calculs.randomNumber(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE)), ref, false);
		myLogger.info(this.getClass().toString(), "Ajout de la potion "+ Arene.nomCompletClient(client) +" ("+ref+") dans la file d'attente");
		printElements();
	}
	
	/**
	 * Lance une potion de la salle d'attente dans l'arene
	 * @param ref reference de la potion a lancer
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException
	 */
	public void lancerPotionEnAttente(int ref, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			VuePotion client = clientsPotions.get(ref);
			
			if (client != null) {
				client.envoyer();
				client.setPhrase("");
				ajouterClientEnJeu(ref, client);
			}
			
			myLogger.info(this.getClass().toString(), "Lancement de la potion "+ client.getElement().getNomGroupe()+" ("+ref+") dans la partie");
			printElements();
		} else {
			myLogger.info("Tentative de lancement de potion en attente avec mot de passe errone");			
		}
	}	


	/**
	 * Recupere tous les elements en attente de connexion
	 */
	public List<VuePotion> getPotionsEnAttente() throws RemoteException{
		ArrayList<VuePotion> aux = new ArrayList<VuePotion>();
		for(VuePotion client : clientsPotions.values()) {
			if(client.isEnAttente()) {
				client.setPhrase("En attente!");
				aux.add(client);
			}
		}
		return aux;
	}
	
	public void commencerPartie(String motDePasse) throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			partieCommencee = true;
			myLogger.info("Demarrage de la partie");
		} else {
			myLogger.info("Tentative de lancement de partie avec mot de passe errone");
		}
	}

	public void ejecter(VueElement joueur, String motDePasse)
			throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			IConsolePersonnage console = consoleFromRef(joueur.getRefRMI());
			if (console == null) {
				ejecterPersonnage(joueur.getRefRMI());
			} else {
				deconnecterConsole(console, "Vous avez ete renvoye du salon.");
			}
		} else {
			myLogger.info("Tentative d'exclusion de personnage avec mot de passe errone");
		}

	}
	
	public boolean verifMotDePasse(char[] motDePasse) throws RemoteException{
		boolean retour = false;
		if (motDePasse != null)
			retour = motDePasse.length == this.motDePasse.length();
		for(int i = 0; i < motDePasse.length && retour; i++) {
			retour = this.motDePasse.charAt(i) == motDePasse[i];
		}
		return retour;
	}

	@Override
	public String getPrintElementsMessage() {
		String msg = super.getPrintElementsMessage();
		for(VueElement client : clientsPotions.values()) {
			if(client.isEnAttente()) {
				msg += "\n"+Arene.nomCompletClient(client)+ " (en attente)";
			}
		}
		return msg;
	}

	@Override
	public Point getPosition(int ref) throws RemoteException {
		Point p = super.getPosition(ref);
		return new Point(p.x, p.y);
	}
}
