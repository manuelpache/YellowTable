package Yellowstone.YellowTable.logic.components;

import java.util.Map;

/**
 * Repraesentiert einen Jahrgang und dessen allgemeinen Stundenbedarf.
 * 
 * @author apag
 *
 */
public interface IYear {

	/**
	 * Gibt den Jahrgang fuer den dieses Objekt Informationen enthelt zurueck.
	 * 
	 * @return den Jahrgang
	 */
	public int getYear();

	/**
	 * Gibt den Wochenstundenbedarf fuer einen bestimmten Stundeninhalt zurueck.
	 * 
	 * @param pSubject
	 *            Stundeninhalt dessen Bedarf angegeben werden muss.
	 * @return den Stundenbedarf
	 */
	public int getHoursPerSubject(final Subject pSubject);

	/**
	 * Setzt den Bedarf neu.
	 * 
	 * @param pSubject
	 *            Stundeninhalt dessen Bedarf neu gesetzt werden soll.
	 * @param pHours
	 *            der neue Bedarf
	 */
	public void setHours(final Subject pSubject, final int pHours);

	/**
	 * Gibt eine Liste aller zu erbringenden Stunden fuer jedes (definierte)
	 * Fach zurueck. Der erste Eintrag des Entry repraesentiert das Subject
	 * (mittels Namen) und der zweite Eintrag die Wochenstundenzahl.
	 * 
	 * @return eine Liste von Entries
	 */
	public Map<Subject, Integer> getIntendedWorkload();
	//public Set<Entry<String, Integer>> getAllSubjectNeeds();
	
	
}
