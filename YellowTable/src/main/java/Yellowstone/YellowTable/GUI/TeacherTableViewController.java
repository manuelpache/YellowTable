/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importiert die Logikkomponenten, sowie zentrale Elemente des Java-FXML-Pakets.
 * In dieser Klasse wurde die JFXtras-Agenda verwendet, wir haben diese zwar Teilweise editiert, stützen uns aber auf deren Code.
 */
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import au.com.bytecode.opencsv.CSVWriter;
import Yellowstone.YellowTable.logic.components.Break;
import Yellowstone.YellowTable.logic.components.BreakUnit;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.logic.components.ExternUnit;
import Yellowstone.YellowTable.logic.components.MeetingUnit;
import Yellowstone.YellowTable.logic.components.Person;
import Yellowstone.YellowTable.logic.components.PersonTime;
import Yellowstone.YellowTable.logic.components.Room;
import Yellowstone.YellowTable.logic.components.Subject;
import Yellowstone.YellowTable.logic.components.Teacher;
import Yellowstone.YellowTable.logic.components.TeachingUnit;
import Yellowstone.YellowTable.logic.components.Timeslot;
import Yellowstone.YellowTable.logic.components.Unit;
import Yellowstone.YellowTable.logic.components.WEEKDAYS;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImpl;

/**
 * Controller des Views der einen individuellen Lehrerstundenplan anzeigt.
 * 
 * @author tomlewandowski, manuelpache , Erik
 *
 */
public class TeacherTableViewController {

	// Agenda

	/**
	 * Das Agenda Attribut, um den Plan anzuzeigen.
	 */
	@FXML
	private Agenda agendaTable;

	/**
	 * Map für die AppointmentGroups
	 */
	final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Node für Export-PDF funktion
	 */
	private Node node;

	/**
	 * Rootlayout variable um ausgewählte Objekte abrufen zu können
	 */
	private RootLayoutViewController rootlayout;

	/**
	 * Initiale Daten für die Skalierung der Agenda
	 */
	private int scaleHours;

	/**
	 * TODO Kommentar
	 */
	private int fromHours;

	/**
	 * TODO Kommentar
	 */
	private int granularity;

	/**
	 * TODO Kommentar
	 */
	private int hourHeight;

	/**
	 * TODO Kommentar
	 */
	private Teacher selcTeach;

	/**
	 * Pfad zum Ordner
	 */
	String path = (System.getProperty("user.dir")) + File.separator;

	/**
	 * Initialisiert die Klasse.
	 */
	public void initialize() {

		/*
		 * Initialisiert die Attribute einer Agenda
		 */
		scaleHours = GUIHandler.getDisplayedHours();
		fromHours = GUIHandler.getStarttime();
		granularity = GUIHandler.getGranularity();
		hourHeight = 100;

		// Agenda
		Calendar cal2 = new GregorianCalendar(2055, 1, 1);
		agendaTable.withDisplayedCalendar(cal2);
		agendaTable.construct(scaleHours, fromHours, granularity, hourHeight);

		setAgendaUp();

	}

	/**
	 * Übergibt das rootlayout und damit den ausgwewählten lehrer
	 * 
	 * @param root
	 */
	public void loadDataToAgenda(RootLayoutViewController root) {
		rootlayout = root;
		selcTeach = rootlayout.getSelectedTeacher();
		loadAppointments(selcTeach);
	}

	/**
	 * Erstellt mehrere AppointmentGroups
	 */
	public void setAgendaUp() {
		lAppointmentGroupMap.put("group01",
				new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
		lAppointmentGroupMap.put("group00",
				new Agenda.AppointmentGroupImpl().withStyleClass("group0"));
		lAppointmentGroupMap.put("group02",
				new Agenda.AppointmentGroupImpl().withStyleClass("group2"));
		lAppointmentGroupMap.put("group03",
				new Agenda.AppointmentGroupImpl().withStyleClass("group23"));
		lAppointmentGroupMap.put("group04",
				new Agenda.AppointmentGroupImpl().withStyleClass("group3"));

		for (String lId : lAppointmentGroupMap.keySet()) {
			Agenda.AppointmentGroup lAppointmentGroup = lAppointmentGroupMap
					.get(lId);
			lAppointmentGroup.setDescription(lId);
			agendaTable.appointmentGroups().add(lAppointmentGroup);
		}
	}
	
	/**
	 * Errechnet einen Calendar aus einem Timeslot
	 * @param s
	 */
	public Calendar calculateStartTime(Timeslot s) {
		// Startzeit
		int startHour = s.getStartHour();
		int startMinute = s.getStartMinute();
		// Wochentag
		int weekday = s.getDay().ordinal();
		Calendar calStart = new GregorianCalendar(2055, 1, 1, 0, 0);
		// Setzt die Startzeit in den Kalender
		calStart.set(Calendar.HOUR, startHour);
		calStart.set(Calendar.MINUTE, startMinute);
		calStart.set(Calendar.DAY_OF_MONTH, weekday);
		return calStart;
	}
	
	/**
	 * Errechnet einen Calendar aus einem Timeslot
	 * @param s
	 */
	public Calendar calculateEndTime(Timeslot s) {
		// Endzeit
		int endHour = s.getEndHour_int();
		int endMinute = s.getEndMinute_int();
		// Wochentag
		int weekday = s.getDay().ordinal();
		Calendar calEnd = new GregorianCalendar(2055, 1, 1, 0, 0);
		calEnd.set(Calendar.HOUR, endHour);
		calEnd.set(Calendar.MINUTE, endMinute);
		calEnd.set(Calendar.DAY_OF_MONTH, weekday);
		return calEnd;
	}

	/**
	 * Läd Appointments aus der Datenbank in die Agenda.
	 * 
	 * @param selClass
	 */
	public void loadAppointments(Teacher seledTeach) {
		if (seledTeach != null && GUIHandler.getWeekTable(seledTeach) != null) {
			for (Unit u : GUIHandler.getWeekTable(seledTeach)) {
				
				Calendar calStart = calculateStartTime(u.getTimeslot());
				Calendar calEnd = calculateEndTime(u.getTimeslot());
				
				if (u instanceof ExternUnit) {
					Appointment appoint = new AppointmentImpl()
							.withStartTime(calStart)
							.withEndTime(calEnd)
							.withUnit(u)
							.withDraggable(false)
							.withSummary("Schwimmen")
							.withAppointmentGroup(
									lAppointmentGroupMap.get("group02"));
					agendaTable.appointments().add(appoint);
				}
				if (u instanceof TeachingUnit) {
					TeachingUnit unit = (TeachingUnit) u;
					Timeslot slot = GUIHandler
							.extractTimeslot(unit, seledTeach);
					
					Calendar inviCalS = calculateStartTime(slot);
					Calendar inviCalE = calculateEndTime(slot);
					
					Appointment appoint = new AppointmentImpl()
							.withStartTime(inviCalS)
							.withEndTime(inviCalE)
							.withUnit(u)
							.withDraggable(false)
							.withSummary(unit.toString())
							.withAppointmentGroup(
									lAppointmentGroupMap.get("group01"));

					agendaTable.appointments().add(appoint);
				}
				if (u instanceof MeetingUnit) {
					MeetingUnit unit = (MeetingUnit) u;
					Appointment appoint = new AppointmentImpl()
							.withStartTime(calStart)
							.withEndTime(calEnd)
							.withUnit(u)
							.withDraggable(false)
							.withSummary(unit.getTitle())
							.withAppointmentGroup(
									lAppointmentGroupMap.get("group04"));

					agendaTable.appointments().add(appoint);
				}
				if (u instanceof Break) {
					Break bu = (Break) u;
					createBreakUnit(bu);
				}
			}
		}
	}

	/**
	 * Läd alle BreakUnits einer Break in die Agenda
	 * 
	 * @param b
	 */
	public void createBreakUnit(Break b) {
		for (BreakUnit brUn : b.getBreakUnits()) {
			int startHour = brUn.getTimeslot().getStartHour();
			int startMinute = brUn.getTimeslot().getStartMinute();
			// Endzeit
			int endHour = brUn.getTimeslot().getEndHour_int();
			int endMinute = brUn.getTimeslot().getEndMinute_int();
			// Wochentag
			int weekday = brUn.getTimeslot().getDay().ordinal();
			// Kalendar für Start und Endzeit
			Calendar calStart = new GregorianCalendar(2055, 1, 1, 0, 0);
			Calendar calEnd = new GregorianCalendar(2055, 1, 1, 0, 0);
			// Setzt die Startzeit in den Kalender
			calStart.set(Calendar.HOUR, startHour);
			calStart.set(Calendar.MINUTE, startMinute);
			calStart.set(Calendar.DAY_OF_MONTH, weekday);
			// Setzt die Endzeit in den Kalender
			calEnd.set(Calendar.HOUR, endHour);
			calEnd.set(Calendar.MINUTE, endMinute);
			calEnd.set(Calendar.DAY_OF_MONTH, weekday);

			Appointment appoint = new AppointmentImpl().withStartTime(calStart)
					.withEndTime(calEnd).withUnit(brUn).withDraggable(false)
					.withSummary(b.getTitle())
					.withAppointmentGroup(lAppointmentGroupMap.get("group03"));

			agendaTable.appointments().add(appoint);
		}
	}

	/**
	 * Aktionen des Buttons, mit dem man zum Hauptmenue zurueckkehrt.
	 */
	@FXML
	public void btnRootLayout() {
		dialogStage.close();
	}

	/**
	 * Setzt die DialogStage neu..
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * pdf-Export Button
	 * 
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	@FXML
	public void bPdf() throws IOException, COSVisitorException {
		generateScreen(agendaTable.getAgendaNode());
		String dirName = "PDF";
		File dir = new File(path + dirName);
		dir.mkdir();
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A1);
		document.addPage(page1);
		// PDPageContentStream cos = new PDPageContentStream(document, page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", node.getId() + (".png"), dir
				+ File.separator + "Lehrer-Stundenplan " + selcTeach.getShortName()+(".pdf"), false);
		File f1 = new File(node.getId() + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
	}

	/**
	 * csv-Export Button
	 */
	@FXML
	private void bCsv() throws IOException {
		// CSV Pfad erstellen?
		String dirName = "CSV";
		File dir = new File(path + dirName);
		dir.mkdir();
		// Funktion
		String csv = "Lehrer-Stundenplan ";
		CSVWriter writer = new CSVWriter(new FileWriter(dir + File.separator
				+ csv+selcTeach.getShortName()+".csv"));
		List<String[]> data = new ArrayList<String[]>();
		ArrayList<Unit> ultimate = new ArrayList<Unit>();
		ultimate.addAll(GUIHandler.getWeekTable(selcTeach));
		ArrayList<ArrayList<Unit>> exportList = new ArrayList<ArrayList<Unit>>();
		ArrayList<Unit> mon = new ArrayList<Unit>();
		ArrayList<Unit> die = new ArrayList<Unit>();
		ArrayList<Unit> mit = new ArrayList<Unit>();
		ArrayList<Unit> don = new ArrayList<Unit>();
		ArrayList<Unit> fre = new ArrayList<Unit>();
		ArrayList<Unit> sam = new ArrayList<Unit>();
		ArrayList<Unit> son = new ArrayList<Unit>();
		for (Unit un : ultimate) {
			if (un.getTimeslot().getDay().equals(WEEKDAYS.MONDAY)) {
				mon.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.TUESDAY)) {
				die.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.WEDNESDAY)) {
				mit.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.THURSDAY)) {
				don.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.FRIDAY)) {
				fre.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.SATURDAY)) {
				sam.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.SUNDAY)) {
				son.add(un);
			}
		}

		Collections.sort(mon);
		Collections.sort(die);
		Collections.sort(mit);
		Collections.sort(don);
		Collections.sort(fre);
		Collections.sort(sam);
		Collections.sort(son);
		exportList.add(mon);
		exportList.add(die);
		exportList.add(mit);
		exportList.add(don);
		exportList.add(fre);
		exportList.add(sam);
		exportList.add(son);

		for (ArrayList<Unit> a : exportList) {
			if (a.size() > 0) {

				data.add(new String[] { "",
						a.get(0).getTimeslot().getDay().getTitle(), "" });
				System.out.println(a.get(0).getTimeslot().getDay().getTitle());
			}
			data.add(new String[] { "", "", "" });
			for (Unit u : a) {
				u.getTimeslot().setStarttime();
				u.getTimeslot().setEndTime();
				data.add(new String[] {
						"***",
						"Uhrzeit: ",
						u.getTimeslot().getStarttime() + "-"
								+ u.getTimeslot().getEndTime() });
				if (u instanceof TeachingUnit) {
					ArrayList<Subject> subjects = new ArrayList<Subject>();
					ArrayList<PersonTime> personTime = new ArrayList<PersonTime>();
					ArrayList<Room> rooms = new ArrayList<Room>();
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((TeachingUnit) u).getClasses());
					personTime.addAll(((TeachingUnit) u).getPersons());
					subjects.addAll(((TeachingUnit) u).getSubjects());
					rooms.addAll(((TeachingUnit) u).getRooms());
					String subjectsString = "";
					String teachersString = "";
					String roomsString = "";
					String classString = "";
					String pendelString = "";

					for (Subject s : subjects) {
						subjectsString += s.getTitle();
						subjectsString += ",";
					}
					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					for (Room r : rooms) {
						roomsString += r.getName();
						roomsString += ",";
					}
					for (PersonTime t : personTime) {
						teachersString += t.getPerson().getShortName();
						teachersString += ",";
					}
					if (teachersString != "") {
						teachersString = teachersString.substring(0,
								teachersString.length() - 1);
					}
					if (roomsString != "") {
						roomsString = roomsString.substring(0,
								roomsString.length() - 1);
					}
					if (subjectsString != "") {
						subjectsString = subjectsString.substring(0,
								subjectsString.length() - 1);
					}
					if (classString != "") {
						classString = classString.substring(0,
								classString.length() - 1);
					}
					if(((TeachingUnit) u).getTravelNeed()){
						pendelString+="(P)";
					}
					data.add(new String[] { subjectsString, roomsString,
							teachersString, classString,pendelString});
					data.add(new String[] { "", "", "" });

				}

				if (u instanceof BreakUnit) {
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((BreakUnit) u).getClasses());
					String classString = "";
					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					if (classString != "") {
						classString = classString.substring(0,
								classString.length() - 1);
					}
					data.add(new String[] { "***", "Pause", classString });
					data.add(new String[] { "", "", "" });

				}
				if (u instanceof ExternUnit) {
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((ExternUnit) u).getClasses());
					String classString = "";
					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					if (classString != "") {
						classString = classString.substring(0,
								classString.length() - 1);
					}
					data.add(new String[] { "Extern",
							((ExternUnit) u).getTitle(), classString });
					data.add(new String[] { "", "", "" });
				}

				if (u instanceof MeetingUnit) {
					ArrayList<Room> rooms = new ArrayList<Room>();
					ArrayList<Person> teachers = new ArrayList<Person>();
					rooms.addAll(((MeetingUnit) u).getRooms());
					teachers.addAll(((MeetingUnit) u).getMembers());
					int year = ((MeetingUnit) u).getYear();
					String roomsString = "";
					String teachersString = "";
					for (Room r : rooms) {
						roomsString += r.getName();
						roomsString += ",";
					}
					for (Person p : teachers) {
						teachersString += p.getShortName();
						teachersString += ",";
					}
					if (teachersString != "") {
						teachersString = teachersString.substring(0,
								teachersString.length() - 1);
					}
					if (roomsString != "") {
						roomsString = roomsString.substring(0,
								roomsString.length() - 1);
					}
					data.add(new String[] { ((MeetingUnit) u).getTitle(),
							roomsString, teachersString, Integer.toString(year) });

				}
			}
		}

		writer.writeAll(data);
		writer.close();
	}

	/**
	 * txt-Export Button
	 */
	@FXML
	private void bTxt() throws IOException {
		// Directory erstellen mithilfe der pathvariable (TXT-ordner erstellen)
		String dirName = "TXT";
		File dir = new File(path + dirName);
		dir.mkdir();
		BufferedWriter out = new BufferedWriter(new FileWriter(dir
				+ File.separator + "Lehrerstundenplan "+ selcTeach.getShortName()+".txt"));
		out.newLine();
		ArrayList<Unit> ultimate = new ArrayList<Unit>();
		ultimate.addAll(GUIHandler.getWeekTable(selcTeach));
		ArrayList<ArrayList<Unit>> exportList = new ArrayList<ArrayList<Unit>>();
		ArrayList<Unit> mon = new ArrayList<Unit>();
		ArrayList<Unit> die = new ArrayList<Unit>();
		ArrayList<Unit> mit = new ArrayList<Unit>();
		ArrayList<Unit> don = new ArrayList<Unit>();
		ArrayList<Unit> fre = new ArrayList<Unit>();
		ArrayList<Unit> sam = new ArrayList<Unit>();
		ArrayList<Unit> son = new ArrayList<Unit>();
		for (Unit un : ultimate) {
			if (un.getTimeslot().getDay().equals(WEEKDAYS.MONDAY)) {
				mon.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.TUESDAY)) {
				die.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.WEDNESDAY)) {
				mit.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.THURSDAY)) {
				don.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.FRIDAY)) {
				fre.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.SATURDAY)) {
				sam.add(un);
			}
			if (un.getTimeslot().getDay().equals(WEEKDAYS.SUNDAY)) {
				son.add(un);
			}
		}

		Collections.sort(mon);
		Collections.sort(die);
		Collections.sort(mit);
		Collections.sort(don);
		Collections.sort(fre);
		Collections.sort(sam);
		Collections.sort(son);
		exportList.add(mon);
		exportList.add(die);
		exportList.add(mit);
		exportList.add(don);
		exportList.add(fre);
		exportList.add(sam);
		exportList.add(son);

		// Oberliste fÃƒÆ’Ã‚Â¼r die ganze Woche durchlaufen
		for (ArrayList<Unit> u : exportList) { // Wochentag reinschreiben ins
												// Dokument
			if (u.size() > 0) {
				out.write(u.get(0).getTimeslot().getDay().getTitle());
				out.newLine();
				out.write("-------");
			}
			// Jeweils fÃƒÆ’Ã‚Â¼r den Wochentag dann alle Units durchlaufen
			for (Unit c : u) {
				out.newLine();
				out.newLine();
				// Zu aller Anfang Start und Endzeit des Slots angeben
				if (u.size() > 0) {
					c.getTimeslot().setStarttime();
					c.getTimeslot().setEndTime();
					out.write("Uhrzeit: " + c.getTimeslot().getStarttime()
							+ "-" + c.getTimeslot().getEndTime());
					out.newLine();
				}
				// wenn das aktuelle Unit ein TeachingUnit ist.
				if (c instanceof TeachingUnit) {
					out.write("***");
					out.write("Unterricht");
					out.newLine();
					// Alle wesentlichen Attribute des Units in Listen
					// abspeichern.
					ArrayList<Subject> subjects = new ArrayList<Subject>();
					ArrayList<PersonTime> personTime = new ArrayList<PersonTime>();
					ArrayList<Room> rooms = new ArrayList<Room>();
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((TeachingUnit) c).getClasses());
					personTime.addAll(((TeachingUnit) c).getPersons());
					subjects.addAll(((TeachingUnit) c).getSubjects());
					rooms.addAll(((TeachingUnit) c).getRooms());
					// Strings initialisieren zum spÃƒÆ’Ã‚Â¤teren einsetzen der
					// Attribute.
					String subjectsString = "";
					String teachersString = "";
					String roomsString = "";
					String classString = "";
					// Alle subjects des Units in den String schreiben.
					for (Subject s : subjects) {
						subjectsString += s.getTitle();
						subjectsString += ",";
					}
					// Wenn der subjectsString nicht leer ist, ihn ausgeben
					if (!(subjectsString.equals(""))) {
						out.write("Fach: "
								+ subjectsString.substring(0,
										subjectsString.length() - 1)); // letztes
																		// Komma
																		// wird
																		// abgetrennt
						out.newLine();

					}
					// Zaehlervariable zum durchlaufen einer HashMap
					int x = 0;
					// Alle Lehrer der Liste durchlaufen
					for (PersonTime p : personTime) {
						teachersString += p.getPerson().getShortName();
						teachersString += ",";
						// Wenn mehr als ein Lehrer vorhanden->
						if (personTime.size() > 1) {
							if (teachersString.endsWith(",")) {
								teachersString = teachersString.substring(0,
										teachersString.length() - 1);
							}
							teachersString += " ";
							// Anwesenheitszeiten der Lehrer mit reinschreiben,
							// da mehrere Lehre vorhanden sind und Zeiten
							// varieeren koennen-
							teachersString += p.getTimeslot().getStarttime();
							teachersString += ":";
							teachersString += p.getTimeslot().getEndTime();
							teachersString += ",";
							// Variable hochzaehlen
							x = x + 1;
						}

					}

					// Formatiertes Ausgeben aller fertigen Strings in das
					// Dokument

					if (!(teachersString.equals(""))) {
						out.write("Personen: "
								+ teachersString.substring(0,
										teachersString.length() - 1));
						out.newLine();
					}

					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					if (!(classString.equals(""))) {
						out.write("Klasse: "
								+ classString.substring(0,
										classString.length() - 1));
						out.newLine();
					}
					for (Room r : rooms) {
						roomsString += r.getName();
						roomsString += ",";
					}

					if (!(roomsString.equals(""))) {
						out.write("Raum: "
								+ roomsString.substring(0,
										roomsString.length() - 1));
						if(((TeachingUnit) c).getTravelNeed()){
							out.write("  (P)");
						}
					}
					out.newLine();
					out.newLine();
				}
				// Wenn es sich bei dem Unit um eine Pause handelt.
				if (c instanceof BreakUnit) {
					out.write("***");
					out.write("Pause");
					out.newLine();
					// Teilnehmenden Klassen in eine Liste speichern.
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((BreakUnit) c).getClasses());
					String classString = "";
					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					// Wenn es Klassen in der Liste gibt, dann diese Ausgeben
					// und letztes Komma abtrennen falls mehrere vorhanden sind.
					if (!(classString.equals(""))) {
						out.write("Klasse: "
								+ classString.substring(0,
										classString.length() - 1));
						out.newLine();
						out.newLine();
					}

				}
				// Wenn es sich bei dem Unit um eine externe Einheit handelt.
				if (c instanceof ExternUnit) {
					out.write("***");
					out.write(((ExternUnit) c).getTitle());
					out.newLine();
					// Klassen des Units in ein HashSet speichern.
					Set<Class> classes = new HashSet<Class>();
					classes.addAll(((ExternUnit) c).getClasses());
					String classString = "";
					for (Class d : classes) {
						classString += d.getName();
						classString += ",";
					}
					// Formatiertes schreiben der Klassen ins Dokument.
					if (!(classString.equals(""))) {
						out.write("Klasse: "
								+ classString.substring(0,
										classString.length() - 1));
						out.newLine();
					}
				}
				// Wenn es sich bei dem Unit um ein MeetingUnit handelt.
				if (c instanceof MeetingUnit) {
					out.write("***");
					out.write("Konferrenz");
					out.write(((MeetingUnit) c).getTitle());
					out.newLine();
					// Gebrauchten Attribute in Listen speichern.
					ArrayList<Room> rooms = new ArrayList<Room>();
					ArrayList<Person> teachers = new ArrayList<Person>();
					rooms.addAll(((MeetingUnit) c).getRooms());
					teachers.addAll(((MeetingUnit) c).getMembers());
					int year = ((MeetingUnit) c).getYear();
					// Leere Strings initialisieren..
					String roomsString = "";
					String teachersString = "";
					for (Room r : rooms) {
						roomsString += r.getName();
						roomsString += ",";
					}
					// Formatiertes ausgeben der Attribute in das Dokument.
					if (!(roomsString.equals(""))) {
						out.write("Raum: "
								+ roomsString.substring(0,
										roomsString.length() - 1));
						out.newLine();
					}

					for (Person p : teachers) {
						teachersString += p.getShortName();
						teachersString += ",";
					}
					if (!(teachersString.equals(""))) {
						out.write("Teilnehmer: "
								+ teachersString.substring(0,
										teachersString.length() - 1));
						out.newLine();
					}
					out.write("Jahrgang" + year);

				}
			}// Dokument wird geschlossen.
			out.newLine();
			out.newLine();
		}
		out.close();
	}

	/**
	 * Erstellt eine PDF aus einer PNG und einer Ausgangsdatei im PDF Format,
	 */
	private void createPDFFromImage(String inputFile, String image,
			String outputFile, Boolean print) throws IOException,
			COSVisitorException {
		// the document
		PDDocument doc = null;
		try {
			doc = PDDocument.load(inputFile);
			// we will add the image to the first page.
			PDPage page = (PDPage) doc.getDocumentCatalog().getAllPages()
					.get(0);
			PDXObjectImage ximage = null;
			BufferedImage awtImage = ImageIO.read(new File(image));
			ximage = new PDPixelMap(doc, awtImage);
			PDPageContentStream contentStream = new PDPageContentStream(doc,
					page, true, true);
			if (print) {
				contentStream.drawXObject(ximage, 20, 0, 490, 750);
			} else {
				contentStream.drawImage(ximage, 20, 950);
			}
			contentStream.close();
			doc.save(outputFile);
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	/**
	 * Erstellt einen Screenshot von der jeweiligen Node
	 */
	private void generateScreen(Node pNode) {
		node = pNode;
		WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
		File file = new File(node.getId() + (".png"));
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);

		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Druckt den jeweiligen Plan.
	 * 
	 * @throws IOException
	 * @throws PrinterException
	 * @throws COSVisitorException
	 */
	@FXML
	public void print() throws IOException, PrinterException,
			COSVisitorException {
		generateScreen(agendaTable.getAgendaNode());
		String dirName = "PRINT";
		File dir = new File(path + dirName);
		dir.mkdir();
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);
		document.addPage(page1);
		// PDPageContentStream cos = new PDPageContentStream(document, page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", node.getId() + (".png"), dir
				+ File.separator + "Lehrer-Stundenplan" + (".pdf"), true);
		File f1 = new File(node.getId() + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
		PDDocument doc2 = null;
		try {
			doc2 = PDDocument.load(dir + File.separator + "Lehrer-Stundenplan"
					+ (".pdf"));
			doc2.print();
		} catch (Exception ec) {
			ec.printStackTrace();
		} finally {
			if (doc2 != null) {
				try {
					doc2.close();
				} catch (IOException ec2) {
					ec2.printStackTrace();
				}
			}
		}
	}
}