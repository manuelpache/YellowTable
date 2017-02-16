/**
 * Die MeetingUnit gehoert zu dem Paket der Komponenten.
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Pause darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author Tom Lewandowski , Erik (Logger), phil (Persistenz)
 *
 */
@Entity
public class MeetingUnit extends SchoolUnit implements IMeetingUnit {

	/**
	 * Der Logger dieser Klasse.
	 */
	@Transient
	final static Logger logger = Logger.getLogger(MeetingUnit.class);

	// Attribute

	/**
	 * Der Titel des Meetings.
	 */
	private String title;

	/**
	 * Die Personen, die am Meeting teilnehmen.
	 */
	@OneToMany
	private Set<Person> persons = new HashSet<Person>();

	/**
	 * Der Jahrgang, falls es sich um ein Jahrgangstreffen handelt.
	 */
	private int years;

	/**
	 * Die Raume eines Meetings.
	 */
	@OneToMany
	private Set<Room> rooms = new HashSet<Room>();

	// Konstruktoren.

	/**
	 * Der Konstruktor dieser Klasse
	 * 
	 * @param pTitle
	 * @param pYear
	 * @param pTime
	 */
	public MeetingUnit(final String pTitle, final int pYear,
			final Timeslot pTime) {
		setTitle(pTitle);
		setYear(pYear);
		setTimeslot(pTime);

		if (logger.isInfoEnabled()) {
			logger.info("Folgendes Meeting wurde erstellt: " + pTitle + ", "
					+ pYear + pTime.toString());
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI
	public MeetingUnit() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres MeetingUnit-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	@OneToMany
	@JoinColumn(name = "shortName")
	public Set<Person> getMembers() {
		return persons;
	}

	/**
	 * Gibt zurueck fuer welchen Jahrgang das Jahrgangstreffen stattfinden soll.
	 * {@inheritDoc}
	 */
	@Override
	public int getYear() {
		return years;
	}

	// Setter-Methoden

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTitle(final String pTitle) throws IllegalArgumentException {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;

			if (logger.isInfoEnabled()) {
				logger.info("Der Titel der MeetingUnit wurde geaendert: "
						+ pTitle);
			}

		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}

			throw new IllegalArgumentException(
					"Der eingegebene Titel ist ungueltig bzw. darf nicht leer sein");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMembers(final Set<Person> pPersons) {
		if (pPersons != null) {
			persons = pPersons;

			if (logger.isInfoEnabled()) {
				logger.info("Die Member in der MeetingUnit wurden gesetzt: "
						+ pPersons.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pPersons);
			}

			throw new IllegalArgumentException(
					"Es sollten Personen an einem Meeting teilnehmen.");
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void setYear(final int pYear) {
		if (pYear < 0 || pYear > IConfig.MAX_YEAR) {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pYear);
			}
			throw new IllegalArgumentException(
					"Der Jahrgang darf nicht kleiner gleich 0 oder groesser als "
							+ IConfig.MAX_YEAR + " sein.");
		}
		years = pYear;
		if (logger.isInfoEnabled()) {
			logger.info("Das Attribut year in MeetingUnit wurde geaendert: "
					+ pYear);
		}
	}

}