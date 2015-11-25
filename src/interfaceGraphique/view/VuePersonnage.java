package interfaceGraphique.view;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import serveur.element.Caracteristique;

public class VuePersonnage extends VueElement {

	private static final long serialVersionUID = -149512999567318593L;
	
	private List<Integer> equipe;
	private int leader;

	public VuePersonnage(int refRMI, Point position, String nom, String groupe,
			Hashtable<Caracteristique, Integer> caracts, Color color,
			String phrase, List<Integer> equipe, int leader) {
		super(refRMI, position, nom, groupe, caracts, color, phrase, TypeElement.PERSONNAGE);
		
		this.equipe = equipe;
		this.leader = leader;
	}
	
	public VuePersonnage(int refRMI, Point position, String nom, String groupe,
			Hashtable<Caracteristique, Integer> caracts, Color color,
			String phrase) {
		super(refRMI, position, nom, groupe, caracts, color, phrase, TypeElement.PERSONNAGE);
		
		this.equipe = new ArrayList<Integer>();
		this.leader = -1;
	}
	
		
	@Override
	public Color getColor() {
		if (leader != -1){
			
		}
		return super.getColor();
	}

	/**
	 * Renvoie l'equipe de l'element sous forme de chaine
	 * Si l'element n'est pas un leader, renvoi "-"
	 * @return equipe de l'element sous forme de String
	 */	
	public String equipeToString(){
		String res = "-";
		if (isLeader()){
			res = "";
			for (Integer ref : equipe)
				res+=ref+" - ";
			res = res.substring(0, res.length()-2);
		}
        return res;
	}

	/**
	 * Renvoi le leader de l'element sous forme de chaine
	 * Si l'element n'a pas de leader, renvoi "-"
	 * @return leader de l'element sous forme de String
	 */
	public String leaderToString(){
		String s;
		if (leader == -1){
			s = "-";
		} else {
			s = String.valueOf(leader);			
		}
		return s;
	}
	
	/**
	 * Permet de savoir si l'element est un leader ou pas
	 * @return vrai si l'element est un leader, faux sinon
	 */
	public boolean isLeader() {
		return !equipe.isEmpty();
	}
	
	/**
	 * Permet de connaitre le nombre d'equipier de l'element
	 * @return nombre de personnages dans l'equipe de l'element
	 */
	public int getNombreEquipiers(){
		return equipe.size();
	}
}
