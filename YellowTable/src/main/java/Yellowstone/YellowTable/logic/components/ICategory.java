package Yellowstone.YellowTable.logic.components;

/**
 * Realisiert in unserer Logik eine Raumkategorie. Der Konstruktor bekommt einen
 * Namen uebergeben. Die Raumkategorie referenziert Stundeninhalte
 * {@linkplain ISubject} sinnvoll mit Raeumen {@linkplain IRoom}.
 * 
 * @author tomlewandowski, apag
 *
 */
public interface ICategory {

	/**
	 * Gibt den Namen der Kategorie zurueck.
	 * 
	 * @return den Namen der Raumkategorie.
	 */
	public String getName();

	/**
	 * Setzt den Namen einer Raumkategorie neu.
	 * 
	 * @param pName
	 *            den Namen einer Raumkategorie.
	 * 
	 * @throws IllegalArgumentException
	 *             falls {@code null} oder ein leerer String uebergeben wird.
	 */
	public void setName(final String pName) throws IllegalArgumentException;
}
