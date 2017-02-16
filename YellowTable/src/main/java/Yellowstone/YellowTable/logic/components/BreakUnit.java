/**
 * Klasse gehört zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

// Importe
import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Pauseneinheit darstellt. Sie erbt von SchoolUnit.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author lamduy, tomlewandowski(Logger), phil (Persistenz)
 *
 */
@Entity
public class BreakUnit extends SchoolUnit implements IBreakUnit {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(BreakUnit.class);

	// Konstruktoren

	/**
	 * Der Konstruktor dieser Klasse.
	 * 
	 * @param pTimeslot
	 */
	public BreakUnit(final Timeslot pTimeslot) {
		setTimeslot(pTimeslot);

		if (logger.isInfoEnabled()) {
			logger.info("Folgende Pauseneineheit wurde erstellt: "
					+ pTimeslot.toString());
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI
	public BreakUnit() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Pauseneinheit-Objekt wurde erstellt.");
		}
	}
}
