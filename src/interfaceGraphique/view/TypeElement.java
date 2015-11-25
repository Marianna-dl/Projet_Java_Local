package interfaceGraphique.view;

public enum TypeElement {
	PERSONNAGE("Personnage"),
	POTION("Potion"),
	TRESOR("Tresor"), 
	OBJET("Objet");
	
	public String nom;
	
	private TypeElement(String nom){
		this.nom = nom;
	}
	
	
}
