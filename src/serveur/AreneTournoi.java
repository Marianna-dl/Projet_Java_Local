package serveur;

import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.Scanner;

import client.controle.IConsole;
import logger.LoggerProjet;
import serveur.element.Personnage;
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
	private boolean partieCommencee;

	/**
	 * Constructeur de l'arene de tournoi.
	 * @param port le port de connexion
	 * @param adresseIP nom de la machine qui heberge l'arene
	 * @param nbTours duree de vue du serveur en nombre de tours de jeu 
	 * (si negatif, duree illimitee)
	 * @param logger gestionnaire de log 
	 * @throws Exception
	 */
	public AreneTournoi(int port, String adresseIP, int nbTours, LoggerProjet logger) throws Exception {
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
	public synchronized boolean connecte(int refRMI, String ipConsole, 
			Personnage personnage, int nbTours, Point position) throws RemoteException {
		boolean res;
		
		int portConsole = port + refRMI;
		String adr = Constantes.nomRMI(ipConsole, portConsole, "Console" + refRMI);
		
		if(partieCommencee) {
			// refus si la partie a commence
			res = false;
			
			logger.info(Constantes.nomClasse(this), 
					"Demande de connexion refusee (partie deja commencee) (" + adr + ")");
		} else {
			res = super.connecte(refRMI, ipConsole, personnage, nbTours, position);
		}
		
		return res;
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
		if(this.motDePasse.equals(motDePasse)) {
			IConsole console = consoleFromRef(refRMI);
			
			if (console != null) {
				try {
					// fermeture de la console en donnant la raison
					consoleFromRef(refRMI).deconnecte("Vous avez ete renvoye du salon.");
					
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
	public synchronized void lancePotion(Potion potion, Point position, String motDePasse) throws RemoteException {
		if (motDePasse.equals(motDePasse)) {
			int refRMI = alloueRefRMI();
			VuePotion vuePotion = new VuePotion(potion, position, refRMI);
			
			// ajout a la liste
			potions.put(refRMI, vuePotion);
			
			logger.info(Constantes.nomClasse(this), "Lancement de la potion " + 
					vuePotion.getElement().getNomGroupe() + " (" + refRMI + ")");
			logElements();
		} else {
			logger.info("Tentative de lancement de potion avec mot de passe errone");
		}	
	}

	@Override
	public void commencePartie(String motDePasse) throws RemoteException {
		if (this.motDePasse.equals(motDePasse)) {
			partieCommencee = true;
			logger.info("Demarrage de la partie");
			
		} else {
			logger.info("Tentative de lancement de partie avec mot de passe errone");
		}
	}

	@Override
	public boolean estPartieCommencee() throws RemoteException {
		return partieCommencee;
	}

	@Override
	public void verifierPartieFinie() {
		super.verifierPartieFinie();
		
		// la partie est aussi terminee s'il n'y a qu'un seul personnage
		partieFinie = partieFinie || personnages.size() <= 1;
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
			msg += "\n" + Constantes.nomCompletClient(vuePot);
		}
		
		return msg;
	}
}
