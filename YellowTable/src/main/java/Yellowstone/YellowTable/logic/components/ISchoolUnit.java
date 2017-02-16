/**
 * 
 */
package Yellowstone.YellowTable.logic.components;

import java.util.Set;

/**
 * Definiert Planungseinheiten an denen zum Beispiel Klassen teilnehmen koennen.
 * Zum Beispiel Pausen, Unterricht etc.
 * 
 * Gibt lediglich vor, dass eine Planungseinheit aus diesem Bereich eine Menge
 * von Klassen besitzen muss.
 * 
 * @author tomlewandowski
 *
 */
public interface ISchoolUnit extends IUnit {

	/**
	 * Gibt eine Menge aller Klassen zurueck, die an den Planungseinheiten
	 * teilnehmen.
	 * 
	 * @return Menge aller Klassen einer Planungseinheit.
	 */
	public Set<Class> getClasses();

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
	public void setClasses(final Set<Class> pClasses)
			throws IllegalArgumentException;
}
