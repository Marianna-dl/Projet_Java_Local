package interfaceGraphique.view;

public enum TypeElement {
	PERSONNAGE("Personnage"),
	POTION("Potion"),
	TRESOR("Tresor"), 
	OBJET("Objet");
	
	private String nom;
	
	private TypeElement(String nom){
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}
	
	
}
