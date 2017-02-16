/**
 * 
 */
package Yellowstone.YellowTable.logic.components;

import java.util.Set;

/**
 * Interface fuer alle Planungseinheiten.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface IUnit {

	/**
	 * Gibt die ID fuer die Klasse zurueck, die entscheidend fuer die Persistenz
	 * ist.
	 * 
	 * @return die ID der Klasse.
	 */
	public int getId();

	/**
	 * Gibt den Zeitslot dieser Unit zurück.
	 * 
	 * @return den zugeordneten Zeitslot
	 */
	public Timeslot getTimeslot();

	/**
	 * Gibt ein Set von Räumen zurück.
	 * 
	 * @return die zugeordneten Räume
	 */
	public Set<Room> getRooms();

	/**
	 * Setzt die ID fuer die Persistenz neu.
	 * 
	 * @param pID
	 *            die neue ID.
	 * @throws IllegalArgumentException
	 *             falls fuer die ID ein invalider Wert uebergeben wird.
	 */
	public void setId(final int pID) throws IllegalArgumentException;

	/**
	 * Setzt ein Set von Räumen.
	 * 
	 * @param pRooms
	 * @throws IllegalArgumentException
	 *             falls ein fehlerhafter Parameter übergeben wurde.
	 */
	public void setRooms(final Set<Room> pRooms) throws IllegalArgumentException;

	/**
	 * Setzt den Timeslot.
	 * @param pTimeslot
	 */
	void setTimeslot(Timeslot pTimeslot) throws IllegalArgumentException;
	
	/**
	 * Gibt die Startzeit zurueck (s. {@linkplain Timeslot})
	 * 
	 * @return die Startzeit als lesbaren String
	 */
	public String getStarttime();

	/**
	 * Gibt die Dauer dieser Unit in Minuten zurueck (s. {@linkplain Timeslot})
	 * 
	 * @return die Dauer in Minuten
	 */
	public int getDuration();


}