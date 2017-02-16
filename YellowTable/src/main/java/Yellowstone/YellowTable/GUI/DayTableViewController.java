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
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import au.com.bytecode.opencsv.CSVWriter;
import Yellowstone.YellowTable.logic.handler.gui_handler.GUIHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Der Controller des DayTable-Views. Fuer naehere Information siehe
 * Architektur.
 * 
 * Hier habe ich das Spreadsheet von JFxtras verwendet..
 * 
 * Fuer genaure Informationen siehe http://jfxtras.org
 * 
 * @author tomlewandowski , Erik
 *
 */
public class DayTableViewController {

	// Spreadsheet

	/**
	 * Das Spreadsheet dieser Klasse. Wird benoetigt um den ClassTableView
	 * darzustellen.
	 */
	@FXML
	private SpreadsheetView spreadsheet;

	/**
	 * Das Grid des Spreadsheets
	 */
	private GridBase grid;

	/**
	 * Die Reihen des Spreadsheets
	 */
	private int spreadRow;

	/**
	 * Die Zeilen des Spreadsheets
	 */
	private int spreadColumn;

	/**
	 * Liste mit dem Zeitraster.
	 */
	private List<String> timeslots;

	/**
	 * Liste, die eine Liste enthaelt, die jedem Lehrer fuer einen Timeslot die
	 * Klassen zuordnet. Bedeutet jede Liste in der Liste ist einem Lehrer
	 * zugeordnet der zu gewissen Timeslots Klassen betreut.
	 * 
	 * Diese werden dann im Spreadsheet angezeigt.
	 * 
	 */
	private List<List<String>> mainList;

	/**
	 * ChoiceBox, mit der man die Auswahl treffen kann, welcher Wochentag
	 * angezeigt werden soll.
	 */
	@FXML
	private ComboBox<String> weekDayBox;

	/**
	 * Die DialogStage dieser Klasse.
	 */
	private Stage dialogStage;

	/**
	 * Node für den PDF-EXPORT
	 */
	Node node;

	/**
	 * Pfad zum Ordner (Wichtig fuer Dateiexport)
	 */
	String path = (System.getProperty("user.dir")) + File.separator;

	/**
	 * Initialisiert die Klasse.
	 */
	public void initialize() {

		// Holt das Zeitraster aus dem GUIHandler (Dieses ist von der Start und
		// Endzeit, sowie der Granularitaet abhaengig, die der Benutzer in den
		// Standardeinstellungen festgelegt hat.
		timeslots = GUIHandler.getTimeFrame();

		// Initalialisiert die ComboBox mit den Wochentagen.
		weekDayBox.getItems().addAll(GUIHandler.getComboDays());

		// Setzt den Wochentag auf Montag, damit keine Probleme entstehen und
		// direkt ein Spreadsheet angezeigt wird.
		weekDayBox.setValue("Montag");

		// Initialisiert das Spreadsheet...
		this.initializeSpreadSheet(weekDayBox.getValue());

		// Aktualisiert das Spreadsheet, falls der Wochentag geandert wird.
		weekDayBox.setOnAction((event) -> {
			this.initializeSpreadSheet(weekDayBox.getValue());
		});
	}

	/**
	 * Initialisiert das Spreadsheet.
	 * 
	 * @param pRow
	 *            Die Anzahl der Reihen des Spreadsheets
	 * @param pColumn
	 *            Die Anzahl der Spalten des Spreadsheets
	 */
	private void initializeSpreadSheet(final String weekday) {

		// Holt die Hauptliste aus der Klasse. (Hier stehen alle Klassen der
		// Lehrer drin)
		mainList = GUIHandler.getDayTable(weekday);

		// Die Spalten des Spreadsheets muessen immer auf die Groesse der Liste,
		// die die Liste der Lehrer enthaelt,
		// +1 gesetzt werden. (Da die erste Spalte leer ist bzw.
		// Lehrer: enthaelt)
		spreadColumn = mainList.size() + 1;

		// Die Anzahl der Zeilen muss immer auf das Zeitraster gessetzt werden,
		// dass sich aus Start und Endzeit, sowie Granularitaet ergibt.
		spreadRow = timeslots.size();

		// Erzeugt das Grid indem am Ende die Werte stehen.
		grid = new GridBase(spreadRow, spreadColumn);

		// Eine ObservableList, die die Zeilen des Tagesplans innehaelt.
		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections
				.observableArrayList();

		// Setzt den row Index auf 0 damit zunaechst die Zeile mit dem Index 0
		// belegt wird. Dieser wird erhoet im Laufe der Initialisierung.
		int rowIndex = 0;

		// Fuegt die erste Zeile mit allen Lehrern hinzu. Wird besondert
		// behandelt, da vorne noch der String Lehrer steht..
		rows.add(getTeachers(grid, rowIndex++));

		// Fuegt die weiteren Zeilen fuer die Lehrer hinzu (mitsamt den Klassen,
		// wo sie sich befinden).
		for (int i = 0; i < timeslots.size(); i++) {
			rows.add(getRowForTimeFrame(grid, rowIndex++, timeslots.get(i)));
		}

		// Fuegt alle zuvor erstellten Zeilen dem Grid hinzu.
		grid.setRows(rows);

		// Setzt das Grid des Spreadsheets auf unser Grid, somit ist die
		// Initialisierunge abgeschlossen.
		spreadsheet.setGrid(grid);
	}

	/**
	 * Gibt die obere Zeile mit allen Lehrern zurueck.
	 * 
	 * @param grid
	 *            Das Grid des Spreadsheets.
	 * @param row
	 *            Die Zeile. In diesem Fall immer 0.
	 * @return Eine ObservableList, die die Zeile der Lehrer repraesentiert.
	 */
	private ObservableList<SpreadsheetCell> getTeachers(GridBase grid, int row) {
		final ObservableList<SpreadsheetCell> teachers = FXCollections
				.observableArrayList();
		SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, 0, 1,
				1, "Lehrer: ");
		cell.setEditable(false);
		teachers.add(cell);
		for (int column = 0; column < grid.getColumnCount() - 1; ++column) {
			// Holt sich immer den 0.ten Wert, da dort die Lehrer stehen.
			cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
					mainList.get(column).get(0));
			cell.setEditable(false);
			teachers.add(cell);
		}
		return teachers;
	}

	/**
	 * Gibt eine Zeile fuer einen Teil des Timeslotrasters zurueck. Bedeutet in
	 * welchen Klassen sich die Lehrer zu einer gegebenen Granularitaet
	 * befinden.
	 * 
	 * Dabei muss immer der jeweilige Tag beachtet werden.
	 */
	private ObservableList<SpreadsheetCell> getRowForTimeFrame(
			final GridBase grid, final int row, final String time) {

		final ObservableList<SpreadsheetCell> timeslotRow = FXCollections
				.observableArrayList();

		// Erste Zelle die, die Zeit anzeigt.
		SpreadsheetCell cellwithTime = SpreadsheetCellType.STRING.createCell(
				row, 0, 1, 1, time);

		cellwithTime.setEditable(false);
		timeslotRow.add(cellwithTime);

		if (row <= 0) {
			throw new IllegalArgumentException(
					"Die Zeile mit den Lehrern wurde nicht richtig gesetzt.");
		} else {
			// FOR-Schleife um Klassen zu den dazugehoerigen Timeslots zu
			// setzen.
			for (int column = 1; column < grid.getColumnCount(); ++column) {
				cellwithTime = SpreadsheetCellType.STRING.createCell(row,
						column, 1, 1, mainList.get(column - 1).get(row));
				cellwithTime.setEditable(false);
				timeslotRow.add(cellwithTime);
			}
		}
		return timeslotRow;
	}

	/**
	 * Button, der es moeglich macht, zum Hauptmenue zurueckzukehren.
	 */
	@FXML
	private void btnRootLayout() {
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

	// Dateien exportieren...

	/**
	 * Button, der eine PDF erzeugt.
	 * 
	 * @throws COSVisitorException
	 * @throws IOException
	 */
	@FXML
	private void bPdf() throws COSVisitorException, IOException {
		generateScreen(spreadsheet);
		String dirName = "PDF";
		File dir = new File(path + dirName);
		dir.mkdir();
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A1);
		document.addPage(page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", node.getId() + (".png"), dir
				+ File.separator + "Tagesplan" + (".pdf"));
		File f1 = new File(node.getId() + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
	}

	/**
	 * Button, der eine CSV erzeugt.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bCsv() throws IOException {
		String dirName = "CSV";
		File dir = new File(path + dirName);
		dir.mkdir();
		String csv = "Tagesplan.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(dir + File.separator
				+ csv));
		List<String[]> data = new ArrayList<String[]>();

		for (int j = 1; j < spreadColumn; j++) {
			for (int i = 0; i < spreadRow; i++) {
				data.add(new String[] {
						spreadsheet.getGrid().getRows().get(i).get(0)
								.textProperty().get(),
						spreadsheet.getGrid().getRows().get(i).get(j)
								.textProperty().get() });
			}
			data.add(new String[] { "" });
			data.add(new String[] { "" });
		}
		writer.writeAll(data);
		writer.close();
	}

	/**
	 * Button, der eine TXT erzeugt.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bTxt() throws IOException {
		String dirName = "TXT";
		File dir = new File(path + dirName);
		dir.mkdir();
		BufferedWriter out = new BufferedWriter(new FileWriter(dir
				+ File.separator + "Tagesplan.txt"));
		out.newLine();
		for (int j = 1; j < spreadColumn; j++) {
			for (int i = 0; i < spreadRow; i++) {
				out.write(spreadsheet.getGrid().getRows().get(i).get(0)
						.textProperty().get());
				out.write("     ");
				out.write(spreadsheet.getGrid().getRows().get(i).get(j)
						.textProperty().get());
				out.newLine();

			}
			out.newLine();
			out.newLine();
			out.newLine();
		}
		out.close();
	}

	/*
	 * Erstellt eine PDF aus einer PNG und einer Ausgangsdatei im PDF Format,
	 */
	private void createPDFFromImage(String inputFile, String image,
			String outputFile) throws IOException, COSVisitorException {
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
			contentStream.drawImage(ximage, 20, 20);
			contentStream.close();
			doc.save(outputFile);
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	/*
	 * Erstellt einen Screenshot von der jeweiligen Node
	 */
	private void generateScreen(Node pNode) {
		node = pNode;
		WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
		File file = new File(node.getId() + (".png"));
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}