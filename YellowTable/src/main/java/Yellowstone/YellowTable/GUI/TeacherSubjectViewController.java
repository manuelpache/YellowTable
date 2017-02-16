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
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * TeacherSubject-View-Controller. Dieser kontrolliert den View der eine
 * Uebersicht ueber die Leher-Fach-Verteilung gibt.
 * 
 * Der View verfuegt dabei ueber ein Spreadsheet, das alle Lehrer sowie ihre
 * Verteilung auf die Faecher und Klassen innehaelt.
 * 
 * Hier habe ich das Spreadsheet von JFxtras verwendet..
 * 
 * Fuer genaure Informationen siehe http://jfxtras.org
 * 
 * @author tomlewandowski, Erik 
 *
 */
public class TeacherSubjectViewController {

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
	 * Die Anzahl der Reihen des Spreadsheets
	 */
	private int spreadRow;

	/**
	 * Die Anzahl der Zeilen des Spreadsheets
	 */
	private int spreadColumn;

	/**
	 * Eine Liste, die die Lehrer-Fachverteilung repraesentiert. Die Daten
	 * werden im weiteren Verlauf aus dem GUI Handler geholt.
	 */
	private List<List<String>> mainList;

	/**
	 * Die DialogStage dieser Klasse. Diese wird benoetigt, wenn der View
	 * aufgerufen wird.
	 */
	private Stage dialogStage;

	/**
	 * Node fuer den PDF Export
	 */
	Node node;

	/**
	 * Fuer den Export wichtig.
	 */
	private String path = (System.getProperty("user.dir")) + File.separator;

	/**
	 * Methode, die den View beim Aufruf initialisiert.
	 */
	public void initialize() {

		/**
		 * Elemente aus dem dem GUI-Handler holen.
		 */
		mainList = GUIHandler.getTeacherDistribution();

		// Setzt die Spalten des Spreadsheets auf die Anzahl der Faecher (+4, da
		// die ersten Spalten fuer Soll-,Ist-Stunden, Minusstunden etc. sind)
		// Diese bekommen wir durch die size von einer der inneren Listen.
		spreadColumn = mainList.get(0).size();

		// Die Anzahl der Zeilen. Diese muss auf die Anzahl der Lehrer gesetzt
		// werden. Wir bekommen sie durch die Size der Hauptliste, da diese alle
		// Listen von den Lehrern plus den Header enthaelt.
		spreadRow = mainList.size();

		// Initialisiert das Spreadsheet...
		this.initializeSpreadSheet();
	}

	/**
	 * Initialisiert das Spreadsheet mit den gewuenschten Werten aus der Logik.
	 * 
	 * @param pRow
	 *            Die Anzahl der Reihen des Spreadsheets (Sind von der Anzahl
	 *            der Lehrer abhaengig).
	 * @param pColumn
	 *            Die Anzahl der Spalten des Spreadsheets (Abhaengig von der
	 *            Anzahl der Faecher)
	 */
	private void initializeSpreadSheet() {

		// Erzeugt das Grid indem am Ende die Werte stehen.
		grid = new GridBase(spreadRow, spreadColumn);

		// Eine ObservableList, die die Zeilen des Tagesplans innehaelt.
		ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections
				.observableArrayList();

		// Fuellt die einzelnen Zeilen nacheinander mit Werten aus dem
		// GUIHandler.
		for (int row = 0; row < spreadRow; row++) {
			// Eine ObservableList, die genau eine Zeile repraesentiert.
			final ObservableList<SpreadsheetCell> rowForTeacher = FXCollections
					.observableArrayList();
			for (int column = 0; column < spreadColumn; column++) {
				SpreadsheetCell cell1 = SpreadsheetCellType.STRING.createCell(
						row, column, 1, 1, this.mainList.get(row).get(column));
				cell1.setEditable(false);
				rowForTeacher.add(cell1);
			}
			rows.add(rowForTeacher);
		}

		// Fuegt alle zuvor erstellten Zeilen dem Grid hinzu.
		grid.setRows(rows);

		// Setzt das Grid des Spreadsheets auf das zuvor erstellte Grid
		spreadsheet.setGrid(grid);

	}

	/**
	 * Button der zurück zum Hauptmenü führt.
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

	/*
	 * Datei-Export von Erik
	 */

	/**
	 * Button, der das Exportieren einer TXT repraesentiert.
	 * 
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	@FXML
	private void bTxt() throws IOException, COSVisitorException {
		String dirName = "TXT";
		File dir = new File(path + dirName);
		dir.mkdir();
		BufferedWriter out = new BufferedWriter(new FileWriter(dir
				+ File.separator + "LehrerFachVerteilung.txt"));
		out.newLine();
		for (int j = 1; j < spreadRow; j++) {
			for (int i = 0; i < spreadColumn; i++) {
				if (!(spreadsheet.getGrid().getRows().get(j).get(i)
						.textProperty().get().isEmpty())) {
					out.write(spreadsheet.getGrid().getRows().get(0).get(i)
							.textProperty().get());
					out.write("     ");
					out.write(spreadsheet.getGrid().getRows().get(j).get(i)
							.textProperty().get());
					out.newLine();
				}
			}
			out.newLine();
		}
		out.close();
	}

	/**
	 * Button der das Exportieren einer CSV repraesentiert.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void bCsv() throws IOException {
		String dirName = "CSV";
		File dir = new File(path + dirName);
		dir.mkdir();
		String csv = "LehrerFachVerteilung.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(dir + File.separator
				+ csv));
		List<String[]> data = new ArrayList<String[]>();

		for (int j = 1; j < spreadRow; j++) {
			for (int i = 0; i < spreadColumn; i++) {
				data.add(new String[] {
						spreadsheet.getGrid().getRows().get(0).get(i)
								.textProperty().get(),
						spreadsheet.getGrid().getRows().get(j).get(i)
								.textProperty().get() });
			}
			data.add(new String[] { "" });
			data.add(new String[] { "" });
		}
		writer.writeAll(data);
		writer.close();
	}

	/**
	 * Button, der das exportieren einer PDF realisiert.
	 * 
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	@FXML
	private void bPdf() throws IOException, COSVisitorException {
		String dirName = "PDF";
		File dir = new File(path + dirName);
		dir.mkdir();
		generateScreen(spreadsheet);
		PDDocument document = new PDDocument();
		PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A3);
		document.addPage(page1);
		// PDPageContentStream cos = new PDPageContentStream(document, page1);
		document.save("rsc.pdf");
		document.close();
		createPDFFromImage("rsc.pdf", "Lehrer-Fach-Verteilung" + (".png"), dir
				+ File.separator + "Lehrer-Fach-Verteilung" + (".pdf"));
		File f1 = new File("Lehrer-Fach-Verteilung" + (".png"));
		File f2 = new File("rsc.pdf");
		f1.delete();
		f2.delete();
		// String b = (System.getProperty("user.dir"));
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
			contentStream.drawImage(ximage, 20, 500);
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
		File file = new File("Lehrer-Fach-Verteilung" + (".png"));
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}