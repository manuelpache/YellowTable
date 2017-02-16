package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Testklasse zu Unit
 * 
 * @author Le
 *
 */
public class UnitTest {

	public UnitTest() {
	}

	/**
	 * Timeslot zum Testen
	 */
	Timeslot time1 = new Timeslot(90, WEEKDAYS.MONDAY, "08", "00");

	/**
	 * BreakUnit zum Testen
	 */
	BreakUnit breaku1 = new BreakUnit(time1);

	/**
	 * ExternUnit zum Testen
	 */
	ExternUnit externu1 = new ExternUnit("ExternUnit", time1);

	/**
	 * MeetingUnit zum Testen
	 */
	MeetingUnit meetingu1 = new MeetingUnit("MeetingUnit", 1, time1);

	/**
	 * TeachingUnit zum Testen
	 */
	TeachingUnit teachu1 = new TeachingUnit(time1);

	/**
	 * Teacher zum Testen
	 */
	Teacher teach1 = new Teacher("ErK", "Erik", "Keshishian", 40, 10, 1);
	Teacher teach2 = new Teacher("AlP", "Alicia", "Pagel", 38, 6, 4);

	/**
	 * PersonTime zum Testen
	 */
	PersonTime person1 = new PersonTime(new Timeslot(), teach1);
	PersonTime person2 = new PersonTime(new Timeslot(), teach2);

	/**
	 * Raeume zum Testen
	 */
	Room room1 = new Room();
	Room room2 = new Room();

	/**
	 * BreakUnit zum Testen
	 */
	Class class1 = new Class();
	Class class2 = new Class();

	/**
	 * Faecher zum Testen
	 */
	Subject sub1 = new Subject();
	Subject sub2 = new Subject();

	/**
	 * RaumSet zum Testen
	 */
	HashSet<Room> rooms = new HashSet<Room>();
	HashSet<Room> rooms1 = new HashSet<Room>();

	/**
	 * KlassenSet zum Testen
	 */
	HashSet<Class> classes = new HashSet<Class>();
	HashSet<Class> classes1 = new HashSet<Class>();

	/**
	 * PersonTimeSet zum Testen
	 */
	Set<PersonTime> newPersonTime = new HashSet<PersonTime>();

	/**
	 * FaecherSet zum Testen
	 */
	Set<Subject> newSubjects = new HashSet<Subject>();

	/**
	 * Wird vor dem Testen ausgefuehrt.
	 */
	@Before
	public void setUp() throws Exception {
		rooms.add(room1);
		rooms1.add(room2);

		classes.add(class1);
		classes1.add(class2);

		newPersonTime.add(person2);
		newSubjects.add(sub2);

		teachu1.getSubjects().add(sub1);

		// Hinzufuegen von Klassen
		breaku1.setClasses(classes);
		externu1.setClasses(classes);
		meetingu1.setClasses(classes);
		teachu1.setClasses(classes);

		// Hinzufuegen von Raeumen
		breaku1.setRooms(rooms);
		externu1.setRooms(rooms);
		meetingu1.setRooms(rooms);
		teachu1.setRooms(rooms);

		// Hinzufuegen von Personen
		meetingu1.getMembers().add(teach1);
		meetingu1.getMembers().add(teach2);

		// Hinzufuegen von PersonTime
		teachu1.getPersons().add(person1);
		teachu1.getPersons().add(person2);

		// setId()
		breaku1.setId(1);
		externu1.setId(2);
		meetingu1.setId(3);
		teachu1.setId(4);
	}

	/**
	 * Testet, ob die Methode getId(), die richtige Id zurueckgibt
	 */
	@Test
	public void testGetId() {
		assertEquals(1, breaku1.getId());
		assertEquals(2, externu1.getId());
		assertEquals(3, meetingu1.getId());
		assertEquals(4, teachu1.getId());
	}

	/**
	 * Testet, ob die Methode setId(), die Id richtig setzt
	 */
	@Test
	public void testSetId() {
		breaku1.setId(5);
		externu1.setId(6);
		meetingu1.setId(7);
		teachu1.setId(8);

		assertEquals(5, breaku1.getId());
		assertEquals(6, externu1.getId());
		assertEquals(7, meetingu1.getId());
		assertEquals(8, teachu1.getId());
	}

	/**
	 * Testet, ob die Methode setId(), bei einer Eingabe von einer Zahl die
	 * kleiner ist als 1 eine {@linkplain IllegalArgumentException} wirft
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetId_Negative_IllegalArgumentException() {
		breaku1.setId(-2);
	}

	/**
	 * Testet, ob die Startzeit der Pause, mit der Startzeit, des Timeslots den
	 * wir den Konstruktor uebergeben haben uebereinstimmt.
	 */
	@Test
	public void testGetStarttime() {
		assertTrue(breaku1.getStarttime().equals("08:00"));
		assertTrue(externu1.getStarttime().equals("08:00"));
		assertTrue(meetingu1.getStarttime().equals("08:00"));
		assertTrue(teachu1.getStarttime().equals("08:00"));
	}

	/**
	 * Testet, ob die Dauer der Pause, mit der Dauer, des Timeslots den wir den
	 * Konstruktor uebergeben haben uebereinstimmt.
	 */
	@Test
	public void testGetDuration() {
		assertEquals(90, breaku1.getDuration());
		assertEquals(90, externu1.getDuration());
		assertEquals(90, meetingu1.getDuration());
		assertEquals(90, teachu1.getDuration());
	}

	/**
	 * Testet, ob der Timeslot der Pause, mit dem Wert, den wir den Konstruktor
	 * uebergeben haben uebereinstimmt
	 */
	@Test
	public void testGetTimeslot() {
		assertEquals(time1, breaku1.getTimeslot());
		assertEquals(time1, externu1.getTimeslot());
		assertEquals(time1, meetingu1.getTimeslot());
		assertEquals(time1, teachu1.getTimeslot());
	}

	/**
	 * Testet, ob das Raum-Set der BreakingUnit, mit dem Set, das wir dem
	 * Konstruktor uebergeben haben uebereinstimmt.
	 */
	@Test
	public void testGetRooms() {
		assertEquals(rooms, breaku1.getRooms());
		assertEquals(rooms, externu1.getRooms());
		assertEquals(rooms, meetingu1.getRooms());
		assertEquals(rooms, teachu1.getRooms());
	}

	/**
	 * Testet, ob ein gueltiges Set von Rauemen angenommen wird.
	 */
	@Test
	public void testSetRooms() {
		breaku1.setRooms(rooms1);
		externu1.setRooms(rooms1);
		meetingu1.setRooms(rooms1);
		teachu1.setRooms(rooms1);

		assertEquals(rooms1, breaku1.getRooms());
		assertEquals(rooms1, externu1.getRooms());
		assertEquals(rooms1, meetingu1.getRooms());
		assertEquals(rooms1, teachu1.getRooms());
	}

	/**
	 * Testet, ob ein Room-Set, das null ist eine
	 * {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetRooms_Null_IllegalArgumentException() {
		externu1.setRooms(null);
	}

	/**
	 * Testet, ob ein leeres Set von Raumen erlaubt ist.
	 */
	@Test
	public void testSetRooms_Empty_NoIllegalArgumentException() {
		HashSet<Room> rooms = new HashSet<Room>();
		teachu1.setRooms(rooms);
		assertTrue(teachu1.getRooms().size() == 0);
	}

	/**
	 * Testet, ob ein valider Timeslot angenommen wird.
	 */
	@Test
	public void testSetTimeslot() {
		Timeslot t = new Timeslot(20, WEEKDAYS.MONDAY, "09", "00");
		breaku1.setTimeslot(t);
		externu1.setTimeslot(t);
		meetingu1.setTimeslot(t);
		teachu1.setTimeslot(t);

		assertEquals(t, breaku1.getTimeslot());
		assertEquals(t, externu1.getTimeslot());
		assertEquals(t, meetingu1.getTimeslot());
		assertEquals(t, teachu1.getTimeslot());
	}

	/**
	 * Testet, ob ein Timeslot, der null ist, eine
	 * {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTimeslot_Null_IllegalArgumentException() {
		externu1.setTimeslot(null);
	}

	/**
	 * Vergleicht diese Unit mit einer übergebenen Unit anhand ihres Timeslots.
	 */
	@Test
	public void testCompareTo() {
		assertEquals(0, breaku1.compareTo(breaku1));
		assertEquals(0, externu1.compareTo(externu1));
		assertEquals(0, meetingu1.compareTo(meetingu1));
		assertEquals(0, teachu1.compareTo(teachu1));

	}

	/**
	 * Testet, ob die Methode compareTo(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCompareTo_Null_IllegalArgumentException() {
		meetingu1.compareTo(null);
	}

	/**
	 * Testet, ob die Methode getClasses(), das richtige Set von Klassen
	 * zurueckgibt.
	 */
	@Test
	public void testGetClasses() {
		assertTrue(breaku1.getClasses().contains(class1));
		assertTrue(externu1.getClasses().contains(class1));
		assertTrue(meetingu1.getClasses().contains(class1));
		assertTrue(teachu1.getClasses().contains(class1));

		assertEquals(classes, breaku1.getClasses());
		assertEquals(classes, externu1.getClasses());
		assertEquals(classes, meetingu1.getClasses());
		assertEquals(classes, teachu1.getClasses());

		assertFalse(classes1 == breaku1.getClasses());
		assertFalse(classes1 == externu1.getClasses());
		assertFalse(classes1 == meetingu1.getClasses());
		assertFalse(classes1 == teachu1.getClasses());
	}

	/**
	 * Testet, ob ein gueltiges Set von Klassen angenommen wird.
	 */
	@Test
	public void testSetClasses() {
		breaku1.setClasses(classes1);
		externu1.setClasses(classes1);
		meetingu1.setClasses(classes1);
		teachu1.setClasses(classes1);

		assertTrue(breaku1.getClasses().contains(class2));
		assertTrue(externu1.getClasses().contains(class2));
		assertTrue(meetingu1.getClasses().contains(class2));
		assertTrue(teachu1.getClasses().contains(class2));

		assertEquals(classes1, breaku1.getClasses());
		assertEquals(classes1, externu1.getClasses());
		assertEquals(classes1, meetingu1.getClasses());
		assertEquals(classes1, teachu1.getClasses());

		assertFalse(classes == breaku1.getClasses());
		assertFalse(classes == externu1.getClasses());
		assertFalse(classes == meetingu1.getClasses());
		assertFalse(classes == teachu1.getClasses());
	}

	/**
	 * Testet, ob ein Klassen-Set, das null ist eine IllegalArgumentException
	 * auslöst.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetClasses_Null_IllegalArgumentException() {
		meetingu1.setClasses(null);
	}

	/**
	 * Ueberprueft ob die Klasse enthalten ist.
	 */
	@Test
	public void testContainsClass() {
		assertTrue(breaku1.containsClass(class1));
		assertTrue(externu1.containsClass(class1));
		assertTrue(meetingu1.containsClass(class1));
		assertTrue(teachu1.containsClass(class1));
	}

	// ExternUnit
	/**
	 * Testet, ob die Methode getTitle(), den Titel zurueckgibt.
	 */
	@Test
	public void testGetTitleEU() {
		assertEquals("ExternUnit", externu1.getTitle());
	}

	/**
	 * Testet, ob die Methode setTitle(), den Titel richtig setzt.
	 */
	@Test
	public void testSetTitleEU() {
		externu1.setTitle("UnitExtern");
		assertEquals("UnitExtern", externu1.getTitle());
	}

	/**
	 * Testet, ob die Methode setTitle(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitleEU_Null_IllegalArgumentException() {
		externu1.setTitle(null);
	}

	/**
	 * Testet, ob die Methode setTitle(), bei einer Eingabe von einem leeren
	 * String eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitleEU_Empty_IllegalArgumentException() {
		externu1.setTitle("");
	}

	/**
	 * Testet, ob die Methode equals(), beim Vergleichen true zurueckgibt
	 */
	@Test
	public void testEqualsEU() {
		ExternUnit externu2 = externu1.clone();
		assertTrue(externu1.equals(externu2));
	}

	/**
	 * Testet, ob die Methode clone(), ein identisches Objekt erstellt.
	 */
	@Test
	public void testCloneEU() {
		ExternUnit externu2 = externu1.clone();
		assertTrue(externu1.equals(externu2));
	}

	// MeetingUnit
	/**
	 * Testet, ob die Methode getTitle(), den Titel zurueckgibt.
	 */
	@Test
	public void testGetTitleMU() {
		assertEquals("MeetingUnit", meetingu1.getTitle());
	}

	/**
	 * Testet, ob die Methode getMembers(), die Personen zurueckgibt.
	 */
	@Test
	public void testGetMembers() {
		assertTrue(meetingu1.getMembers().contains(teach1));
		assertTrue(meetingu1.getMembers().contains(teach2));
	}

	/**
	 * Testet, ob die Methode setTitle(), den Titel richtig setzt.
	 */
	@Test
	public void testSetTitleMU() {
		meetingu1.setTitle("UnitMeeting");
		assertEquals("UnitMeeting", meetingu1.getTitle());
	}

	/**
	 * Testet, ob die Methode setTitle(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitleMU_Null_IllegalArgumentException() {
		meetingu1.setTitle(null);
	}

	/**
	 * Testet, ob die Methode setTitle(), bei einer Eingabe von einem leeren
	 * String eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTitleMU_Empty_IllegalArgumentException() {
		meetingu1.setTitle("");
	}

	/**
	 * Testet, ob die Methode setMembers(), die Personen richtig setzt.
	 */
	@Test
	public void testsetMembers() {
		Set<Person> persons1 = new HashSet<Person>();
		meetingu1.setMembers(persons1);
		assertTrue(meetingu1.getMembers().equals(persons1));
	}

	/**
	 * Testet, ob die Methode setMembers(), bei einer Eingabe von {@code null}
	 * eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetMembers_Null_IllegalArgumentException() {
		meetingu1.setMembers(null);
	}

	/**
	 * Testet, ob die Methode setYear(), den Jahrgang richtig setzt.
	 */
	@Test
	public void testSetYear() {
		meetingu1.setYear(3);
		assertTrue(meetingu1.getYear() == 3);
	}

	/**
	 * Testet, ob die Methode setYear(), bei einer Eingabe von einem Jahrgang
	 * kleiner 1 eine {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_LowerThanOne_IllegalArgumentException() {
		meetingu1.setYear(-2);
	}

	/**
	 * Testet, ob die Methode setYear(), bei einer Eingabe von einem Jahrgang
	 * groeßer {@linkplain IConfig.MAX_YEAR} eine
	 * {@linkplain IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetYear_OverIConfig_MAX_YEAR_IllegalArgumentException() {
		meetingu1.setYear(IConfig.MAX_YEAR + 1);
	}

	// TeachingUnit
	/**
	 * Der Test soll ueberpruefen, ob die Methode getPersons(), das richtige
	 * Set<PersonTime> zurueckgibt mit den enthaltenen PersonTime Objekten.
	 */
	@Test
	public void testGetPersons() {
		assertTrue(teachu1.getPersons().contains(person1));
		assertFalse(teachu1.getPersons().contains(new PersonTime()));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getSubjects(), das richtige
	 * Set<Subject> zurueckgibt mit den enthaltenen Subject Objekten.
	 */
	@Test
	public void testGetSubjects() {
		assertTrue(teachu1.getSubjects().contains(sub1));
		assertFalse(teachu1.getSubjects().contains(sub2));
	}

	/**
	 * Testet , ob die Methode getTravelNeeded(), true zurueckgibt, wenn das
	 * Gebaeude gewechselt werden muss. Testet , ob die Methode
	 * setTravelNeeded(), ob das Gebaeude gewechselt werden kann.
	 */
	@Test
	public void testSetGetTravelNeeded() {
		teachu1.setTravelNeed(false);
		assertFalse(teachu1.getTravelNeed());

		teachu1.setTravelNeed(true);
		assertTrue(teachu1.getTravelNeed());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setSubjects(), das richtige
	 * Set<Subject> mit den enthaltenen Subject Objekten gesetzt wird.
	 */
	@Test
	public void testSetSubjects() {
		teachu1.setSubjects(newSubjects);

		assertTrue(teachu1.getSubjects().contains(sub2));
		assertFalse(teachu1.getSubjects().contains(sub1));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setSubject(), bei einer
	 * Eingabe von {@code null} eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetSubject_null_IllegalArgumentException() {
		teachu1.setSubjects(null);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setPersons(), das richtige
	 * Set<PersonTime> mit den enthaltenen PersonTime Objekten gesetzt wird.
	 */
	@Test
	public void testSetPersonTime() {
		teachu1.setPersonTime(newPersonTime);

		assertTrue(teachu1.getPersons().contains(person2));
		assertFalse(teachu1.getPersons().contains(person1));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setPersons(), bei einer
	 * Eingabe von {@code null} eine IllegalArgumentException ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPersonTime_null_IllegalArgumentException() {
		teachu1.setPersonTime(null);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode containsPersons(), ob ein
	 * Person Objekt in dem Set<PersonTime> enthalten ist.
	 */
	@Test
	public void teachingUnitContainsPersons() {
		assertTrue(teachu1.containsPerson("ErK"));
		assertTrue(teachu1.containsPerson("AlP"));
		assertFalse(teachu1.containsPerson("MaP"));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode containsPersons(), bei einer
	 * Eingabe von {@code NULL} eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsPersons_null_IllegalArgumentException() {
		teachu1.containsPerson(null);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode containsPersons(), bei einer
	 * Eingabe von einem leeren String eine {@link IllegalArgumentException}
	 * ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsPersons_Empty_IllegalArgumentException() {
		teachu1.containsPerson("");
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode containsSubject(), ob ein
	 * Subject Objekt in dem Set<Subject> enthalten ist.
	 */
	@Test
	public void testContainsSubject() {
		assertTrue(teachu1.containsSubject(sub1));
		assertFalse(teachu1.containsSubject(sub2));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode containsSubject(), bei einer
	 * Eingabe von {@code NULL} eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testContainsSubject_null_IllegalArgumentException() {
		teachu1.containsSubject(null);
	}
}
