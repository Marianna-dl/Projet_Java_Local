package interfacegraphique.interfacesimple.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JPanel;

/**
 * Panneau permettant de dessiner le portrait d'un element.
 *
 */
public class Portrait extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Couleur a afficher.
	 */
	private Color color;
	
	/**
	 * Vrai si on fait le portrait d'un personnage.
	 */
	private boolean personnage;
	
	/**
	 * Cree le portrait d'un element.
	 * @param c couleur
	 * @param personnage vrai si on dessine un personnage
	 */
	public Portrait(Color c, boolean personnage) {
		this.color = c;
		this.personnage = personnage;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(color);
		
		if(personnage) {
			g.fillOval(10, 10, this.getWidth() - 20, this.getWidth() - 20);	
			
		} else {
			Polygon p = new Polygon();
			int coordX, coordY;
			coordX = getWidth() / 2;
			coordY = getHeight() / 2;
			int hauteur = getWidth() - 20;
			int base = (int) (hauteur * 0.8);
			
			p.addPoint(coordX - base/2, coordY + hauteur/2);			
			p.addPoint(coordX + base/2, coordY + hauteur/2);			
			p.addPoint(coordX, coordY - hauteur/2);
			
			g.fillPolygon(p);
		}
	}
}
