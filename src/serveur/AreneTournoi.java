package serveur;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import logger.MyLogger;
import serveur.controle.IConsole;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import serveur.vuelement.VueElement;
import serveur.vuelement.VuePotion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Arene (serveur) pour le tournoi. Elle est associee a un mot de passe
 * qu'il faut connaitre pour effectuer des operations specifiques : 
 * ajouter une potion, ejecter un joueur...
 *
 */
public class AreneTournoi extends Arene {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Mot de passe administrateur.
	 */
	private String motDePasse;
	
	/**
	 * Booleen permettant de savoir si la partie est commencee
	 */
	protected boolean partieCommencee;

	public AreneTournoi(int port, String adresseIP, long TTL, MyLogger logger) throws Exception {
		super(port, adresseIP, TTL, logger);
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
				logger.severe(Constantes.nomClasse(this), "Erreur : run\n" + e.toString());
				e.printStackTrace();
			}
		}
		super.run();
	}
	
	
	
	@Override
	public void verifierPartieFinie() {
		super.verifierPartieFinie();
		// la partie est aussi terminee s'il n'y a qu'un seul personnage
		partieFinie = partieFinie || countPersonnages() <= 1;
	}

	/**
	 * permet d'ajouter une potion dans l'arene a n'importe qu'elle moment, mais il faut le mot de passe
	 * @param nom le nom de la potion
	 * @param carac les donnees de la potion
	 * @param position la position ou la potion doit etre depose
	 * @param mdp le mot de passe d'administrateur
	 * @throws RemoteException
	 */
	@Override
	public synchronized void ajoutePotionSecurisee(Potion potion, Point position, String mdp) throws RemoteException {
		ajouterPotionSecurisee(potion, position, mdp);				
	}
	
	private void ajouterPotionSecurisee(Potion element, Point position, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			int ref = allocateRefRMI();
			VuePotion client = new VuePotion(element, position, ref, true);
			client.setPhrase("");
			potions.put(ref, client);
			String type = "de la potion";
			logger.info(Constantes.nomClasse(this), "Ajout "+type+" "+ client.getElement().getNomGroupe()+" ("+ref+")");
			logElements();
		} else {
			logger.info("Tentative d'ajout de potion avec mot de passe errone");
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

		int ref = allocateRefRMI();
		VuePotion client = new VuePotion(
				new Potion(nom, groupe, carac),
				new Point(Calculs.randomNumber(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE), Calculs.randomNumber(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE)), ref, false);
		logger.info(Constantes.nomClasse(this), "Ajout de la potion "+ Constantes.nomCompletClient(client) +" ("+ref+") dans la file d'attente");
		logElements();
	}
	
	/**
	 * Lance une potion de la salle d'attente dans l'arene
	 * @param ref reference de la potion a lancer
	 * @param mdp mot de passe administrateur
	 * @throws RemoteException
	 */
	@Override
	public void lancerPotionEnAttente(VuePotion potion, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			potion.envoyer();
			potion.setPhrase("");
			potions.put(potion.getRefRMI(), potion);
			
			logger.info(Constantes.nomClasse(this), "Lancement de la potion " + 
					potion.getElement().getNomGroupe() + " ("+ref+") dans la partie");
			
			logElements();
		} else {
			logger.info("Tentative de lancement de potion en attente avec mot de passe errone");			
		}
	}	


	/**
	 * Recupere tous les elements en attente de connexion
	 */
	public List<VuePotion> getPotionsEnAttente() throws RemoteException{
		ArrayList<VuePotion> aux = new ArrayList<VuePotion>();
		for(VuePotion client : potions.values()) {
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
			logger.info("Demarrage de la partie");
		} else {
			logger.info("Tentative de lancement de partie avec mot de passe errone");
		}
	}

	public void ejecterPersonnage(VueElement joueur, String motDePasse)
			throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			IConsole console = consoleFromRef(joueur.getRefRMI());
			if (console == null) {
				ejecterPersonnage(joueur.getRefRMI());
			} else {
				deconnecteConsole(console, "Vous avez ete renvoye du salon.");
			}
		} else {
			logger.info("Tentative d'exclusion de personnage avec mot de passe errone");
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
		for(VueElement client : potions.values()) {
			if(client.isEnAttente()) {
				msg += "\n"+Constantes.nomCompletClient(client)+ " (en attente)";
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
