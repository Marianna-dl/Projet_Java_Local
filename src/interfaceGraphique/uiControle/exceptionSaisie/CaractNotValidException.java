package interfaceGraphique.uiControle.exceptionSaisie;

import java.util.List;
import java.util.ListIterator;

import serveur.element.Caracteristique;

/**
 * Exception renvoyé lors de la saisie de caractéristiques
 * si elles sont invalides
 *
 */
public class CaractNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Liste des caractéristiques non valides
	 */
	private List<Caracteristique> listeCaractNotValid;
	
	public CaractNotValidException(List<Caracteristique> l){
		listeCaractNotValid = l;
	}
	
	public List<Caracteristique> getCaracteristique(){
		return listeCaractNotValid;
	}

	public String afficherCaracts() {
		String s = "";
		ListIterator<Caracteristique> it = listeCaractNotValid.listIterator();
		while (it.hasNext()){
			Caracteristique c = it.next();
			s+= c.name();
			if (it.hasNext())
				s+=", ";
		}
		return s;
	}
	
}
