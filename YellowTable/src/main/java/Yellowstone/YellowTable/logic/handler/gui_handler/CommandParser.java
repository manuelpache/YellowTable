package Yellowstone.YellowTable.logic.handler.gui_handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.Category;
import Yellowstone.YellowTable.logic.components.Config;
import Yellowstone.YellowTable.logic.components.DIFFICULTY;
import Yellowstone.YellowTable.logic.components.ExternUnit;
import Yellowstone.YellowTable.logic.components.Room;
import Yellowstone.YellowTable.logic.components.Subject;
import Yellowstone.YellowTable.logic.components.Teacher;
import Yellowstone.YellowTable.logic.components.Timeslot;
import Yellowstone.YellowTable.logic.components.WEEKDAYS;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.warnings.Warner;

/**
 * Anlaufstelle fuer Anfragen der GUI. Ueber diese Schnittstelle werden Befehle
 * ausgefuehrt, die ueber das blosse Laden von Daten hinausgehen (siehe dazu
 * {@linkplain GUIHandler}).
 * 
 * Das eigentliche Parsen findet nicht in dieser Klasse statt: Hier werden die
 * Informationen aus der GUI in ein einheitliches Format gebracht, welches
 * wiederum von den konkreten {@linkplain Command}-Klassen geparst wird.
 * 
 * @author prohleder, apag
 *
 */
public class CommandParser {

	/**
	 * Die globale History in der die Befehle gespeichert werden.
	 */
	private static final CommandHistory HISTORY = new CommandHistory();

	/* Allgemein */
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";

	/* Aus ROOT */
	public static final String ROOM = "ROOM";
	public static final String SUBJECT = "SUBJECT";
	public static final String TEACHER = "TEACHER";
	public static final String EDUC = "EDUC";
	public static final String YEARINFO = "YEARINFO";
	public static final String CLASS = "CLASS";
	public static final String CLASSINFO = "CLASSINFO";
	public static final String EXTERN = "EXTERN";

	/* Aus CLASSVIEW */
	public static final String TEACHING = "TEACHING";
	public static final String TEACHPERS = "TEACHPERS";

	/* Aus DEFAULT */
	public static final String BACKUP = "BACKUP";
	public static final String CONFIG = "CONFIG";
	public static final String BREAK = "BREAK";
	public static final String MEETING = "MEETING";
	public static final String BUILDING = "BUILDING";
	public static final String CATEGORY = "CATEGORY";
	public static final String STYLE = "STYLE";

	/* Indizes */

	/**
	 * Anzahl der Eintraege, die das gegebene String-Array min. enthalten muss.
	 */
	static final int MIN_SIZE = 2;

	/*
	 * Minimal erforderliche Anzahl an Informationen fuer das jeweilige Objekt.
	 * Der erste Eintrag zur Bestimmung der Art des Objekts wird hier nicht
	 * beruecksichtigt.
	 */
	static final int TEACHER_MIN = 6;
	static final int EDUC_MIN = 5;
	static final int ROOM_MIN = 4;
	static final int CLASS_MIN = 3;
	static final int EXTERN_MIN = 7;
	static final int SUBJECT_MIN = 4;
	static final int TEACHING_MIN = 9;
	static final int YEARINFO_MIN = 3;
	static final int TP_MIN = 6;
	static final int CLAINF_MIN = 3;

	static final int BACKUP_MIN = 3;
	static final int CONFIG_MIN = 7;
	static final int BREAK_MIN = 8;
	static final int MEETING_MIN = 7;
	static final int BUILDING_MIN = 2;
	static final int CAT_MIN = 1;
	static final int STYLE_MIN = 1;

	/*
	 * Feste Indizes bei TEACHER und EDUC
	 */
	static final int PERS_SHORT = 1;
	static final int PERS_FIRST = 2;
	static final int PERS_LAST = 3;
	static final int PERS_WORK = 4;
	static final int PERS_MINUS = 5;

	/*
	 * Feste Indizes bei TEACHER
	 */
	static final int TEACHER_YEARMEET = 6;
	static final int TEACHER_NUMSUBS = 7;

	/*
	 * Feste Indizes bei EDUC
	 */
	static final int EDUC_NUMSUBS = 6;

	/*
	 * Feste Indizes bei ROOM
	 */
	static final int ROOM_TITLE = 1;
	static final int ROOM_BUILDING = 2;
	static final int ROOM_CATEGORY = 3;
	static final int ROOM_NUMBER = 4;

	/*
	 * Feste Indizes bei SUBJECT
	 */
	static final int SUB_TITLE = 1;
	static final int SUB_DIFF = 2;
	static final int SUB_CATEGORY = 3;
	static final int SUB_DURATION = 4;

	/*
	 * Feste Indizes bei CLASS
	 */
	static final int CLASS_TITLE = 1;
	static final int CLASS_YEAR = 2;
	static final int CLASS_ROOM = 3;

	/*
	 * Feste Indizes bei CLASSINFO
	 */
	static final int CLASSINFO_CLASS = 1; // year + title
	static final int CLASSINFO_SUB = 2;
	static final int CLASSINFO_WORK = 3;

	/*
	 * Feste Indizes bei CATEGORY
	 */
	static final int CAT_NAME = 1;

	/*
	 * Feste Indizes bei BUILDING
	 */
	static final int BUILD_NAME = 1;
	static final int BUILD_DIST = 2;

	/*
	 * Feste Indizes bei YEARINFO
	 */
	static final int YEAR_YEAR = 1;
	static final int YEAR_SUB = 2;
	static final int YEAR_WORK = 3;

	/*
	 * Feste Indizes bei EXTERN, MEETING, TEACHING und BREAK (ausser day)
	 */
	static final int UNIT_ID = 1;
	static final int UNIT_DURATION = 2;
	static final int UNIT_STARTHR = 3;
	static final int UNIT_STARTMIN = 4;
	static final int UNIT_DAY = 5;

	/*
	 * Feste Indizes bei BREAK
	 */
	static final int BREAK_TITLE = 5;
	static final int BREAK_YEAR = 6;
	static final int BREAK_NUMDAY = 7;

	/*
	 * Feste Indizes bei EXTERN
	 */
	static final int EXT_NUMCLASS = 6;
	static final int EXT_TITLE = 7; // von gui nicht unterstuetzt.

	/*
	 * Feste Indizes bei MEETING
	 */
	static final int MEET_TITLE = 6;
	static final int MEET_YEAR = 7;

	/*
	 * Feste Indizes bei TEACHING
	 */
	static final int TEACH_NUMCLASS = 6;
	static final int TEACH_NUMROOMS = 7;
	static final int TEACH_NUMSUBS = 8;

	/*
	 * Feste Indizes bei BACKUP
	 */
	static final int BAC_TIMER = 1;
	static final int BAC_TITLE = 2;
	static final int BAC_ONCLOSE = 3;

	/*
	 * Feste Indizes bei CONFIG
	 */
	static final int CON_STARTHR = 1;
	static final int CON_STARTMIN = 2;
	static final int CON_ENDHR = 3;
	static final int CON_ENDMIN = 4;
	static final int CON_STDDURATION = 5;
	static final int CON_GRANULARITY = 6;
	static final int CON_NUMDAY = 7;
	static final int CON_TITLE = 8;

	/*
	 * Feste Indizes bei STYLE
	 */
	static final int STYLE_NAME = 1;

	/*
	 * Feste Indizes bei TEACHPERS
	 */
	static final int TP_SHORT = 1;
	static final int TP_UID = 2;
	static final int TP_STHR = 3;
	static final int TP_STMN = 4;
	static final int TP_ENDHR = 5;
	static final int TP_ENDMN = 6;

	/**
	 * Hinzufuegen eines neuen Eintrages. Das koennen Personen, Pausen oder
	 * Aehnliches sein. Der erste Eintrag des Arrays muss dabei die Art des
	 * Eintrages festlegen, z.B. bedeutet der Eintrag "TEACHER" an erster
	 * Stelle, dass ein neuer Lehrer hinzugefuegt werden soll. Die restlichen
	 * Eintraege enthalten alle relevanten Informationen in der festgelegten
	 * Reihenfolge (siehe oben).
	 * 
	 * Dieser Befehl muss in die {@linkplain CommandHistory} eingetragen werden.
	 * 
	 * @param pArray
	 *            enthaelt Informationen ueber Art und Eigenschaften des
	 *            hinzuzufuegenden Eintrages
	 */
	public static void add(final String[] pArray) {
		Add addCommand = new Add(pArray);
		HISTORY.execute(addCommand);
		System.out
				.println("WARNER SIZE: " + Warner.WARNER.getWarnings().size());
		Warner.WARNER.checkAll();
	}

	/**
	 * Editieren eines bestehenden Eintrags. Wie bei {@linkplain #add(String[])}
	 * muessen hier alle Informationen in der richtigen Reihenfolge uebergeben
	 * werden, wobei zu aendernde Eigenschaften durch die neuen Werte zu
	 * ersetzen sind.
	 * 
	 * Dieser Befehl muss in die {@linkplain CommandHistory} eingetragen werden
	 * (ausser fuer Planungseinheiten?).
	 * 
	 * @param pArray
	 *            enthaelt alle Informationen, die der Eintrag nun besitzen
	 *            soll.
	 */
	public static void edit(final String[] pArray) {
		Edit editCommand = new Edit(pArray);
		HISTORY.execute(editCommand);
		System.out
				.println("WARNER SIZE: " + Warner.WARNER.getWarnings().size());
		Warner.WARNER.checkAll();
	}

	/**
	 * Entfernt den durch das Array spezifizierten Eintrag.
	 * 
	 * Dieser Befehl muss in die {@linkplain CommandHistory} eingetragen werden.
	 * 
	 * @param pArray
	 */
	public static void remove(final String[] pArray) {
		Remove remCommand = new Remove(pArray);
		HISTORY.execute(remCommand);
		Warner.WARNER.checkAll();
	}

	/**
	 * Funktioniert im Grunde wie {@linkplain #add(String[])}, nur dass der
	 * Befehl nicht in die {@linkplain CommandHistory} eingetragen werden muss.
	 * 
	 * @param pArray
	 */
	public static void addDefaults(final String[] pArray) {
		AddDefault addDefault = new AddDefault(pArray);
		addDefault.execute();
		Warner.WARNER.checkAll();
	}

	/**
	 * Funktioniert im Grunde wie {@linkplain #remove(String[])}, nur dass der
	 * Befehl nicht in die {@linkplain CommandHistory} eingetragen werden muss.
	 * 
	 * @param pArray
	 */
	public static void removeDefaults(final String[] pArray) {
		RemoveDefault remDefault = new RemoveDefault(pArray);
		remDefault.execute();
		Warner.WARNER.checkAll();
	}

	/**
	 * Funktioniert im Grude wie {@linkplain #edit(String[])}, nur dass der
	 * Befehl nicht in die {@linkplain CommandHistory} eingetragen werden muss.
	 * 
	 * @param pArray
	 */
	public static void editDefaults(final String[] pArray) {
		EditDefault editDefault = new EditDefault(pArray);
		editDefault.execute();
		Warner.WARNER.checkAll();
	}

	/**
	 * Setzt die CommandHistory komplett zurueck.
	 */
	public static void resetHistory() {
		HISTORY.reset();
	}

	/**
	 * Macht den letzten Befehl rueckgaengig.
	 */
	public static void undo() {
		HISTORY.undo();
		Warner.WARNER.checkAll();
	}

	/**
	 * Stellt den letzten Befehl wieder her.
	 */
	public static void redo() {
		HISTORY.redo();
		Warner.WARNER.checkAll();
	}

	/**
	 * Hilfsfunktion, die zu einem booleschen Wert die entsprechende
	 * String-Konstante zurueckgibt.
	 * 
	 * @param pBool
	 *            der boolesche Wert
	 * @return die entsprechende String-Konstante
	 */
	public static String fromBool(final boolean pBool) {
		if (pBool) {
			return TRUE;
		}
		return FALSE;
	}

	/**
	 * Wird noch vor Start der GUI aufgerufen, um sicherzustellen, das ein
	 * initiales Config-Objekt vorhanden ist.
	 * 
	 * @param pTitle
	 *            der Titel der Config
	 */
	public static void init(final String pTitle) {

		if (DBHandler.getConfig() != null) {
			// nix zu tun...
			return;
		}
		// Neu erstellen mit Standardwerten
		Map<String, Integer> buildings = new HashMap<>();
		buildings.put("Stammschule", 0);
		buildings.put("Dependance", 20);

		Category c1 = new Category("Sporthalle");
		Category c2 = new Category("Musikraum");
		Category c3 = new Category("Standard");
		Set<Category> categories = new HashSet<>();
		categories.add(c1);
		categories.add(c2);
		categories.add(c3);

		Set<WEEKDAYS> plannedDays = new HashSet<>();
		plannedDays.add(WEEKDAYS.MONDAY);
		plannedDays.add(WEEKDAYS.TUESDAY);
		plannedDays.add(WEEKDAYS.WEDNESDAY);
		plannedDays.add(WEEKDAYS.THURSDAY);
		plannedDays.add(WEEKDAYS.FRIDAY);

		int backUpTimer = 5;
		String backUpTitle = "backup";
		boolean backUpOnClose = false;
		int stdDuration = 45;
		int granularity = 15;

		Timeslot ts = new Timeslot(600, WEEKDAYS.NONE, "07", "00");

		Config conf = new Config(pTitle, granularity, ts, backUpTimer,
				backUpTitle, backUpOnClose, stdDuration);

		conf.setBuildings(buildings);
		conf.setCategories(categories);
		conf.setPlannedDays(plannedDays);
		conf.setMinPerHourEduc(60);
		conf.setMinPerHourTeacher(45);

		try {
			DBHandler.updateConfig(conf);
			/* SAMPLE */
			Subject s1 = new Subject("MAT", DIFFICULTY.HARD, c3, 45);
			Subject s2 = new Subject("SPO", DIFFICULTY.SOFT, c1, 90);
			Subject s3 = new Subject("MUS", DIFFICULTY.SOFT, c2, 45);

			Timeslot t1 = new Timeslot(60, WEEKDAYS.MONDAY, "12", "00");
			Room r1 = new Room("Senatssaal", "Stammschule", "MZH 1400",
					new Category("Musikraum"));
			ExternUnit ex1 = new ExternUnit("Schwimmen 1", t1);
			Teacher te1 = new Teacher("PAC", "Manuel", "Pache", 32, 10, 2);
			Teacher te2 = new Teacher("KES", "Erik", "Keshishian", 30, 7, 3);
			Class cl1 = new Yellowstone.YellowTable.logic.components.Class("a",
					2, r1);
			Class cl2 = new Class("b", 3, r1);
			HashSet<Class> classes = new HashSet<Class>();
			classes.add(cl1);
			ex1.setClasses(classes);
			DBHandler.addRoom(r1);
			DBHandler.addClass(cl1);
			DBHandler.addClass(cl2);
			DBHandler.addSubject(s1);
			DBHandler.addSubject(s2);
			DBHandler.addSubject(s3);
			DBHandler.addTeacher(te1);
			DBHandler.addTeacher(te2);
			DBHandler.addExternUnit(ex1);

		} catch (final DatabaseException e) {
			// Das waere dann wohl schlecht...
			e.printStackTrace();
		}
	}
}
