/**
 * Klasse gehört zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die einen paed. Mitarbeiter darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author erikke,lamduy, tomlewandowski(Logger), phil (Persistenz)
 *
 */
@Entity
public class EducEmployee extends Person implements IEducEmployee {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(EducEmployee.class);

	// Konstruktoren.

	/**
	 * Der Konstruktor der Klasse
	 * 
	 * @param pShort
	 * @param pFirst
	 * @param pLast
	 * @param pMaxWorkload
	 */
	public EducEmployee(final String pShort, final String pFirst,
			final String pLast, final float pMaxWorkload,
			final float pCurWorkload) {
		setShortName(pShort);
		setFirstName(pFirst);
		setLastName(pLast);
		setMaxWorkload(pMaxWorkload);

		if (logger.isInfoEnabled()) {
			logger.info("Ein neuer paed. Mitarbeiter wurde erstellt: " + pShort
					+ pFirst + " " + pLast);
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI..
	public EducEmployee() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Paed.Mitarbeiter-Objekt wurde erstellt.");
		}
	}

	/**
	 * Erzeugt einen neuen EducEmployee, der inhaltlich komplett mit diesem
	 * uebereinstimmt.
	 * 
	 * @return eine Kopie dieses EducEmployee
	 */
	@Override
	public EducEmployee clone() {
		EducEmployee copy = new EducEmployee(getShortName(), getFirstName(),
				getLastName(), getMaxWorkload(), getCurWorkload());
		copy.setSubjects(getSubjects());
		copy.setMinusHours(getMinusHours());
		if (logger.isInfoEnabled()) {
			logger.info("Ein paed. Mitarbeiter wurde kopiert: "
					+ copy.getShortName());
		}
		return copy;
	}
}