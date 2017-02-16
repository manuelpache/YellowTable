/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Definition einer Person. Eine Person kann entweder ein paed. Mitarbeiter oder
 * ein Lehrer sein. Der Konstruktor muss ein Kuerzel, einen Vor- und Nachnamen
 * sowie Soll und Ist-Stunden uebergeben bekommen.
 * 
 * Außerdem besitzt eine Peron eine Liste von Inhalten, die sie unterrichten
 * kann.
 * 
 * Die Klasse darf nicht abstract sein!!! (wegen DB) (PR) & sie besitzt sie
 * keinen Logger. (TL)
 *
 * @author Erik Keshishian, prohleder, Le
 */
@Entity
public class Person implements IPerson {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Person.class);

	// Attribute

	/**
	 * Das Kuerzel einer Person
	 */
	@Id
	private String shortName;

	/**
	 * Der Vorname einer Person
	 */
	private String firstName;

	/**
	 * Der Nachname einer Person
	 */
	private String lastName;

	/**
	 * Die maximale Anzahl der Arbeitsstunden einer Person
	 */
	private float maxWorkload;

	/**
	 * Der current workload einer Person
	 */
	private float curWorkload;

	/**
	 * Anzahl der ausgeplanten Stunden einer Leherin
	 */
	private float minusHours;

	/**
	 * Faecher einer Lehrerin
	 */
	@OneToMany
	private Set<Subject> subjects = new HashSet<Subject>();

	// Konstruktoren

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Person() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Person-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Gibt das Kuerzel einer Person zurueck.
	 * 
	 * @return das Kuerzel einer Person
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Gibt den Vornamen einer Person zurueck.
	 * 
	 * @return den Vornamen einer Person
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gibt den Nachnamen einer Person zurueck.
	 * 
	 * @return den Nachnamen einer Person
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gibt die maximale Anzahl der Arbeitsstunden einer Person zurueck.
	 * 
	 * @return die Soll-Stunden einer Person
	 */
	public float getMaxWorkload() {
		return maxWorkload;
	}

	/**
	 * Gibt die aktuelle Anzahl der Arbeitsstunden einer Person zurueck.
	 * 
	 * @return die Ist-Stunden einer Person
	 */
	public float getCurWorkload() {
		return curWorkload;
	}

	/**
	 * Gibt die Sammlung aller Unterrichtsinhalte einer Person zurueck. Zum
	 * Beispiel Deutsch, Mathe
	 * 
	 * @return die Unterrichtsinhalte einer Person
	 * 
	 */
	public Set<Subject> getSubjects() {
		return subjects;
	}

	/**
	 * Gibt die Anzahl der ausgeplanten Stunden (zum Beispiel fuer
	 * Fortbildungen) zurueck.
	 * 
	 * @return die Anzahl der ausgeplanten Stunden
	 */
	@Override
	public float getMinusHours() {
		return minusHours;
	}

	// Setter-Methoden

	/**
	 * Setzt das Kuerzel einer Person
	 * 
	 * @param pShortName
	 *            das neue Kuerzel der Person
	 *
	 * @throws IllegalArgumentException
	 *             wenn das Kuerzel aus mehr als drei Zeichen besteht oder wenn
	 *             das Kuerzel {@code null} ist.
	 * 
	 */
	public void setShortName(final String pShortName) {
		if (pShortName != null && pShortName.trim().length() > 0
				&& pShortName.trim().length() <= 4 && !isInteger(pShortName)) {
			shortName = pShortName;
			if (logger.isInfoEnabled()) {
				logger.info("Das Kuerzel der Person wurde auf " + pShortName
						+ " gesetzt");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pShortName);
			}
			throw new IllegalArgumentException(
					"Das Kuerzel darf nicht laenger als 4 sein , Zahlen sind ungueltig.");
		}
	}

	/**
	 * Bestimmt ob es sich bei einem String auch um einen Integer handelt.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isInteger(final String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Setzt den Vornamen einer Person
	 * 
	 * @param pFirstName
	 *            den Vornamen einer Person
	 * @throws IllegalArgumentException
	 *             wenn der Vorname {@code null} ist, oder ungueltige Laenge
	 *             aufweist.
	 */
	public void setFirstName(final String pFirstName)
			throws IllegalArgumentException {
		if (pFirstName != null && pFirstName.trim().length() != 0) {
			firstName = pFirstName;
			if (logger.isInfoEnabled()) {
				logger.info("Der Vorname der Person wurde auf " + pFirstName
						+ " gesetzt");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pFirstName);
			}
			throw new IllegalArgumentException(
					"Der Vorname darf nicht leer sein");
		}
	}

	/**
	 * Setzt den Nachnamen einer Person.
	 * 
	 * @param pLastName
	 *            den Nachnamen einer Person
	 * @throws IllegalArgumentException
	 *             wenn der Vorname {@code null} ist, oder ungueltige Laenge
	 *             aufweist.
	 */
	public void setLastName(final String pLastName) {
		if (pLastName != null && pLastName.trim().length() != 0) {
			lastName = pLastName;
			if (logger.isInfoEnabled()) {
				logger.info("Der Nachname der Person wurde auf " + pLastName
						+ " gesetzt");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pLastName);
			}
			throw new IllegalArgumentException(
					"Der Nachname darf nicht leer sein");
		}
	}

	/**
	 * Setzt die maximale Anzahl von Arbeitsstunden einer Person.
	 * 
	 * @param pMaxWorkload
	 *            maximale Anzahl an Arbeitsstunden einer Person
	 * @throws IllegalArgumentException
	 *             wenn ein negativer Wert uebergeben wird.
	 */
	public void setMaxWorkload(final float pMaxWorkload) {
		if (pMaxWorkload >= 0) {
			maxWorkload = pMaxWorkload;
			if (logger.isInfoEnabled()) {
				logger.info("Der maxWorkload der Person wurde auf "
						+ pMaxWorkload + " gesetzt");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pMaxWorkload);
			}
			throw new IllegalArgumentException(
					"Die maximalen Arbeitsstunden duerfen nicht negativ sein");
		}
	}

	/**
	 * Setzt die verfuegbaren Faecher / Stundeninhalte einer Lehrerin
	 * 
	 * @param pSubject
	 *            Liste mit moeglichen Faechern, die eine Lehrerin unterrichten
	 *            kann.
	 * @throws IllegalArgumentException
	 *             , bei {@code null}
	 */
	@Override
	public void setSubjects(final Set<Subject> pSubject) {
		if (pSubject != null) {
			subjects = pSubject;
			if (logger.isInfoEnabled()) {
				logger.info("Die Faecher eines Lehrers wurden neu gesetzt: "
						+ pSubject);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pSubject);
			}
			throw new IllegalArgumentException(
					"Bitte waehlen Sie Faecher bzw. Stundeninhalte aus.");
		}
	}

	/**
	 * Setzt die Anzahl der Stunden, in denen eine Person nicht verfuegbar ist,
	 * neu. Diese koennen zum Beispiel durch Fortbildungen zu Stande kommen und
	 * muessen im weiteren Planungsverlauf beruecksichtigt werden.
	 * 
	 * @param pMinusHours
	 *            die neue Anzahl der ausgeplanten Stunden einer Person.
	 * 
	 * @throws IllegalArgumentException
	 *             falls fuer die gelbeZeit ein Wert kleiner 0 oder groesser als
	 *             die tatsaechlichen Stunden uebergeben wird.
	 */
	@Override
	public void setMinusHours(final float pMinusHours) {
		if (pMinusHours >= 0) {
			minusHours = pMinusHours;
			addCurWorkload(minusHours);
			if (logger.isInfoEnabled()) {
				logger.info("Die Minusstunden einer Person wurden neu gesetzt: "
						+ pMinusHours);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pMinusHours);
			}
			throw new IllegalArgumentException(
					"Minusstunden duerfen nicht negativ sein oder groesser als die Arbeitsstunden.");
		}
	}

	/**
	 * Addiert den uebergebenen int-Wert zu der aktuellen belegten Stundenanzahl
	 * der Person.
	 * 
	 * @param pCurWorkload
	 *            der zu addierende Wert
	 * 
	 * @throws IllegalArgumentException
	 *             wenn Wert negativ,oder 0.
	 */
	public void addCurWorkload(final float pCurWorkload) {
		if (pCurWorkload >= 0) {
			curWorkload += pCurWorkload;
			if (logger.isInfoEnabled()) {
				logger.info(" zum curWorkload der Person wird folgender Wert hinzuaddiert: "
						+ pCurWorkload);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCurWorkload);
			}
			throw new IllegalArgumentException(
					"Der Wert darf nicht negativ sein");
		}
	}

	/**
	 * Subtrahiert den uebergebenen int-Wert von der aktuellen belegten
	 * Stundenanzahl der Person.
	 * 
	 * @param pSubWorkload
	 *            der zu subtrahierende Wert
	 * 
	 * @throws IllegalArgumentException
	 *             wenn Wert negativ oder 0 ist.
	 */
	public void subCurWorkload(final float pSubWorkload) {
		if (pSubWorkload > 0) {
			curWorkload -= pSubWorkload;
			if (logger.isInfoEnabled()) {
				logger.info(" vom curWorkload der Person wird folgender Wert subtrahiert: "
						+ pSubWorkload);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pSubWorkload);
			}
			throw new IllegalArgumentException(
					"Der Wert darf nicht negativ oder 0 sein");
		}
	}
}