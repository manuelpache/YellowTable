package Yellowstone.YellowTable.logic.components;


/**
 * Definition eines Raums.
 * 
 * Ein Raum besteht initial aus einem Namen, einer Nummer und dem dazugehörendem
 * Gebaeude.
 * 
 * Beispiel: "Klassenraum 2A", 064, "Hauptgebauede"
 * 
 * Die Nummer ist hierbei eine optionale Eingabe.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface IRoom {

	/**
	 * Gibt den Namen eines Raumes zurueck.
	 * 
	 * @return den Namen eines Raumes.
	 * 
	 */
	public String getName();

	/**
	 * Gibt das Gebauede zurueck indem sich der Raum befindet.
	 * 
	 * @return das Gebauede indem sich der Raum befindet.
	 * 
	 */
	public String getBuilding();


	/**
	 * Gibt die Nummer eines Raums zurueck. Diese ist optional.
	 * 
	 * @return die Nummer eines Raums.
	 */
	public String getNumber();


	/**
	 * Gibt die Category zurück, die diesem Raum zugeordnet ist.
	 * 
	 * @return die Category des Raums.
	 */
	public Category getCategory();

	/**
	 * Setzt den Namen eines Raums neu.
	 * 
	 * @param pName
	 *            den neuen Namen eines Raums.
	 * @throws IllegalArgumentException
	 *             falls der Name bereits existiert oder {@code null} uebergeben
	 *             wird.
	 */
	public void setName(final String pName) throws IllegalArgumentException;



	/**
	 * Setzt das Gebaeude eines Raums neu.
	 * 
	 * @param pBuilding
	 *            das Gebaude eines Raums.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setBuilding(final String pBuilding)
			throws IllegalArgumentException;


	/**
	 * Setzt die Nummer eines Raums neu. Achtung: Diese Eingabe ist optional
	 * 
	 * @param pNumber
	 *            setzt die Nummer eines Raums neu.
	 * @throws IllegalArgumentException
	 */
	public void setNumber(final String pNumber) throws IllegalArgumentException;

	
	/**
	 * Setzt die Category eines Raums neu.
	 * 
	 * @param pCategory
	 *            setzt die Category eines Raums neu.
	 * @throws IllegalArgumentException
	 */
	public void setCategory(final Category pCategory)
			throws IllegalArgumentException;


	/**
	 * Die equals-Methode vergleicht dieses Objekt mit einem anderen Objekt vom
	 * Typ IRoom.
	 * 
	 * @param pRoom
	 * @return einen boolean-Wert, ob beide Objekte die selben Attribute
	 *         aufweisen (Namen, Gebäude, Nummer und Category)
	 */
	public boolean equals(final Room pRoom);

}
