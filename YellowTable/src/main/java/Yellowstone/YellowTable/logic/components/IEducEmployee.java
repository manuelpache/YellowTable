package Yellowstone.YellowTable.logic.components;

/**
 * Definition eines paedagogischen Mitarbeiters. Dieser erbt von einer Person.
 * 
 * @author tomlewandowski, apag
 *
 */
public interface IEducEmployee extends IPerson {

	/**
	 * Anzahl der Minuten einer Arbeitsstunde fuer paedagogische Mitarbeiter.
	 * Diese Konstante wird zur Interpretation der Wochenstundenzahl (siehe z.B.
	 * {@linkplain IPerson#setMaxWorkload(float)}) verwendet.
	 */
	public static final int MIN_PER_HOUR = 60;

}
