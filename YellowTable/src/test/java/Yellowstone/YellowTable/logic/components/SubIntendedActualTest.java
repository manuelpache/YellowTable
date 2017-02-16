package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author Le
 *
 */
public class SubIntendedActualTest {

	public SubIntendedActualTest() {
	}

	Subject sub1 = new Subject("Mathe", DIFFICULTY.HARD, new Category(), 45);
	SubIntendedActual sia = new SubIntendedActual(sub1, 8, 4.0);

	/**
	 * Der Test soll das Fach zurueckgeben
	 */
	@Test
	public void testGetSubject() {
		assertTrue(sia.getSubject() == sub1.getTitle());
	}

	/**
	 * Der Test soll die benoetigten Stunden zurueckgeben
	 */
	@Test
	public void testGetIntended() {
		System.out.println(sia.getIntended());
		assertTrue(sia.getIntended().equals("8"));
	}

	/**
	 * Der Test soll die benoetigten Stunden zurueckgeben
	 */
	@Test
	public void testGetIntended_Negativ() {
		SubIntendedActual sia = new SubIntendedActual(sub1, -8, 4);
		assertTrue(sia.getIntended() == "-");
	}

	/**
	 * Der Test soll die aktuellen Stunden zurueckgeben
	 */
	@Test
	public void testGetActual() {
		System.out.println("#########" + sia.getActual());
		assertTrue(sia.getActual().equals("4"));
	}

	/**
	 * Ueberprueft ob bei einer Eingabe von null eine IllegalArgumentException
	 * wirft
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCompareTo_Null_IllegalArgumentException() {
		sia.compareTo(null);
	}

}
