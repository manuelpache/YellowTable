package Yellowstone.YellowTable.logic.handler.gui_handler;

import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;
import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.warnings.DistanceWarning;
import Yellowstone.YellowTable.warnings.MultipleReservationWarning;
import Yellowstone.YellowTable.warnings.OverloadWarning;
import Yellowstone.YellowTable.warnings.Warner;

/**
 * Fuegt ein Objekt dem Datenbestand hinzu. Kann nicht rueckgaengig gemacht
 * werden!
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * BREAK, MEETING, BUILDING, CATEGORY
 * 
 * @author apag
 *
 */
public class AddDefault extends Command {

	/**
	 * Das Objekt/die Informationen, die hinzugefuegt werden sollen
	 */
	private final Object toAdd;

	/**
	 * Die Art des Objekts
	 */
	private final String type;

	/**
	 * Im Konstruktor wird sichergestellt, dass das Kommando ausgefuehrt werden
	 * kann.
	 * 
	 * @param pArgs
	 *            die zu interpretierenden Argumente
	 */
	AddDefault(final String[] pArgs) {
		if (pArgs == null || pArgs.length < MIN_SIZE) {
			throw new IllegalArgumentException(
					"Nicht genuegend oder keine Informationen gegeben zum Hinzufuegen.");
		}
		type = pArgs[0];
		try {
			switch (type) {
			case BREAK:
				if (pArgs.length <= BREAK_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer Pause.");
				}
				// "doppelte" Units ok (wegen id)
				toAdd = initBreak(pArgs);
				break;
			case MEETING:
				if (pArgs.length <= MEETING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer Jahrgangssitzung.");
				}
				// "doppelte" Units ok (wegen id)
				toAdd = initMeeting(pArgs);
				break;
			case BUILDING:
				if (pArgs.length <= BUILDING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen eines Standorts.");
				}
				if (DBHandler.getAllBuildings().contains(pArgs[BUILD_NAME])) {
					throw new NeedUserCorrectionException(
							"Ein solches Gebäude (" + pArgs[BUILD_NAME]
									+ ") existiert bereits.");
				}
				// (Name, Distance)
				try {
					/*
					 * Ueberpruefung der Distanz
					 */
					int dist = Integer.parseInt(pArgs[BUILD_DIST]);
					if (dist < 0) {
						throw new NeedUserCorrectionException(
								"Distanzen koennen nicht unter null liegen ("
										+ pArgs[BUILD_DIST] + ")");
					}
					/*
					 * Ueberpruefung des Namens
					 */
					if (pArgs[BUILD_NAME].trim().length() <= 0) {
						throw new NeedUserCorrectionException(
								"Der Name eines Gebäudes sollte nicht leer sein ("
										+ pArgs[BUILD_NAME] + ")");
					}

					String[] building = { pArgs[BUILD_NAME], pArgs[BUILD_DIST] };
					toAdd = building;
					break;
				} catch (final NumberFormatException n) {// Implementierung
					throw new IllegalArgumentException(
							"AddDefault (BUILDING): Zahl erwartet als Distanz ("
									+ pArgs[BUILD_DIST] + ")");
				}
			case CATEGORY:
				if (pArgs.length <= CAT_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen der Raumfunktion.");
				}
				if (DBHandler.getCategoryByName(pArgs[CAT_NAME]) != null) {
					throw new NeedUserCorrectionException(
							"Eine solche Raumfunktion (" + pArgs[CAT_NAME]
									+ ") existiert bereits.");
				}
				/*
				 * Ueberpruefung des Namens
				 */
				if (pArgs[CAT_NAME].trim().length() <= 0) {
					throw new NeedUserCorrectionException(
							"Der Name einer Raumfunktion sollte nicht leer sein ("
									+ pArgs[CAT_NAME] + ")");
				}
				toAdd = new Category(pArgs[CAT_NAME]);
				break;
			default:
				throw new IllegalArgumentException(
						"AddDefault: Unbekannter Typ " + pArgs[0]);
			}
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException("AddDefault (" + type
					+ "): Arraygrenzen ueberschritten: " + a.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("AddDefault (" + type
					+ "): NULL wird nicht gerne gesehen... " + np.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Fuehrt den Befehl aus.
	 */
	@Override
	public void execute() {
		try {
			switch (type) {
			case BREAK:
				Break bu = (Break) toAdd;

				DBHandler.addBreak(bu);
				// Ueberpruefung NACH Hinzufuegen
				for (Class c : bu.getClasses()) {
					// theoretisch Doppelbelegung
					Warner.WARNER.warn(new MultipleReservationWarning("", bu
							.getTimeslot(), c));
					for (WEEKDAYS w : bu.getDays()) {
						// Distanzen
						Warner.WARNER.warn(new DistanceWarning("", w, c));
					}
				}

				break;
			case MEETING:
				MeetingUnit mu = (MeetingUnit) toAdd;

				DBHandler.addMeetingUnit(mu);
				// Ueberpruefung NACH Hinzufuegen
				for (Person p : mu.getMembers()) {
					Warner.WARNER.warn(new OverloadWarning("", p));
					Warner.WARNER.warn(new MultipleReservationWarning("", mu
							.getTimeslot(), p));
					Warner.WARNER.warn(new DistanceWarning("", mu.getTimeslot()
							.getDay(), p));
				}
				for (Room r : mu.getRooms()) {
					Warner.WARNER.warn(new MultipleReservationWarning("", mu
							.getTimeslot(), r));
				}
				break;
			case BUILDING:
				String[] b = (String[]) toAdd;
				DBHandler.addBuilding(b[0], Integer.parseInt(b[1]));

				// Alle Distanzen pruefen...
				for (WEEKDAYS w : WEEKDAYS.values()) {
					for (Person p : DBHandler.getAllTeachers()) {
						Warner.WARNER.warn(new DistanceWarning("", w, p));
					}
					for (Person p : DBHandler.getAllEducEmployees()) {
						Warner.WARNER.warn(new DistanceWarning("", w, p));
					}
					for (Class c : DBHandler.getAllClasses()) {
						Warner.WARNER.warn(new DistanceWarning("", w, c));
					}
				}

				break;
			case CATEGORY:
				DBHandler.addCategory((Category) toAdd);
				break;
			default:
				throw new IllegalArgumentException(
						"AddDefault (Exec): unbekannter Typ (" + type + ")");
			}
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"AddDefault (Exec): fuer den Typ "
							+ type
							+ " wurde an einer Stelle eine Zahl erwartet, aber nicht gefunden: "
							+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException(
					"AddDefault (Exec): Die Arraygrenzen im Typ: " + type
							+ " wurden ueberschritten: " + a.getMessage());
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException(
					"AddDefault (Exec): Gespeicherter Typ und eig. Typ stimmen nicht ueberein. Erwarte "
							+ type
							+ " habe aber "
							+ toAdd.getClass().getSimpleName());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException(db.getMessage());
		} catch (final Exception e) {
			System.out.println("AddDefault (Exec): Exception ->");
			e.printStackTrace();
		}
	}

}
