/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importiert die Logikkomponenten, sowie zentrale Elemente des Java-FXML-Pakets.
 */
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import au.com.bytecode.opencsv.CSVWriter;
import Yellowstone.YellowTable.logic.components.Teacher;
import Yellowstone.YellowTable.logic.components.Timeslot;
import Yellowstone.YellowTable.logic.components.WEEKDAYS;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImpl;

/**
 * Controller fuer den Wishes-View.
 * 
 * @author tomlewandowski, manuelpache, Erik
 *
 */
public class WishesViewController {

	/**
	 * Das Agenda Attribut, um die präferierten Zeiten anzugeben.
	 */
	@FXML
	private Agenda agendaTable;

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Map für die AppointmentGroups
	 */
	final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();

	/**
	 * Node
	 */
	private Node node;

	/**
	 * Pfad zum Ordner (Wichtig fuer den Datei-Export).
	 */
	String path = (System.getProperty("user.dir")) + File.separator;

	/**
	 * Setzt gesetzten werte für Zeiten und Darstellung der Agenda
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
	 * Der im Rootlayout gewählte Lehrer
	 */
	private Teacher selecTeach;

	/**
	 * Das Rootlayout um die gewählten Lehrer zu ermitteln
	 */
	private RootLayoutViewController rootlayout;

	/**
	 * Initialisiert die Klasse. sowie die Agenda.
	 */
	public void initialize() {

		// Agenda
		setAgendaUp();

		Calendar cal2 = new GregorianCalendar(2055, 1, 1);
		agendaTable.withDisplayedCalendar(cal2);

		scaleHours = GUIHandler.getDisplayedHours();
		fromHours = GUIHandler.getStarttime();
		granularity = GUIHandler.getGranularity();
		hourHeight = 100;

		agendaTable.construct(scaleHours, fromHours, granularity, hourHeight);

		agendaTable.createAppointmentCallbackProperty().set(
				new Callback<Agenda.CalendarRange, Agenda.Appointment>() {
					@Override
					public Agenda.Appointment call(
							Agenda.CalendarRange calendarRange) {
						return new Agenda.AppointmentImpl()
								.withStartTime(calendarRange.getStartCalendar())
								.withEndTime(calendarRange.getEndCalendar())
								.withSummary("Lehrerwünsch")
								.withAppointmentGroup(
										lAppointmentGroupMap.get("group01"))
								.withDraggable(true);
					}
				});

		agendaTable
				.selectedAppointmentProperty()
				.addListener(
						(observable, oldValue, newValue) -> checkIfDayIsValid(newValue));
	}

	/**
	 * Prüft ob ein Appointment an einem planbaren Tag erstellt worden ist.
	 * 
	 * @param a
	 */
	public void checkIfDayIsValid(Appointment a) {
		if (a != null) {
			int Iday = a.getStartTime().get(Calendar.DAY_OF_MONTH);
			WEEKDAYS day = WEEKDAYS.values()[Iday];
			if (!(GUIHandler.getConfig().getPlannedDays().contains(day))) {
				a.setSummary("Nicht planbarer Tag");
				a.setAppointmentGroup(lAppointmentGroupMap.get("group00"));
			} else {
				a.setSummary("Lehrerwunsch");
				a.setAppointmentGroup(lAppointmentGroupMap.get("group01"));
			}
		}
	}

	/**
	 * Läd die Wünsche eines ausgewählten Lehrers
	 * 
	 * @param root
	 */
	public void loadDataToAgenda(RootLayoutViewController root) {
		rootlayout = root;
		selecTeach = rootlayout.getSelectedTeacher();
		loadAppointments(selecTeach);
	}

	/**
	 * Lädt Wünschpläne in die Agenda
	 * 
	 * @param root
	 */
	public void loadAppointments(Teacher selectedTeacher) {
		if (selectedTeacher != null) {
			for (Timeslot ts : GUIHandler.getTeacherWishes(selectedTeacher)) {
				int startHour = ts.getStartHour();
				int startMinute = ts.getStartMinute();
				// Endzeit
				int endHour = ts.getEndHour_int();
				int endMinute = ts.getEndMinute_int();
				// Wochentag
				int weekday = ts.getDay().ordinal();
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

				Appointment appoint = new AppointmentImpl()
						.withStartTime(calStart).withEndTime(calEnd)
						.withDraggable(true).withSummary("Lehrerwunsch");
				checkIfDayIsValid(appoint);

				agendaTable.appointments().add(appoint);
			}
		}
	}

	/**
	 * Speichert alle Appointments
	 */
	public void saveAppointment() {
		Collection<Timeslot> timeslots = new ArrayList<Timeslot>();
		for (Appointment a : agendaTable.appointments()) {
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, 2055);
			cal.set(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH,
					a.getStartTime().get(Calendar.DAY_OF_MONTH));

			int startH = a.getStartTime().get(Calendar.HOUR_OF_DAY);
			int startM = a.getStartTime().get(Calendar.MINUTE);
			int endH = a.getEndTime().get(Calendar.HOUR_OF_DAY);
			int endM = a.getEndTime().get(Calendar.MINUTE);
			int dayI = cal.get(Calendar.DAY_OF_MONTH);

			String startHStr = Timeslot.addStringHour(startH);
			String startMStr = Timeslot.addStringMinute(startM);
			String endHStr = Timeslot.addStringHour(endH);
			String endMStr = Timeslot.addStringMinute(endM);
			WEEKDAYS dayW = WEEKDAYS.values()[dayI];

			int duration = Timeslot.getDurationByStartAndEndtime(startHStr,
					startMStr, endHStr, endMStr);

			Timeslot times = new Timeslot(duration, dayW, startHStr, startMStr);
			timeslots.add(times);
		}
		GUIHandler.setTeacherWishes(selecTeach, timeslots);
	}

	/**
	 * Button, mit dem man zum Hauptmenue zurueckkehrt.
	 */
	@FXML
	public void btnRootLayout() {
		saveAppointment();
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
	private void bPdf() throws IOException, COSVisitorException {
		if (selecTeach != null) {
			saveAppointment();
		}
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
		createPDFFromImage(
				"rsc.pdf",
				node.getId() + (".png"),
				dir + File.separator + "Lehrerwuensche "
						+ selecTeach.getShortName() + (".pdf"));
		File f1 = new File(node.getId() + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
	}

	/**
	 * Erstellt eine PDF aus einer PNG und einer Ausgangsdatei im PDF Format,
	 */
	private void createPDFFromImage(String inputFile, String image,
			String outputFile) throws IOException, COSVisitorException {

		// Unser leeres Dokument.
		PDDocument doc = null;
		try {
			doc = PDDocument.load(inputFile);
			// Bild zur ersten Seite hinzufuegen.
			PDPage page = (PDPage) doc.getDocumentCatalog().getAllPages()
					.get(0);
			PDXObjectImage ximage = null;
			BufferedImage awtImage = ImageIO.read(new File(image));
			ximage = new PDPixelMap(doc, awtImage);
			PDPageContentStream contentStream = new PDPageContentStream(doc,
					page, true, true);
			contentStream.drawImage(ximage, 20, 900);
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
		// Unsere Node ( Tabelle oder Spreadsheet oder Agenda etc..)
		node = pNode;
		WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
		File file = new File(node.getId() + (".png"));
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO Kommentar...
	 */
	// Agenda
	public void setAgendaUp() {
		/**
		 * Erstellt mehrere AppointmentGroups
		 */
		lAppointmentGroupMap.put("group01",
				new Agenda.AppointmentGroupImpl().withStyleClass("group1"));
		lAppointmentGroupMap.put("group00",
				new Agenda.AppointmentGroupImpl().withStyleClass("group0"));
		lAppointmentGroupMap.put("group02",
				new Agenda.AppointmentGroupImpl().withStyleClass("group2"));

		for (String lId : lAppointmentGroupMap.keySet()) {
			Agenda.AppointmentGroup lAppointmentGroup = lAppointmentGroupMap
					.get(lId);
			lAppointmentGroup.setDescription(lId);
			agendaTable.appointmentGroups().add(lAppointmentGroup);
		}
	}

	/**
	 * Methodem die die Tabelle als CSV ausgibt.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bCsv() throws IOException {
		if (selecTeach != null) {
			saveAppointment();
		}
		// CSV Pfad erstellen
		String dirName = "CSV";
		File dir = new File(path + dirName);
		dir.mkdir();
		// Datei erstellen, FileWriter Initialisieren.
		String csv = "Lehrerwuensche " + selecTeach.getShortName()
				+ ".csv";
		CSVWriter writer = new CSVWriter(new FileWriter(dir + File.separator
				+ csv));
		List<String[]> data = new ArrayList<String[]>();
		ArrayList<Timeslot> ultimate = new ArrayList<Timeslot>();
		ultimate.addAll(GUIHandler.getTeacherWishes(selecTeach));
		ArrayList<ArrayList<Timeslot>> exportList = new ArrayList<ArrayList<Timeslot>>();
		// Listen für Timeslots für jeden Wochentag
		ArrayList<Timeslot> mon = new ArrayList<Timeslot>();
		ArrayList<Timeslot> die = new ArrayList<Timeslot>();
		ArrayList<Timeslot> mit = new ArrayList<Timeslot>();
		ArrayList<Timeslot> don = new ArrayList<Timeslot>();
		ArrayList<Timeslot> fre = new ArrayList<Timeslot>();
		ArrayList<Timeslot> sam = new ArrayList<Timeslot>();
		ArrayList<Timeslot> son = new ArrayList<Timeslot>();
		// Timeslots je nach Wochentag in ArrayListen speichern.
		for (Timeslot un : ultimate) {
			if (un.getDay().equals(WEEKDAYS.MONDAY)) {
				mon.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.TUESDAY)) {
				die.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.WEDNESDAY)) {
				mit.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.THURSDAY)) {
				don.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.FRIDAY)) {
				fre.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.SATURDAY)) {
				sam.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.SUNDAY)) {
				son.add(un);
			}
		}
		// ArrayListen nach Zeiten sortieren.
		Collections.sort(mon);
		Collections.sort(die);
		Collections.sort(mit);
		Collections.sort(don);
		Collections.sort(fre);
		Collections.sort(sam);
		Collections.sort(son);
		// In Oberliste einfuegen.
		exportList.add(mon);
		exportList.add(die);
		exportList.add(mit);
		exportList.add(don);
		exportList.add(fre);
		exportList.add(sam);
		exportList.add(son);
		// OberListe durchlaufen, Wochentag ausgeben.
		for (ArrayList<Timeslot> a : exportList) {
			if (a.size() > 0) {
				data.add(new String[] { "", "", "" });
				data.add(new String[] { "----", a.get(0).getDay().getTitle(),
						"----" });
				System.out.println(a.get(0).getDay().getTitle());
			}
			data.add(new String[] { "", "", "" });
			// Jeden Unit durchlaufen, Attribute alle ausgeben.
			for (Timeslot u : a) {
				u.setStarttime();
				u.setEndTime();
				data.add(new String[] { "***", "Uhrzeit: ",
						u.getStarttime() + "-" + u.getEndTime() });

			}
		}

		writer.writeAll(data);
		writer.close();
	}

	/**
	 * Methode, die die Tabelle mit allen Backups als TXT ausgibt.
	 */
	@FXML
	private void bTxt() throws IOException {
		// Directory erstellen mithilfe der pathvariable (TXT-ordner erstellen)
		if (selecTeach != null) {
			saveAppointment();
		}
		String dirName = "TXT";
		File dir = new File(path + dirName);
		dir.mkdir();
		// BufferedWriter initialisieren
		BufferedWriter out = new BufferedWriter(new FileWriter(dir
				+ File.separator + "LehrerWuensche "
				+ selecTeach.getShortName() + ".txt"));
		out.newLine();
		ArrayList<Timeslot> ultimate = new ArrayList<Timeslot>();
		ultimate.addAll(GUIHandler.getTeacherWishes(selecTeach));
		ArrayList<ArrayList<Timeslot>> exportList = new ArrayList<ArrayList<Timeslot>>();
		// Liste von Units für Wochentage
		ArrayList<Timeslot> mon = new ArrayList<Timeslot>();
		ArrayList<Timeslot> die = new ArrayList<Timeslot>();
		ArrayList<Timeslot> mit = new ArrayList<Timeslot>();
		ArrayList<Timeslot> don = new ArrayList<Timeslot>();
		ArrayList<Timeslot> fre = new ArrayList<Timeslot>();
		ArrayList<Timeslot> sam = new ArrayList<Timeslot>();
		ArrayList<Timeslot> son = new ArrayList<Timeslot>();

		// Fügt Units je nach Wochentag den Wochentagslisten hinzu.
		for (Timeslot un : ultimate) {
			if (un.getDay().equals(WEEKDAYS.MONDAY)) {
				mon.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.TUESDAY)) {
				die.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.WEDNESDAY)) {
				mit.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.THURSDAY)) {
				don.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.FRIDAY)) {
				fre.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.SATURDAY)) {
				sam.add(un);
			}
			if (un.getDay().equals(WEEKDAYS.SUNDAY)) {
				son.add(un);
			}
		}
		// Sortiert die Wochentagslisten nach Timeslots der Units
		Collections.sort(mon);
		Collections.sort(die);
		Collections.sort(mit);
		Collections.sort(don);
		Collections.sort(fre);
		Collections.sort(sam);
		Collections.sort(son);
		// Fügt Wochentagslisten der ExportListe hinzu
		exportList.add(mon);
		exportList.add(die);
		exportList.add(mit);
		exportList.add(don);
		exportList.add(fre);
		exportList.add(sam);
		exportList.add(son);

		// Oberliste fuer die ganze Woche durchlaufen
		for (ArrayList<Timeslot> u : exportList) { // Wochentag reinschreiben
													// ins
			out.newLine();
			out.newLine();
			// Dokument
			if (u.size() > 0) {
				out.write(u.get(0).getDay().getTitle());
				out.newLine();
				out.write("-------");
			}
			// Jeweils fuer den Wochentag dann alle Units durchlaufen
			for (Timeslot c : u) {
				c.setStarttime();
				c.setEndTime();
				out.newLine();
				out.newLine();
				// Zu aller Anfang Start und Endzeit des Slots angeben
				if (u.size() > 0) {
					out.write("Uhrzeit: " + c.getStarttime() + "-"
							+ c.getEndTime());
					out.newLine();
				}

			}// Dokument wird geschlossen.
			out.newLine();
		}
		out.close();
	}
}