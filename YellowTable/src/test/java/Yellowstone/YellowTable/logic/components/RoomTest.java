package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Junit Testklasse für die Klasse Room
 * 
 * @author lamduy
 *
 */
public class RoomTest {
	Category cat1 = new Category();
	Room room1 = new Room("Raum1", "Hauptgebaeude", "001", cat1);

	/**
	 * Testet, ob die Methode getName(), den richtigen Namen zurueckgibt
	 */
	@Test
	public void testGetName() {
		assertTrue(room1.getName().equals("Raum1"));
	}

	/**
	 * Testet, ob die Methode getBuilding(), das richtige Gebaeude zurueckgibt
	 */
	@Test
	public void testGetBuilding() {
		assertTrue(room1.getBuilding().equals("Hauptgebaeude"));
	}

	/**
	 * Testet, ob die Methode getNumber(), die richtige Raumnummer zurueckgibt
	 */
	@Test
	public void testGetNumber() {
		assertTrue(room1.getNumber().equals("001"));

	}

	/**
	 * Testet, ob die Methode getCategory(), die richtige Category zurueckgibt
	 */
	@Test
	public void testGetCategory() {
		assertTrue(cat1 == room1.getCategory());
	}

	/**
	 * Testet, ob die Methode setName(), den Namen richtig setzt.
	 */
	@Test
	public void testSetName() {
		room1.setName("Musikraum");
		assertTrue(room1.getName().equals("Musikraum"));
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Null_IllegalArgumentException() {
		room1.setName(null);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Empty_IllegalArgumentException() {
		room1.setName("");
	}

	/**
	 * Testet, ob die Methode setBuilding(), das Gebaeude richtig setzt
	 */
	@Test
	public void testSetBuilding() {
		room1.setBuilding("Dependance");
		assertTrue(room1.getBuilding().equals("Dependance"));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetBuilding_Null_IllegalArgumentException() {
		room1.setBuilding(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetBuilding_Empty_IllegalArgumentException() {
		room1.setBuilding("");
	}

	/**
	 * Testet, ob die Methode setNumber(), die richtige Raumnummer setzt.
	 */
	@Test
	public void testSetNumber() {
		room1.setNumber("002");
		assertTrue(room1.getNumber().equals("002"));
	}

	/**
	 * Testet, ob die Methode setCategory(), die richtige Kategorie setzt.
	 */
	@Test
	public void testSetCategory() {
		Category cat2 = new Category();
		room1.setCategory(cat2);

		assertTrue(cat2 == room1.getCategory());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCategory_Null_IllegalArgumentException() {
		room1.setCategory(null);
	}

	@Test
	public void testEquals() {
		assertTrue(room1.equals(room1.clone()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEquals_Null_IllegalArgumentException() {
		room1.equals(null);
	}

	@Test
	public void testClone() {
		assertTrue(room1.equals(room1.clone()));
	}
}