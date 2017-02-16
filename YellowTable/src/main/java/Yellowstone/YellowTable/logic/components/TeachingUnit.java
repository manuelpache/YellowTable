/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Realisiert in unserer Logik eine Unterrichtseinheit. Zu einer
 * Unterrichtseinheit gehoert eine Menge von Personen, sowie eine Menge von
 * Faechern und Klassen.
 * 
 * Im Konstruktor werden die Personen und das Fach der Unterrichts- einheit
 * gesetzt.
 * 
 * 
 * @author prohleder
 *
 * @author Erik K.
 */
@Entity
public class TeachingUnit extends SchoolUnit {

	// Attribute..

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(TeachingUnit.class);

	/**
	 * Eine Map von Personen(Kuerzel und Timeslots fuer die TeachingUnit)
	 */
	@OneToMany(cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
	private Set<PersonTime> persons = new HashSet<PersonTime>();

	/**
	 * Ein Set von Faechern fuer der TeachingUnit
	 */
	@OneToMany
	private Set<Subject> subjects = new HashSet<Subject>();

	/**
	 * Reisenotwendigkeit nach dieser TeachingUnit
	 */
	private boolean travelNeed;

	// Konstruktoren
	public TeachingUnit(final Timeslot pTimeslot) {
		setTimeslot(pTimeslot);
		if (logger.isInfoEnabled()) {
			logger.info("TeachingUnit-Objekt wurde erstellt.: "
					+ pTimeslot.toString());
		}
	}

	public TeachingUnit() {
		// Logger
		if (logger.isInfoEnabled()) {
			logger.info("Leeres TeachingUnit-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Methode, die die Personen einer Unterrichtseinheit zurueckgibt.
	 * 
	 * @return die Personen einer Unterrichtseinheit.
	 */
	public Set<PersonTime> getPersons() {
		return persons;
	}

	/**
	 * Methode, die die Unterrichtsinhalte einer Unterrichtseinheit zurueckgibt.
	 * 
	 * @return die Unterrichtsinhalte einer Unterrichtseinheit.
	 */
	public Set<Subject> getSubjects() {
		return subjects;
	}

	/**
	 * Die Notwendigkeit, dass nach dieser TeachingUnit der Standort gewechselt
	 * werden muss.
	 */
	public boolean getTravelNeed() {
		return travelNeed;
	}

	// Setter-Methoden

	/**
	 * Setzt die Reisenotwendigkeit auf den uebergebenen Wert.
	 * 
	 * @param pTravel
	 */
	public void setTravelNeed(final boolean pTravel) {
		travelNeed = pTravel;
	}

	/**
	 * Setzt die Unterrichtsinhalte einer Unterrrichtseinheit neu.
	 * 
	 * @param pSubjects
	 *            die neuen Unterrichtsinhalte.
	 */
	public void setSubjects(final Set<Subject> pSubjects) {
		if (pSubjects != null) {
			subjects = pSubjects;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die subjects der TeachingUnit wurde geaendert: "
						+ pSubjects.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in Parameter pSubjects (null) ");
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen gueltigen Unterrichtsinhalt an.");
		}
	}

	/**
	 * Setzt die Teilnehmer einer Unterrichtseinheit neu.
	 * 
	 * @param pPersons
	 *            die neuen Teilnehmer einer Unterrichtseinheit.
	 */
	public void setPersonTime(final Set<PersonTime> pPersons) {
		if (pPersons != null) {
			persons = pPersons;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Das Set persons der TeachingUnit wurde geaendert: "
						+ pPersons.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in Parameter pPerson (null) ");
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie Lehrer oder Paedagogen fuer die Unterrichtseinheit an.");
		}
	}

	/**
	 * ueberprueft, ob diese TeachingUnit das uebergebene Subject beinhaltet.
	 * 
	 * @param pSubject
	 *            zu ueberpruefendes Fach
	 * @return boolean-Wert, ob pSubject enthalten
	 * 
	 * @throws IllegalArgumentException
	 *             bei {@code null} Parameter.
	 */
	public boolean containsSubject(final Subject pSubject) {
		if (pSubject != null) {
			return (subjects.contains(pSubject));
		} else {
			throw new IllegalArgumentException("Bitte geben sie ein Fach an.");

		}
	}

	/**
	 * ueberprueft, ob diese TeachingUnit eine Person mit dem uebergebenen
	 * Kuerzel beinhaltet.
	 * 
	 * @param pPerson
	 * @return boolean-Wert, ob pPerson enthalten
	 * 
	 * @throws IllegalArgumentException
	 *             bei leerem Parameter.
	 */
	public boolean containsPerson(final String pShort) {
		if (pShort == null || pShort.trim().length() <= 0) {
			throw new IllegalArgumentException(
					"Bitte geben sie ein gueltiges Kuerzel an.");
		} else {
			for (PersonTime pt : persons) {
				if (pt.getPerson().getShortName().equals(pShort)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Vergleicht zwei TeachingUnits miteinander.
	 * 
	 * @param pUnit
	 * @return
	 */
	public boolean equals(final Object pObject) {
		if (pObject != null && pObject instanceof TeachingUnit) {
			if (((TeachingUnit) pObject).getSubjects().containsAll(
					getSubjects())) {
				if (((TeachingUnit) pObject).getTimeslot()
						.equals(getTimeslot())) {
					if (((TeachingUnit) pObject).getClasses().containsAll(
							getClasses())) {
						if (((TeachingUnit) pObject).getRooms().containsAll(
								getRooms())) {
							if (((TeachingUnit) pObject).getPersons().equals(
									getPersons())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String endString = "";
		String subjectsString = "";
		String roomsString = "";
		String personsString = "";
		ArrayList<Subject> subjects1 = new ArrayList<Subject>();
		ArrayList<Room> rooms1 = new ArrayList<Room>();
		ArrayList<PersonTime> persons1 = new ArrayList<PersonTime>();
		subjects1.addAll(subjects);
		rooms1.addAll(this.getRooms());
		persons1.addAll(persons);
		for (Subject s : subjects1) {
			subjectsString += s.getTitle();
			subjectsString += ",";
		}
		for (Room r : rooms1) {
			roomsString += r.getName();
			roomsString += ",";
		}

		for (PersonTime s : persons1) {
			s.getTimeslot().setStarttime();
			s.getTimeslot().getEndTime();
			if (persons.size() > 1) {
				personsString += s.getPerson().getShortName();
				personsString += " ";
				personsString += "("
						+ Timeslot.addStringHour((s.getTimeslot()
								.getStartHour()));
				personsString += ":"
						+ Timeslot.addStringMinute((s.getTimeslot()
								.getStartMinute())) + "-";
				personsString += Timeslot.addStringHour((s.getTimeslot()
						.getEndHour_int()));
				personsString += ":"
						+ Timeslot.addStringMinute((s.getTimeslot()
								.getEndMinute_int())) + ")";
				personsString += "\n";
			} else {
				personsString += s.getPerson().getShortName();
			}

		}

		if (subjectsString.endsWith(",")) {
			subjectsString = subjectsString.substring(0,
					subjectsString.length() - 1);
		}
		if (roomsString.endsWith(",")) {
			roomsString = roomsString.substring(0, roomsString.length() - 1);
		}
		if (personsString.endsWith(",")) {
			personsString = personsString.substring(0,
					personsString.length() - 1);
		}
		if (subjectsString != ",") {
			endString += subjectsString + "\n";
		}
		if (personsString != ",") {
			endString += personsString + "\n";
		}
		if (roomsString != ",") {
			endString += roomsString + "\n";
		}
		if (this.getClasses().size() > 1) {
			endString += ("(B)");
		}
		if (this.getTravelNeed()) {
			endString += " (P)";
		}
		return endString;
	}

	/**
	 * Gibt eine neue TeachingUnit zurueck, die inhaltlich mit dieser komplett
	 * uebereinstimmt.
	 * 
	 * @return eine Kopie dieser TeachingUnit
	 */
	@Override
	public TeachingUnit clone() {
		TeachingUnit copy = new TeachingUnit(getTimeslot());
		copy.setClasses(getClasses());
		copy.setId(getId());
		copy.setPersonTime(getPersons());
		copy.setRooms(getRooms());
		copy.setSubjects(getSubjects());
		if (logger.isInfoEnabled()) {
			logger.info("Es wurde eine Kopie einer Teachingunit erstellt "
					+ copy.toString());
		}
		return copy;
	}
}