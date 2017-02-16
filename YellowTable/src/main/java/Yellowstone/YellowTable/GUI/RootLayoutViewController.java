/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importiert die Logikkomponenten, sowie zentrale Elemente des Java-FXML-Pakets.
 */

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.Optional;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.dialog.ProgressDialog;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Klasse, die den Controller unseres RootLayouts repraesentiert. Von diesem
 * View werden alle zentralen Aktivitaeten geregelt. Es stellt unser Hauptmenue
 * dar. Alle anderen Views sind von hier aus erreichbar.
 * 
 * @author manuelpache tomlewandowski
 *
 */
public class RootLayoutViewController {

	/*
	 * Attribute des RootLayoutViewControllers.
	 */
	/**
	 * Attribut zum setzen der MainApp.
	 */
	private MainApp mainApp;

	/*
	 * Attribute des Tabs Raueme:
	 */

	/**
	 * Textfeld, das die Bezeichnung des Raumes entgegen nimmt.
	 */
	@FXML
	private TextField roomTitle;

	/**
	 * Textfeld, das die Raumnummer entgegen nimmt.
	 */
	@FXML
	private TextField roomNumber;

	/**
	 * ComboBox, in der man angeben kann, an welchem Standort sich der Raum
	 * befindet.
	 */
	@FXML
	private ComboBox<String> roomBuilding;

	/**
	 * ComboBox, in der man angeben kann, welche Funktion, der Raum besitzen
	 * soll.
	 */
	@FXML
	private ComboBox<String> roomFunction;

	/**
	 * Tabelle, die eine Uebersicht ueber die bisher erstellten Raueme gibt.
	 */
	@FXML
	private TableView<Room> roomTable;

	/**
	 * Spalte, die alle Bezeichnungen der Raueme anzeigt.
	 */
	@FXML
	private TableColumn<Room, String> roomTitleColumn;

	/**
	 * Spalte, die alle Standorte, der dazugehoerigen Raueme anzeigt.
	 * 
	 */
	@FXML
	private TableColumn<Room, String> roomBuildingColumn;

	/**
	 * Spalte, die alle Raumnummern anzeigt.
	 */
	@FXML
	private TableColumn<Room, String> roomNumberColumn;

	/**
	 * Spalte, die alle Raumfunktionen anzeigt.
	 */
	@FXML
	private TableColumn<Room, String> roomFunctionColumn;

	/*
	 * Attribute des Tabs Facher:
	 */

	/**
	 * Textfeld, indem man den Titel des Faches angeben kann.
	 */
	@FXML
	private TextField subjectTitle;

	/**
	 * Textfeld, indem man die Dauer des Faches angeben kann.
	 */
	@FXML
	private TextField subjectDuration;

	/**
	 * ComboBox, mit der man die Raumfunktion, die fuer ein Fach benoetigt wird
	 * angeben kann.
	 */
	@FXML
	private ComboBox<String> subjectRoomFunction;

	/**
	 * ComboBox, mit der man die Schwierigkeit des Faches auswaehlen kann.
	 */
	@FXML
	private ComboBox<String> subjectDIFFICULTY;

	/**
	 * TableView, der eine Uebersicht aller angelegten Faecher liefert.
	 */
	@FXML
	private TableView<Subject> subjectTable;

	/**
	 * Spalte, die eine Uebersicht aller Fachbezeichnungen liefert.
	 */
	@FXML
	private TableColumn<Subject, String> subjectTitleColumn;

	/**
	 * Spalte, die eine Uebersicht der Dauer, eines jeweiligen Faches, liefert.
	 */
	@FXML
	private TableColumn<Subject, Number> subjectDurationColumn;

	/**
	 * Spalte, die eine Uebersicht aller Raumfunktionen liefert.
	 */
	@FXML
	private TableColumn<Subject, String> subjectRoomFunctionColumn;

	/**
	 * Spalte, die zwischen einen harten und einem weichen Fach unterscheidet.
	 */
	@FXML
	private TableColumn<Subject, String> subjectDifficultyColumn;

	/*
	 * Attribute des Tabs Lehrerinnen:
	 */

	/**
	 * Textfeld, mit dem man das Kuerzel einer Lehrerin uebergeben kann.
	 */
	@FXML
	private TextField teacherShort;

	/**
	 * Textfeld, mit dem man den Vornamen einer Lehrerin uebergeben kann.
	 */
	@FXML
	private TextField teacherFirstN;

	/**
	 * Textfeld, mit dem man den Nachnamen einer Lehrerin uebergeben kann.
	 */
	@FXML
	private TextField teacherLastN;

	/**
	 * Textfeld, mit dem man die Soll-Stunden einer Lehrerin uebergeben kann.
	 */
	@FXML
	private TextField teacherHours;

	/**
	 * Textfeld, mit dem man die Minus-Stunden einer Lehrerin uebergeben kann.
	 */
	@FXML
	private TextField teacherMinusHours;

	/**
	 * ComboBox, mit der man das Jahrgangsmeeting auswaehlen kann.
	 */
	@FXML
	private ComboBox<Number> teacherYearForMeeting;

	/**
	 * TableView, der eine Uebersicht ueber die erstellten Lehrer gibt.
	 */
	@FXML
	private TableView<Teacher> teacherTable;

	/**
	 * Spalte, die alle Kuerzel der Lehrer anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> teacherShortColumn;

	/**
	 * Spalte, die alle Namen der Lehrer anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> teacherLNameColumn;

	/**
	 * Spalte, die alle Vornamen der Lehrer anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> teacherFNameColumn;
	/**
	 * Spalte, die die Soll-Stunden der Lehrer anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, Number> teacherHoursColumn;

	/**
	 * Spalte, die die Minusstunden der Lehrer anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, Number> teacherMinusHoursColumn;

	/**
	 * Lehrer-Fach Wahl
	 */
	@FXML
	private CheckComboBox<String> teacherSubjectChoice;

	/*
	 * Attribute des Tabs paedagogische Mitarbeiterinnen:
	 */

	/**
	 * Textfeld, mit dem man das Kuerzel einer paed. Mitarbeiterin uebergeben
	 * kann.
	 */
	@FXML
	private TextField eeShort;

	/**
	 * Textfeld, mit dem man den Vornamen einer paed. Mitarbeiterin uebergeben
	 * kann.
	 */
	@FXML
	private TextField eeFirstN;

	/**
	 * Textfeld, mit dem man den Nachnamen einer paed. Mitarbeiterin uebergeben
	 * kann.
	 */
	@FXML
	private TextField eeLastN;

	/**
	 * Textfeld, mit dem man die Stunden einer paed. Mitarbeiterin uebergeben
	 * kann.
	 */
	@FXML
	private TextField eeHours;

	/**
	 * Textfeld, mit dem man die Minus-Stunden eines paed. Mitarbeiters
	 * uebergeben kann.
	 */
	@FXML
	private TextField eeMinusHours;

	/**
	 * TableView, der eine Uebersicht ueber die erstellten paed. Mitarbeiter
	 * gibt.
	 */
	@FXML
	private TableView<EducEmployee> eeTable;

	/**
	 * Spalte, die alle Kuerzel der paed. Mitarbeiter anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> eeShortColumn;

	/**
	 * Spalte, die alle Namen der paed. Mitarbeiter anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> eeLNameColumn;

	/**
	 * Spalte, die alle Vornamen der paed. Mitarbeiter anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> eeFNameColumn;

	/**
	 * Spalte, die die Soll-Stunden der paed. Mitarbeiter anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, Number> eeHoursColumn;

	/**
	 * Spalte, die die Minus-Stunden der paed. Mitarbeiter anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, Number> eeMinusHoursColumn;

	/**
	 * CheckComboBox für die Fächer eines EE
	 */
	@FXML
	private CheckComboBox<String> eeSubjectChoiceCC;

	/*
	 * Attribute des Tabs Jahrgaenge:
	 */

	/**
	 * Combo-Box mit der man den Jahrgang auswaehlen kann.
	 */
	@FXML
	private ComboBox<Number> year;

	/**
	 * Textfeld, das das ausgewaehlte Fach anzeigt.
	 */
	@FXML
	private TextField yearSubject;

	/**
	 * Textfeld, in der sich die Stundenanzahl fuer das jeweilige Fach des
	 * ausgewaehlten Jahrgangs einstellen laesst.
	 */
	@FXML
	private TextField yearSubjectHours;

	/**
	 * Tabelle, die eine Uebersicht ueber alle Jahrgaenge gibt.
	 */
	@FXML
	private TableView<Entry<Subject, Integer>> yearTable;

	/**
	 * Spalte, die alle Faecher eines Jahrgangs anzeigt.
	 */
	@FXML
	private TableColumn<Entry<Subject, Integer>, String> yearSubjectColumn;

	/**
	 * Spalte, die alle Stunden der Faecher eines Jahrgangs anzeigt.
	 */
	@FXML
	private TableColumn<Entry<Subject, Integer>, Number> yearSubjectHoursColumn;

	/*
	 * Attribute des Tabs Klassen:
	 */

	/**
	 * ComboBox, mit der man den Jahrgang auswaehlen kann.
	 */
	@FXML
	private ComboBox<Number> classYear;

	/**
	 * Textfeld, mit dem man die Bezeichnung einer Klasse uebergeben kann. Zum
	 * Beispiel 'A','B'
	 */
	@FXML
	private TextField classTitle;

	/**
	 * ComboBox, mit dem man den Klassenraum auswaehlen kann.
	 */
	@FXML
	private ComboBox<String> classRoom;

	/**
	 * Textfeld, indem ein ausgewaehltes Fach angezeigt wird.
	 */
	@FXML
	private TextField classSubject;

	/**
	 * Textfeld, mit dem man die Stunden eines Faches pro Klasse individuell
	 * anpassen kann.
	 */
	@FXML
	private TextField classHours;

	/**
	 * Tabelle, die alle Klassen (Jahrgang+Bezeichnung anzeigt)
	 */
	@FXML
	private TableView<Class> classTable;

	/**
	 * Spalte, die alle Bezeichnungen anzeigt.
	 */
	@FXML
	private TableColumn<Class, String> classTitleColumn;

	/**
	 * Spalte, die alle Jahrgaenge anzeigt.
	 */
	@FXML
	private TableColumn<Class, Number> classYearColumn;

	/**
	 * Tabelle, die alle Faecher einer Klasse samt Dauer anzeigt.
	 */
	@FXML
	private TableView<SubIntendedActual> classSubjectTable;

	/**
	 * Spalte, die alle Faecher anzeigt.
	 */
	@FXML
	private TableColumn<SubIntendedActual, String> classSubjectColumn;

	/**
	 * Spalte, die die Dauer aller Faecher anzeigt.
	 */
	@FXML
	private TableColumn<SubIntendedActual, String> classSubjectDurationColumn;

	/**
	 * Spalte, welche die bereits verplanten Stunden anzeigt.
	 */
	@FXML
	private TableColumn<SubIntendedActual, String> classSubjectPlanedHours;

	/*
	 * Attribute des Tabs Schwimmunterricht:
	 */

	/**
	 * ComboBox mit der man auswaehlen kann fuer welche Klasse Schwimmunterricht
	 * angelegt werden soll.
	 */
	@FXML
	private CheckComboBox<String> swimLessonClass;

	/**
	 * Tag an dem der Schwimmunterricht stattfinden soll.
	 */
	@FXML
	private ComboBox<String> swimLessonDay;

	/**
	 * Zeit zu dem der Schwimmunterricht beginnen soll. (Stunde)
	 */
	@FXML
	private TextField swimLessonBeginHour;

	/**
	 * Zeit zu dem der Schwimmunterricht beginnen soll. (Minuten)
	 */
	@FXML
	private TextField swimLessonBeginMinutes;

	/**
	 * Dauer des Schwimmunterrichts.
	 */
	@FXML
	private TextField swimLessonDuration;

	/**
	 * Tabelle mit der Uebersicht ueber den Schwimmunterricht.
	 * 
	 */
	@FXML
	private TableView<ExternUnit> swimLessonTable;

	/**
	 * Spalte, die alle Klassen anzeigt, die Schwimmunterricht haben.
	 */
	@FXML
	private TableColumn<ExternUnit, String> swimLessonClassesColumn;

	/**
	 * Spalte, die die Startzeit des Schwimmunterrichts anzeigt.
	 */
	@FXML
	private TableColumn<ExternUnit, String> swimLesssonBeginColumn;

	/**
	 * Spalte, die die Dauer des Schwimmunterrichts anzeigt.
	 */
	@FXML
	private TableColumn<ExternUnit, Number> swimLesssonDurationColumn;

	/*
	 * Attribute des Tabs Uebersicht:
	 */

	/**
	 * Tabellen, mit Uebersicht ueber alle Kapazitaten. Hier lassen sich
	 * individuelle Plaene auswaehlen.
	 */
	/**
	 * Uebersicht aller Klassen.
	 */
	@FXML
	private TableView<Class> overviewclassTable;

	/**
	 * Spalte in der Uebersichtstabelle, die den Titel einer Klasse anzeigt.
	 */
	@FXML
	private TableColumn<Class, String> overviewClassTitleColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die den Jahrgang einer Klasse anzeigt.
	 */
	@FXML
	private TableColumn<Class, Number> overviewYearTitleColumn;

	/**
	 * Uebersicht aller Lehrer.
	 */
	@FXML
	private TableView<Teacher> overviewteacherTable;

	/**
	 * Spalte in der Uebersichtstabelle, die das Kuerzel eines Lehrers anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> overviewteacherShortColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die den Vornamen eines Lehrers anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> overviewteacherFNameColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die den Nachnamen eines Lehrers
	 * anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, String> overviewteacherLNameColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Soll-Stunden eines Lehrers
	 * anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, Number> overviewteacherMaxWorkloadColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Minus-Stunden eines Lehrers
	 * anzeigt.
	 */
	@FXML
	private TableColumn<Teacher, Number> overviewteacherMinusHoursColumn;

	/**
	 * Tabelle in der Uebersicht, die eine Uebersicht uebersicht e aller paed.
	 * Mitarbeiter gibt.
	 */
	@FXML
	private TableView<EducEmployee> overviewEETable;

	/**
	 * Spalte in der Uebersichtstabelle, die das Kuerzel eines paed.
	 * Mitarbeiters anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> overvieweeShortColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die den Vornamen eines paed.
	 * Mitarbeiters anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> overvieweeFNameColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die den Nachnamen eines Mitarbeiters
	 * anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, String> overvieweeLNameColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Soll-Stunden eines paed.
	 * Mitarbeiters anzeigt.
	 */
	@FXML
	private TableColumn<EducEmployee, Number> overvieweeMaxWorkloadColumn;

	/**
	 * Tabelle in der Uebersicht aller bislang erstellten Raume.
	 * 
	 */
	@FXML
	private TableView<Room> overviewRoomTable;

	/**
	 * Spalte in der Uebersichtstabelle, die die Raumnummer anzeigt.
	 *
	 */
	@FXML
	private TableColumn<Room, String> overviewRoomNumberColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Raumbezeichnung anzeigt.
	 *
	 */
	@FXML
	private TableColumn<Room, String> overviewRoomTitleColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Standort des Raumes anzeigt.
	 *
	 */
	@FXML
	private TableColumn<Room, String> overviewRoomBuildingColumn;

	/**
	 * Spalte in der Uebersichtstabelle, die die Raumfunktion anzeigt.
	 *
	 */
	@FXML
	private TableColumn<Room, String> overviewRoomFunctionColumn;

	@FXML
	private Button btnClass;

	// Interessant fuer die Auswahl der Plaene...
	@FXML
	private Tab classes;

	@FXML
	private Tab teachers;

	@FXML
	private Tab eemployee;

	@FXML
	private Tab rooms;

	/**
	 * Eine ausgewählte Klasse
	 */
	private Class selectedClass;

	/**
	 * Ein ausgewählter Raum
	 */
	private Room selectedRoom;

	/**
	 * Eine ausgewählte Lehrkraft
	 */
	private Teacher selectedTeacher;

	/**
	 * Eine ausgewählter PädMitarbeiter
	 */
	private EducEmployee selectedEE;

	/**
	 * primaryStage
	 */
	private Stage primaryStage;

	/*
	 * Methoden des RootLayoutViewControllers.
	 */

	/**
	 * Setzt die MainApp als aufrufende Scene.
	 */
	public void setMainApp(final MainApp pMainApp) {
		this.mainApp = pMainApp;
		roomTable.setItems(GUIHandler.getRooms());
		subjectTable.setItems(GUIHandler.getSubjects());
		teacherTable.setItems(GUIHandler.getTeachers());
		eeTable.setItems(GUIHandler.getEducEmployees());
		// YearTable wird initial auf Jahrgang 1 gesetzt.
		yearTable.setItems(GUIHandler.getSubjectHoursPerYear(1));
		classTable.setItems(GUIHandler.getClasses());
		swimLessonTable.setItems(GUIHandler.getExternUnits());
		overviewclassTable.setItems(GUIHandler.getClasses());
		overviewteacherTable.setItems(GUIHandler.getTeachers());
		overviewRoomTable.setItems(GUIHandler.getRooms());
		overviewEETable.setItems(GUIHandler.getEducEmployees());
	}

	/**
	 * Key-Kombinationen für Undo/Redo uuund andere Sachen
	 * 
	 * @param pPrimary
	 */
	public void setPrimaryStage(Stage pPrimary) {
		this.primaryStage = pPrimary;

		final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.D,
				KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN);

		final KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.Z,
				KeyCombination.CONTROL_DOWN);

		final KeyCombination keyComb3 = new KeyCodeCombination(KeyCode.Y,
				KeyCombination.CONTROL_DOWN);

		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (keyComb1.match(event)) {
							try {
								java.awt.Desktop
										.getDesktop()
										.browse(new URI(
												"http://giphy.com/gifs/dog-confused-i-have-no-idea-what-im-doing-xDQ3Oql1BN54c/tile"));
							} catch (IOException | URISyntaxException e) {
								
								e.printStackTrace();
							}
						}
					}
				});

		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (keyComb2.match(event)) {
							mUndo();
						}
					}
				});

		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (keyComb3.match(event)) {
							mRedo();
						}
					}
				});
	}

	/**
	 * Methode welche den View initialisiert.
	 */
	public void initialize() {

		/**
		 * ComboBoxen
		 */
		roomBuilding.getItems().addAll(GUIHandler.getComboBuildings());

		roomFunction.getItems().addAll(GUIHandler.getComboCategories());

		subjectRoomFunction.getItems().addAll(GUIHandler.getComboCategories());

		subjectDIFFICULTY.getItems().addAll(GUIHandler.getComboDifficulties());

		teacherYearForMeeting.getItems().addAll(GUIHandler.getOptionalYears());

		year.getItems().addAll(GUIHandler.getYears());

		classYear.getItems().addAll(GUIHandler.getYears());

		classRoom.getItems().addAll(GUIHandler.getComboRooms());

		swimLessonClass.getItems().addAll(GUIHandler.getComboClasses());

		swimLessonDay.getItems().addAll(GUIHandler.getComboDays());

		// overviewExportFormat.getItems().addAll(GUIHandler.getComboClasses());

		teacherSubjectChoice.getItems().addAll(GUIHandler.getComboSubjects());

		eeSubjectChoiceCC.getItems().addAll(GUIHandler.getComboSubjects());

		year.getSelectionModel().select(0);

		/**
		 * RoomTable und Columns werden initialisiert.
		 */
		roomTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getName()));
		roomNumberColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getNumber()));
		roomBuildingColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getBuilding()));
		roomFunctionColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getCategory().getName()));

		showRoomDetails(null);

		roomTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showRoomDetails(newValue));

		/**
		 * SubjectTable und Columns werden initialisiert.
		 */
		subjectTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getTitle()));
		subjectDurationColumn.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getDuration()));
		subjectRoomFunctionColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getCategory().getName()));
		subjectDifficultyColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getDifficulty()
						.getTitle()));

		showSubjectDetails(null);

		subjectTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showSubjectDetails(newValue));

		/**
		 * TeacherTable und Columns werden initialisiert.
		 */
		teacherShortColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getShortName()));
		teacherFNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getFirstName()));
		teacherLNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getLastName()));
		teacherHoursColumn.setCellValueFactory(cellData -> GUIHandler
				.toFloatProperty(cellData.getValue().getMaxWorkload()));
		teacherMinusHoursColumn.setCellValueFactory(cellData -> GUIHandler
				.toFloatProperty(cellData.getValue().getMinusHours()));

		showTeacherDetails(null);

		teacherTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showTeacherDetails(newValue));

		/**
		 * EducEmployeeTable und Columns werden initialisiert.
		 */
		eeShortColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getShortName()));
		eeFNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getFirstName()));
		eeLNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getLastName()));
		eeHoursColumn.setCellValueFactory(cellData -> GUIHandler
				.toFloatProperty(cellData.getValue().getMaxWorkload()));
		eeMinusHoursColumn.setCellValueFactory(cellData -> GUIHandler
				.toFloatProperty(cellData.getValue().getMinusHours()));

		showEEDetails(null);

		eeTable.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showEEDetails(newValue));

		/**
		 * YearTable und Columns werden initialisiert
		 */
		yearSubjectColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getKey().getTitle()));
		yearSubjectHoursColumn.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getValue()));

		showYearDetails(null);

		yearTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showYearDetails(newValue));

		/**
		 * ClassTable und Columns werden initialisiert
		 */
		classTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getName()));
		classYearColumn.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getYear()));

		showClassDetails(null);

		classTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showClassDetails(newValue));

		classTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showSubjectsForClass(newValue));

		/**
		 * ClassSubjectTable und Columns werden initialisiert
		 */
		classSubjectColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getSubject()));
		classSubjectDurationColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getIntended()));
		classSubjectPlanedHours.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getActual()));

		showClassSubjectDetails(null);

		classSubjectTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showClassSubjectDetails(newValue));

		classSubjectTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> disabeButton());

		showSubjectsForClass(null);

		/**
		 * SwimLessonTable und Columns werden initialisiert
		 */
		swimLessonClassesColumn.setCellValueFactory(cellData -> GUIHandler
				.fromSet(cellData.getValue().getClasses()));
		swimLesssonBeginColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getStarttime()));
		swimLesssonDurationColumn.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getDuration()));

		showSwimLessonDetails(null);

		swimLessonTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showSwimLessonDetails(newValue));

		/**
		 * Overviews
		 */
		/**
		 * OverviewTable und Columns von Room
		 */
		overviewRoomTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getName()));
		overviewRoomNumberColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getNumber()));
		overviewRoomBuildingColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getBuilding()));
		overviewRoomFunctionColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getCategory().getName()));

		overviewRoomTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> setSelectedRoom(newValue));

		/**
		 * OverviewTable und Columns von Teacher
		 */
		overviewteacherShortColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getShortName()));
		overviewteacherFNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getFirstName()));
		overviewteacherLNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getLastName()));
		overviewteacherMaxWorkloadColumn
				.setCellValueFactory(cellData -> GUIHandler
						.toFloatProperty(cellData.getValue().getMaxWorkload()));
		overviewteacherMinusHoursColumn
				.setCellValueFactory(cellData -> GUIHandler
						.toFloatProperty(cellData.getValue().getMinusHours()));

		overviewteacherTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> setSelectedTeacher(newValue));

		/**
		 * OverviewTable und Columns von Päd-Mitarbeitern
		 */
		overvieweeShortColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getShortName()));
		overvieweeFNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getFirstName()));
		overvieweeLNameColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getLastName()));
		overvieweeMaxWorkloadColumn.setCellValueFactory(cellData -> GUIHandler
				.toFloatProperty(cellData.getValue().getMaxWorkload()));

		overviewEETable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> setSelectedEE(newValue));

		/**
		 * OverviewTable und Columns von Klassen
		 */
		overviewClassTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getName()));
		overviewYearTitleColumn.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getYear()));

		overviewclassTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> setSelectedClass(newValue));
	}

	@FXML
	private void tableClear() {
		classSubjectTable.getSelectionModel().clearSelection();
	}

	/**
	 * Setzt eine ausgewählte Klasse
	 * 
	 * @param pClass
	 */
	private void setSelectedClass(Class pClass) {
		selectedClass = pClass;
	}

	private void setSelectedRoom(Room pRoom) {
		selectedRoom = pRoom;
	}

	private void setSelectedTeacher(Teacher pTeacher) {
		selectedTeacher = pTeacher;
	}

	private void setSelectedEE(EducEmployee ee) {
		selectedEE = ee;
	}

	/**
	 * Deaktiviert/ Aktiviert den newClassButton
	 */
	public void disabeButton() {
		if (classSubjectTable.getSelectionModel().getSelectedItem() != null) {
			btnClass.setDisable(true);
		} else {
			btnClass.setDisable(false);
		}
	}

	/**
	 * Zeigt die Raum-Details in den Textfeldern und ComboBoxen Wir benoetigt,
	 * wenn man ein Objekt aus der Tabelle laedt.
	 * 
	 * @param room
	 */
	private void showRoomDetails(Room room) {
		if (room != null) {
			this.roomBuilding.setValue(room.getBuilding());
			this.roomFunction.setValue(room.getCategory().getName());
			roomTitle.setText(room.getName());
			roomNumber.setText(room.getNumber());
		} else {
			roomBuilding.setValue(null);
			roomFunction.setValue(null);
			roomTitle.clear();
			roomNumber.clear();

		}
	}

	/**
	 * Zeigt die Fach-Details in den Textfeldern und ComboBoxen
	 * 
	 * @param subject
	 */
	private void showSubjectDetails(Subject subject) {
		if (subject != null) {
			this.subjectRoomFunction.setValue(subject.getCategory().getName());
			this.subjectDIFFICULTY.setValue(subject.getDifficulty().getTitle());
			subjectTitle.setText(subject.getTitle());
			// parse int to String
			Integer int1 = new Integer(subject.getDuration());
			subjectDuration.setText(int1.toString());

		} else {
			subjectRoomFunction.setValue(null);
			subjectDIFFICULTY.setValue(null);
			subjectTitle.clear();
			Integer int1 = new Integer(GUIHandler.getConfig().getStdDuration());
			subjectDuration.setText(int1.toString());
		}
	}

	/**
	 * Zeigt die Lehrer-Details in Textfeldern und ComboBoxen
	 * 
	 * @param teacher
	 */
	private void showTeacherDetails(Teacher teacher) {
		if (teacher != null) {
			teacherYearForMeeting.setValue(teacher.getYearTeam());
			teacherShort.setText(teacher.getShortName());
			teacherFirstN.setText(teacher.getFirstName());
			teacherLastN.setText(teacher.getLastName());

			// Parse float to String.
			Float flo1 = new Float(teacher.getMaxWorkload());
			teacherHours.setText(flo1.toString());
			Float flo2 = new Float(teacher.getMinusHours());
			teacherMinusHours.setText(flo2.toString());
			teacherSubjectChoice.getCheckModel().clearChecks();
			for (Subject s : teacher.getSubjects()) {
				teacherSubjectChoice.getCheckModel().check(s.getTitle());
			}

		} else {
			teacherYearForMeeting.setValue(null);
			teacherShort.clear();
			teacherFirstN.clear();
			teacherLastN.clear();
			teacherHours.clear();
			teacherMinusHours.clear();
			teacherSubjectChoice.getCheckModel().clearChecks();
		}
	}

	/**
	 * Zeigt die EducEmployee-Details in Textfeldern und Comboboxen
	 * 
	 * @param ee
	 */
	private void showEEDetails(EducEmployee ee) {
		if (ee != null) {
			eeShort.setText(ee.getShortName());
			eeFirstN.setText(ee.getFirstName());
			eeLastN.setText(ee.getLastName());
			// parse float -> String
			Float flo1 = new Float(ee.getMaxWorkload());
			eeSubjectChoiceCC.getCheckModel().clearChecks();
			eeHours.setText(flo1.toString());
			Float flo2 = new Float(ee.getMinusHours());
			eeMinusHours.setText(flo2.toString());
			for (Subject s : ee.getSubjects()) {
				eeSubjectChoiceCC.getCheckModel().check(s.getTitle());
			}

		} else {
			eeShort.clear();
			eeFirstN.clear();
			eeLastN.clear();
			eeHours.clear();
			eeMinusHours.clear();
			eeSubjectChoiceCC.getCheckModel().clearChecks();
		}
	}

	/**
	 * Zeigt die Jahrgangs-Details in Textfeldern und ComboBoxen an
	 * 
	 */

	private void showYearDetails(Entry<Subject, Integer> pEntry) {
		if (pEntry != null) {
			yearSubject.setText(pEntry.getKey().getTitle());
			yearSubjectHours.setText(pEntry.getValue().toString());

		} else {
			yearSubject.clear();
			yearSubjectHours.clear();
		}
	}

	/**
	 * Zeigt die Details einer Klasse in Textfeldern und ComboBoxen
	 * 
	 * @param pClass
	 */
	private void showClassDetails(Class pClass) {
		if (pClass != null) {
			classYear.setValue(pClass.getYear());
			classRoom.setValue(pClass.getRoom().getName());
			classTitle.setText(pClass.getDescr());
		} else {
			classTitle.clear();
			classYear.setValue(null);
			classRoom.setValue(null);
		}
	}

	/**
	 * Zeigt die Details einer SwimmLesson in Textfeldern und ComboBoxen
	 * 
	 * @param unit
	 */
	private void showSwimLessonDetails(ExternUnit unit) {
		if (unit != null) {
			swimLessonClass.getCheckModel().clearChecks();
			for (Class s : unit.getClasses()) {
				swimLessonClass.getCheckModel().check(s.getName());
			}
			swimLessonDay.setValue(unit.getTimeslot().getDay().getTitle());
			swimLessonBeginHour.setText(Timeslot.addStringHour(unit
					.getTimeslot().getStartHour()));
			swimLessonBeginMinutes.setText(Timeslot.addStringMinute(unit
					.getTimeslot().getStartMinute()));
			Integer int1 = new Integer(unit.getDuration());
			swimLessonDuration.setText(int1.toString());
		} else {
			swimLessonBeginHour.clear();
			swimLessonBeginMinutes.clear();
			swimLessonDuration.clear();
			swimLessonClass.getCheckModel().clearChecks();
			swimLessonDay.setValue(null);
		}
	}

	/**
	 * Zeigt die individuellen Deteils von Unterrichtseinheiten einer Klasse
	 * 
	 * @param pEntry
	 */
	private void showClassSubjectDetails(SubIntendedActual pEntry) {
		if (pEntry != null) {
			classSubject.setText(pEntry.getSubject());
			classHours.setText(pEntry.getIntended());
		} else {
			classSubject.clear();
			classHours.clear();
		}
	}

	/**
	 * Läd die Verteilung der jeweiligen Klasse in die Tabelle für die
	 * Fächer/Zeit -Verteilung
	 * 
	 * @param pClass
	 */
	private void showSubjectsForClass(Class pClass) {
		if (pClass != null) {
			classSubjectTable.setItems(GUIHandler
					.getSubjectHoursPerClass(pClass.getName()));
		}
	}

	/**
	 * Button, der das Hinzufuegen eines Raumes realisiert.
	 */
	@FXML
	public void btnNewRoom() {
		String pTitel = roomTitle.getText();
		String pNumber = roomNumber.getText();
		String pBuilding = roomBuilding.getValue();
		String pFunction = roomFunction.getValue();

		if (!pTitel.isEmpty() && pBuilding != null && pFunction != null) {
			// Room r = new Room(pTitel, pBuilding, pNumber, pFunction);
			try {
				GUIHandler.addRoom(pTitel, pBuilding, pNumber, pFunction);
				roomTitle.clear();
				roomNumber.clear();
				roomBuilding.setValue(null);
				roomFunction.setValue(null);
			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshRooms();

		} else {
			missingInfoDialog("Räume");
		}
	}

	/**
	 * Button, der das Editieren eines Raumes realisiert.
	 */
	@FXML
	public void btnEditRoom() {
		if (roomTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				String pTitel = roomTitle.getText();
				String pNumber = roomNumber.getText();
				String pBuilding = roomBuilding.getValue();
				String pFunction = roomFunction.getValue();

				// benutze unveraenderten Primaerschluessel, statt maske fuer
				// titel
				try {
					GUIHandler.editRoom(pTitel, pNumber, pBuilding, pFunction);
					roomTitle.clear();
					roomNumber.clear();
					roomBuilding.setValue(null);
					roomFunction.setValue(null);
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				roomTable.getItems().removeAll(roomTable.getItems());
				refreshRooms();
			}
		}
	}

	/**
	 * Button, der das Loeschen eines Raumes realisiert.
	 */
	@FXML
	public void btnDeleteRoom() {
		if (roomTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				Room r = roomTable.getSelectionModel().getSelectedItem();
				try {
					GUIHandler.removeRoom(r.getName());
					roomTitle.clear();
					roomNumber.clear();
					roomBuilding.setValue(null);
					roomFunction.setValue(null);
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}
				refreshRooms();
				roomTable.getSelectionModel().selectLast();
			}
		}
	}

	/**
	 * Button, der das Hinzufuegen eines Fachs realisiert.
	 */
	@FXML
	public void btnNewSubject() {
		String pTitel = subjectTitle.getText();
		String pDuration = subjectDuration.getText();
		String pRoomFunction = subjectRoomFunction.getValue();
		String pDifficulty = subjectDIFFICULTY.getValue();

		if (pTitel != null && pRoomFunction != null && pDifficulty != null) {
			try {
				GUIHandler.addSubject(pTitel, pDifficulty, pDuration,
						pRoomFunction);
				subjectTitle.clear();
				subjectDuration.clear();
				subjectRoomFunction.setValue(null);
				subjectDIFFICULTY.setValue(null);

			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshSubjects();
		} else {
			missingInfoDialog("Fächer");
		}
	}

	/**
	 * Button, der das Editieren eines Fachs realisiert.
	 */
	@FXML
	public void btnEditSubject() {
		if (subjectTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				String pTitel = subjectTitle.getText();
				String pDuration = subjectDuration.getText();
				String pRoomFunction = subjectRoomFunction.getValue();
				String pDifficulty = subjectDIFFICULTY.getValue();

				try {
					GUIHandler.editSubject(pTitel, pDifficulty, pRoomFunction,
							pDuration);
					subjectTitle.clear();
					subjectDuration.clear();
					subjectRoomFunction.setValue(null);
					subjectDIFFICULTY.setValue(null);
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}
				
				refreshSubjects();
				refreshYear();
			}
		}
	}

	/**
	 * Button, der das Loeschen eines Fachs realisiert.
	 */
	@FXML
	public void btnDeleteSubject() {
		if (subjectTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				Subject s = subjectTable.getSelectionModel().getSelectedItem();

				try {
					GUIHandler.removeSubject(s.getTitle());
					subjectTitle.clear();
					subjectDuration.clear();
					subjectRoomFunction.setValue(null);
					subjectDIFFICULTY.setValue(null);
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				refreshSubjects();
				refreshYear();
				subjectTable.getSelectionModel().selectLast();
				year.getSelectionModel().clearAndSelect(0);
			}
		}
	}

	/**
	 * Button, der das Hinzufuegen eines Lehrers realisiert.
	 */
	@FXML
	public void btnNewTeacher() {
		String pShort = teacherShort.getText();
		String pFirst = teacherFirstN.getText();
		String pLast = teacherLastN.getText();
		String pHours = teacherHours.getText();
		String pMinus = teacherMinusHours.getText();
		Number pYearMeet = teacherYearForMeeting.getValue();

		ObservableList<String> subs = teacherSubjectChoice.getCheckModel()
				.getCheckedItems();

		if (pShort != null && pFirst != null && pLast != null && pHours != null
				&& pMinus != null) {
			try {
				if (pYearMeet != null) {
					GUIHandler.addTeacher(pShort, pLast, pFirst, pHours,
							pMinus, subs.toArray(new String[subs.size()]),
							pYearMeet.toString());
					teacherShort.clear();
					teacherFirstN.clear();
					teacherLastN.clear();
					teacherHours.clear();
					teacherMinusHours.clear();
					teacherYearForMeeting.setValue(null);
					teacherSubjectChoice.getCheckModel().clearChecks();
				} else {
					GUIHandler.addTeacher(pShort, pLast, pFirst, pHours,
							pMinus, subs.toArray(new String[subs.size()]), "0");
					teacherShort.clear();
					teacherFirstN.clear();
					teacherLastN.clear();
					teacherHours.clear();
					teacherMinusHours.clear();
					teacherYearForMeeting.setValue(null);
					teacherSubjectChoice.getCheckModel().clearChecks();
				}
			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshTeachers();
		} else {
			missingInfoDialog("LehrerInnen");
		}
	}

	/**
	 * Button, der das Editieren eines Lehrers realisiert.
	 */
	@FXML
	public void btnEditTeacher() {
		if (teacherTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				String pShort = teacherShort.getText();
				String pFirst = teacherFirstN.getText();
				String pLast = teacherLastN.getText();
				String pHours = teacherHours.getText();
				String pMinus = teacherMinusHours.getText();
				Number pYearMeet = teacherYearForMeeting.getValue();

				ObservableList<String> subs = teacherSubjectChoice
						.getCheckModel().getCheckedItems();

				try {
					GUIHandler.editTeacher(pShort, pFirst, pLast, pHours,
							pMinus, pYearMeet.toString(),
							subs.toArray(new String[subs.size()]));
					teacherShort.clear();
					teacherFirstN.clear();
					teacherLastN.clear();
					teacherHours.clear();
					teacherMinusHours.clear();
					teacherYearForMeeting.setValue(null);
					teacherSubjectChoice.getCheckModel().clearChecks();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				refreshTeachers();
			}
		}
	}

	/**
	 * Button, der das Loeschen eines Lehrers realisiert.
	 */
	@FXML
	public void btnDeleteTeacher() {
		if (teacherTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				Teacher t = teacherTable.getSelectionModel().getSelectedItem();
				try {
					GUIHandler.removeTeacher(t.getShortName());
					teacherShort.clear();
					teacherFirstN.clear();
					teacherLastN.clear();
					teacherHours.clear();
					teacherMinusHours.clear();
					teacherYearForMeeting.setValue(null);
					teacherSubjectChoice.getCheckModel().clearChecks();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}
				refreshTeachers();
				teacherTable.getSelectionModel().selectLast();
			}
		}
	}

	/**
	 * Button, der das Hinzufuegen eines paed. Mit. realisiert.
	 */
	@FXML
	public void btnNewEducEmployee() {
		String pName = eeShort.getText();
		String pFirst = eeFirstN.getText();
		String pLast = eeLastN.getText();
		String pHours = eeHours.getText();
		String pMinus = eeMinusHours.getText();

		ObservableList<String> subs = eeSubjectChoiceCC.getCheckModel()
				.getCheckedItems();

		if (pName != null && pFirst != null && pLast != null && pHours != null) {
			try {
				System.out.println("LAST: " + pLast);
				GUIHandler.addEduc(pName, pLast, pFirst, pHours, pMinus,
						subs.toArray(new String[subs.size()]));
				eeShort.clear();
				eeFirstN.clear();
				eeLastN.clear();
				eeHours.clear();
				eeMinusHours.clear();
				eeSubjectChoiceCC.getCheckModel().clearChecks();
			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshEducs();
		} else {
			missingInfoDialog("Päd-MitarbeiterInnen");
		}
	}

	/**
	 * Button, der das Editieren eines paed. Mit. realisiert.
	 */
	@FXML
	public void btnEditEducEmployee() {
		if (eeTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				String pName = eeShort.getText();
				String pFirst = eeFirstN.getText();
				String pLast = eeLastN.getText();
				String pHours = eeHours.getText();
				String pMinus = eeMinusHours.getText();

				ObservableList<String> subs = eeSubjectChoiceCC.getCheckModel()
						.getCheckedItems();

				try {
					GUIHandler.editEduc(pName, pFirst, pLast, pHours, pMinus,
							subs.toArray(new String[subs.size()]));
					eeShort.clear();
					eeFirstN.clear();
					eeLastN.clear();
					eeHours.clear();
					eeMinusHours.clear();
					eeSubjectChoiceCC.getCheckModel().clearChecks();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				refreshEducs();

			}
		}
	}

	/**
	 * Button, der das Loeschen eines paed. Mit. realisiert.
	 */
	@FXML
	public void btnDeleteEducEmployee() {
		if (eeTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				EducEmployee ed = eeTable.getSelectionModel().getSelectedItem();

				try {
					GUIHandler.removeEduc(ed.getShortName());
					eeShort.clear();
					eeFirstN.clear();
					eeLastN.clear();
					eeHours.clear();
					eeSubjectChoiceCC.getCheckModel().clearChecks();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				refreshEducs();
				eeTable.getSelectionModel().selectLast();
			}
		}
	}

	/**
	 * Button, der das Editieren eines Jahrgangs realisiert.
	 */
	@FXML
	public void btnEditYear() {
		if (yearTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {

				Number pYear = year.getValue();
				String pSubject = yearSubject.getText();
				String pHours = yearSubjectHours.getText();

				if (pYear != null && pSubject != null && pHours != null) {
					try {
						GUIHandler.editYearInfo(pYear.toString(), pSubject,
								pHours);

					} catch (final NeedUserCorrectionException e) {
						exceptionDialog(e);
					}
					refreshYear();
					year.getSelectionModel().selectFirst();
					refreshClassSubject();
				} else {
					missingInfoDialog("Jahrgänge");
				}
			}
		}
	}

	/**
	 * Button, der das Loeschen eines Jahrgangs realisiert.
	 */
	@FXML
	public void btnDeleteYear() {
		if (yearTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {

				Number pYear = year.getValue();
				String pSubject = yearSubject.getText();

				try {
					GUIHandler.editYearInfo(pYear.toString(), pSubject, "0");
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}
				refreshYear();
				refreshClassSubject();
				yearTable.getSelectionModel().selectFirst();
			}
		}
	}

	/**
	 * Button, der das Hinzufuegen einer Klasse realisiert.
	 */
	@FXML
	public void btnNewClass() {
		Number pYear = classYear.getValue();
		String pTitel = classTitle.getText();
		String pRoom = classRoom.getValue();

		if (pYear != null && pTitel != null && pRoom != null) {
			try {
				GUIHandler.addClass(pYear.toString(), pTitel, pRoom);
				classYear.setValue(null);
				classTitle.clear();
				classRoom.setValue(null);
			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshClasses();
		} else {
			missingInfoDialog("Klassen");
		}
	}

	/**
	 * Button, der das Editieren einer Klasse realisiert.
	 */
	@FXML
	public void btnEditClass() {
		if (classTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				Class cla = null;
				if (classSubjectTable.getSelectionModel().getSelectedItem() == null) {
					try {
						Number pYear = classYear.getValue();
						String pTitel = classTitle.getText();
						String pRoom = classRoom.getValue();
						System.out.println(pYear + " " + pTitel + " " + pRoom);
						GUIHandler.editClass(pYear.toString(), pTitel, pRoom);
						classYear.setValue(null);
						classTitle.clear();
						classRoom.setValue(null);
						classTable.getItems().removeAll(classTable.getItems());
						overviewclassTable.getItems().removeAll(
								overviewclassTable.getItems());
						refreshClasses();
					} catch (final NeedUserCorrectionException e) {
						exceptionDialog(e);
					}
				} else {
					try {
						String pClass2 = classTable.getSelectionModel()
								.getSelectedItem().getName();
						String pSub = classSubject.getText();
						String pHours = classHours.getText();
						cla = classTable.getSelectionModel().getSelectedItem();
						GUIHandler.addClassInfo(pClass2, pSub, pHours);
						classSubjectTable.setItems(GUIHandler
								.getSubjectHoursPerClass(cla.getName()));
						classTable.getSelectionModel().select(cla);
						classSubjectTable.getSelectionModel().selectFirst();
					} catch (NeedUserCorrectionException e) {
						exceptionDialog(e);
					}
				}
			}
		}
	}

	/**
	 * Button, der das Loeschen einer Klasse realisiert.
	 */
	@FXML
	public void btnDeleteClass() {
		if (classTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				if (classSubjectTable.getSelectionModel().getSelectedItem() == null) {
					System.out.println("Miiist");
					String pYear = classYear.getValue().toString();
					String pTitel = classTitle.getText();
					try {
						GUIHandler.removeClass(pYear, pTitel);
						classYear.setValue(null);
						classTitle.clear();
						classRoom.setValue(null);
						refreshClasses();
						classTable.getSelectionModel().selectFirst();

					} catch (final NeedUserCorrectionException e) {
						exceptionDialog(e);
					}
				} else {
					try {
						System.out.println("Alles ok");
						Class cla = classTable.getSelectionModel()
								.getSelectedItem();
						SubIntendedActual sub = classSubjectTable
								.getSelectionModel().getSelectedItem();
						GUIHandler.removeClassInfo(cla.getName(),
								sub.getSubject());
						classSubjectTable.setItems(GUIHandler
								.getSubjectHoursPerClass(cla.getName()));
						classTable.getSelectionModel().select(cla);
						classSubjectTable.getSelectionModel().selectFirst();
					} catch (NeedUserCorrectionException e) {
						exceptionDialog(e);
					}

				}
			}
		}
	}

	/**
	 * Button, der das Hinzufuegen von Schwimmunterricht realisiert.
	 */
	@FXML
	public void btnNewSwimLesson() {
		String[] pClasses = swimLessonClass.getCheckModel().getCheckedItems()
				.toArray(new String[0]);
		String pDay = swimLessonDay.getValue();
		String pHours = swimLessonBeginHour.getText();
		String pMinutes = swimLessonBeginMinutes.getText();
		String pDuration = swimLessonDuration.getText();

		// Erzeugt einen Timeslot, um zu ueberpruefen ob ob der Timeslot im
		// Timeframe ist, bedeutet nicht die Start und Endzeit des Schultages
		// uebersteigt.
		Timeslot t = new Timeslot(
				Integer.parseInt(swimLessonDuration.getText()), WEEKDAYS.NONE,
				swimLessonBeginHour.getText(), swimLessonBeginMinutes.getText());

		if (pClasses.length > 0 && pDay != null && pHours != null
				&& pMinutes != null && pDuration != null
				&& (t.isInTimeframe(GUIHandler.getConfig().getTimeframe()))) {
			try {
				GUIHandler.addSwimming(pDay, pDuration, pHours, pMinutes,
						pClasses);
				swimLessonClass.getCheckModel().clearChecks();
				swimLessonDay.setValue(null);
				swimLessonBeginHour.clear();
				swimLessonBeginMinutes.clear();
				swimLessonDuration.clear();
			} catch (final NeedUserCorrectionException e) {
				exceptionDialog(e);
			}
			refreshSwimLessons();
		} else {
			missingInfoDialog("Schwimmunterricht");
		}
	}

	/**
	 * Button, der das Editieren von Schwimmunterricht realisiert.
	 */
	@FXML
	public void btnEditSwimLesson() {
		if (swimLessonTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionEditDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich Ändern?");
			alert.setHeaderText("Hierdurch wird der Eintrag geändert!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {

				String[] pClasses = swimLessonClass.getCheckModel()
						.getCheckedItems().toArray(new String[0]);
				System.out.println("Klassen: " + pClasses.length);
				String pDay = swimLessonDay.getValue();
				String pHours = swimLessonBeginHour.getText();
				String pMinutes = swimLessonBeginMinutes.getText();
				String pDuration = swimLessonDuration.getText();

				ExternUnit ex = swimLessonTable.getSelectionModel()
						.getSelectedItem();

				try {
					GUIHandler.editExtern(ex.getId(), pDay, pHours, pMinutes,
							pDuration, ex.getTitle(), pClasses);
					swimLessonClass.getCheckModel().clearChecks();
					swimLessonDay.setValue(null);
					swimLessonBeginHour.clear();
					swimLessonBeginMinutes.clear();
					swimLessonDuration.clear();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				swimLessonTable.getItems()
						.removeAll(swimLessonTable.getItems());
				refreshSwimLessons();
			}
		}
	}

	/**
	 * Button, der das Loeschen von Schwimmunterricht realisiert.
	 */
	@FXML
	public void btnDeleteSwimLesson() {
		if (swimLessonTable.getSelectionModel().getSelectedItem() == null) {
			noSelectionDeleteDialog();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Wirklich löschen?");
			alert.setHeaderText("Hierdurch wird der Eintrag entfernt!");
			alert.setContentText("Wollen Sie wirklich fortfahren?");
			alert.initStyle(StageStyle.UTILITY);
			ButtonType buttonTypeOne = new ButtonType("Ok");
			ButtonType buttonTypeTwo = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne) {
				ExternUnit ex = swimLessonTable.getSelectionModel()
						.getSelectedItem();
				try {
					GUIHandler.removeExtern(ex.getId());
					swimLessonClass.getCheckModel().clearChecks();
					swimLessonDay.setValue(null);
					swimLessonBeginHour.clear();
					swimLessonBeginMinutes.clear();
					swimLessonDuration.clear();
				} catch (final NeedUserCorrectionException e) {
					exceptionDialog(e);
				}

				refreshSwimLessons();
				swimLessonTable.getSelectionModel().selectLast();
			}
		}
	}

	/**
	 * Button, der den View, der Lehrerwuensche laedt. Ruft die Show-Methode der
	 * Main-App auf.
	 */
	@FXML
	public void btnWishes() {
		if (selectedTeacher != null) {
			mainApp.showTeacherWishesView(this);
		} else {
			nothingChosenDialog("keinen Lehrer");
		}
	}

	/**
	 * Button, der den Teacher-Subject-View laedt. Ruft die Show-Methode der
	 * Main-App auf.
	 */
	@FXML
	public void btnTeacherSubject() {
		mainApp.showTeacherSubjectView();
	}

	/**
	 * Button, der den View, der Stundenplaene laedt.
	 */

	@FXML
	public void btnTable() {
		if (classes.isSelected()) {
			if (selectedClass != null) {
				mainApp.showClassTableView(this);
			} else {
				nothingChosenDialog("keine Klasse");
			}
		}

		if (eemployee.isSelected()) {
			if (selectedEE != null) {
				mainApp.showCarerView(this);
			} else {
				nothingChosenDialog("keinen Mitarbeiter");
			}

		}

		if (rooms.isSelected()) {
			if (selectedRoom != null) {
				mainApp.showRoomView(this);
			} else {
				nothingChosenDialog("keinen Raum");
			}
		}

		if (teachers.isSelected()) {
			if (selectedTeacher != null) {
				mainApp.showTeacherTableView(this);
			} else {
				nothingChosenDialog("keinen Lehrer");
			}
		}
	}

	/**
	 * Button, der den TagesView laedt. Ruft die Show-Methode der Main-App auf.
	 */
	@FXML
	public void btnDayTable() {
		mainApp.showDayTableView();
	}

	/**
	 * Button, der den PDF Viewer oeffnet.
	 */
	@FXML
	public void btnPDFViewer() {
		mainApp.showPDFView();
	}

	/**
	 * Button, in der Menueleiste, der ein neues Projekt erstellt.
	 */
	@FXML
	public void mNewProject() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Neues Projekt");
		alert.setHeaderText("Neues Projekt");
		alert.initStyle(StageStyle.UTILITY);
		alert.setContentText("Sind Sie sicher, dass Sie ein neues Projekt erstellen möchten? Nicht-gespeicherte Projekte gehen verloren.");

		ButtonType buttonTypeOne = new ButtonType("Bestätigen");
		ButtonType buttonTypeCancel = new ButtonType("Abbrechen",
				ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne) {
			GUIHandler.resetProject();
			progressDialog();
			refresh();
			mainApp.showNewProjectView();
		} else {
			// es passiert nichts...
		}
	}

	/**
	 * Button, in der Menueleiste, der ein neues Projekt oeffnet.
	 */
	@FXML
	public void mOpen() {
		Stage dialogStage = new Stage();
		DirectoryChooser chooser = new DirectoryChooser();

		String currentDir = System.getProperty("user.dir") + File.separator
				+ "Projekte" + File.separator;
		File file = new File(currentDir);

		if (!file.exists()) {
			file.mkdirs();
		}
		chooser.setInitialDirectory(file);
		chooser.setTitle("Öffnen");
		File dir = chooser.showDialog(dialogStage);
		if (dir != null && !dir.getName().endsWith("Yellowtable.db")) {
			GUIHandler.loadBackup(dir.getName(), false);
			progressDialog();
			refresh();
		}
	}

	/**
	 * Button, in der Menueleiste, der ein Projekt speichert. Wenn der Pfad im
	 * GUIHandler null ist, wird nichts getan.
	 */
	@FXML
	public void mSave() {
		if (GUIHandler.getConfig().getProjectTitle() != null) {
			GUIHandler.saveProject();
		} else {
			mSaveAs();
		}
	}

	/**
	 * Button, in der Menueleiste, der ein neues Projekt speichert. (Speichern
	 * unter..) 
	 * dann als Titel für den Pfad an den GUIHandler gegeben wird.
	 */
	@FXML
	public void mSaveAs() {
		mainApp.showSaveAsView();
	}

	/**
	 * Button, in der Menueleiste, der ein Undo ausfuehrt.
	 */
	@FXML
	public void mUndo() {
		GUIHandler.undo();
		refresh();
	}

	/**
	 * Button, in der Menueleiste, der ein Redo ausfuehrt.
	 */
	@FXML
	public void mRedo() {
		GUIHandler.redo();
		refresh();
	}

	/**
	 * Button, in der Menueleiste, der zum BackupView wechselt.
	 */
	@FXML
	public void mBackup() {
		Stage dialogStage = new Stage();
		DirectoryChooser chooser = new DirectoryChooser();

		String currentDir = System.getProperty("user.dir") + File.separator + "Backups";
		File file = new File(currentDir);
		if(!file.exists()){
			file.mkdir();
		}

		chooser.setInitialDirectory(file);
		chooser.setTitle("Ordner-Auswahl");
		File dir = chooser.showDialog(dialogStage);
		if (dir != null && dir.exists()) {
			String path = dir.getName();
			System.out.println("###### mBackup: " + path);
			progressDialog();
			GUIHandler.loadBackup(path, true);
			refresh();
		}

	}

	/**
	 * Button, in der Menueleiste, der das Handbuch oeffnet.
	 * 
	 */
	@FXML
	public void mHelp() {
		try {
			java.awt.Desktop
					.getDesktop()
					.browse(new URI(
							"https://drive.google.com/file/d/0B1ax6nigPlGDcFBjNVNBY19GazQ/view"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void mDefaultMenu() {
		GUIHandler.resetHistory();
		mainApp.showDefaultView();
	}

	/**
	 * Setzt die informationen des Ausgewählten Jahrgangs in den YearTable
	 */
	@FXML
	public void cbYearChoise() {
		yearTable.setItems(GUIHandler.getSubjectHoursPerYear(year.getValue()
				.intValue()));
	}

	/**
	 * Hinweis für nicht ausgefüllte Textfelder und ComboBoxen
	 * 
	 * @param pCase
	 */
	private void missingInfoDialog(String pCase) {
		Alert alertEmptyTitle = new Alert(AlertType.WARNING);
		alertEmptyTitle.setTitle("Fehlende Angaben für " + pCase);
		alertEmptyTitle.initStyle(StageStyle.UTILITY);
		alertEmptyTitle.setHeaderText("Leere Textfelder oder Auswahlboxen");
		alertEmptyTitle
				.setContentText("Bitte geben Sie alle geforderten Informationen an!");
		alertEmptyTitle.showAndWait();
	}

	/**
	 * HinweisDialog für das Löschen ohne eine getroffende Auswahl
	 */
	private void noSelectionDeleteDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Keine Auswahl getroffen");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Sie haben nichts zum Löschen ausgewählt!");
		alert.setContentText("Bitte wählen Sie zuerst einen Eintrag in der Tabelle.");
		alert.showAndWait();
	}

	/**
	 * HinweisDialog für das Editieren ohne eine getroffende Auswahl
	 */
	private void noSelectionEditDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Keine Auswahl getroffen");
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText("Sie haben nichts zum bearbeiten ausgewählt!");
		alert.setContentText("Bitte wählen Sie zuerst einen Eintrag aus der Tabelle");
		alert.showAndWait();
	}

	/**
	 * Wird aufgerufen, fall aus einem anderen View heraus der Datenbestand der
	 * Raumfunktionen geaendert wurde.
	 */
	public void refreshRoomfunctions() {
		roomFunction.setItems(GUIHandler.getComboCategories());
		subjectRoomFunction.setItems(GUIHandler.getComboCategories());
	}

	/**
	 * Wird aufgerufen, fall aus einem anderen View heraus der Datenbestand der
	 * Standorte/Gebaeude geaendert wurde.
	 */
	public void refreshBuildings() {
		roomBuilding.setItems(GUIHandler.getComboBuildings());
	}

	/**
	 * Erneuert alle Listen, die Informationen zu Raeumen enthalten. Private, da
	 * Raeume nur hier geaendert werden koennen.
	 */
	private void refreshRooms() {
		roomTable.getItems().clear();
		roomTable.setItems(GUIHandler.getRooms());
		classRoom.setItems(GUIHandler.getComboRooms());
	}

	/**
	 * Erneuert alle Listen, die Informationen zu Faechern/Stundeninhalten
	 * enthalten. Private, da Faecher nur hier geaendert werden koennen.
	 */
	private void refreshSubjects() {
		subjectTable.getItems().clear();
		subjectTable.setItems(GUIHandler.getSubjects());
		eeSubjectChoiceCC.getItems().clear();
		eeSubjectChoiceCC.getItems().addAll(GUIHandler.getComboSubjects());
		teacherSubjectChoice.getItems().clear();
		teacherSubjectChoice.getItems().addAll(GUIHandler.getComboSubjects());
		refreshYear();
	}

	private void refreshClasses() {
		classTable.setItems(GUIHandler.getClasses());
		overviewclassTable.setItems(GUIHandler.getClasses());
		swimLessonClass.getItems().clear();
		swimLessonClass.getItems().addAll(GUIHandler.getComboClasses());
	}

	private void refreshEducs() {
		eeTable.getItems().clear();
		overviewEETable.getItems().clear();
		eeTable.setItems(GUIHandler.getEducEmployees());
		overviewEETable.setItems(GUIHandler.getEducEmployees());
	}

	private void refreshTeachers() {
		teacherTable.getItems().clear();
		overviewteacherTable.getItems().clear();
		teacherTable.setItems(GUIHandler.getTeachers());
		overviewteacherTable.setItems(GUIHandler.getTeachers());
	}

	private void refreshSwimLessons() {
		swimLessonTable.getItems().clear();
		swimLessonTable.setItems(GUIHandler.getExternUnits());
	}

	private void refreshYear() {
		if (year.getValue() != null) {
			yearTable.setItems(GUIHandler.getSubjectHoursPerYear(year
					.getValue().intValue()));
		}
	}

	private void refreshClassSubject() {
		Class cla = classTable.getSelectionModel().getSelectedItem();
		if (cla != null) {
			classSubjectTable.setItems(GUIHandler.getSubjectHoursPerClass(cla
					.getName()));
		}
	}

	private void refresh() {
		refreshClassSubject();
		refreshRoomfunctions();
		refreshBuildings();
		refreshRooms();
		refreshSubjects();
		refreshTeachers();
		refreshEducs();
		refreshClasses();
		refreshSwimLessons();
		refreshYear();
	}

	/**
	 * Methode, die Exceptions ausprinted (Von Tom)
	 */
	public void exceptionDialog(Exception e) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Hinweis");
		alert.setHeaderText("Hinweis");
		alert.setContentText(e.getMessage());
		alert.initStyle(StageStyle.UTILITY);
		alert.showAndWait();
	}

	public void nothingChosenDialog(String selected) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Keine Auswahl getroffen");
		alert.setHeaderText("Sie haben " + selected + " ausgewählt!");
		alert.setContentText("Bitte wählen Sie zuerst einen Eintrag aus der Tabelle");
		alert.initStyle(StageStyle.UTILITY);
		alert.showAndWait();
	}

	public void progressDialog() {
		Service<Void> task = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						for (int i = 0; i < 100000000; i++) {

							this.updateProgress(i, 100000000);

						}
						return null;
					}
				};
			}
		};
		ProgressDialog progDiag = new ProgressDialog(task);
		progDiag.setTitle("Projekt wird geladen!");
		progDiag.initOwner(primaryStage);
		progDiag.setHeaderText("Haben Sie bitte einen Moment Geduld");
		progDiag.initModality(Modality.WINDOW_MODAL);
		progDiag.initStyle(StageStyle.UTILITY);
		task.start();
	}

	/**
	 * the Selected Objects
	 * 
	 * @return
	 */
	public Class getSelectedClass() {
		return selectedClass;
	}

	public Room getSelectedRoom() {
		return selectedRoom;
	}

	public Teacher getSelectedTeacher() {
		return selectedTeacher;
	}

	public EducEmployee getSelectedEE() {
		return selectedEE;
	}

}
