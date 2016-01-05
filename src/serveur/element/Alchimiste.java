/**
 * 
 */
package serveur.element;

import java.util.HashMap;

/**
 * @author utilisateur
 *
 */
public class Alchimiste extends Personnage {
	
	public  Alchimiste (String nom, String groupe, HashMap<Caracteristique, Integer> caracts) {
		super(nom, groupe, caracts,30,50,50);

}
}