/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Imports

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine ExternUnit darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author erikke, tomlewandowski(Logger), phil (Persistenz)
 *
 */
@Entity
public class ExternUnit extends SchoolUnit {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(ExternUnit.class);

	// Der Titel der ExternUnit
	private String title;

	// Konstruktoren
	/**
	 * Der Konstruktor der Klasse
	 * 
	 * @param pTitle
	 * @param pTimeslot
	 */
	public ExternUnit(final String pTitle, final Timeslot pTimeslot) {
		setTitle(pTitle);
		setTimeslot(pTimeslot);
		if (logger.isInfoEnabled()) {
			logger.info("Folgende Pause wurde erstellt: " + pTitle + ", "
					+ pTimeslot.toString());
		}
	}

	// Leerer Konstruktor fuer DB
	public ExternUnit() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres ExternUnit-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden...

	/**
	 * Gibt den Namen zurueck.
	 * 
	 * @return den Namen einer ExternUnit
	 */
	public String getTitle() {
		return title;
	}

	// Setter-Methoden mit Logger

	/**
	 * Setzt den Namen auf den uebergebenen Wert.
	 * 
	 * @param pName
	 *            der neue Name der externen Planungseinheit.
	 * @throws IllegalArgumentException
	 *             wenn der Name leer oder {@code null} ist.
	 */
	public void setTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Der Titel der ExternUnit wurde geaendert: "
						+ pTitle);
			}
		} else {

			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Titel der Planungseinheit ist ungueltig.");
		}
	}

	@Override
	public boolean equals(final Object pObject) {
		if (pObject != null && pObject instanceof ExternUnit) {
			if (((ExternUnit) pObject).getTitle().equals(getTitle())) {
				if (((ExternUnit) pObject).getTimeslot().equals(getTimeslot())) {
					if (((ExternUnit) pObject).getRooms().containsAll(
							getRooms())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gibt eine neue ExternUnit zurueck, die inhaltlich mit dieser ExternUnit
	 * komplett uebereinstimmt.
	 * 
	 * @return eine Kopie dieser ExternUnit
	 */
	@Override
	public ExternUnit clone() {
		ExternUnit copy = new ExternUnit(getTitle(), getTimeslot());
		copy.setClasses(getClasses());
		copy.setRooms(getRooms());
		copy.setId(getId());
		if (logger.isInfoEnabled()) {
			logger.info("Eine Externunit wurde kopiert: " + copy.getTitle());
		}
		return copy;
	}
}