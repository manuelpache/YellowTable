package Yellowstone.YellowTable.logic.components;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * 
 * Klasse welche die Relation zwischen einer Person und einem Timeslot fuer
 * Teachingunits darstellt. Wurde aufgrund der Datenbank erstellt.
 * 
 * @author erik
 *
 *
 */

@Entity
public class PersonTime {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(PersonTime.class);

	@Id
	@GeneratedValue(strategy = AUTO)
	private int id;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Timeslot timeslot;

	@ManyToOne
	private Person person;

	/**
	 * zero argument-konstructor
	 */
	public PersonTime() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres PersonTime-Objekt wurde erstellt.");
		}
	}

	/**
	 * Konstruktor einer PersonTime
	 * 
	 * @param pTimeslot
	 *            Timeslot
	 * @param pPerson
	 *            Person
	 */
	public PersonTime(final Timeslot pTimeslot, final Person pPerson) {
		setTimeslot(pTimeslot);
		setPerson(pPerson);
		if (logger.isInfoEnabled()) {
			logger.info("Ein neues PersonTime-Objekt wurde erstellt: "
					+ pTimeslot.toString() + ", " + pPerson.getShortName());
		}
	}

	/**
	 * Gibt die Person zurueck
	 * 
	 * @return person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Gibt den Timeslot zurueck
	 * 
	 * @return timeslot
	 */
	public Timeslot getTimeslot() {
		return timeslot;
	}

	/**
	 * Setzt die Person der PersonTime neu.
	 * 
	 * @param pPerson
	 */
	public void setPerson(final Person pPerson) {
		if (pPerson != null) {
			person = pPerson;
			if (logger.isInfoEnabled()) {
				logger.info("Person der PersonTime wurde auf die Person "
						+ pPerson.getShortName() + " gesetzt.");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("pPerson wurde null gesetzt");
			}
			throw new IllegalArgumentException(
					"Die uebergebene Person ist ungueltig.");
		}
	}

	/**
	 * Setzt den Timeslot dieser PersonTime
	 * 
	 * @param pTimeslot
	 */
	public void setTimeslot(final Timeslot pTimeslot) {
		if (pTimeslot != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Timeslot der PersonTime wurde gesetzt: "
						+ pTimeslot.toString());
			}
			timeslot = pTimeslot;
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("pTimeslot wurde null gesetzt");
			}
			throw new IllegalArgumentException(
					"Es muss ein Zeitraum gewaehlt werden.");
		}
	}

	/**
	 * Gibt die Id dieser PersonTime zurueck.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Ausgabe dieser PersonTime
	 */
	public String toString() {
		return "" + person.getShortName() + " " + timeslot.getStarttime();
	}
}
