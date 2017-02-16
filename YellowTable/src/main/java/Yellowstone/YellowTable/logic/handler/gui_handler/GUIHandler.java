package Yellowstone.YellowTable.logic.handler.gui_handler;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;

/**
 * GUI_Handler stellt Daten aus dem DB_Handler für die GUI bereit, indem er
 * ObservableLists erstellt.
 * 
 * Zusaetzlich bietet er Funktionen zum Erstellen von Properties aus
 * darzustellenden Datentypen, die von der GUI benoetigt werden.
 * 
 * @author prohleder, apag, tomlewandowski (FloatProperty, ArrayList zu TreeMaps
 *         und Teile der Methode getTeacherDistributon)
 * 
 */
public class GUIHandler {

	/**
	 * Erstellt ein StringProperty aus einem String.
	 * 
	 * @param pString
	 *            umzuwandelnder String
	 * @return entsprechendes Property
	 */
	public static StringProperty toStringProperty(final String pString) {
		return new SimpleStringProperty(pString);
	}

	/**
	 * Erstellt ein IntegerProperty aus einem int.
	 * 
	 * Anmerkung: IntegerProperty implementiert Observable<Number> statt
	 * Observable<Integer>
	 * 
	 * @param pInt
	 *            umzuwandelnder int
	 * @return entsprechendes Property
	 */
	public static IntegerProperty toIntegerProperty(final int pInt) {
		return new SimpleIntegerProperty(pInt);
	}

	/**
	 * Erstellt ein FloatProperty aus einem float.
	 * 
	 * @param pFloat
	 *            umzuwandelnder float
	 * @return entsprechendes Property
	 */
	public static FloatProperty toFloatProperty(final float pFloat) {
		return new SimpleFloatProperty(pFloat);
	}

	/**
	 * Macht aus jedem Set einen wunderschoenen String (solange toString eine
	 * mindestens ebenso schoene Ausgabe liefert)
	 * 
	 * @param pSet
	 *            der auszugebene String
	 * @return ein StringProperty, das javafx freuen sollte
	 */
	public static <T> StringProperty fromSet(final Set<T> pSet) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (T entry : pSet) {
			if (first) {
				buf.append(entry.toString());
				first = false;
			} else {
				if (entry != null) {
					buf.append(", ");
					buf.append(entry.toString());
				}
			}
		}
		String result = buf.toString();

		return toStringProperty(result.substring(0, result.length()));
	}

	/**
	 * Gibt das Config-Objekt des aktuellen Projekts zurueck.
	 * 
	 * @return die Config
	 */
	public static Config getConfig() {
		return DBHandler.getConfig();
	}

	/**
	 * Gibt eine Liste aller Raeume zurueck.
	 * 
	 * @return eine Liste aller Raeume
	 */
	public static ObservableList<Room> getRooms() {
		try {
			ObservableList<Room> rooms = FXCollections
					.observableArrayList(DBHandler.getAllRooms());

			return rooms;
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt Raeume als Strings zurueck
	 * 
	 * @return die Raeume fuer ComboBoxen
	 */
	public static ObservableList<String> getComboRooms() {
		ObservableList<String> comboRooms = FXCollections.observableArrayList();
		for (Room r : getRooms()) {
			comboRooms.add(r.getName());
		}
		return comboRooms;
	}

	/**
	 * Gibt eine Liste aller Lehrerinnen zurueck.
	 * 
	 * @return eine Liste aller Lehrerinnen
	 */
	public static ObservableList<Teacher> getTeachers() {
		try {
			ObservableList<Teacher> teachers = FXCollections
					.observableArrayList(DBHandler.getAllTeachers());
			return teachers;
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt alle Lehrer als Liste von Strings zurueck.
	 * 
	 * @return eine String-Liste der Lehrer
	 */
	public static ObservableList<String> getComboTeachers() {
		ObservableList<String> teachers = FXCollections.observableArrayList();
		for (Teacher t : getTeachers()) {
			teachers.add(t.getShortName());
		}
		return teachers;
	}

	/**
	 * Gibt eine Liste aller paedagogischen Mitarbeiter zurueck.
	 * 
	 * @return eine Liste aller paedagogischen Mitarbeiter
	 */
	public static ObservableList<EducEmployee> getEducEmployees() {
		try {
			ObservableList<EducEmployee> educs = FXCollections
					.observableArrayList(DBHandler.getAllEducEmployees());

			return educs;
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt eine Liste aller Kuerzel der Lehrer und paed. Mitarbeiter zurueck.
	 * 
	 * @return eine Liste aller Personen fuer Comboboxen.
	 */
	public static ObservableList<String> getComboPersons() {
		ObservableList<String> pers = FXCollections
				.observableArrayList(getComboTeachers());
		for (EducEmployee e : getEducEmployees()) {
			pers.add(e.getShortName());
		}
		return pers;
	}

	/**
	 * Gibt eine Liste aller Klassen zurueck.
	 * 
	 * @return eine Liste aller Klassen
	 */
	public static ObservableList<Class> getClasses() {
		try {
			ObservableList<Class> classes = FXCollections
					.observableArrayList(DBHandler.getAllClasses());

			return classes;
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Die Klassen als Strings.
	 * 
	 * @return Listen von Klassen fuer ComboBoxen
	 */
	public static ObservableList<String> getComboClasses() {
		ObservableList<String> comboClasses = FXCollections
				.observableArrayList();
		for (Class c : getClasses()) {
			comboClasses.add(c.getName());
		}
		return comboClasses;
	}

	/**
	 * Gibt eine Liste aller Stundeninhalte zurueck.
	 * 
	 * @return eine Liste aller Stundeninhalte
	 */
	public static ObservableList<Subject> getSubjects() {
		try {
			ObservableList<Subject> subjects = FXCollections
					.observableArrayList(DBHandler.getAllSubjects());

			return subjects;
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Die Stundeninhalte als Strings
	 * 
	 * @return eine Liste der Namen aller Stundeninhalte
	 */
	public static ObservableList<String> getComboSubjects() {
		ObservableList<String> ol = FXCollections.observableArrayList();
		for (Subject s : getSubjects()) {
			ol.add(s.getTitle());
		}
		return ol;
	}

	/**
	 * Gibt eine Liste von Eintraegen zurueck, die zu einem Stundeninhalt den
	 * Bedarf angibt.
	 * 
	 * @param pYear
	 *            der Jahrgang fuer den jeder Bedarf angegeben werden soll
	 * @return eine Liste mit Jahrgangsbedarfen
	 */
	public static ObservableList<Entry<Subject, Integer>> getSubjectHoursPerYear(
			final int pYear) {
		try {
			HashMap<Subject, Integer> workload = new HashMap<>();
			for (Subject s : DBHandler.getAllSubjects()) {
				workload.put(s, DBHandler.getIntendedWorkloadPerSubjectPerYear(
						s, pYear));
			}
			return FXCollections.observableArrayList(workload.entrySet());
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt den individuellen Bedarf einer Klasse fuer alle Faecher zurueck.
	 * 
	 * @param pClass
	 *            die Klasse dessen Bedarf angezeigt werden soll
	 * @return eine Liste mit Objekten, die das Fach, den Bedarf und die
	 *         aktuelle Last enthalten (siehe {@link SubIntendedActual})
	 */
	public static ObservableList<SubIntendedActual> getSubjectHoursPerClass(
			final String pClass) {

		ObservableList<SubIntendedActual> classinfo = FXCollections
				.observableArrayList();

		try {
			Class c = DBHandler.getClassByName(pClass);

			for (Subject s : DBHandler.getAllSubjects()) {
				classinfo.add(new SubIntendedActual(s, DBHandler
						.getIntendedWorkloadPerSubjectPerClass(s, c), DBHandler
						.getHoursPerSubjectPerClass(s, c)));
			}

			return classinfo;
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt eine Liste aller Schwimmunterrichtseinheiten zurueck.
	 * 
	 * @return eine Liste aller Schwimmunterrichtseinheiten
	 */
	public static ObservableList<ExternUnit> getExternUnits() {
		try {
			return FXCollections.observableArrayList(DBHandler
					.getAllExternUnits());
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt eine Liste aller Planungseinheiten eines bestimmten Eintrags an dem
	 * gegebenen Wochentag zurueck.
	 * 
	 * @param pWeekday
	 *            der Wochentag fuer den die Liste erstellt werden soll
	 * @param pObject
	 *            der, dessen Planungseinheiten angezeigt werden sollen.
	 * @return eine Liste der Planungseinheiten eines Wochentages einer Person
	 *         oder eines Raums.
	 */
	public static ObservableList<String> getDaytablePerTeacher(
			final String pWeekday, final Teacher pTeacher) {

		try {

			final int gran = DBHandler.getConfig().getGranularity();

			ObservableList<String> daytable = FXCollections
					.observableArrayList();
			List<String> startHours = getTimeFrame();

			for (int i = 0; i < startHours.size() - 1; i++) {
				int sthour = Integer.parseInt(startHours.get(i).split(":")[0]);
				int stmin = Integer.parseInt(startHours.get(i).split(":")[1]);

				int endhour = Integer
						.parseInt(startHours.get(i + 1).split(":")[0]);
				int endmin = Integer
						.parseInt(startHours.get(i + 1).split(":")[1]);
				Collection<Class> c = DBHandler.getClassesPerPersonPerTime(
						pTeacher, WEEKDAYS.getByTitle(pWeekday), sthour, stmin,
						endhour, endmin);

				Set<Class> classes = new HashSet<Class>(c);
				daytable.add(fromSet(classes).get());
			}

			int last = startHours.size() - 1;
			Timeslot ts = new Timeslot(gran, WEEKDAYS.getByTitle(pWeekday),
					startHours.get(last).split(":")[0], startHours.get(last)
							.split(":")[1]);
			ts.setStarttime();
			ts.setEndTime();

			Collection<Class> c = DBHandler.getClassesPerPersonPerTime(
					pTeacher, WEEKDAYS.getByTitle(pWeekday), ts.getStartHour(),
					ts.getStartMinute(), ts.getEndHour_int(),
					ts.getEndMinute_int());
			Set<Class> classes = new HashSet<Class>(c);
			daytable.add(fromSet(classes).get());

			return daytable;

		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException("Zahl erwartet: "
					+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException("Problematische Arraygroesse: "
					+ a.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("Null gefunden: "
					+ np.getMessage());
		}
	}

	/**
	 * Gibt eine Liste fuer den Wochentagplan zurueck
	 * 
	 * @param pWeekday
	 *            der Tag, dessen Plan angezeigt werden.
	 * @return eine Liste
	 */
	public static ObservableList<List<String>> getDayTable(final String pWeekday) {
		ObservableList<List<String>> daytable = FXCollections
				.observableArrayList();
		for (Teacher t : getTeachers()) {
			List<String> teachTable = FXCollections.observableArrayList();
			teachTable.add(t.getShortName());
			teachTable.addAll(getDaytablePerTeacher(pWeekday, t));
			daytable.add(teachTable);
		}
		return daytable;
	}

	/**
	 * Gibt eine Liste von allen Planungseinheiten zurueck.
	 * 
	 * @param pClass
	 *            Klasse, dessen Stundenplan zurueckgegeben werden soll
	 * @return einen Stundenplan
	 */
	public static ObservableList<Unit> getWeekTable(final Class pClass) {
		return FXCollections.observableArrayList(DBHandler
				.getWeekTableByClass(pClass));
	}

	/**
	 * Gibt eine Liste von allen Planungseinheiten zurueck.
	 * 
	 * @param pPerson
	 *            Person, dessen Stundenplan zurueckgegeben werden soll
	 * @return einen Stundenplan
	 */
	public static ObservableList<Unit> getWeekTable(final Person pPerson) {
		return FXCollections.observableArrayList(DBHandler
				.getWeekTableByPerson(pPerson));
	}

	/**
	 * Gibt eine Liste von allen Planungseinheiten zurueck.
	 * 
	 * @param pRoom
	 *            Raum, dessen Stundenplan zurueckgegeben werden soll
	 * @return einen Stundenplan
	 */
	public static ObservableList<Unit> getWeekTable(final Room pRoom) {
		return FXCollections.observableArrayList(DBHandler
				.getWeekTableByRoom(pRoom));
	}

	/**
	 * Gibt eine Liste aller Pausen zurueck.
	 * 
	 * @return eine Liste aller Pausen
	 */
	public static ObservableList<Break> getBreaks() {
		try {
			return FXCollections.observableArrayList(DBHandler.getAllBreaks());
		} catch (final DatabaseException db) {
			return FXCollections.observableArrayList();
		}
	}

	/**
	 * Gibt eine Liste aller Meetings zurueck.
	 * 
	 * @return eine Liste aller Meetings
	 */
	public static ObservableList<MeetingUnit> getMeetingUnits() {
		try {
			return FXCollections.observableArrayList(DBHandler
					.getAllMeetingUnits());
		} catch (final DatabaseException db) {
			return FXCollections.observableArrayList();
		}
	}

	/**
	 * Gibt eine Liste aller Standorte und deren Entfernung zurueck.
	 * 
	 * @return Eine Liste aller Standorte und Entfernungen zum Hauptstandort
	 */
	public static ObservableList<Entry<String, Integer>> getBuildings() {
		Config con = DBHandler.getConfig();
		if (con != null && con.getBuildings() != null) {
			ObservableList<Entry<String, Integer>> buildings = FXCollections
					.observableArrayList(con.getBuildings().entrySet());
			return buildings;
		}
		throw new IllegalArgumentException(
				"Keine Config oder keine Standorte gefunden.");
	}

	/**
	 * Gibt eine Liste aller Standortsnamen zurueck, die in ComboBoxen geladen
	 * werden koennen.
	 * 
	 * @return eine Liste von Strings
	 */
	public static ObservableList<String> getComboBuildings() {
		ObservableList<String> comboBuildings = FXCollections
				.observableArrayList();
		for (Entry<String, Integer> e : getBuildings()) {
			comboBuildings.add(e.getKey());
		}
		return comboBuildings;
	}

	/**
	 * Gibt eine Liste aller Raumfunktionen zurueck.
	 * 
	 * @return eine Liste aller Raumfunktionen
	 */
	public static ObservableList<Category> getCategories() {
		try {
			ObservableList<Category> categories = FXCollections
					.observableArrayList(DBHandler.getAllCategories());
			return categories;
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt die Raumfunktionen als Strings zurueck.
	 * 
	 * @return die Raumfunktionen für ComboBoxen
	 */
	public static ObservableList<String> getComboCategories() {
		ObservableList<String> comboCategories = FXCollections
				.observableArrayList();
		for (Category c : getCategories()) {
			comboCategories.add(c.getName());
		}
		return comboCategories;
	}

	/**
	 * Listen von Granularitäten (5,10,15,20,25...)
	 */
	public static ObservableList<Integer> getGranularityList() {
		ObservableList<Integer> ol = FXCollections.observableArrayList(5, 10,
				15, 20, 25, 30, 60);
		return ol;
	}

	/**
	 * Listen von Backup-Abstaenden (5,10,15,20,25...)
	 */
	public static ObservableList<String> getBackupIntervallList() {
		ObservableList<String> ol = FXCollections.observableArrayList("5",
				"10", "15", "20", "30", "60");
		return ol;
	}

	/**
	 * Gibt eine ObservableList mit den Jahrgängen zurück. 1,2,3,4
	 * 
	 * @return ObservableList mit Jahrgängen
	 */
	public static ObservableList<Integer> getYears() {
		ObservableList<Integer> ol = FXCollections.observableArrayList();
		for (int i = 1; i <= IConfig.MAX_YEAR; i++) {
			ol.add(i);
		}
		return ol;
	}

	/**
	 * Gibt eine Liste mit allen Jahrgaengen und einem 0-Wert zurueck, falls die
	 * Auswahl eines Jahrgangs optional ist.
	 * 
	 * @return eine Liste mit Jahrgaengen, wobei die Auswahl optional ist.
	 */
	public static ObservableList<Integer> getOptionalYears() {
		ObservableList<Integer> ol = FXCollections.observableArrayList();
		for (int i = 0; i <= IConfig.MAX_YEAR; i++) {
			ol.add(i);
		}
		return ol;
	}

	/**
	 * Eine Liste mit allen Wochentagen.
	 * 
	 * @return alle Wochentage
	 */
	public static ObservableList<WEEKDAYS> getDays() {
		ObservableList<WEEKDAYS> days = FXCollections.observableArrayList();
		for (WEEKDAYS w : WEEKDAYS.values()) {
			if (w != WEEKDAYS.NONE) {
				days.add(w);
			}
		}
		return days;
	}

	/**
	 * Die geplanten Tage als Strings.
	 * 
	 * @return planbare Tage als Strings fuer ComboBoxen
	 */
	public static ObservableList<String> getComboDays() {
		ObservableList<String> ol = FXCollections.observableArrayList();
		Collection<WEEKDAYS> planned = getConfig().getPlannedDays();
		WEEKDAYS[] allDays = WEEKDAYS.values();
		for (int i = 0; i < allDays.length; i++) {
			WEEKDAYS w = allDays[i];
			if (planned.contains(w)) {
				ol.add(w.getTitle());
			}
		}
		return ol;
	}

	/**
	 * Alle Tage (ob geplant oder nicht) als Strings.
	 * 
	 * @return alle Tage als Strings fuer ComboBoxen
	 */
	public static ObservableList<String> getComboAllDays() {
		ObservableList<String> ol = FXCollections.observableArrayList();
		for (WEEKDAYS w : getDays()) {
			ol.add(w.getTitle());
		}
		return ol;
	}

	/**
	 * Eine Liste aller Schwierigkeitsgrade.
	 * 
	 * @return Eine Liste aller Schwierigkeitsgrade.
	 */
	public static ObservableList<DIFFICULTY> getDifficulties() {
		ObservableList<DIFFICULTY> diff = FXCollections
				.observableArrayList(DIFFICULTY.values());
		return diff;
	}

	/**
	 * Eine Liste der Schwierigkeitsgrade als String.
	 * 
	 * @return Schwierigkeitsgrade fuer ComboBoxen
	 */
	public static ObservableList<String> getComboDifficulties() {
		ObservableList<String> o = FXCollections.observableArrayList();

		for (DIFFICULTY d : DIFFICULTY.values()) {
			o.add(d.getTitle());
		}
		return o;
	}

	/**
	 * Gibt eine Liste mit den Startzeiten nach Granularitaeten zurueck.
	 * 
	 * @return alle Startzeiten nach Granularitaet
	 */
	public static ArrayList<String> getTimeFrame() {
		Config c = getConfig();
		return Timeslot.getDayTableSlots(c.getTimeframe().getStartHour(), c
				.getTimeframe().getStartMinute(), c.getTimeframe()
				.getEndHour_int(), c.getTimeframe().getEndMinute_int(), c
				.getGranularity());
	}

	/**
	 * Gibt eine Liste fuer die Lehrerfachverteilung zurueck. Die inneren Listen
	 * repraesentieren dabei immer die Informationen zu einer Person. Die Art
	 * der Informationen wird in der ersten Zeile beschrieben.
	 * 
	 * @return die Lehrer-Fach-Verteilung, neuerdings auch Personaleinsatzplan
	 */
	public static ObservableList<List<String>> getTeacherDistribution() {
		try {
			ObservableList<List<String>> distribution = FXCollections
					.observableArrayList();
			List<Person> techs = FXCollections.observableArrayList(DBHandler
					.getAllTeachers());
			techs.addAll(DBHandler.getAllEducEmployees());
			List<Subject> subs = getSubjects();

			/*
			 * Repraesentiert eine Zeile. Beim ersten Mal stehen hier nur die
			 * Arten der Informationen.
			 */
			List<String> line = FXCollections.observableArrayList("Person",
					"Stunden", "Minusstunden", "Ist-Stunden");
			// Alle Faecher in fester Reihenfolge
			for (int i = 0; i < subs.size(); i++) {
				line.add(subs.get(i).getTitle());
			}
			// Zeile hinzufuegen
			distribution.add(line);

			DecimalFormat df = new DecimalFormat("#.##");
			// Zeile pro Person
			for (Person p : techs) {
				if (p instanceof Teacher) {
					Teacher t = (Teacher) p;

					String hours = df.format(DBHandler.getHoursPerTeacher(t));
					// neue Zeile initialsiert
					line = FXCollections.observableArrayList(t.getShortName(),
							String.valueOf(t.getMaxWorkload()),
							String.valueOf(t.getMinusHours()), hours);
					for (int i = 0; i < subs.size(); i++) {
						// pro Fach...
						Subject s = subs.get(i);
						StringBuilder sb = new StringBuilder();
						Collection<Class> classes = DBHandler
								.getClassesByTeacher(t);
						if (classes != null) {
							for (Class c : classes) {
								double hperstc = DBHandler
										.getHoursPerSubjectPerTeacherPerClass(
												s, t, c);
								// Testet, zunaechst ob die Stunden 0 sind. Dann
								// muss nicht in die Spalte geschrieben werden.
								if (hperstc != 0) {
									sb.append(c.getName());
									sb.append(" - ");
									// Setzt das Format, der Stunden, sodass
									// nicht
									// ewig lange Doubles entstehen.
									String s1 = df.format(hperstc);
									sb.append(s1 + " Std");
									sb.append("\n");
								}
							}
						}
						sb.append("");
						line.add(sb.toString());
					}
					distribution.add(line);
				} else if (p instanceof EducEmployee) {
					EducEmployee e = (EducEmployee) p;

					String hours = df.format(DBHandler.getHoursPerEduc(e));
					// neue Zeile initialsiert
					line = FXCollections.observableArrayList(e.getShortName(),
							String.valueOf(e.getMaxWorkload()),
							String.valueOf(e.getMinusHours()), hours);
					for (int i = 0; i < subs.size(); i++) {
						// pro Fach...
						Subject s = subs.get(i);
						StringBuilder sb = new StringBuilder();
						Collection<Class> classes = DBHandler
								.getClassesByEduc(e);
						if (classes != null) {
							for (Class c : classes) {
								double hperstc = DBHandler
										.getHoursPerSubjectPerEducPerClass(s,
												e, c);
								// Testet, zunaechst ob die Stunden 0 sind. Dann
								// muss nicht in die Spalte geschrieben werden.
								if (hperstc != 0) {
									sb.append(c.getName());
									sb.append(" - ");
									// Setzt das Format, der Stunden, sodass
									// nicht
									// ewig lange Doubles entstehen.
									String s1 = df.format(hperstc);
									sb.append(s1 + " Std");
									sb.append("\n");
								}
							}
						}
						sb.append("");
						line.add(sb.toString());
					}
					distribution.add(line);
				}
			}

			return distribution;
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("Null gefunden: "
					+ np.getMessage());
		}
	}

	/**
	 * Gibt das Fach mit diesem Kuerzel zurueck
	 * 
	 * @param pName
	 *            das Kuerzel des Fachs
	 * @return das Fach
	 */
	public static Subject getSubjectByName(final String pName) {
		try {
			return DBHandler.getSubjectByName(pName);
		} catch (final DatabaseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ e.getMessage());
		}
	}

	/**
	 * Gibt den Raum mit dem Namen zurueck.
	 * 
	 * @param pName
	 *            der Name des Raums
	 * @return der Raum
	 */
	public static Room getRoomByName(final String pName) {
		try {
			return DBHandler.getRoomByTitle(pName);
		} catch (final DatabaseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ e.getMessage());
		}
	}

	/**
	 * Gibt die Wuensche einer Lehrerin zurueck.
	 * 
	 * @param pTeacher
	 * @return
	 */
	public static ObservableList<Timeslot> getTeacherWishes(
			final Teacher pTeacher) {
		try {
			Teacher t = DBHandler.getTeacherByShort(pTeacher.getShortName());
			return FXCollections.observableArrayList(t.getPreferences());
		} catch (final DatabaseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ e.getMessage());
		}
	}

	/**
	 * Setzt die Wuensche eines Lehrers neu.
	 * 
	 * @param pTeacher
	 *            der Lehrer
	 * @param pWishes
	 *            seine neuen Wuensche
	 */
	public static void setTeacherWishes(final Teacher pTeacher,
			final Collection<Timeslot> pWishes) {
		if (pTeacher != null && pWishes != null) {
			try {
				Teacher t = DBHandler
						.getTeacherByShort(pTeacher.getShortName());
				t.setPreferences(pWishes);
				DBHandler.updateTeacher(t);
			} catch (final DatabaseException db) {
				db.printStackTrace();
				throw new IllegalArgumentException("Datenbankfehler: "
						+ db.getMessage());
			}
		}
	}

	/**
	 * Initialisiert ein neues Projekt mit dem gegebenen Titel
	 * 
	 * @param pTitle
	 *            der neue Titel
	 */
	public static void newProject(final String pTitle, final String pDescription) {
		if (pTitle != null && !pTitle.equals("")) {
			getConfig().setProjectTitle(pTitle);
		} else {
			// keine Behandlung notwendig
		}
	}

	/**
	 * Führt einen Reset für das aktuelle Projekt aus, in dem die Datenbank
	 * heruntergefahren, und die Datenbank gelöscht wird.
	 * 
	 * @param pTitle
	 *            der neue Titel
	 */
	public static void resetProject() {
		DBHandler.resetDatabase();
	}

	/**
	 * Erstellt einen Initialzustand, der später für neue Projekte verwendet
	 * werdenkann.
	 */
	public static void createInitialProject() {
		try {
			File file = new File(Config.DBInitPath);
			if (!file.exists()) {
				DBHandler.createBackup(Config.DBInitPath);
			}
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Gibt zurueck, ob bereits ein Projekt besteht.
	 * 
	 * @return <code>true</code> wenn bereits ein Projekt geladen werden kann.
	 */
	public static boolean projecttExists() {
		return GUIHandler.getConfig().getProjectExist();
	}

	/* Befehle */

	/**
	 * Editiert den Raum mit dem gegebenen Namen und setzt die restlichen Werte
	 * entsprechend der restlichen Parameter.
	 * 
	 * @param pName
	 *            der Name des Raums
	 * @param pNumber
	 *            die Raum nummer
	 * @param pBuilding
	 *            das Gebaeude des Raums
	 * @param pCategory
	 *            die diesem Raum zugeordnete Raumfunktion
	 */
	public static void editRoom(final String pName, final String pNumber,
			final String pBuilding, final String pCategory) {
		TreeMap<Integer, String> room = new TreeMap<Integer, String>();

		room.put(0, CommandParser.ROOM);
		room.put(CommandParser.ROOM_TITLE, pName);
		room.put(CommandParser.ROOM_NUMBER, pNumber);
		room.put(CommandParser.ROOM_BUILDING, pBuilding);
		room.put(CommandParser.ROOM_CATEGORY, pCategory);

		editValid(room);
	}

	/**
	 * Editiert den Stundeninhalt mit dem gegebenen Titel und setzt die
	 * restlichen Werte entsprechend der restlichen Parameter.
	 * 
	 * @param pTitle
	 *            der Titel des Stundeninhaltes
	 * @param pDifficulty
	 *            der Schwierigkeitsgrad des Stundeninhaltes
	 * @param pCategory
	 *            die zugeordnete Raumfunktion
	 * @param pDuration
	 *            die Standardlaenge in Minuten
	 */
	public static void editSubject(final String pTitle,
			final String pDifficulty, final String pCategory,
			final String pDuration) {
		TreeMap<Integer, String> subject = new TreeMap<Integer, String>();
		subject.put(0, CommandParser.SUBJECT);
		subject.put(CommandParser.SUB_TITLE, pTitle);
		subject.put(CommandParser.SUB_DURATION, pDuration);
		subject.put(CommandParser.SUB_DIFF, pDifficulty);
		subject.put(CommandParser.SUB_CATEGORY, pCategory);

		editValid(subject);
	}

	/**
	 * Editiert die Lehrerin mit dem gegebenen Kuerzel und setzt die restlichen
	 * Werte entsprechend der anderen Parameter.
	 * 
	 * @param pShort
	 *            das Kuerzel der Lehrerin
	 * @param pFirst
	 *            der Vorname
	 * @param pLast
	 *            der Nachname
	 * @param pWork
	 *            die Sollstunden
	 * @param pMinus
	 *            die Minusstunden
	 * @param pYear
	 *            der Jahrgang zu dessem Treffen diese Lehrerin muss
	 * @param pSubjects
	 *            ein Array aller Stundeninhalte dieser Lehrerin
	 */
	public static void editTeacher(final String pShort, final String pFirst,
			final String pLast, final String pWork, final String pMinus,
			final String pYear, final String[] pSubjects) {
		TreeMap<Integer, String> teacher = new TreeMap<Integer, String>();
		teacher.put(0, CommandParser.TEACHER);
		teacher.put(CommandParser.PERS_SHORT, pShort);
		teacher.put(CommandParser.PERS_FIRST, pFirst);
		teacher.put(CommandParser.PERS_LAST, pLast);
		teacher.put(CommandParser.PERS_WORK, pWork);
		teacher.put(CommandParser.PERS_MINUS, pMinus);
		teacher.put(CommandParser.TEACHER_YEARMEET, pYear);
		teacher.put(CommandParser.TEACHER_NUMSUBS,
				String.valueOf(pSubjects.length));
		for (int i = 0; i < pSubjects.length; i++) {
			teacher.put(CommandParser.TEACHER_NUMSUBS + i + 1, pSubjects[i]);
		}

		editValid(teacher);
	}

	/**
	 * Editiert den paed. Mitarbeiter mit dem gegebenen Kuerzel und setzt die
	 * restlichen Werte entsprechend der anderen Parameter.
	 * 
	 * @param pShort
	 *            das Kuerzel des paed. Mitarbeiters
	 * @param pFirst
	 *            der Vorname
	 * @param pLast
	 *            der Nachname
	 * @param pWork
	 *            die Wochenstundenzahl
	 * @param pSubjects
	 *            ein Array aller Stundeninhalte dieses paed. Mitarbeiters
	 */
	public static void editEduc(final String pShort, final String pFirst,
			final String pLast, final String pWork, final String pMinus,
			final String[] pSubjects) {

		TreeMap<Integer, String> educ = new TreeMap<Integer, String>();
		educ.put(0, CommandParser.EDUC);
		educ.put(CommandParser.PERS_SHORT, pShort);
		educ.put(CommandParser.PERS_FIRST, pFirst);
		educ.put(CommandParser.PERS_LAST, pLast);
		educ.put(CommandParser.PERS_WORK, pWork);
		educ.put(CommandParser.PERS_MINUS, pMinus);
		educ.put(CommandParser.EDUC_NUMSUBS, String.valueOf(pSubjects.length));
		for (int i = 0; i < pSubjects.length; i++) {
			educ.put(CommandParser.EDUC_NUMSUBS + i + 1, pSubjects[i]);
		}
		System.out.println("edit educ " + educ);
		editValid(educ);
	}

	/**
	 * Editiert die vorgegebene Wochenstundenzahl fuer den gegebenen
	 * Stundeninhalt in dem gegebenen Jahrgang.
	 * 
	 * @param pYear
	 *            der Jahrgang
	 * @param pSubject
	 *            der Stundeninhalt
	 * @param pWork
	 *            die neue Wochenstundenzahl
	 */
	public static void editYearInfo(final String pYear, final String pSubject,
			final String pWork) {

		TreeMap<Integer, String> year = new TreeMap<Integer, String>();
		year.put(0, CommandParser.YEARINFO);
		year.put(CommandParser.YEAR_YEAR, pYear);
		year.put(CommandParser.YEAR_SUB, pSubject);
		year.put(CommandParser.YEAR_WORK, pWork);

		editValid(year);
	}

	/**
	 * Editiert die Schulklasse aus dem gegebenen Jahrgang mit dem gegebenen
	 * Titel.
	 * 
	 * @param pYear
	 *            der Jahrgang
	 * @param pTitle
	 *            der Titel
	 * @param pRoom
	 *            der Klassenraum
	 */
	public static void editClass(final String pYear, final String pTitle,
			final String pRoom) {
		TreeMap<Integer, String> schoolclass = new TreeMap<Integer, String>();

		schoolclass.put(0, CommandParser.CLASS);
		schoolclass.put(CommandParser.CLASS_YEAR, pYear);
		schoolclass.put(CommandParser.CLASS_TITLE, pTitle);
		schoolclass.put(CommandParser.CLASS_ROOM, pRoom);

		editValid(schoolclass);
	}

	/**
	 * Editiert die externe Planungseinheit mit diesem Namen.
	 * 
	 * @param pID
	 *            die ID
	 * @param pDay
	 *            der Tag
	 * @param pStartHr
	 *            die Startstunde
	 * @param pStartMin
	 *            die Startminute
	 * @param pDuration
	 *            die Dauer
	 * @param pTitle
	 *            der Titel
	 * @param pClasses
	 *            die teilnehmenden Klassen
	 */
	public static void editExtern(final int pID, final String pDay,
			final String pStartHr, final String pStartMin,
			final String pDuration, final String pTitle, final String[] pClasses) {

		TreeMap<Integer, String> extern = new TreeMap<Integer, String>();
		extern.put(0, CommandParser.EXTERN);
		extern.put(CommandParser.UNIT_ID, String.valueOf(pID));
		extern.put(CommandParser.UNIT_DAY, pDay);
		extern.put(CommandParser.UNIT_STARTHR, pStartHr);
		extern.put(CommandParser.UNIT_STARTMIN, pStartMin);
		extern.put(CommandParser.UNIT_DURATION, pDuration);
		extern.put(CommandParser.EXT_TITLE, pTitle);
		extern.put(CommandParser.EXT_NUMCLASS, String.valueOf(pClasses.length));

		for (int i = 0; i < pClasses.length; i++) {
			extern.put(CommandParser.EXT_NUMCLASS + i + 1, pClasses[i]);
		}

		editValid(extern);
	}

	/**
	 * Editiert die Unterrichtseinheit mit diesem Namen.
	 * 
	 * @param pID
	 *            die ID
	 * @param pDay
	 *            der Tag
	 * @param pStartHr
	 *            die Startstunde
	 * @param pStartMin
	 *            die Startminute
	 * @param pDuration
	 *            die Dauer
	 * @param pClasses
	 *            die teilnehmenden Klassen
	 * @param pRooms
	 *            die verwendeten Raeume
	 * @param pSubjects
	 *            die unterrichteten Stundeninhalte
	 * @param pPersons
	 *            die unterrichtenden Personen
	 */
	public static void editTeaching(final int pID, final String pDay,
			final String pStartHr, final String pStartMin,
			final String pDuration, final String[] pClasses,
			final String[] pRooms, final String[] pSubjects,
			final String[] pPersons) {

		TreeMap<Integer, String> teaching = new TreeMap<Integer, String>();

		teaching.put(0, CommandParser.TEACHING);
		teaching.put(CommandParser.UNIT_ID, String.valueOf(pID));
		teaching.put(CommandParser.UNIT_DAY, pDay);
		teaching.put(CommandParser.UNIT_STARTHR, pStartHr);
		teaching.put(CommandParser.UNIT_STARTMIN, pStartMin);
		teaching.put(CommandParser.UNIT_DURATION, pDuration);
		teaching.put(CommandParser.TEACH_NUMCLASS,
				String.valueOf(pClasses.length));
		for (int i = 0; i < pClasses.length; i++) {
			teaching.put(CommandParser.TEACH_NUMCLASS + i + 1, pClasses[i]);
		}

		final int num_rooms = teaching.lastKey() + 1;
		teaching.put(num_rooms, String.valueOf(pRooms.length));
		for (int j = 0; j < pRooms.length; j++) {
			teaching.put(num_rooms + j + 1, pRooms[j]);
		}

		final int num_subs = teaching.lastKey() + 1;
		teaching.put(num_subs, String.valueOf(pSubjects.length));
		for (int k = 0; k < pSubjects.length; k++) {
			teaching.put(num_subs + k + 1, pSubjects[k]);
		}

		final int num_pers = teaching.lastKey() + 1;
		teaching.put(num_pers, String.valueOf(pPersons.length));
		for (int l = 0; l < pPersons.length; l++) {
			teaching.put(num_pers + l + 1, pPersons[l]);
		}

		editValid(teaching);
	}

	/**
	 * Nimmt die TreeMap und erzeugt daraus ein String-Array, das als Parameter
	 * fuer {@link CommandParser#edit(String[])} verwendet wird.
	 * 
	 * @param map
	 *            die weiterzuverarbeitende map
	 * @throws IllegalArgumentException
	 *             wenn die Indizes nicht lueckenlos verwendet wurden, also der
	 *             letzte Schluessel einen anderen Wert als map.size() - 1 hat.
	 */
	private static void editValid(final TreeMap<Integer, String> map) {
		if (map.lastKey() != map.size() - 1) {
			throw new IllegalArgumentException(
					"Die Konstanten im CommandParser muessen lueckenlos sein und bei 0 beginnen,"
							+ " werden z.B. die Indizes 1 und 3 verwendet,"
							+ " muss auch der Index 2 belegt werden.");
		}

		CommandParser.edit(map.values().toArray(new String[map.size()]));
	}

	/**
	 * Editiert die Backup-Einstellungen. Kann nicht rueckgaengig gemacht
	 * werden.
	 * 
	 * @param pTitle
	 *            der Titel der Backups
	 * @param pTimer
	 *            der Zeitabstand zwischen Backups
	 * @param pOnClose
	 *            ob beim Schliessen ein weiteres Backup erstellt werden soll.
	 */
	public static void editDefBackup(final String pTitle, final String pTimer,
			final boolean pOnClose) {
		TreeMap<Integer, String> backup = new TreeMap<Integer, String>();

		backup.put(0, CommandParser.BACKUP);
		backup.put(CommandParser.BAC_TITLE, pTitle);
		backup.put(CommandParser.BAC_TIMER, pTimer);
		backup.put(CommandParser.BAC_ONCLOSE, CommandParser.fromBool(pOnClose));

		editDefValid(backup);
	}

	/**
	 * Editiert die grundlegenden Planungseinstellungen. Kann nicht rueckgaengig
	 * gemacht werden.
	 * 
	 * @param pGranularity
	 *            die Granularitaet der Planung in Minuten
	 * @param pStartHr
	 *            die Startstunde der Planung
	 * @param pStartMin
	 *            die Startminute der Planung
	 * @param pEndHr
	 *            die Endstunde der Planung
	 * @param pEndMin
	 *            die Endminute der Planung
	 * @param pDays
	 *            die geplanten Wochentage
	 */
	public static void editDefConfig(final String pGranularity,
			final String pStartHr, final String pStartMin, final String pEndHr,
			final String pEndMin, final String pStdDuration,
			final String[] pDays) {
		TreeMap<Integer, String> config = new TreeMap<Integer, String>();

		config.put(0, CommandParser.CONFIG);
		config.put(CommandParser.CON_GRANULARITY, pGranularity);
		config.put(CommandParser.CON_STARTHR, pStartHr);
		config.put(CommandParser.CON_STARTMIN, pStartMin);
		config.put(CommandParser.CON_ENDHR, pEndHr);
		config.put(CommandParser.CON_STDDURATION, pStdDuration);
		config.put(CommandParser.CON_ENDMIN, pEndMin);
		config.put(CommandParser.CON_NUMDAY, String.valueOf(pDays.length));
		for (int i = 0; i < pDays.length; i++) {
			config.put(CommandParser.CON_NUMDAY + i + 1, pDays[i]);
		}

		editDefValid(config);
	}

	/**
	 * Editert eine Pause. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pID
	 * @param pStartHr
	 * @param pStartMin
	 * @param pDuration
	 * @param pYear
	 * @param pTitle
	 * @param pDays
	 */
	public static void editDefBreak(final int pID, final String pStartHr,
			final String pStartMin, final String pDuration, final String pYear,
			final String pTitle, final String[] pDays) {
		TreeMap<Integer, String> breakU = new TreeMap<Integer, String>();
		breakU.put(0, CommandParser.BREAK);
		breakU.put(CommandParser.UNIT_ID, String.valueOf(pID));
		breakU.put(CommandParser.UNIT_STARTHR, pStartHr);
		breakU.put(CommandParser.UNIT_STARTMIN, pStartMin);
		breakU.put(CommandParser.UNIT_DURATION, pDuration);
		breakU.put(CommandParser.BREAK_YEAR, pYear);
		breakU.put(CommandParser.BREAK_TITLE, pTitle);
		breakU.put(CommandParser.BREAK_NUMDAY, String.valueOf(pDays.length));
		for (int i = 0; i < pDays.length; i++) {
			breakU.put(CommandParser.BREAK_NUMDAY + i + 1, pDays[i]);
		}

		editDefValid(breakU);
	}

	/**
	 * Editiert ein Jahrgangstreffen. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pID
	 * @param pDay
	 * @param pStartHr
	 * @param pStartMin
	 * @param pDuration
	 * @param pYear
	 * @param pTitle
	 * @param pForEducs
	 * @param pForTeachers
	 */
	public static void editDefMeeting(final int pID, final String pDay,
			final String pStartHr, final String pStartMin,
			final String pDuration, final String pYear, final String pTitle) {
		TreeMap<Integer, String> meet = new TreeMap<Integer, String>();

		meet.put(0, CommandParser.MEETING);
		meet.put(CommandParser.UNIT_ID, String.valueOf(pID));
		meet.put(CommandParser.UNIT_DAY, pDay);
		meet.put(CommandParser.UNIT_STARTHR, pStartHr);
		meet.put(CommandParser.UNIT_STARTMIN, pStartMin);
		meet.put(CommandParser.UNIT_DURATION, pDuration);
		meet.put(CommandParser.MEET_TITLE, pTitle);
		meet.put(CommandParser.MEET_YEAR, pYear);

		editDefValid(meet);
	}

	/**
	 * Editiert ein Gebaeude. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pName
	 *            der Name des zu aendernden Gebaeudes
	 * @param pDist
	 *            die neue Distanz
	 */
	public static void editDefBuilding(final String pName, final String pDist) {
		TreeMap<Integer, String> building = new TreeMap<Integer, String>();

		building.put(0, CommandParser.BUILDING);
		building.put(CommandParser.BUILD_NAME, pName);
		building.put(CommandParser.BUILD_DIST, pDist);

		editDefValid(building);
	}

	/**
	 * Nimmt die TreeMap und erzeugt daraus ein String-Array, das als Parameter
	 * fuer {@link CommandParser#editDefaults(String[])} verwendet wird.
	 * 
	 * @param map
	 *            die weiterzuverarbeitende map
	 * @throws IllegalArgumentException
	 *             wenn die Indizes nicht lueckenlos verwendet wurden, also der
	 *             letzte Schluessel einen anderen Wert als map.size() - 1 hat.
	 */
	private static void editDefValid(final TreeMap<Integer, String> map) {
		if (map.lastKey() != map.size() - 1) {
			throw new IllegalArgumentException(
					"Die Konstanten im CommandParser muessen lueckenlos sein und bei 0 beginnen,"
							+ " werden z.B. die Indizes 1 und 3 verwendet,"
							+ " muss auch der Index 2 belegt werden.");
		}

		CommandParser
				.editDefaults(map.values().toArray(new String[map.size()]));
	}

	/**
	 * Entfernt den Raum mit dem gegebenen Namen
	 * 
	 * @param pTitle
	 *            der Name des Raums
	 */
	public static void removeRoom(final String pTitle) {
		String[] room = { CommandParser.ROOM, pTitle };
		CommandParser.remove(room);
	}

	/**
	 * Entfernt den Stundeninhalt mit dem Namen.
	 * 
	 * @param pName
	 *            der Name des Stundeninhaltes
	 */
	public static void removeSubject(final String pName) {
		String[] subject = { CommandParser.SUBJECT, pName };
		CommandParser.remove(subject);
	}

	/**
	 * Entfernt die Lehrerin mit dem gegebenen Kuerzel.
	 * 
	 * @param pShort
	 *            das Kuerzel der Lehrerin
	 */
	public static void removeTeacher(final String pShort) {
		String[] teacher = { CommandParser.TEACHER, pShort };
		CommandParser.remove(teacher);
	}

	/**
	 * Entfernt einen paed. Mitarbeiter.
	 * 
	 * @param pShort
	 *            das Kuerzel des paed. Mitarbeiters
	 */
	public static void removeEduc(final String pShort) {
		String[] educ = { CommandParser.EDUC, pShort };
		CommandParser.remove(educ);
	}

	/**
	 * Entfernt eine Klasse
	 * 
	 * @param pYear
	 *            der Jahrgang der Klasse
	 * @param pTitle
	 *            der Titel der Klasse
	 */
	public static void removeClass(final String pYear, final String pTitle) {
		TreeMap<Integer, String> schoolclass = new TreeMap<Integer, String>();
		schoolclass.put(0, CommandParser.CLASS);
		schoolclass.put(CommandParser.CLASS_YEAR, pYear);
		schoolclass.put(CommandParser.CLASS_TITLE, pTitle);

		System.out.println(schoolclass);

		CommandParser.remove(schoolclass.values().toArray(
				new String[schoolclass.size()]));
	}

	/**
	 * Entfernt eine externe Planungseinheit
	 * 
	 * @param pID
	 *            die ID der Planungseinheit
	 */
	public static void removeExtern(final int pID) {
		String[] extern = { CommandParser.EXTERN, String.valueOf(pID) };
		CommandParser.remove(extern);
	}

	/**
	 * Entfernt eine Unterrichtseinheit.
	 * 
	 * @param pID
	 *            die ID der Unterrichtseinheit
	 */
	public static void removeTeaching(final int pID) {
		String[] teaching = { CommandParser.TEACHING, String.valueOf(pID) };
		CommandParser.remove(teaching);
	}

	/**
	 * Entfernt einen individuellen Bedarf einer Klasse, so dass der
	 * Jahrgangswert gilt.
	 * 
	 * @param pClass
	 *            die Klasse
	 * @param pSubject
	 *            das Fach
	 */
	public static void removeClassInfo(final String pClass,
			final String pSubject) {
		String[] classinfo = { CommandParser.CLASSINFO, pClass, pSubject };
		CommandParser.remove(classinfo);
	}

	/**
	 * Entfernt eine Pause. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pID
	 *            die ID der Pause
	 */
	public static void removeDefBreak(final int pID) {
		String[] breakU = { CommandParser.BREAK, String.valueOf(pID) };
		CommandParser.removeDefaults(breakU);
	}

	/**
	 * Entfernt ein Jahrgangstreffen. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pID
	 *            die ID des Jahrgangstreffen
	 */
	public static void removeDefMeeting(final int pID) {
		String[] meet = { CommandParser.MEETING, String.valueOf(pID) };
		CommandParser.removeDefaults(meet);
	}

	/**
	 * Entfernt ein Gebaeude. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pName
	 *            der Name des Gebaeudes
	 */
	public static void removeDefBuilding(final String pName) {
		String[] building = { CommandParser.BUILDING, pName };
		CommandParser.removeDefaults(building);
	}

	/**
	 * Entfernt eine Raumfunktion. Kann nicht rueckgaengig gemacht werden.
	 * 
	 * @param pName
	 *            der Name der Raumfunktion
	 */
	public static void removeDefCategory(final String pName) {
		String[] category = { CommandParser.CATEGORY, pName };
		CommandParser.removeDefaults(category);
	}

	/**
	 * Dieser Befehl fuegt einen Raum dem Datenbestand hinzu.
	 * 
	 * @param pName
	 *            der Name des Raums
	 * @param pBuilding
	 *            der Standort, an dem sich der Raum befindet
	 * @param pNumber
	 *            die Nummer des Raums
	 * @param pCategory
	 *            die dem Raum zugeordnete Raumfunktion
	 */
	public static void addRoom(final String pName, final String pBuilding,
			final String pNumber, final String pCategory) {
		TreeMap<Integer, String> room = new TreeMap<Integer, String>();
		room.put(0, CommandParser.ROOM);
		room.put(CommandParser.ROOM_TITLE, pName);
		room.put(CommandParser.ROOM_BUILDING, pBuilding);
		room.put(CommandParser.ROOM_NUMBER, pNumber);
		room.put(CommandParser.ROOM_CATEGORY, pCategory);

		addValid(room);
	}

	/**
	 * Dieser Befehl fuegt einen Stundeninhalt dem Datenbestand hinzu.
	 * 
	 * @param pTitle
	 *            die Bezeichnung des Stundeninhaltes
	 * @param pDifficulty
	 *            der Schwierigkeitsgrad des Stundeninhaltes
	 * @param pDuration
	 *            die Standarddauer dieses Stundeninhaltes in Minuten
	 * @param pCategory
	 *            die Raumfunktion dieses Stundeninhaltes
	 */
	public static void addSubject(final String pTitle,
			final String pDifficulty, final String pDuration,
			final String pCategory) {
		TreeMap<Integer, String> subject = new TreeMap<Integer, String>();

		subject.put(0, CommandParser.SUBJECT);
		subject.put(CommandParser.SUB_TITLE, pTitle);
		subject.put(CommandParser.SUB_DIFF, pDifficulty);
		subject.put(CommandParser.SUB_DURATION, pDuration);
		subject.put(CommandParser.SUB_CATEGORY, pCategory);

		addValid(subject);
	}

	/**
	 * Fuegt eine Lehrerin dem Datenbestand hinzu.
	 * 
	 * @param pShort
	 *            das Kuerzel der Lehrerin
	 * @param pLast
	 *            der Nachname der Lehrerin
	 * @param pFirst
	 *            der Vorname der Lehrerin
	 * @param pWork
	 *            die Wochenstundenzahl
	 * @param pMinus
	 *            die abzuziehenden Stunden
	 * @param pSubjects
	 *            ein Array der unterrichteten Stundeninhalte
	 * @param pMeetingYear
	 *            der Jahrgang, zu dessem Meeting diese Lehrkraft geht
	 */
	public static void addTeacher(final String pShort, final String pLast,
			final String pFirst, final String pWork, final String pMinus,
			final String[] pSubjects, final String pMeetingYear) {

		TreeMap<Integer, String> teacher = new TreeMap<Integer, String>();

		teacher.put(0, CommandParser.TEACHER);
		teacher.put(CommandParser.PERS_SHORT, pShort);
		teacher.put(CommandParser.PERS_FIRST, pFirst);
		teacher.put(CommandParser.PERS_LAST, pLast);
		teacher.put(CommandParser.PERS_WORK, pWork);
		teacher.put(CommandParser.PERS_MINUS, pMinus);
		teacher.put(CommandParser.TEACHER_YEARMEET, pMeetingYear);
		teacher.put(CommandParser.TEACHER_NUMSUBS,
				String.valueOf(pSubjects.length));

		for (int i = 0; i < pSubjects.length; i++) {
			teacher.put(CommandParser.TEACHER_NUMSUBS + i + 1, pSubjects[i]);
		}

		System.out.println("add teacher " + teacher);

		addValid(teacher);
	}

	/**
	 * Fuegt einen paed. Mitarbeiter
	 * 
	 * @param pShort
	 *            das Kuerzel des paed. Mitarbeiters
	 * @param pLast
	 *            der Nachname
	 * @param pFirst
	 *            der Vorname
	 * @param pWork
	 *            die Wochenstundenzahl
	 * @param pSubjects
	 *            die betreuten Stundeninhalte
	 */
	public static void addEduc(final String pShort, final String pLast,
			final String pFirst, final String pWork, final String pMinus,
			final String[] pSubjects) {
		TreeMap<Integer, String> educ = new TreeMap<Integer, String>();

		educ.put(0, CommandParser.EDUC);
		educ.put(CommandParser.PERS_SHORT, pShort);
		educ.put(CommandParser.PERS_FIRST, pFirst);
		educ.put(CommandParser.PERS_LAST, pLast);
		educ.put(CommandParser.PERS_WORK, pWork);
		educ.put(CommandParser.PERS_MINUS, pMinus);
		educ.put(CommandParser.EDUC_NUMSUBS, String.valueOf(pSubjects.length));
		for (int i = 0; i < pSubjects.length; i++) {
			educ.put(CommandParser.EDUC_NUMSUBS + i + 1, pSubjects[i]);
		}

		System.out.println("add educ " + educ);

		addValid(educ);
	}

	/**
	 * Fuegt eine Schulklasse dem Datenbestand hinzu.
	 * 
	 * @param pYear
	 *            der Jahrgang der Klasse
	 * @param pTitle
	 *            der Bezeichner der Klasse
	 * @param pRoom
	 *            der Klassenraum
	 */
	public static void addClass(final String pYear, final String pTitle,
			final String pRoom) {
		TreeMap<Integer, String> schoolclass = new TreeMap<Integer, String>();

		schoolclass.put(0, CommandParser.CLASS);
		schoolclass.put(CommandParser.CLASS_YEAR, pYear);
		schoolclass.put(CommandParser.CLASS_TITLE, pTitle);
		schoolclass.put(CommandParser.CLASS_ROOM, pRoom);

		addValid(schoolclass);
	}

	/**
	 * Fuegt dem Datenbestand eine externe Planungseinheit hinzu.
	 * 
	 * @param pDay
	 *            der Tag der Planungseinheit
	 * @param pDuration
	 *            die Dauer in Minuten
	 * @param pStarthr
	 *            die Startstunde
	 * @param pStartmin
	 *            die Startminute
	 * @param pTitle
	 *            der Titel
	 * @param pClasses
	 *            die Schulklassen die teilnehmen (muessen)
	 */
	public static void addExtern(final String pDay, final String pDuration,
			final String pStarthr, final String pStartmin, final String pTitle,
			final String[] pClasses) {
		TreeMap<Integer, String> extern = new TreeMap<Integer, String>();

		extern.put(0, CommandParser.EXTERN);
		extern.put(CommandParser.UNIT_ID, "NONE");
		extern.put(CommandParser.UNIT_DAY, pDay);
		extern.put(CommandParser.UNIT_DURATION, pDuration);
		extern.put(CommandParser.UNIT_STARTHR, pStarthr);
		extern.put(CommandParser.UNIT_STARTMIN, pStartmin);
		extern.put(CommandParser.EXT_TITLE, pTitle);
		extern.put(CommandParser.EXT_NUMCLASS, String.valueOf(pClasses.length));
		for (int i = 0; i < pClasses.length; i++) {
			extern.put(CommandParser.EXT_NUMCLASS + i + 1, pClasses[i]);
		}

		addValid(extern);
	}

	/**
	 * Fuegt dem Datenbestand eine externe Planungseinheit mit dem Titel
	 * "Schwimmen" hinzu.
	 * 
	 * @param pDay
	 *            der Tag der Planungseinheit
	 * @param pDuration
	 *            die Dauer in Minuten
	 * @param pStarthr
	 *            die Startstunde
	 * @param pStartmin
	 *            die Startminute
	 * @param pClasses
	 *            die Schulklassen die teilnehmen (muessen)
	 */
	public static void addSwimming(final String pDay, final String pDuration,
			final String pStarthr, final String pStartmin,
			final String[] pClasses) {
		TreeMap<Integer, String> swim = new TreeMap<Integer, String>();

		swim.put(0, CommandParser.EXTERN);
		swim.put(CommandParser.UNIT_ID, "NONE");
		swim.put(CommandParser.UNIT_DAY, pDay);
		swim.put(CommandParser.UNIT_DURATION, pDuration);
		swim.put(CommandParser.UNIT_STARTHR, pStarthr);
		swim.put(CommandParser.UNIT_STARTMIN, pStartmin);
		swim.put(CommandParser.EXT_TITLE, "Schwimmen");
		swim.put(CommandParser.EXT_NUMCLASS, String.valueOf(pClasses.length));
		for (int i = 0; i < pClasses.length; i++) {
			swim.put(CommandParser.EXT_NUMCLASS + i + 1, pClasses[i]);
		}

		addValid(swim);
	}

	/**
	 * Fuegt dem Datenbestand eine Unterrichtseinheit hinzu.
	 * 
	 * @param pDay
	 *            der Tag der Unterrichtseinheit
	 * @param pDuration
	 *            die Dauer in Minuten
	 * @param pStarthr
	 *            die Startstunde
	 * @param pStartmin
	 *            die Startminute
	 * @param pClasses
	 *            die teilnehmenden Schulklassen
	 * @param pRooms
	 *            die verwendeten Raeume
	 * @param pSubjects
	 *            die unterrichteten Stundeninhalte
	 * @param pPersons
	 *            die teilnehmenden Personen (initial waehrend der kompletten
	 *            Unit anwesend)
	 */
	public static void addTeaching(final String pDay, final String pDuration,
			final String pStarthr, final String pStartmin,
			final String[] pClasses, final String[] pRooms,
			final String[] pSubjects, final String[] pPersons) {
		TreeMap<Integer, String> teaching = new TreeMap<Integer, String>();

		teaching.put(0, CommandParser.TEACHING);
		teaching.put(CommandParser.UNIT_ID, "NONE");
		teaching.put(CommandParser.UNIT_DAY, pDay);
		teaching.put(CommandParser.UNIT_DURATION, pDuration);
		teaching.put(CommandParser.UNIT_STARTHR, pStarthr);
		teaching.put(CommandParser.UNIT_STARTMIN, pStartmin);
		teaching.put(CommandParser.TEACH_NUMCLASS,
				String.valueOf(pClasses.length));
		for (int i = 0; i < pClasses.length; i++) {
			teaching.put(CommandParser.TEACH_NUMCLASS + i + 1, pClasses[i]);
		}

		final int num_rooms = teaching.lastKey() + 1;
		teaching.put(num_rooms, String.valueOf(pRooms.length));
		for (int j = 0; j < pRooms.length; j++) {
			teaching.put(num_rooms + j + 1, pRooms[j]);
		}

		final int num_subs = teaching.lastKey() + 1;
		teaching.put(num_subs, String.valueOf(pSubjects.length));
		for (int k = 0; k < pSubjects.length; k++) {
			teaching.put(num_subs + k + 1, pSubjects[k]);
		}

		final int num_pers = teaching.lastKey() + 1;
		teaching.put(num_pers, String.valueOf(pPersons.length));
		for (int l = 0; l < pPersons.length; l++) {
			teaching.put(num_pers + l + 1, pPersons[l]);
		}
		System.out.println("add " + teaching);
		addValid(teaching);
	}

	/**
	 * Fuegt eine Person mit individuellem Timeslot einer TeachingUnit hinzu,
	 * bzw. setzt den Timeslot der Person neu.
	 * 
	 * @param pId
	 *            die Id der TeachingUnit
	 * @param pShort
	 *            das Kuerzel der Person
	 * @param pStartHour
	 *            die neue Startstunde
	 * @param pStartMinute
	 *            die neue Startminute
	 * @param pEndHour
	 *            die Endstunde
	 * @param pEndMinute
	 *            die Endminute
	 */
	public static void addPersonToTeach(final int pId, final String pShort,
			final String pStartHour, final String pStartMinute,
			final String pEndHour, final String pEndMinute) {
		TreeMap<Integer, String> teachingPers = new TreeMap<>();

		teachingPers.put(0, CommandParser.TEACHPERS);
		teachingPers.put(CommandParser.TP_SHORT, pShort);
		teachingPers.put(CommandParser.TP_UID, String.valueOf(pId));
		teachingPers.put(CommandParser.TP_STHR, pStartHour);
		teachingPers.put(CommandParser.TP_STMN, pStartMinute);
		teachingPers.put(CommandParser.TP_ENDHR, pEndHour);
		teachingPers.put(CommandParser.TP_ENDMN, pEndMinute);

		editValid(teachingPers);
	}

	/**
	 * Fuegt einer Klasse einne individuellen Bedarf hinzu.
	 * 
	 * @param pClass
	 *            die Klasse
	 * @param pSubject
	 *            das Fach
	 * @param pIntend
	 *            der Arbeitsbedarf
	 */
	public static void addClassInfo(final String pClass, final String pSubject,
			final String pIntend) {
		TreeMap<Integer, String> classinfo = new TreeMap<>();
		classinfo.put(0, CommandParser.CLASSINFO);
		classinfo.put(CommandParser.CLASSINFO_CLASS, pClass);
		classinfo.put(CommandParser.CLASSINFO_SUB, pSubject);
		classinfo.put(CommandParser.CLASSINFO_WORK, pIntend);

		addValid(classinfo);
	}

	/**
	 * Nimmt die TreeMap und erzeugt daraus ein String-Array, das als Parameter
	 * fuer {@link CommandParser#add(String[])} verwendet wird.
	 * 
	 * @param map
	 *            die weiterzuverarbeitende map
	 * @throws IllegalArgumentException
	 *             wenn die Indizes nicht lueckenlos verwendet wurden, also der
	 *             letzte Schluessel einen anderen Wert als map.size() - 1 hat.
	 */
	private static void addValid(final TreeMap<Integer, String> map) {
		if (map.lastKey() != map.size() - 1) {
			throw new IllegalArgumentException(
					"Die Konstanten im CommandParser muessen lueckenlos sein und bei 0 beginnen,"
							+ " werden z.B. die Indizes 1 und 3 verwendet,"
							+ " muss auch der Index 2 belegt werden.");
		}

		CommandParser.add(map.values().toArray(new String[map.size()]));
	}

	/**
	 * Fuegt dem Datenbestand eine Pause hinzu. Kann nicht rueckgaengig gemacht
	 * werden.
	 * 
	 * @param pDuration
	 *            die Dauer einer Pause
	 * @param pStarthr
	 *            die Startstunde
	 * @param pStartmin
	 *            die Startminute
	 * @param pTitle
	 *            der Titel
	 * @param pYear
	 *            der Jahrgang fuer den diese Pause stattfindet
	 * @param pDays
	 *            die Tage an denen die Pause stattfindet
	 */
	public static void addDefBreak(final String pDuration,
			final String pStarthr, final String pStartmin, final String pTitle,
			final String pYear, final String[] pDays) {
		TreeMap<Integer, String> breakU = new TreeMap<Integer, String>();

		breakU.put(0, CommandParser.BREAK);
		breakU.put(CommandParser.UNIT_ID, "NONE");
		breakU.put(CommandParser.UNIT_DURATION, pDuration);
		breakU.put(CommandParser.UNIT_STARTHR, pStarthr);
		breakU.put(CommandParser.UNIT_STARTMIN, pStartmin);
		breakU.put(CommandParser.BREAK_TITLE, pTitle);
		breakU.put(CommandParser.BREAK_YEAR, pYear);
		breakU.put(CommandParser.BREAK_NUMDAY, String.valueOf(pDays.length));
		for (int i = 0; i < pDays.length; i++) {
			breakU.put(CommandParser.BREAK_NUMDAY + i + 1, pDays[i]);
		}

		addDefValid(breakU);
	}

	/**
	 * Fuegt ein Jahrgangstreffen dem Datenbestand hinzu. Kann nicht
	 * rueckgaengig gemacht werden.
	 * 
	 * @param pDay
	 *            der Tag des Treffens
	 * @param pDuration
	 *            die Dauer in Minuten
	 * @param pStarthr
	 *            die Startstunde
	 * @param pStartmin
	 *            die Startminute
	 * @param pTitle
	 *            der Titel
	 * @param pYear
	 *            der Jahrgang
	 * @param pForEducs
	 *            ob paed. Mitarbeiter erlaubt sind
	 * @param pForTeach
	 *            ob Lehrkraefte erlaubt sind
	 */
	public static void addDefMeeting(final String pDay, final String pDuration,
			final String pStarthr, final String pStartmin, final String pTitle,
			final String pYear) {
		TreeMap<Integer, String> meeting = new TreeMap<Integer, String>();
		meeting.put(0, CommandParser.MEETING);
		meeting.put(CommandParser.UNIT_ID, "NONE");
		meeting.put(CommandParser.UNIT_DAY, pDay);
		meeting.put(CommandParser.UNIT_DURATION, pDuration);
		meeting.put(CommandParser.UNIT_STARTHR, pStarthr);
		meeting.put(CommandParser.UNIT_STARTMIN, pStartmin);
		meeting.put(CommandParser.MEET_TITLE, pTitle);
		meeting.put(CommandParser.MEET_YEAR, pYear);

		addDefValid(meeting);
	}

	/**
	 * Fuegt einen Standort dem Datenbestand hinzu. Kann nicht rueckgaengig
	 * gemacht werden.
	 * 
	 * @param pName
	 *            der Name des Standorts
	 * @param pDist
	 *            die Distanz zum Standort in Minuten
	 */
	public static void addDefBuilding(final String pName, final String pDist) {
		TreeMap<Integer, String> build = new TreeMap<Integer, String>();

		build.put(0, CommandParser.BUILDING);
		build.put(CommandParser.BUILD_NAME, pName);
		build.put(CommandParser.BUILD_DIST, pDist);

		addDefValid(build);
	}

	/**
	 * Fuegt eine Raumfunktion dem Datenbestand hinzu. Kann nicht rueckgaengig
	 * gemacht werden.
	 * 
	 * @param pName
	 *            der Name der Raumfunktion
	 */
	public static void addDefCategory(final String pName) {
		TreeMap<Integer, String> category = new TreeMap<Integer, String>();
		category.put(0, CommandParser.CATEGORY);
		category.put(CommandParser.CAT_NAME, pName);

		addDefValid(category);
	}

	/**
	 * Nimmt die TreeMap und erzeugt daraus ein String-Array, das als Parameter
	 * fuer {@link CommandParser#addDefaults(String[])} verwendet wird.
	 * 
	 * @param map
	 *            die weiterzuverarbeitende map
	 * @throws IllegalArgumentException
	 *             wenn die Indizes nicht lueckenlos verwendet wurden, also der
	 *             letzte Schluessel einen anderen Wert als map.size() - 1 hat.
	 */
	private static void addDefValid(final TreeMap<Integer, String> map) {
		if (map.lastKey() != map.size() - 1) {
			throw new IllegalArgumentException(
					"Die Konstanten im CommandParser muessen lueckenlos sein und bei 0 beginnen,"
							+ " werden z.B. die Indizes 1 und 3 verwendet,"
							+ " muss auch der Index 2 belegt werden.");
		}

		CommandParser.addDefaults(map.values().toArray(new String[map.size()]));
	}

	/**
	 * Laedt das Backup aus dem gegebenen (relativen) Pfad.
	 * 
	 * @param pPath
	 *            der Pfad zum Backup
	 */
	public static void loadBackup(final String pPath, final boolean pBackup) {
		try {
			if(pBackup){
				System.out.println("###### Backup 2");
				DBHandler.restoreBackup(pPath, 2);
			}
			else{
				System.out.println("###### Projekt 3");
				DBHandler.restoreBackup(pPath, 3);
			}
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Speichert das Projekt an dem gegebenen (relativen) Pfad. Erstellt ein
	 * Backup unter einem bestimmten Namen.
	 * 
	 * @param pPath
	 *            der Pfad zum Backup/Projekt
	 */
	public static void saveAsProject(final String pPath) {
		try {
			if (pPath != null && pPath.length() != 0) {
				Calendar cal = Calendar.getInstance();
				Date date = new Date(cal.getTimeInMillis());
				DateFormat df1 = new SimpleDateFormat("dd-MM-yy");
				String formDat = df1.format(date);

				DBHandler.getConfig().setProjectTitle(pPath);
				DBHandler.createBackup("Projekte" + File.separator + pPath
						+ "_" + formDat);
			}
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Speichert das Projekt an dem gespeicherten Pfad. Erstellt ein Backup
	 * unter dem zuvor verwendeten Namen.
	 * 
	 */
	public static void saveProject() {
		String path = DBHandler.getConfig().getProjectTitle();
		try {
			if (path != null) {
				Calendar cal = Calendar.getInstance();
				Date date = new Date(cal.getTimeInMillis());
				DateFormat df1 = new SimpleDateFormat("dd-MM-yy");
				String formDat = df1.format(date);
				DBHandler.createBackup("Projekte" + File.separator + path + "_"
						+ formDat);
			}
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Setzt die CommandHistory zurueck.
	 * 
	 * Soll aufgerufen werden, wenn zwischen den Standardeinstellungen, dem
	 * RootLayout und dem ClassTableView gewechselt wird.
	 */
	public static void resetHistory() {
		CommandParser.resetHistory();
	}

	/**
	 * Macht die letzte Aktion rueckgaengig.
	 */
	public static void undo() {
		CommandParser.undo();
	}

	/**
	 * Wiederholt die letzte Aktion.
	 */
	public static void redo() {
		CommandParser.redo();
	}

	/**
	 * Initialisiert das Projekt
	 * 
	 * @param pTitle
	 *            der Titel der Config
	 */
	public static void initProject(final String pTitle) {
		CommandParser.init("TESTINIT");
	}

	/**
	 * Gibt die Namen aller Raeume zurueck, die in der TeachingUnit enthalten
	 * sind.
	 * 
	 * @param pTeachingUnit
	 * @return Raeume als Strings
	 */
	public static ObservableList<String> extractRooms(
			final TeachingUnit pTeachingUnit) {
		ObservableList<String> unitRooms = FXCollections.observableArrayList();
		if (pTeachingUnit != null) {
			for (Room r : pTeachingUnit.getRooms()) {
				unitRooms.add(r.getName());
			}
		}

		return unitRooms;
	}

	/**
	 * Gibt die Namen aller Stundeninhalte zurueck, die in der TeachingUnit
	 * enthalten sind.
	 * 
	 * @param pTeachingUnit
	 * @return Stundeninhalte als Strings
	 */
	public static ObservableList<String> extractSubjects(
			final TeachingUnit pTeachingUnit) {
		ObservableList<String> unitSubs = FXCollections.observableArrayList();
		if (pTeachingUnit != null) {
			for (Subject s : pTeachingUnit.getSubjects()) {
				unitSubs.add(s.getTitle());
			}
		}
		return unitSubs;
	}

	/**
	 * Gibt die Namen aller Personen zurueck, die in der TeachingUnit enthalten
	 * sind.
	 * 
	 * @param pTeachingUnit
	 * @return Personen als Strings
	 */
	public static ObservableList<String> extractPersons(
			final TeachingUnit pTeachingUnit) {
		ObservableList<String> unitPers = FXCollections.observableArrayList();
		if (pTeachingUnit != null) {
			for (PersonTime pt : pTeachingUnit.getPersons()) {
				unitPers.add(pt.getPerson().getShortName());
			}
		}
		return unitPers;
	}

	/**
	 * Gibt die Namen aller Klassen zurueck, die in der TeachingUnit enthalten
	 * sind.
	 * 
	 * @param pTeachingUnit
	 * @return Klassen als Strings
	 */
	public static ObservableList<String> extractClasses(
			final TeachingUnit pTeachingUnit) {
		ObservableList<String> unitClasses = FXCollections
				.observableArrayList();
		if (pTeachingUnit != null) {
			for (Class c : pTeachingUnit.getClasses()) {
				unitClasses.add(c.getName());
			}
		}
		return unitClasses;
	}

	/**
	 * Gibt den individuellen Timeslot der gegebenen Person in der gegebenen
	 * TeachingUnit zurueck.
	 * 
	 * @param pTeachingUnit
	 *            die betrachtete Unit
	 * @param pPerson
	 *            die Person
	 * @return ihr individueller Timeslot
	 */
	public static Timeslot extractTimeslot(final TeachingUnit pTeachingUnit,
			final Person pPerson) {
		if (pTeachingUnit != null && pPerson != null) {
			Set<PersonTime> spt = pTeachingUnit.getPersons();
			for (PersonTime pt : spt) {
				if (pt.getPerson().getShortName()
						.equals(pPerson.getShortName())) {
					return pt.getTimeslot();
				}
			}
		}
		return null;
	}

	/**
	 * Gibt die Startstunde zurueck
	 * 
	 * @return die Startstunde
	 */
	public static int getStarttime() {
		return getConfig().getTimeframe().getStartHour();
	}

	/**
	 * Gibt die gerundete Duration des planbaren Tages in Stunden zurueck.
	 * 
	 * @return die gerundete Dauer in Stunden
	 */
	public static int getDisplayedHours() {
		double x = Math.abs(getConfig().getTimeframe().getDuration() / 60);
		int i = (int) x;
		double result = x - (double) i;
		if (result == 0.0) {
			return (int) x;
		} else {
			return (int) x < 0 ? -(i + 1) : i + 1;
		}
	}

	/**
	 * Gibt die Granularitaet zurueck.
	 * 
	 * @return die Granularitaet
	 */
	public static int getGranularity() {
		return getConfig().getGranularity();
	}
}
