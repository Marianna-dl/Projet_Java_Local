package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Archer;
import serveur.element.Caracteristique;
import serveur.element.Magicien;
import serveur.element.Paladin;
import serveur.element.Personnage;
import serveur.element.Voleur;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente un duel entre deux personnages.
 *
 */
public class Duel extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Duel(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	@Override
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie = forceAttaquant;
			Personnage pDefenseur = (Personnage) defenseur.getElement();
			
			if(!(pAttaquant instanceof Magicien) && !(pAttaquant instanceof Archer)){
				
				Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), forceAttaquant);
				// ejection du defenseur
				defenseur.setPosition(positionEjection);
			}
			
			//On teste si on attaque un Paladin
			if(pDefenseur instanceof Paladin){
				if(blocAttaque(pAttaquant.getCaracts().get(Caracteristique.FORCE),
						pDefenseur.getCaracts().get(Caracteristique.FORCE)))
					perteVie=0;

				}
		
			
			//On teste si c'est un voleur
			if(pAttaquant instanceof Voleur){
				int init = pDefenseur.getCaract(Caracteristique.INITIATIVE);
				int voleInit = Calculs.nombreAleatoire(0,init);	
				if(!((voleInit+pAttaquant.getCaract(Caracteristique.INITIATIVE))> pAttaquant.getMaxInit())){
					
					arene.incrementeCaractElement(attaquant, Caracteristique.INITIATIVE, +voleInit);
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vole l'initiative  ("
							+ voleInit + " points d'initiative) a " + Constantes.nomRaccourciClient(defenseur));
				}
					
			}
			
			// degats
			if (perteVie > 0) {
				//On test si c'est un Archer
                if(pAttaquant instanceof Archer){
                    int reussite= Calculs.nombreAleatoire(0, 10);
                    int saveVie =perteVie;
                    perteVie=0;
                    if (reussite==0)
                        perteVie=saveVie;
                }
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " colle une beigne ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			
			// initiative
			incrementeInitiative(defenseur);
			decrementeInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}

	//Calcul la probabilite que le Paladin ait de bloquer une attaque
	private boolean blocAttaque(Integer forceAtk, Integer forceDef) {
		// TODO Auto-generated method stub
		int bloc=Calculs.nombreAleatoire(0, 100);
		if(2*forceAtk<=forceDef)
			return(bloc>=5);
		if(forceAtk<forceDef)
			return(bloc>=20);
		if(forceAtk>=2*forceDef)
			return (bloc>=90);
		if(forceAtk>forceDef)
			return (bloc>=75);
		else
			return(bloc>=50);
	}

	/**
	 * Incremente l'initiative du defenseur en cas de succes de l'attaque. 
	 * @param defenseur defenseur
	 * @throws RemoteException
	 */
	private void incrementeInitiative(VuePersonnage defenseur) throws RemoteException {
		arene.incrementeCaractElement(defenseur, Caracteristique.INITIATIVE, 
				Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementeInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.incrementeCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}

	
	/**
	 * Retourne la position ou le defenseur se retrouvera apres ejection.
	 * @param posDefenseur position d'origine du defenseur
	 * @param positionAtt position de l'attaquant
	 * @param forceAtt force de l'attaquant
	 * @return position d'ejection du personnage
	 */
	private Point positionEjection(Point posDefenseur, Point positionAtt, int forceAtt) {
		int distance = forceVersDistance(forceAtt);
		
		// abscisses 
		int dirX = posDefenseur.x - positionAtt.x;
		
		if (dirX > 0) {
			dirX = distance;
		}
		
		if (dirX < 0) {
			dirX = -distance;
		}
		
		// ordonnees
		int dirY = posDefenseur.y - positionAtt.y;
		
		if (dirY > 0) {
			dirY = distance;
		}
		
		if (dirY < 0) {
			dirY = -distance;
		}
		
		int x = posDefenseur.x + dirX;
		int y = posDefenseur.y + dirY;
		
		return Calculs.restreintPositionArene(new Point(x, y));
	}
	
	/**
	 * Calcule la distance a laquelle le defenseur est projete suite a un coup.
	 * @param forceAtt force de l'attaquant
	 * @return distance de projection
	 */
	private int forceVersDistance(int forceAtt) {
		int max = Caracteristique.FORCE.getMax();
		
		int quart = (int) (4 * ((float) (forceAtt - 1) / max)); // -1 pour le cas force = 100
		
		return Constantes.DISTANCE_PROJECTION[quart];
	}
}
