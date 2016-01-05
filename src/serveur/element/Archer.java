/**
 * 
 */
package serveur.element;

import java.util.HashMap;

/**
 * @author utilisateur
 *
 */
public class Archer extends Personnage  {
	
		public Archer (String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super("archer", "24", caracts, 20,50,40);
}
		
}