package utilitaires;

public class Util {

	/**
	 * Transforme une durée en Chaine de caractère de type H:M:S
	 * @param duree
	 * @return la durée sous forme de chaine H:M:S
	 */
	public static String timerToString(int duree) {
		if (duree < 0)
			return "illimité";
		int heure, minute, seconde;
		seconde = duree % 60;
		minute = duree / 60;
		heure = minute / 60;
		minute = minute % 60;
		
		String res;
		if (heure == 0){
			res = minute + ":" + ((seconde<10) ? "0" : "") + seconde ;
		} else {
			res = heure + ":" + ((minute<10) ? "0" : "") + minute + ":" + ((seconde<10) ? "0" : "") + seconde;				
		}
		return res;
	}
}
