package serveur.element;



public class Tresor extends Element {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur d'une potion avec un nom, un groupe et un montant 
	 * (ce montant est ajoute lorsqu'un Personnage ramasse ce tresor).
	 * @param nom
	 * @param groupe
	 * @param montant
	 */
	public Tresor(String nom, String groupe, int montant) {
		super(nom,groupe,Caracteristique.hachageDeTresor(montant));
	}
	
	
	@Override
	public String toString(){
		return super.toString() + " " + Caracteristique.hachageToString(caract);
	}
}
