package serveur.element;



public class Tresor extends Element {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur d'une potion avec un nom et une quantite de force et de charisme
	 * (ces quantites sont celles ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom
	 * @param groupe
	 * @param montant
	 */
	public Tresor(String nom, String groupe, int montant) {
		super(nom,groupe,Caracteristique.hachageDeTresor(montant));
	}
	
	
	@Override
	/** ----------------------------------------------------------
	 * 		public String toString
	 *  ----------------------------------------------------------
	 *  Cette methode determine l'affichage des differentes stats
	 *  ----------------------------------------------------------
	 * @return string
	 * ------------------------------------------------------- */
	public String toString(){
		return super.toString() + " " + Caracteristique.hachageToString(caract);
	}
}
