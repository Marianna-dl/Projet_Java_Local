package serveur.element;

import java.util.HashMap;

public class PotionClairvoyance extends Potion{

	private int vue;
	
	public PotionClairvoyance(int vision,HashMap<Caracteristique, Integer> caracts) {
		super("Potion de Clairvoyance", "24", caracts);
		this.vue=vision;
		// TODO Auto-generated constructor stub
	}
	public int getVue(){
		return this.vue;
	}
}
