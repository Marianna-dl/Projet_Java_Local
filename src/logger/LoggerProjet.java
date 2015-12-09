package logger;

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
 * Adapte par : Christophe Claustre
 */
public class LoggerProjet implements Serializable {
	
	/*
	 * Les constantes
	 */
	private static final long serialVersionUID = 162766580131625298L;
	
	private static final String DOSSIER_CLIENT = "./logs_client/";
	private static final String DOSSIER_SERVEUR = "./logs_server/";
	private static final String PREFIXE_CLIENT = "logs_client_";
	private static final String PREFIXE_SERVEUR = "logs_server_";
	
	private static final String EXT = ".html";
	private static final DateFormat SDF = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
	
	/**
	 * Liste des log.
	 */
	private ArrayList<String> logs;
	
	/**
	 * Gestionnaire de log.
	 */
	private Logger logger = null;
	
	public LoggerProjet(boolean isAnClient) throws IOException {
		this(isAnClient, "");
	}
	
	public LoggerProjet(boolean isAnClient, String suffixeFichier) throws IOException {
		this(calcFilename(suffixeFichier, isAnClient), isAnClient);
	}
	
	private LoggerProjet(String loggerStr, boolean isAnClient) throws IOException {
		// init des attributs
		logs = new ArrayList<String>();
		logger = Logger.getLogger(loggerStr);
		
		String dossier = (isAnClient)?DOSSIER_CLIENT:DOSSIER_SERVEUR;
		
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
		fileHTML.setFormatter(new HtmlFormatter());
		logger.addHandler(fileHTML);

		// create an Console formatter
		console.setFormatter(new ConsoleFormatter());
		logger.addHandler(console);
	}
	
	private static String calcFilename(String suffixeFichier, boolean isAnClient) {
		String prefixe = (isAnClient)?PREFIXE_CLIENT:PREFIXE_SERVEUR;
		String suffixe = "";
		if (suffixeFichier != null && suffixeFichier != "") suffixe = "_"+suffixeFichier;
		String loggerStr = prefixe + SDF.format(new Date()) + suffixe + EXT;
		
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
	
	private void severe(String msg) {
		logger.severe(msg);
		logs.add(msg);
	}
	
	//warning
	public void warning(String prefixe, String msg) {
		this.warning(format(prefixe,msg));
	}
	
	private void warning(String msg) {
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
	
	private void fine(String msg) {
		logger.fine(msg);
		logs.add(msg);
	}
	
	//finer
	public void finer(String prefixe, String msg) {
		this.finer(format(prefixe,msg));
	}
	
	private void finer(String msg) {
		logger.finer(msg);
		logs.add(msg);
	}
	
	//finest
	public void finest(String prefixe, String msg) {
		this.finest(format(prefixe,msg));
	}
	
	private void finest(String msg) {
		logger.finest(msg);
		logs.add(msg);
	}
	
	//config
	public void config(String prefixe, String msg) {
		this.config(format(prefixe,msg));
	}
	
	private void config(String msg) {
		logger.config(msg);
		logs.add(msg);
	}
	
	//log
	public void log(Level level, String prefixe, String msg) {
		this.log(level, format(prefixe,msg));
	}
	
	private void log(Level level, String msg) {
		logger.log(level, msg);
		logs.add(msg);
	}
	
	
}
