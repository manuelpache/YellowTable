/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Schulklasse darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author tomlewandowski
 *
 */
@Entity
public class Class implements IClass {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Class.class);

	/**
	 * Der Name der Klasse zum Beispiel '4a' '1b'
	 */
	@Id
	private String name;

	/**
	 * Der Jahrgang der Klasse.
	 */
	private int years;

	/**
	 * Der Klassenraum.
	 */
	@ManyToOne
	private Room room;

	/*
	 * Speichert den Bezeichner, der den zweiten Teil des eig. Namens darstellt.
	 * Wurde hinzu gefuegt um unnoetige Aenderungen der Schnittstelle zu
	 * vermeiden.
	 */
	@Transient
	private String descr;

	@ElementCollection
	private Map<Subject, Integer> intendedWorkload = new HashMap<Subject, Integer>();

	// Konstruktoren

	/**
	 * Konstruktor der Klasse.
	 * 
	 * @param pName
	 *            der initiale Bezeichner ('a','b','c', ...) der Klasse.
	 * @param pYear
	 *            der initiale Jahrgang der Klasse.
	 * @param pRoom
	 *            der initiale Klassenraum.
	 */
	public Class(final String pName, final int pYear, final Room pRoom) {
		setYear(pYear);
		setName(pName);
		setRoom(pRoom);

		name = years + descr;

		if (logger.isInfoEnabled()) {
			logger.info("Folgende Klasse wurde erstellt: " + pYear + pName
					+ ", " + pRoom);
		}
	}

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Class() {
	}

	// Getter-Methoden

	/**
	 * Gibt den Namen einer Klasse zurueck. Ein Name besteht dabei aus dem
	 * Jahrgang und einem Buchstaben. z.b. 1a,2b,3c
	 * 
	 * @return den Namen einer Klasse.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gibt die Beschreibung der Klasse zurueck. Die Beschreibung ist in dem
	 * Fall die Bezeichnung a,b,c
	 * 
	 * @return die Beschreibung einer Klasse.
	 */
	public String getDescr() {
		return name.substring(name.length() - 1, 2);
	}

	/**
	 * Gibt den Jahrgang einer Klasse zurueck.
	 * 
	 * @return den Jahrgang einer Klasse.
	 */
	public int getYear() {
		return years;
	}

	/**
	 * Gibt den Klassenraum der Klasse zurueck, in dem diese Klasse
	 * hauptsaechlich unterrichtet wird.
	 * 
	 * @return den Klassenraum der Klasse.
	 */
	@ManyToOne
	public Room getRoom() {
		return room;
	}

	/**
	 * Überprüft ob eine individuelle Dauer für das Fach einer Klasse vorhanden
	 * ist.
	 * 
	 * @return -1 wenn keine individuelle Dauer für das Fach vorhanden ist.
	 *         Sonst Individuelle Dauer des Faches.
	 */
	public int getHoursPerSubject(final Subject pSubject) {
		if (pSubject == null) {
			throw new IllegalArgumentException(
					"Das übergebene Fach ist fehlerhaft (null).");
		} else {
			if (intendedWorkload.containsKey(pSubject)) {
				return intendedWorkload.get(pSubject);
			} else {
				return -1;
			}
		}

	}

	/**
	 * @return Das Set mit den Faechern und ihrer Individuellen Dauer.
	 */
	@Override
	public Set<Entry<Subject, Integer>> getAllSubjectNeeds() {
		return intendedWorkload.entrySet();
	}

	// Setter-Methoden

	/**
	 * Setzt den Namen einer Klasse. Dieser besteht aus einem Buchstaben. In
	 * Kobination mit dem Jahrgang ergibt sich so zum Beispiel 3A.
	 * 
	 * @param pName
	 *            Der neue Name einer Klasse.
	 * @throws IllegalArgumentException
	 *             Wenn als Name {@code null} uebergeben wird.
	 */
	public void setName(final String pName) {
		if (pName != null && pName.trim().length() == 1
				&& pName.matches("[a-z]+")) {
			descr = pName;
			if (logger.isInfoEnabled()) {
				logger.info("Der Bezeichner der Klasse wurde geaendert: "
						+ pName);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pName);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Buchstaben fuer die Klasse an.");
		}
	}

	/**
	 * Setzt den Jahrgang einer Klasse.
	 * 
	 * @param pYear
	 *            den neuen Jahrgang einer Klasse.
	 * @throws IllegalArgumentException
	 *             wenn fuer den Jahrgang ein Wert uebergeben wird der kleiner
	 *             als 1 oder groesser als 4 ist.
	 */
	public void setYear(final int pYear) {
		if (pYear > 0 && pYear <= IConfig.MAX_YEAR) {
			years = pYear;
			if (logger.isInfoEnabled()) {
				logger.info("Der Jahrgang der Klasse wurde geaendert: " + pYear);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pYear);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen gueltigen Jahrgang an.");
		}
	}

	/**
	 * Setzt den Klassenraum fuer diese Klassen neu.
	 * 
	 * @param pRoom
	 *            der neue Klassenraum.
	 * @throws IllegalArgumentException
	 *             falls fuer die ID ein invalider Wert uebergeben wird.
	 */
	public void setRoom(final Room pRoom) {
		if (pRoom != null) {
			room = pRoom;
			if (logger.isInfoEnabled()) {
				logger.info("Der Klassenraum wurde geaendert: " + pRoom);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pRoom);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Klassenraum an.");
		}
	}

	/**
	 * Fügt der Map intendedWorkload ein Fach und ihre Individuelle Dauer hinzu.
	 * 
	 * @param pSubject
	 *            ,pHours
	 */
	@Override
	public void setHours(final Subject pSubject, final int pHours) {
		if (pSubject != null && pHours >= 0) {
			intendedWorkload.put(pSubject, pHours);
			if (logger.isInfoEnabled()) {
				logger.info("Dem intendedWorkload wurden hinzugefügt "
						+ pSubject.toString() + ", " + pHours);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in einem der folgenden Parametern: "
						+ pHours + ", " + pSubject);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie ein Fach und die Unterrichtsstunden an.");
		}
	}

	/**
	 * Entfernt ein gegebenes Fach und die individuelle Zeit aus der Map
	 * intendedWorkload
	 * 
	 * @param pSubject
	 */
	public void deleteIntendedWorkloadForSubject(final Subject pSubject) {
		if (pSubject != null) {
			intendedWorkload.remove(pSubject);
			if (logger.isInfoEnabled()) {
				logger.info("Dem intendedWorkload wurde ein Fach entfernt: "
						+ pSubject.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in einem der folgendem Parameter pSubject : "
						+ pSubject);
			}
			throw new IllegalArgumentException("Bitte geben Sie ein Fach an.");
		}
	}

	/**
	 * Gibt den Klassennamen als String zurueck
	 * 
	 * @return den Klassennamen
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Erzeugt eine neue Klasse, die inhaltlich mit dieser uebereinstimmt.
	 * 
	 * @return eine Kopie dieser Klasse
	 */
	@Override
	public Class clone() {
		Class copy = new Class(getDescr(), getYear(), getRoom());
		copy.intendedWorkload = this.intendedWorkload;
		if (logger.isInfoEnabled()) {
			logger.info("Die Klasse wurde gecloned: " + copy.getDescr());
		}
		return copy;
	}

}