package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Category von Lam Duy.
 * 
 * @author tomlewandowski , Le
 *
 */
public class CategoryTest {

	/**
	 * Beispiel Raumkategorie, um setter-Methoden zu testen.
	 */
	Category c = new Category();

	/**
	 * Beispiel Raumkategorie, die in der setUp-Methode initialisiert wird, um
	 * die richtigen Zueweisungen in getter zu testen...
	 */
	Category c2;

	/**
	 * Wird vor dem Testen ausgefuehrt. Erzeugt eine Raumkategorie mit dem Namen
	 * Sporthalle.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		c2 = new Category("Sporthalle");
	}

	/**
	 * Testet, ob der Titel der Kategorie, mit dem Wert, den wir den Konstruktor
	 * uebergeben haben uebereinstimmt
	 */
	@Test
	public void testGetName() {
		assertEquals("Sporthalle", c2.getName());
	}

	/**
	 * Testet, ob ein gueltiger Titel angenommen wird.
	 */
	@Test
	public void testSetName() {
		c.setName("Musikraum");
		assertEquals("Musikraum", c.getName());
	}

	/**
	 * Testet, ob null eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Null_IllegalArgumentException() {
		c.setName(null);
	}

	/**
	 * Testet, ob ein leerer Title eine {@link IllegalArgumentException}
	 * ausloest
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Empty_IllegalArgumentException() {
		c.setName("");
	}
}
