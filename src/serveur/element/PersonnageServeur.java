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
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom du personnage
	 * @param groupe groupe du personnage
	 */
	public PersonnageServeur(String nom, String groupe) {
		// Caracteristiques de base
		super(nom, groupe, Caracteristique.hachageDeCaracInitPerso());
		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
		
	/** ----------------------------------------------------------
	 * 		public int getLeader()
	 *  ----------------------------------------------------------
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 *  ---------------------------------------------------------- */
	public int getLeader() {
		return leader;
	}

	
	/** ----------------------------------------------------------
	 * 		public ArrayList<Integer> getEquipe
	 *  ----------------------------------------------------------
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 * -------------------------------------------------------- */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	/** ----------------------------------------------------------
	 * 		public String toString
	 *  ----------------------------------------------------------
	 *  Cette methode determine l'affichage des differentes stats
	 *  ----------------------------------------------------------
	 * @return string
	 * ------------------------------------------------------- */
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
		
		return super.toString() + "[" + Caracteristique.hachageToString(caract) + lead + eq + "]";
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
		caract.put(c, Calculs.caperCarac(c, val));
	}

	/** 
	 */
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
	 * @param ref reference de l'équipier
	 */
	public void ajouterEquipier(int ref) {
		equipe.add((Integer) ref);
	}

	/**
	 * Enleve une ref de l'équipe du personnage
	 * @param ref reference de l'équipier
	 */
	public void enleverEquipier(int ref) {
		equipe.remove((Integer) ref);
	}


	/**
	 * Vide l'équipe du personnage
	 */
	public void enleverTouteEquipe() {
		equipe.clear();
	}
}
