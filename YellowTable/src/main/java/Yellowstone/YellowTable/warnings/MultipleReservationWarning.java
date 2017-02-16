package Yellowstone.YellowTable.warnings;

import javax.persistence.*;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.logic.components.EducEmployee;
import Yellowstone.YellowTable.logic.components.Person;
import Yellowstone.YellowTable.logic.components.Room;
import Yellowstone.YellowTable.logic.components.Teacher;
import Yellowstone.YellowTable.logic.components.Timeslot;
import Yellowstone.YellowTable.logic.components.Class;

/**
 * Repraesentiert eine Warnung wegen Mehrfachbelegung einer Ressource.
 * Beruecksichtigte Ressourcen:
 * 
 * <li>Personen</li>
 * 
 * <li>Raeume</li>
 * 
 * <li>Klassen</li>
 * <p>
 * Solche Warnungen haben grundsaetzlich eine hohe Prioritaet.
 * </p>
 * 
 * @author apag
 *
 *
 */
@Entity
public class MultipleReservationWarning extends Warning {

	/**
	 * Schluessel des Objekts, das mehrfach belegt wird.
	 */
	private String what;

	/**
	 * Art des Objekts
	 */
	private String type;

	/**
	 * Zeitrahmen in dem diese Ueberlappungen vorkommen.
	 */
	private Timeslot slot;

	/**
	 * Interner Konstruktor, der von den anderen Konstruktoren dieser Klasse
	 * aufgerufen wird. Hier werden u.a. die Prioritaet als hoch (
	 * {@link Warning#PRIORITY_HIGH}) festgelegt und der {@link Timeslot}
	 * gespeichert.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pSlot
	 *            der Zeitraum, in dem diese Warnung relevant ist.
	 */
	private MultipleReservationWarning(final String pDescription,
			final Timeslot pSlot) {
		super(PRIORITY_HIGH, "Doppelbelegung: " + pDescription);
		slot = pSlot;
	}

	/**
	 * leerer Konstruktor für die DB
	 */
	public MultipleReservationWarning() {
	}

	/**
	 * Konstruktor fuer Mehrfachbelegungen, die Personen ({@link Teacher} oder
	 * {@link EducEmployee}) betreffen.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pSlot
	 *            der Zeitraum, in dem diese Warnung relevant ist.
	 * @param pPerson
	 *            die Person, die mehrfach verwendet wird.
	 */
	public MultipleReservationWarning(final String pDescription,
			final Timeslot pSlot, final Person pPerson) {
		this("Person (" + pPerson.getShortName() + ") " + pDescription, pSlot);
		what = pPerson.getShortName();
		type = Person.class.getSimpleName();
	}

	/**
	 * Konstruktor fuer Mehrfachbelegungen, die Raeume ({@link Room}) betreffen.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pSlot
	 *            der Zeitraum, in dem diese Warnung relevant ist.
	 * @param pRoom
	 *            der Raum, die mehrfach verwendet wird.
	 */
	public MultipleReservationWarning(final String pDescription,
			final Timeslot pSlot, final Room pRoom) {
		this("Raum (" + pRoom.getName() + ") " + pDescription, pSlot);
		what = pRoom.getName();
		type = Room.class.getSimpleName();
	}

	/**
	 * Konstruktor fuer Mehrfachbelegungen, die Schulklassen ({@link Class})
	 * betreffen.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pSlot
	 *            der Zeitraum, in dem diese Warnung relevant ist.
	 * @param pClass
	 *            die Klasse, die mehrfach verwendet wird.
	 */
	public MultipleReservationWarning(final String pDescription,
			final Timeslot pSlot, final Class pClass) {
		this("Klasse (" + pClass.getName() + ") " + pDescription, pSlot);
		what = pClass.getName();
		type = Class.class.getSimpleName();
	}

	/**
	 * Gibt den Schlüssel des Objekts zurück.
	 * 
	 * @return Schlüssel
	 */
	public String getWhat() {
		return what;
	}

	/**
	 * Gibt den Timeslot dieser Warnung zurück.
	 * 
	 * @return
	 */
	public Timeslot getTimeslot() {
		return slot;
	}

	/**
	 * Gibt den Typ dieser Warnung zurück.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Diese Warnung ist aktuell, wenn das Objekt zu dem gefragten Zeitraum
	 * weiterhin mehrfach belegt ist.
	 */
	@Override
	public boolean check() {
		try {
			if (type.equals(Person.class.getSimpleName())) {
				Person pers = DBHandler.getEducEmployeeByShort(what);
				if (pers == null) {
					pers = DBHandler.getTeacherByShort(what);
				}
				if (pers != null) {
					return DBHandler.checkConflictPerson(pers, slot);
				}
				return false;
			}
			if (type.equals(Room.class.getSimpleName())) {
				Room room = DBHandler.getRoomByTitle(what);
				if (room != null) {
					return DBHandler.checkConflictRoom(room, slot);
				}
				return false;
			}
			if (type.equals(Class.class.getSimpleName())) {
				Class cla = DBHandler.getClassByName(what);
				if (cla != null) {
					return DBHandler.checkConflictClass(cla, slot);
				}
				return false;
			}

			throw new IllegalArgumentException("Unbekannte Instanz: " + type);
		} catch (final Exception db) {
			throw new IllegalArgumentException("Datenbank-Problem: " + db);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			if (obj instanceof MultipleReservationWarning) {
				MultipleReservationWarning w = (MultipleReservationWarning) obj;
				return what.equals(w.what) && type.equals(w.type)
						&& slot.equals(w.slot);
			}
		}
		return false;
	}
}
