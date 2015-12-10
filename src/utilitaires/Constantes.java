package utilitaires;

import java.awt.Color;

import serveur.vuelement.VueElement;

/**
 * Definit des constantes utiles et quelques methodes tres simples.
 * Les calculs sont dans la classe {@code Calculs}.
 *
 */
public class Constantes {
	
	/**
	 * Adresse IP par defaut (localhost). 
	 */
	public static final String IP_DEFAUT = "localhost";

	/**
	 * Port utilise par defaut.
	 */
	public static final int PORT_DEFAUT = 5099;
	
	/**
	 * Nombre de tours par defaut (30 minutes).
	 */
	public static final long NB_TOURS_DEFAUT = 60 * 30;
	
	/**
	 * Nombre de tours par defaut pour un personnage client (20 minutes), 
	 * hors tournoi. 
	 */
	public static final long NB_TOURS_PERSONNAGE_DEFAUT = 60 * 20;
	
	/**
	 * Taille du champ de vision des personnages. 
	 */
	public static final int VISION = 30;
	
	/**
	 * Quantite d'initiative retiree a l'attaquant et ajoutee au defenseur
	 * lors d'un duel.
	 */
	public static final int INCR_DECR_INITIATIVE_DUEL = 10;
	
	/**
	 * Contient les distances de projection (suite a un duel) associees a 
	 * chaque "quart" de force. 
	 * Par exemple, si l'attaquant a une valeur de force inferieure ou egale a
	 * un quart de la valeur maximale de force, alors il projetera a la 
	 * distance donnee dans la premiere case.
	 * La deuxieme case contient la distance pour un attaquant ayant une force
	 * superieure au quart de la force possible, mais inferieure a la moitie ;
	 * la troisieme, entre la moitie et les trois quarts ; 
	 * la quatrieme, entre les trois quarts et le maximum possible.
	 */
	public static final int[] DISTANCE_PROJECTION = {4, 5, 6, 7};
	
	/**
	 * Distance minimale entre deux elements pour qu'une interaction soit
	 * possible. 
	 */
	public static final int DISTANCE_MIN_INTERACTION = 2;

	/**
	 * Abscisse minimale de l'arene.
	 */
	public static final int XMIN_ARENE = 0;

	/**
	 * Abscisse maximale de l'arene.
	 */
	public static final int XMAX_ARENE = 100;
	
	/**
	 * Ordonnee minimale de l'arene.
	 */
	public static final int YMIN_ARENE = 0;

	/**
	 * Ordonnee maximale de l'arene.
	 */
	public static final int YMAX_ARENE = 100;
	
	/**
	 * Couleur sur l'IHM pour les personnages qui sont morts ou les potions qui
	 * sont en attente.
	 */
	public static final Color COULEUR_MORTS_OU_EN_ATTENTE = new Color(112, 112, 112);
	
	/**
	 * Retourne le nom de la classe de l'objet passe en parametre, sous 
	 * forme de chaine de caracteres.
	 * @param o object
	 * @return chaine de caracteres representant la classe de l'objet
	 */
	public static String nomClasse(Object o) {
		return o.getClass().getSimpleName();
	}
	
	/**
	 * Renvoie le nom RMI associe a une adresse, un port et un nom.
	 * @param ip adresse
	 * @param port port 
	 * @param nom nom
	 * @return nom RMI de la forme "rmi://..."
	 */
	public static String nomRMI(String ip, int port, String nom) {
		return "rmi://" + ip + ":" + port + "/" + nom;
	}

	/**
	 * Cree une nom raccourci d'un client (pour les logs). 
	 * @param client client
	 * @return chaine de caracteres contenant la reference RMI de client, son 
	 * nom et son groupe. 
	 */
	public static String nomRaccourciClient(VueElement client) {
		return "(Client" + client.getRefRMI() + " * " + client.getElement().getNomGroupe() + ")";
	}

	/**
	 * Cree le nom complet d'un client (pour les logs).
	 * @param client client
	 * @return chaine de caracters contenant la classe du client, sa reference
	 * RMI, son nom et son groupe.
	 */
	public static String nomCompletClient(VueElement client) {
		return "("+ nomClasse(client) + client.getRefRMI() + " * " + client.getElement().getNomGroupe() + ")";
	}
}
