/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 * Definition eines Lehrers.
 * 
 * Ein Lehrer erbt von einer Person.
 * 
 * Dabei muss der Konstruktor die Anzahl der Meetings, die ausgeplanten Stunden
 * (zum Beispiel fuer Fortbildungen), sowie die Lehrerwuensche uebergeben
 * bekommen.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author prohleder
 *
 * @author Erik K. , Tom (Logger)
 */
@Entity
public class Teacher extends Person implements ITeacher {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Teacher.class);

	/**
	 * Jahrgangsteam einer Lehrerin
	 */
	private int yearTeam;

	/**
	 * Praeferenzen einer Lehrerin
	 */
	@OneToMany
	private Collection<Timeslot> preferences = new ArrayList<Timeslot>();

	// Konstruktoren

	// Leerer Konstruktor fuer Datenbank und GUI
	public Teacher() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Lehrer-Objekt wurde erstellt.");
		}
	}

	/**
	 * Der Konstruktor der Klasse.
	 * 
	 * @param pShort
	 * @param pFirst
	 * @param pLast
	 */
	public Teacher(final String pShortName, final String pFirstName,
			final String pLastName, final float pMaxWorkload,
			final float pMinusHours, final int pYear) {
		setShortName(pShortName);
		setFirstName(pFirstName);
		setLastName(pLastName);
		setMaxWorkload(pMaxWorkload);
		setMinusHours(pMinusHours);
		setYearTeam(pYear);
		if (logger.isInfoEnabled()) {
			logger.info("Folgender Lehrer wurde erstellt:" + pFirstName + " "
					+ pLastName);
		}
	}

	// Getter-Methoden..

	/**
	 * Gibt den Jahrgang zurueck, fuer den dieser Lehrerin im JahrgangsTeam ist.
	 * 
	 * @return den Jahrgang fuer das Jahrgangsteam.
	 */
	@Override
	public int getYearTeam() {
		return yearTeam;
	}

	/**
	 * Gibt die Zeitwuensche eines Lehrer zurueck.
	 * 
	 * @return die Zeitwuensche einer Lehrerin.
	 * 
	 */
	@OneToMany
	public Collection<Timeslot> getPreferences() {
		return preferences;
	}

	// Setter-Methoden..

	/**
	 * Setzt den Jahrgang fuer das Jahrgangsteam neu.
	 * 
	 * @param pYearTeam
	 *            die neue Anzahl der Meetings.
	 * 
	 * @throws IllegalArgumentException
	 *             wenn eine negative Zahl uebergeben wird.
	 */
	@Override
	public void setYearTeam(final int pYearTeam) {
		if (pYearTeam >= 0 && pYearTeam <= IConfig.MAX_YEAR) {
			yearTeam = pYearTeam;
			if (logger.isInfoEnabled()) {
				logger.info("Das Jahrgangsteam eines Lehrers wurden neu gesetzt: "
						+ pYearTeam);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pYearTeam);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen gueltigen Jahrgang an.");
		}
	}

	/**
	 * Setzt die Wuensche eines Lehrers neu. Es darf auch {@code null}
	 * uebergeben werden.
	 * 
	 * @param pTimeslot
	 *            die neuen Wuensche eines Lehrers.
	 * 
	 */
	@Override
	public void setPreferences(final Collection<Timeslot> pPreferences) {
		if (pPreferences != null) {
			preferences = pPreferences;
			if (logger.isInfoEnabled()) {
				logger.info("Die praeferierten Zeiten eines Lehrers wurden neu gesetzt: "
						+ pPreferences);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pPreferences);
			}
			throw new IllegalArgumentException(
					"Die preaferierten Zeiten eines Lehrers duerfen nicht leer sein.");
		}
	}

	/**
	 * Erzeugt einen neuen Teacher, der inhaltlich komplett mit diesem
	 * uebereinstimmt.
	 * 
	 * @return eine Kopie dieses Lehrers
	 */
	@Override
	public Teacher clone() {

		Teacher copy = new Teacher(getShortName(), getFirstName(),
				getLastName(), getMaxWorkload(), getMinusHours(), getYearTeam());
		copy.setPreferences(getPreferences());
		copy.setSubjects(getSubjects());
		if (logger.isInfoEnabled()) {
			logger.info("Es wurde eine Kopie des Lehrers "
					+ copy.getShortName() + " erstellt.");
		}
		return copy;
	}
}
