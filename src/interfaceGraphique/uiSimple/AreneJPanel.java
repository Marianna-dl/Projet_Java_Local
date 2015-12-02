package interfaceGraphique.uiSimple;

import interfaceGraphique.view.TypeElement;
import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;
import interfaceGraphique.view.VuePotion;
import modele.Caracteristique;
import utilitaires.Constantes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

/** 
 * Gere la fenetre de l'arene. 
 * Si le serveur de l'arene est connecte, recupere la VueElement des elements 
 * connectes et les dessine.
 */
public class AreneJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Liste de tous les personnages connectes a l'interface
	 */
	private List<VuePersonnage> personnages = new ArrayList<VuePersonnage>();

	/**
	 * Liste de toutes les potions connectees a l'interface.
	 */
	private List<VuePotion> potions = new ArrayList<VuePotion>();
	
	/**
	 * Message a afficher.
	 */
	private String message = null;
	
	/**
	 * Vrai les jauges de vie doivent etre affichees.
	 */
	private boolean jaugesAffichees = false;
	
	/**
	 * Taille des elements.
	 */
	private static final int ELEMENT_SIZE = 14;
	
	/**
	 * Couleur du rond entourant les elements selectionnes.
	 */
	private static final Color SELECTED_COLOR = new Color(0,0,0,70);
	
	/**
	 * Timer permettant l'affichage du compte a rebours.
	 */
	private Timer declencheur;
	
	/**
	 * Decompte avant le demarrage de la partie.
	 */
	private int decompte = 5;
	
	/**
	 * Vrai si le compte a rebours a demarre.
	 */
	private boolean compteARebours = false;

	/**
	 * Cree un panel affichant l'arene.
	 */
	public AreneJPanel() {
		super();
		
		// timer d'une seconde permettant l'affichage du compte a rebours
		declencheur = new Timer(1000, new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				decompte--;
				
				if(decompte == -1) {
					compteARebours = false;
					declencheur.stop();
				}
			}
		});
	}
	
	/**
	 * Lance le compte a rebours.
	 */
	public void lancerCompteARebours() {
		compteARebours = true;
		declencheur.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Rectangle rect = this.getBounds();
		
		// si la connexion est en cours ou il y a une erreur
		if (message != null) {
			Font of = g.getFont();
			g.setFont(new Font("Arial",Font.BOLD,20));
			g.drawString(message, 20, rect.height - 20);
			message = null;
			g.setFont(of);				
		}
		
		// dessiner les elements
		for(VueElement vueElement:potions) {
			dessinerElement(g, vueElement);
		}

		for(VuePersonnage vuePersonnage : personnages) {
			dessinerElement(g, vuePersonnage);
		}
		
		// affiche le decompte avant le debut de partie
		if (compteARebours) {
			g.setColor(new Color(0, 0, 0, 255));
			Font of = g.getFont();
			g.setFont(new Font("Helvetica", Font.BOLD, 150));
			
			if (decompte <= 0) {
				g.drawString("GO !", (rect.width / 2) - 150, (rect.height / 2) + 30);
			} else {
				g.drawString(decompte + "", (rect.width /2) -50, (rect.height / 2) + 30);
			}
			
			g.setFont(of);
		}
		
	}
	
	/**
	 * Dessine une VueElement.
	 * @param g graphics
	 * @param vueElement vue de l'element a dessiner
	 */
	private void dessinerElement(Graphics g, VueElement vueElement) {
		// affiche l'arene comme un rectangle
		Rectangle rect = this.getBounds();
		
		// calcule les coordonnes pour afficher l'element
		Point p = getRealPosition(vueElement.getPosition());

		int coordX = (int) p.getX();
		int coordY = (int) p.getY();
		
		// definit la couleur de l'element
		g.setColor(vueElement.getColor());
		
		// dessine la representation geometrique de l'element
		dessinerElementGeometrique(g, vueElement, coordX, coordY);									
		
		// ecrit le nom de l'element
		boolean descendu = dessinerElementNom(g, vueElement, coordX, coordY);
		
		// dessine la jauge de vie du personnage
		if(jaugesAffichees && vueElement.getType().equals(TypeElement.PERSONNAGE)) {
			dessinerJauge(g, vueElement, rect, coordX, coordY, descendu);		
		}
	}

	/**
	 * Dessine la representation geometrique de l'element.
	 * @param g graphics
	 * @param vueElement vue de l'element a dessiner
	 * @param coordX abscisse de l'element
	 * @param coordY ordonnee de l'element
	 */
	private void dessinerElementGeometrique(Graphics g, VueElement vueElement, int coordX, int coordY) {

		if (vueElement.isSelected()) {
			g.setColor(SELECTED_COLOR);
			g.fillOval(coordX - 5, coordY - 5, ELEMENT_SIZE + 10, ELEMENT_SIZE + 10);
			g.setColor(vueElement.getColor());
		}
		
		switch (vueElement.getType()) {
		case PERSONNAGE:
			// construit un ovale aux coordonnes coordX, coordY de taille ELEMENT_SIZE x ELEMENT_SIZE
			g.fillOval(coordX, coordY, ELEMENT_SIZE, ELEMENT_SIZE);	
			break;
		case POTION:
			Polygon p = new Polygon();// Triangle
			p = creeTriangle(coordX + ELEMENT_SIZE/2, coordY + ELEMENT_SIZE/2 - 1, ELEMENT_SIZE);
			g.fillPolygon(p);
			break;
		}
	}

	/**
	 * Ecrit le nom de l'element.
	 * @param g graphics
	 * @param vueElement vue de l'element a dessiner
	 * @param coordX abscisse de l'element
	 * @param coordY ordonnee de l'element
	 * @return vrai si le texte a ete ecrit en dessous de la forme representant 
	 * l'element, faux sinon
	 */
	private boolean dessinerElementNom(Graphics g, VueElement vueElement, int coordX, int coordY) {
		Rectangle rect = this.getBounds();
		
		// affiche au dessus du point ses informations
		String s = vueElement.getNom();
		int stringWidth = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		int stringHeight = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
		int start = (stringWidth/2) - (ELEMENT_SIZE/2);
		
		// gestion du debordement des infos
		int coordXString = Math.max(coordX - start, 2);
		
		if (coordXString + stringWidth > rect.getWidth()) {
			coordXString = (int) (rect.getWidth() - 2 - stringWidth);
		}
		
		int coordYString = coordY - 10;
		boolean descendu = false;
		
		if (coordY < stringHeight) {
			coordYString = coordY + 29;
			descendu = true;
		}
		
		g.drawString(s, coordXString, coordYString);
		
		return descendu;
	}

	/**
	 * Dessine la jauge de vie de l'element.
	 * @param g graphics
	 * @param vueElement vue de l'element a dessiner
	 * @param rect rectangle de l'arene
	 * @param coordX abscisse de l'element
	 * @param coordY ordonnee de l'element
	 * @param descendu vrai si le texte a ete ecrit en dessous de la forme 
	 * representant l'element, faux sinon
	 */
	private void dessinerJauge(Graphics g, VueElement vueElement, Rectangle rect, int coordX, int coordY, boolean descendu) {
		Color elementColor = vueElement.getColor();	
		
		// dessin de la jauge de vie			
		int barWidth = 80;
		int barHeight = 13;
		int barStart = (barWidth/2) - (ELEMENT_SIZE/2);
		
		// gestion du debordement de la barre
		int coordXBar = Math.max(coordX - barStart, 2);
		
		if (coordXBar + barWidth > rect.getWidth()) {
			coordXBar = (int) (rect.getWidth() - 2 - barWidth);
		}
		
		int coordYBar = coordY - 38;
		
		if (coordYBar < 0) {
			if (descendu) {
				coordYBar = coordY + 35;
			} else {
				coordYBar = coordY + 22;
			}
		}
		
		// dessin du contour de la jauge
		g.drawRect(coordXBar - 1, coordYBar - 1, barWidth + 1, barHeight + 1);
		g.drawRect(coordXBar - 2, coordYBar - 2, barWidth + 3, barHeight + 3);
		
		// remplissage du fond de jauge
		g.setColor(new Color(183, 28, 28, 100));
		g.fillRect(coordXBar, coordYBar, barWidth, barHeight);
		
		// remplissage de la jauge
		Integer hp = vueElement.getCaract(Caracteristique.VIE);
		int hpWidth = hp * barWidth / 100;
		
		g.setColor(new Color(183, 28, 28));
		g.fillRect(coordXBar, coordYBar, hpWidth, barHeight);
		
		// ecriture de la valeur
		g.setColor(elementColor );

		Font fontSave = g.getFont();
		g.setFont(new Font("Arial",Font.PLAIN,12));
		String hpString = hp.toString();
		int hpStringWidth = (int) g.getFontMetrics().getStringBounds(hpString, g).getWidth();
		
		int coordXHp = coordXBar + ((barWidth - hpStringWidth) / 2);
		g.drawString(hpString, coordXHp, coordYBar + 11);
		
		g.setFont(fontSave);			
	}

	/**
	 * Cree un triangle.
	 * @param coordX coordonnee x du centre du triangle
	 * @param coordY coordonnee y du centre du triangle
	 * @param base taille de la base du triangle
	 * @return polygone correspondant a un triangle
	 */
	private Polygon creeTriangle(int coordX, int coordY, int base) {
		Polygon p = new Polygon();
		int hauteur = (int) (1.2 * base);
		
		p.addPoint(coordX - base/2, coordY + hauteur/2);			
		p.addPoint(coordX + base/2 , coordY + hauteur/2);			
		p.addPoint(coordX, coordY - hauteur/2);
		
		return p;
	}

	/**
	 * Definit le message a afficher.
	 * @param msg message a afficher
	 */
	public void afficherMessage(String msg) {
		message = msg;
	}

	/**
	 * Definit les VueElement a afficher.
	 * @param personnages liste des personnages a afficher
	 * @param potions liste des potions a afficher
	 */
	public void setVues(List<VuePersonnage> personnages, List<VuePotion> potions) {
		this.potions = potions;
		this.personnages = personnages;
	}

	/**
	 * Definit si les jauges de vie doivent etre affichees.
	 * @param affichage vrai si les jauges doivent etre affichees, faux sinon
	 */
	public void setAffichageJauge(boolean affichage) {
		jaugesAffichees = affichage;
	}

	/**
	 * Renvoie la position dans l'arene correspondant a une position cliquee 
	 * sur le panel.
	 * @param point position dans le panel
	 * @return position dans l'arene
	 */
	public Point getPositionArene(Point point) {
		Rectangle rect = this.getBounds();			

		int width = (int) rect.getWidth();
		int height = (int) rect.getHeight();
		
		int x = (int) point.getX() - (ELEMENT_SIZE/2);
		int y = (int) point.getY() - (ELEMENT_SIZE/2);
		
		int coordX = (x * Constantes.XMAX_ARENE) / (width - ELEMENT_SIZE);
		int coordY = (y * Constantes.YMAX_ARENE) / (height - ELEMENT_SIZE);

		return new Point(coordX,coordY);
	}
	
	/**
	 * Renvoie la position dans le panel correspondant a une position de 
	 * l'arene.
	 * @param point position dans l'arene
	 * @return position dans le panel
	 */
	public Point getRealPosition(Point point) {
		Rectangle rect = this.getBounds();
		
		int width = (int) rect.getWidth();
		int height = (int) rect.getHeight();
		
		int x = (int) point.getX();
		int y = (int) point.getY();
		
		int coordX = x * (width - ELEMENT_SIZE) / Constantes.XMAX_ARENE;
		int coordY = y * (height - ELEMENT_SIZE) / Constantes.YMAX_ARENE;
		
		return new Point(coordX, coordY);
	}
	
	
}