package serveur;

public class PaireRefIntitiative{

	private int ref;
	private int initiative;
	public PaireRefIntitiative(int ref, int initiative) {
		super();
		this.setRef(ref);
		this.setInitiative(initiative);
	}
	public int getRef() {
		return ref;
	}
	public void setRef(int ref) {
		this.ref = ref;
	}
	public int getInitiative() {
		return initiative;
	}
	public void setInitiative(int initiative) {
		this.initiative = initiative;
	}
	@Override
	public String toString() {
		return "[ref=" + ref + ", init=" + initiative
				+ "]";
	}	
	
	
}
