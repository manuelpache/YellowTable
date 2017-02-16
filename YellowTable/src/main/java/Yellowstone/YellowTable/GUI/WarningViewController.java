/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import Yellowstone.YellowTable.warnings.Warner;
import Yellowstone.YellowTable.warnings.Warning;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Controller des Warning-Views, dieser zeigt alle Warnungen an.
 * 
 * @author tomlewandowski
 *
 */
public class WarningViewController {

	/**
	 * Tabelle mit Warnings
	 */
	@FXML
	private TableView<Warning> warningTable;

	/**
	 * Die Spalte mit den Warnungsnummern
	 */
	@FXML
	private TableColumn<Warning, Number> numbers;

	/**
	 * Die Spalte mit den Warnungen
	 */
	@FXML
	private TableColumn<Warning, String> warning;

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Die Main-App.
	 */
	@FXML
	private MainApp mainApp;

	/**
	 * Initialisiert die Tabelle.
	 */
	public void initialize() {

		warning.setCellValueFactory(cellData -> GUIHandler
				.toStringProperty(cellData.getValue().getDescription()));
		numbers.setCellValueFactory(cellData -> GUIHandler
				.toIntegerProperty(cellData.getValue().getPriority()));
		warningTable.setItems(FXCollections.observableArrayList((Warner.WARNER
				.getWarnings())));
	}

	/**
	 * Setzt die Main App neu. Wird benoetigt, da die MainApp neu gesetzt werden
	 * muss, wenn der ClassTableView den WarningView aufruft
	 * 
	 * @param mainApp
	 *            die MainApp unseres Projekts
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Schliesst das Fenster.
	 */
	@FXML
	public void btnReturnClassViewTable() {
		dialogStage.close();
	}

	/**
	 * Setzt die DialogStage beim Aufruf neu.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}