package serveur;

import java.rmi.RemoteException;

import client.controle.IConsole;
import utilitaires.Constantes;

/**
 * Classe permettant de lancer l'execution de la strategie d'un personnage
 * dans un thread separe, pour pouvoir limiter son temps d'execution.
 */
public class ThreadStrategie extends Thread {
	
	/**
	 * Console correspondant au personnage.
	 */
	private IConsole console;
	
	/**
	 * Cree un thread pour executer la strategie d'un personnage.
	 * @param console console correspondant au personnage
	 */
	public ThreadStrategie(IConsole console) { 
		this.console = console; 
		
		start(); 
	}
	
	@Override
	public void run() {
		try {
			// lancement de la strategie
			console.executeStrategie(); 

		} catch (Exception e) {
			// les exceptions sont inhibees ici
			// en cas d'erreur, le client est deconnecte
			try {
				Arene arene = (Arene) console.getArene();
				
				arene.getLogger().warning(Constantes.nomClasse(this), 
						"Erreur lors de l'execution de la strategie de " +
								arene.nomRaccourciClient(console.getRefRMI()) +
								" \n" + e.toString());
				
				arene.deconnecte(console.getRefRMI(), 
						e.toString(), 
						"erreur strategique");
				e.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		} 
	}
}
