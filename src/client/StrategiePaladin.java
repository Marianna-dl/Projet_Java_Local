package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import serveur.element.Paladin;
import serveur.element.Personnage;

public class StrategiePaladin extends StrategiePersonnage{
	protected Console console;
	public StrategiePaladin(String ipArene, int port, String ipConsole, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, long nbTours, Point position, LoggerProjet logger) {
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Paladin(caracts), nbTours, position, logger);
			logger.info("lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	public void strategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// TODO Auto-generated method stub
		super.strategie(voisins);
	}
}
