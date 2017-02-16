package Yellowstone.YellowTable.logic.handler.gui_handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;

/**
 * Abstrakte Oberklasse fuer alle Commands, die von der GUI ausgeloest werden
 * koennen.
 * 
 * Von mehreren Unterklassen genutzte Konstanten und Funktionen werden hier
 * bereitgestellt.
 * 
 * @author apag
 */
public abstract class Command {

	/**
	 * Fuehrt diesen Befehl aus.
	 */
	public abstract void execute();

	public static WEEKDAYS toWeekdays(final String pWeekday) {
		return WEEKDAYS.getByTitle(pWeekday);
	}

	public static DIFFICULTY toDifficulty(final String pDifficulty) {
		final String diffValue = pDifficulty.toLowerCase().trim();
		switch (diffValue) {
		case "weich":
			return DIFFICULTY.SOFT;
		case "hart":
			return DIFFICULTY.HARD;
		default:
			throw new IllegalArgumentException("Schwierigkeitsgrad unbekannt: "
					+ diffValue);
		}
	}

	/**
	 * Hilfsfunktion, die aus einem String ein Bool machen.
	 * 
	 * @param pBool
	 *            der Wahrheitswert als String
	 * @return den Wahrheitswert
	 * @throws IllegalArgumentException
	 *             wenn der String nicht bekannt ist
	 */
	public static boolean toBool(final String pBool) {
		switch (pBool) {
		case CommandParser.FALSE:
			return false;
		case CommandParser.TRUE:
			return true;
		default:
			throw new IllegalArgumentException("Wahrheitswert unbekannt: "
					+ pBool);
		}
	}

	/**
	 * Erstellt ein Room-Objekt aus den Informationen, ohne es zu persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #ROOM_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return der aus den Informationen erstellte Raum
	 */
	Room initRoom(final String[] pArgs) {
		try {
			// (Name, Building, Number, CategoryName)
			Category c = DBHandler.getCategoryByName(pArgs[ROOM_CATEGORY]);
			if (!DBHandler.getConfig().getBuildings()
					.containsKey(pArgs[ROOM_BUILDING])) {
				throw new IllegalArgumentException("ROOM: Unbekanntes Gebaeude");
			}

			return new Room(pArgs[ROOM_TITLE], pArgs[ROOM_BUILDING],
					pArgs[ROOM_NUMBER], c);
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt ein Subject-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #SUBJECT_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return der aus den Informationen erstellte Stundeninhalt
	 */
	Subject initSubject(final String pArgs[]) {
		// (Title, Difficulty, Duration, Category)
		try {
			int duration = 0;
			if (pArgs[SUB_DURATION] != null
					&& !pArgs[SUB_DURATION].trim().equals("")) {
				duration = Integer.parseInt(pArgs[SUB_DURATION]);
			}

			return new Subject(pArgs[SUB_TITLE], toDifficulty(pArgs[SUB_DIFF]),
					DBHandler.getCategoryByName(pArgs[SUB_CATEGORY]), duration);
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"SUBJECT: Zahl erwartet als Dauer (" + pArgs[SUB_DURATION]
							+ ")");
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt ein Teacher-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #TEACHER_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Lehrerin
	 */
	Teacher initTeacher(final String[] pArgs) {
		// (Short, First, Last, Workload, Minus, noSubjects,
		// [subjectsByTitle], year)
		try {
			Teacher t = new Teacher(pArgs[PERS_SHORT], pArgs[PERS_FIRST],
					pArgs[PERS_LAST], Float.parseFloat(pArgs[PERS_WORK]),
					Float.parseFloat(pArgs[PERS_MINUS]),
					Integer.parseInt(pArgs[TEACHER_YEARMEET]));

			Set<Subject> subs = new HashSet<>();

			for (int i = 0; i < Integer.parseInt(pArgs[TEACHER_NUMSUBS]); i++) {
				subs.add(DBHandler.getSubjectByName(pArgs[TEACHER_NUMSUBS + i
						+ 1]));
			}

			t.setSubjects(subs);

			return t;
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbank-Fehler: "
					+ db.getMessage());
		} catch (final NumberFormatException n) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < pArgs.length; i++) {
				s.append(pArgs[i]);
				s.append(" ");
			}
			throw new IllegalArgumentException(s.toString());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt ein EducEmployee-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #EDUC_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return der aus den Informationen erstellte paed. Mitarbeiter
	 */
	EducEmployee initEduc(final String[] pArgs) {
		// (Short, First, Last, Workload, noSubjects, [subjectsByTitle])
		try {
			Set<Subject> subs = new HashSet<Subject>();
			EducEmployee e = new EducEmployee(pArgs[PERS_SHORT],
					pArgs[PERS_FIRST], pArgs[PERS_LAST],
					Float.parseFloat(pArgs[PERS_WORK]), 0);

			e.setMinusHours(Float.parseFloat(pArgs[PERS_MINUS]));

			for (int i = 0; i < Integer.parseInt(pArgs[EDUC_NUMSUBS]); i++) {
				subs.add(DBHandler
						.getSubjectByName(pArgs[EDUC_NUMSUBS + i + 1]));
			}

			e.setSubjects(subs);
			return e;
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbank-Fehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Erstellt ein YearInfo-Objekt (String[]) aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #YEARINFO_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return das aus den Informationen erstellte Array
	 */
	String[] initYearInfo(final String[] pArgs) {
		// (Year, Subject, Workload)
		try {
			/*
			 * ueberpruefen des Jahrgangs
			 */
			int year = Integer.parseInt(pArgs[YEAR_YEAR]);
			if (0 >= year || year > Config.MAX_YEAR) {
				throw new NeedUserCorrectionException(
						"Ein solcher Jahrgang existiert nicht ("
								+ pArgs[YEAR_YEAR] + ")");
			}

			/*
			 * ueberpruefen der Arbeitslast
			 */
			int work = Integer.parseInt(pArgs[YEAR_WORK]);
			if (work < 0) {
				throw new NeedUserCorrectionException(
						"Negative Wochenstundenzahlen existieren nicht ("
								+ pArgs[YEAR_WORK] + ")");
			}

			/*
			 * ueberpruefen des Fachs (Existenz)
			 */
			DBHandler.getSubjectByName(pArgs[YEAR_SUB]);

			// nur notwendige Informationen in das Objekt stecken, nullter Index
			// entfaellt
			String[] y = new String[YEARINFO_MIN];
			y[YEAR_YEAR - 1] = pArgs[YEAR_YEAR];
			y[YEAR_SUB - 1] = pArgs[YEAR_SUB];
			y[YEAR_WORK - 1] = pArgs[YEAR_WORK];
			return y;
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"YEARINFO: Zahl erwartet als Jahrgang (" + pArgs[YEAR_YEAR]
							+ ") und Arbeitslast (" + pArgs[YEAR_WORK] + ")");
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Erstellt ein Class-Objekt aus den Informationen, ohne es zu persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #CLASS_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Schulklasse
	 */
	Class initClass(final String[] pArgs) {
		// (Name, Year, RoomTitle)
		try {
			return new Class(pArgs[CLASS_TITLE],
					Integer.parseInt(pArgs[CLASS_YEAR]),
					DBHandler.getRoomByTitle(pArgs[CLASS_ROOM]));
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"CLASS: Zahl erwartet als Jahrgang (" + pArgs[CLASS_YEAR]
							+ ")");
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt ein ExternUnit-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #EXTERN_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte externe Planungseinheit
	 *         (meist Schwimmunterricht)
	 */
	ExternUnit initExtern(final String[] pArgs) {
		// (Duration, StartHour, StartMin, Day, Title, numClasses, [Classes])
		try {
			final int numOfclasses = Integer.parseInt(pArgs[EXT_NUMCLASS]);

			Set<Class> classes = new HashSet<>();
			for (int i = 1; i <= numOfclasses; i++) {
				classes.add(DBHandler.getClassByName(pArgs[EXT_NUMCLASS + i]));
			}

			Timeslot ts = new Timeslot(Integer.parseInt(pArgs[UNIT_DURATION]),
					toWeekdays(pArgs[UNIT_DAY]), pArgs[UNIT_STARTHR],
					pArgs[UNIT_STARTMIN]);

			ExternUnit ex = new ExternUnit(pArgs[EXT_TITLE], ts);
			ex.setClasses(classes);
			return ex;
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"EXTERN: Zahl erwartet als Dauer (" + pArgs[UNIT_DURATION]
							+ ")");
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt ein TeachingUnit-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #TEACHING_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Unterrichtseinheit
	 */
	TeachingUnit initTeaching(final String[] pArgs) {
		try {
			// (Duration, StartHour, StartMin, Day, numClasses, [classes],
			// numRooms, [rooms], numSubs, [subjects])
			Timeslot ts = new Timeslot(Integer.parseInt(pArgs[UNIT_DURATION]),
					toWeekdays(pArgs[UNIT_DAY]), pArgs[UNIT_STARTHR],
					pArgs[UNIT_STARTMIN]);
			TeachingUnit tu = new TeachingUnit(ts);

			// Reihenfolge wichtig!
			// Erst Klassen, dann Raeume, dann Faecher, dann Personen
			final int numOfClasses = Integer.parseInt(pArgs[TEACH_NUMCLASS]);
			final int TEACH_NUMROOMS = TEACH_NUMCLASS + numOfClasses + 1;
			final int numOfRooms = Integer.parseInt(pArgs[TEACH_NUMROOMS]);
			final int TEACH_NUMSUBS = TEACH_NUMROOMS + numOfRooms + 1;
			final int numOfSubs = Integer.parseInt(pArgs[TEACH_NUMSUBS]);
			final int TEACH_NUMPERS = TEACH_NUMSUBS + numOfSubs + 1;
			final int numOfPers = Integer.parseInt(pArgs[TEACH_NUMPERS]);

			// Klassen-Set
			HashSet<Class> classes = new HashSet<Class>();
			for (int i = 1; i <= numOfClasses; i++) {
				Class c = DBHandler.getClassByName(pArgs[TEACH_NUMCLASS + i]);
				classes.add(c);
			}

			// Raum-Set
			HashSet<Room> rooms = new HashSet<Room>();
			for (int r = 1; r <= numOfRooms; r++) {
				Room ro = DBHandler.getRoomByTitle(pArgs[TEACH_NUMROOMS + r]);
				rooms.add(ro);
			}

			// Faecher-Set
			HashSet<Subject> subjects = new HashSet<Subject>();
			for (int s = 1; s <= numOfSubs; s++) {
				Subject sub = DBHandler.getSubjectByName(pArgs[TEACH_NUMSUBS
						+ s]);
				subjects.add(sub);
			}

			// Persons-Set (Time initial bei voller Anwesenheit)
			HashSet<PersonTime> persons = new HashSet<PersonTime>();
			for (int p = 1; p <= numOfPers; p++) {
				Person per = DBHandler
						.getEducEmployeeByShort(pArgs[TEACH_NUMPERS + p]);
				if (per == null) {
					per = DBHandler.getTeacherByShort(pArgs[TEACH_NUMPERS + p]);
				}
				if (per != null) {
					Timeslot pts = new Timeslot(ts.getDuration(), ts.getDay(),
							ts.getTimeBeginHour(), ts.getTimeBeginMinute());
					persons.add(new PersonTime(pts, per));
				} else {
					throw new IllegalArgumentException(
							"Person konnte nicht gefunden werden: "
									+ pArgs[TEACH_NUMPERS + p]);
				}
			}

			// Setzen
			tu.setClasses(classes);
			tu.setRooms(rooms);
			tu.setSubjects(subjects);
			tu.setPersonTime(persons);

			return tu;

		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Erstellt ein Break-Objekt aus den Informationen, ohne es zu persistieren.
	 * 
	 * Es wir davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #BREAK_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Pause
	 */
	Break initBreak(final String[] pArgs) {
		// (Duration, StartHour, StartMin, Title, Year, numDays, [days])

		try {
			final int numOfDays = Integer.parseInt(pArgs[BREAK_NUMDAY]);

			Set<WEEKDAYS> days = new HashSet<>();
			for (int k = 1; k <= numOfDays; k++) {
				days.add(toWeekdays(pArgs[BREAK_NUMDAY + k]));
			}

			Timeslot ts = new Timeslot(Integer.parseInt(pArgs[UNIT_DURATION]),
					WEEKDAYS.NONE, pArgs[UNIT_STARTHR], pArgs[UNIT_STARTMIN]);

			Break b = new Break(Integer.parseInt(pArgs[BREAK_YEAR]),
					pArgs[BREAK_TITLE], ts);

			b.setDays(days);
			Set<Class> classes = new HashSet<>();
			Collection<Class> dbclasses = DBHandler.getClassesByYear(b
					.getYear());
			if (dbclasses != null) {
				classes.addAll(dbclasses);
			}
			b.setClasses(classes);

			return b;
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException("BREAK: Zahl erwartet: "
					+ n.getMessage());
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException(db.getMessage());
		}
	}

	/**
	 * Erstellt ein MeetingUnit-Objekt aus den Informationen, ohne es zu
	 * persistieren.
	 * 
	 * Es wir davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #MEETING_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Planungeinheit fuer Sitzungen
	 */
	MeetingUnit initMeeting(final String[] pArgs) {
		// (Duration, StartHour, StartMin, Day, Title, Year, forTeachers,
		// forEducs)
		try {
			Timeslot ts = new Timeslot(Integer.parseInt(pArgs[UNIT_DURATION]),
					toWeekdays(pArgs[UNIT_DAY]), pArgs[UNIT_STARTHR],
					pArgs[UNIT_STARTMIN]);

			return new MeetingUnit(pArgs[MEET_TITLE],
					Integer.parseInt(pArgs[MEET_YEAR]), ts);
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"MEETING: Zahl erwartet als Dauer (" + pArgs[UNIT_DURATION]
							+ ") und Jahrgang (" + pArgs[MEET_YEAR] + ")");
		} catch (final IllegalArgumentException i) {
			// Exception bei Konstruktion, invalide Werte
			throw new NeedUserCorrectionException(i.getMessage());
		}
	}

	/**
	 * Erstellt eine PersonTime-Objekt ohne es zu persistieren.
	 * 
	 * Es wir davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #TP_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte PersonTime
	 */
	PersonTime initPersonTime(final String pArgs[]) {
		try {
			TeachingUnit tu = (TeachingUnit) DBHandler.getUnitByID(
					TeachingUnit.class.getSimpleName(),
					Integer.parseInt(pArgs[TP_UID]));

			if (tu != null) {
				Person pers = DBHandler.getEducEmployeeByShort(pArgs[TP_SHORT]);
				if (pers == null) {
					pers = DBHandler.getTeacherByShort(pArgs[TP_SHORT]);
				}

				if (pers != null) {
					int duration = Timeslot.getDurationByStartAndEndtime(
							pArgs[TP_STHR], pArgs[TP_STMN], pArgs[TP_ENDHR],
							pArgs[TP_ENDMN]);
					Timeslot ts = new Timeslot(duration, tu.getTimeslot()
							.getDay(), pArgs[TP_STHR], pArgs[TP_STMN]);
					PersonTime pt = new PersonTime(ts, pers);
					return pt;
				}
				throw new IllegalArgumentException(
						"Es existiert keine solche Person: " + pArgs[TP_SHORT]);
			}

			throw new IllegalArgumentException(
					"Es existiert keine solche TeachingUnit: " + pArgs[TP_UID]);
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		}
	}
}
