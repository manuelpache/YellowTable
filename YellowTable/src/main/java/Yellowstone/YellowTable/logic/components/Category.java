/**
 * Klasse gehört zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import javax.persistence.Id;
import javax.persistence.Entity;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Raumkategorie darstellt.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author tomlewandowski , lamduy
 *
 */
@Entity
public class Category implements ICategory {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Category.class);

	/**
	 * Der Name der Kategorie. Zum Beispiel "Musikraum"
	 */
	@Id
	private String name;

	// Konstruktoren

	/**
	 * Konstruktor der Klasse
	 * 
	 * @param pName
	 */
	public Category(final String pName) {
		setName(pName);
		if (logger.isInfoEnabled()) {
			logger.info("Folgende Raumkategorie wurde erstellt: " + pName);
		}
	}

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Category() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Raumkategorie-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Gibt den Namen der Kategorie zurueck.
	 * 
	 * @return den Namen der Raumkategorie.
	 */
	@Override
	public String getName() {
		return name;
	}

	// Setter-Methoden

	/**
	 * Setzt den Namen einer Raumkategorie neu.
	 * 
	 * @param pName
	 *            den Namen einer Raumkategorie.
	 * 
	 * @throws IllegalArgumentException
	 *             falls {@code null} oder ein leerer String uebergeben wird.
	 */
	@Override
	public void setName(final String pName) throws IllegalArgumentException {
		if (pName != null && pName.trim().length() != 0) {
			name = pName;
			if (logger.isInfoEnabled()) {
				logger.info("Der Titel der Raumkategorie wurde geaendert: "
						+ pName);
			}

		} else {
			logger.error("Fehler in folgendem Parameter: " + pName);

			throw new IllegalArgumentException(
					"Der Name der Kategorie ist ungueltig.");
		}
	}
}