/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Pause darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author Erik (Logger), phil (Persistenz)
 *
 */
@Entity
public class Years implements IYear {

	// Attribute
	/**
	 * Der Logger dieser Klasse.
	 */
	@Transient
	final static Logger logger = Logger.getLogger(Years.class);

	@Id
	private int year_int;

	@ElementCollection
	private Map<Subject, Integer> intendedWorkload = new HashMap<Subject, Integer>();

	/**
	 * Konstruktor von einem Jahrgang
	 * 
	 * @param pYear
	 *            Jahrgangsnummer
	 */
	public Years(final int pYear) {
		if (pYear > 0) {
			year_int = pYear;
			if (logger.isInfoEnabled()) {
				logger.info("Ein Years-Objekt wurde erzeugt: " + pYear);
			}
		}

		else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter: " + pYear);
			}
			throw new IllegalArgumentException(
					"Bitte tragen sie eine Jahrgangsnummer die groeßer als 0 ist ein.");
		}
	}

	/**
	 * public zero-argument exception
	 */
	public Years() {

	}

	@Override
	public int getYear() {
		return year_int;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int getHoursPerSubject(final Subject pSubject) {
		if (pSubject != null) {
			if (intendedWorkload.containsKey(pSubject)) {
				return intendedWorkload.get(pSubject);
			}
		}
		return 0;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setHours(final Subject pSubject, final int pHours) {
		if (pSubject != null && pHours > 0) {
			if (intendedWorkload.containsKey(pSubject)) {
				intendedWorkload.replace(pSubject, pHours);
			} else {
				intendedWorkload.put(pSubject, pHours);
			}
		} else if (pSubject != null && pHours == 0) {
			intendedWorkload.remove(pSubject);
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die Stunden und das Fach des Year-Objects wurden geaendert"
						+ pSubject.toString() + pHours);
			}
			throw new IllegalArgumentException(
					"Es traf ein ein Fehler auf bei dem Hinzufuegen einer Arbeitszeit");
		}
	}

	/**
	 * Gibt die Map von vorgegebenen Wochenstunden pro Fach
	 * 
	 * @return
	 */
	public Map<Subject, Integer> getIntendedWorkload() {
		return intendedWorkload;
	}

	/**
	 * Erstelllt ein neues Years-Objekt, das inhaltlich komplett mit diesem
	 * uebereinstimmt.
	 * 
	 * @return eine Kopie dieses Years
	 */
	@Override
	public Years clone() {
		Years copy = new Years(getYear());
		copy.intendedWorkload = new HashMap<>(intendedWorkload);
		if (logger.isInfoEnabled()) {
			logger.info("Ein Years-Objekt wurde kopiert: " + copy.toString());
		}

		return copy;
	}
}