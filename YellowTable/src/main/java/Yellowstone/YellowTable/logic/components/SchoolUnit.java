/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

/**
 * Definiert Planungseinheiten an denen zum Beispiel Klassen teilnehmen koennen.
 * Zum Beispiel Pausen, Unterricht etc.
 * 
 * Gibt lediglich vor, dass eine Planungseinheit aus diesem Bereich eine Menge
 * von Klassen besitzen muss.
 * 
 * @author prohlede
 *
 * @author Erik k.
 */

//Importe
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse Schoolunit.
 * 
 * 
 * @author Erik , phil (Persistenz)
 *
 */
@MappedSuperclass
public abstract class SchoolUnit extends Unit implements ISchoolUnit {

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(SchoolUnit.class);

	// Ein Set von Klassen der Schoolunit
	@OneToMany
	private Set<Class> classes = new HashSet<Class>();

	public SchoolUnit() {
	}

	/**
	 * Gibt eine Menge aller Klassen zurueck, die an den Planungseinheiten
	 * teilnehmen.
	 * 
	 * @return Menge aller Klassen einer Planungseinheit.
	 */
	public Set<Class> getClasses() {
		return classes;
	}

	// Setter-Methoden

	/**
	 * Setzt die Menge der Klassen neu.
	 * 
	 * @param pClasses
	 *            die neuen Klassen, die an einer Planungseinheit teilnehmen.
	 * 
	 * @throws IllegalArgumentException
	 *             falls zum Beispiel {@code null} uebergeben wird.
	 * 
	 */
	public void setClasses(final Set<Class> pClasses) {
		if (pClasses != null) {
			classes = pClasses;
			if (logger.isInfoEnabled()) {
				logger.info("Die Klassen der Schoolunit wurden neu gesetzt: ."
						+ pClasses.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in einem der folgendem Parameter : "
						+ pClasses);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie die teilnehmende Klassen an.");
		}
	}

	/**
	 * ueberprueft, ob diese SchoolUnit die uebergebene Klasse beinhaltet.
	 * 
	 * @param pClass
	 *            zu ueberpruefende Klasse
	 * @return boolean-Wert, ob pClass enthalten
	 * @throws IllegalArgumentException
	 *             bei {@code null} Parameter.
	 */
	public boolean containsClass(final Class pClass)
			throws IllegalArgumentException {
		if (pClass != null) {
			return (classes.contains(pClass));
		} else {
			throw new IllegalArgumentException("Die Klasse ist ungueltig");
		}
	}
}