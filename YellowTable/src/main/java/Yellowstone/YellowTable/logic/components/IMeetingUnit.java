package Yellowstone.YellowTable.logic.components;

import java.util.Set;

/**
 * Planungseinheit, die ein Meeting realisiert. Zu einem Meeting gehoert initial
 * eine Menge von Personen. Außerdem kann ein Meeting ueber einen Namen
 * verfuegen. In Meetings ist es des weiteren moeglich anzugeben, welche Arten
 * von Personen teilnehmen duerfen.
 * 
 * @author tomlewandowski, apag
 *
 */
public interface IMeetingUnit extends IUnit {

	/**
	 * Gibt den Titel eines Meetings zurueck.
	 * 
	 * @return den Titel eines Meetings.
	 */
	public String getTitle();

	/**
	 * Gibt die Teilnehmer eines Meetings zurueck. Dabei soll eine Kopie des
	 * eigentlichen Attributs zurueckgegeben werden, damit der Aufrufer keine
	 * Möglichkeit hat dieses Attribut einfach zu bearbeiten.
	 * 
	 * @return die Teilnehmer eines Meetings
	 */
	public Set<Person> getMembers();

	/**
	 * Gibt den Jahrgang des Meetings zurueck.
	 * 
	 * @return den Jahrgang des Meetings
	 */
	public int getYear();

	/**
	 * Setzt den Titel eines Meetings
	 * 
	 * @param pTitle
	 *            der neue Titel eines Meetings
	 * @throws IllegalArgumentException
	 *             falls {@code null} oder ein leerer String uebergeben wird.
	 */
	public void setTitle(final String pTitle) throws IllegalArgumentException;

	/**
	 * Setzt die Mitglieder eines Meetings neu.
	 * 
	 * @param pPersons
	 *            die neuen Teilnehmer eines Meetings
	 * @throws IllegalArgumentException
	 *             falls {@code null} oder uebergeben wird.
	 */
	public void setMembers(final Set<Person> pPersons)
			throws IllegalArgumentException;

	/**
	 * Setzt den Jahrgang des Meetings.
	 * 
	 * Wirft eine IllegalArgumentException, wenn Zahlen kleiner gleich 0 oder
	 * größer als der maximale Jahrgang uebergeben werden.
	 */
	public void setYear(final int pYear) throws IllegalArgumentException;

}