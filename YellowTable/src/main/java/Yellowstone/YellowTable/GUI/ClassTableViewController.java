/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importiert die Logikkomponenten, sowie zentrale Elemente des Java-FXML-Pakets und die Agenda.
 * In dieser Klasse wurde die JFXtras-Agenda verwendet, wir haben diese zwar Teilweise editiert, stützen uns aber auf deren Code
 */
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.controlsfx.control.CheckComboBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import au.com.bytecode.opencsv.CSVWriter;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import Yellowstone.YellowTable.warnings.Warner;
import Yellowstone.YellowTable.warnings.Warning;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jfxtras.scene.control.CalendarTimeTextField;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImpl;

/**
 * Der Controller des ClassTable-Views. Fuer naehere Information siehe
 * Architektur.
 * 
 * Von hier aus findet die zentrale Planung statt.
 * 
 * @author tomlewandowski, manuelpache, Erik
 *
 */
public class ClassTableViewController {

	/**
	 * Attribut zum setzen der MainApp.
	 */
	private MainApp mainApp;

	/**
	 * RootLayout Object
	 */
	private RootLayoutViewController rootlayout;

	/**
	 * Das Agenda Attribut, welches den Stundenplan darstellt.
	 */
	@FXML
	private Agenda agendaTable;

	/**
	 * RadioButton welcher festlegt ob es sich bei einer Unterrichtseinheit um
	 * ein Band handelt
	 */
	@FXML
	private RadioButton rbBand;

	/**
	 * RadioButton für kein Band, you know
	 */
	@FXML
	private RadioButton rbBandNo;

	/**
	 * CheckCombobox, in der man auswaehlen kann, welche Faecher zur
	 * Planungseinheit gehoeren.
	 */
	@FXML
	private CheckComboBox<String> cbSubjects;

	/**
	 * CheckCombobox, in der man auswaehlen kann, welche Personen zur
	 * Planungseinheit gehoeren.
	 */
	@FXML
	private CheckComboBox<String> cbPersons;

	/**
	 * CheckCombobox, in der man auswaehlen kann, welche Klassen zur
	 * Planungseinheit gehoeren.
	 */
	@FXML
	private CheckComboBox<String> cbClasses;

	/**
	 * CheckCombobox, in der man auswaehlen kann, welche Raueme zur
	 * Planungseinheit gehoeren.
	 */
	@FXML
	private CheckComboBox<String> cbRooms;

	/**
	 * Combobox, mit der man die Art der Anzeige auswaehlen kann. (Zum Beispiel
	 * normaler Unterricht, rhythmisierter Unterricht etc)
	 */
	@FXML
	private ComboBox<String> cbAdvert;

	/**
	 * TextFeld indem man die Startzeit der Planungseinheit bestimmen kann.
	 */
	@FXML
	private CalendarTimeTextField startTime = new CalendarTimeTextField()
			.withDateFormat(SimpleDateFormat.getTimeInstance());

	/**
	 * Textfeld indem man das Ende der Planungseinheit bestimmen kann.
	 */
	@FXML
	private CalendarTimeTextField endTime = new CalendarTimeTextField()
			.withDateFormat(SimpleDateFormat.getTimeInstance());

	/**
	 * Startzeit für individuelle Startzeit
	 */
	@FXML
	private CalendarTimeTextField inviStartT = new CalendarTimeTextField()
			.withDateFormat(SimpleDateFormat.getTimeInstance());

	/**
	 * Endzeit für individuelle Endzeit
	 */
	@FXML
	private CalendarTimeTextField inviEndT = new CalendarTimeTextField()
			.withDateFormat(SimpleDateFormat.getTimeInstance());
	/**
	 * Tabelle mit Warnungen an den Benutzer.
	 */
	@FXML
	private TableView<Warning> warningTable;

	/**
	 * Spalte, die zu den Warnungen, die Warnungsnummern anzeigt.
	 */
	@FXML
	private TableColumn<Warning, Number> number;

	/**
	 * Spalte, die Informationen angibt, um welche Warnung es sich handelt.
	 */
	@FXML
	private TableColumn<Warning, String> info;

	/**
	 * Zur Auswahl eines Lehrers für indiviuelle Zeiten
	 */
	@FXML
	private ComboBox<String> indivTeach;

	/**
	 * Tab für invividuelle Zeiten
	 */
	@FXML
	private Tab inviTab;

	/**
	 * Map für die AppointmentGroups
	 */
	final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Combo-Box mit der man das Format zum Exportieren auswählen kann.
	 */
	@FXML
	private ComboBox<String> format;

	/**
	 * Node
	 */
	private Node node;

	/**
	 * Pfad zum Ordner
	 */
	String path = (System.getProperty("user.dir")) + File.separator;

	/**
	 * Die Skalierung. Wenn der Unterricht zum Beispiel um 7:00 beginnt und
	 * diese 10 ist geht der Unterricht bis 17 Uhr.
	 */
	private int scaleHours;

	/**
	 * Startzeit (wenn zum Beispiel 7 uebergeben wird)
	 */
	private int fromHours;

	/**
	 * Die Granularitaet einer Unterrichtseinheit. (zum Beispiel: 1,5,15,45,60)
	 */
	private int granularity;

	/**
	 * Bestimmt die Höhe einer Stunde und damit die Höhe des gesammten Plans
	 */
	private int hourHeight;

	/**
	 * Ausgewählte übergebene Klasse
	 */
	private Class selClass;

	/**
	 * Setzt die MainApp als aufrufende Scene.
	 * 
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		warningTable.setItems(FXCollections.observableArrayList((Warner.WARNER
				.getWarnings())));
	}

	/**
	 * Initialisiert die Klasse. Gesetzt werden die Export-Format, Ansichten und
	 * die Agenda. Sowie alle Attribute einer Unterrichtseinheit.
	 */
	public void initialize() {

		cbAdvert.getItems().addAll("Normal", "Rhythmisierter Unterricht");
		cbRooms.getItems().setAll(GUIHandler.getComboRooms());
		cbClasses.getItems().setAll(GUIHandler.getComboClasses());
		cbSubjects.getItems().setAll(GUIHandler.getComboSubjects());
		cbPersons.getItems().setAll(GUIHandler.getComboPersons());

		info.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getDescription()));
		number.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getPriority()));

		/*
		 * Initialisiert die Attribute einer Agenda
		 */
		scaleHours = GUIHandler.getDisplayedHours();
		fromHours = GUIHandler.getStarttime();
		granularity = GUIHandler.getGranularity();
		hourHeight = 100;

		initializeAgenda(scaleHours, fromHours, granularity, hourHeight);

		/*
		 * Da wir den Kalendar der Agenda für den Stundenplan nicht gebraucht
		 * wird, setzen wir ihn auf ein festes Datum. Wir sind voller Hoffung
		 * dass dieses Tool in diesem Jahr nicht mehr von Nöten ist.
		 */
		Calendar cal2 = new GregorianCalendar(2055, 1, 1);
		agendaTable.withDisplayedCalendar(cal2);
		inviTab.setDisable(true);

		showAppointmentDetails(null);

		selClass = null;

		agendaTable
				.selectedAppointmentProperty()
				.addListener(
						(observable, oldValue, newValue) -> showAppointmentDetails(newValue));

		/*
		 * Ein Listener für einen Dragging-Event
		 */
		agendaTable.selectedAppointmentProperty().addListener(
				(observable, oldValue, newValue) -> handleDragging(newValue));

		/*
		 * Ein Listener für ein gelöschtes Appointment
		 */
		agendaTable
				.deletedAppointmentProperty()
				.addListener(
						(observable, oldValue, newValue) -> handleDeletedAppointment(newValue));

		/*
		 * Ein Listener für ausgewählte Personen
		 */
		cbPersons.getCheckModel().getCheckedItems()
				.addListener(new ListChangeListener<String>() {
					public void onChanged(
							ListChangeListener.Change<? extends String> c) {
						addCheckedToIndiv(c.getList());
					}
				});

		/*
		 * Reagiert auf die Benutzung des cbAdvert-Buttons.
		 */
		cbAdvert.setOnAction((event) -> {
			switchToOtherView();
		});

		/*
		 * Reagiert auf die Benutztung des indivTeach Buttons.
		 */
		indivTeach.setOnAction((event) -> {
			switchInviTime();
		});

		/*
		 * Reagiert auf die Benutzung von cbSubjects.
		 */
		cbSubjects.getCheckModel().getCheckedItems()
				.addListener(new ListChangeListener<String>() {
					public void onChanged(
							ListChangeListener.Change<? extends String> c) {
						checkRoomFunktionValid();
						setTimeToStandard();

					}
				});
	}

	/**
	 * Sorgt dafür dass der Klassenraum nur ausgewählt ist, falls ein
	 * ausgewähltes Fach in diesem Unterrichtet werden kann.
	 */
	public void checkRoomFunktionValid() {
		if (selClass != null && agendaTable.getSelectedAppointment() != null) {
			if (agendaTable.getSelectedAppointment().getUnit() == null) {
				for (String subStr : cbSubjects.getCheckModel()
						.getCheckedItems()) {
					if (GUIHandler.getSubjectByName(subStr).getCategory()
							.getName() == "Standard") {
						cbRooms.getCheckModel().check(
								selClass.getRoom().getName());
					} else {
						cbRooms.getCheckModel().clearCheck(
								selClass.getRoom().getName());
						break;
					}
				}
			}
		}
	}

	/**
	 * Setzt nach Auswahl eines Faches die Zeit der Planungseinheit auf die
	 * Standardzeit des Faches
	 */
	public void setTimeToStandard() {
		Appointment lAppointment = agendaTable.getSelectedAppointment();
		if (lAppointment != null) {
			if (lAppointment.getUnit() != null) {
			} else {
				if (cbSubjects.getCheckModel().getCheckedItems().size() > 0) {
					int dur = getLongestStandard();
					if(dur == 0){
						dur = GUIHandler.getConfig().getStdDuration();
					}
					Calendar cal1 = agendaTable.getSelectedAppointment()
							.getStartTime();
					Calendar calStart = new GregorianCalendar(2055, 1, 1, 0, 0);
					int hour = cal1.get(Calendar.HOUR_OF_DAY);
					int minute = cal1.get(Calendar.MINUTE);
					int day = cal1.get(Calendar.DAY_OF_MONTH);
					calStart.set(Calendar.HOUR_OF_DAY, hour);
					calStart.set(Calendar.MINUTE, minute);
					calStart.set(Calendar.DAY_OF_MONTH, day);
					calStart.add(Calendar.MINUTE, dur);
					lAppointment.setEndTime(calStart);
					startTime.setCalendar(lAppointment.getStartTime());
					endTime.setCalendar(lAppointment.getEndTime());
					agendaTable.refresh();

				}
			}
		}
	}

	/**
	 * Hilfs Methode zum ermitteln des längsten Standardzeit von ausgwählten
	 * Fächern.
	 * 
	 * @return
	 */
	public int getLongestStandard() {
		int current = 0;
		ArrayList<Subject> checkSubs = new ArrayList<>();
		for (String s : cbSubjects.getCheckModel().getCheckedItems()) {
			checkSubs.add(GUIHandler.getSubjectByName(s));
		}
		for (Subject sub : checkSubs) {
			if (sub.getDuration() > current) {
				current = sub.getDuration();
			}
		}
		return current;
	}

	/**
	 * Wechsel zwischen Personen für individuelle Zeiten
	 */
	public void switchInviTime() {
		if (indivTeach.getValue() != null) {
			TeachingUnit unit = (TeachingUnit) agendaTable
					.getSelectedAppointment().getUnit();
			for (PersonTime pers : unit.getPersons()) {
				if (pers.getPerson().getShortName()
						.equals(indivTeach.getValue())) {
					// Startzeit
					int startHour = pers.getTimeslot().getStartHour();
					int startMinute = pers.getTimeslot().getStartMinute();
					// Endzeit
					int endHour = pers.getTimeslot().getEndHour_int();
					int endMinute = pers.getTimeslot().getEndMinute_int();
					// Wochentag
					int weekday = pers.getTimeslot().getDay().ordinal();
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

					inviStartT.setCalendar(calStart);
					inviEndT.setCalendar(calEnd);
				}
			}
		} else {
			inviEndT.setCalendar(null);
			inviStartT.setCalendar(null);
		}
	}

	/**
	 * Eintragen individuelle Zeiten
	 */
	@FXML
	public void inviTimes() {
		// gewählte Unit
		TeachingUnit unit = (TeachingUnit) agendaTable.getSelectedAppointment()
				.getUnit();
		// gewählter Lehrer
		String teach = indivTeach.getValue();
		// Zeiten aus den TimePickerBoxen
		int startH = inviStartT.getCalendar().get(Calendar.HOUR_OF_DAY);
		int startM = inviStartT.getCalendar().get(Calendar.MINUTE);
		int endH = inviEndT.getCalendar().get(Calendar.HOUR_OF_DAY);
		int endM = inviEndT.getCalendar().get(Calendar.MINUTE);
		// Zeiten also Strings
		String startHStr = Timeslot.addStringHour(startH);
		String startMStr = Timeslot.addStringMinute(startM);
		String endHStr = Timeslot.addStringHour(endH);
		String endMStr = Timeslot.addStringMinute(endM);

		int duration = Timeslot.getDurationByStartAndEndtime(startHStr,
				startMStr, endHStr, endMStr);
		WEEKDAYS day = unit.getTimeslot().getDay();
		Timeslot checkT = new Timeslot(duration, day, startHStr, startMStr);
		Timeslot checkU = unit.getTimeslot();
		if (checkT.isInTimeframe(checkU)) {
			GUIHandler.addPersonToTeach(unit.getId(), teach, startHStr,
					startMStr, endHStr, endMStr);
		} else {
			notInTimeSlotDialog();
		}

		refresh();
		agendaTable.appointments().clear();
		loadAppointments(selClass);
	}

	/**
	 * Editiert die Zeiten einer Unit durch Dragging
	 * 
	 * @param lAppointment
	 */
	public void handleDragging(Appointment lAppointment) {
		try {
			if (lAppointment != null) {
				if (lAppointment.getUnit() != null) {
					if (lAppointment.getDragged() == true) {
						TeachingUnit unit = (TeachingUnit) lAppointment
								.getUnit();
						Calendar cal = startTime.getCalendar();
						cal.set(Calendar.YEAR, 2055);
						cal.set(Calendar.MONTH, 1);
						cal.set(Calendar.DAY_OF_MONTH,
								agendaTable.getSelectedAppointment()
										.getStartTime()
										.get(Calendar.DAY_OF_MONTH));

						String[] teachers = cbPersons.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						String[] subjects = cbSubjects.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						String[] rooms = cbRooms.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						String[] classes = cbClasses
								.getCheckModel()
								.getCheckedItems()
								.toArray(
										new String[(cbClasses.getCheckModel()
												.getCheckedItems().size() + 1)]);

						classes[classes.length - 1] = selClass.getName();

						int startH = startTime.getCalendar().get(
								Calendar.HOUR_OF_DAY);
						int startM = startTime.getCalendar().get(
								Calendar.MINUTE);
						int endH = endTime.getCalendar().get(
								Calendar.HOUR_OF_DAY);
						int endM = endTime.getCalendar().get(Calendar.MINUTE);
						int dayI = cal.get(Calendar.DAY_OF_MONTH);

						String startHStr = Timeslot.addStringHour(startH);
						String startMStr = Timeslot.addStringMinute(startM);
						String endHStr = Timeslot.addStringHour(endH);
						String endMStr = Timeslot.addStringMinute(endM);
						String dayStr = WEEKDAYS.values()[dayI].getTitle();

						int duration = Timeslot.getDurationByStartAndEndtime(
								startHStr, startMStr, endHStr, endMStr);
						Integer int1 = new Integer(duration);
						String duraStr = int1.toString();

						if (GUIHandler
								.getConfig()
								.getPlannedDays()
								.contains(
										WEEKDAYS.values()[cal
												.get(Calendar.DAY_OF_MONTH)])) {
							GUIHandler.editTeaching(unit.getId(), dayStr,
									startHStr, startMStr, duraStr, classes,
									rooms, subjects, teachers);

							refresh();
							agendaTable.appointments().clear();
							loadAppointments(selClass);

							for (Appointment a : agendaTable.appointments()) {
								a.setDragged(false);
							}
						} else {
							Alert alert1 = new Alert(AlertType.INFORMATION);
							alert1.setTitle("Ungültige Eingabe");
							alert1.initStyle(StageStyle.UTILITY);
							alert1.setHeaderText("Der angegebene Tag wurde nicht für eine Planung zugelassen!");
							alert1.setContentText("Bitte ändern Sie dies in den Standardeinstellungen");
							alert1.showAndWait();

							for (Appointment a : agendaTable.appointments()) {
								a.setDragged(false);
							}
							refresh();
							agendaTable.appointments().clear();
							loadAppointments(selClass);
						}
					}

				} else {
					for (Appointment a : agendaTable.appointments()) {
						a.setDragged(false);
					}
				}
			}
		} catch (NeedUserCorrectionException e) {
			noTimeToSwitchPlaces(e.getMessage());
			agendaTable.appointments().clear();
			loadAppointments(selClass);
			for (Appointment a : agendaTable.appointments()) {
				a.setDragged(false);
			}
		}
	}

	/**
	 * Läd ausgewählte Personen in die individuelle Auswahl
	 * 
	 * @param obl
	 */
	public void addCheckedToIndiv(ObservableList<? extends String> obl) {
		if (obl.size() > 1
				&& agendaTable.getSelectedAppointment().getUnit() != null) {
			inviTab.setDisable(false);
			for (String s : obl) {
				if (!indivTeach.getItems().contains(s)) {
					indivTeach.getItems().add(s);
				}
			}
		}
		for (String c : FXCollections
				.observableArrayList(indivTeach.getItems())) {
			if (!obl.contains(c)) {
				indivTeach.getItems().remove(c);
				if (obl.size() < 2) {
					inviTab.setDisable(true);
				}
			}
		}
	}

	/**
	 * Ändert die Farbe von TeachingUnits abhängig von ihrer Schwierigkeit
	 */
	public void switchToOtherView() {
		if (cbAdvert.getValue().equals("Rhythmisierter Unterricht")) {
			for (Appointment a : agendaTable.appointments()) {
				Object o = a.getUnit();
				if (o instanceof TeachingUnit) {
					TeachingUnit unit = (TeachingUnit) o;
					for (Subject sub : unit.getSubjects()) {
						if (sub.getDifficulty().getTitle()
								.equals(DIFFICULTY.HARD.getTitle())) {
							a.setAppointmentGroup(lAppointmentGroupMap
									.get("group04"));
							agendaTable.refresh();
						}
					}
				}
			}
		}
		if (cbAdvert.getValue().equals("Normal")) {
			for (Appointment a : agendaTable.appointments()) {
				Object o = a.getUnit();
				if (o instanceof TeachingUnit) {
					a.setAppointmentGroup(lAppointmentGroupMap.get("group01"));
					agendaTable.refresh();
				}
			}
		}
	}

	/**
	 * Errechnet aus einer Unit einen Calendar für die Startzeit
	 * 
	 * @param u
	 * @return
	 */
	public Calendar calculateStartCalendar(Unit u) {
		// Startzeit
		int startHour = u.getTimeslot().getStartHour();
		int startMinute = u.getTimeslot().getStartMinute();
		// Kalendar für Start und Endzeit
		Calendar calStart = new GregorianCalendar(2055, 1, 1, 0, 0);
		int weekday = u.getTimeslot().getDay().ordinal();
		// Setzt die Startzeit in den Kalendar
		calStart.set(Calendar.HOUR, startHour);
		calStart.set(Calendar.MINUTE, startMinute);
		calStart.set(Calendar.DAY_OF_MONTH, weekday);
		return calStart;
	}

	/**
	 * Errechnet aus einer Unit einen Calendar für die Endzeit
	 * 
	 * @param u
	 * @return
	 */
	public Calendar calculateEndCalendar(Unit u) {
		// Endzeit
		int endHour = u.getTimeslot().getEndHour_int();
		int endMinute = u.getTimeslot().getEndMinute_int();
		// Wochentag
		int weekday = u.getTimeslot().getDay().ordinal();
		Calendar calEnd = new GregorianCalendar(2055, 1, 1, 0, 0);
		// Setzt die Endzeit in den Kalendar
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
	public void loadAppointments(Class selClass) {
		if (selClass != null && GUIHandler.getWeekTable(selClass) != null) {
			for (Unit u : GUIHandler.getWeekTable(selClass)) {
				agendaTable.setDeletedAppointment(null);
				Calendar calStart = calculateStartCalendar(u);
				Calendar calEnd = calculateEndCalendar(u);

				if (u instanceof ExternUnit) {
					Appointment appoint = new AppointmentImpl()
							.withStartTime(calStart)
							.withEndTime(calEnd)
							.withUnit(u)
							.withDraggable(false)
							.withSummary("Schwimm- \n Unterricht")
							.withAppointmentGroup(
									lAppointmentGroupMap.get("group02"));
					agendaTable.appointments().add(appoint);
				}
				if (u instanceof TeachingUnit) {
					TeachingUnit unit = (TeachingUnit) u;
					Appointment appoint = new AppointmentImpl()
							.withStartTime(calStart)
							.withEndTime(calEnd)
							.withUnit(u)
							.withDraggable(true)
							.withDragged(false)
							.withSummary(unit.toString())
							.withAppointmentGroup(
									lAppointmentGroupMap.get("group01"));

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

			Calendar calStart = calculateStartCalendar(brUn);
			Calendar calEnd = calculateEndCalendar(brUn);

			Appointment appoint = new AppointmentImpl().withStartTime(calStart)
					.withEndTime(calEnd).withUnit(brUn).withDraggable(false)
					.withSummary(b.getTitle())
					.withAppointmentGroup(lAppointmentGroupMap.get("group03"));

			agendaTable.appointments().add(appoint);
		}
	}

	/**
	 * Falls ein Appointment gelöscht wird
	 */
	public void handleDeletedAppointment(Appointment lAppointment) {
		if (lAppointment != null) {
			if (lAppointment.getUnit() != null) {
				TeachingUnit unit = (TeachingUnit) lAppointment.getUnit();
				GUIHandler.removeTeaching(unit.getId());
				refresh();
				agendaTable.appointments().clear();
				loadAppointments(selClass);
			}
		}
	}

	/**
	 * Schnittstelle, welche die Agenda beim ersten Aufruf initialisiert.
	 * 
	 * @param pScaleHours
	 * @param pFromHours
	 * @param pGranularity
	 * @param pHourHeight
	 */
	public void initializeAgenda(final int pScaleHours, final int pFromHours,
			final int pGranularity, final int pHourHeight) {

		/*
		 * Erstellt mehrere AppointmentGroups
		 */
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

			/*
			 * Konstruktor des Agenda-Tools, mit der Stundenzahl, der
			 * Untergrenze, der Obergrenze, der Granularität und der
			 * Stundenhöhne.
			 */
			agendaTable.construct(pScaleHours, pFromHours, pGranularity,
					pHourHeight);

			/*
			 * Die Basis welche für ein neues Appointment benötigt werden, diese
			 * werden verwendet wenn per Drag and Drop ein Appointment erstellt
			 * wird.
			 */
			agendaTable.createAppointmentCallbackProperty().set(
					new Callback<Agenda.CalendarRange, Agenda.Appointment>() {
						@Override
						public Agenda.Appointment call(
								Agenda.CalendarRange calendarRange) {
							return new Agenda.AppointmentImpl()
									.withStartTime(
											calendarRange.getStartCalendar())
									.withEndTime(calendarRange.getEndCalendar())
									.withSummary(
											"Neue Eintragung bitte Attribute wählen")
									.withAppointmentGroup(
											lAppointmentGroupMap.get("group01"))
									.withDraggable(true).withUnit(null)
									.withDragged(false);
						}
					});
			for (Appointment a : agendaTable.appointments()) {
				a.setDragged(false);
			}
		}
	}

	/**
	 * Läd die Details eines Appointsments in die Maske
	 * 
	 * @param lAppointment
	 */
	public void showAppointmentDetails(Appointment lAppointment) {
		if (lAppointment != null) {

			startTime.setCalendar(lAppointment.getStartTime());
			endTime.setCalendar(lAppointment.getEndTime());

			if (lAppointment.getUnit() != null) {
				Object o = lAppointment.getUnit();
				TeachingUnit unit = (TeachingUnit) o;
				cbPersons.getCheckModel().clearChecks();
				for (String s : GUIHandler.extractPersons(unit)) {
					cbPersons.getCheckModel().check(s);
				}
				cbRooms.getCheckModel().clearChecks();
				for (String s : GUIHandler.extractRooms(unit)) {
					cbRooms.getCheckModel().check(s);
				}
				cbSubjects.getCheckModel().clearChecks();
				for (String s : GUIHandler.extractSubjects(unit)) {
					cbSubjects.getCheckModel().check(s);
				}
				cbClasses.getCheckModel().clearChecks();
				for (String s : GUIHandler.extractClasses(unit)) {
					cbClasses.getCheckModel().check(s);
				}
				rbBandNo.setSelected(true);
				if (cbRooms.getCheckModel().getCheckedItems().size() > 1
						|| cbSubjects.getCheckModel().getCheckedItems().size() > 1
						|| cbClasses.getCheckModel().getCheckedItems().size() > 0) {
					rbBand.setSelected(true);
				} else {
					rbBand.setSelected(false);
				}
			} else {
				cbPersons.getCheckModel().clearChecks();
				cbRooms.getCheckModel().clearChecks();
				cbSubjects.getCheckModel().clearChecks();
				cbClasses.getCheckModel().clearChecks();
				cbRooms.getCheckModel().check(selClass.getRoom().getName());
				rbBandNo.setSelected(true);
			}

		} else {
			startTime.setCalendar(null);
			endTime.setCalendar(null);
			cbPersons.getCheckModel().clearChecks();
			cbRooms.getCheckModel().clearChecks();
			cbClasses.getCheckModel().clearChecks();
			cbSubjects.getCheckModel().clearChecks();
			rbBand.setSelected(false);
			rbBandNo.setSelected(true);
		}
	}

	/**
	 * Button, der das Fenster schliesst, speichert und zum Hauptmenue
	 * zurueckkehrt.
	 */
	@FXML
	public void btnRootLayout() {
		for (Appointment a : agendaTable.appointments()) {
			if (a.getUnit() != null) {
			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Wirklich schließen");
				alert.initStyle(StageStyle.UTILITY);
				alert.setHeaderText("Hierdurch werden alle nicht gespeicherten Einträge gelöscht");
				alert.setContentText("Wollen Sie wirklich fortfahren?");
				ButtonType buttonTypeOne = new ButtonType("Ok");
				ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeOne) {
					dialogStage.close();
					GUIHandler.resetHistory();
					return;
				} else {
					return;
				}
			}
		} dialogStage.close();
	}

	/**
	 * Button, der eine neue Planungseinheit erzeugt.
	 */
	@FXML
	public void btnNewUnit() {
		try {
			if (agendaTable.getSelectedAppointment() != null) {
				// Fängt Probleme bei Setten außerhalb des Stundenplanes ab
				if (startTime.getCalendar().get(Calendar.HOUR_OF_DAY) < fromHours
						|| startTime.getCalendar().get(Calendar.HOUR_OF_DAY) > (fromHours
								+ scaleHours - 1)
						|| endTime.getCalendar().get(Calendar.HOUR_OF_DAY) < fromHours
						|| endTime.getCalendar().get(Calendar.HOUR_OF_DAY) > (fromHours
								+ scaleHours - 1)) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Ungültige Eingabe");
					alert.initStyle(StageStyle.UTILITY);
					alert.setHeaderText("Sie haben eine Zeit außerhalb des Stundenplans eingegeben!");
					alert.setContentText("Bitte entscheiden Sie sich für eine passende Zeit");
					alert.showAndWait();

					clearDetails();
					return;
				}
				// Fängt Probleme mit der Zeitsetzung von Start und End-Zeit ab
				if (endTime.getCalendar().get(Calendar.HOUR_OF_DAY) < startTime
						.getCalendar().get(Calendar.HOUR_OF_DAY)) {
					Alert alert1 = new Alert(AlertType.INFORMATION);
					alert1.setTitle("Ungültige Eingabe");
					alert1.initStyle(StageStyle.UTILITY);
					alert1.setHeaderText("Das angegebene Ende der Unterrichtseinheit liegt vor der Startzeit!");
					alert1.setContentText("Bitte entscheiden Sie sich für eine passende Zeit");
					alert1.showAndWait();

					clearDetails();
					return;
				} else {
					// Settet die Startzeit
					Calendar cal = startTime.getCalendar();
					cal.set(Calendar.YEAR, 2055);
					cal.set(Calendar.MONTH, 1);
					cal.set(Calendar.DAY_OF_MONTH,
							agendaTable.getSelectedAppointment().getStartTime()
									.get(Calendar.DAY_OF_MONTH));

					if (!GUIHandler
							.getConfig()
							.getPlannedDays()
							.contains(
									WEEKDAYS.values()[cal
											.get(Calendar.DAY_OF_MONTH)])) {
						Alert alert1 = new Alert(AlertType.INFORMATION);
						alert1.setTitle("Ungültige Eingabe");
						alert1.initStyle(StageStyle.UTILITY);
						alert1.setHeaderText("Der angegebene Tag wurde nicht für eine Planung zugelassen!");
						alert1.setContentText("Bitte ändern Sie dies in den Standarteinstellungen");
						alert1.showAndWait();
						return;
					} else {
						String[] teachers = cbPersons.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						if (teachers.length == 0) {
							nothingChosenDialog("Kein Personal",
									"eine LehrerIn oder eine MitarbeiterIn");
							return;
						}
						String[] subjects = cbSubjects.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						if (subjects.length == 0) {
							nothingChosenDialog("Kein Stundeninhalt",
									"ein Stundeninhalt");
							return;
						}
						String[] rooms = cbRooms.getCheckModel()
								.getCheckedItems().toArray(new String[0]);
						if (rooms.length == 0) {
							nothingChosenDialog("Keinen Raum", "einen Raum");
							return;
						}
						if (rooms.length > 1) {
							String place;
							String placeO;
							for (int i = 0; i < rooms.length; i++) {
								place = GUIHandler.getRoomByName(rooms[1])
										.getBuilding();
								placeO = GUIHandler.getRoomByName(rooms[i])
										.getBuilding();
								if (place != placeO) {
									twoBuildingsOneCase();
									return;
								}
							}
						}

						String[] classes = cbClasses
								.getCheckModel()
								.getCheckedItems()
								.toArray(
										new String[(cbClasses.getCheckModel()
												.getCheckedItems().size() + 1)]);

						classes[classes.length - 1] = selClass.getName();

						if (rbBand.isSelected() && classes.length < 2) {
							noBandDialog();
							return;
						}

						int startH = startTime.getCalendar().get(
								Calendar.HOUR_OF_DAY);
						int startM = startTime.getCalendar().get(
								Calendar.MINUTE);
						int endH = endTime.getCalendar().get(
								Calendar.HOUR_OF_DAY);
						int endM = endTime.getCalendar().get(Calendar.MINUTE);
						int dayI = cal.get(Calendar.DAY_OF_MONTH);

						String startHStr = Timeslot.addStringHour(startH);
						String startMStr = Timeslot.addStringMinute(startM);
						String endHStr = Timeslot.addStringHour(endH);
						String endMStr = Timeslot.addStringMinute(endM);
						String dayStr = WEEKDAYS.values()[dayI].getTitle();

						int duration = Timeslot.getDurationByStartAndEndtime(
								startHStr, startMStr, endHStr, endMStr);
						Integer int1 = new Integer(duration);
						String duraStr = int1.toString();

						if (agendaTable.getSelectedAppointment().getUnit() == null) {
							GUIHandler.addTeaching(dayStr, duraStr, startHStr,
									startMStr, classes, rooms, subjects,
									teachers);
						} else {
							TeachingUnit unit = (TeachingUnit) agendaTable
									.getSelectedAppointment().getUnit();
							GUIHandler.editTeaching(unit.getId(), dayStr,
									startHStr, startMStr, duraStr, classes,
									rooms, subjects, teachers);
						}

						// Neu laden
						agendaTable.appointments().clear();
						refresh();
						loadAppointments(selClass);
						clearDetails();
					}
				}
			}
		} catch (NeedUserCorrectionException e) {
			noTimeToSwitchPlaces(e.getMessage());
		}
	}

	/**
	 * macht ein wenig sauber
	 */
	public void clearDetails() {
		rbBand.setSelected(false);
		rbBandNo.setSelected(true);
		cbPersons.getCheckModel().clearChecks();
		cbClasses.getCheckModel().clearChecks();
		cbRooms.getCheckModel().clearChecks();
		cbSubjects.getCheckModel().clearChecks();
		startTime.setCalendar(null);
		endTime.setCalendar(null);
		indivTeach.setValue(null);
		inviStartT.setCalendar(null);
		inviEndT.setCalendar(null);
		cbRooms.getCheckModel().check(selClass.getRoom().getName());
		cbAdvert.setValue("Normal");
		// Alles undragged setzen, nur zur Sicherheit!
		for (Appointment a : agendaTable.appointments()) {
			a.setDragged(false);
		}
	}

	/**
	 * Wird von der MainApp aufgerufen und läd die Daten einer übergebenen
	 * Klasse in die Agenda
	 * 
	 * @param root
	 */
	public void loadDataToAgenda(RootLayoutViewController root) {
		rootlayout = root;
		selClass = rootlayout.getSelectedClass();
		loadAppointments(selClass);
		cbClasses.getItems().remove(selClass.getName());
		cbRooms.getCheckModel().check(selClass.getRoom().getName());
		refresh();
	}

	/**
	 * Button, der ein Fenster mit allen Warnings oeffnet.
	 */
	@FXML
	public void btnShowallWarnings() {
		mainApp.showWarningView();
	}

	/**
	 * Setzt die DialogStage neu. Findet beim Aufruf dieses Views statt. Es
	 * werden noch KeyCombinations von Undo und Redo gesetzt.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;

		dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				GUIHandler.resetHistory();
			}

		});

		final KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.Z,
				KeyCombination.CONTROL_DOWN);

		final KeyCombination keyComb3 = new KeyCodeCombination(KeyCode.Y,
				KeyCombination.CONTROL_DOWN);

		dialogStage.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (keyComb2.match(event)) {
							bUndo();
						}
					}
				});

		dialogStage.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (keyComb3.match(event)) {
							bRedo();
						}
					}
				});
	}

	// Export von CSV, PDF und TXT

	/**
	 * Undo-Button
	 */
	@FXML
	private void bUndo() {
		GUIHandler.undo();
		agendaTable.appointments().clear();
		loadAppointments(selClass);
		refresh();
	}

	/**
	 * Redo-Button
	 */
	@FXML
	private void bRedo() {
		GUIHandler.redo();
		agendaTable.appointments().clear();
		loadAppointments(selClass);
		refresh();
	}

	/**
	 * Läd die Warnings neu
	 */
	public void refresh() {
		warningTable.getItems().clear();
		warningTable.setItems(FXCollections.observableArrayList((Warner.WARNER
				.getWarnings())));
	}

	/**
	 * Pdf-Export Button
	 * 
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	@FXML
	private void bPdf() throws COSVisitorException, IOException {
		String dirName = "PDF";
		File dir = new File(path + dirName);
		dir.mkdir();
		generateScreen(agendaTable.getAgendaNode());
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A2);
		document.addPage(page1);
		// PDPageContentStream cos = new PDPageContentStream(document, page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", "ClassTable" + (".png"), dir
				+ File.separator + "Klassenstundenplan "+ selClass.getName() + (".pdf"), false);
		File f1 = new File("ClassTable" + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
	}

	/**
	 * Csv-Export Button
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bCsv() throws IOException {
		// CSV Pfad erstellen?
		String dirName = "CSV";
		File dir = new File(path + dirName);
		dir.mkdir();
		// Funktion
		String csv = "Klassenstundenplan ";
		CSVWriter writer = new CSVWriter(new FileWriter(dir + File.separator
				+ csv+selClass.getName()+".csv"));
		List<String[]> data = new ArrayList<String[]>();
		ArrayList<Unit> ultimate = new ArrayList<Unit>();
		ultimate.addAll(GUIHandler.getWeekTable(selClass));
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
					if (((TeachingUnit) u).getTravelNeed()) {
						pendelString += "(P)";
					}

					data.add(new String[] { subjectsString, roomsString,
							teachersString, classString, pendelString });
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
	 * Txt-Export Button
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bTxt() throws IOException {
		// Directory erstellen mithilfe der pathvariable (TXT-ordner erstellen)
		String dirName = "TXT";
		File dir = new File(path + dirName);
		dir.mkdir();
		// .. Dient hier nur als Beispiel..
		BufferedWriter out = new BufferedWriter(new FileWriter(dir
				+ File.separator + "Klassenstundenplan "+ selClass.getName()+".txt"));
		out.newLine();
		ArrayList<Unit> ultimate = new ArrayList<Unit>();
		ultimate.addAll(GUIHandler.getWeekTable(selClass));
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

		// Oberliste fuer die ganze Woche durchlaufen
		for (ArrayList<Unit> u : exportList) { // Wochentag reinschreiben ins
												// Dokument
			if (u.size() > 0) {
				out.write(u.get(0).getTimeslot().getDay().getTitle());
				out.newLine();
				out.write("-------");
			}
			// Jeweils fuer den Wochentag dann alle Units durchlaufen
			for (Unit c : u) {
				out.newLine();
				out.newLine();
				c.getTimeslot().setStarttime();
				c.getTimeslot().setEndTime();
				// Zu aller Anfang Start und Endzeit des Slots angeben
				if (u.size() > 0) {
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
					// Strings initialisieren zum spaeteren einsetzen der
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
							// Variable hochzÃƒÆ’Ã‚Â¤hlen
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
						if (((TeachingUnit) c).getTravelNeed()) {
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
				contentStream.drawImage(ximage, 20, 0);
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
		File file = new File("ClassTable" + (".png"));
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Der Button für das Drucken des Plans
	 * 
	 * @throws COSVisitorException
	 * @throws IOException
	 */
	@FXML
	private void bPrint() throws COSVisitorException, IOException {

		String dirName = "PRINT";
		File dir = new File(path + dirName);
		dir.mkdir();
		generateScreen(agendaTable.getAgendaNode());
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);
		document.addPage(page1);
		// PDPageContentStream cos = new PDPageContentStream(document, page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", "ClassTable" + (".png"), dir
				+ File.separator + "Klassenstundenplan" + (".pdf"), true);
		File f1 = new File("ClassTable" + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
		PDDocument doc2 = null;
		try {
			doc2 = PDDocument.load(dir + File.separator + "Klassenstundenplan"
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

	/**
	 * Ein Dialog der fehlede Eintragungen abfängt.
	 * 
	 * @param s
	 * @param t
	 */
	public void nothingChosenDialog(String s, String t) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(s + " ausgewählt!");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Einer Planungseinheit muss " + t
				+ " zugeordnet sein!");
		alert.setContentText("Bitte treffen Sie vor dem Speichern eine Auswahl");
		alert.showAndWait();
	}

	/**
	 * Dialog für den Fall, dass der Band RadioButton angehakt worden ist, aber
	 * keine passende Auswahl getroffen worden ist.
	 */
	public void noBandDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Zu wenig Angaben für ein Band");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Einem Band müssen mindestens 2 Klassen zugeordnet sein");
		alert.setContentText("Bitte treffen Sie vor dem Speichern eine Auswahl");
		alert.showAndWait();
	}

	/**
	 * Dialog falls einen individuelle Zeit nicht im TimeSlot des Plaungseinheit
	 * liegt.
	 */
	public void notInTimeSlotDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Ungültige Eingabe");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Die individuelle Zeit muss innerhalb der Planungeinheit liegen!");
		alert.setContentText("Bitte wählen Sie eine passende Zeit");
		alert.showAndWait();
	}

	/**
	 * Dialog hinweis auf Standortwechsel
	 * 
	 * @param pExp
	 */
	public void noTimeToSwitchPlaces(String pExp) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Ungültige Planung");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText(pExp);
		alert.setContentText("Bitte planen Sie genug Zeit zum Wechseln des Standortes ein");
		alert.showAndWait();
	}

	/**
	 * Dialog für den Fall, dass einer Planungseinheit Räume von mehreren
	 * Standorten zugeteilt werden.
	 * 
	 * @param pExp
	 */
	public void twoBuildingsOneCase() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Ungültige Planung");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Für eine Unterrichtseinheit können nur" + "\n"
				+ "Räume des selben Standortes verplant werden");
		alert.setContentText("Bitte entscheiden Sie sich für Räume des selben Standortes");
		alert.showAndWait();
	}

}
