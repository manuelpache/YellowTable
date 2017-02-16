package Yellowstone.YellowTable.logic.components;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

/**
 * Stellt eine Zuordnung zwischen einem Fach, seiner Wochenstundenzahl und der
 * tatsaechlichen Auslastung dar.
 * 
 * Diese Klasse wird zur einfacheren Darstellung der individuellen Zeiten von
 * Schulklassen verwendet (ich haette ja gern Tripel genommen...)
 * 
 * @author apag
 *
 */
public class SubIntendedActual implements Comparable<SubIntendedActual> {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(SchoolUnit.class);
	
	/**
	 * Speichert den Subject-Titel
	 */
	private final String subject;

	/**
	 * Die Soll-Stundenzahl
	 */
	private final int intended;

	/**
	 * Die Ist-Stundenzahl
	 */
	private final double actual;

	public SubIntendedActual(final Subject pSubject, final int pIntended,
			final double pActual) {
		if (pSubject == null) {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter pSubject : (null)");
			}
			throw new IllegalArgumentException(
					"Bitte geben sie einen Stundeninhalt an.");
		}
		if (pActual < 0) {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter pActual : " +pActual);
			}
			throw new IllegalArgumentException(
					"Die aktuelle Arbeitslast kann nicht unter 0 liegen.");
		}
		subject = pSubject.getTitle();
		intended = pIntended;
		actual = pActual;
		if (logger.isInfoEnabled()) {
			logger.info("Neues SubIntendedActual wurde erzeugt: ." + pSubject.getTitle()+" "+pIntended+" "+pActual);
		}
	}

	/**
	 * Gibt den Titel des Fachs zurueck.
	 * 
	 * @return das Fach
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Gibt die Soll-Stunden als String zurueck.
	 * 
	 * @return die Soll-Stunden
	 */
	public String getIntended() {
		if (intended < 0) {
			return "-";
		}
		return String.valueOf(intended);
	}

	/**
	 * Gibt die Ist-Stunden als String zurueck;
	 * 
	 * @return die Ist-Stunden
	 */
	public String getActual() {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(actual);
	}

	/**
	 * Vergleicht anhand ihres Bedarfs.
	 */
	@Override
	public int compareTo(final SubIntendedActual o) {
		if (o == null) {
			throw new IllegalArgumentException(
					"Mit null kann nicht verglichen werden.");
		}
		return new Double(o.intended).compareTo(new Double(intended));
	}
}
