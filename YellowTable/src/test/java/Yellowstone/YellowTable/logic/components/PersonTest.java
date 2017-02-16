package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Le
 *
 */
public class PersonTest {

	public PersonTest() {
	}

	Teacher teach1 = new Teacher("ErK", "Erik", "Keshishian", 40, 10, 1);
	EducEmployee educ1 = new EducEmployee("PhR", "Philip", "Rohleder", 20, 3);

	Timeslot time1 = new Timeslot(90, WEEKDAYS.MONDAY, "08", "00");
	Timeslot time2 = new Timeslot(45, WEEKDAYS.MONDAY, "11", "30");
	Timeslot time3 = new Timeslot(90, WEEKDAYS.TUESDAY, "09", "50");

	Subject sub1 = new Subject();
	Subject sub2 = new Subject();
	Subject sub3 = new Subject();

	HashSet<Subject> subjects = new HashSet<Subject>();

	@Before
	public void setUp() throws Exception {
		teach1.getSubjects().add(sub1);
		educ1.getSubjects().add(sub3);
		teach1.setYearTeam(3);
		subjects.add(sub2);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getShortName(), den richtigen
	 * Kuerzel zurueckgibt.
	 */
	@Test
	public void testGetShortName() {
		assertTrue(teach1.getShortName().equals("ErK"));
		assertFalse(teach1.getShortName().equals("KeE"));

		assertTrue(educ1.getShortName().equals("PhR"));
		assertFalse(educ1.getShortName().equals("RoP"));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getFirstName(), den richtigen
	 * Vornamen zurueckgibt.
	 */
	@Test
	public void testGetFirstName() {
		assertTrue(teach1.getFirstName().equals("Erik"));
		assertFalse(teach1.getFirstName().equals("Erika"));

		assertTrue(educ1.getFirstName().equals("Philip"));
		assertFalse(educ1.getFirstName().equals("Philipe"));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getLastName(), den richtigen
	 * Nachnamen zurueckgibt.
	 */
	@Test
	public void testGetLastName() {
		assertTrue(teach1.getLastName().equals("Keshishian"));
		assertFalse(teach1.getLastName().equals("Keshishiian"));

		assertTrue(educ1.getLastName().equals("Rohleder"));
		assertFalse(educ1.getLastName().equals("Rohrleger"));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getMaxWorkload(), die richtige
	 * maximale Arbeitszeit zurueckgibt.
	 */
	@Test
	public void testGetMaxWorkload() {
		assertTrue(40 == teach1.getMaxWorkload());
		assertFalse(35 == teach1.getMaxWorkload());

		assertTrue(20 == educ1.getMaxWorkload());
		assertFalse(30 == educ1.getMaxWorkload());
	}

	/**
	 * Fuegt der aktuellen Arbeitszeit, Stunden hinzu
	 */
	@Test
	public void testGetCurWorkload() {
		educ1.addCurWorkload(5);
		assertTrue(5 == educ1.getCurWorkload());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getSubjects(), das richtige
	 * Set<Subjects> mit den gegebenen Objekten von Typ Subject zurueckgibt.
	 */
	@Test
	public void testGetSubjects() {
		assertTrue(teach1.getSubjects().contains(sub1));
		assertFalse(teach1.getSubjects().contains(sub3));

		assertTrue(educ1.getSubjects().contains(sub3));
		assertFalse(educ1.getSubjects().contains(sub1));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getMinusHours(), die richtigen
	 * Minusstunden zurueckgibt.
	 **/
	@Test
	public void testGetMinusHours() {
		teach1.setMinusHours(5);
		assertTrue(5 == teach1.getMinusHours());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setShortName(), den richtigen
	 * Kuerzel setzt.
	 */
	@Test
	public void testSetShortName() {
		teach1.setShortName("AlP");
		assertTrue(teach1.getShortName().equals("AlP"));

		educ1.setShortName("ToL");
		assertTrue(educ1.getShortName().equals("ToL"));
	}

	/**
	 * Testet, ob die Methode setShortName(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetShortName_Null_IllegalArgumentException() {
		educ1.setShortName(null);
	}

	/**
	 * Testet, ob die Methode setShortName(), bei einer Eingabe von einem leeren
	 * String eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetShortName_Empty_IllegalArgumentException() {
		teach1.setShortName("");
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setFirstName(), den richtigen
	 * Vornamen setzt.
	 */
	@Test
	public void testSetFirstName() {
		teach1.setFirstName("Alicia");
		assertTrue(teach1.getFirstName().equals("Alicia"));

		educ1.setFirstName("Tom");
		assertTrue(educ1.getFirstName().equals("Tom"));
	}

	/**
	 * Testet, ob die Methode setFirstName(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetFirstName_Null_IllegalArgumentException() {
		educ1.setFirstName(null);
	}

	/**
	 * Testet, ob die Methode setFirstName(), bei einer Eingabe von einem leeren
	 * String eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetFirstName_Empty_IllegalArgumentException() {
		educ1.setFirstName("");
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setLastName(), den richtigen
	 * Nachnamen setzt.
	 */
	@Test
	public void testSetLastName() {
		teach1.setLastName("Pagel");
		assertTrue(teach1.getLastName().equals("Pagel"));

		educ1.setLastName("Lewandowski");
		assertTrue(educ1.getLastName().equals("Lewandowski"));
	}

	/**
	 * Testet, ob die Methode setLastName(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetLastName_Null_IllegalArgumentException() {
		educ1.setLastName(null);
	}

	/**
	 * Testet, ob die Methode setLastName(), bei einer Eingabe von einem leeren
	 * String eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetLastName_Empty_IllegalArgumentException() {
		educ1.setSubjects(null);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setMaxWorkload(), die richtige
	 * maximale Arbeitszeit setzt.
	 */
	@Test
	public void testSetMaxWorkload() {
		teach1.setMaxWorkload(32);
		assertTrue(32 == teach1.getMaxWorkload());

		educ1.setMaxWorkload(10);
		assertTrue(10 == educ1.getMaxWorkload());
	}

	/**
	 * Testet, ob die Methode setMaxWorkload(), bei einer Eingabe von einem
	 * negativen Wert eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetMaxWorkload_negative_IllegalArgumentException() {
		teach1.setMaxWorkload(-4);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setSubject(), das richtige
	 * Set<Subject> mit den Objekten von Typ Subject setzt.
	 */
	@Test
	public void testSetSubjects() {
		teach1.setSubjects(subjects);
		assertTrue(teach1.getSubjects().contains(sub2));

		educ1.setSubjects(subjects);
		assertTrue(educ1.getSubjects().contains(sub2));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setSubject(), bei einer
	 * Eingabe von {@code null} eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetSubject_null_IllegalArgumentException() {
		teach1.setSubjects(null);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setMinusHours(), die richtigen
	 * Minusstunden setzt.
	 */
	@Test
	public void testSetMinusHours() {
		teach1.setMinusHours(7);
		assertTrue(7 == teach1.getMinusHours());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setMinusHours(), bei einer
	 * Eingabe von {@code weniger 0} eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetMinusHours_lesserThanZero_IllegalArgumentException() {
		teach1.setMinusHours(-1);
	}

	/**
	 * Testet, ob die Aktuelle Arbeitszeit erhoeht wird.
	 */
	@Test
	public void testAddCurWorkload() {
		educ1.addCurWorkload(3);
		assertTrue(3 == educ1.getCurWorkload());
	}

	/**
	 * Testet, ob die Aktuelle Arbeitszeit gemindert wird.
	 */
	@Test
	public void testSubCurWorkload() {
		educ1.addCurWorkload(5);
		educ1.subCurWorkload(3);
		assertTrue(2 == educ1.getCurWorkload());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getYearTeam(), das richtige
	 * Jahrgangsteam zurueckgibt.
	 */
	@Test
	public void testGetYearTeam() {
		assertTrue(3 == teach1.getYearTeam());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getPreferences(), die
	 * richtigen Preferenzen zurueckgibt.
	 */
	@Test
	public void testGetPreferences() {
		teach1.getPreferences().add(time1);
		teach1.getPreferences().add(time2);
		teach1.getPreferences().add(time3);

		assertTrue(teach1.getPreferences().contains(time1));
		assertTrue(teach1.getPreferences().contains(time2));
		assertTrue(teach1.getPreferences().contains(time3));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setYearTeam(), das richtige
	 * Jahrgangsteam setzt.
	 */
	@Test
	public void testSetYearTeam() {
		teach1.setYearTeam(4);
		assertTrue(4 == teach1.getYearTeam());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setYearTeam(), bei einer
	 * Eingabe von kleiner 1 eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYearTeam_lesserThanZero_IllegalArgumentException() {
		teach1.setYearTeam(-1);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setYearTeam(), bei einer
	 * Eingabe von groeßer {@code IConfig.MAX_YEAR} eine
	 * IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYearTeam_greaterThanMaxYear_IllegalArgumentException() {
		teach1.setYearTeam(IConfig.MAX_YEAR + 1);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setPreferences(), die
	 * richtigen Preferenzen setzt.
	 */
	@Test
	public void testSetPreferences() {
		ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
		timeslots.add(time1);
		teach1.setPreferences(timeslots);
		assertTrue(teach1.getPreferences().contains(time1));
		assertFalse(teach1.getPreferences().contains(time2));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setPreferences(), bei einer
	 * Eingabe von {@code null} eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPreferences_null_IllegalArgumentException() {
		teach1.setPreferences(null);
	}
}
