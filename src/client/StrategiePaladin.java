package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.element.Caracteristique;

public class StrategiePaladin extends StrategiePersonnage{

	public StrategiePaladin(String ipArene, int port, String ipConsole, String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, long nbTours, Point position, LoggerProjet logger) {
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void strategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// TODO Auto-generated method stub
		super.strategie(voisins);
	}
}
