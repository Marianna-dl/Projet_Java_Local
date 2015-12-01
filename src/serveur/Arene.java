package serveur;

import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePersonnageDeconnecte;

import java.awt.Point;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.PersonnageServeur;
import serveur.element.Potion;
import serveur.interaction.Attaque;
import serveur.interaction.Deplacements;
import serveur.interaction.EntreElement;
import serveur.interaction.Ramassage;
import utilitaires.Calculs;
import utilitaires.logger.MyLogger;
import client.controle.IConsole;

/**
 * Definit le serveur de l'arene. 
 * Les elements, personnages, potions... Definit les methodes qui communiquent en RMI.
 */
public class Arene extends UnicastRemoteObject implements IArene, Runnable {

	private static final long serialVersionUID = 1L;

	/**
	 * Port a utiliser pour la connexion.
	 */
	private int port;

	/**
	 * Adresse IP de la machine hebergeant l'arene.
	 */
	private String ipName;

	/**
	 * Duree de vie du serveur en tour de jeu
	 */
	private long ttl = 60 * 30;

	/**
	 * Nombre de tours de jeu
	 */
	private int nbTours;

	/**
	 * Nombre d'elements connectes au serveur.
	 */
	private int compteur = 0;

	/**
	 * Repertoire des refRMI et des instances de la classe ClientPersonnage contenant toutes les donnees de chaque client
	 */	
	private Hashtable<Integer, ClientPersonnage> clientsPersonnages = null;

	/**
	 * Repertoire des refRMI et des instances de la classe ClientObjet contenant toutes les donnees de objet qui sont d'office en jeu
	 */
	private Hashtable<Integer, ClientElement> clientsObjets = null;

	/**
	 * Liste des elements deconnectees de l'arene
	 */
	private List<VuePersonnageDeconnecte> deconnectedElements = new ArrayList<VuePersonnageDeconnecte>();	

	/**
	 * Le gestionnaire des logs
	 */
	protected MyLogger myLogger;

	/**
	 * Marqueur de fin de partie
	 */
	private boolean partieFinie = false;
		
	/**
	 * Tour To Live du client ~ 20 minutes
	 */
	private static final int TTL_CLIENT = 60 * 20;
	
	public static final int XMIN = 0;
	public static final int XMAX = 100;
	public static final int YMIN = 0;
	public static final int YMAX = 100;

	/**
	 * Constructeur 
	 * @param port le port de connexion
	 * @param ipName nom de la machine qui heberge l'arene
	 * @param ttl duree de vue du serveur en tour de jeu
	 * @param logger gestionnire de log qui va etre utilise 
	 * @throws Exception
	 */
	public Arene(int port, String ipName, long ttl, MyLogger logger) throws Exception {
		super();
		this.port=port;
		this.ipName = ipName;
		if (ttl != 0) {
			this.ttl = ttl;
		}

		clientsPersonnages = new Hashtable<Integer, ClientPersonnage>();
		clientsObjets = new Hashtable<Integer, ClientElement>();
		this.myLogger = logger;

		// Ajout de l'arene au registre RMI
		Naming.rebind(adrToString(), this);
		myLogger.info(this.getClass().toString(), "Arene cree a l'adresse "+adrToString());

		new Thread(this).start();
	}	

	/**
	 * Construit l'adresse complete de l'arene sous forme de String
	 * @return adresse complete de l'arene
	 */
	private String adrToString() {
		return "rmi://"+ipName+":"+port+"/Arene";
	}

	/**
	 * la synchro permet de garantir l'acces a un seul thread a la fois au compteur++
	 */
	@Override
	public synchronized int allocateRef() throws RemoteException {
		compteur ++;
		return compteur;
	}


	@Override
	public void run() {
		TimeoutOp to;
		List<Integer> listRef;
		while (!partieFinie) {
			
			long begin = System.currentTimeMillis();
			
			synchronized (this) {
				/* on verouille le serveur durant un tour
				 * de jeu -> pas de connexion/deconnexion
				 * a cet instant, pour chaque client connecte, on verifie
				 * s'il est en vie
				 */

				// Tri des console par Initiative des consoles
				listRef = getSortedRefs();
				// Lancement de la strategie de chacun des personnage
				for (int refRMI : listRef) {
					try {
						IConsole console = consoleFromRef(refRMI);
						PersonnageServeur elems = (PersonnageServeur) getAnyElement(refRMI);
						
						/* peut etre que ce client a ete tue lors d'un
						 * duel plus tot dans ce tour si c'est le cas,
						 * il ne peut pas jouer son tour et il doit etre
						 * ejecte
						 */
						if (!elems.isAlive()) {
							myLogger.info(this.getClass().toString(), nomRaccourciClient(refRMI) + " est mort... Client ejecte");
							deconnecterConsole(console, "Vous etes mort...");
						} else {
							to = null;
							if (clientsPersonnages.get(refRMI).getTourSonne() == 0) {
								// Lancement de la strategie
								to = new TimeoutOp(console);
								// attente de la fin de la strategie (temps d'attente max 1 seconde)
								to.join(2000);
							}
							// action de fin de tour pour ce client
							clientsPersonnages.get(refRMI).finTour();
							if (to != null && to.isAlive()) {
								/* si alors que le temps d'attente max est
								 * ecoule la strategie est toujours en cours
								 * j'arrete la strat et j'ejecte le client
								 */
								to = null;
								myLogger.info( this.getClass().toString(), "Execution de la strategie de " + nomRaccourciClient(refRMI) + " trop longue ! Client ejecte");
								deconnecterConsole(console, "Execution de strategie trop longue. Degage !");
							} else {
								console = consoleFromRef(refRMI);
								if (console != null) {
									if (!elems.isAlive()) {
										myLogger.info(this.getClass().toString(), nomRaccourciClient(refRMI) + " est mort... Client ejecte");
										deconnecterConsole(console, "Vous etes mort...");
									} else if (clientsPersonnages.get(refRMI).getTTL() <= 0) {
										myLogger.info(this.getClass().toString(), "Fin du TTL de " + nomRaccourciClient(refRMI) + "... Client ejecte");
										deconnecterConsole(console, "Temps autorise dans l'arene ecoule, vous etes elimine !");
									} else if (elems instanceof PersonnageServeur) {
										if (!verifCaract((PersonnageServeur) elems)) {
											myLogger.info( this.getClass().toString(),
													nomRaccourciClient(refRMI) + " est un tricheur... Client ejecte");
											deconnecterConsole(console, "Vous etes mort pour cause de triche...");
										}
									}
								}
							}
						}
					} catch (Exception e) {
						myLogger.severe(this.getClass().toString(), "Erreur dans le run "
								+ "avec la console de reference " + refRMI + "\n" + e.toString());
						e.printStackTrace();
						ejecterPersonnage(refRMI);
					} // Fin try catch
				} // Fin for
			} // Fin synchronize
			nbTours++;
			
			updatePartieFinie();
			
			try {
				// dormir 'au plus' 1 seconde (difference temps execution est 1sec.)
				// pour permettre connexion/deconnexion des consoles
				long dureeTour = System.currentTimeMillis() - begin;
				long time = 1000 - dureeTour ;
				if (time > 0) Thread.sleep(time);

			} catch(Exception e) {
				myLogger.severe(this.getClass().toString(), "Erreur : run\n" + e.toString());
				e.printStackTrace();
			}
		} // Fin while
		
		fermerServeur();
	}

	/**
	 * Traitement de fermeture du serveur
	 */
	private void fermerServeur() {
		printClassement();

		// Apres une certaine duree (30mn par defaut), on ferme la "porte" RMI
		try {
			List<Integer> listRef = getSortedRefs();
			for (int refRMI : listRef) {
				deconnecterConsole(consoleFromRef(refRMI), "Fermeture du serveur");
			}

			myLogger.info(this.getClass().toString(),
					"Fin de la partie ! Fermeture du serveur");
			unexportObject(this, true);
			myLogger.info(this.getClass().toString(), "Serveur ferme");
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Renvoie la liste des references triees par ordre de passage
	 * @return liste de toutes les references des personnages de la partie
	 */
	private List<Integer> getSortedRefs(){
		List<PaireRefIntitiative> listRefsInitiative = new ArrayList<PaireRefIntitiative>();
		
		// On cree une liste de paires Refernece -> Initiative
		for(ClientPersonnage client : clientsPersonnages.values()){			
			listRefsInitiative.add(new PaireRefIntitiative(
					client.getRef(), 
					client.getElement().getCaract(Caracteristique.INITIATIVE)));
		}	
		/*
		 *  Tri des paires selon l'initiative
		 *  Si egalite alors aleatoire
		 */
		Collections.sort(listRefsInitiative, new Comparator<PaireRefIntitiative>() {

			@Override
			public int compare(PaireRefIntitiative paire1, PaireRefIntitiative paire2) {				
				int ret = paire2.getInitiative() - paire1.getInitiative();
				if (ret == 0)
					ret = Calculs.randomNumber(-100, 100);
				
				return ret;
			}			
		});
		
		// On recupere juste les references
		List<Integer> listRefsSorted = new ArrayList<Integer>();
		for (PaireRefIntitiative paire : listRefsInitiative){
			listRefsSorted.add(paire.getRef());
		}
	
		return listRefsSorted;
	}
	
	/**
	 * Classe permettant de lancer une execution du client (run)
	 * dans un thread separe, pour pouvoir limiter son temps d'execution
	 * via un join(timeout)
	 */
	public class TimeoutOp extends Thread {		
		private IConsole console;
		TimeoutOp(IConsole r) { this.console=r; start(); }
		public void run() {
			try {
				console.run(); //on lance une execution
			} catch (Exception e) {
				//les exceptions sont inhibees ici, que ce soit une deconnection du client ou autre
				//en cas d'erreur, le client ne sera plus jamais execute
				try {
					myLogger.warning(this.getClass().toString(), 
							"Erreur lors de l'execution de la strategie de "+nomRaccourciClient(console.getRefRMI())
							+" \n"+e.toString());
					deconnecterConsole(console, e.toString());
					e.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} 
		}
	}

	/**
	 * Verifie les conditions de victoire de l'arene
	 */
	public void updatePartieFinie() {
		setPartieFinie(ttl > 0 && nbTours > ttl);
	}
	
	protected void setPartieFinie(boolean b){
		partieFinie = b;
	}

	protected boolean isPartieFinie(){
		return partieFinie;
	}
	
	@Override
	public boolean isPartieFinieRMI() throws RemoteException{
		return isPartieFinie();
	}	

	@Override
	public VueElement getVueGagnant() throws RemoteException{		
		return getGagnant().getVue();
	}

	/**
	 * Permet de connaitre le gagnant de la partie
	 * @return ClientPersonnage gagnant
	 */
	private ClientPersonnage getGagnant() {
		if (getPersonnageClassement().isEmpty())
			return null;
		else
			return getPersonnageClassement().get(0);
	}

	/**
	 * Verifie les caracteristiques du personnage
	 * @param elems personnage
	 * @return true si caracteristique valide
	 */
	private boolean verifCaract (PersonnageServeur elems) {
		HashMap<Caracteristique, Integer> caracts = elems.getCaracts();
		for (Entry<Caracteristique, Integer> caractEntry : caracts.entrySet()){
			Caracteristique c = caractEntry.getKey();
			int valeur = caractEntry.getValue();
			if (c.max >= 0 && valeur > c.max)
				return false;
		}
		return true;
	}

	/*
	 * Ajout d'element dans l'arene
	 */
	
	@Override
	public synchronized boolean connect(int refRMI, String ipConsole, PersonnageServeur pers, Point position) throws RemoteException {
		int portConsole = port+refRMI; //on associe un port unique a chaque console
		String adr = "rmi://"+ipConsole+":"+portConsole+"/Console"+refRMI;
		

		try {
			myLogger.info(this.getClass().toString(), "Demande de connexion ("+adr+")");
			clientsPersonnages.put(refRMI, new ClientPersonnage(ipConsole, pers, TTL_CLIENT, position, refRMI));

			myLogger.info(this.getClass().toString(), "Connexion accepte ("+adr+")");
			printElements();
			return true;
		} catch (Exception e) {
			myLogger.severe(this.getClass().toString(), "Echec de connexion ("+adr+")");
			e.printStackTrace();
			ejecterPersonnage(refRMI);
			return false;
		}
	}
	

	@Override
	public synchronized void ajouterPotion(String nom, String groupe, HashMap<Caracteristique,Integer> ht) throws RemoteException {
		Point position = Calculs.randomPosition();
		ajouterObjet(new Potion(nom, groupe, ht), position);
	}

	/**
	 * Ajoute un objet dans l'arene
	 * @param element
	 * @throws RemoteException
	 */
	private void ajouterObjet(Element element, Point position) throws RemoteException {
		int ref = allocateRef();
		ClientElement client = new ClientElement(
				element,
				position, ref);
		client.getVue().setPhrase("En attente!");
		clientsObjets.put(ref, client);
		String type = "de l'objet";
		if (element instanceof Potion)
			type = "de la potion";
		myLogger.info(this.getClass().toString(), "Ajout "+type+" "+ client.getElement().getNomGroupe()+" ("+ref+")");
		printElements();
	}

	/*
	 * Deconnexion des elements
	 */
	
	@Override
	public void deconnecterConsole(IConsole console, String cause) throws RemoteException {
		
		// Enregistrement des infos de la console lors de sa deconnexion
		// le but etant de garder des informations sur les deconnectes
		int ref = console.getRefRMI();
		Element element = clientsPersonnages.get(ref).getElement();
		
		VuePersonnageDeconnecte vue = new VuePersonnageDeconnecte(
				ref, 
				getPosition(ref), 
				element.getNom(), 
				element.getGroupe(), 
				true, 
				element.getCaracts(), 
				nbTours);
		
		deconnectedElements.add(deconnectedElements.size(), vue);
		
		ejecterPersonnage(console.getRefRMI());
		
		// ensuite on lui dit qu'il a ete ejecte
		try {
			console.shutDown(cause);
		} catch (UnmarshalException e) {
			// ne rien faire
		}
		// ainsi si l'ejection ne fonctionne pas le serveur peut continuer sans disfonctionnement
	}

	private void ejecterPersonnage(int refRMI) {
		clientsPersonnages.remove(refRMI);
		myLogger.info(this.getClass().toString(), "Console "+refRMI+" ejectee du registres !");
	}	

	/**
	 * Deconnecte un objet
	 * @param cObjet
	 */
	public void ejecterObjet(ClientElement cObjet) {
		clientsObjets.remove(cObjet.getRef());
	}

	/*
	 * Accesseur sur les liste d'elements
	 */
	
	@Override
	public List<VuePersonnage> getPersonnages() throws RemoteException {
		ArrayList<VuePersonnage> aux = new ArrayList<VuePersonnage>();

		for(ClientPersonnage client : clientsPersonnages.values()) {
			aux.add(client.getVue());
		}
		
		return aux;
	}
	
	@Override
	public List<VueElement> getObjets() throws RemoteException {
		List<VueElement> aux = new ArrayList<VueElement>();
		
		for(ClientElement client : clientsObjets.values()) {
			aux.add(client.getVue());
		}
		return aux;
	}	

	@Override
	public List<VuePersonnageDeconnecte> getHell() throws RemoteException{
		return deconnectedElements;
	}
	

	public IConsole consoleFromRef(int refRMI) throws RemoteException {
		int p = port + refRMI;
		String ip = null;
		Remote r = null;
		String adr = null;

		try {			
			ip = clientsPersonnages.get(refRMI).getIpAddress();
			adr = "rmi://" + ip + ":" + p + "/Console" + refRMI;
			r = Naming.lookup(adr);
		} catch (MalformedURLException e) {
			r = null;
			myLogger.severe(this.getClass().toString(), "Erreur : acces a "+adr+"\n"+e.toString());
			e.printStackTrace();
		} catch (NotBoundException e) {
			r = null;
			myLogger.severe(this.getClass().toString(), "Erreur : acces a "+adr+"\n"+e.toString());
			e.printStackTrace();
		} catch (NullPointerException e) {
			return null;
		}

		return (IConsole) r;
	}

	@Override
	public HashMap<Integer, Point> voisins(IConsole console) throws RemoteException {
		HashMap<Integer, Point> aux = new HashMap<Integer, Point>();

		int ref = console.getRefRMI();
		Point positionConsole = clientsPersonnages.get(ref).getPosition();
		// Recuperation des personnages
		for(int refVoisin : clientsPersonnages.keySet()) {
			Point positionVoisin = clientsPersonnages.get(refVoisin).getPosition();			
			/* 
			 * n'est pas voisin de ce client :
			 *  - le client lui meme
			 *  - tout element situe a une distance > 30 + taille de l'equipe du client
			 *  - tout element deja mort
			 */
			if (refVoisin != ref && (Calculs.distanceChebyshev(positionVoisin, positionConsole)) <= (30 + this.getTailleEquipe(ref))
					&& getAnyElement(refVoisin).isAlive()) {
				aux.put(refVoisin, positionVoisin);
			}
		}
		// Recuperation des objets
		for(int refVoisin : clientsObjets.keySet()) {
			Point positionVoisin = clientsObjets.get(refVoisin).getPosition();			
			/* 
			 * n'est pas voisin de ce client :
			 *  - le client lui meme
			 *  - tout element situe a une distance > 30 + taille de l'equipe du client
			 *  - tout element deja mort
			 */
			if (refVoisin != ref && (Calculs.distanceChebyshev(positionVoisin, positionConsole)) <= (30 + this.getTailleEquipe(ref))
					&& getAnyElement(refVoisin).isAlive()) {
				aux.put(refVoisin, positionVoisin);
			}
		}
		return aux;
	}

	/* ***************************************************
	 * Affichage des elements
	 */
	
	/**
	 * Affiche dans le logger tous les elements present dans l'arene
	 */
	public void printElements() {
		String msg = getPrintElementsMessage();
		myLogger.info(this.getClass().toString(), "Compte-rendu :"+msg);
	}
	
	public String getPrintElementsMessage(){
		String msg = "";
		for(ClientPersonnage client : clientsPersonnages.values()) {
			msg += "\n"+Arene.nomCompletClient(client);
		}
		for(ClientElement client : clientsObjets.values()) {
			msg += "\n"+Arene.nomCompletClient(client);
		}
		return msg;
	}

	private List<VuePersonnage> getClassementVues() {
		List<VuePersonnage> classement = new ArrayList<VuePersonnage>();
		for (ClientPersonnage client : getPersonnageClassement()){
			classement.add(client.getVue());
		}
		classement.addAll(getDeconnectedClassement());		
		return classement;
	}
	
	public List<VuePersonnage> getClassementVuesRMI() throws RemoteException {
		return getClassementVues();
	}
	
	/**
	 * Affiche dans le logger le classement de la partie
	 */
	private void printClassement(){
		List<VuePersonnage> classement = getClassementVues();
		
		String msg = "";
		int i = 1;
		for (VuePersonnage vue : classement){
			msg += "\n"+i+" : "+vue.getNom()+" "+vue.getGroupe()+" "+vue.getPhrase();
			i++;
		}
		myLogger.info(this.getClass().toString(), "Classement :"+msg);		
	}
	
	/**
	 * Renvois la liste des clients vivants tries par classement
	 * @return liste des personnages
	 */
	private List<ClientPersonnage> getPersonnageClassement() {
		List<ClientPersonnage> classement = new ArrayList<ClientPersonnage>();
		
		// Recuperation des personnages en vie
		for(ClientPersonnage client : clientsPersonnages.values()) {
			classement.add(client);
		}
//		Comparator<ClientPersonnage> comparator = new Comparator<ClientPersonnage>() {
//			@Override
//			public int compare(ClientPersonnage client1, ClientPersonnage client2) {
//				return client2.getElement().getCaract(Caracteristique.ARGENT) - client1.getElement().getCaract(Caracteristique.ARGENT);
//			}
//		};
		// Tri des personnages
//		Collections.sort(classement, comparator);
		return classement;
	}
	
	/**
	 * Renvoie la liste des elements deconnectees tries par ordre de deconnexion
	 * @return liste des elements deconnectes
	 */
	private List<VuePersonnageDeconnecte> getDeconnectedClassement() {
		List<VuePersonnageDeconnecte> persosDeconnected = new ArrayList<VuePersonnageDeconnecte>();
		persosDeconnected.addAll(deconnectedElements);
		
		Comparator<VuePersonnageDeconnecte> deconnectedComparator = new Comparator<VuePersonnageDeconnecte>() {
			@Override
			public int compare(VuePersonnageDeconnecte v1, VuePersonnageDeconnecte v2) {
				Integer tour1, tour2;
				tour1 = v1.getTourDeconnexion();
				tour2 = v2.getTourDeconnexion();
				return tour2.compareTo(tour1);
			}
		};
		
		Collections.sort(persosDeconnected, deconnectedComparator);
		return persosDeconnected;
	}
	
	/**
	 * Renvoie le nombre de personnages connectes
	 */
	public int countPersonnages() {
		return clientsPersonnages.size();
	}
	
	/* ***************************************************
	 * gestions des interactions
	 */

	@Override
	public boolean ramasserObjet(IConsole console, int refObjet) throws RemoteException {
		
		int ref = console.getRefRMI();
		ClientPersonnage client = clientsPersonnages.get(ref);
		ClientElement clientObjet = clientsObjets.get(refObjet);
		
		
		if (client.isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+"a tente de jouer plusieurs actions dans le meme tour");
		} else {
			int distance = Calculs.distanceChebyshev(client.getPosition(), clientObjet.getPosition());
			if (distance <= EntreElement.distanceMinInteraction) {
				new Ramassage(this, client, clientObjet).interagir();
				clientsPersonnages.get(ref).actionExecutee();
				
				return true;
			} else {
				myLogger.warning(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+" a tente d'interagir avec "+clientObjet.getVue().getNom()+", alors qu'il est trop eloigne... Distance de chebyshev = "+distance);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean lancerUneAttaque(IConsole console, int refAdv) throws RemoteException {
		int ref = console.getRefRMI();
		ClientPersonnage client = clientsPersonnages.get(ref);
		ClientPersonnage clientAdv = clientsPersonnages.get(refAdv);
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			IConsole consoleAdv = consoleFromRef(refAdv);
			
			int distance = Calculs.distanceChebyshev(clientsPersonnages.get(ref).getPosition(), clientsPersonnages.get(refAdv).getPosition());
			if (distance <= EntreElement.distanceMinInteraction) {
							
				PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
				PersonnageServeur persAdv = (PersonnageServeur) getAnyElement(refAdv);
				if (pers.isAlive() && persAdv.isAlive()) {
					console.log(Level.INFO, this.getClass().toString(), "J'attaque "+nomRaccourciClient(consoleAdv.getRefRMI()));
					consoleAdv.log(Level.INFO, this.getClass().toString(), "Je me fait attaquer par "+nomRaccourciClient(console.getRefRMI()));
					myLogger.info(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+" attaque "+nomRaccourciClient(consoleAdv.getRefRMI()));
			
					new Attaque(this, client, clientAdv).interagir();
					clientsPersonnages.get(ref).actionExecutee();
					clientsPersonnages.get(refAdv).sonne();
					
					pers = (PersonnageServeur) getAnyElement(ref);
					persAdv = (PersonnageServeur) getAnyElement(refAdv);
					
					if (! persAdv.isAlive()) {
						setPhrase(console, "Je tue " + nomRaccourciClient(consoleAdv.getRefRMI()));
						console.log(Level.INFO, this.getClass().toString(), "Je tue "+nomRaccourciClient(console.getRefRMI()));
						myLogger.info(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+" tue "+nomRaccourciClient(consoleAdv.getRefRMI()));
					}
					
					return true;
				} else {
					myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente d'interagir avec "+nomRaccourciClient(refAdv)+", alors qu'il est mort...");
					console.log(Level.WARNING, this.getClass().toString(), nomRaccourciClient(refAdv)+" est deja mort !");
				}
			} else {
				myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente d'interagir avec "+nomRaccourciClient(refAdv)+", alors qu'il est trop eloigne... Distance de chebyshev = "+distance);
				console.log(Level.WARNING, "AVERTISSEMENT ARENE", nomRaccourciClient(refAdv)+" est trop eloigne !!!\nDistance de chebyshev = "+distance);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean deplacer(IConsole console, int refCible) throws RemoteException {
		int ref = console.getRefRMI();
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			new Deplacements(this, ref, getPosition(ref), voisins(console)).seDirigerVers(refCible);
			clientsPersonnages.get(ref).actionExecutee();
			return true;
		}
		return false;
	}

	@Override
	public boolean deplacer(IConsole console, Point objectif) throws RemoteException {
		int ref = console.getRefRMI();
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			new Deplacements(this, ref, getPosition(ref), voisins(console)).seDirigerVers(objectif);
			clientsPersonnages.get(ref).actionExecutee();
			
			return true;
		}
		
		return false;
	}
	
	
	/* ***************************************************
	 * changement de l'etat des ElementServeur
	 */	
	/**
	 * Ajoute l'increment a la caracteristique carac de l'elementServeur correspondant au client
	 * L'increment peut etre positif ou negatif
	 * @param client le client qui doit etre mis a jour
	 * @param carac la caracteristique a mettre a jour
	 * @param increment l'increment a ajouter au valeur courante de l'element
	 * @throws RemoteException
	 */
	public void ajouterCaractElement(ClientPersonnage client, Caracteristique carac, int increment) throws RemoteException {
		int ref = client.getRef();
		IConsole console = consoleFromRef(ref);
		
		PersonnageServeur pers = client.getPersServeur();
		pers.ajouterCaract(carac, pers.getCaract(carac) + increment);
		
		if (increment < 0) {
			console.log(Level.INFO, this.getClass().toString(), "J'ai perdu "+Math.abs(increment)+" points de "+carac);
			if (carac == Caracteristique.VIE)
				setPhrase(client, "Ouch, j'ai perdu "+increment+" points de vie.");
		} else {
			console.log(Level.INFO, this.getClass().toString(), "J'ai gagne "+increment+" points de "+carac);
		}
		
		if (carac == Caracteristique.VIE && ! pers.isAlive()) {
			setPhrase(console, "MORT >_<");
			enleverTousPersonnagesEquipe(client);
			changerLeader(ref, -1);
		}		
		setPersServeur(ref, pers);
	}
	
	/**
	 * Modifie le leader (le client et le nouveau leader doivent etre des personnages).
	 * @param client le client qui change de leader
	 * @param refLead reference du nouveau leader
	 */
	public void changerLeader(ClientPersonnage client, int refLead) {
		changerLeader(client.getRef(), refLead);
	}
	
	private void changerLeader(int ref, int refLead) {
		/*
		// ancien leader (s'il existe)
		int refOldLeader = ((PersonnageServeur) getAnyElement(ref)).getLeader();
		
		// si existant, enlever this de l'equipe de l'ancien leader (si existant)
		if(refOldLeader != -1) {
			enleverPersonnageEquipe(refOldLeader, ref);
		}
		*/
		
		// si on a un nouveau leader
		if(refLead != -1) {
			// ajouter ref a l'equipe du nouveau leader (et modifier le leader)
			ajouterPersonnageEquipe(refLead, ref);

			// on recupere la version serveur de l'element
			PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);

			// ajouter toute l'equipe de ref a l'equipe du nouveau leader (et modifier leur leader)
			for(int refEq : pers.getEquipe()) {
				ajouterPersonnageEquipe(refLead, refEq);
			}
			
			// vider l'equipe de ref
			pers.enleverTouteEquipe();
			
			// on renvoie la version serveur de l'element
			setPersServeur(ref, pers);
		}
	}

	/**
	 * Ajoute un personnage a l'equipe (l'element courant et lead doivent etre des personnages).
	 * @param client le leader
	 * @param refEq nouvel equipier
	 */
	public void ajouterPersonnageEquipe(ClientPersonnage client, int refEq) {
		ajouterPersonnageEquipe(client.getRef(), refEq);
	}
	private void ajouterPersonnageEquipe(int refLeader, int refEquipie) {
		// ajouter refEquipie a l'equipe de refLeader
		PersonnageServeur leader = (PersonnageServeur) getAnyElement(refLeader);
		leader.ajouterEquipier(refEquipie);
		setPersServeur(refLeader, leader);
				
		// changer le refEquipie de eq vers refLeader
		setLeaderOnly(refEquipie, refLeader);
	}

	/**
	 * Enleve un personnage de l'equipe (l'element courant et lead doivent etre des personnages).
	 * @param client le leader
	 * @param refEq ancien equipier
	 */
	public void enleverPersonnageEquipe(ClientPersonnage client, int refEq) {
		enleverPersonnageEquipe(client.getRef(), refEq);
	}
	private void enleverPersonnageEquipe(int ref, int refEq) {
		// enlever le leader de eq
		clearLeaderOnly(refEq);

		// enlever eq de l'equipe de this
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		pers.enleverEquipier(refEq);
		setPersServeur(ref, pers);
	}

	/**
	 * Enleve tous les personnages de l'equipe.
	 * @param client le leader
	 */
	public void enleverTousPersonnagesEquipe(ClientPersonnage client) {
		enleverTousPersonnagesEquipe(client.getRef());
	}
	private void enleverTousPersonnagesEquipe(int ref) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		
		// efface le leader de tous les personnages de l'equipe de ref
		for(int r : pers.getEquipe()) {
			clearLeaderOnly(r);
		}
		pers.enleverTouteEquipe();
		setPersServeur(ref, pers);
	}
	
	/**
	 * Change le leader (sans modifier son equipe). Ne devrait pas etre utilise seul.
	 * @param client le client dont il faut changer le leader
	 * @param refLead nouveau leader
	 */
	public void setLeaderOnly(ClientPersonnage client, int refLead) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(client.getRef());
		pers.setLeader(refLead);
		setPersServeur(client.getRef(), pers);
	}
	private void setLeaderOnly(int ref, int refLead) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		pers.setLeader(refLead);
		setPersServeur(ref, pers);
	}

	/**
	 * Supprime le leader (sans modifier son equipe). Ne devrait pas etre utilise seul.
	 * @throws RemoteException
	 */
	public void clearLeaderOnly(ClientPersonnage client) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(client.getRef());
		pers.clearLeader();
		setPersServeur(client.getRef(), pers);
	}
	private void clearLeaderOnly(int ref) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		pers.clearLeader();
		setPersServeur(ref, pers);
	}
	

	
	public void setLeader(int ref, int refLeader){
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		
		int refOldLeader = pers.getLeader();
		
		// Si le personnage a deja un leader
		if (refOldLeader != -1){
			// Le personnage change d'equipe
			PersonnageServeur oldLeader = (PersonnageServeur) getAnyElement(refOldLeader);
			oldLeader.enleverEquipier(ref);
		}

		// Si le personnage etait leader
		if (pers.getEquipe().size() > 0){
			transfererEquipe(ref, refLeader);
		}

		pers.setLeader(refLeader);		
	}
	
	private void transfererEquipe(int ref, int refLeader) {
		PersonnageServeur pers = (PersonnageServeur) getAnyElement(ref);
		PersonnageServeur leader = (PersonnageServeur) getAnyElement(refLeader);
		
		for(int refEquipier : pers.getEquipe()){
			pers.enleverEquipier(refEquipier);
			leader.ajouterEquipier(refEquipier);
		}		
	}

	@Override
	public Element getMyElement(IConsole console) throws RemoteException {
		return getAnyElement(console.getRefRMI());
	}
	public Element getAnElement(int refRMI) throws RemoteException {
		return getAnyElement(refRMI);
	}
	private Element getAnyElement(int refRMI) {
		return getClientElement(refRMI).getElement();
	}
	
	/**
	 * Actualise le PersonnageServeur correspondant au client de reference refRMI
	 * @param refRMI
	 * @param elems
	 * @throws RemoteException
	 */
	private void setPersServeur(int refRMI, PersonnageServeur pers) {
		clientsPersonnages.get(refRMI).setPersServeur(pers);
	}
	
	/* ***************************************************
	 * accesseurs sur les VueElement
	 */

	@Override
	public void setPhrase(IConsole console, String s) throws RemoteException {
		getClientElement(console.getRefRMI()).setPhrase(s);
	}

	public void setPhrase(ClientElement client, String s) {
		client.setPhrase(s);		
	}
	
	@Override
	public VueElement getMyVueElement(IConsole console) throws RemoteException {
		return getAnVueElement(console.getRefRMI());
	}
	
	@Override
	public VueElement getAnVueElement(int refRMI) throws RemoteException {
		return getAnyVueElement(refRMI);
	}
	
	private VueElement getAnyVueElement(int refRMI) {
		return getClientElement(refRMI).getVue();
	}
	
	/* ***************************************************
	 * autres
	 */
	
	@Override
	protected void finalize() throws Throwable {
		List<Integer> listRef = getSortedRefs();
		for (int refRMI: listRef) {
			try {
				deconnecterConsole(consoleFromRef(refRMI), "Fermeture du serveur (Arret force ou crash...)");
			} catch (Exception e) {
				/* Si il y a une erreur lors de la deconnexion on ne peut rien faire...
				 * Donc on ne fais rien... et on continu de fermer le serveur
				 */
			}
		}

		super.finalize();
	}
	

	/**
	 * Retourne la taille de l'equipe du leader
	 * @param console
	 * @return taille de l'equipe
	 * @throws RemoteException
	 */
	private int getTailleEquipe(int ref){
		Element elem = getAnyElement(ref);
		if (elem instanceof PersonnageServeur) {
			PersonnageServeur pers = (PersonnageServeur) elem;
			if (pers.getLeader() != -1)
				pers = (PersonnageServeur) clientsPersonnages.get(pers.getLeader()).getPersServeur();
			return pers.getEquipe().size();
		}
		return 0;
	}
	
	protected ClientElement getClientElement(int ref) {
		ClientElement client = clientsPersonnages.get(ref);
		if (client == null)
			client = clientsObjets.get(ref);
		return client;
	}

	@Override
	public Point getPosition(int ref) throws RemoteException {
		ClientElement client = getClientElement(ref);
		Point p = client.getPosition();
		return new Point(p.x, p.y);
	}

	public void setPosition(int ref, Point position) {
		position = new Point(Calculs.caperNumber(XMIN, XMAX, position.x),
				Calculs.caperNumber(YMIN, YMAX, position.y));
		clientsPersonnages.get(ref).setPosition(position);
	}
	
	public int getNbToursRestants() throws RemoteException {
		return (int) (ttl - nbTours);
	}

	public int getNbTour() throws RemoteException{
		return nbTours;
	}
	
	public void ajouterClientEnJeu(int ref, ClientElement client) {
		clientsObjets.put(ref, client);		
	}
	
	/* ***************************************************
	 * Logs
	 */
	
	public String nomRaccourciClient(int ref) {
		return nomRaccourciClient(getClientElement(ref));
	}
	
	public static String nomRaccourciClient(ClientElement client) {
		return "(Client" + client.getRef() + " * " + client.getElement().getNomGroupe()+ ")";
	}
	
	public String nomCompletClient(int ref) {
		return nomCompletClient(getClientElement(ref));
	}
	
	public static String nomCompletClient(ClientElement client) {
		Element element = client.getElement();
		String type = "Client";
		if (element instanceof PersonnageServeur){
			type = "Personnage";
		}
		if (element instanceof Potion){
			type = "Potion";
		}
		return "("+ type + client.getRef() + " * " + client.getElement().toString() + ")";
	}

	public String nomComplet() throws RemoteException{
		return "("+adrToString()+")";
	}

	public String nomRaccourci(){
		return "(Arene)";
	}

	public void log(Level level, String prefixe, String msg) throws RemoteException {
		myLogger.log(level, prefixe,msg);
	}
	
	public void logClient(ClientElement client, Level level, String prefixe, String msg) throws RemoteException {
		IConsole cons = consoleFromRef(client.getRef());
		if (cons != null) cons.log(level, prefixe, msg);
	}
	
	/*
	 * MeTHODES NE FAISANT RIEN POUR UNE AReNE SIMPLE
	 */
	
	@Override
	public boolean isPartieCommencee() throws RemoteException {
		return true;
	}
	
	@Override
	public List<VueElement> getObjetsEnAttente() throws RemoteException {
		return new ArrayList<VueElement>();
	}

	@Override
	public void ajouterPotionSecurisee(String nom,
			HashMap<Caracteristique, Integer> ht, Point position, String mdp)
			throws RemoteException {	
	}
	

	@Override
	public void commencerPartie(String motDePasse) throws RemoteException {		
	}

	@Override
	public void ejecter(VueElement joueur, String motDePasse) throws RemoteException {		
	}
	
	@Override
	public synchronized void lancerObjetEnAttente(int ref, String mdp) throws RemoteException {
		
	}

	@Override
	public boolean verifMotDePasse(char[] motDePasse) throws RemoteException {
		return false;
	}

	@Override
	public boolean isEnAttente(int refRMI) throws RemoteException {
		return false;
	}

	
}
