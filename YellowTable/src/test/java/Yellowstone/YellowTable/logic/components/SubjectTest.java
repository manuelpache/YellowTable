/**
 * 
 */
package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Subject von Erik K.
 * 
 * @author tomlewandowski, Le
 *
 */
public class SubjectTest {

	/**
	 * Subject-Objekt fuer die getter-Methoden.
	 */
	Subject s;

	/**
	 * Subject-Objekt fuer die setter-Methoden.
	 */
	Subject s1;

	/**
	 * Raumkategorie fuer allgemeine Unterrichtsraueme.
	 */
	Category c = new Category("Allgemeiner Unterrichtsraum");

	/**
	 * Wird vor dem Testen ausgefuehrt. Erzeugt einmal das Schulfach Mathe
	 * (Schwierigkeit: Hard), 45 Minuten im allgemeinen Unterrichtsruam
	 * 
	 * Und einmal ein leeres Subject, dass wichtig zum Testen der
	 * setter-Methoden ist.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// getter
		s = new Subject("Mathe", DIFFICULTY.HARD, c, 45);
		// setter
		s1 = new Subject();
	}

	// getter-Methoden einmal kurz getestet...

	/**
	 * Testet, ob der Titel des Fachs, der im Konstruktor gesetzt wurde,
	 * zurueckgegeben wird.
	 */
	@Test
	public void testGetTitle() {
		assertEquals("Mathe", s.getTitle());
	}

	/**
	 * Testet, ob der Schwierigkeitgrad des Fachs, der im Konstruktor gesetzt
	 * wurde, zurueckgegeben wird.
	 */
	@Test
	public void testGetDifficulty() {
		assertEquals(DIFFICULTY.HARD, s.getDifficulty());
	}

	/**
	 * Testet, ob die Raumkategorie des Fachs, die im Konstruktor gesetzt wurde,
	 * zurueckgegeben wird.
	 */
	@Test
	public void testGetCategory() {
		assertEquals(c, s.getCategory());
	}

	/**
	 * Testet, ob die Dauer des Fachs, die im Konstruktor gesetzt wurde,
	 * zurueckgegeben wird.
	 */
	@Test
	public void testGetDuration() {
		assertEquals(45, s.getDuration());
	}

	// setter-Methoden:

	/**
	 * Testet, ob der Titel gesettet werden kann, wenn der uebergebene Wert
	 * gueltig ist.
	 */
	@Test
	public void testSetTitle() {
		s1.setTitle("Deutsch");
		assertEquals("Deutsch", s1.getTitle());
	}

	/**
	 * Testet, ob der Titel gesettet werden kann, wenn der uebergebene Wert
	 * ungueltig ist. Hier null. Es sollte eine IllegalArgumentException
	 * fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Null_IllegalArgumentException() {
		s1.setTitle(null);
	}

	/**
	 * Testet, ob der Titel gesettet werden kann, wenn der uebergebene Wert
	 * ungueltig ist. Hier leer. Es sollte eine IllegalArgumentException
	 * fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Empty_IllegalArgumentException() {
		s1.setTitle("");
	}

	/**
	 * Testet, ob die Kategorie gesettet werden kann, wenn der uebergebene Wert
	 * gueltig ist.
	 */
	@Test
	public void testSetCategory() {
		s1.setCategory(c);
		assertEquals(c, s1.getCategory());
	}

	/**
	 * Testet, ob die Raumkategorie gesettet werden kann, wenn der uebergebene
	 * Wert ungueltig ist. Hier null. Es sollte eine IllegalArgumentException
	 * fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCategory_Null_IllegalArgumentException() {
		s1.setCategory(null);
	}

	/**
	 * Testet, ob der Schwierigkeitsgrad gesettet werden kann, wenn der
	 * uebergebene Wert gueltig ist.
	 */
	@Test
	public void testSetDifficulty() {
		s1.setDifficulty(DIFFICULTY.HARD);
		assertEquals(DIFFICULTY.HARD, s1.getDifficulty());
	}

	/**
	 * Testet, ob der Schwierigkeitsgrad gesettet werden kann, wenn der
	 * uebergebene Wert ungueltig ist. Hier null. Es sollte eine
	 * IllegalArgumentException fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDifficulty_Null_IllegalArgumentException() {
		s1.setDifficulty(null);
	}

	/**
	 * Testet, ob die Duration durch positive Werte gesetzt werden kann.
	 */
	@Test
	public void testSetDuration() {
		s1.setDuration(60);
		assertTrue(s1.getDuration() == 60);
	}

	/**
	 * Testet, ob bei einer Eingabe von eine negativen Wert eine
	 * IllegalArgumentException ausgeloest wird
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDuration_Negative_IllegalArgumentException() {
		s1.setDuration(-4);
	}

	/**
	 * Testet, ob ein identisches Objekt erstellt wird
	 */
	@Test
	public void testClone() {
		assertTrue(s.getTitle() == s.clone().getTitle());
		assertTrue(s.getCategory() == s.clone().getCategory());
		assertTrue(s.getDifficulty() == s.clone().getDifficulty());
		assertTrue(s.getDuration() == s.clone().getDuration());
	}
}