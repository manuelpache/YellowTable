package Yellowstone.YellowTable.logic.handler.gui_handler;

import java.util.Stack;

import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;

/**
 * Diese Klasse speichert alle ausgeführten Commands in einer Liste und führt
 * diese aus. Architekturmuster: Command
 * 
 * @author prohleder, apag, Le
 */
public class CommandHistory {

	// Liste der ausgeführten Commands
	private Stack<Command> listOfCommands = new Stack<Command>();

	// Speichert die Commands ab, die rückgängig gemacht werden
	private Stack<Command> redoCommands = new Stack<Command>();

	/**
	 * Maximale Zahl zu speichernder Befehle.
	 */
	public final static int SIZE = 10;

	/**
	 * Bekommt einen Befehl gegeben, haengt ihn entsprechend an die Liste und
	 * fuehrt ihn aus. Ist die History voll, fliegt der aelteste Befehl raus.
	 * 
	 * @param pCommand
	 *            der auszufuehrende Befehl
	 */
	public void execute(final UndoRedoCommand pCommand) {
		try {
			pCommand.execute();
			listOfCommands.push(pCommand);
			redoCommands.clear();
			if (listOfCommands.size() > SIZE) {
				listOfCommands.remove(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof NeedUserCorrectionException) {
				throw e;
			}
		}
	}

	/**
	 * Macht einen Befehl rueckgaengig.
	 */
	public void undo() {
		if (!listOfCommands.isEmpty()) {
			UndoRedoCommand undoCmd = (UndoRedoCommand) listOfCommands.pop();
			undoCmd.undo();
			redoCommands.push(undoCmd);
		}
	}

	/**
	 * Stellt den letzten Befehl wieder her.
	 */
	public void redo() {
		if (!redoCommands.isEmpty()) {
			UndoRedoCommand redoCmd = (UndoRedoCommand) redoCommands.pop();
			redoCmd.redo();
			listOfCommands.push(redoCmd);
		}
	}

	/**
	 * Setzt die ComandHistory zurueck. Dies soll verhindern, dass Undos und
	 * Redos in andere Views uebernommen werden koennen.
	 */
	public void reset() {
		listOfCommands.clear();
		redoCommands.clear();
	}
}
