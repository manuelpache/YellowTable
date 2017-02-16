package Yellowstone.YellowTable.logic.components;

import static javax.persistence.GenerationType.AUTO;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Interface fuer alle Planungseinheiten.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author phil, Erik , phil (Persistenz)
 *
 */
@MappedSuperclass
public abstract class Unit implements IUnit, Comparable<Unit> {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	@Transient
	final static Logger logger = Logger.getLogger(Unit.class);

	/**
	 * Die automatisch generierte ID wird als Primaerschluessel fuer die
	 * Datenbank verwendet.
	 */
	@Id
	@GeneratedValue(strategy = AUTO)
	private int id;

	/**
	 * Dies ist der Zeitslot, in dem diese Aktivitaet stattfindet.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	protected Timeslot timeslot;

	/**
	 * die Raeume, in denen diese Aktivitaet stattfindet.
	 */
	@OneToMany
	private Set<Room> rooms = new HashSet<Room>();

	/**
	 * Gibt die ID fuer die Klasse zurueck, die entscheidend fuer die Persistenz
	 * ist.
	 * 
	 * @return die ID der Klasse.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt die ID fuer die Persistenz neu.
	 * 
	 * @param pID
	 *            die neue ID.
	 * @throws IllegalArgumentException
	 *             falls fuer die ID ein invalider Wert uebergeben wird.
	 */
	public void setId(final int pID) {
		if (pID > 0) {
			id = pID;
			if (logger.isInfoEnabled()) {
				logger.info("Die ID der Unit wurde gesetzt: " + pID);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler beim setzen der ID: " + pID);
			}
			throw new IllegalArgumentException("Die Id darf nicht negativ sein");
		}

	}

	@Override
	public String getStarttime() {
		return timeslot.getStarttime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDuration() {
		return timeslot.getDuration();
	}

	/**
	 * Gibt den Zeitslot dieser Unit zurueck.
	 * 
	 * @return den zugeordneten Zeitslot
	 */
	@ManyToOne
	public Timeslot getTimeslot() {
		return timeslot;
	}

	/**
	 * Gibt ein Set von Raeumen zurueck.
	 * 
	 * @return die zugeordneten Raeume
	 */
	@OneToMany
	public Set<Room> getRooms() {
		return rooms;
	}

	/**
	 * Setzt ein Set von Raeumen.
	 * 
	 * @param pRooms
	 * @throws IllegalArgumentException
	 *             falls ein fehlerhafter Parameter uebergeben wurde.
	 */
	public void setRooms(final Set<Room> pRooms) {
		if (pRooms != null) {
			rooms = pRooms;
			if (logger.isInfoEnabled()) {
				logger.info("Die Raeume der Unit wurden gesetzt: "
						+ pRooms.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter pRooms (null)");
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Raum fuer die Einheit an.");
		}
	}

	/**
	 * Setzt den Zeitslot dieser Aktivitaet neu. Darf nicht leer / null sein.
	 * 
	 * @param pTimeslot
	 *            : der Zeitslot
	 * 
	 */
	public void setTimeslot(Timeslot pTimeslot) {
		if (pTimeslot != null) {
			timeslot = pTimeslot;
			if (logger.isInfoEnabled()) {
				logger.info("Der Timeslot der Unit wurde gesetzt: "
						+ pTimeslot.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter pTimeslot (null)");
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Zeitraum fuer die Einheit an.");
		}
	}

	/**
	 * Vergleicht diese Unit mit einer uebergebenen Unit anhand ihres Timeslots.
	 * 
	 * @param u
	 *            Unit
	 * @return 0 wenn identisch
	 * 
	 */
	public int compareTo(final Unit u) {
		if (u == null) {
			throw new IllegalArgumentException("Unit darf nicht null sein");
		}
		int thisMinutes = (Integer.parseInt(this.getTimeslot()
				.getTimeBeginHour()) * 60)
				+ Integer.parseInt(this.getTimeslot().getTimeBeginMinute());
		int thatMinutes = (Integer.parseInt(u.getTimeslot().getTimeBeginHour()) * 60)
				+ Integer.parseInt(u.getTimeslot().getTimeBeginMinute());
		if (thatMinutes > thisMinutes) {
			return -1;
		}
		if (thisMinutes > thatMinutes) {
			return 1;
		}
		if (thisMinutes == thatMinutes) {
			return 0;
		}
		return this.compareTo(u);
	}
}
