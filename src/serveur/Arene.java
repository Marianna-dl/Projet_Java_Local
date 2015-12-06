package serveur;

import java.awt.Point;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import logger.MyLogger;
import serveur.controle.IConsolePersonnage;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.infosclient.PaireRefRMIIntitiative;
import serveur.infosclient.VueElement;
import serveur.infosclient.VuePersonnage;
import serveur.infosclient.VuePotion;
import serveur.interaction.Attaque;
import serveur.interaction.Deplacements;
import serveur.interaction.Ramassage;
import utilitaires.Calculs;
import utilitaires.Constantes;

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
	 * Repertoire des refRMI et des instances de la classe ClientPersonnage 
	 * contenant toutes les donnees de chaque client
	 */	
	private Hashtable<Integer, VuePersonnage> clientsPersonnages = null;

	/**
	 * Repertoire des refRMI et des instances de la classe ClientPotion
	 * contenant toutes les donnees des potions qui sont d'office en jeu
	 */
	protected Hashtable<Integer, VuePotion> clientsPotions = null;

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

		clientsPersonnages = new Hashtable<Integer, VuePersonnage>();
		clientsPotions = new Hashtable<Integer, VuePotion>();
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
						IConsolePersonnage console = consoleFromRef(refRMI);
						Personnage elems = (Personnage) getElementRef(refRMI);
						
						/* peut etre que ce client a ete tue lors d'un
						 * duel plus tot dans ce tour si c'est le cas,
						 * il ne peut pas jouer son tour et il doit etre
						 * ejecte
						 */
						if (!elems.estActif()) {
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
									if (!elems.estActif()) {
										myLogger.info(this.getClass().toString(), nomRaccourciClient(refRMI) + " est mort... Client ejecte");
										deconnecterConsole(console, "Vous etes mort...");
									} else if (clientsPersonnages.get(refRMI).getTTL() <= 0) {
										myLogger.info(this.getClass().toString(), "Fin du TTL de " + nomRaccourciClient(refRMI) + "... Client ejecte");
										deconnecterConsole(console, "Temps autorise dans l'arene ecoule, vous etes elimine !");
									} else if (elems instanceof Personnage) {
										if (!verifCaract((Personnage) elems)) {
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
	 * Renvoie la liste des references triees par ordre de passage en fonction
	 * de l'initiative.
	 * @return liste de toutes les references des personnages de la partie,
	 * ordonnee par initiative (decroissante)
	 */
	private List<Integer> getSortedRefs() {
		// on cree une priority queue de paires reference RMI/initiative du 
		// personnage
		// en ajoutant des paires dans la queue, elles automatiquement seront 
		// classees en suivant leur methode compareTo
		Queue<PaireRefRMIIntitiative> queueRefsInitiative = new PriorityQueue<PaireRefRMIIntitiative>();
		
		for(VuePersonnage client : clientsPersonnages.values()) {			
			queueRefsInitiative.offer(new PaireRefRMIIntitiative(
					client.getRefRMI(), 
					client.getElement().getCaract(Caracteristique.INITIATIVE)));
		}

		
		// on recupere juste les references
		List<Integer> listeRefsTriees = new ArrayList<Integer>();
		
		while(!queueRefsInitiative.isEmpty()) {
			listeRefsTriees.add(queueRefsInitiative.poll().getRef());
		}
	
		return listeRefsTriees;
	}
	
	/**
	 * Classe permettant de lancer une execution du client (run)
	 * dans un thread separe, pour pouvoir limiter son temps d'execution
	 * via un join(timeout)
	 */
	public class TimeoutOp extends Thread {		
		private IConsolePersonnage console;
		TimeoutOp(IConsolePersonnage r) { this.console=r; start(); }
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
	
	protected void setPartieFinie(boolean b) {
		partieFinie = b;
	}

	protected boolean isPartieFinie() {
		return partieFinie;
	}
	
	@Override
	public boolean isPartieFinieRMI() throws RemoteException{
		return isPartieFinie();
	}

	/**
	 * Verifie les caracteristiques du personnage
	 * @param elems personnage
	 * @return true si caracteristique valide
	 */
	private boolean verifCaract (Personnage elems) {
		HashMap<Caracteristique, Integer> caracts = elems.getCaracts();
		for (Entry<Caracteristique, Integer> caractEntry : caracts.entrySet()) {
			Caracteristique c = caractEntry.getKey();
			int valeur = caractEntry.getValue();
			if (c.getMax() >= 0 && valeur > c.getMax())
				return false;
		}
		return true;
	}

	/*
	 * Ajout d'element dans l'arene
	 */
	
	@Override
	public synchronized boolean connect(int refRMI, String ipConsole, Personnage pers, Point position) throws RemoteException {
		int portConsole = port+refRMI; //on associe un port unique a chaque console
		String adr = "rmi://"+ipConsole+":"+portConsole+"/Console"+refRMI;
		

		try {
			myLogger.info(this.getClass().toString(), "Demande de connexion ("+adr+")");
			clientsPersonnages.put(refRMI, new VuePersonnage(ipConsole, pers, TTL_CLIENT, position, refRMI, true));

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
		ajouterPotion(new Potion(nom, groupe, ht), position);
	}

	/**
	 * Ajoute une potion dans l'arene
	 * @param element
	 * @throws RemoteException
	 */
	private void ajouterPotion(Element element, Point position) throws RemoteException {
		int ref = allocateRef();
		VuePotion client = new VuePotion(
				(Potion) element,
				position, ref, true);
		client.setPhrase("En attente!");
		clientsPotions.put(ref, client);
		String type = "de la potion";
		if (element instanceof Potion)
			type = "de la potion";
		myLogger.info(this.getClass().toString(), "Ajout "+type+" "+ client.getElement().getNomGroupe()+" ("+ref+")");
		printElements();
	}

	/*
	 * Deconnexion des elements
	 */
	
	// TODO disconnect console
	@Override
	public void deconnecterConsole(IConsolePersonnage console, String cause) throws RemoteException {
		
		// Enregistrement des infos de la console lors de sa deconnexion
		// le but etant de garder des informations sur les deconnectes
		/*
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
		*/
		
		ejecterPersonnage(console.getRefRMI());
		
		// ensuite on lui dit qu'il a ete ejecte
		try {
			console.shutDown(cause);
		} catch (UnmarshalException e) {
			// ne rien faire
		}
		// ainsi si l'ejection ne fonctionne pas le serveur peut continuer sans disfonctionnement
	}

	protected void ejecterPersonnage(int refRMI) {
		clientsPersonnages.remove(refRMI);
		myLogger.info(this.getClass().toString(), "Console "+refRMI+" ejectee du registre !");
	}		
	
	public void ejecterPotion(int refRMI) {
		clientsPotions.remove(refRMI);
	}	

	/*
	 * Accesseur sur les liste d'elements
	 */
	
	@Override
	public List<VuePersonnage> getPersonnages() throws RemoteException {
		ArrayList<VuePersonnage> aux = new ArrayList<VuePersonnage>();

		for(VuePersonnage client : clientsPersonnages.values()) {
			aux.add(client);
		}
		
		return aux;
	}
	
	@Override
	public List<VuePotion> getPotions() throws RemoteException {
		List<VuePotion> aux = new ArrayList<VuePotion>();
		
		for(VuePotion client : clientsPotions.values()) {
			aux.add(client);
		}
		
		return aux;
	}
	

	public IConsolePersonnage consoleFromRef(int refRMI) throws RemoteException {
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

		return (IConsolePersonnage) r;
	}

	@Override
	public HashMap<Integer, Point> getVoisins(IConsolePersonnage console) throws RemoteException {
		HashMap<Integer, Point> aux = new HashMap<Integer, Point>();

		int ref = console.getRefRMI();
		Point positionConsole = clientsPersonnages.get(ref).getPosition();
		// Recuperation des personnages
		for(int refVoisin : clientsPersonnages.keySet()) {
			Point positionVoisin = clientsPersonnages.get(refVoisin).getPosition();			
			/* 
			 * n'est pas voisin de ce client :
			 *  - le client lui meme
			 *  - tout element situe a une distance <= VISION
			 *  - tout element deja mort
			 */
			if (refVoisin != ref && 
					Calculs.distanceChebyshev(positionVoisin, positionConsole) <= Constantes.VISION && 
					getElementRef(refVoisin).estActif()) {
				aux.put(refVoisin, positionVoisin);
			}
		}
		// Recuperation des potions
		for(int refVoisin : clientsPotions.keySet()) {
			Point positionVoisin = clientsPotions.get(refVoisin).getPosition();			
			/* 
			 * n'est pas voisin de ce client :
			 *  - le client lui meme
			 *  - tout element situe a une distance <= VISION
			 *  - tout element deja mort
			 */
			if (refVoisin != ref && 
					Calculs.distanceChebyshev(positionVoisin, positionConsole) <= Constantes.VISION && 
					getElementRef(refVoisin).estActif()) {
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
	
	public String getPrintElementsMessage() {
		String msg = "";
		for(VuePersonnage client : clientsPersonnages.values()) {
			msg += "\n"+Arene.nomCompletClient(client);
		}
		for(VuePotion client : clientsPotions.values()) {
			msg += "\n"+Arene.nomCompletClient(client);
		}
		return msg;
	}

	private List<VuePersonnage> getClassementVues() {
		List<VuePersonnage> classement = new ArrayList<VuePersonnage>();
		for (VuePersonnage client : getPersonnageClassement()) {
			classement.add(client);
		}
		
		return classement;
	}
	
	public List<VuePersonnage> getClassementVuesRMI() throws RemoteException {
		return getClassementVues();
	}
	
	/**
	 * Affiche dans le logger le classement de la partie
	 */
	private void printClassement() {
		List<VuePersonnage> classement = getClassementVues();
		
		String msg = "";
		int i = 1;
		for (VuePersonnage vue : classement) {
			msg += "\n"+i+" : "+vue.getElement().getNom()+" "+vue.getElement().getGroupe()+" "+vue.getPhrase();
			i++;
		}
		myLogger.info(this.getClass().toString(), "Classement :"+msg);		
	}
	
	/**
	 * Renvois la liste des clients vivants tries par classement
	 * @return liste des personnages
	 */
	private List<VuePersonnage> getPersonnageClassement() {
		List<VuePersonnage> classement = new ArrayList<VuePersonnage>();
		
		// Recuperation des personnages en vie
		for(VuePersonnage client : clientsPersonnages.values()) {
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
	 * Renvoie le nombre de personnages connectes
	 */
	public int countPersonnages() {
		return clientsPersonnages.size();
	}
	
	/* ***************************************************
	 * gestions des interactions
	 */

	@Override
	public boolean ramasserPotion(IConsolePersonnage console, int refPotion) throws RemoteException {
		
		int ref = console.getRefRMI();
		VuePersonnage client = clientsPersonnages.get(ref);
		VuePotion clientPotion = clientsPotions.get(refPotion);
		
		
		if (client.isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+"a tente de jouer plusieurs actions dans le meme tour");
		} else {
			int distance = Calculs.distanceChebyshev(client.getPosition(), clientPotion.getPosition());
			if (distance <= Constantes.DISTANCE_MIN_INTERACTION) {
				new Ramassage(this, client, clientPotion).interagir();
				clientsPersonnages.get(ref).actionExecutee();
				
				return true;
			} else {
				myLogger.warning(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+" a tente d'interagir avec "+clientPotion.getElement().getNom()+", alors qu'il est trop eloigne... Distance de chebyshev = "+distance);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean lancerUneAttaque(IConsolePersonnage console, int refAdv) throws RemoteException {
		int ref = console.getRefRMI();
		VuePersonnage client = clientsPersonnages.get(ref);
		VuePersonnage clientAdv = clientsPersonnages.get(refAdv);
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			IConsolePersonnage consoleAdv = consoleFromRef(refAdv);
			
			int distance = Calculs.distanceChebyshev(clientsPersonnages.get(ref).getPosition(), clientsPersonnages.get(refAdv).getPosition());
			if (distance <= Constantes.DISTANCE_MIN_INTERACTION) {
							
				Personnage pers = (Personnage) getElementRef(ref);
				Personnage persAdv = (Personnage) getElementRef(refAdv);
				if (pers.estActif() && persAdv.estActif()) {
					console.log(Level.INFO, this.getClass().toString(), "J'attaque "+nomRaccourciClient(consoleAdv.getRefRMI()));
					consoleAdv.log(Level.INFO, this.getClass().toString(), "Je me fait attaquer par "+nomRaccourciClient(console.getRefRMI()));
					myLogger.info(this.getClass().toString(), nomRaccourciClient(console.getRefRMI())+" attaque "+nomRaccourciClient(consoleAdv.getRefRMI()));
			
					new Attaque(this, client, clientAdv).interagir();
					clientsPersonnages.get(ref).actionExecutee();
					clientsPersonnages.get(refAdv).sonne();
					
					pers = (Personnage) getElementRef(ref);
					persAdv = (Personnage) getElementRef(refAdv);
					
					if (! persAdv.estActif()) {
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
	public boolean deplacer(IConsolePersonnage console, int refCible) throws RemoteException {
		int ref = console.getRefRMI();
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			new Deplacements(this, ref, getPosition(ref), getVoisins(console)).seDirigerVers(refCible);
			clientsPersonnages.get(ref).actionExecutee();
			return true;
		}
		return false;
	}

	@Override
	public boolean deplacer(IConsolePersonnage console, Point objectif) throws RemoteException {
		int ref = console.getRefRMI();
		
		if (clientsPersonnages.get(ref).isActionExecutee()) {
			console.log(Level.WARNING, "AVERTISSEMENT ARENE", "Une action a deja ete execute ce tour-ci !!!");
			myLogger.warning(this.getClass().toString(), nomRaccourciClient(ref)+" a tente de jouer plusieurs actions dans le meme tour");
		} else {
			new Deplacements(this, ref, getPosition(ref), getVoisins(console)).seDirigerVers(objectif);
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
	public void ajouterCaractElement(VuePersonnage client, Caracteristique carac, int increment) throws RemoteException {
		int ref = client.getRefRMI();
		IConsolePersonnage console = consoleFromRef(ref);
		
		Personnage pers = client.getPersServeur();
		if(pers.ajouterCaract(carac, pers.getCaract(carac) + increment)) { // si mort
			client.setTourMort(nbTours);
		}
		
		if (increment < 0) {
			console.log(Level.INFO, this.getClass().toString(), "J'ai perdu "+Math.abs(increment)+" points de "+carac);
			if (carac == Caracteristique.VIE)
				setPhrase(console, "Ouch, j'ai perdu "+increment+" points de vie.");
		} else {
			console.log(Level.INFO, this.getClass().toString(), "J'ai gagne "+increment+" points de "+carac);
		}
		
		if (carac == Caracteristique.VIE && ! pers.estActif()) {
			setPhrase(console, "MORT >_<");
		}
		
		setPersServeur(ref, pers);
	}
	
	@Override
	public Element getMyElement(IConsolePersonnage console) throws RemoteException {
		return getElementRef(console.getRefRMI());
	}
	
	@Override
	public Element getElement(int refRMI) throws RemoteException {
		return getElementRef(refRMI);
	}
	
	private Element getElementRef(int refRMI) {
		return getClientElement(refRMI).getElement();
	}
	
	/**
	 * Actualise le PersonnageServeur correspondant au client de reference refRMI
	 * @param refRMI
	 * @param elems
	 * @throws RemoteException
	 */
	private void setPersServeur(int refRMI, Personnage pers) {
		clientsPersonnages.get(refRMI).setPersServeur(pers);
	}
	
	/* ***************************************************
	 * accesseurs sur les VueElement
	 */

	@Override
	public void setPhrase(IConsolePersonnage console, String s) throws RemoteException {
		getClientElement(console.getRefRMI()).setPhrase(s);
	}

	public void setPhrase(VueElement client, String s) {
		client.setPhrase(s);		
	}
	
	@Override
	public VueElement getMyVueElement(IConsolePersonnage console) throws RemoteException {
		return getAnVueElement(console.getRefRMI());
	}
	
	@Override
	public VueElement getAnVueElement(int refRMI) throws RemoteException {
		return getAnyVueElement(refRMI);
	}
	
	private VueElement getAnyVueElement(int refRMI) {
		return getClientElement(refRMI);
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
	
	protected VueElement getClientElement(int ref) {
		VueElement client = clientsPersonnages.get(ref);
		if (client == null)
			client = clientsPotions.get(ref);
		return client;
	}

	@Override
	public Point getPosition(int ref) throws RemoteException {
		VueElement client = getClientElement(ref);
		Point p = client.getPosition();
		return new Point(p.x, p.y);
	}

	public void setPosition(int ref, Point position) {
		position = new Point(Calculs.caperNumber(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE, position.x),
				Calculs.caperNumber(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE, position.y));
		clientsPersonnages.get(ref).setPosition(position);
	}
	
	public int getNbToursRestants() throws RemoteException {
		return (int) (ttl - nbTours);
	}

	public int getNbTour() throws RemoteException{
		return nbTours;
	}
	
	public void ajouterClientEnJeu(int ref, VuePotion client) {
		clientsPotions.put(ref, client);		
	}
	
	/* ***************************************************
	 * Logs
	 */
	
	public String nomRaccourciClient(int ref) {
		return nomRaccourciClient(getClientElement(ref));
	}
	
	public static String nomRaccourciClient(VueElement client) {
		return "(Client" + client.getRefRMI() + " * " + client.getElement().getNomGroupe()+ ")";
	}
	
	public String nomCompletClient(int ref) {
		return nomCompletClient(getClientElement(ref));
	}
	
	public static String nomCompletClient(VueElement client) {
		Element element = client.getElement();
		String type = "Client";
		if (element instanceof Personnage) {
			type = "Personnage";
		}
		if (element instanceof Potion) {
			type = "Potion";
		}
		return "("+ type + client.getRefRMI() + " * " + client.getElement().toString() + ")";
	}

	public String nomComplet() throws RemoteException{
		return "("+adrToString()+")";
	}

	public String nomRaccourci() {
		return "(Arene)";
	}

	public void log(Level level, String prefixe, String msg) throws RemoteException {
		myLogger.log(level, prefixe,msg);
	}
	
	public void logClient(VueElement client, Level level, String prefixe, String msg) throws RemoteException {
		IConsolePersonnage cons = consoleFromRef(client.getRefRMI());
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
	public List<VuePotion> getPotionsEnAttente() throws RemoteException {
		return new ArrayList<VuePotion>();
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
	public synchronized void lancerPotionEnAttente(int ref, String mdp) throws RemoteException {
		
	}

	@Override
	public boolean verifMotDePasse(char[] motDePasse) throws RemoteException {
		return false;
	}

	@Override
	public boolean isEnAttente(int refRMI) throws RemoteException {
		return false;
	}

	@Override
	public VuePersonnage getVueGagnant() throws RemoteException{		
		if (getPersonnageClassement().isEmpty())
			return null;
		else
			return getPersonnageClassement().get(0);
	}

	
}
