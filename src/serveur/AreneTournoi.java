package serveur;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import client.controle.IConsole;
import logger.LoggerProjet;
import serveur.element.Potion;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
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

	/**
	 * Constructeur de l'arene de tournoi.
	 * @param port le port de connexion
	 * @param adresseIP nom de la machine qui heberge l'arene
	 * @param nbTours duree de vue du serveur en nombre de tours de jeu 
	 * (si negatif, duree illimitee)
	 * @param logger gestionnaire de log 
	 * @throws Exception
	 */
	public AreneTournoi(int port, String adresseIP, long nbTours, LoggerProjet logger) throws Exception {
		super(port, adresseIP, nbTours, logger);
		
		synchronized (this) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Veuillez definir le mot de passe du serveur.");
			motDePasse = sc.nextLine();
			sc.close();
		}
		
		partieCommencee = false;
	}
	
	@Override
	public void run() {
		// tant que la partie n'est pas commencee, on attend 
		// (en verifiant regulierement)
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
	public boolean verifieMotDePasse(char[] motDePasse) throws RemoteException{
		boolean retour = false;
		if (motDePasse != null)
			retour = motDePasse.length == this.motDePasse.length();
		for(int i = 0; i < motDePasse.length && retour; i++) {
			retour = this.motDePasse.charAt(i) == motDePasse[i];
		}
		return retour;
	}

	@Override
	public void ejectePersonnage(int refRMI, String motDePasse) throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			IConsole console = consoleFromRef(refRMI);
			
			if (console != null) {
				try {
					// fermeture de la console en donnant la raison
					consoleFromRef(refRMI).shutDown("Vous avez ete renvoye du salon.");
					
				} catch (UnmarshalException e) {
					// ne rien faire
				}
			}
			
			// on tente d'ejecter des personnages et des potions (peu importe que l'un echoue)
			ejectePersonnage(refRMI);
			ejectePotion(refRMI);
			
		} else {
			logger.info("Tentative d'exclusion de personnage avec mot de passe errone");
		}
	
	}

	@Override
	public void commencerPartie(String motDePasse) throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			partieCommencee = true;
			logger.info("Demarrage de la partie");
			
		} else {
			logger.info("Tentative de lancement de partie avec mot de passe errone");
		}
	}

	@Override
	public boolean isPartieCommencee() throws RemoteException {
		return partieCommencee;
	}

	@Override
	public void verifierPartieFinie() {
		super.verifierPartieFinie();
		
		// la partie est aussi terminee s'il n'y a qu'un seul personnage
		partieFinie = partieFinie || countPersonnages() <= 1;
	}
	
	@Override
	public synchronized void ajoutePotionSecurisee(Potion potion, Point position, 
			String mdp) throws RemoteException {
		
		if (motDePasse.equals(mdp)) {
			int refRMI = allocateRefRMI();
			VuePotion vuePotion = new VuePotion(potion, position, refRMI, false); // ne pas envoyer immediatement
			vuePotion.setPhrase("En attente !");
			
			// ajout a la liste
			potions.put(refRMI, vuePotion);
			
			logger.info(Constantes.nomClasse(this), "Ajout de la potion " + vuePotion.getElement().getNomGroupe()+" ("+refRMI+")");
			logElements();
		} else {
			logger.info("Tentative d'ajout de potion avec mot de passe errone");
		}			
	}
	
	@Override
	public void lancePotionEnAttente(int refRMI, String mdp) throws RemoteException {
		if (this.motDePasse.equals(mdp)) {
			VuePotion vuePotion = potions.get(refRMI);
			
			vuePotion.envoyer();
			vuePotion.setPhrase("");
			
			logger.info(Constantes.nomClasse(this), "Lancement de la potion " + 
					Constantes.nomCompletClient(vuePotion) + " (" + refRMI + ") dans la partie");
			
			logElements();
		} else {
			logger.info("Tentative de lancement de potion en attente avec mot de passe errone");			
		}
	}	

	@Override
	public List<VuePotion> getPotionsEnAttente() throws RemoteException{
		ArrayList<VuePotion> aux = new ArrayList<VuePotion>();
		
		for(VuePotion vuePotion : potions.values()) {
			if(vuePotion.isEnAttente()) {
				aux.add(vuePotion);
			}
		}
		
		return aux;
	}

	@Override
	public String getPrintElementsMessage() {		
		String msg = "";
		
		for(VuePersonnage vuePers : personnages.values()) {
			msg += "\n" + Constantes.nomCompletClient(vuePers);
		}
		
		for(VuePersonnage vuePers : personnagesMorts) {
			msg += "\n" + Constantes.nomCompletClient(vuePers);
		}
		
		for(VuePotion vuePot : potions.values()) {
			if(vuePot.isEnAttente()) {
				msg += "\n" + Constantes.nomCompletClient(vuePot) + " (en attente)";
			} else {
				msg += "\n" + Constantes.nomCompletClient(vuePot);
			}
		}
		
		return msg;
	}
}
