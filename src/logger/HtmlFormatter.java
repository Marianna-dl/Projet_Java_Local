package logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Cette classe va faire la configuration du systeme de gestion du journal des evenements
 * Source : http://www.vogella.com/tutorials/Logging/article.html
 * Adapte par : Christophe Claustre
 */

// this custom formatter formats parts of a log record to a single line
public class HtmlFormatter extends Formatter {

	// this method is called for every log records
	public String format(LogRecord rec) {
		String buf = "<tr>\n";

		// colorize levels
		if (rec.getLevel().intValue() == Level.SEVERE.intValue()) {
			buf += "\t<tr style=\"color:red\">";
		} else if (rec.getLevel().intValue() == Level.WARNING.intValue()) {
			buf += "\t<tr style=\"color:yellow\">";
		} else if (rec.getLevel().intValue() == Level.INFO.intValue()) {
			buf += "\t<tr style=\"color:green\">";
		} else if (rec.getLevel().intValue() <= Level.FINE.intValue()) {
			buf += "\t<tr>";
		}

		buf += "\n\t<td>"+rec.getLevel()+"</td>\n"
				+ "\t<td>"+calcDate(rec.getMillis())+"</td>\n"
				+ "\t<td>"+formatMessage(rec)+"</td>\n"
				+ "</tr>\n";

		return buf.toString();
	}
	
	@Override
	public synchronized String formatMessage(LogRecord record) {
		return super.formatMessage(record).replaceAll("\n", "<br/>\n\t");
	}

	private static String calcDate(long millisecs) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		Date resultdate = new Date(millisecs);
		return sdf.format(resultdate);
	}

	// this method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h) {
		return "<!DOCTYPE html>\n<head>\n"
		+ "<style type=\"text/css\">\n"
        + "table { width: 100% }\n"
        + "th { font:bold 10pt Tahoma; }\n"
        + "td { font:normal 10pt Tahoma; }\n"
        + "h1 { font:normal 11pt Tahoma; }\n"
        + "</style>\n"
        + "</head>\n"
        + "<body>\n"
        + "<h1>" + (new Date()) + "</h1>\n"
        + "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
        + "<tr align=\"left\">\n"
        + "\t<th style=\"width:10%\">Loglevel</th>\n"
        + "\t<th style=\"width:15%\">Time</th>\n"
        + "\t<th style=\"width:75%\">Log Message</th>\n"
        + "</tr>\n";
	}

	// this method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h) {
		return "</table>\n</body>\n</html>";
	}

}
