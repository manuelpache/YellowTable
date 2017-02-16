package Yellowstone.YellowTable.db;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.warnings.MultipleReservationWarning;
import Yellowstone.YellowTable.warnings.OverloadWarning;
import Yellowstone.YellowTable.warnings.Warning;

public class DBHandlerTest {

	Timeslot time0 = new Timeslot(600, WEEKDAYS.MONDAY, "08", "00");
	Timeslot time1 = new Timeslot(90, WEEKDAYS.MONDAY, "08", "00");
	Timeslot time2 = new Timeslot(45, WEEKDAYS.MONDAY, "11", "30");
	Timeslot time3 = new Timeslot(90, WEEKDAYS.TUESDAY, "09", "50");
	Timeslot time4 = new Timeslot(60, WEEKDAYS.TUESDAY, "13", "00");
	Timeslot time5 = new Timeslot(10, WEEKDAYS.WEDNESDAY, "07", "50");
	Timeslot time6 = new Timeslot(135, WEEKDAYS.WEDNESDAY, "12", "00");
	Timeslot time7 = new Timeslot(30, WEEKDAYS.THURSDAY, "11", "30");
	Timeslot time8 = new Timeslot(15, WEEKDAYS.THURSDAY, "11", "15");
	Timeslot time9 = new Timeslot(20, WEEKDAYS.FRIDAY, "10", "20");
	Timeslot time10 = new Timeslot(90, WEEKDAYS.FRIDAY, "13", "30");
	Timeslot time11 = new Timeslot(60, WEEKDAYS.FRIDAY, "13", "20");
	Timeslot time12 = new Timeslot(90, WEEKDAYS.FRIDAY, "13", "50");

	Category cat1 = new Category("Klassenraum");
	Category cat2 = new Category("Musikraum");
	Category cat3 = new Category("Kunstraum");
	Category cat4 = new Category("Sporthalle");

	Unit[] unit = new Unit[100];

	Config config = new Config("1. Halbjahr 2015", 15, time0, 20,
			"Backup 15.1", true, 45);

	Teacher teach1 = new Teacher("ErK", "Erik", "Keshishian", 40, 10, 1);
	Teacher teach2 = new Teacher("AlP", "Alicia", "Pagel", 38, 6, 4);
	Teacher teach3 = new Teacher("MaP", "Manuel", "Pache", 40, 5, 3);

	EducEmployee educ1 = new EducEmployee("PhR", "Philip", "Rohleder", 20, 3);
	EducEmployee educ2 = new EducEmployee("LaL", "Lam Duy", "Le", 10, 1);
	EducEmployee educ3 = new EducEmployee("ToL", "Tom", "Lewandowski", 28, 4);

	Room room1 = new Room("Klassenraum 1a", "Hauptgebaeude", "001", cat1);
	Room room2 = new Room("Klassenraum 1b", "Hauptgebaeude", "002", cat1);
	Room room3 = new Room("Klassenraum 2a", "Hauptgebaeude", "003", cat1);
	Room room4 = new Room("Klassenraum 2b", "Hauptgebaeude", "004", cat1);
	Room room5 = new Room("Klassenraum 3a", "Hauptgebaeude", "005", cat1);
	Room room6 = new Room("Klassenraum 3b", "Hauptgebaeude", "006", cat1);
	Room room7 = new Room("Klassenraum 4a", "Hauptgebaeude", "007", cat1);
	Room room8 = new Room("Klassenraum 4b", "Hauptgebaeude", "008", cat1);
	Room room9 = new Room("Musikraum Hauptgebaeude", "Hauptgebaeude", "001",
			cat2);
	Room room10 = new Room("Kunstraum Hauptgebaeude", "Hauptgebaeude", "001",
			cat3);
	Room room11 = new Room("Sporthalle Hauptgebaeude", "Dependance", "001",
			cat4);

	Class class1a = new Class("a", 1, room1);
	Class class1b = new Class("b", 1, room2);
	Class class2a = new Class("a", 2, room3);
	Class class2b = new Class("b", 2, room4);
	Class class3a = new Class("a", 3, room5);
	Class class3b = new Class("b", 3, room6);
	Class class4a = new Class("a", 4, room7);
	Class class4b = new Class("b", 4, room8);

	Subject sub1 = new Subject("Mathe", DIFFICULTY.HARD, cat1, 45);
	Subject sub2 = new Subject("Deutsch", DIFFICULTY.HARD, cat1, 45);
	Subject sub3 = new Subject("Englisch", DIFFICULTY.HARD, cat1, 90);
	Subject sub4 = new Subject("Kunst", DIFFICULTY.SOFT, cat3, 90);
	Subject sub5 = new Subject("Musik", DIFFICULTY.SOFT, cat2, 90);
	Subject sub6 = new Subject("Sport", DIFFICULTY.SOFT, cat4, 90);
	Subject sub7 = new Subject("Schwimmen", DIFFICULTY.SOFT, cat4, 90);

	Break break1 = new Break(1, "1. Pause 1. Klasse", time3);
	Break break2 = new Break(2, "2. Pause 2. Klasse", time7);
	Break break3 = new Break(3, "Mittagspause 3. Klasse", time6);
	Break break4 = new Break(4, "1. Pause 4. Klasse", time2);
	Break break5 = new Break(4, "1", time2);
	Break break6 = new Break(2, "Große Pause", time1);

	BreakUnit breaku1 = new BreakUnit(time1);
	BreakUnit breaku2 = new BreakUnit(time2);
	BreakUnit breaku3 = new BreakUnit(time3);
	BreakUnit breaku4 = new BreakUnit(time4);
	BreakUnit breaku5 = new BreakUnit(time5);
	BreakUnit breaku6 = new BreakUnit(time6);

	ExternUnit extern1 = new ExternUnit("Extern1", time4);
	ExternUnit extern2 = new ExternUnit("Extern2", time8);
	ExternUnit extern3 = new ExternUnit("Extern3", time2);
	ExternUnit extern4 = new ExternUnit("Extern4", time5);
	ExternUnit extern5 = new ExternUnit("Extern5", time3);

	MeetingUnit meet1 = new MeetingUnit("Treffen 2. Jahrgang", 2, time1);
	MeetingUnit meet2 = new MeetingUnit("Treffen 3. Jahrgang", 3, time5);
	MeetingUnit meet3 = new MeetingUnit("Treffen 4. Jahrgang", 4, time7);
	MeetingUnit meet4 = new MeetingUnit("Treffen 1. Jahrgang", 1, time9);
	MeetingUnit meet5 = new MeetingUnit("Konferenz 3. Jahrgang", 3, time3);

	TeachingUnit teachu1 = new TeachingUnit(time6);
	TeachingUnit teachu2 = new TeachingUnit(time3);
	TeachingUnit teachu3 = new TeachingUnit(time1);
	TeachingUnit teachu4 = new TeachingUnit(time9);
	TeachingUnit teachu5 = new TeachingUnit(time4);
	TeachingUnit teachu6 = new TeachingUnit(time2);

	Warning warn1 = new Warning(1, "kleiner Fehler");
	Warning warn1b = new Warning(2, "Großer Fehler");

	OverloadWarning warn2 = new OverloadWarning("Überladung warn2", teach1);
	OverloadWarning warn2b = new OverloadWarning("Überladung warn2b", class1a);

	MultipleReservationWarning warn3 = new MultipleReservationWarning(
			"Multiplereservierung warn3", time1, class1a);
	MultipleReservationWarning warn3b = new MultipleReservationWarning(
			"Multiplereservierung warn3b", time2, teach1);
	MultipleReservationWarning warn3c = new MultipleReservationWarning(
			"Multiplereservierung warn3c", time3, room1);

	@Before
	public void init() throws DatabaseException {
		config.setMinPerHourTeacher(45);
		config.getBuildings().put("Dependance", 20);
		config.getBuildings().put("Hauptgebaeude", 0);
		DBHandler.updateConfig(config);
		// Fächer
		teach1.getSubjects().add(sub1);
		teach1.getSubjects().add(sub2);
		teach2.getSubjects().add(sub3);
		teach2.getSubjects().add(sub4);
		teach3.getSubjects().add(sub5);
		teach3.getSubjects().add(sub6);

		educ1.getSubjects().add(sub1);
		educ1.getSubjects().add(sub2);
		educ2.getSubjects().add(sub3);
		educ2.getSubjects().add(sub4);
		educ3.getSubjects().add(sub5);
		educ3.getSubjects().add(sub6);

		teachu1.getSubjects().add(sub1);
		teachu2.getSubjects().add(sub2);
		teachu3.getSubjects().add(sub3);
		teachu6.getSubjects().add(sub1);

		// Klassen
		break1.getClasses().add(class1a);
		break1.getClasses().add(class1b);
		break2.getClasses().add(class2a);
		break6.getClasses().add(class2b);
		break3.getClasses().add(class3a);

		breaku1.getClasses().add(class1a);
		breaku2.getClasses().add(class1b);
		breaku3.getClasses().add(class2a);
		breaku4.getClasses().add(class2b);
		breaku5.getClasses().add(class3a);

		extern1.getClasses().add(class1a);
		extern2.getClasses().add(class1b);
		extern3.getClasses().add(class2a);
		extern4.getClasses().add(class2b);

		meet1.getClasses().add(class1a);
		meet2.getClasses().add(class1b);
		meet3.getClasses().add(class2a);
		meet4.getClasses().add(class2b);

		teachu1.getClasses().add(class1a);
		teachu2.getClasses().add(class1b);
		teachu3.getClasses().add(class2a);
		teachu4.getClasses().add(class2b);
		teachu5.getClasses().add(class3a);
		teachu6.getClasses().add(class1b);

		// Räume
		teachu1.getRooms().add(room1);
		teachu2.getRooms().add(room2);
		teachu3.getRooms().add(room3);
		teachu4.getRooms().add(room4);

		break1.getRooms().add(room1);
		break2.getRooms().add(room2);
		break3.getRooms().add(room3);
		break4.getRooms().add(room4);

		breaku1.getRooms().add(room1);
		breaku2.getRooms().add(room2);
		breaku3.getRooms().add(room3);
		breaku4.getRooms().add(room4);

		meet1.getRooms().add(room1);
		meet2.getRooms().add(room2);
		meet3.getRooms().add(room3);
		meet4.getRooms().add(room4);

		extern1.getRooms().add(room1);
		extern2.getRooms().add(room2);
		extern3.getRooms().add(room3);
		extern4.getRooms().add(room4);

		// DB persistierung
		DBHandler.addCategory(cat1);
		DBHandler.addCategory(cat2);
		DBHandler.addCategory(cat3);
		DBHandler.addCategory(cat4);

		DBHandler.addRoom(room1);
		DBHandler.addRoom(room2);
		DBHandler.addRoom(room3);
		DBHandler.addRoom(room4);
		DBHandler.addRoom(room5);
		DBHandler.addRoom(room6);
		DBHandler.addRoom(room7);
		DBHandler.addRoom(room8);
		DBHandler.addRoom(room9);
		DBHandler.addRoom(room10);
		DBHandler.addRoom(room11);

		DBHandler.addClass(class1a);
		DBHandler.addClass(class1b);
		DBHandler.addClass(class2a);
		DBHandler.addClass(class2b);
		DBHandler.addClass(class3a);
		DBHandler.addClass(class3b);
		DBHandler.addClass(class4a);
		DBHandler.addClass(class4b);

		DBHandler.addBreak(break1);
		DBHandler.addBreak(break2);
		DBHandler.addBreak(break3);
		DBHandler.addBreak(break4);
		DBHandler.addBreak(break5);

		DBHandler.addBreakUnit(breaku1);
		DBHandler.addBreakUnit(breaku2);
		DBHandler.addBreakUnit(breaku3);
		DBHandler.addBreakUnit(breaku4);
		DBHandler.addBreakUnit(breaku5);

		DBHandler.addSubject(sub1);
		DBHandler.addSubject(sub2);
		DBHandler.addSubject(sub3);
		DBHandler.addSubject(sub4);
		DBHandler.addSubject(sub5);
		DBHandler.addSubject(sub6);
		DBHandler.addSubject(sub7);

		DBHandler.addExternUnit(extern1);
		DBHandler.addExternUnit(extern2);
		DBHandler.addExternUnit(extern3);
		DBHandler.addExternUnit(extern4);

		DBHandler.addMeetingUnit(meet1);
		DBHandler.addMeetingUnit(meet2);
		DBHandler.addMeetingUnit(meet3);
		DBHandler.addMeetingUnit(meet4);

		DBHandler.addTeachingUnit(teachu1);
		DBHandler.addTeachingUnit(teachu2);
		DBHandler.addTeachingUnit(teachu3);
		DBHandler.addTeachingUnit(teachu4);
		DBHandler.addTeachingUnit(teachu5);
		DBHandler.addTeachingUnit(teachu6);

		DBHandler.addTeacher(teach1);
		DBHandler.addTeacher(teach2);
		DBHandler.addTeacher(teach3);

		DBHandler.addEducEmployee(educ1);
		DBHandler.addEducEmployee(educ2);
		DBHandler.addEducEmployee(educ3);

		DBHandler.addWarning(warn1);
		DBHandler.addOverloadWarning(warn2);
		DBHandler.addOverloadWarning(warn2b);
		DBHandler.addMultipleReservationWarning(warn3);
		DBHandler.addMultipleReservationWarning(warn3b);
		DBHandler.addMultipleReservationWarning(warn3c);
	}

	@After
	public void deleteDatabase() throws DatabaseException {
		DBHandler.deleteWarning(warn1);
		DBHandler.deleteOverloadWarning(warn2);
		DBHandler.deleteOverloadWarning(warn2b);
		DBHandler.deleteMultipleReservationWarning(warn3);
		DBHandler.deleteMultipleReservationWarning(warn3b);
		DBHandler.deleteMultipleReservationWarning(warn3c);

		

		DBHandler.deleteTeachingUnit(teachu1);
		DBHandler.deleteTeachingUnit(teachu2);
		DBHandler.deleteTeachingUnit(teachu3);
		DBHandler.deleteTeachingUnit(teachu4);
		DBHandler.deleteTeachingUnit(teachu5);
		DBHandler.deleteTeachingUnit(teachu6);

		DBHandler.deleteMeetingUnit(meet1);
		DBHandler.deleteMeetingUnit(meet2);
		DBHandler.deleteMeetingUnit(meet3);
		DBHandler.deleteMeetingUnit(meet4);

		DBHandler.deleteExternUnit(extern1);
		DBHandler.deleteExternUnit(extern2);
		DBHandler.deleteExternUnit(extern3);
		DBHandler.deleteExternUnit(extern4);
		
		DBHandler.deleteEducEmployee(educ1);
		DBHandler.deleteEducEmployee(educ2);
		DBHandler.deleteEducEmployee(educ3);

		DBHandler.deleteTeacher(teach1);
		DBHandler.deleteTeacher(teach2);
		DBHandler.deleteTeacher(teach3);

		DBHandler.deleteSubject(sub1);
		DBHandler.deleteSubject(sub2);
		DBHandler.deleteSubject(sub3);
		DBHandler.deleteSubject(sub4);
		DBHandler.deleteSubject(sub5);
		DBHandler.deleteSubject(sub6);
		DBHandler.deleteSubject(sub7);

		DBHandler.deleteBreakUnit(breaku1);
		DBHandler.deleteBreakUnit(breaku2);
		DBHandler.deleteBreakUnit(breaku3);
		DBHandler.deleteBreakUnit(breaku4);
		DBHandler.deleteBreakUnit(breaku5);

		DBHandler.deleteBreak(break1);
		DBHandler.deleteBreak(break2);
		DBHandler.deleteBreak(break3);
		DBHandler.deleteBreak(break4);
		DBHandler.deleteBreak(break5);

		DBHandler.deleteClass(class1a);
		DBHandler.deleteClass(class1b);
		DBHandler.deleteClass(class2a);
		DBHandler.deleteClass(class2b);
		DBHandler.deleteClass(class3a);
		DBHandler.deleteClass(class3b);
		DBHandler.deleteClass(class4a);
		DBHandler.deleteClass(class4b);

		DBHandler.deleteRoom(room1);
		DBHandler.deleteRoom(room2);
		DBHandler.deleteRoom(room3);
		DBHandler.deleteRoom(room4);
		DBHandler.deleteRoom(room5);
		DBHandler.deleteRoom(room6);
		DBHandler.deleteRoom(room7);
		DBHandler.deleteRoom(room8);
		DBHandler.deleteRoom(room9);
		DBHandler.deleteRoom(room10);
		DBHandler.deleteRoom(room11);

		DBHandler.deleteCategory(cat1);
		DBHandler.deleteCategory(cat2);
		DBHandler.deleteCategory(cat3);
		DBHandler.deleteCategory(cat4);

	}
	
	/*
	 * Testet ob alle Lehererinnen in die Datenbank geschrieben wurden.
	 */
	@Test
	public void testGetAllTeachers() throws DatabaseException {
		assertEquals(3, DBHandler.getAllTeachers().size());
	}

	/*
	 * Testet ob alle paedagogischen Mitarbeiter in die Datenbank geschrieben
	 * wurden.
	 */
	@Test
	public void testGetAllEducEmployees() throws DatabaseException {
		assertEquals(3, DBHandler.getAllEducEmployees().size());
	}

	/*
	 * Testet ob alle Kategorien in die Datenbank geschrieben wurden.
	 */
	@Test
	public void testGeAllCategories() throws DatabaseException {
		assertEquals(4, DBHandler.getAllCategories().size());
	}

	@Test
	public void testGetAllRooms() throws DatabaseException {
		assertEquals(11, DBHandler.getAllRooms().size());
	}

	@Test
	public void testGetAllClasses() throws DatabaseException {
		assertEquals(8, DBHandler.getAllClasses().size());
	}

	@Test
	public void testGetAllBreaks() throws DatabaseException {
		assertEquals(5, DBHandler.getAllBreaks().size());
	}

	@Test
	public void testGetAllExternUnits() throws DatabaseException {
		assertEquals(4, DBHandler.getAllExternUnits().size());
	}

	@Test
	public void testGetAllMeetingUnits() throws DatabaseException {
		assertEquals(4, DBHandler.getAllMeetingUnits().size());
	}

	@Test
	public void testGetAllTeachingUnits() throws DatabaseException {
		assertEquals(6, DBHandler.getAllTeachingUnits().size());
	}

	@Test
	public void testGetDeleteAllSubjects() throws DatabaseException {
		assertEquals(7, DBHandler.getAllSubjects().size());
	}

	@Test
	public void testGetAllYears() throws DatabaseException {
		assertTrue(IConfig.MAX_YEAR == DBHandler.getAllYears().size());
		int i = 1;
		for (Years y : DBHandler.getAllYears()) {
			assertTrue(i == y.getYear());
			i++;
		}
	}

	@Test
	public void testGetHoursPerSubjectYear() throws DatabaseException {
		DBHandler.getYearsByInt(1).setHours(sub1, 30);
		DBHandler.getYearsByInt(2).setHours(sub2, 40);

		assertTrue(30 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub1, 1));
		assertTrue(0 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub2, 1));
		assertTrue(40 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub2, 2));
		assertTrue(0 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub1, 2));

		DBHandler.getYearsByInt(1).getIntendedWorkload().clear();
		DBHandler.getYearsByInt(2).getIntendedWorkload().clear();

	}

	@Test
	public void testGetHoursPerTeacher() throws DatabaseException {
		assertEquals(0.0, DBHandler.getHoursPerTeacher(teach1), 0);
		PersonTime p = new PersonTime(time1, teach1);
		teachu3.getPersons().add(p);
		DBHandler.updateTeachingUnit(teachu3);
		assertEquals(2.0, DBHandler.getHoursPerTeacher(teach1), 0);
		teachu3.getPersons().remove(p);
		DBHandler.updateTeachingUnit(teachu3);
	}

	@Test
	public void testGetClassHoursPerSubjectPerTeacher()
			throws DatabaseException {
		PersonTime p3 = new PersonTime(time1, teach1);
		PersonTime p6 = new PersonTime(time2, teach1);
		teachu3.getPersons().add(p3);
		teachu6.getPersons().add(p6);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
		Collection<Class> coll1 = new ArrayList<Class>();
		Collection<Double> coll2 = new ArrayList<Double>();
		coll1.add(class2a);
		coll2.add(2.0);
		coll2.add(1.0);
		coll1.add(class1b);
		assertTrue(coll1.containsAll(DBHandler
				.getClassHoursPerSubjectPerTeacher(teach1, sub1).keySet()));
		assertTrue(coll2.containsAll(DBHandler
				.getClassHoursPerSubjectPerTeacher(teach1, sub1).values()));
		teachu3.getPersons().remove(p3);
		teachu6.getPersons().remove(p6);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
	}

	// keine Überschneidung, exakt
	@Test
	public void testGetClassesPerPersonPerTime_1() throws DatabaseException {

		PersonTime p1 = new PersonTime(time3, teach1);
		PersonTime p2 = new PersonTime(time4, teach1);

		teachu2.getPersons().add(p1);
		teachu5.getPersons().add(p2);

		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu5);

		Collection<Class> coll = new ArrayList<Class>();
		coll.add(class3a);
		coll.add(class1b);

		assertTrue(DBHandler.getClassesPerPersonPerTime(teach1,
				WEEKDAYS.TUESDAY, 9, 50, 14, 0).containsAll(coll));

		teachu2.getPersons().remove(p1);
		teachu5.getPersons().remove(p2);

		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu5);
	}

	// Überschneidung, vorne
	@Test
	public void testGetClassesPerPersonPerTime_2() throws DatabaseException {
		PersonTime p1 = new PersonTime(time3, teach1);
		PersonTime p2 = new PersonTime(time4, teach1);

		teachu2.getPersons().add(p1);
		teachu5.getPersons().add(p2);

		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu5);

		Collection<Class> coll = new ArrayList<Class>();
		coll.add(class3a);
		coll.add(class1b);

		assertTrue(DBHandler.getClassesPerPersonPerTime(teach1,
				WEEKDAYS.TUESDAY, 9, 50, 13, 30).containsAll(coll));

		teachu2.getPersons().remove(p1);
		teachu5.getPersons().remove(p2);

		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu5);
	}

	// Überschneidung hinten
	@Test
	public void testGetClassesPerPersonPerTime_3() throws DatabaseException {
		PersonTime p1 = new PersonTime(time1, teach1);
		PersonTime p2 = new PersonTime(time2, teach1);
		teachu3.getPersons().add(p1);
		teachu6.getPersons().add(p2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
		Collection<Class> coll = new ArrayList<Class>();
		coll.add(class2a);
		coll.add(class1b);
		assertTrue(DBHandler.getClassesPerPersonPerTime(teach1,
				WEEKDAYS.MONDAY, 7, 0, 12, 0).containsAll(coll));
		teachu3.getPersons().remove(p1);
		teachu6.getPersons().remove(p2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
	}

	@Test
	public void testGetClassesByYear() throws DatabaseException {
		assertEquals(0, DBHandler.getClassesByYear(0).size());
		Collection<Class> coll = new ArrayList<Class>();
		coll.add(class1a);
		coll.add(class1b);
		assertEquals(2, DBHandler.getClassesByYear(1).size());
		assertTrue(DBHandler.getClassesByYear(1).containsAll(coll));

		assertEquals(2, DBHandler.getClassesByYear(3).size());
		assertEquals(2, DBHandler.getClassesByYear(4).size());
	}

	@Test
	public void testGetClassesByTeacher_1() throws DatabaseException {
		PersonTime p1 = new PersonTime(time6, teach1);
		PersonTime p2 = new PersonTime(time1, teach1);
		PersonTime p3 = new PersonTime(time9, teach1);
		Collection<Class> coll = new ArrayList<Class>();
		coll.add(class1a);
		coll.add(class2a);
		coll.add(class2b);
		teachu1.getPersons().add(p1);
		teachu3.getPersons().add(p2);
		teachu4.getPersons().add(p3);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);
		assertTrue(DBHandler.getClassesByTeacher(teach1).containsAll(coll));
		assertTrue(coll.containsAll(DBHandler.getClassesByTeacher(teach1)));
		teachu1.getPersons().remove(p1);
		teachu3.getPersons().remove(p2);
		teachu4.getPersons().remove(p3);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);
	}

	@Test
	public void testGetClassesByTeacher_2() throws DatabaseException {
		PersonTime p1 = new PersonTime(time6, teach1);
		PersonTime p2 = new PersonTime(time3, teach1);
		PersonTime p3 = new PersonTime(time1, teach1);
		PersonTime p6 = new PersonTime(time2, teach1);
		Collection<Class> coll = new ArrayList<Class>();

		coll.add(class1b);
		coll.add(class1a);
		coll.add(class2a);

		teachu1.getPersons().add(p1);
		teachu2.getPersons().add(p2);
		teachu3.getPersons().add(p3);
		teachu6.getPersons().add(p6);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
		assertTrue(DBHandler.getClassesByTeacher(teach1).containsAll(coll));
		assertTrue(coll.containsAll(DBHandler.getClassesByTeacher(teach1)));
		teachu1.getPersons().remove(p1);
		teachu2.getPersons().remove(p2);
		teachu3.getPersons().remove(p3);
		teachu6.getPersons().remove(p6);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu6);
	}

	@Test
	public void testGetHoursPerSubjectPerClass() throws DatabaseException {
		teachu2.getClasses().add(class1a);
		teachu1.getSubjects().add(sub1);
		teachu2.getSubjects().add(sub1);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(5.0,
				DBHandler.getHoursPerSubjectPerClass(sub1, class1a), 0);
		assertEquals(0.0, DBHandler.getHoursPerSubjectPerClass(sub3, class1a), 0);
		teachu1.getSubjects().remove(sub1);
		teachu2.getSubjects().remove(sub1);
		teachu2.getClasses().remove(class1a);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
	}

	@Test
	public void testGetHoursPerSubjectPerTeacher() throws DatabaseException {
		teachu1.getSubjects().add(sub1);
		teachu2.getSubjects().add(sub1);

		PersonTime pt1 = new PersonTime(time6, teach1);
		PersonTime pt2 = new PersonTime(time3, teach1);

		Set<PersonTime> pt0 = new HashSet<PersonTime>();

		teachu1.getPersons().add(pt1);
		teachu2.getPersons().add(pt2);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);

		assertEquals(5.0, DBHandler.getHoursPerSubjectPerTeacher(sub1, teach1),
				0);
		teachu3.getSubjects().add(sub1);
		PersonTime pt3 = new PersonTime(time1, teach1);
		teachu3.getPersons().add(pt3);
		DBHandler.updateTeachingUnit(teachu3);
		assertEquals(7.0, DBHandler.getHoursPerSubjectPerTeacher(sub1, teach1),
				0);

		teachu1.getPersons().remove(pt1);
		teachu2.getPersons().remove(pt2);
		teachu3.getPersons().remove(pt3);
		teachu1.setPersonTime(pt0);
		teachu2.setPersonTime(pt0);
		teachu3.setPersonTime(pt0);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu3);
	}

	@Test
	public void testGetHoursPerSubjectPerTeacherPerClass_1()
			throws DatabaseException {
		teachu2.getSubjects().add(sub1);

		PersonTime pt1 = new PersonTime(time6, teach1);
		PersonTime pt2 = new PersonTime(time3, teach1);

		Set<PersonTime> pt0 = new HashSet<PersonTime>();

		teachu1.getPersons().add(pt1);
		teachu2.getPersons().add(pt2);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);

		assertEquals(3.0, DBHandler.getHoursPerSubjectPerTeacherPerClass(sub1,
				teach1, class1a), 0);
		assertEquals(2.0, DBHandler.getHoursPerSubjectPerTeacherPerClass(sub1,
				teach1, class1b), 0);

		teachu6.getSubjects().add(sub1);
		PersonTime pt3 = new PersonTime(time1, teach1);
		teachu6.getPersons().add(pt3);
		DBHandler.updateTeachingUnit(teachu6);
		assertEquals(4.0, DBHandler.getHoursPerSubjectPerTeacherPerClass(sub1,
				teach1, class1b), 0);

		teachu1.getPersons().remove(pt1);
		teachu2.getPersons().remove(pt2);
		teachu6.getPersons().remove(pt3);
		teachu1.setPersonTime(pt0);
		teachu2.setPersonTime(pt0);
		teachu6.setPersonTime(pt0);
		teachu2.getSubjects().remove(sub1);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu6);
	}

	@Test
	public void testGetHoursPerSubjectPerTeacherPerClass_2()
			throws DatabaseException {
		PersonTime pt3 = new PersonTime(time1, teach2);
		PersonTime pt4 = new PersonTime(time9, teach2);

		Set<PersonTime> pt0 = new HashSet<PersonTime>();

		teachu3.setTimeslot(time1);
		teachu4.setTimeslot(time9);
		teachu4.getSubjects().add(sub3);
		teachu3.getPersons().add(pt3);
		teachu4.getPersons().add(pt4);
		teachu4.getClasses().clear();
		teachu4.getClasses().add(class2a);

		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);

		Double a = DBHandler.getHoursPerSubjectPerClass(sub3, class2a);
		assertEquals(a, DBHandler.getHoursPerSubjectPerTeacherPerClass(sub3, teach2, class2a), 0);

		teachu3.getPersons().remove(pt3);
		teachu4.getPersons().remove(pt4);
		teachu4.getClasses().remove(class2a);
		teachu3.setPersonTime(pt0);
		teachu4.setPersonTime(pt0);
		teachu4.getSubjects().remove(sub3);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);

	}

	@Test
	public void testGetConfig() throws DatabaseException {
		assertTrue(DBHandler.getConfig().equals(config));
	}

	@Test
	public void testAddTeacher() throws DatabaseException {
		assertTrue(DBHandler.getAllTeachers().contains(teach1));
		assertTrue(DBHandler.getAllTeachers().contains(teach2));
		assertTrue(DBHandler.getAllTeachers().contains(teach3));
	}

	@Test
	public void testAddEducEmployee() throws DatabaseException {
		assertTrue(DBHandler.getAllEducEmployees().contains(educ1));
		assertTrue(DBHandler.getAllEducEmployees().contains(educ2));
		assertTrue(DBHandler.getAllEducEmployees().contains(educ3));
	}

	@Test
	public void testAddBreak() throws DatabaseException {
		assertTrue(DBHandler.getAllBreaks().contains(break1));
		assertTrue(DBHandler.getAllBreaks().contains(break2));
		assertTrue(DBHandler.getAllBreaks().contains(break3));
		assertTrue(DBHandler.getAllBreaks().contains(break4));
	}

	@Test
	public void testAddCategory() throws DatabaseException {
		assertTrue(DBHandler.getAllCategories().contains(cat1));
		assertTrue(DBHandler.getAllCategories().contains(cat2));
		assertTrue(DBHandler.getAllCategories().contains(cat3));
		assertTrue(DBHandler.getAllCategories().contains(cat4));
	}

	@Test
	public void testAddClass() throws DatabaseException {
		assertTrue(DBHandler.getAllClasses().contains(class1a));
		assertTrue(DBHandler.getAllClasses().contains(class1b));
		assertTrue(DBHandler.getAllClasses().contains(class2a));
		assertTrue(DBHandler.getAllClasses().contains(class2b));
		assertTrue(DBHandler.getAllClasses().contains(class3a));
		assertTrue(DBHandler.getAllClasses().contains(class3b));
		assertTrue(DBHandler.getAllClasses().contains(class4a));
		assertTrue(DBHandler.getAllClasses().contains(class4b));
	}

	@Test
	public void testAddExternUnit() throws DatabaseException {
		assertTrue(DBHandler.getAllExternUnits().contains(extern1));
		assertTrue(DBHandler.getAllExternUnits().contains(extern2));
		assertTrue(DBHandler.getAllExternUnits().contains(extern3));
		assertTrue(DBHandler.getAllExternUnits().contains(extern4));
	}

	@Test
	public void testAddMeetingUnit() throws DatabaseException {
		assertTrue(DBHandler.getAllMeetingUnits().contains(meet1));
		assertTrue(DBHandler.getAllMeetingUnits().contains(meet2));
		assertTrue(DBHandler.getAllMeetingUnits().contains(meet3));
		assertTrue(DBHandler.getAllMeetingUnits().contains(meet4));
	}

	@Test
	public void testAddTeachingUnit() throws DatabaseException {
		assertTrue(DBHandler.getAllTeachingUnits().contains(teachu1));
		assertTrue(DBHandler.getAllTeachingUnits().contains(teachu2));
		assertTrue(DBHandler.getAllTeachingUnits().contains(teachu3));
		assertTrue(DBHandler.getAllTeachingUnits().contains(teachu4));
		TeachingUnit teachu5 = new TeachingUnit(time5);
		int test = DBHandler.addTeachingUnit(teachu5);
		assertEquals(test, DBHandler.getTeachingUnit(teachu5).getId());
		DBHandler.deleteTeachingUnit(teachu5);
	}

	@Test
	public void testAddRoom() throws DatabaseException {
		assertTrue(DBHandler.getAllRooms().contains(room1));
		assertTrue(DBHandler.getAllRooms().contains(room2));
		assertTrue(DBHandler.getAllRooms().contains(room3));
		assertTrue(DBHandler.getAllRooms().contains(room4));
		assertTrue(DBHandler.getAllRooms().contains(room5));
		assertTrue(DBHandler.getAllRooms().contains(room6));
		assertTrue(DBHandler.getAllRooms().contains(room7));
		assertTrue(DBHandler.getAllRooms().contains(room8));
		assertTrue(DBHandler.getAllRooms().contains(room9));
		assertTrue(DBHandler.getAllRooms().contains(room10));
		assertTrue(DBHandler.getAllRooms().contains(room11));
	}

	@Test
	public void testAddSubject() throws DatabaseException {
		assertTrue(DBHandler.getAllSubjects().contains(sub1));
		assertTrue(DBHandler.getAllSubjects().contains(sub2));
		assertTrue(DBHandler.getAllSubjects().contains(sub3));
		assertTrue(DBHandler.getAllSubjects().contains(sub4));
		assertTrue(DBHandler.getAllSubjects().contains(sub5));
		assertTrue(DBHandler.getAllSubjects().contains(sub6));
		assertTrue(DBHandler.getAllSubjects().contains(sub7));
	}

	@Test
	public void testUpdateHoursPerSubjectYear() throws DatabaseException {
		DBHandler.updateHoursPerSubjectYear(sub1, 1, 40);
		DBHandler.updateHoursPerSubjectYear(sub2, 1, 90);
		assertTrue(40 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub1, 1));
		assertTrue(90 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub2, 1));
		assertTrue(0 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub3, 1));

		assertTrue(0 == DBHandler.getIntendedWorkloadPerSubjectPerYear(sub4, 2));

		DBHandler.getYearsByInt(1).getIntendedWorkload().clear();
		DBHandler.getYearsByInt(2).getIntendedWorkload().clear();
	}

	@Test
	public void testUpdateConfig() throws DatabaseException {
		Config config1 = new Config(DBHandler.getConfig().getTitle(), 20,
				time0, 20, "Backup 15.2", true, 45);
		DBHandler.updateConfig(config1);
		assertTrue(DBHandler.getConfig().equals(config1));
	}

	@Test
	public void testUpdateTeacher() throws DatabaseException {
		assertTrue(teach1.equals(DBHandler.getTeacherByShort("ErK")));
		assertTrue("Erik".equals(DBHandler.getTeacherByShort("ErK")
				.getFirstName()));
		assertTrue("Keshishian".equals(DBHandler.getTeacherByShort("ErK")
				.getLastName()));
		teach1.setFirstName("Erikke");
		teach1.setLastName("Kesh");
		DBHandler.updateTeacher(teach1);
		assertTrue("Erikke".equals(DBHandler.getTeacherByShort("ErK")
				.getFirstName()));
		assertTrue("Kesh".equals(DBHandler.getTeacherByShort("ErK")
				.getLastName()));
	}

	@Test
	public void testUpdateEducEmployee() throws DatabaseException {
		assertTrue(educ1.equals(DBHandler.getEducEmployeeByShort("PhR")));
		assertTrue("Philip".equals(DBHandler.getEducEmployeeByShort("PhR")
				.getFirstName()));
		assertTrue("Rohleder".equals(DBHandler.getEducEmployeeByShort("PhR")
				.getLastName()));
		educ1.setFirstName("Phil");
		educ1.setLastName("Rohrleger");
		DBHandler.updateEducEmployee(educ1);
		assertEquals("Phil",
				(DBHandler.getEducEmployeeByShort("PhR").getFirstName()));
		assertTrue("Rohrleger".equals(DBHandler.getEducEmployeeByShort("PhR")
				.getLastName()));
	}

	@Test
	public void testUpdateBreak() throws DatabaseException {
		assertTrue(break1.getTitle() == DBHandler.getBreak(break1).getTitle());
		break1.setTitle("Pause");
		DBHandler.updateBreak(break1);
		assertTrue(DBHandler.getBreak(break1).getTitle().equals("Pause"));
	}

	@Test
	public void testUpdateClass() throws DatabaseException {
		assertTrue(class1a == DBHandler.getClassByName(class1a.getName()));
		class1a.setYear(3);
		DBHandler.updateClass(class1a);
		assertEquals(3, DBHandler.getClassByName(class1a.getName()).getYear());
	}

	@Test
	public void testUpdateExternUnit() throws DatabaseException {
		assertTrue(extern1 == DBHandler.getExternUnit(extern1));
		extern1.setTitle("Blabla");
		;
		DBHandler.updateExternUnit(extern1);
		assertTrue("Blabla" == DBHandler.getExternUnit(extern1).getTitle());
	}

	@Test
	public void testUpdateMeetingUnit() throws DatabaseException {
		assertEquals(meet1, DBHandler.getMeetingUnit(meet1));
		meet1.setTimeslot(time2);
		DBHandler.updateMeetingUnit(meet1);
		assertEquals(time2, DBHandler.getMeetingUnit(meet1).getTimeslot());
	}

	@Test
	public void testUpdateTeachingUnit() throws DatabaseException {
		assertEquals(teachu1, DBHandler.getTeachingUnit(teachu1));
		teachu1.setTimeslot(time1);
		DBHandler.updateTeachingUnit(teachu1);
		assertEquals(time1, DBHandler.getTeachingUnit(teachu1).getTimeslot());
	}

	@Test
	public void testUpdateRoom() throws DatabaseException {
		assertEquals(room1, DBHandler.getRoomByTitle(room1.getName()));
		room1.setBuilding("Haus am Wald");
		DBHandler.updateRoom(room1);
		assertEquals(room1.getBuilding(),
				DBHandler.getRoomByTitle(room1.getName()).getBuilding());

	}

	@Test
	public void testUpdateSubject() throws DatabaseException {
		assertEquals(sub1, DBHandler.getSubjectByName(sub1.getTitle()));
		assertEquals(DIFFICULTY.HARD,
				DBHandler.getSubjectByName(sub1.getTitle()).getDifficulty());
		sub1.setDifficulty(DIFFICULTY.SOFT);
		DBHandler.updateSubject(sub1);
		assertEquals(DIFFICULTY.SOFT,
				DBHandler.getSubjectByName(sub1.getTitle()).getDifficulty());
	}

	@Test
	public void testGetTeacherByShort() throws DatabaseException {
		assertTrue(teach1.equals(DBHandler.getTeacherByShort("ErK")));
		assertTrue(teach2.equals(DBHandler.getTeacherByShort("AlP")));
		assertTrue(teach3.equals(DBHandler.getTeacherByShort("MaP")));
	}

	@Test
	public void testGetEducEmployeeByShort() throws DatabaseException {
		assertTrue(educ1.equals(DBHandler.getEducEmployeeByShort("PhR")));
		assertTrue(educ2.equals(DBHandler.getEducEmployeeByShort("LaL")));
		assertTrue(educ3.equals(DBHandler.getEducEmployeeByShort("ToL")));
	}

	@Test
	public void testGetCategoryByName() throws DatabaseException {
		assertTrue(cat1.equals(DBHandler.getCategoryByName("Klassenraum")));
		assertTrue(cat2.equals(DBHandler.getCategoryByName("Musikraum")));
		assertTrue(cat3.equals(DBHandler.getCategoryByName("Kunstraum")));
		assertTrue(cat4.equals(DBHandler.getCategoryByName("Sporthalle")));
	}

	@Test
	public void testGetClassByName() throws DatabaseException {
		assertTrue(class1a.equals(DBHandler.getClassByName("1a")));
		assertTrue(class1b.equals(DBHandler.getClassByName("1b")));
		assertTrue(class2a.equals(DBHandler.getClassByName("2a")));
		assertTrue(class2b.equals(DBHandler.getClassByName("2b")));
		assertTrue(class3a.equals(DBHandler.getClassByName("3a")));
		assertTrue(class3b.equals(DBHandler.getClassByName("3b")));
		assertTrue(class4a.equals(DBHandler.getClassByName("4a")));
		assertTrue(class4b.equals(DBHandler.getClassByName("4b")));
	}

	@Test
	public void testGetRoomByTitle() throws DatabaseException {
		assertTrue(room1.equals(DBHandler.getRoomByTitle("Klassenraum 1a")));
		assertTrue(room2.equals(DBHandler.getRoomByTitle("Klassenraum 1b")));
		assertTrue(room3.equals(DBHandler.getRoomByTitle("Klassenraum 2a")));
		assertTrue(room4.equals(DBHandler.getRoomByTitle("Klassenraum 2b")));
		assertTrue(room5.equals(DBHandler.getRoomByTitle("Klassenraum 3a")));
		assertTrue(room6.equals(DBHandler.getRoomByTitle("Klassenraum 3b")));
		assertTrue(room7.equals(DBHandler.getRoomByTitle("Klassenraum 4a")));
		assertTrue(room8.equals(DBHandler.getRoomByTitle("Klassenraum 4b")));
		assertTrue(room9.equals(DBHandler
				.getRoomByTitle("Musikraum Hauptgebaeude")));
		assertTrue(room10.equals(DBHandler
				.getRoomByTitle("Kunstraum Hauptgebaeude")));
		assertTrue(room11.equals(DBHandler
				.getRoomByTitle("Sporthalle Hauptgebaeude")));
	}

	@Test
	public void testGetSubjectByName() throws DatabaseException {
		assertTrue(sub1.equals(DBHandler.getSubjectByName("Mathe")));
		assertTrue(sub2.equals(DBHandler.getSubjectByName("Deutsch")));
		assertTrue(sub3.equals(DBHandler.getSubjectByName("Englisch")));
		assertTrue(sub4.equals(DBHandler.getSubjectByName("Kunst")));
		assertTrue(sub5.equals(DBHandler.getSubjectByName("Musik")));
		assertTrue(sub6.equals(DBHandler.getSubjectByName("Sport")));
		assertTrue(sub7.equals(DBHandler.getSubjectByName("Schwimmen")));
	}

	@Test
	public void testGetTeachingUnit() throws DatabaseException {
		assertEquals(teachu1, DBHandler.getTeachingUnit(teachu1));
		assertEquals(teachu2, DBHandler.getTeachingUnit(teachu2));
		assertEquals(teachu3, DBHandler.getTeachingUnit(teachu3));
		assertEquals(teachu4, DBHandler.getTeachingUnit(teachu4));
		assertEquals(teachu5, DBHandler.getTeachingUnit(teachu5));
	}

	@Test
	public void testgetTimeslotByID() throws DatabaseException {
		assertEquals(time1, DBHandler.getTimeslotByID(time1.getTime_id()));
		assertEquals(time2, DBHandler.getTimeslotByID(time2.getTime_id()));
	}

	@Test
	public void testGetExternUnit() throws DatabaseException {
		assertEquals(extern1, DBHandler.getExternUnit(extern1));
		assertEquals(extern2, DBHandler.getExternUnit(extern2));
		assertEquals(extern3, DBHandler.getExternUnit(extern3));
		assertEquals(extern4, DBHandler.getExternUnit(extern4));
		assertEquals(null, DBHandler.getExternUnit(extern5));
	}

	@Test
	public void testGetMeetingUnit() throws DatabaseException {
		assertEquals(meet1, DBHandler.getMeetingUnit(meet1));
		assertEquals(meet2, DBHandler.getMeetingUnit(meet2));
		assertEquals(meet3, DBHandler.getMeetingUnit(meet3));
		assertEquals(meet4, DBHandler.getMeetingUnit(meet4));
		assertEquals(null, DBHandler.getMeetingUnit(meet5));
	}

	public void testGetBreakUnit() throws DatabaseException {
		assertEquals(breaku1, DBHandler.getBreakUnit(breaku1));
		assertEquals(breaku2, DBHandler.getBreakUnit(breaku2));
		assertEquals(breaku3, DBHandler.getBreakUnit(breaku3));
		assertEquals(breaku4, DBHandler.getBreakUnit(breaku4));
		assertEquals(breaku5, DBHandler.getBreakUnit(breaku5));
		assertEquals(breaku6, DBHandler.getBreakUnit(breaku6));
	}

	@Test
	public void testGetBreak() throws DatabaseException {
		assertEquals(break1, DBHandler.getBreak(break1));
		assertEquals(break2, DBHandler.getBreak(break2));
		assertEquals(break3, DBHandler.getBreak(break3));
		assertEquals(break4, DBHandler.getBreak(break4));
		assertEquals(null, DBHandler.getBreak(break6));
	}

	@Test
	public void testCheckConflictClass_Teaching() throws DatabaseException {
		teachu2.getClasses().add(class1a);
		teachu2.getClasses().add(class1b);
		teachu2.setTimeslot(time6);
		teachu2.getRooms().add(room1);
		teachu2.getSubjects().add(sub1);

		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictClass(class1a, time6));
	}

	@Test
	public void testCheckConflictClass_Break() throws DatabaseException {
		Timeslot timea = new Timeslot(20, WEEKDAYS.MONDAY, "09", "00");
		Timeslot timeb = new Timeslot(90, WEEKDAYS.MONDAY, "08", "50");
		Timeslot timec = new Timeslot(45, WEEKDAYS.MONDAY, "18", "30");

		break1.setYear(2);
		break2.setYear(2);
		break1.setTimeslot(timea);
		break2.setTimeslot(timeb);
		break1.getTimeslot().setStarttime();
		break2.getTimeslot().setStarttime();
		break1.getTimeslot().setEndTime();
		break2.getTimeslot().setEndTime();

	
		DBHandler.updateBreak(break2);
		DBHandler.updateBreak(break1);

		assertEquals(true, DBHandler.checkConflictClass(class2a, timea));
		assertEquals(false, DBHandler.checkConflictClass(class2a, timec));
	}

	@Test
	public void testcheckConflictPlannedWeekdays() throws DatabaseException {
		// Testet, ob es einen Konflikt gibt, ob bei der Belegung von
		// plannedWeekdays Units wegfallen.
		DBHandler.updateConfig(config);
		Collection<WEEKDAYS> coll = new ArrayList<WEEKDAYS>();
		coll.add(WEEKDAYS.MONDAY);
		coll.add(WEEKDAYS.TUESDAY);
		coll.add(WEEKDAYS.FRIDAY);
		config.setPlannedDays(coll);
		DBHandler.updateConfig(config);
		assertTrue(DBHandler.checkConflictPlannedWeekdays());
		coll.add(WEEKDAYS.WEDNESDAY);
		coll.add(WEEKDAYS.THURSDAY);
		config.setPlannedDays(coll);
		DBHandler.updateConfig(config);
		assertFalse(DBHandler.checkConflictPlannedWeekdays());
	}

	@Test
	public void testCheckConflictPerson_Teaching() throws DatabaseException {
		PersonTime pt1 = new PersonTime(time10, teach1);
		PersonTime pt2 = new PersonTime(time10, teach1);
		Set<PersonTime> pt0 = new HashSet<PersonTime>();

		teachu1.getPersons().add(pt1);
		teachu1.setTimeslot(time10);
		teachu2.getPersons().add(pt2);
		teachu2.setTimeslot(time10);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(2, DBHandler.getPersonTimeByPerson(teach1).size());
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));

		teachu2.setTimeslot(time11);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));

		teachu2.setTimeslot(time12);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));
		teachu1.getPersons().remove(pt1);
		teachu2.getPersons().remove(pt2);
		teachu1.setPersonTime(pt0);
		teachu2.setPersonTime(pt0);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.deletePersonTime(pt1);
		DBHandler.deletePersonTime(pt2);
	}

	@Test
	public void testCheckConflictPerson_Meeting() throws DatabaseException {
		meet1.getMembers().add(teach1);
		meet1.setTimeslot(time10);
		meet2.getMembers().add(teach1);
		meet2.setTimeslot(time10);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet2);
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));

		meet2.setTimeslot(time11);
		DBHandler.updateMeetingUnit(meet2);
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));

		meet2.setTimeslot(time12);
		DBHandler.updateMeetingUnit(meet2);
		assertEquals(true, DBHandler.checkConflictPerson(teach1, time10));
		meet1.getMembers().remove(teach1);
		meet2.getMembers().remove(teach1);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet2);
	}

	@Test
	public void testCheckConflictRoom_Teaching() throws DatabaseException {
		teachu1.getRooms().add(room1);
		teachu1.setTimeslot(time10);
		teachu2.getRooms().add(room1);
		teachu2.setTimeslot(time10);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		teachu2.setTimeslot(time11);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		teachu2.setTimeslot(time12);
		DBHandler.updateTeachingUnit(teachu2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));
	}

	@Test
	public void testCheckConflictTimeFrameByTimeslot() throws DatabaseException {
		Timeslot t0 = new Timeslot(700, WEEKDAYS.NONE, "07", "49");
		Timeslot t1 = new Timeslot(200, WEEKDAYS.NONE, "08", "00");
		Timeslot t2 = new Timeslot(60, WEEKDAYS.NONE, "16", "00");
		Timeslot t3 = new Timeslot(1000, WEEKDAYS.NONE, "07", "00");
		assertFalse(DBHandler.checkConflictTimeFrameByTimeslot(t0));
		assertFalse(DBHandler.checkConflictTimeFrameByTimeslot(t3));
		assertTrue(DBHandler.checkConflictTimeFrameByTimeslot(t1));
		assertTrue(DBHandler.checkConflictTimeFrameByTimeslot(t2));
	}

	@Test
	public void testCheckTravelTimePerPersonPerDay() throws DatabaseException {
		Timeslot time8 = new Timeslot(15, WEEKDAYS.THURSDAY, "11", "15");
		Timeslot time7 = new Timeslot(30, WEEKDAYS.THURSDAY, "11", "30");

		PersonTime pt1 = new PersonTime(time8, teach1);
		PersonTime pt2 = new PersonTime(time7, teach1);

		Set<Room> setMain = new HashSet<Room>();
		Set<Room> setDep = new HashSet<Room>();
		Set<Room> set0 = new HashSet<Room>();
		setMain.add(room3);
		setDep.add(room11);

		teachu1.setTimeslot(time8);
		teachu1.setRooms(setMain);
		teachu1.getPersons().add(pt1);

		teachu2.setTimeslot(time7);
		teachu2.setRooms(setDep);
		teachu2.getPersons().add(pt2);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		assertTrue(DBHandler.checkTravelTimePerPersonPerDay(teach1,
				WEEKDAYS.THURSDAY));
		assertTrue(DBHandler.getTeachingUnit(teachu1).getTravelNeed());
		teachu1.setRooms(set0);
		teachu2.setRooms(set0);
		teachu1.getPersons().remove(pt1);
		teachu2.getPersons().remove(pt2);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
	}

	@Test
	public void testCheckTravelTimePerClassPerDay() throws DatabaseException {
		Timeslot time8 = new Timeslot(15, WEEKDAYS.THURSDAY, "11", "15");
		Timeslot time7 = new Timeslot(30, WEEKDAYS.THURSDAY, "11", "30");
		
		Set<Room> setMain = new HashSet<Room>();
		Set<Room> setDep = new HashSet<Room>();
		Set<Room> set0 = new HashSet<Room>();
		setMain.add(room3);
		setDep.add(room11);

		teachu1.setTimeslot(time8);
		teachu1.setRooms(setMain);
		teachu1.getClasses().add(class3a);

		teachu2.setTimeslot(time7);
		teachu2.setRooms(setDep);
		teachu2.getClasses().add(class3a);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		assertTrue(DBHandler.checkTravelTimePerClassPerDay(class3a,
				WEEKDAYS.THURSDAY));
		assertTrue(DBHandler.getTeachingUnit(teachu1).getTravelNeed());
		teachu1.setRooms(set0);
		teachu2.setRooms(set0);
		teachu1.getClasses().remove(class3a);
		teachu2.getClasses().remove(class3a);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		
	}
	
	@Test
	public void testCheckConflictRoom_Extern() throws DatabaseException {
		extern1.getRooms().add(room1);
		extern1.setTimeslot(time10);
		extern2.getRooms().add(room1);
		extern2.setTimeslot(time10);
		DBHandler.updateExternUnit(extern1);
		DBHandler.updateExternUnit(extern2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		extern2.setTimeslot(time11);
		DBHandler.updateExternUnit(extern2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		extern2.setTimeslot(time12);
		DBHandler.updateExternUnit(extern2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));
	}

	@Test
	public void testCheckConflictRoom_Break() throws DatabaseException {
		break1.getRooms().add(room1);
		break1.setTimeslot(time10);
		break2.getRooms().add(room1);
		break2.setTimeslot(time10);
		DBHandler.updateBreak(break1);
		DBHandler.updateBreak(break2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		break2.setTimeslot(time11);
		DBHandler.updateBreak(break2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		break2.setTimeslot(time12);
		DBHandler.updateBreak(break2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));
		break1.getRooms().clear();
		break2.getRooms().clear();
		DBHandler.updateBreak(break1);
		DBHandler.updateBreak(break2);
	}

	@Test
	public void testCheckConflictRoom_BreakUnit() throws DatabaseException {
		breaku1.getRooms().add(room1);
		breaku1.setTimeslot(time10);
		breaku2.getRooms().add(room1);
		breaku2.setTimeslot(time10);
		DBHandler.updateBreakUnit(breaku1);
		DBHandler.updateBreakUnit(breaku2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		breaku2.setTimeslot(time11);
		DBHandler.updateBreakUnit(breaku2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		breaku2.setTimeslot(time12);
		DBHandler.updateBreakUnit(breaku2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));
		breaku1.getRooms().clear();
		breaku2.getRooms().clear();
		DBHandler.updateBreakUnit(breaku1);
		DBHandler.updateBreakUnit(breaku2);
	}

	@Test
	public void testCheckConflictRoom_Meeting() throws DatabaseException {
		meet1.getRooms().add(room1);
		meet1.setTimeslot(time10);
		meet2.getRooms().add(room1);
		meet2.setTimeslot(time10);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet2);
		
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		meet2.setTimeslot(time11);
		DBHandler.updateMeetingUnit(meet2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));

		meet2.setTimeslot(time12);
		DBHandler.updateMeetingUnit(meet2);
		assertEquals(true, DBHandler.checkConflictRoom(room1, time10));
	}

	public void testgetWeekTableByPerson_Teaching() throws DatabaseException {
		Collection<Unit> collUnit = new ArrayList<Unit>();
		Set<Class> set1 = new HashSet<Class>();
		set1.add(class1a);
		Set<Class> set2 = new HashSet<Class>();
		set2.add(class2b);
		Set<PersonTime> setPerT0 = new HashSet<PersonTime>();

		teachu1.setClasses(set1);

		PersonTime pt1 = new PersonTime(time1, teach1);
		teachu1.getPersons().add(pt1);

		PersonTime pt2 = new PersonTime(time1, teach1);
		teachu2.getPersons().add(pt2);

		PersonTime pt3 = new PersonTime(time1, teach1);
		teachu3.getPersons().add(pt3);

		PersonTime pt4 = new PersonTime(time1, teach1);
		teachu4.getPersons().add(pt4);

		collUnit.add(teachu1);
		collUnit.add(teachu2);
		collUnit.add(teachu3);
		collUnit.add(teachu4);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);

		assertEquals(collUnit, DBHandler.getWeekTableByPerson(teach1));
		teachu1.getPersons().remove(pt1);
		teachu2.getPersons().remove(pt2);
		teachu3.getPersons().remove(pt3);
		teachu4.getPersons().remove(pt4);
		teachu1.setPersonTime(setPerT0);
		teachu2.setPersonTime(setPerT0);
		teachu3.setPersonTime(setPerT0);
		teachu4.setPersonTime(setPerT0);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu2);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateTeachingUnit(teachu4);
	}

	@Test
	public void testgetWeekTableByPerson_Meeting() throws DatabaseException {
		Collection<Unit> collUnit = new ArrayList<Unit>();
		Set<Person> setPer1 = new HashSet<Person>();
		setPer1.add(teach1);

		meet1.setYear(1);
		meet2.setYear(1);
		meet3.setYear(1);
		meet4.setYear(1);

		collUnit.add(meet1);
		collUnit.add(meet2);
		collUnit.add(meet3);
		collUnit.add(meet4);

		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet2);
		DBHandler.updateMeetingUnit(meet3);
		DBHandler.updateMeetingUnit(meet4);

		assertTrue(DBHandler.getWeekTableByPerson(teach1).containsAll(collUnit));
		assertTrue(collUnit.containsAll(DBHandler.getWeekTableByPerson(teach1)));
		
		meet1.setYear(2);
		meet2.setYear(3);
		meet3.setYear(4);
		meet4.setYear(1);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet2);
		DBHandler.updateMeetingUnit(meet3);
		DBHandler.updateMeetingUnit(meet4);
	}

	@Test
	public void testGetWeekTableByClass() throws DatabaseException {
		Collection<Unit> collUnit1 = new ArrayList<Unit>();
		Collection<Unit> collUnit2 = new ArrayList<Unit>();

		collUnit1.add(break1);
		collUnit1.add(extern1);
		collUnit1.add(teachu1);
		collUnit1.add(breaku1);
		collUnit2.add(break2);
		collUnit2.add(extern4);
		collUnit2.add(teachu4);
		collUnit2.add(breaku4);

		assertEquals(true,
				DBHandler.getWeekTableByClass(class1a).containsAll(collUnit1));
		assertEquals(true,
				DBHandler.getWeekTableByClass(class2b).containsAll(collUnit2));
	}

	@Test
	public void testGetWeekTableByRoom() throws DatabaseException {
		Collection<Unit> collUnit1 = new ArrayList<Unit>();
		Collection<Unit> collUnit2 = new ArrayList<Unit>();
		Collection<Unit> collUnit3 = new ArrayList<Unit>();
		Collection<Unit> collUnit4 = new ArrayList<Unit>();

		collUnit1.add(break1);
		collUnit1.add(breaku1);
		collUnit1.add(extern1);
		collUnit1.add(teachu1);
		collUnit1.add(meet1);

		collUnit2.add(break2);
		collUnit2.add(teachu2);
		collUnit2.add(extern2);
		collUnit2.add(breaku2);
		collUnit2.add(meet2);

		collUnit3.add(break3);
		collUnit3.add(teachu3);
		collUnit3.add(extern3);
		collUnit3.add(breaku3);
		collUnit3.add(meet3);

		collUnit4.add(break4);
		collUnit4.add(teachu4);
		collUnit4.add(extern4);
		collUnit4.add(breaku4);
		collUnit4.add(meet4);
		assertEquals(true,
				DBHandler.getWeekTableByRoom(room1).containsAll(collUnit1));
		assertEquals(true,
				DBHandler.getWeekTableByRoom(room2).containsAll(collUnit2));
		assertEquals(true,
				DBHandler.getWeekTableByRoom(room3).containsAll(collUnit3));
		assertEquals(true,
				DBHandler.getWeekTableByRoom(room4).containsAll(collUnit4));
	}

	@Test
	public void testGetDayTablePerTeacher() throws DatabaseException {
		Collection<Unit> collUnit1 = new ArrayList<Unit>();
		Collection<Unit> collUnit2 = new ArrayList<Unit>();

		PersonTime pt1 = new PersonTime(time1, teach1);
		PersonTime pt2 = new PersonTime(time3, teach1);

		Set<Person> SetPer0 = new HashSet<Person>();
		Set<Person> SetPer1 = new HashSet<Person>();
		Set<PersonTime> SetPerT1 = new HashSet<PersonTime>();
		SetPer1.add(teach1);

		teachu1.setTimeslot(time1);
		teachu3.setTimeslot(time3);
		teachu1.getPersons().add(pt1);
		teachu3.getPersons().add(pt2);

		meet1.setTimeslot(time1);
		meet3.setTimeslot(time3);
		meet1.setMembers(SetPer1);
		meet3.setMembers(SetPer1);

		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet3);

		collUnit1.add(teachu1);
		collUnit1.add(meet1);
		collUnit2.add(teachu3);
		collUnit2.add(meet3);

		assertEquals(collUnit1,
				DBHandler.getDayTablePerTeacher(WEEKDAYS.MONDAY, teach1));
		assertEquals(true,
				DBHandler.getDayTablePerTeacher(WEEKDAYS.TUESDAY, teach1)
						.containsAll(collUnit2));

		meet1.setMembers(SetPer0);
		meet3.setMembers(SetPer0);
		teachu1.getPersons().remove(pt1);
		teachu3.getPersons().remove(pt2);
		teachu1.setPersonTime(SetPerT1);
		teachu3.setPersonTime(SetPerT1);
		DBHandler.updateTeachingUnit(teachu1);
		DBHandler.updateTeachingUnit(teachu3);
		DBHandler.updateMeetingUnit(meet1);
		DBHandler.updateMeetingUnit(meet3);
	}

	@Test
	public void testGetAllWarnings() throws DatabaseException {
		assertEquals(6, DBHandler.getAllWarnings().size());
	}

	@Test
	public void testAdd_DeleteWarning() throws DatabaseException {
		Warning warna1 = new Warning(1, "Ein Fehler ist aufgetreten");
		Warning warna2 = new Warning(2, "Was ist passiert");

		DBHandler.addWarning(warna1);

		assertTrue(DBHandler.getAllWarnings().contains(warna1));
		assertFalse(DBHandler.getAllWarnings().contains(warna2));

		DBHandler.deleteWarning(warna1);

		assertTrue(!DBHandler.getAllWarnings().contains(warna1));
	}

	@Test
	public void testAdd_DeleteOverloadWarning() throws DatabaseException {
		OverloadWarning warnb1 = new OverloadWarning("Überladung warnb1",
				teach2);
		OverloadWarning warnb2 = new OverloadWarning("Überladung warnb2",
				class1a);

		DBHandler.addOverloadWarning(warnb1);

		assertTrue(DBHandler.getAllWarnings().contains(warnb1));
		assertFalse(DBHandler.getAllWarnings().contains(warnb2));

		DBHandler.deleteOverloadWarning(warnb1);

		assertTrue(!DBHandler.getAllWarnings().contains(warnb1));
	}

	@Test
	public void testAdd_DeleteMultipleReservationWarning()
			throws DatabaseException {
		MultipleReservationWarning warnc1 = new MultipleReservationWarning(
				"Multiplereservierung warc1", time4, class1b);
		MultipleReservationWarning warnc2 = new MultipleReservationWarning(
				"Multiplereservierung warnc2", time5, teach2);
		MultipleReservationWarning warnc3 = new MultipleReservationWarning(
				"Multiplereservierung warnc3", time6, room5);

		DBHandler.addMultipleReservationWarning(warnc1);
		DBHandler.addMultipleReservationWarning(warnc2);

		assertTrue(DBHandler.getAllWarnings().contains(warnc1));
		assertTrue(DBHandler.getAllWarnings().contains(warnc2));
		assertFalse(DBHandler.getAllWarnings().contains(warnc3));

		DBHandler.deleteMultipleReservationWarning(warnc2);
		DBHandler.deleteMultipleReservationWarning(warnc1);

		assertTrue(!DBHandler.getAllWarnings().contains(warnc1));
		assertTrue(!DBHandler.getAllWarnings().contains(warnc2));
	}

	@Test
	public void testGetWarningByAttribute() throws DatabaseException {
		assertTrue(warn1 == DBHandler.getWarningByAttribute(warn1));
		assertTrue(warn2 == DBHandler.getWarningByAttribute(warn2));
		assertTrue(warn2b == DBHandler.getWarningByAttribute(warn2b));
		assertTrue(warn3 == DBHandler.getWarningByAttribute(warn3));
		assertTrue(warn3b == DBHandler.getWarningByAttribute(warn3b));
		assertTrue(warn3c == DBHandler.getWarningByAttribute(warn3c));
		assertTrue(null == DBHandler.getWarningByAttribute(warn1b));
	}

	@Test
	public void testGetOverloadWarningByAttribute() throws DatabaseException {
		assertTrue(warn2 == DBHandler.getOverloadWarningByAttribute(warn2));
		assertTrue(warn2b == DBHandler.getOverloadWarningByAttribute(warn2b));
	}

	@Test
	public void testGetMultipleReservationWarningByAttribute()
			throws DatabaseException {
		assertEquals(warn3, DBHandler
				.getMultipleReservationWarningByAttribute(warn3));
		assertEquals(warn3b, DBHandler
				.getMultipleReservationWarningByAttribute(warn3b));
		assertEquals(warn3c, DBHandler
				.getMultipleReservationWarningByAttribute(warn3c));
	}

	@Test
	public void testGetIntendedWorkloadPerSubjectPerYear() throws DatabaseException {
		DBHandler.getConfig().addIntendedWorkload(1, sub1, 2);
		assertEquals(2, DBHandler.getIntendedWorkloadPerSubjectPerYear(sub1, 1));
		assertEquals(0, DBHandler.getIntendedWorkloadPerSubjectPerYear(sub1, 2));
		DBHandler.getConfig().addIntendedWorkload(2, sub2, 3);
		assertEquals(3, DBHandler.getIntendedWorkloadPerSubjectPerYear(sub2, 2));
		DBHandler.getConfig().addIntendedWorkload(1, sub1, 0);
		DBHandler.getConfig().addIntendedWorkload(2, sub2, 0);
		assertEquals(0, DBHandler.getIntendedWorkloadPerSubjectPerYear(sub3, 1));
	}

	@Test
	public void testGetIntendedWorkloadPerSubjectPerClass()
			throws DatabaseException {
		DBHandler.getConfig().addIntendedWorkload(1, sub1, 2);
		class1a.setHours(sub1, 3);
		DBHandler.updateClass(class1a);
		assertEquals(3,
				DBHandler.getIntendedWorkloadPerSubjectPerClass(sub1, class1a));
		assertEquals(2,
				DBHandler.getIntendedWorkloadPerSubjectPerClass(sub1, class1b));
		DBHandler.getConfig().addIntendedWorkload(1, sub1, 0);
		class1a.deleteIntendedWorkloadForSubject(sub1);
		DBHandler.updateClass(class1a);
		assertEquals(0,
				DBHandler.getIntendedWorkloadPerSubjectPerClass(sub1, class1a));
		assertEquals(0,
				DBHandler.getIntendedWorkloadPerSubjectPerClass(sub1, class1b));
		
		
	}

}
