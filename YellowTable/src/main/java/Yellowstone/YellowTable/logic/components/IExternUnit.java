package Yellowstone.YellowTable.logic.components;

/**
 * Realisiert eine externe Planungseinheit, wie zum Beispiel den
 * Schwimmunterricht. Eine externe Planungseinheit besitzt initial einen Namen.
 * 
 * @author tomlewandowski, apag
 */
public interface IExternUnit extends ISchoolUnit {

	/**
	 * Gibt den Namen zurueck.
	 * 
	 * @return den Namen einer ExternUnit
	 */
	public String getTitle();

	/**
	 * Setzt den Namen auf den uebergebenen Wert.
	 * 
	 * @param pName
	 *            der neue Name der externen Planungseinheit.
	 * @throws IllegalArgumentException
	 *             wenn der Name leer oder {@code null} ist.
	 */
	public void setTitle(final String pTitle) throws IllegalArgumentException;
}
