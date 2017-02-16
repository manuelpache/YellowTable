package Yellowstone.YellowTable.logic.components;

import java.util.Set;

/**
 * Definition einer Person. Eine Person kann entweder ein paed. Mitarbeiter oder
 * ein Lehrer sein. Der Konstruktor muss ein Kuerzel, einen Vor- und Nachnamen
 * sowie Soll und Ist-Stunden uebergeben bekommen.
 * 
 * Außerdem besitzt eine Peron eine Liste von Inhalten, die sie unterrichten
 * kann.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface IPerson {

	/**
	 * Gibt das Kuerzel einer Person zurueck.
	 * 
	 * @return das Kuerzel einer Person
	 */
	public String getShortName();

	/**
	 * Gibt den Vornamen einer Person zurueck.
	 * 
	 * @return den Vornamen einer Person
	 */
	public String getFirstName();

	/**
	 * Gibt den Nachnamen einer Person zurueck.
	 * 
	 * @return den Nachnamen einer Person
	 */
	public String getLastName();

	/**
	 * Gibt die maximale Anzahl der Arbeitsstunden einer Person zurueck.
	 * 
	 * @return die Soll-Stunden einer Person
	 */
	public float getMaxWorkload();

	/**
	 * Gibt die aktuelle Anzahl der Arbeitsstunden einer Person zurueck.
	 * 
	 * @return die Ist-Stunden einer Person
	 */
	public float getCurWorkload();

	/**
	 * Gibt die möglichen Unterrichtsinhalte einer Person zurueck. Zum Beispiel
	 * Deutsch, Mathe
	 * 
	 * @return die Unterrichtsinhalte einer Person
	 * 
	 */
	public Set<Subject> getSubjects();

	/**
	 * Gibt die Anzahl der ausgeplanten Stunden (zum Beispiel fuer
	 * Fortbildungen) zurueck.
	 * 
	 * @return die Anzahl der ausgeplanten Stunden
	 */
	public float getMinusHours();

	/**
	 * Setzt das Kuerzel einer Person
	 * 
	 * @param pShortName
	 *            das neue Kuerzel der Person
	 *
	 * @throws IllegalArgumentException
	 *             wenn das Kuerzel aus mehr als drei Zeichen besteht. wenn das
	 *             Kuerzel {@code null} ist.
	 * 
	 */
	public void setShortName(final String pShortName)
			throws IllegalArgumentException;

	/**
	 * Setzt den Vornamen einer Person
	 * 
	 * @param pFirstName
	 *            den Vornamen einer Person
	 * @throws IllegalArgumentException
	 *             wenn der Vorname {@code null} ist.
	 */
	public void setFirstName(final String pFirstName)
			throws IllegalArgumentException;

	/**
	 * Setzt den Nachnamen einer Person.
	 * 
	 * @param pLastName
	 *            den Nachnamen einer Person
	 * @throws IllegalArgumentException
	 *             wenn der Nachname {@code null} ist.
	 */
	public void setLastName(final String pLastName)
			throws IllegalArgumentException;

	/**
	 * Setzt die maximale Anzahl von Arbeitsstunden einer Person.
	 * 
	 * @param pMaxWorkload
	 *            maximale Anzahl an Arbeitsstunden einer Person
	 * @throws IllegalArgumentException
	 *             wenn ein negativer Wert uebergeben wird.
	 */
	public void setMaxWorkload(final float pMaxWorkload)
			throws IllegalArgumentException;

	/**
	 * Setzt die verfuegbaren Faecher / Stundeninhalte einer Person
	 * 
	 * @param pSubject
	 *            Liste mit moeglichen Faechern, die eine Person unterrichten
	 *            kann.
	 * @throws IllegalArgumentException
	 */
	public void setSubjects(final Set<Subject> pSubject)
			throws IllegalArgumentException;

	/**
	 * Setzt die Anzahl der Stunden, in denen eine Person nicht verfuegbar ist,
	 * neu. Diese koennen zum Beispiel durch Fortbildungen zu Stande kommen und
	 * muessen im weiteren Planungsverlauf beruecksichtigt werden.
	 * 
	 * @param pMinusHours
	 *            die neue Anzahl der ausgeplanten Stunden einer Person.
	 * 
	 * @throws IllegalArgumentException
	 *             falls fuer die gelbeZeit ein Wert kleiner 0 oder groesser als
	 *             die tatsaechlichen Stunden uebergeben wird.
	 */
	public void setMinusHours(final float pMinusHours)
			throws IllegalArgumentException;

	/**
	 * Addiert den übergebenen float-Wert zu der aktuellen belegten
	 * Stundenanzahl der Person.
	 * 
	 * @param pCurWorkload
	 *            der zu addierende Wert
	 * 
	 * @throws IllegalArgumentException
	 *             , wenn CurWorkload > maxWorkload
	 */
	public void addCurWorkload(final float pCurWorkload)
			throws IllegalArgumentException;

	/**
	 * Subtrahiert den übergebenen float-Wert von der aktuellen belegten
	 * Stundenanzahl der Person.
	 * 
	 * @param pSubWorkload
	 *            der zu subtrahierende Wert
	 * 
	 * @throws IllegalArgumentException
	 *             , wenn CurWorkload < 0
	 */
	public void subCurWorkload(final float pSubWorkload)
			throws IllegalArgumentException;
}
