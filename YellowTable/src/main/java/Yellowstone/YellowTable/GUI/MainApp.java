/**
 * Die MainApp gehört zum GUI-Package und dient dazu das Programm zu starten.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importe..
 */
import java.io.IOException;
import java.util.Optional;

import Yellowstone.YellowTable.db.Backup;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import Yellowstone.YellowTable.warnings.Warner;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Die Main-App unseres Projekts. Von hier aus wird das Programm gestartet.
 * 
 * @author tomlewandowski, Manuel Pache
 * 
 */
public class MainApp extends Application {

	/**
	 * Die primaryStage unseres Views.
	 */
	private Stage primaryStage;

	/**
	 * Das Hauptmenue.
	 */
	private Pane rootLayout;

	/*
	 * Die Controller-Instanzen.
	 */
	private RootLayoutViewController controller;

	/**
	 * Die Start-Methode.
	 * 
	 * Sie setzt die primary-Stage auf die uebergebene Stage.
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Hauptmenü");
		initRootLayout();
	}

	/**
	 * Der Konstruktor der MainApp. Kann zum Beispiel dafuer genutzt werden um
	 * Beispieldaten in die GUI zu laden.
	 */
	public MainApp() {

	}

	/**
	 * Die Main-Methode.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		GUIHandler.initProject(null);
		Thread backup = new Thread(new Backup());
		backup.start();
		launch(args);
		backup.interrupt();
	}

	/**
	 * Gibt die primary-Stage zuereck.
	 *
	 * @return die primary-Stage.
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * 
	 * Initialisiert das RootLayout und somit unser Hauptmenue. Fur naehere
	 * Informationen siehe Architektur oder oeffne das RootLayout.fxml mit dem
	 * SceneBuilder.
	 * 
	 * Wenn das Programm zum ersten Mal gestartet wird, wird der ProjectView
	 * geoeffnet.
	 */
	public void initRootLayout() {
		try {
			// Laedt das RootLayout von der FXML-Datei.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("RootLayout.fxml"));
			rootLayout = (Pane) loader.load();

			// Laedt den Main-Controller.
			controller = loader.getController();
			controller.setMainApp(this);

			// Zeigt die Scene, die das RootLayout enthält.
			Scene scene = new Scene(rootLayout);

			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.setMaximized(true);
			controller.setPrimaryStage(primaryStage);
			primaryStage.show();
			this.primaryStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Falls das Programm zum ersten Mal gestartet wird.
			if (!(GUIHandler.projecttExists())) {
				showNewProjectView();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den Carer-View in unserer GUI. Dieser laesst sich von dem
	 * Hauptmenü aus oeffnen.
	 * 
	 * Er zeigt einen Wochenplan eines paed. Mitarbeiters an.
	 */
	public void showCarerView(RootLayoutViewController root) {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("CarerView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("StundenPlan: "
					+ root.getSelectedEE().getShortName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setScene(scene);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			CarerViewController controller = loader.getController();
			controller.loadDataToAgenda(root);
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Backup-View geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.setMaximized(true);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den ClassTableView von dem die zentrale Planung stattfinden wird.
	 */
	public void showClassTableView(RootLayoutViewController root) {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("ClassTable.fxml"));
			ScrollPane page = (ScrollPane) loader.load();

			// Erzeugt den Controller.
			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Stundenplan: "
					+ root.getSelectedClass().getName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaxHeight(900);
			dialogStage.setMaxWidth(1800);
			dialogStage.setResizable(true);
			// dialogStage.setMaximized(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			ClassTableViewController classCont = loader.getController();
			classCont.loadDataToAgenda(root);
			classCont.setDialogStage(dialogStage);
			Warner.WARNER.checkAll();
			GUIHandler.resetHistory();

			// Laedt den Main-Controller.
			classCont.setMainApp(this);
			// Sorgt dafuer, dass der Tagesplan geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den DayTable-View in unserer GUI. Dieser laesst sich von der
	 * Uebersicht des Hauptmenues aus oeffnen.
	 * 
	 * Er realisiert den Tagesplan in unserer GUI.
	 */
	public void showDayTableView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("DayTableView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Tagesplan");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaximized(true);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			DayTableViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Tagesplan geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Oeffnet die Standardeinstellungen in unserer GUI. Diese oeffnen sich beim
	 * ersten Start des Programmes oder alternativ vom Hauptmenue.
	 * 
	 * Beim Schliessen wird auch das Hauptmenue angezeigt.
	 * 
	 */
	public void showDefaultView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Default.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Standardeinstellungen");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Wenn man aufs Schliessen-Symbol klickt...
			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Standardeinstellungen beenden");
					alert.setHeaderText("Standardeinstellungen beenden. Beachten Sie bitte:");
					alert.initStyle(StageStyle.UTILITY);
					alert.setContentText("Nicht-gespeicherte Änderungen gehen verloren. Außerdem könnten \nzukünftige Änderungen der Standardeinstellungen Einfluss auf ihre Planung haben.");

					ButtonType buttonTypeOne = new ButtonType("Bestätigen");
					ButtonType buttonTypeCancel = new ButtonType("Abbrechen",
							ButtonData.CANCEL_CLOSE);

					alert.getButtonTypes().setAll(buttonTypeOne,
							buttonTypeCancel);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeOne) {
						refresh();
					} else {
						dialogStage.showAndWait();
					}
				}
			});

			// Erzeugt den Controller.
			DefaultViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Sorgt dafuer, dass die Standardeinstellungen geschlossen werden
			// muessen, bevor man weiter plant.
			dialogStage.setResizable(false);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * View mit dem man ein neues Projekt erzeugen kann.
	 * 
	 * Falls dieser geschlossen wird landet man in den Standardeinstellungen.
	 * 
	 */
	public void showNewProjectView() {
		// Laedt das FXML-File.
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("NewProjectView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			controller.setMainApp(this);

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Neues Projekt");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));
			dialogStage.setScene(scene);

			// Wenn man aufs Schliessen-Symbol klickt muss man immer in den
			// Standardeinstellungen landen. Wird benoetigt damit wir in keinem
			// ungueltigen Zustand landen.
			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					GUIHandler.getConfig().setProjectExist(true);
					GUIHandler.createInitialProject();
					GUIHandler.newProject("Projekt", "Beschreibung");
					showDefaultView();
				}
			});

			// Erzeugt den Controller.
			NewProjectViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			// Laedt den Main-Controller.
			controller.setMainApp(this);

			// Sorgt dafuer, dass das Fenster geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den PDFView, der von der Uebersicht des Hauptmenues aufrufbar
	 * ist.
	 * 
	 * In diesem View lassen sich PDFs anzeigen.
	 */
	public void showPDFView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("PDFViewerView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("PDF-Viewer");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			PDFViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Tagesplan geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zeigt einen View, der einen Raumplan repraesentiert (Fuer den jeweils
	 * ausgewaehlten Raum)
	 */
	public void showRoomView(RootLayoutViewController root) {
		try {
			// TODO
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("RoomView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Raumplan: "
					+ root.getSelectedRoom().getName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaximized(true);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			RoomViewController controller = loader.getController();
			controller.loadDataToAgenda(root);
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Tagesplan geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den View der das Speichern unter in unserer GUI realisiert.
	 */
	public void showSaveAsView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("SaveAsView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Speichern unter...");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			SaveAsViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Teacher-Subject-View geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den Teacher-Subject-View in unserer GUI. Dieser laesst sich von
	 * der Uebersicht des Hauptmenues aus oeffnen.
	 */
	public void showTeacherSubjectView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("TeacherSubjectView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Lehrer-Fachverteilung");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaximized(true);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			TeacherSubjectViewController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Teacher-Subject-View geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Zeigt ein Fenster mit einem Lehrerstundenplan an.
	 */
	public void showTeacherTableView(RootLayoutViewController root) {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("TeacherTable.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Stundenplan: "
					+ root.getSelectedTeacher().getShortName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaximized(true);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			TeacherTableViewController controller = loader.getController();
			controller.loadDataToAgenda(root);
			controller.setDialogStage(dialogStage);

			// Sorgt dafuer, dass der Teacher-Subject-View geschlossen werden
			// muss, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Oeffnet den Wishes-View in unserer GUI. Dieser laesst sich von der
	 * Uebersicht des Hauptmenues aus oeffnen.
	 */
	public void showTeacherWishesView(RootLayoutViewController root) {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Wishes.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Wunschezeiten: "
					+ root.getSelectedTeacher().getShortName());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setMinHeight(768);
			dialogStage.setMinWidth(1024);
			dialogStage.setMaximized(true);
			dialogStage.setResizable(true);
			dialogStage.getIcons().add(
					new Image("file:src/main/resources/school.png"));

			// Erzeugt den Controller.
			WishesViewController controllerw = loader.getController();
			controllerw.setDialogStage(dialogStage);

			dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					controllerw.saveAppointment();
				}
			});
			controllerw.loadDataToAgenda(root);

			// Sorgt dafuer, dass die Lehrerwuensche geschlossen werden
			// muessen, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Oeffnet den Warning-View in unserer GUI. Dieser laesst sich von der
	 * Warnungsbox des Classtableviews oeffnen sowie aus dem Hauptmenue.
	 */
	public void showWarningView() {
		try {
			// Laedt das FXML-File.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("WarningView.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Erzeugt die Stage mitsamt der Ueberschrift etc.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Alle Warnungen");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Erzeugt den Controller.
			WarningViewController controllerw = loader.getController();
			// Laedt den Main-Controller.
			controllerw.setMainApp(this);
			controllerw.setDialogStage(dialogStage);

			// Sorgt dafuer, dass die Lehrerwuensche geschlossen werden
			// muessen, bevor man weiter plant.
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO Aktualisiert alle Tabellen.
	 */
	public void refresh() {
		controller.refreshRoomfunctions();
		controller.refreshBuildings();
	}

	/**
	 * Gibt den Hauptcontroller zurueck.
	 * 
	 * @return
	 */
	public RootLayoutViewController getRootLayout() {
		return this.controller;
	}
}
