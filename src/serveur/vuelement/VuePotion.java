package serveur.vuelement;

import java.awt.Point;

import serveur.element.Potion;

/**
 * Donnees que le serveur doit conserver sur chacun de ces clients potions.
 */
public class VuePotion extends VueElement<Potion> implements Comparable<VuePotion> {
	
	private static final long serialVersionUID = 4227900415029065269L;

	/**
	 * Cree une vue d'une potion personnage.
	 * @param potion potion correspondante
	 * @param position position courante
	 * @param ref reference RMI
	 */
	public VuePotion(Potion potion, Point position, int ref) {
		super(potion, position, ref);
	}

	@Override
	public int compareTo(VuePotion vp2) {
		return vp2.getRefRMI() - this.getRefRMI();
	}
}
