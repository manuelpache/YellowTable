package Yellowstone.YellowTable.logic.components;

/**
 * Realisiert einen Stunden/Unterrichtsinhalt in unserer Logik. Kann zum
 * Beispiel ein Fach sein.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface ISubject {

	/**
	 * Gibt den Namen dieses Stundeninhaltes zurueck.
	 * 
	 * @return den Namen des Stundeninhalts.
	 */
	public String getTitle();

	/**
	 * Gibt den Schwierigkeitsgrad des Stundeninhaltes zurueck.
	 * 
	 * @return den Schwierigkeitsgrad.
	 */
	public DIFFICULTY getDifficulty();
	
	
	/**
	 * Gibt die Kategorie des Faches zurück.
	 * 
	 * @return die Kategorie diese Faches
	 */
	public Category getCategory();
	
	/**
	 * Gibt die Standarddauer dieses Stundeninhaltes zurueck.
	 * 
	 * @return die Standarddauer
	 */
	public int getDuration();
	


	/**
	 * Setzt den Titel des Stundeninhaltes neu.
	 * 
	 * @param pTitle
	 *            den neuen Titel des Stundeninhaltes.
	 *
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setTitle(final String pTitle) throws IllegalArgumentException;


	/**
	 * Setzt die Kategorie des Faches neu.
	 * 
	 * @param pCategory
	 *            den neuen Kategorie des Stundeninhaltes.
	 *
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setCategory(final Category pCategory)
			throws IllegalArgumentException;

	/**
	 * Setzt den Schwierigkeitsgrad des Stundeninhalts neu.
	 * 
	 * @param pDifficulty
	 *            der neue Schwierigkeitsgrad.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 * 
	 */
	public void setDifficulty(final DIFFICULTY pDifficulty)
			throws IllegalArgumentException;
}
