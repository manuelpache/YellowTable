/**
 * Der Controller gehoert zum GUI-Package und dient dazu die Views mit der Logik zu verbinden.
 */
package Yellowstone.YellowTable.GUI;

/**
 * Importe. Dabei basiert diese Klasse auf folgendes Open-Source Projekt, 
 * das uns als Grundlage fuer den PDF-Viewer gedient hat:
 * 
 * https://java.net/projects/pdf-renderer/
 * https://github.com/james-d/PdfViewer
 * 
 * (Der fxml-View wurde komplett selbst gestaltet. Der Code wurde teilweise vom Open-Source-Projekt genommen.)
 * 
 * TODO PDFs koennen nicht in jeder Version geoeffnet werden.
 */
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * Controller, der unseren PDF-Viewer-View mit der Logik verbindet. Dabei haben
 * wir unseren PDF-Viewer auf Folgende Open-Source-Projekte gestützt, die wir
 * dann mit Java-FXML verbunden haben:
 * 
 * PDF-RENDERER LIBRARY
 * 
 * https://java.net/projects/pdf-renderer/ https://github.com/james-d/PdfViewer
 * 
 * Idee gefunden bei Stackoverflow.
 * 
 * 
 * Ans Projekt angepasst von:
 * 
 * @author tomlewandowski
 * 
 *         Um XML-File mit SceneBuilder anzupassen bitte folgende Zeilen
 *         entfernen:
 * 
 *         <fx:define> <ScrollPane fx:id="scroller" pannable="true" />
 *         </fx:define>
 *
 */
public class PDFViewController {

	/**
	 * Die DialogStage dieser Klasse. Wird beim Aufruf vom Hauptmenue benoetigt.
	 * 
	 */
	@SuppressWarnings("unused")
	private Stage dialogStage;

	/**
	 * Attribut, das die "Seitenauswahl" in unserem PDF-Viewer darstellt. Diese
	 * passt sich automatisch an das Input an.
	 */
	@FXML
	private Pagination pagination;

	/**
	 * Label, das den Skalierungs/Zommfaktor unseres Dokuments anzeigt (Oben
	 * Rechts im View).
	 */
	@FXML
	private Label currentZoomLabel;

	/**
	 * File-Chooser. Wird benötigt um Files von der Festplatte des Benutzers zu
	 * laden. In diesem Fall die PDFs.
	 */
	private FileChooser fileChooser;

	/**
	 * ObjectProperty, die das aktuelle PDF-File speichert.
	 */
	private ObjectProperty<PDFFile> currentFile;

	/**
	 * Wird benötigt um das Zoomen zu realisieren. (Beim Zoomen wird einfach
	 * immer ein neues Image erzeugt)
	 */
	private ObjectProperty<ImageView> currentImage;

	/**
	 * Scroll-Pane, die das PDF File innehaelt und somit das Scrollen
	 * ermoeglicht.
	 */
	@FXML
	private ScrollPane scroller;

	/**
	 * Property fuer einen Double, der unseren Zoom repraesentiert.
	 */
	private DoubleProperty zoom;

	/**
	 * Innere Klasse, die die Maße eine PDF beschreibt..
	 */
	private PageDimensions currentPageDimensions;

	/**
	 * Attribut, das wichtig fuer das Zoomen ist, da beim Zommen ein Bild der
	 * PDF erstellt wird.
	 */
	private ExecutorService imageLoadService;

	/**
	 * Statisches Attribut, dass den Zoom-Grad innehaelt. Mit diesem wird
	 * entweder dividiert oder multipliziert.
	 */
	private static final double ZOOM_DELTA = 1.05;

	/**
	 * Initialisiert den View beim Aufruf.
	 * 
	 * Um den Zoom-Effekt zu erzeugen wird einfach jedes mal ein neues Image vom
	 * PDF-File erzeugt.
	 * 
	 * Grundlegend funktioniert das auch. Allerdings werden einige PDFs schlecht
	 * angezeigt etc. (Vor allem LateX-Dokumente)
	 * 
	 * /TODO Eventuell Alternative zum PDF anzeigen ueberlegen falls noch genug
	 * Zeit ist!!
	 */
	public void initialize() {

		/**
		 * Threads werden erzeugt. (Um Bilder und Dateien zu laden).
		 */
		createAndConfigureImageLoadService();
		createAndConfigureFileChooser();

		/**
		 * Beim initialisieren wurde noch kein PDF geladen, deswegen wird
		 * zunächst eine neue leere Property erzeugt.
		 */
		currentFile = new SimpleObjectProperty<>();

		// Titel des Fensters wird auf Titel des PDFs geaendert.
		updateWindowTitleWhenFileChanges();

		/**
		 * Setzt das currentImage zunaechst auf eine leere Object-Property.
		 */
		currentImage = new SimpleObjectProperty<>();
		scroller.contentProperty().bind(currentImage);

		/**
		 * Der Zoom wird zu Beginn auf 100 Prozent (1) gesetzt.
		 */
		zoom = new SimpleDoubleProperty(1);

		/**
		 * Erzeugt einen neuen ChangeListener, der ueberprueft, ob das Zooming
		 * geandert wird.
		 */
		zoom.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				updateImage(pagination.getCurrentPageIndex());
			}
		});

		/**
		 * Setzt den aktuellen Zoom, dabei wird der Wert immer mal 100
		 * multipliziert.
		 */
		currentZoomLabel.textProperty().bind(
				Bindings.format("%.0f %%", zoom.multiply(100)));

		// Seitenzahlen werden an die PDF angepasst.
		bindPaginationToCurrentFile();

		createPaginationPageFactory();
	}

	/**
	 * Erzeugt wohl einen neuen Thread der ein Bild laedt...
	 */
	private void createAndConfigureImageLoadService() {
		imageLoadService = Executors
				.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setDaemon(true);
						return thread;
					}
				});
	}

	/**
	 * Erzeugt den FileChooser, der es moeglich macht Dateien von der
	 * Festplatte, des Benutzers zu laden (Egal ob Mac, Linux, Windows).
	 * 
	 * Dabei startet man immer im Home-Verzeichnis und es werden PDF-Dokumente
	 * als Auswahl erwartet.
	 */
	private void createAndConfigureFileChooser() {
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(Paths.get(
				System.getProperty("user.home")).toFile());
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
	}

	/**
	 * Aendert den Titel des Fensters auf den Titel des PDFs
	 */
	private void updateWindowTitleWhenFileChanges() {
		currentFile.addListener(new ChangeListener<PDFFile>() {
			@Override
			public void changed(ObservableValue<? extends PDFFile> observable,
					PDFFile oldFile, PDFFile newFile) {
				try {
					String title = newFile == null ? "PDF Viewer" : newFile
							.getStringMetadata("Title");
					Window window = pagination.getScene().getWindow();
					if (window instanceof Stage) {
						((Stage) window).setTitle(title);
					}
				} catch (IOException e) {
					showErrorMessage(
							"Konnte den Titel des PDFs nicht ermitteln", e);
				}
			}
		});
	}

	/**
	 * Die Seitenzahlen werden auf die aktuelle PDF-Datei angepasst.
	 * 
	 * Wenn das neue File {@code null} ist, wird der Index auf 0 gesetzt.
	 */
	private void bindPaginationToCurrentFile() {
		currentFile.addListener(new ChangeListener<PDFFile>() {
			@Override
			public void changed(ObservableValue<? extends PDFFile> observable,
					PDFFile oldFile, PDFFile newFile) {
				if (newFile != null) {
					pagination.setCurrentPageIndex(0);
				}
			}
		});
		pagination.pageCountProperty().bind(new IntegerBinding() {
			{
				super.bind(currentFile);
			}

			@Override
			protected int computeValue() {
				return currentFile.get() == null ? 0 : currentFile.get()
						.getNumPages();
			}
		});
		pagination.disableProperty().bind(Bindings.isNull(currentFile));
	}

	/**
	 * 
	 */
	private void createPaginationPageFactory() {
		pagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer pageNumber) {
				if (currentFile.get() == null) {
					return null;
				} else {
					if (pageNumber >= currentFile.get().getNumPages()
							|| pageNumber < 0) {
						return null;
					} else {
						updateImage(pageNumber);
						return scroller;
					}
				}
			}
		});
	}

	/**
	 * Öffnet per Button ein extra Fenster, um PDF-Dateien zu laden.
	 */
	@FXML
	private void loadFile() {
		final File file = fileChooser.showOpenDialog(pagination.getScene()
				.getWindow());
		if (file != null) {
			final Task<PDFFile> loadFileTask = new Task<PDFFile>() {
				@Override
				protected PDFFile call() throws Exception {
					try (RandomAccessFile raf = new RandomAccessFile(file, "r");
							FileChannel channel = raf.getChannel()) {
						ByteBuffer buffer = channel.map(
								FileChannel.MapMode.READ_ONLY, 0,
								channel.size());
						return new PDFFile(buffer);
					}
				}
			};
			loadFileTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					pagination.getScene().getRoot().setDisable(false);
					final PDFFile pdfFile = loadFileTask.getValue();
					currentFile.set(pdfFile);
				}
			});
			loadFileTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					pagination.getScene().getRoot().setDisable(false);
					showErrorMessage("Could not load file " + file.getName(),
							loadFileTask.getException());
				}
			});
			pagination.getScene().getRoot().setDisable(true);
			imageLoadService.submit(loadFileTask);
		}
	}

	/**
	 * Zoomt in das PDF. Dabei wird beim Zoomen mit unserer Konstante
	 * multipliziert.
	 */
	@FXML
	private void zoomIn() {
		zoom.set(zoom.get() * ZOOM_DELTA);
	}

	/**
	 * Zoomt aus dem PDF. Dabei wird beim Rauszoomen durch unsere Konstante
	 * dividiert.
	 */
	@FXML
	private void zoomOut() {
		zoom.set(zoom.get() / ZOOM_DELTA);
	}

	/**
	 * Zeigt das gesamte PDF-Dokument in der Scrollbar an, ohne das man Scrollen
	 * muss.
	 */
	@FXML
	private void zoomFit() {
		// the -20 is a kludge to account for the width of the scrollbars,
		// if showing.
		double horizZoom = (scroller.getWidth() - 20)
				/ currentPageDimensions.width;
		double verticalZoom = (scroller.getHeight() - 20)
				/ currentPageDimensions.height;
		zoom.set(Math.min(horizZoom, verticalZoom));
	}

	/**
	 * Setzt den Zoom auf die Breite des PDFs. Man muss nur noch nach unten
	 * Scrollen, die Breite wurde allerdings ans Fenster bzw. an die Scrollpane
	 * angepasst.
	 */
	@FXML
	private void zoomWidth() {
		zoom.set((scroller.getWidth() - 20) / currentPageDimensions.width);
	}

	/**
	 * Update vom Image
	 * 
	 * @param pageNumber
	 */
	private void updateImage(final int pageNumber) {
		final Task<ImageView> updateImageTask = new Task<ImageView>() {
			@Override
			protected ImageView call() throws Exception {
				PDFPage page = currentFile.get().getPage(pageNumber + 1);
				Rectangle2D bbox = page.getBBox();
				final double actualPageWidth = bbox.getWidth();
				final double actualPageHeight = bbox.getHeight();
				currentPageDimensions = new PageDimensions(actualPageWidth,
						actualPageHeight);
				final int width = (int) (actualPageWidth * zoom.get());
				final int height = (int) (actualPageHeight * zoom.get());
				java.awt.Image awtImage = page.getImage(width, height, bbox,
						null, true, true);
				BufferedImage buffImage = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				buffImage.createGraphics().drawImage(awtImage, 0, 0, null);
				// Konvertieren zum JAVA FX Image
				Image image = SwingFXUtils.toFXImage(buffImage, null);
				// Packt das Bild in einen ImageView
				ImageView imageView = new ImageView(image);
				imageView.setPreserveRatio(true);
				return imageView;
			}
		};

		updateImageTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				pagination.getScene().getRoot().setDisable(false);
				currentImage.set(updateImageTask.getValue());
			}
		});

		updateImageTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				pagination.getScene().getRoot().setDisable(false);
				updateImageTask.getException().printStackTrace();
			}

		});

		pagination.getScene().getRoot().setDisable(true);
		imageLoadService.submit(updateImageTask);
	}

	/**
	 * Methode, die geworfene Exceptions an den Benutzer weiterreicht. Dabei
	 * wird ein Dialog geoeffnet, der sich ausklappen laesst.
	 * 
	 * @param message
	 * @param exception
	 */
	private void showErrorMessage(String message, Throwable exception) {

		final Stage dialog = new Stage();
		dialog.initOwner(pagination.getScene().getWindow());
		dialog.initStyle(StageStyle.UNDECORATED);
		final VBox root = new VBox(10);
		root.setPadding(new Insets(10));
		StringWriter errorMessage = new StringWriter();
		exception.printStackTrace(new PrintWriter(errorMessage));
		final Label detailsLabel = new Label(errorMessage.toString());
		TitledPane details = new TitledPane();
		details.setText("Details:");
		Label briefMessageLabel = new Label(message);
		final HBox detailsLabelHolder = new HBox();

		Button closeButton = new Button("OK");
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.hide();
			}
		});
		HBox closeButtonHolder = new HBox();
		closeButtonHolder.getChildren().add(closeButton);
		closeButtonHolder.setAlignment(Pos.CENTER);
		closeButtonHolder.setPadding(new Insets(5));
		root.getChildren().addAll(briefMessageLabel, details,
				detailsLabelHolder, closeButtonHolder);
		details.setExpanded(false);
		details.setAnimated(false);

		details.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					detailsLabelHolder.getChildren().add(detailsLabel);
				} else {
					detailsLabelHolder.getChildren().remove(detailsLabel);
				}
				dialog.sizeToScene();
			}

		});
		final Scene scene = new Scene(root);

		dialog.setScene(scene);
		dialog.show();
	}

	/**
	 * Eine innere Klasse, die die Maße einer PDF-Page beschreibt. Dabei
	 * beseitzt eine Seite immer eine Hoehe und Breite. (Wird in Pixeln
	 * angegeben)
	 * 
	 * Nuetzlich fuer den Breiten-Zoom und der Zoom, bei dem das Dokument an die
	 * Seite angepasst wird. (Siehe Methoden zoomWidth() und zoomFit())
	 * 
	 * @author tomlewandowski
	 *
	 */
	private class PageDimensions {

		/**
		 * physikalische Breite der Seite.
		 */
		private double width;

		/**
		 * physikalische Hoehe der Seite.
		 */
		private double height;

		/**
		 * Konstruktor der Klasse. Dieser bekommt die Breite und Hoehe einer
		 * Seite uebergeben.
		 * 
		 * @param width
		 *            die Breite unserer Seite.
		 * @param height
		 *            die Hoehe unserer Seite.
		 */
		PageDimensions(double width, double height) {
			this.width = width;
			this.height = height;
		}

		/**
		 * Ueberschriebene toString()-Methode.
		 */
		@Override
		public String toString() {
			return String.format("[%.1f, %.1f]", width, height);
		}
	}

	/**
	 * Setzt die DialogStage neu..
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}