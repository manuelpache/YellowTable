package Yellowstone.YellowTable.logic.handler.gui_handler;

/**
 * Abstrakte Oberklasse aller Commands, die rueckgaengig gemacht werden koennen.
 * Nur Subklassen dieser Klasse koennen von der CommandHistory verwaltet werden.
 * 
 * @author apag
 */
public abstract class UndoRedoCommand extends Command {

	/**
	 * Macht diesen Befehl wieder rueckgaengig.
	 */
	public abstract void undo();

	/**
	 * Fuehrt diesen Befehl erneut aus.
	 */
	public abstract void redo();
}
