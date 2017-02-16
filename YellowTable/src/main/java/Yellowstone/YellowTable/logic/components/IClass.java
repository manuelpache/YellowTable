/**
 * 
 */
package Yellowstone.YellowTable.logic.components;

import java.util.Map.Entry;
import java.util.Set;

/**
 * Realisert in unserer Logik eine Schulklasse. Die Schulklasse verfuegt initial
 * ueber einen Namen, einen Jahrgang und eine ID. Zum Beispiel Klasse 3A.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface IClass {

	/**
	 * Gibt den Namen einer Klasse zurueck. Ein Name besteht dabei aus einem
	 * Buchstaben. z.b. A,B,C
	 * 
	 * @return den Namen einer Klasse.
	 */
	public String getName();

	/**
	 * Gibt den Jahrgang einer Klasse zurueck.
	 * 
	 * @return den Jahrgang einer Klasse.
	 */
	public int getYear();

	/**
	 * Gibt den Klassenraum der Klasse zurueck, in dem diese Klasse
	 * hauptsächlich unterrichtet wird.
	 * 
	 * @return den Klassenraum der Klasse.
	 */
	public Room getRoom();

	/**
	 * Setzt den Namen einer Klasse. Dieser besteht aus einem Buchstaben. In
	 * Kobination mit dem Jahrgang ergibt sich so zum Beispiel 3A.
	 * 
	 * @param pName
	 *            Der neue Name einer Klasse.
	 * @throws IllegalArgumentException
	 *             Wenn als Name {@code null} uebergeben wird.
	 */
	public void setName(final String pName) throws IllegalArgumentException;

	/**
	 * Setzt den Jahrgang einer Klasse.
	 * 
	 * @param pYear
	 *            den neuen Jahrgang einer Klasse.
	 * @throws IllegalArgumentException
	 *             wenn fuer den Jahrgang ein Wert uebergeben wird der kleiner
	 *             als 0 oder groesser als 4 ist.
	 */
	public void setYear(final int pYear) throws IllegalArgumentException;

	/**
	 * Setzt den Klassenraum fuer diese Klassen neu.
	 * 
	 * @param pRoom
	 *            der neue Klassenraum.
	 * @throws IllegalArgumentException
	 *             falls fuer die ID ein invalider Wert uebergeben wird.
	 */
	public void setRoom(final Room pRoom) throws IllegalArgumentException;

	/**
	 * Gibt die zu erbringende Stundenzahl fuer das gegebene Fach zurueck. Wurde
	 * fuer diese Klasse keine spezielle Wochenstundenzahl festgelegt, wird der
	 * Wert aus dem entsprechenden Jahrgang zurueckgegeben (s.
	 * {@linkplain IYear}).
	 * 
	 * @param pSubject
	 *            Stundeninhalt dessen Bedarf angegeben werden muss.
	 * @return den Stundenbedarf
	 */
	public int getHoursPerSubject(final Subject pSubject);

	/**
	 * Gibt eine Liste aller zu erbringenden Stunden fuer jedes (definierte)
	 * Fach zurueck. Der erste Eintrag des Entry repraesentiert das Subject
	 * (mittels Namen) und der zweite Eintrag die Wochenstundenzahl.
	 * 
	 * Die Ausgabe entspricht etwa der von {@linkplain IYear}, aber
	 * ueberschiebene Entries muessen ersetzt werden.
	 * 
	 * @return eine Liste von Entries
	 */
	public Set<Entry<Subject, Integer>> getAllSubjectNeeds();

	/**
	 * Legt die Wochenstundenzahl für ein Fach individuell für diese Klasse
	 * fest. Wird (-1) als neuer Wochenstundenwert uebergeben, soll wieder der
	 * Wert aus der entsprechenden Jahrgangsinformation betrachtet werden.
	 * 
	 * @param pSubject
	 *            der Stundeninhalt dessen Arbeitslast neu gesetzt werden soll.
	 * @param pHours
	 *            die neue Arbeitslast
	 */
	public void setHours(final Subject pSubject, final int pHours);

}