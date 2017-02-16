package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Break von Lam Duy.
 * 
 * @author tomlewandowski, Le
 *
 */
public class BreakTest {
	/**
	 * Timeslot zum Testen
	 */
	Timeslot time1 = new Timeslot(20, WEEKDAYS.MONDAY, "09", "00");

	/**
	 * Erzeugt ein Pausen-Objekt mit dem Standardkonstruktor zum Testen der
	 * setter-Methoden.
	 */
	Break break1 = new Break();

	/**
	 * Erzeugt ein Pausen-Objekt
	 */
	Break break2 = new Break(1, "Fruehstueckspause", time1);

	/**
	 * Erzeugt ein Pausen-Objekt
	 */
	Break break3 = new Break(1, "Fruehstueckspause", time1);

	/**
	 * Eine HashSet fuer die Tage
	 */
	HashSet<WEEKDAYS> days = new HashSet<WEEKDAYS>();

	/**
	 * Wird vor dem Testen ausgefuehrt.
	 */
	@Before
	public void setUp() throws Exception {
		days.add(WEEKDAYS.MONDAY);
		days.add(WEEKDAYS.MONDAY);
		days.add(WEEKDAYS.TUESDAY);
		days.add(WEEKDAYS.WEDNESDAY);
		days.add(WEEKDAYS.THURSDAY);
		days.add(WEEKDAYS.FRIDAY);

		break2.setDays(days);
		break3.setDays(days);
	}

	/*
	 * Tests der getter-Methoden
	 */

	/**
	 * Testet, ob der Titel der Pause, mit dem Wert, den wir den Konstruktor
	 * uebergeben haben uebereinstimmt
	 */
	@Test
	public void testGetTitle() {
		assertTrue(break2.getTitle().equals("Fruehstueckspause"));
	}

	/**
	 * Testet, ob der Jahrgang der Pause, mit dem Wert, den wir den Konstruktor
	 * uebergeben haben uebereinstimmt
	 */
	@Test
	public void testGetYear() {
		assertEquals(1, break2.getYear());
	}

	/**
	 * Testet, ob die Tage der Pause, mit den Tagen, den wir den Konstruktor
	 * uebergeben haben uebereinstimmt
	 */
	@Test
	public void testGetDays() {
		assertTrue(break2.getDays().contains(WEEKDAYS.MONDAY));
		assertTrue(break2.getDays().contains(WEEKDAYS.TUESDAY));
		assertTrue(break2.getDays().contains(WEEKDAYS.WEDNESDAY));
		assertTrue(break2.getDays().contains(WEEKDAYS.THURSDAY));
		assertTrue(break2.getDays().contains(WEEKDAYS.FRIDAY));
	}

	/**
	 * Testet, ob die Tage der Pause als String, mit den Tagen, des Timeslots
	 * den wir den Konstruktor uebergeben haben uebereinstimmt.
	 */
	@Test
	public void testGetDaysAsString() {
		assertTrue(break2.getDaysAsString().equals("Mo,Di,Mi,Do,Fr"));
	}

	/*
	 * Tests der Setter-Methoden
	 */
	/**
	 * Testet, ob ein gueltiger Titel angenommen wird.
	 */
	@Test
	public void testSetTitle() {
		break1.setTitle("Mittagspause");
		assertTrue(break1.getTitle().equals("Mittagspause"));
	}

	/**
	 * Testet, ob eine IllegalArgumentException fliegt, wenn man {@code null}
	 * als Titel uebergibt.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Null_IllegalArgumentException() {
		break1.setTitle(null);
	}

	/**
	 * Testet, ob eine IllegalArgumentException fliegt, wenn man einen leeren
	 * String als Title uebergibt.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Empty_IllegalArgumentException() {
		break1.setTitle("");
	}

	/**
	 * Testet, ob ein gueltiger Jahrgang angenommen wird.
	 */
	@Test
	public void testSetYear() {
		break1.setYear(1);
		assertTrue(break1.getYear() == 1);
	}

	/**
	 * Testet, ob ein negativer Jahrgang eine IllegalArgumentException auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_NegativeValue_IllegalArgumentException() {
		break1.setYear(-1);
	}

	/**
	 * Testet, ob ein Jahrgang, der 0 ist, eine IllegalArgumentException
	 * auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_Zero_IllegalArgumentException() {
		break1.setYear(0);
	}

	/**
	 * Testet, ob ein Jahrgang, der groesser als unsere Konstante ist, eine
	 * IllegalArgumentException auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_HigherthenMaxValue_IllegalArgumentException() {
		break1.setYear(IConfig.MAX_YEAR + 1);
	}

	/**
	 * Testet, ob ein gueltiges Set von Wochentagen angenommen wird.
	 */
	@Test
	public void testSetDays() {
		break1.getDays().add(WEEKDAYS.MONDAY);
		break1.getDays().add(WEEKDAYS.FRIDAY);
		assertTrue(break1.getDays().contains(WEEKDAYS.MONDAY));
		assertTrue(break1.getDays().contains(WEEKDAYS.FRIDAY));
	}

	/**
	 * Testet, ob ein Wochentags-Set, das null ist eine IllegalArgumentException
	 * auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDays_Null_IllegalArgumentException() {
		break1.setDays(null);
	}

	/**
	 * Testet, ob ein Wochentags-Set, das leer ist eine IllegalArgumentException
	 * auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDays_Empty_IllegalArgumentException() {
		HashSet<WEEKDAYS> set = new HashSet<>();
		break1.setDays(set);
	}

	/**
	 * Test, ob die Methode equals() true zurueckgibt, wenn beide Pausenobjekte
	 * identisch sind.
	 */
	@Test
	public void testEquals() {
		assertTrue(break2.equals(break3));
		assertFalse(break2.equals(break1));
	}

	/**
	 * Testet ob die richtigen Pausentage erstellt werden.
	 */
	@Test
	public void testGetBreakUnits() {
		Collection<BreakUnit> breakUnit = new ArrayList<BreakUnit>();

		for (BreakUnit bu : break2.getBreakUnits()) {
			breakUnit.add(bu);
		}

		for (int i = 0; i < breakUnit.size(); i++) {
			assertTrue(breakUnit.contains(breakUnit.toArray()[i]));
		}
	}
}