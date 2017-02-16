/**
 * Der Controller gehoert zum GUI-Package und dient dazu den View mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importiert die Logikkomponenten, sowie zentrale Elemente des Java-FXML-Pakets.
 */
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;

import org.controlsfx.control.CheckComboBox;

import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * Controller, fuer den Default-View, der die Standardeinstellungen
 * repraesentiert.
 * 
 * @author tomlewandowski
 *
 *         Da dieser uber relativ viele Zeilen verfuegt hier eine Uebersicht
 *         ueber den groben Aufbau:
 * 
 *         - Attribute: Zuerst werden die zentralen Elemente des fxml-Views
 *         aufgelistet, die mit dem fxml-File verbunden sind. (Textfelder,
 *         Tabellen etc.)
 * 
 *         - initialize()-Methode: Initialisiert bereits gemachte
 *         Benutzereingaben und holt diese aus dem GUI-Handler. (Zum Beispiel
 *         Tabellenwerte etc.)
 * 
 *         -setMainApp und setDialogStage: Setzen die MainApp und die
 *         Dialogstage, die wichtig sind, wenn der View aufgerufen wird.
 * 
 *         - Button-Operationen: Danach folgen, die Button-Operationen, die die
 *         Aktionen ausführen, wenn ein Benutzer auf einen bestimmten Button
 *         klickt. (Zum Beispiel: Speichern, Schliessen, Erstellen, Aendern,
 *         Loeschen) Diese rufen die valid()-Methoden auf, die Benutzereingaben
 *         kontrollieren.
 * 
 *         - showDetails-Methoden: Zeigen Details von bereits angelegten
 *         Objekten, diese werden aufgerufen, wenn der Benutzer
 *         Tabelleneintraege anklickt. Zum Beispiel wichtig zum Aendern.
 * 
 *         -checkValidValues-Methoden: Testen die Validitaet von
 *         Benutzereingaben. Bei Fehleingaben werden direkt Dialoge geworfen.
 * 
 *         -clearAllFields-Methoden: Setzen die Felder,Boxen etc. auf Standard
 *         zurueck.
 * 
 *         Zum Ende folgt dann noch eine Methode, die Exceptions ausprinted
 */
public class DefaultViewController {

	/**
	 * Die DialogStage dieser Klasse. (Dient dazu falls der View aufgerufen
	 * wird, dann wird die DialogStage neu gesetzt)
	 */
	private Stage dialogStage;

	/*
	 * Elemente fuer die Backups:
	 */

	/**
	 * Combo-Box, in der sich das Zeitintervall eines Backups einstellen laesst.
	 */
	@FXML
	private ComboBox<String> cbIntervall;

	/**
	 * Textfeld mit dem man den Titel der Backups bestimmen kann.
	 */
	@FXML
	private TextField backupTitle;

	/**
	 * Radiobutton, ob ein Backup beim Schliessen erstellt werden soll.
	 */
	@FXML
	private RadioButton backupOnCloseY;

	/**
	 * Radiobutton, das kein Backup beim Schliessen erstellt werden soll.
	 */
	@FXML
	private RadioButton backupOnCloseNo;

	/*
	 * Elemente fuer die Planung:
	 */

	/**
	 * Checkcombobox mit der sich die Wochentage für die Planung festlegen
	 * lassen.
	 */
	@FXML
	private CheckComboBox<String> weekdaysForPlans;

	/**
	 * Textfeld, in dem man die Stunde eintragen kann, in der der Schultag
	 * beginnt.
	 */
	@FXML
	private TextField beginHoursP;

	/**
	 * Textfeld, in dem man die Stunde eintragen kann, in der der Schultag
	 * endet.
	 */
	@FXML
	private TextField endHoursP;

	/**
	 * Textfeld, in dem man die Minute eintragen kann, in der der Schultag
	 * beginnt.
	 */
	@FXML
	private TextField beginMinutesP;

	/**
	 * Textfeld, in dem man die Minute eintragen kann, in der der Schultag
	 * endet.
	 */
	@FXML
	private TextField endMinutesP;

	/**
	 * Textfeld, in dem man die uebliche Dauer einer Planungseinheit eintragen
	 * kann.
	 */
	@FXML
	private TextField durationUnitP;

	/**
	 * Textfeld, in dem man den Detaillierungsgrad eingeben kann.
	 */
	@FXML
	private TextField granularityP;

	/*
	 * Elemente für den Tab Zeiteinheiten
	 */

	/**
	 * Anzahl der Minuten einer Lehrerstunde
	 */
	@FXML
	private TextField teacherMinutesForHour;

	/**
	 * Anzahl der Minuten einer päd. Mitarbeiter Stunde.
	 */
	@FXML
	private TextField eeMinutesForHour;

	/*
	 * Elemente fuer die Pausen:
	 */

	/**
	 * ComboBox, in der man den Jahrgang auswaehlen kann, fuer den die Pause
	 * festgelegt werden soll.
	 */
	@FXML
	private ComboBox<Integer> breakYear;

	/**
	 * Textfeld, indem man den Titel der Pause festlegen kann.
	 */
	@FXML
	private TextField breakTitle;

	/**
	 * Legt fest, wann die Pause beginnen soll. (Stunde)
	 */
	@FXML
	private TextField breakBeginHours;

	/**
	 * Legt fest, wann die Pause beginnen soll. (Minute)
	 */
	@FXML
	private TextField breakBeginMinutes;

	/**
	 * Legt fest, wie lange die Pause dauern soll.
	 */
	@FXML
	private TextField breakDuration;

	/**
	 * Checkcombobox, mit der man festlegen kann, an welchen Tagen die Pause
	 * stattfinden soll.
	 */
	@FXML
	private CheckComboBox<String> weekdaysForBreak;

	/**
	 * Tabelle, die eine Uebersicht aller Pausen liefert.
	 */
	@FXML
	private TableView<Break> breakTable;

	/**
	 * Spalte, die die Namen der Pausen anzeigt.
	 */
	@FXML
	private TableColumn<Break, String> nameB;

	/**
	 * Spalte, die die Jahrgaenge der Pausen anzeigt.
	 */
	@FXML
	private TableColumn<Break, Number> yearB;

	/**
	 * Spalte, die den Beginn der Pausen anzeigt.
	 */
	@FXML
	private TableColumn<Break, String> beginB;

	/**
	 * Spalte, die die Dauer der Pausen anzeigt.
	 */
	@FXML
	private TableColumn<Break, Number> durationB;

	/**
	 * Spalte, die die Wochentage der Pausen anzeigt.
	 */
	@FXML
	private TableColumn<Break, String> WEEKDAYSB;

	/*
	 * Elemente fuer den Meeting-Tab:
	 */

	/**
	 * ComboBox, in der man den Jahrgang auswaehlen kann, fuer den das Meeting
	 * festgelegt werden soll.
	 */
	@FXML
	private ComboBox<Integer> meetingYear;

	/**
	 * Tag an dem das Treffen stattfinden soll.
	 */
	@FXML
	private ComboBox<String> meetingDay;

	/**
	 * Beginn des Meetings (Stunden)
	 */
	@FXML
	private TextField beginHoursM;

	/**
	 * Beginn des Meetings (Minuten)
	 */
	@FXML
	private TextField beginMinutesM;

	/**
	 * Die Dauer des Meetings
	 */
	@FXML
	private TextField durationM;

	/**
	 * Der Titel des Treffens.
	 */
	@FXML
	private TextField titleM;

	/**
	 * Uebersicht aller Meetings.
	 */
	@FXML
	private TableView<MeetingUnit> meetingTable;

	/**
	 * Spalte, die die Namen der Meetings anzeigt.
	 */
	@FXML
	private TableColumn<IMeetingUnit, String> nameM;

	/**
	 * Spalte, die anzeigt, ob paedagogische Mitarbeiter am Meetings teilnehmen.
	 */
	@FXML
	private TableColumn<IMeetingUnit, String> eeM;

	/**
	 * Spalte, die anzeigt, wann die Meetings jeweils beginnen.
	 */
	@FXML
	private TableColumn<IMeetingUnit, String> beginM;

	/**
	 * Spalte, die anzeigt, wie lange das Meeting andauert.
	 */
	@FXML
	private TableColumn<IMeetingUnit, Number> durationM1;

	/**
	 * Spalte, die anzeigt, an welchem Tag das Meeting stattfindet.
	 */
	@FXML
	private TableColumn<IMeetingUnit, String> dayM;

	/*
	 * Elemente fuer den Standort-Tab:
	 */

	/**
	 * Textfeld, indem man den Namen des Standorts übergeben kann.
	 */
	@FXML
	private TextField titleLocation;

	/**
	 * Textfeld, indem man angibt, wie viele Minuten benoetigt werden, um den
	 * Standort vom Hauptstandort zu erreichen.
	 */
	@FXML
	private TextField minutesToLocation;

	/**
	 * Tabelle, die eine Uebersicht aller Standorte erlaubt.
	 */
	@FXML
	private TableView<Entry<String, Integer>> locationTable;

	/**
	 * Spalte, die die Namen aller Standorte anzeigt.
	 */
	@FXML
	private TableColumn<Entry<String, Integer>, String> titleL;

	/**
	 * Spalte, die die Minutenabstaende anzeigt.
	 */
	@FXML
	private TableColumn<Entry<String, Integer>, Number> minutesToL;

	/*
	 * Elemente fuer den Raumfunktionen-Tab:
	 */

	/**
	 * Textfeld, dass den Namen der Raumfunktion anzeigt.
	 */
	@FXML
	private TextField titleCategory;

	/**
	 * Tabelle, die alle Funktionen anzeigt.
	 */
	@FXML
	private TableView<Category> categoryTable;

	/**
	 * Spalte, die alle Funktionen anzeigt.
	 */
	@FXML
	private TableColumn<Category, String> titleC;

	/**
	 * Die MainApp.
	 */
	private MainApp mainApp;

	/**
	 * 
	 * Initialisiert den View und somit auch die Tabellenspalten..
	 * 
	 */
	public void initialize() {

		/**
		 * Zunaeachst aktuelle Config aus dem GUI Handler holen um bereits
		 * gemachte Angaben in die Standardeinstellungen zu laden.
		 */
		Config config = GUIHandler.getConfig();

		// Tab Backups wird geladen

		Integer i = config.getBackupTimer();
		cbIntervall.setValue(i.toString());
		backupTitle.setText(config.getBackupTitle());
		// Radiobuttons
		if (config.isBackupOnClose() == true) {
			backupOnCloseY.setSelected(true);
		} else {
			backupOnCloseNo.setSelected(true);
		}

		// Tab Plaung wird geladen.
		Integer i1 = config.getGranularity();
		granularityP.setText(i1.toString());
		Integer i2 = config.getStdDuration();
		durationUnitP.setText(i2.toString());

		// Uhrzeiten..
		beginHoursP.setText(Timeslot.addStringHour(config.getTimeframe()
				.getStartHour()));
		beginMinutesP.setText(Timeslot.addStringMinute(config.getTimeframe()
				.getStartMinute()));
		endHoursP.setText(Timeslot.addStringHour(config.getTimeframe()
				.getEndHour_int()));
		endMinutesP.setText(Timeslot.addStringMinute(config.getTimeframe()
				.getEndMinute_int()));

		// Setzt die Checkcomboboxen auf die Wochentage.
		weekdaysForBreak.getItems().setAll(GUIHandler.getComboDays());
		weekdaysForPlans.getItems().setAll(GUIHandler.getComboAllDays());

		for (WEEKDAYS day : config.getPlannedDays()) {
			weekdaysForPlans.getCheckModel().check(day.getTitle());
		}

		/**
		 * ComboBoxen, wobei die Daten aus dem GUIHandler geladen werden. Falls
		 * man diese verändern möchte muss man die ObservableLists, die im
		 * GUI-Handler erstellt werden ändern.
		 */

		// ComboBox in der man das Intervall eines Backups auswaehlen kann.
		cbIntervall.setItems(GUIHandler.getBackupIntervallList());

		// ComboBox in der man den Jahrgang fuer die Pausen auswaehlen kann.
		breakYear.setItems(GUIHandler.getYears());

		// ComboBox in der man den Jahrgang fuer die Meetings auswaehlen kann.
		meetingYear.setItems(GUIHandler.getYears());

		// ComboBox in der man den Tag des Meetings auswaehlen kann.
		meetingDay.setItems(GUIHandler.getComboDays());

		/**
		 * Textefelder der Zeiteinheiten werden initialisiert..
		 */
		Integer minperhourt = config.getMinPerHourTeacher();
		teacherMinutesForHour.setText(minperhourt.toString());

		Integer minperhourE = config.getMinPerHourEduc();
		eeMinutesForHour.setText(minperhourE.toString());

		/**
		 * Pausentabelle und Spalten werden initialisiert.
		 */

		// Spalte mit dem Namen der Pause.
		nameB.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getTitle()));

		// Spalte mit den Jahrgaengen fuer die dazugehoerigen Pausen.
		yearB.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getYear()));

		// Spalte mit dem Beginn aller Pausen.
		beginB.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getStarttime()));

		// Spalte mit der Dauer aller Pausen.
		durationB.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getDuration()));

		// Spalte mit den Tagen aller Pausen.
		WEEKDAYSB.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getDaysAsString()));

		showBreakDetails(null);

		breakTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showBreakDetails(newValue));

		/**
		 * Tabelle der Meetings und die dazugehoerigen Spalten werden
		 * initialisiert.
		 */

		// Spalte mit dem Namen des Meetings.
		nameM.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getTitle()));

		// Spalte, die angibt, wann die Pause beginnt.
		beginM.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getStarttime()));

		// Spalte, die angibt, an welchem Tag das Meeting stattfindet.
		dayM.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getTimeslot().getDay()
						.getTitle()));

		// Saplte, die angibt, wie lange die Pause andauert.
		durationM1.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty((cellData.getValue().getTimeslot()
						.getDuration())));

		showMeetingDetails(null);

		meetingTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showMeetingDetails(newValue));

		/**
		 * Tabelle der Standorte und die dazugehoerigen Spalten werden
		 * initialisiert.
		 */
		titleL.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getKey()));
		minutesToL.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getValue()));

		showBuildingDetails(null);

		locationTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showBuildingDetails(newValue));

		/**
		 * Tabelle der Raumfunitonen und die dazugehoerige Spalte werden
		 * initialisiert.
		 */

		// Spalte mit allen Kategorien.
		titleC.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getName()));

		showFunctionDetails(null);

		categoryTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showFunctionDetails(newValue));
	}

	/**
	 * Setzt die MainApp als aufrufende Scene. (Ist zum Beispiel wichtig fuer
	 * Standartwerte)
	 */
	public void setMainApp(final MainApp pMainApp) {
		this.mainApp = pMainApp;
		locationTable.setItems(GUIHandler.getBuildings());
		categoryTable.setItems(GUIHandler.getCategories());
		breakTable.setItems(GUIHandler.getBreaks());
		meetingTable.setItems(GUIHandler.getMeetingUnits());
	}

	/**
	 * Setzt die Dialogstage dieser Klasse, die wichtig ist falls man den View
	 * aufrufen moechte.
	 * 
	 * @param dialogStage
	 *            die DialogStage dieser Klasse.
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Button, der es moeglich macht die Standardeinstellungen zu speichern und
	 * zum Hauptmenue zu gelangen.
	 */
	@FXML
	public void btnRootLayout() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Standardeinstellungen beenden");
		alert.setHeaderText("Standardeinstellungen beenden. Beachten Sie bitte:");
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Nicht-gespeicherte Änderungen gehen verloren. Außerdem könnten \nzukünftige Änderungen der Standardeinstellungen Einfluss auf ihre Planung haben.");

		ButtonType buttonTypeOne = new ButtonType("Bestätigen");
		ButtonType buttonTypeCancel = new ButtonType("Abbrechen",
				ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne) {
			mainApp.refresh();
			dialogStage.close();
		} else {
			// es passiert nichts...
		}
	}

	/**
	 * Button, der das Speichern der Backup-Einstellungen realisiert.
	 */
	@FXML
	public void btnSaveBackups() {
		// Checkt, ob die eingegebenen Werte gueltig sind. Wenn nicht wird ein
		// Dialog geworfen.
		if (checkValidTabBackups()) {

			/**
			 * Werte des Benutzers werden gespeichert.
			 */
			// Das Intervall indem die Backups stattfinden sollen.
			// Sind nur Zahlen, deswegen keine Exception abfangen.
			String intervall = cbIntervall.getValue();

			// Der Titel des Backups
			String title = backupTitle.getText();

			// Wert, ob Backups beim Schliessen erstellt werden sollen.
			boolean backuponclose = false;
			if (backupOnCloseY.isSelected() == true
					&& backupOnCloseNo.isSelected() == false) {
				backuponclose = true;
			}

			/**
			 * Werte werden an die Config weitergereicht. Bei Erfolg wird ein
			 * Bestaetigungsdialog geworfen. Bei Misserfolg ein Exception-Dialog
			 */
			try {
				GUIHandler.editDefBackup(title, intervall, backuponclose);
				// Dialog, dass das abspeichern erfolgreich war.
				showInformationDialog("BackupGespeichert");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
		}
	}

	/**
	 * Button, der das Speichern der Planungseinstellungen, realisiert.
	 */
	@FXML
	public void btnSavePlanningOptions() {
		// Checkt, ob die eingegebenen Werte gueltig sind. Wenn nicht werden
		// Dialoge geworfen.
		if (checkValidTabPlanningOptions()) {

			// Tage, die verplant werden sollen, werden aus der Checkcombobox
			// geholt.
			ArrayList<WEEKDAYS> weekdaysPlanned = new ArrayList<WEEKDAYS>();
			for (String w : weekdaysForPlans.getCheckModel().getCheckedItems()) {
				weekdaysPlanned.add(WEEKDAYS.getByTitle(w));
			}

			// Wie lange eine Planungseinheit dauert:
			String stdDuration = durationUnitP.getText();

			// Wie detailliert diese angezeigt werden soll:
			String stdGranularity = granularityP.getText();

			// Begin des Schultages:
			String beginSchoolHours = beginHoursP.getText();
			String beginSchoolMinutes = beginMinutesP.getText();

			// Ende des Schultages:
			String endSchoolHours = endHoursP.getText();
			String endSchoolMinutes = endMinutesP.getText();

			/**
			 * Werte werden an die Config weitergereicht. Bei Erfolg wird ein
			 * Bestaetigungsdialog geworfen. Bei Misserfolg ein Exception-Dialog
			 */
			String[] days = new String[weekdaysPlanned.size()];
			for (int i = 0; i < days.length; i++) {
				days[i] = weekdaysPlanned.get(i).getTitle();
			}
			try {
				GUIHandler.editDefConfig(stdGranularity, beginSchoolHours,
						beginSchoolMinutes, endSchoolHours, endSchoolMinutes,
						stdDuration, days);
				this.refresh();
				showInformationDialog("PlanungGespeichert");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
		}
	}

	/**
	 * Button der die Zeiteinheiten speichert.
	 */
	@FXML
	public void btnSaveUnits() {
		// Checkt, ob die eingegebenen Werte gueltig sind. Wenn nicht wird ein
		// Dialog geworfen.
		if (checkValidTabUnits()) {
			String teacher = teacherMinutesForHour.getText();
			String ee = eeMinutesForHour.getText();
			GUIHandler.getConfig().setMinPerHourEduc(Integer.parseInt(ee));
			GUIHandler.getConfig().setMinPerHourTeacher(
					Integer.parseInt(teacher));
			showInformationDialog("speichernSaveUnits");
		}
	}

	/*
	 * Methoden, die das Hinzufuegen von Eintraegen realisieren. Zum Beispiel:
	 * Standorte, Raumfunktionen, Meetings, Pausen, Backupeinstellungen etc...
	 */

	/**
	 * Button, der das Hinzufuegen einer Pause realisiert.
	 */
	@FXML
	public void btnNewBreak() {
		// Checkt, ob die Eingabedaten gueltig sind. Wirft bei falschen
		// Eingabedaten Dialoge.
		if (checkValidTabBreaks()) {

			/**
			 * Eingabedaten des Benutzers
			 */
			// Der Jahrgang fuer den die Pause stattfinden soll.
			String year = breakYear.getSelectionModel().getSelectedItem()
					.toString();

			// Der Titel der Pause der im Textfeld uebergeben wird.
			String title = breakTitle.getText();

			// Stunde in der die Pause beginnen soll.
			String beginHours = breakBeginHours.getText();

			// Minuten in der die Pause beginnen soll.
			String beginMinutes = breakBeginMinutes.getText();

			// Dauer der Pause.
			String duration = breakDuration.getText();

			/**
			 * Tage der Pause. (Werden durch Benutzer festgelegt).
			 */
			ObservableList<String> checkedDays = weekdaysForBreak
					.getCheckModel().getCheckedItems();

			String[] days = checkedDays.toArray(new String[checkedDays.size()]);

			// Benutzerwerte werden dem GUIHandler uebergeben.
			try {
				GUIHandler.addDefBreak(duration, beginHours, beginMinutes,
						title, year, days);
				// Nach dem Hinzufuegen auf den Standard zuruecksetzen.
				// Hier kein Dialog. (Dialoge nur in Fehlerfaellen.
				clearAllFieldsInTabBreaks();
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
				n.printStackTrace();
			}
			// Fuegt die Pause zu der Tabelle hinzu.
			breakTable.getItems().removeAll(breakTable.getItems());
			breakTable.setItems(GUIHandler.getBreaks());
		}
	}

	/**
	 * Button, der das Hinzufuegen eines Meetings realisiert.
	 */
	@FXML
	public void btnNewMeeting() {
		// Checkt, ob die Eingabedaten gueltig sind.
		if (checkValidTabMeetings()) {

			// Speichert die Benutzerdaten.
			String title = titleM.getText();
			String duration = durationM.getText();
			String beginHours = beginHoursM.getText();
			String beginMinutes = beginMinutesM.getText();
			String dayofMeeting = meetingDay.getSelectionModel()
					.getSelectedItem();
			String year = meetingYear.getSelectionModel().getSelectedItem()
					.toString();

			try {
				GUIHandler.addDefMeeting(dayofMeeting, duration, beginHours,
						beginMinutes, title, year);

				// Nach dem Hinzufuegen GUI-Elemente auf den Standard
				// zuruecksetzen.
				clearAllFieldsInTabMeetings();

			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}

			// Fuegt das Meeting unserer Tabelle hinzu.
			meetingTable.setItems(GUIHandler.getMeetingUnits());

		}
	}

	/**
	 * Button, der das Hinzufuegen eines Standorts realisiert. Dabei werden die
	 * Informationen aus allen Feldern in lokalen Variablen gespeichert. Und ein
	 * neues Objekt erstellt.
	 */
	@FXML
	public void btnNewBuilding() {
		// Checkt, ob die Eingabedaten gueltig sind.
		if (checkValidTabBuildings()) {

			try {
				GUIHandler.addDefBuilding(titleLocation.getText(),
						minutesToLocation.getText());
				clearAllFieldsInTabBuildings();
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}

			// In die Tabelle eintragen.
			locationTable.setItems(GUIHandler.getBuildings());
		}
	}

	/**
	 * Button, der das Hinzufuegen einer Raumfunktion realisiert. Dabei werden
	 * die Informationen aus allen Feldern in lokalen Variablen gespeichert. Und
	 * ein neues Objekt erstellt.
	 */
	@FXML
	public void btnNewFunction() {
		// Eingabedaten des Benutzers werden auf Richtigkeit gecheckt.
		if (checkValidTabCategories()) {
			try {
				GUIHandler.addDefCategory(titleCategory.getText());
				clearAllFieldsInTabCategories();
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
			categoryTable.setItems(GUIHandler.getCategories());
		}
	}

	/*
	 * Methoden, die das Bearbeiten von Eintraegen realisieren. Zum Beispiel:
	 * Standorte, Meetings, Pausen..
	 */

	/**
	 * Button, der das Bearbeiten einer Pausen realisiert.
	 */
	@FXML
	public void btnEditBreak() {
		// Checkt, ob die Eingabedaten gueltig sind.
		if (checkValidTabBreaks()) {
			// Zwischengespeichertes ausgewaehltes Objekt.
			Break b = breakTable.getSelectionModel().getSelectedItem();

			/**
			 * Benutzereingaben.
			 */
			// Der Jahrgang fuer den die Pause stattfinden soll.
			String year = breakYear.getSelectionModel().getSelectedItem()
					.toString();

			// Der Titel der Pause der im Textfeld uebergeben wird.
			String title = breakTitle.getText();

			// Stunde in der die Pause beginnen soll.
			String beginHours = breakBeginHours.getText();

			// Minuten in der die Pause beginnen soll.
			String beginMinutes = breakBeginMinutes.getText();

			// Dauer der Pause.
			String duration = breakDuration.getText();

			/**
			 * Tage der Pause. (Werden durch Benutzer festgelegt).
			 */
			ObservableList<String> checkedDays = weekdaysForBreak
					.getCheckModel().getCheckedItems();

			String[] days = checkedDays.toArray(new String[checkedDays.size()]);

			// Werden dem GUIHandler uebergeben.
			try {
				GUIHandler.editDefBreak(b.getId(), beginHours, beginMinutes,
						duration, year, title, days);
				// Dialog anzeigen
				showInformationDialog("PauseGeaendert");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}

			// Aktualisiert die Tabellen.
			breakTable.getItems().removeAll(breakTable.getItems());
			breakTable.setItems(GUIHandler.getBreaks());

			// Nach dem Aendern auf den Standard zuruecksetzen.
			clearAllFieldsInTabBreaks();
		}
	}

	/**
	 * Button, der das Bearbeiten eines Meetings realisiert.
	 *
	 */
	@FXML
	public void btnEditMeeting() {
		if (checkValidTabMeetings()) {
			// Zwischengespeichertes Objekt.
			MeetingUnit m = meetingTable.getSelectionModel().getSelectedItem();
			// Objekt auf Benutzereingaben anpassen.
			String title = titleM.getText();
			String year = meetingYear.getValue().toString();
			String duration = durationM.getText();
			String beginHours = beginHoursM.getText();
			String beginMinutes = beginMinutesM.getText();
			String dayofMeeting = meetingDay.getSelectionModel()
					.getSelectedItem();

			try {
				GUIHandler.editDefMeeting(m.getId(), dayofMeeting, beginHours,
						beginMinutes, duration, year, title);
				// Dialog nach dem Aendern.
				showInformationDialog("MeetingGeaendert");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
				n.printStackTrace();
			}

			// Aktualisiert die Tabellen.
			meetingTable.getItems().removeAll(meetingTable.getItems());
			meetingTable.setItems(GUIHandler.getMeetingUnits());

			// Nach dem Aendern auf den Standard zuruecksetzen.
			clearAllFieldsInTabMeetings();

		}
	}

	/**
	 * Button, der das Bearbeiten eines Standorts realisiert.
	 * 
	 */
	@FXML
	public void btnEditBuilding() {
		if (checkValidTabBuildings()) {

			String building = titleLocation.getText();
			String distance = minutesToLocation.getText();

			try {
				GUIHandler.editDefBuilding(building, distance);
				showInformationDialog("StandortGeaendert");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}

			// Aktualisiert die Tabellen.
			locationTable.getItems().removeAll(locationTable.getItems());
			locationTable.setItems(GUIHandler.getBuildings());

			// Nach dem Aendern Felder auf den Standard zuruecksetzen.
			clearAllFieldsInTabBuildings();
		}
	}

	/*
	 * Methoden, die das Loeschen von Eintraegen realisieren. Zum Beispiel:
	 * Standorte, Raumfunktionen, Meetings, Pausen..
	 */

	/**
	 * Button, der das Loeschen einer Pause realisiert.
	 * 
	 * Setzt den Index auf den Tabellenindex, der ausgewaehlt wurde und entfernt
	 * das ausgewaehlte Element.
	 */
	@FXML
	public void btnDeleteBreak() {
		// Index der entfernt werden soll, wird aus der Tabelle ausgelesen.
		Break b = breakTable.getSelectionModel().getSelectedItem();

		if (b != null) {
			try {
				GUIHandler.removeDefBreak(b.getId());
				showInformationDialog("ErfolgreichGeloescht");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
			breakTable.setItems(GUIHandler.getBreaks());
		} else {
			showErrorDialog("LoeschenFail");
		}
	}

	/**
	 * Button, der das Entfernen eines Meetings realisiert.
	 * 
	 * Setzt den Index auf den Tabellenindex, der ausgewaehlt wurde und entfernt
	 * das ausgewaehlte Element.
	 */
	@FXML
	public void btnDeleteMeeting() {
		// Index der entfernt werden soll, wird aus der Tabelle ausgelesen.
		MeetingUnit m = meetingTable.getSelectionModel().getSelectedItem();

		if (m != null) {
			try {
				GUIHandler.removeDefMeeting(m.getId());
				showInformationDialog("ErfolgreichGeloescht");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
			meetingTable.setItems(GUIHandler.getMeetingUnits());
			clearAllFieldsInTabMeetings();
		} else {
			showErrorDialog("LoeschenFail");
		}
	}

	/**
	 * Button, der das Entfernen eines Standorts realisiert.
	 * 
	 * Setzt den Index auf den Tabellenindex, der ausgewaehlt wurde und entfernt
	 * das ausgewaehlte Element.
	 */
	@FXML
	public void btnDeleteBuildings() {
		if (locationTable.getSelectionModel().getSelectedItem() != null) {
			String building = locationTable.getSelectionModel()
					.getSelectedItem().getKey();
			try {
				GUIHandler.removeDefBuilding(building);
				showInformationDialog("ErfolgreichGeloescht");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
			locationTable.setItems(GUIHandler.getBuildings());
		} else {
			showErrorDialog("LoeschenFail");
		}
	}

	/**
	 * Button, der das Entfernen einer Raumfunktion realisiert.
	 */
	@FXML
	public void btnDeleteFunction() {
		Category c = categoryTable.getSelectionModel().getSelectedItem();
		if (c != null) {
			System.out.println(c.getName());
			if (c.getName().equals("Standard")) {
				showErrorDialog("StandardNichtLoschen");
			}
			else {
			try {
				GUIHandler.removeDefCategory(c.getName());
				showInformationDialog("ErfolgreichGeloescht");
			} catch (final NeedUserCorrectionException n) {
				this.exceptionDialog(n);
			}
			categoryTable.setItems(GUIHandler.getCategories());
		}} else {
			showErrorDialog("LoeschenFail");
		}	
	}

	/*
	 * Hilfsfunktionen, die Details in unserem DefaultView, also in den
	 * Standardeinstellungen anzeigen.
	 * 
	 * Dies geschieht, wenn Spalten aus den Tabellen ausgewaehlt werden zum
	 * Beispiel um diese zu Bearbeiten.
	 */

	/**
	 * Methode, die Details des Tabs Pausen anzeigt.
	 * 
	 * Hinweis: Man klickt auf einen Eintrag in die Pausen-Tabelle und die Werte
	 * werden in die Felder geladen. (Zum Beispiel zum Aendern).
	 * 
	 */
	private void showBreakDetails(final IBreak pBreak) {
		if (pBreak != null) {
			// Jahrgaenge fuer die die Pause stattfinden soll.
			this.breakYear.setValue(pBreak.getYear());

			// Titel der Pause.
			breakTitle.setText(pBreak.getTitle());

			// Beginn der Pause.

			breakBeginHours.setText(Timeslot.addStringHour(pBreak.getTimeslot()
					.getStartHour()));
			breakBeginMinutes.setText(Timeslot.addStringMinute(pBreak
					.getTimeslot().getStartMinute()));

			// Dauer der Pause.
			Integer i = pBreak.getTimeslot().getDuration();
			breakDuration.setText(i.toString());

			weekdaysForBreak.getCheckModel().clearChecks();
			for (WEEKDAYS w : pBreak.getDays()) {
				weekdaysForBreak.getCheckModel().check(w.getTitle());
			}
		} else {
			weekdaysForBreak.getCheckModel().clearChecks();
			breakYear.setValue(null);
			// Titel-Box auf leer.
			breakTitle.setText("");
			// Uhrzeit-Format auf leer.
			breakBeginHours.setText("");
			breakBeginMinutes.setText("");
			// Dauer-Box auf leer.
			breakDuration.setText("");
		}
	}

	/**
	 * Methode, die Details des Tabs Meeting anzeigt.
	 * 
	 * Hinweis: Man klickt auf einen Eintrag in die Pausen-Tabelle und die Werte
	 * werden in die Felder geladen. (Zum Beispiel zum Aendern).
	 */
	private void showMeetingDetails(final IMeetingUnit pMeeting) {
		if (pMeeting != null) {
			// Jahrgang fuer den ein Jahrgangstreffen festgelegt werden soll.
			Integer i = pMeeting.getYear();
			this.meetingYear.setValue(i);

			// Tag an dem das Treffen beginnen soll.
			meetingDay.setValue(pMeeting.getTimeslot().getDay().getTitle());

			// Stunden in denen das Treffen beginnen soll.
			beginHoursM.setText(Timeslot.addStringHour(pMeeting.getTimeslot()
					.getStartHour()));
			beginMinutesM.setText(Timeslot.addStringMinute((pMeeting
					.getTimeslot().getStartMinute())));

			// Dauer des Treffens
			Integer ii = pMeeting.getTimeslot().getDuration();
			durationM.setText(ii.toString());

			// Titel des Meetings
			titleM.setText(pMeeting.getTitle());
		} else {
			meetingYear.setValue(null);
			meetingDay.setValue(null);
			beginHoursM.setText("");
			beginMinutesM.setText("");
			durationM.setText("");
		}
	}

	/**
	 * Methode, die Details des Tabs Raumfunktionen anzeigt.
	 * 
	 * Hinweis: Man klickt auf einen Eintrag in die Standort-Tabelle und die
	 * Werte werden in die Felder geladen. (Zum Beispiel zum Aendern).
	 */
	private void showBuildingDetails(final Entry<String, Integer> pBuilding) {
		if (pBuilding != null) {
			// Name des Standorts
			titleLocation.setText(pBuilding.getKey());

			// Zeit um den Standort zu erreichen.
			Integer i = pBuilding.getValue();
			minutesToLocation.setText(i.toString());
		} else {
			titleLocation.setText("");
			minutesToLocation.setText("");
		}
	}

	/**
	 * Methode, die die Funktionen von Raeumen in das Textfeld laedt um dieses
	 * zum Beispiel zu bearbeiten.
	 */
	private void showFunctionDetails(final ICategory pCategory) {
		if (pCategory != null) {
			titleCategory.setText(pCategory.getName());
		} else {
			titleCategory.setText("");
		}
	}

	/*
	 * Hilfsfunktionen, die die Gueltigkeit von Werten der einzelnen Tabs
	 * ueberpruefen.
	 */

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs Backups
	 * gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabBackups() {
		// Testet den Titel auf Validitaet
		if (backupTitle.getText() == null || backupTitle.getText().isEmpty()
				|| backupTitle.getText().trim().isEmpty()) {
			showErrorDialog("LeererTitel");
			return false;
		}
		// Testet, ob ein Intervall ausgewaehlt wurde und gueltig ist.
		try {
			int intervall = Integer.parseInt(cbIntervall.getValue());
			if (intervall <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("KeinIntervall");
			return false;
		}
		// Testet, ob nicht "Ja" und "Nein" oder keines von beiden angekreuzt
		// wurde.
		if (backupOnCloseY.isSelected() == true
				&& backupOnCloseNo.isSelected() == true) {
			return false;
		}
		if (backupOnCloseY.isSelected() == false
				&& backupOnCloseNo.isSelected() == false) {
			showErrorDialog("KeineAngabeObBackupBeimSchliessen");
			return false;
		}

		// Wenn alle Prüfungen negativ ausfallen ist die Validitaet getestet und
		// es kann True zurueckgegebebn werden.
		return true;
	}

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs Planung
	 * gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabPlanningOptions() {

		// Checkt, ob kein Tag ausgewaehlt wurde.
		if (weekdaysForPlans.getCheckModel().getCheckedItems().isEmpty()) {
			showErrorDialog("KeinTagAusgewaehlt");
			return false;
		}

		// Ob eine leere Dauer uebergeben wurde oder keine Nummer.
		if (durationUnitP.getText() == null
				|| durationUnitP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		try {
			int duration = Integer.parseInt(durationUnitP.getText());
			if (duration <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}
		// Ob eine leere Granularitaet ubergeben worden ist oder keine Nummer.
		if (granularityP.getText() == null || granularityP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		try {
			int granularity = Integer.parseInt(granularityP.getText());
			if (granularity <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}

		// Beginn des Schultages
		if (beginHoursP.getText() == null || beginHoursP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		if (beginMinutesP.getText() == null
				|| beginMinutesP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Ende des Schultages
		if (endHoursP.getText() == null || endHoursP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		if (endMinutesP.getText() == null || endMinutesP.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Test, ob das Uhrenformat stimmt. Der Rest in der Logik...
		if (beginHoursP.getText().length() != 2
				|| beginMinutesP.getText().length() != 2) {
			showErrorDialog("FalschesFormat");
			return false;
		}
		if (endHoursP.getText().length() != 2
				|| endMinutesP.getText().length() != 2) {
			showErrorDialog("FalschesFormat");
			return false;
		}
		// Wenn alle Tests negativ waren, war die Benutzereingabe erfolgreich.
		// Es kann True zurueckgegeben werden.
		return true;
	}

	/**
	 * Methode, die ueberprueft ob die uebergebenen Benutzerwerte des Tabs
	 * Zeiteinheiten gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabUnits() {
		// Checkt auf leeres Textfeld
		if (teacherMinutesForHour == null
				|| teacherMinutesForHour.getText().isEmpty()
				|| teacherMinutesForHour.getText().trim().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Checkt auf falsche Eingaben.
		try {
			int hourMinutesTeacher = Integer.parseInt(teacherMinutesForHour
					.getText());
			if (hourMinutesTeacher <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}

		// Checkt auf leeres Textfeld
		if (eeMinutesForHour == null || eeMinutesForHour.getText().isEmpty()
				|| eeMinutesForHour.getText().trim().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Checkt auf falsche Eingaben.
		try {
			int hourMinutesEE = Integer.parseInt(eeMinutesForHour.getText());
			if (hourMinutesEE <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}
		return true;
	}

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs Pausen
	 * gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabBreaks() {

		// Testet, ob ein Jahrgang ausgewaehlt wurde und gueltig ist.

		if (breakYear.getValue() == null) {
			showErrorDialog("KeinJahrgangAusgewaehlt");
			return false;
		}

		// Der Titel der Pause der im Textfeld uebergeben wird.
		if (breakTitle.getText() == null || breakTitle.getText().isEmpty()
				|| breakTitle.getText().trim().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Stunde in der die Pause beginnen soll.
		if (breakBeginHours.getText() == null
				|| breakBeginHours.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		// Minuten in der die Pause beginnen soll.
		if (breakBeginMinutes.getText() == null
				|| breakBeginMinutes.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}

		// Test auf richtiges Format z.B 08:00
		if (breakBeginHours.getText().length() != 2
				|| breakBeginMinutes.getText().length() != 2) {
			showErrorDialog("FalschesFormat");
			return false;
		}

		// Dauer der Pause.
		try {
			int duration = Integer.parseInt(breakDuration.getText());
			if (duration <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}
		// Checkt, ob kein Tag ausgewaehlt wurde.
		if (weekdaysForBreak.getCheckModel().getCheckedItems().isEmpty()) {
			showErrorDialog("KeinTagAusgewaehlt");
			return false;
		}

		// Letzter Test, ob der Timeslot im Timeframe ist, bedeutet nicht die
		// Start und Endzeit des Schultages uebersteigt.
		Timeslot t = new Timeslot(Integer.parseInt(breakDuration.getText()),
				WEEKDAYS.NONE, breakBeginHours.getText(),
				breakBeginMinutes.getText());
		if ((t.isInTimeframe(GUIHandler.getConfig().getTimeframe()))) {
			return true;
		}
		showErrorDialog("NichtImTimeframe");
		return false;
	}

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs Meetings
	 * gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabMeetings() {
		// Testet, ob Jahrgang ausgewaehlt wurde.
		if (meetingYear.getValue() == null) {
			showErrorDialog("KeinJahrgangAusgewaehlt");
			return false;
		}

		// Testet,ob ein Tag ausgewaehlt wurde.
		if (meetingDay.getValue() == null) {
			showErrorDialog("KeinTagAusgewaehlt");
			return false;
		}

		// Testet, ob der uebegebene Titel ungueltig ist.
		if (titleM.getText() == null || titleM.getText().isEmpty()
				|| titleM.getText().trim().isEmpty()) {
			showErrorDialog("LeererTitel");
			return false;
		}

		// Testet, ob die Dauer gueltig ist
		try {
			int duration = Integer.parseInt(durationM.getText());
			if (duration <= 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}

		// Testet, ob die Zeitangabe gueltig ist.
		if (beginMinutesM.getText() == null
				|| beginMinutesM.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		if (beginHoursM.getText() == null || beginHoursM.getText().isEmpty()) {
			showErrorDialog("KeineAngabe");
			return false;
		}
		if (beginMinutesM.getText().length() != 2
				|| beginHoursM.getText().length() != 2) {
			showErrorDialog("FalschesFormat");
			return false;
		}

		// Letzter Test, ob der Timeslot im Timeframe ist, bedeutet nicht die
		// Start und Endzeit des Schultages uebersteigt.
		Timeslot t = new Timeslot(Integer.parseInt(durationM.getText()),
				WEEKDAYS.NONE, beginHoursM.getText(), beginMinutesM.getText());
		if ((t.isInTimeframe(GUIHandler.getConfig().getTimeframe()))) {
			// Wenn alle Tests positiv waren...
			return true;
		}
		showErrorDialog("NichtImTimeframe");
		return false;
	}

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs Standorte
	 * gueltig sind.
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabBuildings() {

		// Testet ob der Titel des Standorts gueltig ist.
		if (titleLocation.getText() == null
				|| titleLocation.getText().isEmpty()
				|| titleLocation.getText().trim().isEmpty()) {
			showErrorDialog("LeererTitel");
			return false;
		}

		// Testet, ob die Eingabe fuer die Entfernung zum Standort gueltig ist
		try {
			int minutesToLocate = Integer
					.parseInt((minutesToLocation.getText()));
			if (minutesToLocate < 0) {
				showErrorDialog("NegativerWert");
				return false;
			}
		} catch (NumberFormatException e) {
			showErrorDialog("FalscherWert");
			return false;
		}
		return true;
	}

	/**
	 * Methode, die ueberprueft, ob die uebergebenen Werte des Tabs
	 * Raumfunktionen gueltig sind.
	 * 
	 * Wenn nicht wird ein Dialog geworfen..
	 * 
	 * @return Boolean, ob Benutzerwerte gueltig sind.
	 */
	private boolean checkValidTabCategories() {
		if (titleCategory.getText() == null
				|| titleCategory.getText().isEmpty()
				|| titleCategory.getText().trim().isEmpty()) {
			showErrorDialog("LeererTitel");
			return false;
		}
		return true;
	}

	/*
	 * Hilfsfunktionen, um Felder in der GUI auf den Standard zurueckzusetzen.
	 * Diese Methoden werden zum Beispiel benoetigt, wenn man ein neues Objekt
	 * erstellt.
	 * 
	 * Also zum Beispiel "Pause hinzufuegen".
	 */

	/**
	 * Setzt die Werte des Tabs "Pausen" auf die Standardwerte.
	 */
	private void clearAllFieldsInTabBreaks() {
		breakYear.setValue(1);
		breakTitle.clear();
		breakBeginHours.clear();
		breakBeginMinutes.clear();
		breakDuration.clear();
		weekdaysForBreak.getCheckModel().clearChecks();
	}

	/**
	 * Setzt die Werte des Tabs "Meeting" auf die Standardwerte.
	 */
	private void clearAllFieldsInTabMeetings() {
		meetingYear.setValue(1);
		meetingDay.setValue(WEEKDAYS.MONDAY.getTitle());
		durationM.clear();
		beginHoursM.clear();
		beginMinutesM.clear();
		titleM.clear();
	}

	/**
	 * Setzt die Werte des Tabs "Standorte" auf die Standardwerte.
	 */
	private void clearAllFieldsInTabBuildings() {
		titleLocation.clear();
		minutesToLocation.clear();
	}

	/**
	 * Setzt die Werte des Tabs "Raumfunktionen" auf die Standardwerte.
	 */
	private void clearAllFieldsInTabCategories() {
		titleCategory.clear();
	}

	/*
	 * Hilfsmethoden fuer Dialoge: Diese zeigen spezifische Dialoge fuer die
	 * Methoden. Dabei muss man lediglich einen String uebergeben, welchen
	 * Dialog man angezeigt haben moechte.
	 */

	/**
	 * Methode, die Informationsdialoge erzeugt, dabei kann man ihr einen String
	 * uebergeben, welche Art von Dialog erzeugt werden soll.
	 * 
	 * @param pWhichDialog
	 *            Art des Dialogs der erzeugt werden soll.
	 */
	private void showInformationDialog(final String pWhichDialog) {

		switch (pWhichDialog) {
		// Dialog, wenn Backup erfolgreich angelegt wurde.
		case "BackupGespeichert":
			// Aktuelle Werte:
			String titleBackup = backupTitle.getText();
			int intervall = Integer.parseInt(cbIntervall.getValue());
			// Wert, ob Backups beim Schliessen erstellt werden sollen.
			String backuponclose = "Nein";
			if (backupOnCloseY.isSelected() == true
					&& backupOnCloseNo.isSelected() == false) {
				backuponclose = "Ja";
			}

			Alert alertBackup = new Alert(AlertType.INFORMATION);
			alertBackup.setTitle("Backupeinstellungen gespeichert");
			alertBackup.initStyle(StageStyle.UTILITY);
			alertBackup.setHeaderText("Backupeinstellungen gespeichert:");
			alertBackup
					.setContentText("Name des Backups: " + titleBackup + "\n"
							+ "Intervall des Backups: " + intervall
							+ " Minuten\n" + "Backup beim Schliessen: "
							+ backuponclose);
			alertBackup.showAndWait();
			break;

		/**
		 * Aenderungen von Elementen in den Standardeinstellungen.
		 */
		case "MeetingGeaendert":
			Alert alertMeeting = new Alert(AlertType.INFORMATION);
			alertMeeting.setTitle("Meeting geändert");
			alertMeeting.initStyle(StageStyle.UTILITY);
			alertMeeting.setHeaderText("Meeting geändert");
			alertMeeting
					.setContentText("Das Meeting wurde erfolgreich geändert!");
			alertMeeting.showAndWait();
			break;

		case "StandortGeaendert":
			// Speichert den Title um ihn im Dialog anzuzeigen.
			Alert alertBuilding = new Alert(AlertType.INFORMATION);
			alertBuilding.setTitle("Standort geändert");
			alertBuilding.setHeaderText("Standort geändert");
			alertBuilding.initStyle(StageStyle.UTILITY);
			alertBuilding
					.setContentText("Der Standort wurde erfolgreich geändert!");
			alertBuilding.showAndWait();
			break;

		case "PauseGeaendert":
			// Speichert den Title um ihn im Dialog anzuzeigen.
			Alert alertBreak = new Alert(AlertType.INFORMATION);
			alertBreak.setTitle("Pause geändert");
			alertBreak.initStyle(StageStyle.UTILITY);
			alertBreak.setHeaderText("Pause geändert");
			alertBreak.setContentText("Die Pause wurde erfolgreich geändert!");
			alertBreak.showAndWait();
			break;

		case "PlanungGespeichert":

			// Benutzereingaben:
			int stdDuration = Integer.parseInt(durationUnitP.getText());
			int stdGranularity = Integer.parseInt(granularityP.getText());
			String beginSchoolHours = beginHoursP.getText();
			String beginSchoolMinutes = beginMinutesP.getText();
			String endSchoolHours = endHoursP.getText();
			String endSchoolMinutes = endMinutesP.getText();

			Alert alertPlanning = new Alert(AlertType.INFORMATION);
			alertPlanning.setTitle("Planungseinstellungen geändert.");
			alertPlanning.initStyle(StageStyle.UTILITY);
			alertPlanning.setHeaderText("Planungseinstellungen geändert");
			alertPlanning.setContentText("Planungsdaten: " + "StandardDauer: "
					+ stdDuration + ", Standarddetaillierungsgrad: "
					+ stdGranularity + ", Start des Schultages: "
					+ beginSchoolHours + ":" + beginSchoolMinutes
					+ ", Ende des Schultages: " + endSchoolHours + ":"
					+ endSchoolMinutes);
			alertPlanning.showAndWait();
			break;

		case "ErfolgreichGeloescht":
			Alert alertDelete = new Alert(AlertType.INFORMATION);
			alertDelete.setTitle("Erfolgreich gelöscht.");
			alertDelete.initStyle(StageStyle.UTILITY);
			alertDelete.setHeaderText("Erfolgreich gelöscht");
			alertDelete
					.setContentText("Der Tabelleneintrag wurde erfolgreich gelöscht.");
			alertDelete.showAndWait();
			break;
		case "speichernSaveUnits":
			Alert alertSaveUnits = new Alert(AlertType.INFORMATION);
			alertSaveUnits.setTitle("Erfolgreich gespeichert.");
			alertSaveUnits.initStyle(StageStyle.UTILITY);
			alertSaveUnits.setHeaderText("Erfolgreich gespeichert.");
			alertSaveUnits
					.setContentText("Die Stundeneinstellungen wurden erfolgreich gespeichert.");
			alertSaveUnits.showAndWait();
			break;
		}
	}

	/**
	 * Methode, die Fehlerdialoge erzeugt, dabei kann man ihr einen String
	 * uebergeben, welche Art von Fehlerdialog erzeugt werden soll.
	 * 
	 * @param pWhichDialog
	 *            Art des Dialogs der erzeugt werden soll.
	 */
	private void showErrorDialog(final String pWhichDialog) {
		// Entscheidet, welcher Dialog angezeigt werden soll.
		switch (pWhichDialog) {
		case ("LeererTitel"):
			Alert alertEmptyTitle = new Alert(AlertType.WARNING);
			alertEmptyTitle.setTitle("Leerer Title");
			alertEmptyTitle.initStyle(StageStyle.UTILITY);
			alertEmptyTitle.setHeaderText("Leerer Titel");
			alertEmptyTitle.setContentText("Der Titel darf nicht leer sein!");
			alertEmptyTitle.showAndWait();
			break;
		case ("StandardNichtLoschen"):
			Alert alertDefault = new Alert(AlertType.WARNING);
			alertDefault.setTitle("Standard nicht löschbar");
			alertDefault.initStyle(StageStyle.UTILITY);
			alertDefault.setHeaderText("Standard nicht löschbar");
			alertDefault
					.setContentText("Sie können den Standort nicht löschen.");
			alertDefault.showAndWait();
			break;
		case ("KeinIntervall"):
			Alert alertNotIntervall = new Alert(AlertType.WARNING);
			alertNotIntervall.setTitle("Kein Intervall ausgewählt");
			alertNotIntervall.initStyle(StageStyle.UTILITY);
			alertNotIntervall.setHeaderText("Kein Intervall ausgewählt");
			alertNotIntervall
					.setContentText("Sie müssen ein Intervall auswählen!");
			alertNotIntervall.showAndWait();
			break;

		case ("KeineAngabeObBackupBeimSchliessen"):
			Alert alertBackupOnClose = new Alert(AlertType.WARNING);
			alertBackupOnClose.setTitle("Fehler");
			alertBackupOnClose.initStyle(StageStyle.UTILITY);
			alertBackupOnClose.setHeaderText("Backup beim Schließen");
			alertBackupOnClose
					.setContentText("Sie müssen auswählen, ob ein Backup beim Schließen erstellt werden soll!");
			alertBackupOnClose.showAndWait();
			break;

		case ("KeinTagAusgewaehlt"):
			Alert alertNoDay = new Alert(AlertType.WARNING);
			alertNoDay.setTitle("Keinen Tag ausgewählt.");
			alertNoDay.initStyle(StageStyle.UTILITY);
			alertNoDay.setHeaderText("Keinen Tag ausgewählt");
			alertNoDay.setContentText("Sie haben keinen Tag ausgewählt.");
			alertNoDay.showAndWait();
			break;

		case ("KeinJahrgangAusgewaehlt"):
			Alert alertNoYear = new Alert(AlertType.WARNING);
			alertNoYear.setTitle("Keinen Jahrgang ausgewählt.");
			alertNoYear.initStyle(StageStyle.UTILITY);
			alertNoYear.setHeaderText("Keinen Jahrgang ausgewählt");
			alertNoYear.setContentText("Sie haben keinen Jahrgang ausgewählt.");
			alertNoYear.showAndWait();
			break;

		case ("KeineAngabe"):
			Alert alertEmptyTextField = new Alert(AlertType.WARNING);
			alertEmptyTextField.setTitle("Leeres Textfeld");
			alertEmptyTextField.initStyle(StageStyle.UTILITY);
			alertEmptyTextField
					.setHeaderText("Nicht alle Textfelder ausgefüllt");
			alertEmptyTextField
					.setContentText("Sie haben nicht alle Textfelder ausgefüllt. Bitte korrigieren Sie ihre Eingaben.");
			alertEmptyTextField.showAndWait();
			break;

		case ("FalscherWert"):
			Alert alertNotValidValue = new Alert(AlertType.WARNING);
			alertNotValidValue.setTitle("Kein gültiger Wert.");
			alertNotValidValue.initStyle(StageStyle.UTILITY);
			alertNotValidValue.setHeaderText("Kein gültiger Wert übergeben");
			alertNotValidValue
					.setContentText("Vielleicht haben Sie ein falsches Format verwendet oder einen Buchstaben übergeben, wo eigentlich eine Zahl erwartet wurde.");
			alertNotValidValue.showAndWait();
			break;

		case ("NegativerWert"):
			Alert alertNegative = new Alert(AlertType.WARNING);
			alertNegative.setTitle("Kein gültiger Wert.");
			alertNegative.initStyle(StageStyle.UTILITY);
			alertNegative.setHeaderText("Kein gültiger Wert übergeben");
			alertNegative
					.setContentText("Bitte geben Sie einen positiven Wert ein.");
			alertNegative.showAndWait();
			break;

		case ("LoeschenFail"):
			Alert alertDeleteFail = new Alert(AlertType.WARNING);
			alertDeleteFail.setTitle("Keinen Eintrag ausgewählt.");
			alertDeleteFail.initStyle(StageStyle.UTILITY);
			alertDeleteFail.setHeaderText("Keinen Eintrag ausgewählt.");
			alertDeleteFail
					.setContentText("Sie haben keinen Tabelleneintrag zum Löschen ausgewählt.");
			alertDeleteFail.showAndWait();
			break;

		case ("PersonTeilnahme"):
			Alert alertNoPerson = new Alert(AlertType.WARNING);
			alertNoPerson.setTitle("Keine Personengruppe ausgewählt.");
			alertNoPerson.initStyle(StageStyle.UTILITY);
			alertNoPerson.setHeaderText("Keine Personengruppe ausgewählt");
			alertNoPerson
					.setContentText("Sie haben für ihr Meeting keine Personengruppe ausgewählt.");
			alertNoPerson.showAndWait();
			break;

		case ("FalschesFormat"):
			Alert alertTimeFormat = new Alert(AlertType.WARNING);
			alertTimeFormat.setTitle("Falsches Zeitformat");
			alertTimeFormat.initStyle(StageStyle.UTILITY);
			alertTimeFormat.setHeaderText("Falsches Zeitformat");
			alertTimeFormat
					.setContentText("Bitte beachten Sie das das Zeitformat folgendermaßen eingegeben werden muss: HH:MM Uhr");
			alertTimeFormat.showAndWait();
			break;
		case ("NichtImTimeframe"):
			Alert alertNotInTimeFrame = new Alert(AlertType.WARNING);
			alertNotInTimeFrame.setTitle("Falsche Uhrzeit");
			alertNotInTimeFrame.initStyle(StageStyle.UTILITY);
			alertNotInTimeFrame.setHeaderText("Falsche Uhrzeit");
			alertNotInTimeFrame
					.setContentText("Ihre Uhrzeit ist nicht im gueltigen Bereich. Gucken Sie im Tab Planung auf ihre Tageseinstellungen.");
			alertNotInTimeFrame.showAndWait();
			break;
		}
	}

	/**
	 * Methode, die Exceptions (aus der Logik) ausprinted
	 */
	private void exceptionDialog(Exception e) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("Hinweis");
		alert.setHeaderText("Hinweis");
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}

	/**
	 * Refreshen der Checkcomboboxen Dies ist wichtig, damit man nur Wochentage
	 * auswaehlen kann, die wirklich in der Planung beruecksichtigt wurden.
	 */
	private void refresh() {
		weekdaysForBreak.getItems().setAll(GUIHandler.getComboDays());
		meetingDay.getItems().setAll(GUIHandler.getComboDays());
	}
}