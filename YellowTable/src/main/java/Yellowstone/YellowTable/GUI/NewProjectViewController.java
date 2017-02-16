/**
 * Der Controller gehoert zum GUI-Package und dient dazu den View mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

// Importe..
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Der Controller des NewProject-Views. Fuer naehere Information siehe
 * Architektur.
 * 
 * Von hier aus lässt sich ein neues Projekt erstellen.
 * 
 * @author tomlewandowski, manuelpache
 *
 */
public class NewProjectViewController {

	/**
	 * Attribut zum Setzen der MainApp.
	 */
	private MainApp mainApp;

	/**
	 * Ein Textfeld um den Titel des neuen Projekts zu uebergeben.
	 */
	@FXML
	private TextField projectTitle;

	/**
	 * Ein Textfeld um das Projekt naeher zu beschreiben.
	 */
	@FXML
	private TextField projectdescription;

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Initialisiert die Klasse.
	 */
	public void initialize() {
		// Auf Beispiele setzen.
		projectTitle.setText("Projektname");
		projectdescription.setText("Ihre Beschreibung");
	}

	/**
	 * Setzt die MainApp als aufrufende Scene. Wird benoetigt, da wir von hier
	 * aus die Standardeinstellungen aufrufen wollen.
	 */
	public void setMainApp(final MainApp mainApp) {
		this.mainApp = mainApp;
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
	 * Projekteinstellungen speichern und zu den Standardeinstellungen wechseln.
	 */
	@FXML
	public void saveAndDefault() {
		if (isValid()) {
			GUIHandler.getConfig().setProjectExist(true);
			GUIHandler.createInitialProject();
			GUIHandler.newProject(projectTitle.getText(),
					projectdescription.getText());
			mainApp.showDefaultView();
			dialogStage.close();
		}
	}

	/**
	 * Hilfsmethode, die ueberprueft, ob die Benutzereingaben valide sind.
	 * 
	 * Wenn dies nicht der Fall ist wird ein Dialog geworfen.
	 * 
	 */
	private boolean isValid() {
		if (projectTitle.getText() == null || projectTitle.getText().isEmpty()
				|| projectTitle.getText().trim().isEmpty()) {
			Alert alertBreak = new Alert(AlertType.INFORMATION);
			alertBreak.setTitle("Kein Projekttitel");
			alertBreak.setHeaderText("Kein Projektitel");
			alertBreak
					.setContentText("Sie haben vergessen einen Projektitel anzugeben.");
			alertBreak.showAndWait();
			return false;
		}
		if (projectdescription.getText() == null
				|| projectdescription.getText().isEmpty()
				|| projectdescription.getText().trim().isEmpty()) {
			Alert alertBreak = new Alert(AlertType.INFORMATION);
			alertBreak.setTitle("Keine Projektbeschreibung");
			alertBreak.setHeaderText("Keine Projektbeschreibung");
			alertBreak
					.setContentText("Sie haben vergessen eine Projektbeschreibung anzugeben.");
			alertBreak.showAndWait();
			return false;
		}
		return true;
	}
}