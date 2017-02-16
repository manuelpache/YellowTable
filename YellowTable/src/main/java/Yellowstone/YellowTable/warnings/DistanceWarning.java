package Yellowstone.YellowTable.warnings;

import javax.persistence.Entity;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.Person;
import Yellowstone.YellowTable.logic.components.WEEKDAYS;
import Yellowstone.YellowTable.logic.components.Class;

/**
 * Repraesentiert eine Warnung wegen zu wenig Zeit beim Wechsel zwischen
 * Standorten.
 * 
 * @author apag
 *
 */
@Entity
public class DistanceWarning extends Warning {

	/**
	 * Der Schluessel der betroffenen Ressource
	 */
	private String what;

	/**
	 * Die Art der Ressource
	 */
	private String type;

	/**
	 * Der Tag fuer den diese Warnung gilt.
	 */
	private String day;

	/**
	 * Konstruktor für die DistanceWarning
	 * 
	 * @param pDescription
	 * @param pWeekday
	 * @param pPerson
	 */
	public DistanceWarning(final String pDescription, final WEEKDAYS pWeekday,
			final Person pPerson) {
		this("Person (" + pPerson.getShortName() + ") am "
				+ pWeekday.getTitle() + " " + pDescription);
		day = pWeekday.getTitle();
		what = pPerson.getShortName();
		type = Person.class.getSimpleName();
	}

	/**
	 * Erzeugt Distanzwarnungen fuer Klassen
	 * 
	 * @param pDescription
	 * @param pWeekday
	 * @param pClass
	 */
	public DistanceWarning(final String pDescription, final WEEKDAYS pWeekday,
			final Class pClass) {
		this("Klasse (" + pClass.getName() + ") am " + pWeekday.getTitle()
				+ " " + pDescription);
		day = pWeekday.getTitle();
		what = pClass.getName();
		type = Class.class.getSimpleName();
	}

	private DistanceWarning(final String pDescription) {
		super(Warning.PRIORITY_MIDDLE, "Wegzeiten: " + pDescription);
	}

	/**
	 * leerer Konstruktor fuer Datenbank.
	 */
	public DistanceWarning() {

	}

	/**
	 * Gibt das Attribut what zurück.
	 * 
	 * @return what
	 */
	public String getWhat() {
		return what;
	}

	/**
	 * Gibt den Typ zurück.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gibt den Tag zurück.
	 * 
	 * @return day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * Setzt den Tag neu.
	 * 
	 * @param day
	 */
	public void setDay(final String pDay) {
		day = pDay;
	}

	/**
	 * Setzt das Attribut what neu.
	 * 
	 * @param what
	 */
	public void setWhat(final String pWhat) {
		if (pWhat != null) {
			what = pWhat;
		}
	}

	/**
	 * Setzt den Typ neu.
	 * 
	 * @param type
	 */
	public void setType(final String pType) {
		if (pType != null) {
			type = pType;
		}
	}

	/**
	 * Warnung ist aktuell, wenn die Person bzw. Klasse noch existiert und an
	 * dem Wochentag weiterhin nicht genuegend Zeit eingeplant ist.
	 */
	@Override
	public boolean check() {
		try {
			if (type.equals(Person.class.getSimpleName())) {
				Person p = DBHandler.getTeacherByShort(what);
				if (p == null) {
					p = DBHandler.getEducEmployeeByShort(what);
				}
				if (p != null) {
					WEEKDAYS wday = WEEKDAYS.getByTitle(day);
					if (wday != null) {
						return DBHandler
								.checkTravelTimePerPersonPerDay(p, wday);
					}
				}
				return false;
			}
			if (type.equals(Class.class.getSimpleName())) {
				Class c = DBHandler.getClassByName(what);
				if (c != null) {
					WEEKDAYS wday = WEEKDAYS.getByTitle(day);
					if (wday != null) {
						return DBHandler.checkTravelTimePerClassPerDay(c, wday);
					}
				}
				return false;
			}
			throw new IllegalArgumentException("Unbekannte Instanz: " + type);
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}
}
