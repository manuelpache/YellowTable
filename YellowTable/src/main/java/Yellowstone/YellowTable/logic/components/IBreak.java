package Yellowstone.YellowTable.logic.components;

import java.util.Set;

/**
 * Realisiert in unserer Logik eine Pause. Dabei wird dem Konstruktor ein
 * Jahrgang uebergeben, sowie die Tage an denen die Pause stattfindet und der
 * Pausentitel. Eine Pause soll dabei als {@code Composite} von
 * {@linkplain IBreakUnit} realisiert werden, das heisst fuer jeden uebergebenen
 * Tag wird eine BreakUnit erzeugt, die alle im selben Timeslot liegen.
 * 
 * @author tomlewandowski, apag
 *
 */
public interface IBreak extends ISchoolUnit {

	/**
	 * Gibt den Titel einer Pause zurueck.
	 * 
	 * @return den Titel der Pause.
	 */
	public String getTitle();

	/**
	 * Gibt den Jahrgang zurueck, fuer den die Pause stattfindet.
	 * 
	 * @return den Jahrgang, dem die Pause zugeordnet ist.
	 */
	public int getYear();

	/**
	 * Gibt die Tage zurueck an denen die Pause stattfindet.
	 * 
	 * @return die Tage an denen die Pause stattfindet.
	 */
	public Set<WEEKDAYS> getDays();

	/**
	 * Setzt den Namen einer Pause neu.
	 * 
	 * @param pTitle
	 *            den Namen der Pause
	 * @throws IllegalArgumentException
	 *             wenn der Name {@code null} oder leer ist.
	 */
	public void setTitle(final String pTitle) throws IllegalArgumentException;

	/**
	 * Setzt den Jahrgang, der mit der Pause referenziert ist, neu.
	 * 
	 * @param pYear
	 *            der Jahrgang der Pause.
	 * @throws IllegalArgumentException
	 *             wenn der Jahrgang negativ, null oder groesser als
	 *             {@linkplain IConfig#MAX_YEAR} ist.
	 */
	public void setYear(final int pYear) throws IllegalArgumentException;
	
	/**
	 * Setzt die Tage an denen die Pause stattfindet. Enthaelt das uebergebene
	 * Set nur {@linkplain WEEKDAYS#NONE}, dann gilt diese Pause fuer alle
	 * Wochentage, die WEEKDAYS bereitstellt. Ansonsten werden NONE-Eintraege
	 * ignoriert.
	 * 
	 * @param pDays
	 *            die Tage an denen die Pause stattfindet.
	 * @throws IllegalArgumentException
	 *             wenn {@Code null} oder ein leeres Set uebergeben wird.
	 */
	public void setDays(final Set<WEEKDAYS> pDays)
			throws IllegalArgumentException;

}
