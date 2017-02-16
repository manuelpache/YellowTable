package Yellowstone.YellowTable.logic.handler.gui_handler;

import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;

import java.util.Collection;
import java.util.HashSet;

import Yellowstone.YellowTable.GUI.MainApp;
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
 * Editiert oder setzt einen Wert im Datenbestand. Kann nicht rueckgaengig
 * gemacht werden!
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * BACKUP, CONFIG, BREAK, MEETING, BUILDING, STYLE
 * 
 * @author apag
 *
 */
public class EditDefault extends Command {

	/**
	 * Die Art des Objekts
	 */
	private final String type;

	/**
	 * Objekt mit neuen Infos
	 */
	private final Object newInfo;

	public EditDefault(final String[] pArgs) {
		if (pArgs == null || pArgs.length < MIN_SIZE) {
			throw new IllegalArgumentException(
					"Nicht genuegend oder keine Informationen gegeben zum Editieren.");
		}
		type = pArgs[0];
		try {
			switch (type) {
			case BACKUP:
				// (Timer, Title, OnClose)
				if (pArgs.length <= BACKUP_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Speichern der Backup-Einstellungen.");
				}
				try {
					/*
					 * Ueberpruefung des Timer-Werts
					 */
					int timer = Integer.parseInt(pArgs[BAC_TIMER]);
					if (timer < 0) {
						throw new NeedUserCorrectionException(
								"Timer-Wert kann nicht unter null liegen ("
										+ timer + ")");
					}
					/*
					 * Ueberpruefung des Wahrheitswertes (Backup beim
					 * Schliessen)
					 */
					toBool(pArgs[BAC_ONCLOSE]); // koennte Exception werfen

					String[] backup = { pArgs[BAC_TIMER], pArgs[BAC_TITLE],
							pArgs[BAC_ONCLOSE] };
					newInfo = backup;
				} catch (final NumberFormatException b) {
					throw new IllegalArgumentException(
							"EditDefault (BACKUP): Zahl erwartet als Timer ("
									+ pArgs[BAC_TIMER] + ")");
				}
				break;
			case CONFIG:
				if (pArgs.length <= CONFIG_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Speichern der Basis-Konfiguration.");
				}

				newInfo = initConfig(pArgs);
				break;
			case BREAK:
				if (pArgs.length <= BREAK_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Pause.");
				}

				if (DBHandler.getUnitByID(Break.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID])) == null) {
					throw new IllegalArgumentException("Pause nicht gefunden.");
				}

				Break bu = initBreak(pArgs);
				bu.setId(Integer.parseInt(pArgs[UNIT_ID]));

				newInfo = bu;
				break;
			case MEETING:
				if (pArgs.length <= MEETING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Jahrgangssitzung.");
				}

				if (DBHandler.getUnitByID(MeetingUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID])) == null) {
					throw new IllegalArgumentException(
							"Treffen nicht gefunden.");
				}

				MeetingUnit mu = initMeeting(pArgs);
				mu.setId(Integer.parseInt(pArgs[UNIT_ID]));

				newInfo = mu;
				break;
			case BUILDING:
				if (pArgs.length <= BUILDING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren eines Standorts.");
				}
				if (!DBHandler.getAllBuildings().contains(pArgs[BUILD_NAME])) {
					throw new IllegalArgumentException(
							"EditDefault (BUILDING): Ein solches Gebaeude existiert nicht.");
				}
				// (Name, Distance)
				try {
					/*
					 * Ueberpruefung der Distanz
					 */
					int dist = Integer.parseInt(pArgs[BUILD_DIST]);
					if (dist < 0) {
						throw new IllegalArgumentException(
								"EditDefault (BUILDING): Distanzen koennen nicht unter null liegen ("
										+ pArgs[BUILD_DIST] + ")");
					}
					/*
					 * Ueberpruefung des Namens
					 */
					if (pArgs[BUILD_NAME].trim().length() <= 0) {
						throw new IllegalArgumentException(
								"EditDefault (BUILDING): Der Name sollte nicht leer sein ("
										+ pArgs[BUILD_NAME] + ")");
					}

					String[] building = { pArgs[BUILD_NAME], pArgs[BUILD_DIST] };
					newInfo = building;
					break;
				} catch (final NumberFormatException n) {
					throw new IllegalArgumentException(
							"EditDefault (BUILDING): Zahl erwartet als Distanz ("
									+ pArgs[BUILD_DIST] + ")");
				}
			default:
				throw new IllegalArgumentException(
						"EditDefault: Unbekannter Typ " + pArgs[0]);
			}
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException("EditDefault (" + type
					+ "): Arraygrenzen ueberschritten: " + a.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("EditDefault (" + type
					+ "): NULL wird nicht gerne gesehen... " + np.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Bearbeitet die aktuelle Config entsprechend der Informationen und gibt
	 * diese zurueck. Ist keine Config vorhanden, fliegt eine
	 * {@link NullPointerException}. Dieser Fall sollte nicht eintreten, da die
	 * Config in {@link MainApp#main(String[])} gesetzt wird.
	 * 
	 * Es wird davon ausgegangen, dass das gegebene Array laenger als
	 * {@linkplain #CONFIG_MIN} ist.
	 * 
	 * @param pArgs
	 *            das vollstaendige String-Array mit allen Informationen
	 *            einschliesslich der Art des Objektes.
	 * @return die aus den Informationen erstellte Konfiguration
	 */
	private Config initConfig(final String[] pArgs) {
		// (StartHour, StartMin, EndHour, EndMin, StdDuration, Granularity)
		try {
			Config co = DBHandler.getConfig();
			int durationOfDay = Timeslot.getDurationByStartAndEndtime(
					pArgs[CON_STARTHR], pArgs[CON_STARTMIN], pArgs[CON_ENDHR],
					pArgs[CON_ENDMIN]);
			Timeslot ts = new Timeslot(durationOfDay, WEEKDAYS.NONE,
					pArgs[CON_STARTHR], pArgs[CON_STARTMIN]);
			final int stdDuration = Integer.parseInt(pArgs[CON_STDDURATION]);
			final int granularity = Integer.parseInt(pArgs[CON_GRANULARITY]);

			Collection<WEEKDAYS> days = new HashSet<>();
			final int numOfDays = Integer.parseInt(pArgs[CON_NUMDAY]);
			for (int i = 1; i <= numOfDays; i++) {
				days.add(toWeekdays(pArgs[CON_NUMDAY + i]));
			}

			// co kann nicht null sein, da in main (initProject) gesetzt

			co.setPlannedDays(days);
			co.setTimeframe(ts);
			co.setStdDuration(stdDuration);
			co.setGranularity(granularity);

			return co;
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"AddDefault (CONFIG): Es werden Zahlen fuer die Standarddauer eines Stundeninhaltes ("
							+ pArgs[CON_STDDURATION]
							+ "), die Granularitaet ("
							+ pArgs[CON_GRANULARITY]
							+ "), die Startstunde und -minute ("
							+ pArgs[CON_STARTHR]
							+ ":"
							+ pArgs[CON_STARTMIN]
							+ ") sowie die Anzahl der verplanten Tage ("
							+ pArgs[CON_NUMDAY] + ") erwartet");
		} catch (final IllegalArgumentException i) {
			throw new NeedUserCorrectionException(i.getMessage());
		} catch (final NullPointerException np) {
			throw new IllegalArgumentException("NULL ist nie schoen... "
					+ np.getMessage());
		}
	}

	@Override
	public void execute() {
		try {
			switch (type) {
			case BACKUP:
				String[] backup = (String[]) newInfo;
				Config edited = DBHandler.getConfig();
				edited.setBackupTimer(Integer.parseInt(backup[BAC_TIMER - 1]));
				edited.setBackupTitle(backup[BAC_TITLE - 1]);
				edited.setBackupOnClose(toBool(backup[BAC_ONCLOSE - 1]));
				DBHandler.updateConfig(edited);
				break;
			case CONFIG:
				Config con = (Config) newInfo;
				// wenn neue Granularitaet groeber, koennte Darstellung
				// leiden...
				if (con.getGranularity() > DBHandler.getConfig()
						.getGranularity()) {
					// ...aber nur, wenn ueberhaupt schon etwas eingetragen
					// wurde
					if (DBHandler.getAllExternUnits().size() > 0
							|| DBHandler.getAllBreaks().size() > 0
							|| DBHandler.getAllMeetingUnits().size() > 0
							|| DBHandler.getAllTeachingUnits().size() > 0) {
						// eig. nur Hinweisdialog, da trotzdem persistiert wird
						DBHandler.updateConfig(con);
						throw new NeedUserCorrectionException(
								"Einstellungen übernommen. Das Ändern der Granularität kann Auswirkungen auf die Darstellung"
										+ " bereits erstellter Planungseinheiten haben.");
					}
				}

				if (DBHandler.checkConflictTimeFrameByTimeslot(con
						.getTimeframe())) {
					throw new NeedUserCorrectionException(
							"Es gibt Planungseinheiten außerhalb des neu gesetzten Zeitraums.");
				}
				if (DBHandler.checkConflictPlannedWeekdays()) {
					throw new NeedUserCorrectionException(
							"Es gibt Planungseinheiten, die nicht innerhalb der jetzt verplanten Wochentage liegen.");
				}

				DBHandler.updateConfig(con);
				break;
			case BREAK:
				Break bu = (Break) newInfo;

				DBHandler.updateBreak(bu);
				for (Class c : bu.getClasses()) {
					// theoretisch Doppelbelegung
					Warner.WARNER.warn(new MultipleReservationWarning("", bu
							.getTimeslot(), c));
				}
				break;
			case MEETING:
				MeetingUnit mu = (MeetingUnit) newInfo;

				DBHandler.updateMeetingUnit(mu);
				// DANACH pruefen
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
				String[] b = (String[]) newInfo;

				DBHandler.addBuilding(b[0], Integer.parseInt(b[1]));
				// Distanzen DANACH pruefen
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
							+ newInfo.getClass().getSimpleName());
		} catch (final Exception e) {
			System.out.println("AddDefault (Exec): Exception ->");
			e.printStackTrace();
		}
	}
}
