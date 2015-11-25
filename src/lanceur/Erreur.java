package lanceur;

public enum Erreur {
	too_much_arg	(1, "Trop d'argument !"),
	port_NaN		(2, "Le premier argument doit être une nombre entre 0 et 65535 !"),
	ip_incorrect	(3, "Adresse IP de l'arene incorrect !"),
	ttl_incorrect	(4,"Le TTL doit être un nombre positif");
	
	public static int suivant = 5;
	
	public String str;
	public int code;
	
	private Erreur(int code, String str) {
		this.code = code;
		this.str = str;
	}
	
	public void erreur(String usage) {
		System.err.println("\tErreur "+code+" : "+str);
		System.err.println("\t"+usage);
		System.exit(code);
	}
	
	public static void help(String usage) {
		System.out.println(usage);
		System.out.println();
		System.out.println("Ce programme fait partie d'une suite de logiciels"
						+ "servant pour le projet de programmation de L3 de la"
						+ "licence d'informatique de l'université paul sabatier.");
		System.out.println();
		System.out.println("Voici la liste des codes de retour valable pour toute la suite logiciel :");
		System.out.println("\tCode 0 : Aucun probleme d'execution");
		for (Erreur e : values()) {
			System.out.println("\tCode "+e.code+" : "+e.str);
		}
		System.out.println("\tCode "+suivant+" : Tout autre probleme");
		System.exit(0);
	}
}
