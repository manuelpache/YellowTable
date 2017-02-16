package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Config (Standardeinstellungen) von Lam Duy.
 * 
 * @author tomlewandowski, Le
 *
 */
public class ConfigTest {
	/**
	 * Timeslot fuer die getter-Methoden
	 */
	Timeslot t1 = new Timeslot(45, WEEKDAYS.MONDAY, "10", "00");

	/**
	 * Config-Objekt fuer die setter-Methoden.
	 */
	Config c = new Config();

	/**
	 * Config-Objekt, um zu testen, ob in den getter-Methoden die richtigen
	 * Elemente zurückgegeben werden.
	 */
	Config c1 = new Config("Planung Sommer 2015", 15, t1, 30, "BackupsSommer",
			true, 45);

	/**
	 * Kategorie
	 */
	Category n = new Category("Sporthalle");

	/**
	 * Ein Set von Wochentagen zum Testen
	 */
	HashSet<WEEKDAYS> daysset = new HashSet<>();

	/**
	 * Ein Set von Kategorien zum Testen
	 */
	HashSet<Category> catset = new HashSet<Category>();

	/**
	 * Eine Map von Gebaueden zum Testen
	 */
	HashMap<String, Integer> buildings = new HashMap<String, Integer>();

	MeetingUnit b = new MeetingUnit();
	MeetingUnit[] u = { b };
	Years year = new Years(1);

	// Workload
	// Erzeugt eine Map von Faechern und ihrer Standarddauer..
	Subject maths = new Subject("Mathe", DIFFICULTY.HARD, new Category(), 45);
	List<Years> intendedWorkload = new ArrayList<Years>();

	/**
	 * Wird vor dem Testen ausgefuehrt. Erstellt ein Config.Objekt fuer die
	 * Getter-Methoden.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		/**
		 * Beispiel-Config. Der Schultag findet an allen Wochentagen statt. Die
		 * Granularitaet betraegt 15. Die normale Dauer 45. Backups werden alle
		 * 30 Minuten und beim Schliessen erstellt.
		 */

		// Tagesset fuellen
		daysset.add(WEEKDAYS.MONDAY);
		daysset.add(WEEKDAYS.TUESDAY);
		daysset.add(WEEKDAYS.WEDNESDAY);
		daysset.add(WEEKDAYS.THURSDAY);
		daysset.add(WEEKDAYS.FRIDAY);

		// Kategorienset fuellen.
		catset.add(n);
		// BuildingsMap fuellen.
		buildings.put("Dependence", 20);

		// Workload - Setzt fuer den Jahrgang 1 Mathe auf 45 Minuten.
		year.setHours(maths, 45);
		intendedWorkload.add(year);
		c1.setYears(intendedWorkload);
	}

	/*
	 * Tests der getter-Methoden
	 */

	/**
	 * Test, ob die Granularitaet mit der im Konstruktor gesetzten Granularitaet
	 * uebereinstimmt. (15)
	 */
	@Test
	public void testGetGranularity() {
		assertEquals(15, c1.getGranularity());
	}

	/**
	 * Nicht moeglich zu testen
	 */
	@Test
	public void testGetFixUnits() {

	}

	/**
	 * Test, ob der Timeslot mit dem im Konstruktor gesetzten Timeslot
	 * uebereinstimmt.
	 */
	@Test
	public void testGetTimeframe() {
		assertEquals(t1, c1.getTimeframe());
	}

	/**
	 * Test, ob der BackupTimer, der im Konstruktor gesetzt wurde zurueckgegeben
	 * wird.
	 */
	@Test
	public void testGetBackupTimer() {
		assertEquals(30, c1.getBackupTimer());
	}

	/**
	 * Test, ob der BackupTitel, der im Konstruktor gesetzt wurde,
	 * zurueckgegeben wird.
	 */
	@Test
	public void testGetBackupTitle() {
		assertEquals("BackupsSommer", c1.getBackupTitle());
	}

	/**
	 * Test, ob ein Backup beim Schliessen erstellt wird (Dies wird im
	 * konstruktor auf true gesetzt).
	 */
	@Test
	public void testIsBackupOnClose() {
		assertEquals(true, c1.isBackupOnClose());
	}

	/**
	 * Test, ob die Standarddauer einer Unterrichtseinheit, mit der die vom
	 * Konstruktor gesetzt wurde uerbeinstimmt.
	 */
	@Test
	public void testGetStdDuration() {
		assertEquals(45, c1.getStdDuration());
	}

	/**
	 * Testet, ob die richtige Minutenanzahl je Stunde zurueckgegeben wird.
	 */
	@Test
	public void testGetMinPerHourEduc() {
		c1.setMinPerHourEduc(60);
		assertEquals(60, c1.getMinPerHourEduc());
	}

	/**
	 * Testet, ob die richtige Minutenanzahl je Stunde zurueckgegeben wird.
	 */
	@Test
	public void testGetMinPerHourTeacher() {
		c1.setMinPerHourTeacher(45);
		assertEquals(45, c1.getMinPerHourTeacher());
	}

	/**
	 * Testet, ob der Titel der Konfiguration zurueckgegeben wird.
	 */
	@Test
	public void testGetTitle() {
		assertTrue(c1.getTitle().equals("Planung Sommer 2015"));
	}

	/**
	 * Testet, ob ein Projekt existiert.
	 */
	@Test
	public void testGetProjectExist() {
		c1.setProjectExist(true);
		assertTrue(c1.getProjectExist());

	}

	/**
	 * Testet, ob der Projektname zurueckgegeben wird
	 */
	@Test
	public void testGetProjectTitle() {
		c1.setProjectTitle("Projekt1");
		assertTrue(c1.getProjectTitle().equals("Projekt1"));
	}

	/**
	 * Testet, ob die Jahrgaenge zurueckgegeben werden
	 */
	@Test
	public void testGetYears() {
		assertTrue(intendedWorkload == c1.getYears());
	}

	/**
	 * Test, ob die Tage, die verplant werden duerfen, mit den Tagen, die als
	 * verplanbar im Konstruktor gesetzt wurden uerbeinstimmen.
	 */
	@Test
	public void testGetPlannedDays() {
		c1.setPlannedDays(daysset);
		assertEquals(daysset, c1.getPlannedDays());
	}

	/**
	 * Test, ob die verfuegbaren Gebaude mit den Gebaueden des Konstruktors
	 * uebereinstimmen.
	 */
	@Test
	public void testGetBuildings() {
		c1.setBuildings(buildings);
		assertEquals(buildings, c1.getBuildings());
	}

	/**
	 * Test, ob die Raumkategorien, mit den Raumkategorien aus dem Konstruktor
	 * uebereinstimmen.
	 */
	@Test
	public void testGetCategories() {
		c1.setCategories(catset);
		assertEquals(catset, c1.getCategories());
	}

	/**
	 * Gibt eine Map von dem Vorgegebenen Wochenstunden eines Faches pro Fach
	 * zurück.
	 */
	@Test
	public void testGetIntendedWorkload() {
		assertTrue(45 == c1.getIntendedWorkload(1).get(maths));
	}

	/*
	 * Tests der setter-Methoden
	 */
	/**
	 * Setzt den Titel der Konfiguration
	 */
	@Test
	public void testSetTitle() {
		c1.setTitle("Konfiguration 33");
		assertTrue(c1.getTitle().equals("Konfiguration 33"));
	}

	/**
	 * Wirft eine Exception bei einer Eingabe von null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitle_Null_IllegalArgumentException() {
		c1.setTitle(null);
	}

	/**
	 * Wirft eine Exception bei einer Eingabe von einem leeren String
	 */
	@Test
	public void testSetTitle_Empty_IllegalArgumentException() {

	}

	/**
	 * Test, ob die Granularitaet mit einem gueltigen Wert belegt werden kann.
	 */
	@Test
	public void testSetGranularity() {
		c.setGranularity(15);
		assertEquals(15, c.getGranularity());
	}

	/**
	 * Test, ob die Granularitaet mit einem negativen Wert belegt werden kann.
	 * Sollte Exception ausloesen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetGranularity_Negative_IllegalErgumentException() {
		c.setGranularity(-15);
	}

	/**
	 * Setzt den Status ob ein Projekt vorhanden ist
	 */
	@Test
	public void testSetProjectExist() {
		c1.setProjectExist(false);
		assertFalse(c1.getProjectExist());

	}

	/**
	 * Setzt den Titel des Projekts
	 */
	@Test
	public void testSetProjectTitle() {
		c1.setProjectTitle("Projekt X");
		assertTrue(c1.getProjectTitle().equals("Projekt X"));
	}

	/**
	 * Wirft ein IllegalArgumentException wenn null uebergeben wird.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetProjectTitle_Null_IllegalArgumentException() {
		c1.setProjectTitle(null);

	}

	/**
	 * Wirft eine IllegalArgumentException wenn der Titel leer ist
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetProjectTitle_Empty_IllegalArgumentException() {
		c1.setProjectTitle("");
	}

	/**
	 * Test, ob der Timeslot mit einem gueltigen Wert belegt werden kann.
	 */
	@Test
	public void testSetTimeframe() {
		c.setTimeframe(t1);
		assertEquals(t1, c.getTimeframe());
	}

	/**
	 * Test, ob der Timeslot mit null belegt werden kann. Sollte
	 * IllegalArgumentException ausloesen..
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTimeframe_Null_IllegalArgumentException() {
		c.setTimeframe(null);
	}

	/**
	 * Nicht moeglich zu testen
	 */
	@Test
	public void testSetFixUnits() {

	}

	/**
	 * Nicht moeglich zu testen
	 */
	@Test
	public void testSetFixUnits_Null_IllegalArgumentException() {

	}

	/**
	 * Setzt die Minuten pro Stunde fuer ein EducEmployee
	 */
	@Test
	public void testSetMinPerHourEduc() {
		c1.setMinPerHourEduc(75);
		assertEquals(75, c1.getMinPerHourEduc());
	}

	/**
	 * wirft ein IllegalArgumentException bei einer Eingabe von einem negativen
	 * Wert
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetMinPerHourEduc_Negative_IllegalArgumentException() {
		c1.setMinPerHourEduc(-3);
	}

	/**
	 * Setzt die Minuten pro Stunde fuer ein Lehrer
	 */
	@Test
	public void testSetMinPerHourTeacher() {

	}

	/**
	 * wirft ein IllegalArgumentException bei einer Eingabe von einem negativen
	 * Wert
	 */
	@Test
	public void testSetMinPerHourTeacher_Negative_IllegalArgumentException() {

	}

	/**
	 * Test, ob der Zeitabstand eines Backups mit einem gueltigen Wert belegt
	 * werden kann.
	 */
	@Test
	public void testSetBackupTimer() {
		c.setBackupTimer(15);
		assertEquals(15, c.getBackupTimer());
	}

	/**
	 * Test, ob der Zeitabstand eines Backups mit einem ungueltigen Wert belegt
	 * werden kann. Sollte Excpetion ausloesen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBackupTimer_Negative_IllegalArgumentException() {
		c.setBackupTimer(-10);
	}

	/**
	 * Test, ob der Titel eines Backups mit einem gueltigen Wert belegt werden
	 * kann.
	 */
	@Test
	public void testSetBackupTitle() {
		c.setBackupTitle("BeispielBackupTitle");
		assertEquals("BeispielBackupTitle", c.getBackupTitle());
	}

	/**
	 * Test, ob der Titel eines Backups mit null belegt werden kann. Sollte
	 * Exception ausloesen...
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBackupTitle_Null_IllegalArgumentException() {
		c.setBackupTitle(null);
	}

	/**
	 * Test, ob der Titel eines Backups mit einem leeren String belegt werden
	 * kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBackupTitle_Empty_IllegalArgumentException() {
		c.setBackupTitle("");
	}

	/**
	 * Test, ob eingestellt werden kann das man ein Backup beim Schliessen nicht
	 * erwuenscht.
	 */
	@Test
	public void testSetBackupOnClose_False() {
		c.setBackupOnClose(false);
		assertEquals(false, c.isBackupOnClose());
	}

	/**
	 * Test, ob eingestellt werden kann das man ein Backup beim Schliessen
	 * erwuenscht ist.
	 */
	@Test
	public void testSetBackupOnClose_True() {
		c.setBackupOnClose(true);
		assertEquals(true, c.isBackupOnClose());
	}

	/**
	 * Test, ob die Standarddauer eines Unterrichtsfaches mit einem gueltigen
	 * Wert belegt werden kann.
	 */
	@Test
	public void testSetStdDuration() {
		c.setStdDuration(45);
		assertEquals(45, c.getStdDuration());
	}

	/**
	 * Test, ob die Standarddauer eines Unterrichtsfaches mit einem ungueltigen
	 * Wert belegt werden kann. -> Exception sollte ausgeloest werden..
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStdDuration_Negative_IllegalArgumentException() {
		c.setStdDuration(-10);
	}

	/**
	 * Setzt die Jahrgaenge neu
	 */
	@Test
	public void testSetYear() {
		List<Years> y = new ArrayList<Years>();
		Years yea = new Years();
		y.add(yea);
		c1.setYears(y);
		assertTrue(c1.getYears() == y);
	}

	/**
	 * Wirft eine {@link IllegalArgumentException} wenn ein {@code null}
	 * uebergeben wird
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_Null_IllegalArgumentException() {
		c1.setYears(null);
	}

	/**
	 * Wirft eine {@link IllegalArgumentException} wenn ein leere Liste
	 * uebergeben wird
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_Empty_IllegalArgumentException() {
		List<Years> y = new ArrayList<Years>();
		c1.setYears(y);
	}

	/**
	 * Test, ob die verplanten Tage mit einem gueltigen Wert belegt werden
	 * koennen.
	 * 
	 * Hier wird der Montag eingetragen.
	 */
	@Test
	public void testSetPlannedDays() {
		HashSet<WEEKDAYS> weekset = new HashSet<WEEKDAYS>();
		weekset.add(WEEKDAYS.MONDAY);
		c.setPlannedDays(weekset);
		assertEquals(true, c.getPlannedDays().contains(WEEKDAYS.MONDAY));
	}

	/**
	 * Test, ob die verplanten Tage mit einem leeren Set belegt werden koennen.
	 * Also wenn man gar keine Wochentage ausgewaehlt hat.
	 * 
	 * Exception sollte fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPlannedDays_EmptySet_IllegalArgumentException() {
		HashSet<WEEKDAYS> weekset = new HashSet<WEEKDAYS>();
		c.setPlannedDays(weekset);
	}

	/**
	 * Test, ob die verplanten Tage mit null belegt werden koennen. Also wenn
	 * man gar keine Wochentage ausgewaehlt hat.
	 * 
	 * Exception sollte fliegen.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPlannedDays_Null_IllegalArgumentException() {
		c.setPlannedDays(null);
	}

	/**
	 * Test, ob die Standorte mit einem gueltigen Set belegt werden koennen.
	 */
	@Test
	public void testSetBuildings() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Dependence", 20);
		c.setBuildings(buildings);
		assertEquals(buildings, c.getBuildings());
	}

	/**
	 * Test, ob die Standorte mit einem ungueltigen Set belegt werden koennen.
	 * Zum beispiel ein leeres Set
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBuildings_Empty_IllegalArgumentException() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		c.setBuildings(buildings);
	}

	/**
	 * Test, ob die Standorte mit einem ungueltigen Set belegt werden koennen.
	 * Zum beispiel null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBuildings_Null_IllegalArgumentException() {
		c.setBuildings(null);
	}

	/**
	 * Test, ob der Kategorien mit einem gueltigen Set belegt werden koennen.
	 */
	@Test
	public void testSetCategories() {
		HashSet<Category> catset = new HashSet<Category>();
		catset.add(new Category("Musikraum"));
		c.setCategories(catset);
	}

	/**
	 * Test, ob die Kategorien mit einem ungueltigen Set belegt werden koennen.
	 * Zum beispiel ein leeres Set
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCategories_Empty_IllegalArgumentException() {
		HashSet<Category> catset = new HashSet<Category>();
		c.setCategories(catset);
	}

	/**
	 * Test, ob die Kategorien mit einem ungueltigen Set belegt werden koennen.
	 * Zum beispiel null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCategories_Null_IllegalArgumentException() {
		c.setCategories(null);
	}

	/**
	 * Fuegt einen neuen Jahrgang hinzu
	 */
	@Test
	public void addYears() {
		c1.addYears(5);
		assertTrue(c1.getYears().size() == 2);
	}

	/**
	 * Loest eine {@link IllegalArgumentException} aus wenn man einen negativen
	 * Wert uebergibt
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addYears_Negative_IllegalArgumentException() {
		c1.addYears(-2);
	}

	/**
	 * Test, ob man Workload hinzufuegen kann.
	 */
	@Test
	public void testAddIntendedWorkload() {
		assertEquals(true, !c.getYears().isEmpty());
		// Englisch wird eingetragen.
		Subject english = new Subject();

		c.addIntendedWorkload(1, english, 45);
		// Jetzt sollte die Map fuer Jahrgang 1 nicht mehr leer sein.
		assertEquals(false, c.getIntendedWorkload(1).isEmpty());
	}

	/**
	 * Test, ob man den Intended Workload (Subejct) mit null fuellen kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIntendedWorkload_Null_IllegalArgumentException() {
		c.addIntendedWorkload(1, null, 10);
	}

	/**
	 * Test, ob man den Intended Workload mit einem negativen Wert fuellen kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIntendedWorkload_NegativeValue_IllegalArgumentException() {
		c.addIntendedWorkload(1, new Subject(), -10);
	}

	/**
	 * Test, ob man den Intended Workload mit einem negativen Wert fuellen kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIntendedWorkload_NegativeYear_IllegalArgumentException() {
		c.addIntendedWorkload(-3, new Subject(), 10);
	}

	/**
	 * Test, ob man den Intended Workload mit einem hoeheren Wert fuellen kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddIntendedWorkload_OverYear_IllegalArgumentException() {
		c.addIntendedWorkload(IConfig.MAX_YEAR + 1, new Subject(), 10);
	}

	/**
	 * Test, ob man ein Workload nach dem Hinzufuegen wieder entfernen kann.
	 */
	@Test
	public void testRemoveIntendedWorkload() {
		Subject maths = new Subject("Mathe", DIFFICULTY.HARD, new Category(),
				45);

		c.addIntendedWorkload(1, maths, 45);
		// Testet, ob Mathe eingetragen ist..
		assertEquals(false, c.getIntendedWorkload(1).isEmpty());
		c.removeIntendedWorkload(1, maths);
		// Testet, ob Mathe immernoch eingetragen ist..
		assertEquals(true, c.getIntendedWorkload(1).isEmpty());
	}

	/**
	 * Wirft eine IllegalArgumentException bei einer Eingabe von null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveIntendedWorkload_Null_IllegalArgumentException() {
		c.removeIntendedWorkload(20, null);
	}

	/**
	 * Wirft eine IllegalArgumentException bei einer Eingabe von einem negativen
	 * Wert
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveIntendedWorkload_NegativeYear_IllegalArgumentException() {
		c.removeIntendedWorkload(-3, new Subject());
	}

	/**
	 * Test, ob man von einem nicht vorhandenen Jahrgang, die Stunden pro Fach
	 * entfernen kann
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveIntendedWorkload_OverYear_IllegalArgumentException() {
		c.addIntendedWorkload(IConfig.MAX_YEAR + 1, new Subject(), 10);
	}

	/**
	 * Test, ob ein Standort im Nachhinein veraendert werden kann.
	 */
	@Test
	public void testEditBuilding() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		assertEquals(0, c.getBuildings().get("Hauptgebaeude").intValue());
		// Setzt die neue Distanz auf 10
		c.editBuilding("Hauptgebaeude", 10);
		assertEquals(10, c.getBuildings().get("Hauptgebaeude").intValue());
	}

	/**
	 * Test, ob man beim veraendern des Standorts im Nachhinein einen Null-Wert
	 * als String uebergeben kann.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEditBuilding_Null_IllegalArgumentException() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		c.editBuilding(null, 10);
	}

	/**
	 * Test, ob man beim Veraendern des Standorts im Nachhinein einen negativen
	 * Wert ubergeben kann, als Distanz
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEditBuilding_NegativeValue_IllegalArgumentException() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		c.editBuilding("Hauptgebaeude", -10);
	}

	/**
	 * Test, ob beim Veraendern eines Standorts der gar nicht existiert eine
	 * IllegalArgumentException fliegt.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEditBuilding_NoValue_IllegalArgumentException() {
		c.setBuildings(buildings);
		c.editBuilding("Hauptgebaeude", 10);
	}

	/**
	 * Test, ob ein Standort aus der Liste entfernt werden kann.
	 */
	@Test
	public void testRemoveBuilding() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		c.removeBuilding("Hauptgebaeude");
		assertEquals(true, c.getBuildings().isEmpty());
	}

	/**
	 * Test, ob eine IllegalArgumentException fliegt, wenn man null entfernen
	 * moechte.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveBuilding_Null_IllegalArgumentException() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		c.removeBuilding(null);
	}

	/**
	 * Test, ob eine IllegalArgumentException fliegt, wenn man einen leeren
	 * String entfernen moechte.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveBuilding_Empty_IllegalArgumentException() {
		HashMap<String, Integer> buildings = new HashMap<String, Integer>();
		buildings.put("Hauptgebaeude", 0);
		c.setBuildings(buildings);
		c.removeBuilding("");
	}

	/**
	 * Testet, ob eine neu erstellte Kategorie hinzugefuegt wird.
	 */
	@Test
	public void testAddCategory() {
		HashSet<Category> catset = new HashSet<Category>();
		catset.add(new Category("Musikraum"));
		c.setCategories(catset);
		// Testet, ob eine neu erstellte Kategorie hinzugefuegt wird.
		Category category = new Category("Sporthalle");
		c.addCategory(category);
		assertEquals(true, c.getCategories().contains(category));
	}

	/**
	 * Test, ob eine Raumkategorie mit dem Wert null hinzugefuegt werden kann.
	 * 
	 * Es sollte eine IllegalArgumentException fliegen...
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddCategory_Null_IllegalArgumentException() {
		c.addCategory(null);
	}

	/**
	 * Test, ob eine Raumkategorie geloescht werden kann.
	 */
	@Test
	public void testRemoveCategory() {
		// Traegt ein Set von Categorien ein. Sowie der Musikraum als Kategorie.
		HashSet<Category> catset = new HashSet<Category>();
		Category category = new Category("Musikraum");
		catset.add(category);
		c.setCategories(catset);

		// Loescht den Musikraum als Kategorie.
		c.removeCategory(category);
		assertEquals(false, c.getCategories().contains(category));
	}

	/**
	 * Test, ob eine Raumkategorie mit dem Wert null geloescht werden kann.
	 * 
	 * Es sollte eine IllegalArgumentException fliegen.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveCategory_Null_IllegalArgumentException() {
		// Zunaechst ein HashSet einfuegen...
		HashSet<Category> catset = new HashSet<Category>();
		Category category = new Category("Musikraum");
		catset.add(category);
		c.setCategories(catset);
		// null loeschen.
		c.removeCategory(null);
	}
}