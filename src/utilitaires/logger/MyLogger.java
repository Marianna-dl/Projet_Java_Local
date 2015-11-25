package utilitaires.logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Definition d'une classe Logger afin de changer les fonctions de publication
 * La partie configuration a ete trouve dans un tutoriel sur internet.
 * Source: http://www.vogella.com/tutorials/Logging/article.html
 */
public class MyLogger implements Serializable {
	
	/*
	 * Les constantes
	 */
	private static final long serialVersionUID = 162766580131625298L;
	
	private static final String dossier_client = "./logs_client/";
	private static final String dossier_server = "./logs_server/";
	private static final String prefixe_client = "logs_client_";
	private static final String prefixe_server = "logs_server_";
	
	private static final String extension = ".html";
	private static final DateFormat sdf = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
	
	/**
	 * La liste des log
	 */
	private ArrayList<String> logs;
	
	/**
	 * Le gestionnaire de log
	 */
	private Logger logger = null;
	
	public MyLogger(boolean isAnClient) throws IOException {
		this(isAnClient, "");
	}
	
	public MyLogger(boolean isAnClient, String suffixeFichier) throws IOException {
		this(calcFilename(suffixeFichier, isAnClient), isAnClient);
	}
	
	public MyLogger(String loggerStr, boolean isAnClient) throws IOException {
		// init des attributs
		logs = new ArrayList<String>();
		logger = Logger.getLogger(loggerStr);
		
		String dossier = (isAnClient)?dossier_client:dossier_server;
		
		// creation du dossier au cas ou
		new File(dossier).mkdirs();
		
		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers.length > 0) {
			if (handlers[0] instanceof ConsoleHandler) {
				rootLogger.removeHandler(handlers[0]);
			}
		}
		
		// config
		logger.setLevel(Level.FINEST);
		FileHandler fileHTML = new FileHandler(dossier+loggerStr, true);
		ConsoleHandler console = new ConsoleHandler();

		// create an HTML formatter
		fileHTML.setFormatter(new MyHtmlFormatter());
		logger.addHandler(fileHTML);

		// create an Console formatter
		console.setFormatter(new MyConsoleFormatter());
		logger.addHandler(console);
	}
	
	private static String calcFilename(String suffixeFichier, boolean isAnClient) {
		String prefixe = (isAnClient)?prefixe_client:prefixe_server;
		String suffixe = "";
		if (suffixeFichier != null && suffixeFichier != "") suffixe = "_"+suffixeFichier;
		String loggerStr = prefixe + sdf.format(new Date()) + suffixe + extension;
		
		return loggerStr;
	}
	
	private String format(String prefixe, String msg) {
		String s = "";
	    if (prefixe != null) s += prefixe+" : ";
		s += msg;
		
		return s;
	}
	
	// redefinition des publications
	//severe
	public void severe(String prefixe, String msg) {
		this.severe(format(prefixe,msg));
	}
	public void severe(String msg) {
		logger.severe(msg);
		logs.add(msg);
	}
	
	//warning
	public void warning(String prefixe, String msg) {
		this.warning(format(prefixe,msg));
	}
	public void warning(String msg) {
		logger.warning(msg);
		logs.add(msg);
	}
	
	//info
	public void info(String prefixe, String msg) {
		this.info(format(prefixe,msg));
	}
	public void info(String msg) {
		logger.info(msg);
		logs.add(msg);
	}
	
	//fine
	public void fine(String prefixe, String msg) {
		this.fine(format(prefixe,msg));
	}
	public void fine(String msg) {
		logger.fine(msg);
		logs.add(msg);
	}
	
	//finer
	public void finer(String prefixe, String msg) {
		this.finer(format(prefixe,msg));
	}
	public void finer(String msg) {
		logger.finer(msg);
		logs.add(msg);
	}
	
	//finest
	public void finest(String prefixe, String msg) {
		this.finest(format(prefixe,msg));
	}
	public void finest(String msg) {
		logger.finest(msg);
		logs.add(msg);
	}
	
	//config
	public void config(String prefixe, String msg) {
		this.config(format(prefixe,msg));
	}
	public void config(String msg) {
		logger.config(msg);
		logs.add(msg);
	}
	
	//log
	public void log(Level level, String prefixe, String msg) {
		this.log(level, format(prefixe,msg));
	}
	public void log(Level level, String msg) {
		logger.log(level, msg);
		logs.add(msg);
	}
	
	/**
	 * renvoie la ligne de logs numero i
	 * @param i
	 * @return la ligne de logs numero i
	 */
	public String getLogs(int i) {
		return logs.get(i);
	}
}
