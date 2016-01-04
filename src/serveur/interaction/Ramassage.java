package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.element.Voleur;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

/**
 * Represente le ramassage d'une potion par un personnage.
 *
 */
public class Ramassage extends Interaction<VuePotion> {

	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}

	@Override
	public void interagir() {
		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de rammasser " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant et la potion non encore ramassee
			if(attaquant.getElement().estVivant() && defenseur.getElement().estVivant()) {
				// caracteristiques de la potion
				HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
				Personnage pRamasseur = (Personnage) attaquant.getElement();
				int vie = pRamasseur.getCaract(Caracteristique.VIE);
				int init = pRamasseur.getCaract(Caracteristique.INITIATIVE);
				int force = pRamasseur.getCaract(Caracteristique.FORCE);

				int maxVie = pRamasseur.getMaxVie();
				logs(Level.INFO, "MAXVIEEE "+maxVie);
				int maxForce = pRamasseur.getMaxForce();
				int maxInit = pRamasseur.getMaxInit();

				
				for(Caracteristique c : valeursPotion.keySet()) {
				
					if(c.getNomComplet().equals("Vie") && valeursPotion.get(c)+vie > maxVie){
						logs(Level.INFO, "INFOOOOOOOOOOO "+( (maxVie+valeursPotion.get(c))-(vie+valeursPotion.get(c))) );
						logs(Level.INFO, "valeurPo "+valeursPotion.get(c) );
						logs(Level.INFO, "vie "+vie );
						logs(Level.INFO, "maxvie "+maxVie);
						logs(Level.INFO, "1e  "+(maxVie+valeursPotion.get(c)) );
						logs(Level.INFO, "2e  "+((vie+valeursPotion.get(c))) );
						
						arene.ajouterCaractElement(attaquant, c, ( (maxVie+valeursPotion.get(c))-(vie+valeursPotion.get(c))) );
						
					}
					else if(c.getNomComplet().equals("Force") && valeursPotion.get(c)+force > maxForce){
						arene.ajouterCaractElement(attaquant, c, ( (maxForce+valeursPotion.get(c))-(force+valeursPotion.get(c))) );
					}
					else if(c.getNomComplet().equals("Initiative") && valeursPotion.get(c)+init > maxInit){
						arene.ajouterCaractElement(attaquant, c, ( (maxInit+valeursPotion.get(c))-(init+valeursPotion.get(c))) );
					}
					else{
						arene.ajouterCaractElement(attaquant, c, valeursPotion.get(c));
					}
				}
				
				logs(Level.INFO, "Potion bue !");
				
				logs(Level.INFO, "INFOOOOOOOOOOO "+pRamasseur.getCaract(Caracteristique.VIE));
				
				// test si mort
				if(!attaquant.getElement().estVivant()) {
					arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
				}

				// suppression de la potion
				((Potion) defenseur.getElement()).ramasser();
				arene.ejectePotion(defenseur.getRefRMI());
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}
