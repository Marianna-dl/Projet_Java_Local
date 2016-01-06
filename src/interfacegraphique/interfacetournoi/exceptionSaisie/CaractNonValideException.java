package interfacegraphique.interfacetournoi.exceptionSaisie;

import java.util.List;
import java.util.ListIterator;

import serveur.element.Caracteristique;

/**
 * Exception renvoye lors de la saisie de caracteristiques si elle est invalide.
 *
 */
public class CaractNonValideException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Liste des caracteristiques non valides.
	 */
	private List<Caracteristique> listeCaractNotValid;
	
	/**
	 * Cree une exception pour des caracteristiques non valides.
	 * @param l liste des caracteristiques non valides
	 */
	public CaractNonValideException(List<Caracteristique> l) {
		listeCaractNotValid = l;
	}

	/**
	 * Retourne une chaine de caracteres contenant les caracteristiques non
	 * valides.
	 * @return caracteristiques non valides sous forme d'une chaine de 
	 * caracteres
	 */
	public String afficheCaracts() {
		String s = "";
		
		ListIterator<Caracteristique> it = listeCaractNotValid.listIterator();
		
		while (it.hasNext()) {
			Caracteristique c = it.next();
			s += c.name();
			
			if (it.hasNext()) {
				s += ", ";
			}
		}
		
		return s;
	}
	
}
