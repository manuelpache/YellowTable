package Yellowstone.YellowTable.logic.components;

import java.util.Collection;

/**
 * Definition eines Lehrers.
 * 
 * Ein Lehrer erbt von einer Person.
 * 
 * Dabei muss der Konstruktor die Anzahl der Meetings, die ausgeplanten Stunden
 * (zum Beispiel fuer Fortbildungen), sowie die Lehrerwuensche uebergeben
 * bekommen.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface ITeacher extends IPerson {

	/**
	 * Gibt den Jahrgang zurueck, für den dieser Lehrer im JahrgangsTeam ist.
	 * 
	 * @return den Jahrgang fuer das Jahrgangsteam.
	 */
	public int getYearTeam();

	/**
	 * Gibt die Zeitwuensche eines Lehrer zurueck.
	 * 
	 * @return die Zeitwuensche eines Lehrers.
	 * 
	 */
	public Collection<Timeslot> getPreferences();

	/**
	 * Setzt den Jahrgang für das Jahrgangsteam neu.
	 * 
	 * @param pYearTeam
	 *            die neue Anzahl der Meetings.
	 * 
	 * @throws IllegalArgumentException
	 *             wenn eine negative Zahl uebergeben wird.
	 */
	public void setYearTeam(final int pYearTeam)
			throws IllegalArgumentException;

	/**
	 * Setzt die Wuensche eines Lehrers neu. Es darf auch {@code null}
	 * uebergeben werden.
	 * 
	 * @param pTimeslot
	 *            die neuen Wuensche eines Lehrers.
	 * @throws IllegalArgumentException
	 * 
	 */
	public void setPreferences(final Collection<Timeslot> pPreferences)
			throws IllegalArgumentException;
}
