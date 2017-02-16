/**
 * 
 */
package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Project von Lam Duy.
 * 
 * @author erikke , Le
 *
 */
public class ProjectTest {
	Config config = new Config();
	Unit[] units;
	Project project = new Project("Project", config, units);

	@Before
	public void setUp() throws Exception {
		project.setConfig(config);
	}

	/**
	 * Testet ob die richtige Config zurueckgegeben wird
	 */
	@Test
	public void testGetConfig() {
		assertTrue(config == project.getConfig());
	}

	/**
	 * Testet, ob der richtige Titel zurueckgegeben wird
	 */
	@Test
	public void testGetTitle() {
		assertEquals("Project", project.getTitle());
	}

	/**
	 * Setzt die Config des Projektes neu
	 */
	@Test
	public void testSetConfig() {
		Config config2 = new Config();
		project.setConfig(config2);
		assertTrue(config2 == project.getConfig());

	}

	/**
	 * Wirft eine IllegalArgumentException bei einer Eingabe von null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetConfig_Null_IllegalArgumentException() {
		project.setConfig(null);
	}

	/**
	 * Setzt den Titel
	 */
	@Test
	public void testSetTitle() {
		project.setTitle("Projekt2");
		assertEquals("Projekt2", project.getTitle());
	}

	/**
	 * Wirft eine IllegalArgumentException bei einer Eingabe von null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Null_IllegalArgumentException() {
		project.setTitle(null);
	}

	/**
	 * Wirft eine IllegalArgumentException bei einer Eingabe von einem leeren
	 * String
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Empty_IllegalArgumentException() {
		project.setTitle("");
	}
}