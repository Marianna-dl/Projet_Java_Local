/**
 * 
 */
package serveur.element;

import java.io.Serializable;
import java.util.ArrayList;

import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie.
 */
public class PersonnageServeur extends Element implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Reference du leader de ce personnage, -1 si aucun.
	 */
	protected int leader;
	
	/**
	 * Reference des personnages de l'equipe de ce personnage. 
	 * Vide si le leader n'est pas egal a -1.
	 */
	protected ArrayList<Integer> equipe;

	/**
	 * Constructeur d'un personnage avec un nom et un groupe.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom du personnage
	 * @param groupe groupe du personnage
	 */
	public PersonnageServeur(String nom, String groupe) {
		// Caracteristiques de base
		super(nom, groupe, Caracteristique.caracteristiquesDefaut());
		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
		
	/**
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 */
	public int getLeader() {
		return leader;
	}

	
	/** 
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	/** 
	 *  Cette methode determine l'affichage des differentes stats
	 */
	public String toString() {
		String lead = (leader != -1)? ", leader: " + leader: "";
		String eq = "";
		
		if(!equipe.isEmpty()) {
			eq += ", equipe: ";
			
			for(int i = 0; i < equipe.size(); i++) {
				eq += equipe.get(i) + " ";
				
				if(i < equipe.size() - 1) {
					eq += " ";
				}
			}
		}
		
		return super.toString() + "[" + Caracteristique.caracteristiquesToString(caracts) + lead + eq + "]";
	}
	
	/**
	 * Ajoute la caracteristique specifiee avec la valeur specifiee. Si la 
	 * caracteristique existe deja, la valeur sera ecrasee.
	 * @param c caracteristique
	 * @param val valeur
	 */
	public void ajouterCaract(Caracteristique c, int val) {
		if (c == Caracteristique.VIE && val <= 0) {
			setAlive(false);
		}		
		caracts.put(c, Calculs.caperCarac(c, val));
	}

	/**
	 * definie qui est le leader
	 * @param ref ref du leader
	 */
	public void setLeader(int ref) {
		leader = ref;
	}
	
	/**
	 * Reinitialise le leader du personnage
	 */
	public void clearLeader() {
		leader = -1;
	}
	
	/**
	 * Ajoute une ref dans l'equipe du personnage
	 * @param ref reference de l'equipier
	 */
	public void ajouterEquipier(int ref) {
		equipe.add((Integer) ref);
	}

	/**
	 * Enleve une ref de l'equipe du personnage
	 * @param ref reference de l'equipier
	 */
	public void enleverEquipier(int ref) {
		equipe.remove((Integer) ref);
	}


	/**
	 * Vide l'equipe du personnage
	 */
	public void enleverTouteEquipe() {
		equipe.clear();
	}
}
