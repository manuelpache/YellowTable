/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik (GUIHandler) zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

// Importe... 
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Der Controller der das Speichern unter... realisiert.
 * 
 * @author tomlewandowski
 *
 */
public class SaveAsViewController {

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Das Textfeld das den neuen Namen enthaelt.
	 */
	@FXML
	private TextField saveAsName;

	/**
	 * Initialisiert die Klasse.
	 */
	public void initalize() {

	}

	/**
	 * Methode, die den Button, um zum RootLayout zurueckzukehren, realisiert.
	 */
	@FXML
	private void btnRootLayout() {
		dialogStage.close();
	}

	/**
	 * Button, der das Speichern unter realisiert. Der Name wird, wenn er
	 * gueltig ist an den GUIHandler weitergegeben.
	 */
	@FXML
	private void btnSave() {
		if (saveAsName.getText() == null || saveAsName.getText().isEmpty()
				|| saveAsName.getText().trim().isEmpty()) {
			Alert alertBreak = new Alert(AlertType.INFORMATION);
			alertBreak.setTitle("Kein Projekttitel");
			alertBreak.initStyle(StageStyle.UTILITY);
			alertBreak.setHeaderText("Kein Projektitel");
			alertBreak
					.setContentText("Sie haben vergessen einen Projektitel anzugeben.");
			alertBreak.showAndWait();
			
		} else {
			GUIHandler.saveAsProject(saveAsName.getText());
			dialogStage.close();
		}
	}

	/**
	 * Setzt die DialogStage neu.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}