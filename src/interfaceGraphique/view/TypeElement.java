package interfaceGraphique.view;

public enum TypeElement {
	PERSONNAGE("Personnage"),
	POTION("Potion"),
	TRESOR("Trésor"), 
	OBJET("Objet");
	
	public String nom;
	
	private TypeElement(String nom){
		this.nom = nom;
	}
	
	
}
