package Yellowstone.YellowTable.logic.handler.gui_handler;

import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;
import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;

/**
 * Entfernt einen Wert ausc dem Datenbestand. Kann nicht rueckgaengig gemacht
 * werden!
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * BREAK, MEETING, BUILDING, CATEGORY
 * 
 * @author apag
 *
 */
public class RemoveDefault extends Command {

	/**
	 * Die Art des Objekts
	 */
	private final String type;

	/**
	 * Objekt, das entfernt wird.
	 */
	private final Object toRemove;

	/**
	 * Der Konstruktor erwartet ein String-Array, dessen erster Eintrag den
	 * Typen des zu entfernenden Objekts definiert. Nur wenn der Befehl
	 * fehlerfrei ausgefuehrt werden kann, wird er auch konstruiert.
	 * 
	 * @param pArgs
	 *            die Informationen zum Entfernen
	 */
	public RemoveDefault(final String[] pArgs) {
		try {
			if (pArgs == null || pArgs.length <= 1) {
				throw new IllegalArgumentException(
						"Nicht genuegend Informationen gegeben zum Entfernen.");
			}
			type = pArgs[0];
			switch (type) {
			case BREAK:
				Break remb = (Break) DBHandler.getUnitByID(
						Break.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));
				toRemove = remb;
				break;
			case MEETING:
				MeetingUnit remmeet = (MeetingUnit) DBHandler.getUnitByID(
						MeetingUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));
				toRemove = remmeet;
				break;
			case BUILDING:
				if (!DBHandler.getAllBuildings().contains(pArgs[BUILD_NAME])) {
					throw new NeedUserCorrectionException("Der Raum "
							+ pArgs[BUILD_NAME] + " existiert nicht.");
				}
				toRemove = pArgs[BUILD_NAME];
				break;
			case CATEGORY:
				toRemove = DBHandler.getCategoryByName(pArgs[CAT_NAME]);
				break;
			default:
				throw new IllegalArgumentException(
						"RemoveDefault: Unbekannter Typ: " + type);
			}

			if (toRemove == null) {
				throw new NeedUserCorrectionException(
						"Es existiert kein solches Objekt.");
			}
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Entfernen nicht möglich: "
					+ db.getMessage());
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException("Zahl erwartet: "
					+ n.getMessage());
		}
	}

	/**
	 * Entfernt das Objekt aus dem Datenbestand.
	 */
	@Override
	public void execute() {
		try {
			switch (type) {
			case BREAK:
				DBHandler.deleteBreak((Break) toRemove);
				break;
			case MEETING:
				DBHandler.deleteMeetingUnit((MeetingUnit) toRemove);
				break;
			case BUILDING:
				DBHandler.deleteBuilding((String) toRemove);
				break;
			case CATEGORY:
				DBHandler.deleteCategory((Category) toRemove);
				break;
			default:
				throw new IllegalArgumentException(
						"RemoveDefault (Exec): Unbekannter Typ: " + type);
			}
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Entfernen nicht möglich: "
					+ db.getMessage());
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException("RemoveDefault (Exec): Erwarte "
					+ type + " habe aber "
					+ toRemove.getClass().getSimpleName());
		}
	}
}
