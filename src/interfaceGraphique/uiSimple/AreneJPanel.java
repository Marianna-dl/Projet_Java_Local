package interfaceGraphique.uiSimple;

import interfaceGraphique.view.TypeElement;
import interfaceGraphique.view.VueElement;
import interfaceGraphique.view.VuePersonnage;

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

import serveur.Arene;
import serveur.element.Caracteristique;

/** 
 * Definit la fenetre de l'arene. 
 * Si le serveur de l'arene est connecte, recolte la VueElement des elements connectes et les dessine
 */
public class AreneJPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Liste de tous les objets connectes a l'interface.
	 */
	private List<VueElement> objets = new ArrayList<VueElement>();
	/**
	 * Liste de tous les personnages connectes a l'interface
	 */
	private List<VuePersonnage> personnages = new ArrayList<VuePersonnage>();
	/**
	 * Message a afficher
	 */
	private String message = null;
	/**
	 * Affiche les jauges de vies ou pas
	 */
	private boolean affichageJauge = false;
	/**
	 * Taille des elements
	 */
	private static final int ELEMENT_SIZE = 14;
	/**
	 * Couleur du rond entourant les elements selectionnes
	 */
	private static final Color SELECTED_COLOR = new Color(0,0,0, 70);
	/**
	 * Timer permettant l'affichage du compte a rebours
	 */
	private Timer declencheur;
	/**
	 * Decompte du compte a rebours
	 */
	private int decompte = 5;
	/**
	 * compte a rebours active ou pas
	 */
	private boolean compteARebours = false;


	
	public AreneJPanel() {
		super();
		// timer d'une seconde permettant l'affichage du compte a rebours
		declencheur = new Timer(1000, new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				decompte--;
				if(decompte == -1){
					compteARebours = false;
					declencheur.stop();
				}
			}
		});
	}
	
	/**
	 * Lance le compte a rebours
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
			g.drawString(message, 20, rect.height-20);
			message = null;
			g.setFont(of);				
		}
		

		for(VueElement vueElement:objets)
			dessinerElement(g, vueElement);

		for(VuePersonnage vuePersonnage : personnages)
			dessinerElement(g, vuePersonnage);
		
		// affiche le compte a rebours de debut de partie
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
	 * Dessine la representation d'une VueElement dans le graphics g
	 * @param g destination du dessin
	 * @param vueElement Element a dessiner
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
		drawElementGeometric(g, vueElement, coordX, coordY);									
		
		// ecrit le nom de l'element
		boolean descendu = drawElementName(g, vueElement, coordX, coordY);
		
		// dessine la jauge de vie du personnage
		if (affichageJauge && vueElement.getType().equals(TypeElement.PERSONNAGE))
			drawJauge(g, vueElement, rect, coordX, coordY, descendu);			
		
	}


	/**
	 * Dessine la representation geometrique de l'element
	 * @param g
	 * @param vueElement
	 * @param coordX
	 * @param coordY
	 */
	private void drawElementGeometric(Graphics g, VueElement vueElement, int coordX,
			int coordY) {

		if (vueElement.isSelected()){
			g.setColor(SELECTED_COLOR);
			g.fillOval(coordX - 5, coordY - 5, ELEMENT_SIZE + 10, ELEMENT_SIZE + 10);
			g.setColor(vueElement.getColor());
		}
		switch (vueElement.getType()){
		case OBJET:
			g.fillRect(coordX, coordY, ELEMENT_SIZE, ELEMENT_SIZE);
			break;
		case PERSONNAGE:
			// si le personnage est leader, on lui met une couronne
			if (((VuePersonnage)vueElement).isLeader()){
				Polygon couronne = createCouronne(coordX,coordY);
				g.fillPolygon(couronne);					
			}
			
			// construit un ovale aux coordonnes coordX, coordY de taille ELEMENT_SIZE x ELEMENT_SIZE
			g.fillOval(coordX, coordY, ELEMENT_SIZE, ELEMENT_SIZE);	
			break;
		case POTION:
			Polygon p = new Polygon();// Triangle
			p = createTriangle(coordX + ELEMENT_SIZE/2, coordY + ELEMENT_SIZE/2 - 1, ELEMENT_SIZE);
			g.fillPolygon(p);
			break;
		case TRESOR:
			
			g.fillRect(coordX -1, coordY +1, ELEMENT_SIZE + 3, ELEMENT_SIZE - 2);
			
			break;
		default:
			break;
		
		}
	}

	

	/**
	 * Ecris le nom de l'element
	 * @param g Graphics dans lequel ecrire le nom
	 * @param vueElement
	 * @param rect
	 * @param coordX
	 * @param coordY
	 * @return vrai si le texte a ete ecris en dessous de la forme representant l'element,
	 *  faux sinon
	 */
	private boolean drawElementName(Graphics g, VueElement vueElement, int coordX, int coordY) {

		Rectangle rect = this.getBounds();
		// affiche au dessus du point ses informations
		String s = vueElement.getNom();
		int stringWidth = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		int stringHeight = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
		int start = (stringWidth/2) - (ELEMENT_SIZE/2);
		
		// gestion du debordement des infos
		int coordXString = Math.max(coordX - start, 2);
		if (coordXString + stringWidth > rect.getWidth()){
			coordXString = (int) (rect.getWidth() - 2 - stringWidth);
		}
		int coordYString = coordY - 10;
		boolean descendu = false;
		if (coordY < stringHeight){
			coordYString = coordY + 29;
			descendu = true;
		}
		g.drawString(s, coordXString, coordYString);
		return descendu;
	}

	/**
	 * Dessine la jauge de vie de l'element
	 * @param g
	 * @param vueElement
	 * @param rect
	 * @param coordX
	 * @param coordY
	 * @param descendu
	 */
	private void drawJauge(Graphics g, VueElement vueElement, Rectangle rect, int coordX, int coordY, boolean descendu) {

		Color elementColor = vueElement.getColor();			
		// dessin de la jauge de vie			
		int barWidth = 80;
		int barHeight = 13;
		int barStart = (barWidth/2) - (ELEMENT_SIZE/2);
		
		// gestion du debordement de la barre
		int coordXBar = Math.max(coordX - barStart, 2);
		if (coordXBar + barWidth > rect.getWidth()){
			coordXBar = (int) (rect.getWidth() - 2 - barWidth);
		}
		int coordYBar = coordY - 38;
		if (coordYBar < 0){
			if (descendu)
				coordYBar = coordY + 35;
			else
				coordYBar = coordY + 22;
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
	 * Initialise le message a afficher
	 * @param string message a afficher
	 */
	public void afficherMessage(String string) {
		message = string;
	}

	/**
	 * Initialise les VueElement a afficher
	 * @param personnages 
	 * @param objets liste des VueElement a afficher
	 */
	public void updateWorld(List<VuePersonnage> personnages, List<VueElement> objets) {
		this.objets = objets;
		this.personnages = personnages;
	}

	/**
	 * Defini l'affichage ou non des jauges de vie
	 * @param affichage affichage des jauges
	 */
	public void setAffichageJauge(boolean affichage) {
		affichageJauge = affichage;
	}
	
	/**
	 * Creee une couronne
	 * @param coordX
	 * @param coordY
	 * @return polygon correspondant a une couronne
	 */
	private Polygon createCouronne(int coordX, int coordY) {
		int largeur = ELEMENT_SIZE - 2;
		int x = coordX + 1;
		int y = coordY - 1;
		
		int tailleQuart = largeur / 4;
					
		int[] couronneX = {x, x, x + tailleQuart, x + 2* tailleQuart, x + 3* tailleQuart, x + 4* tailleQuart, x + 4* tailleQuart};
		int[] couronneY = {y, y - 8, y - 4, y - 8, y - 4, y - 8 ,y};
		
		return new Polygon(couronneX, couronneY, 7);
	}

	/**
	 * Cree un triangle
	 * @param coordX coordonnee x du centre du triangle
	 * @param coordY coordonnee y du centre du triangle
	 * @param base taille de la base du triangle
	 * @return polygon correspondant a un triangle
	 */
	private Polygon createTriangle(int coordX, int coordY, int base) {
		Polygon p = new Polygon();
		int hauteur = (int) (1.2 * base);
		
		p.addPoint(coordX - base/2, coordY + hauteur/2);			
		p.addPoint(coordX + base/2 , coordY + hauteur/2);			
		p.addPoint(coordX, coordY - hauteur/2);
		
		return p;
	}

	/**
	 * Renvoi la position dans l'arene correspondant a une position cliquee sur le panel
	 * @param point position dans le panel
	 * @return position dans l'arene
	 */
	public Point getPositionArene(Point point) {
		
		Rectangle rect = this.getBounds();			

		int width = (int) rect.getWidth();
		int height = (int) rect.getHeight();
		
		int x = (int) point.getX() - (ELEMENT_SIZE/2);
		int y = (int) point.getY() - (ELEMENT_SIZE/2);
		
		int coordX = (x * Arene.XMAX) / (width - ELEMENT_SIZE);
		int coordY = (y * Arene.YMAX) / (height - ELEMENT_SIZE);

		return new Point(coordX,coordY);
	}
	
	/**
	 * Renvoi la position dans le panel d'une position de l'arene
	 * @param point position dans l'arene
	 * @return position dans le panel
	 */
	public Point getRealPosition(Point point){

		Rectangle rect = this.getBounds();
		
		int width = (int) rect.getWidth();
		int height = (int) rect.getHeight();
		
		int x = (int) point.getX();
		int y = (int) point.getY();
		
		int coordX = x * (width - ELEMENT_SIZE) / Arene.XMAX;
		int coordY = y * (height - ELEMENT_SIZE) / Arene.YMAX;
		
		return new Point(coordX, coordY);
	}
	
	
}