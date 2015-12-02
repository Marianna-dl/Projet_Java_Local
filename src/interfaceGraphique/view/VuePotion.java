package interfaceGraphique.view;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import modele.Caracteristique;

public class VuePotion extends VueElement {

	private static final long serialVersionUID = -149512999567318593L;

	public VuePotion(int refRMI, Point position, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, Color color,	String phrase) {
		super(refRMI, position, nom, groupe, caracts, color, phrase, TypeElement.POTION);
	}
	
		
	@Override
	public Color getColor() {
		return super.getColor();
	}
}
