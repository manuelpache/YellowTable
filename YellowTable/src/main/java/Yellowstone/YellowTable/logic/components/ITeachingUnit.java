package Yellowstone.YellowTable.logic.components;

import java.util.Map;
import java.util.Set;

/**
 * Realisiert in unserer Logik eine Unterrichtseinheit. Zu einer
 * Unterrichtseinheit gehoert eine Menge von Personen, sowie eine Menge von
 * Faechern und Klassen.
 * 
 * Im Konstruktor werden die Personen und das Fach der Unterrichts- einheit
 * gesetzt.
 * 
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface ITeachingUnit {

	/**
	 * Methode, die die Personen einer Unterrichtseinheit zurueckgibt.
	 * 
	 * @return die Personen einer Unterrichtseinheit.
	 */
	public Map<Person, Timeslot> getPersons();

	/**
	 * Methode, die die Unterrichtsinhalte einer Unterrichtseinheit zurueckgibt.
	 * 
	 * @return die Unterrichtsinhalte einer Unterrichtseinheit.
	 */
	public Set<Subject> getSubjects();

	/**
	 * Setzt die Unterrichtsinhalte einer Unterrrichtseinheit neu.
	 * 
	 * @param pSubjects
	 *            die neuen Unterrichtsinhalte.
	 */
	public void setSubjects(final Set<Subject> pSubjects);

	/**
	 * Setzt die Teilnehmer einer Unterrichtseinheit neu.
	 * 
	 * @param pPersons
	 *            die neuen Teilnehmer einer Unterrichtseinheit.
	 */
	public void setPersons(Map<Person, Timeslot> pPersons);

	/**
	 * Überprüft, ob diese TeachingUnit das übergebene Subject beinhaltet.
	 * 
	 * @param pSubject zu überprüfendes Fach
	 * @return boolean-Wert, ob pSubject enthalten
	 */
	public boolean containsSubject(final Subject pSubject);
	
	/**
	 * Überprüft, ob diese TeachingUnit die übergebene Person beinhaltet.
	 * 
	 * @param pPerson
	 * @return boolean-Wert, ob pPerson enthalten
	 */
	public boolean containsPerson(final Person pPerson);
}
