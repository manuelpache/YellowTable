package Yellowstone.YellowTable.logic.components;

//Imports..
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Class von Lam Duy.
 * 
 * @author tomlewandowski, Le
 *
 */
public class ClassTest {

	/**
	 * Erzeugt Raumobjekte zum Testen.
	 */
	Room room1 = new Room();
	Room room2 = new Room();
	Room room3 = new Room();
	Room room4 = new Room();

	/**
	 * Erzeugt ein Klassen-Objekt mit dem Standardkonstruktor zum Testen der
	 * setter-Methoden.
	 */
	Class c = new Class();

	/**
	 * Erzeugt Klassenobjekte zum Testen.
	 */
	Class class1a = new Class("a", 1, room1);
	Class class1b = new Class("b", 1, room2);
	Class class2a = new Class("a", 2, room3);
	Class class2b = new Class("b", 2, room4);

	/**
	 * Erzeugt Subjectobjekte zum Testen.
	 */
	Subject sub1 = new Subject("Mathe", DIFFICULTY.HARD, new Category(), 45);
	Subject sub2 = new Subject("Deutsch", DIFFICULTY.HARD, new Category(), 45);
	Subject sub3 = new Subject("Englisch", DIFFICULTY.HARD, new Category(), 90);
	Subject sub4 = new Subject("Kunst", DIFFICULTY.SOFT, new Category(), 90);

	/*
	 * Tests der getter-Methoden
	 */

	/**
	 * Der Test soll ueberpruefen, ob die Methode getName(), den richtigen Namen
	 * zurueckgibt.
	 */
	@Test
	public void testGetName() {
		assertEquals("1a", class1a.getName());
		assertEquals("1b", class1b.getName());
		assertEquals("2a", class2a.getName());
		assertEquals("2b", class2b.getName());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getDescr(), die richtige
	 * Bezeichnung zurueckgibt.
	 */
	@Test
	public void testGetDescr() {
		assertEquals("a", class1a.getDescr());
		assertEquals("b", class1b.getDescr());
		assertEquals("a", class2a.getDescr());
		assertEquals("b", class2b.getDescr());

	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getYear(), den richtigen
	 * Jahrgang zurueckgibt.
	 */
	@Test
	public void testGetYear() {
		assertEquals(1, class1a.getYear());
		assertEquals(1, class1b.getYear());
		assertEquals(2, class2a.getYear());
		assertEquals(2, class2b.getYear());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getDuration(), den richtigen
	 * Raum zurueckgibt.
	 */
	@Test
	public void testGetRoom() {
		assertEquals(room1, class1a.getRoom());
		assertEquals(room2, class1b.getRoom());
		assertEquals(room3, class2a.getRoom());
		assertEquals(room4, class2b.getRoom());

	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getHoursPerSubject(), die
	 * richtige Stundenanzahl eines Faches zurueckgibt.
	 */
	@Test
	public void testGetHoursPerSubject() {
		Subject sub1 = new Subject("Mathe", DIFFICULTY.HARD, new Category(), 45);
		c.setHours(sub1, 10);
		assertEquals(10, c.getHoursPerSubject(sub1));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getAllSubjectNeeds(), das Set
	 * mit den Faechern und ihrer Individuellen Dauer zurueckgibt.
	 */
	@Test
	public void testGetAllSubjectNeeds() {
		c.setHours(sub1, 10);
		c.setHours(sub2, 6);
		c.setHours(sub3, 8);
		c.setHours(sub4, 7);

		HashMap<Subject, Integer> hashmp = new HashMap<Subject, Integer>();

		for (Entry<Subject, Integer> entries : c.getAllSubjectNeeds()) {
			hashmp.put(entries.getKey(), entries.getValue());
		}

		assertTrue(hashmp.get(sub1) == 10);
		assertTrue(hashmp.get(sub2) == 6);
		assertTrue(hashmp.get(sub3) == 8);
		assertTrue(hashmp.get(sub4) == 7);

	}

	/*
	 * Tests der setter-Methoden
	 */

	/**
	 * Der Test soll ueberpruefen, ob die Methode setName(), die richtige
	 * Beschreibung setzt. Die Methode ist nicht testbar
	 */
	@Test
	public void testSetName() {
	}

	/**
	 * Testet, ob das Setten von null als Name eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Null_IllegalArgumentException() {
		c.setName(null);
	}

	/**
	 * Testet, ob das Setten, von leeren Zeichenketten als Name, eine Exception
	 * wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Empty_IllegalArgumentException() {
		c.setName("");
	}

	/**
	 * Testet, ob das setten von Namen, die ueber mehr als einen Buchstaben
	 * verfuegen, eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_Invalid_IllegalArgumentException() {
		c.setName("AB");
	}

	/**
	 * Testet, ob das setten, von Nummern als Namen, eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetName_AsNumber_IllegalArgumentException() {
		c.setName("1");
	}

	/**
	 * Testet, ob das setten von validen Jahrgaengen einwandfrei funktioniert.
	 */
	@Test
	public void testSetYear() {
		c.setYear(1);
		assertEquals(1, c.getYear());
	}

	/**
	 * Testet, ob das setten von negativen Jahrgaengen eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_Negative_IllegalArgumentException() {
		c.setYear(-1);
	}

	/**
	 * Testet, ob das setten von Jahrgaengen, die groesser als MAX_Year sind,
	 * eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_HigherthenMaxYear_IllegalArgumentException() {
		c.setYear(IConfig.MAX_YEAR + 1);
	}

	/**
	 * Testet, ob das setten von validen Rauemen einwandfrei funktioniert.
	 */
	@Test
	public void testSetRoom() {
		Room r = new Room("Musikraum", "Hauptgebaeude", "MU1", new Category());
		c.setRoom(r);
	}

	/**
	 * Testet, ob das setten von Rauemen, die null sind, eine Exception wirft.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetRoom_Null_IllegalArgumentException() {
		c.setRoom(null);
	}

	/**
	 * Testet, ob das setten von validen Stundenanzahlen pro Fach einwandfrei
	 * funktioniert.
	 */
	@Test
	public void testSetHours() {
		c.setHours(sub1, 10);
		assertTrue(10 == c.getHoursPerSubject(sub1));
	}

	/**
	 * Testet, ob das setten von null als Subject eine IllegalArgumentException
	 * ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetHours_Null_IllegalArgumentException() {
		c.setHours(null, 10);
	}

	/**
	 * Testet, ob das setten von negativen Werten als Fachdauer eine
	 * IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetHours_NegativValue_IllegalArgumentException() {
		Subject exmpleSubject = new Subject("Mathe", DIFFICULTY.HARD, null, 45);
		c.setHours(exmpleSubject, -10);
	}

	/**
	 * Testet, ob das Loeschen von Stunden fuer ein Fach funktioniert
	 */
	@Test
	public void testDeleteIntendedWorkloadForSubject() {
		class1a.setHours(sub1, 10);
		assertTrue(class1a.getHoursPerSubject(sub1) == 10);

		class1a.deleteIntendedWorkloadForSubject(sub1);
		assertTrue(class1a.getHoursPerSubject(sub1) == -1);
	}

	/**
	 * Testet, ob das Loeschen von Stunden fuer ein Fach mit null funktioniert
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteIntendedWorkloadForSubject_Null_IllegalArgumentException() {
		class1a.deleteIntendedWorkloadForSubject(null);
	}

	/**
	 * Testet, ob die Methode toString(), den richtigen Namen zurueckgibt
	 */
	@Test
	public void testToString() {
		assertEquals("1a", class1a.toString());
		assertEquals("1b", class1b.toString());
		assertEquals("2a", class2a.toString());
		assertEquals("2b", class2b.toString());
	}

	/**
	 * Testet, ob ein identisches Objekt erstellt wurde
	 */
	@Test
	public void testClone() {
		assertEquals(class1a.getDescr(), class1a.clone().getDescr());
		assertEquals(class1a.getYear(), class1a.clone().getYear());
		assertEquals(class1a.getRoom(), class1a.clone().getRoom());
		assertEquals(class1a.getAllSubjectNeeds(), class1a.clone()
				.getAllSubjectNeeds());
	}
}