package serveur.infosclient;

import java.awt.Point;

import serveur.element.Potion;

/**
 * Toutes les donnees que le serveur doit conserver sur chacun de ces clients
 */
public class VuePotion extends VueElement {
	
	private static final long serialVersionUID = 4227900415029065269L;

	public VuePotion(Potion pers, Point position, int ref, boolean envoyeImm) {
		super(pers, position, ref, envoyeImm);
	}
}
