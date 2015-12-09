package logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Cette classe va faire la configuration du systeme de gestion du journal des 
 * evenements.
 * Source : http://www.vogella.com/tutorials/Logging/article.html
 * Adapte par : Christophe Claustre
 */

// this custom formatter formats parts of a log record to a single line
public class ConsoleFormatter extends Formatter {

	// this method is called for every log records
	public String format(LogRecord rec) {
		return calcDate(rec.getMillis())+" - ["+rec.getLevel()+"] == "+formatMessage(rec)+"\n";
	}
	
	@Override
	public synchronized String formatMessage(LogRecord record) {
		return super.formatMessage(record).replaceAll("\n", "\n\t");
	}

	private static String calcDate(long millisecs) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date resultdate = new Date(millisecs);
		return sdf.format(resultdate);
	}

	// this method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h) {
		return (new Date()) +"\n";
	}

	// this method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h) {
		return "Fin du serveur\n\n";
	}
}
