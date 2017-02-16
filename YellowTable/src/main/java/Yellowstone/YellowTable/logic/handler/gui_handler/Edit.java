package Yellowstone.YellowTable.logic.handler.gui_handler;

import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;

import java.util.HashSet;
import java.util.Set;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.warnings.DistanceWarning;
import Yellowstone.YellowTable.warnings.MultipleReservationWarning;
import Yellowstone.YellowTable.warnings.ConflictWishesWarning;
import Yellowstone.YellowTable.warnings.OverloadWarning;
import Yellowstone.YellowTable.warnings.Warner;

/**
 * Editiert ein Objekt im Datenbestand.
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * ROOM, SUBJECT, TEACHER, EDUCEMPLOYEE, YEARINFO, CLASS, EXTERN, TEACHING,
 * TEACHPERS, CLASSINFO
 * 
 * @author apag
 *
 */
public class Edit extends UndoRedoCommand {

	/**
	 * Das neue Objekt/die neuen Informationen
	 */
	private final Object newInfo;

	/**
	 * Das alte Objekt/die alten Informationen
	 */
	private final Object oldInfo;

	/**
	 * Die Art des Objekts
	 */
	private final String type;

	/**
	 * Der Konstruktor stellt sicher, dass der Befehl auch ausfuehrbar ist.
	 * 
	 * @param pArgs
	 *            die Informationen des zu aendernden Objekts
	 */
	Edit(final String[] pArgs) {
		if (pArgs == null || pArgs.length < MIN_SIZE) {
			throw new IllegalArgumentException(
					"Nicht genuegend oder keine Informationen gegeben zum Editieren.");
		}

		type = pArgs[0];
		try {
			switch (type) {
			case ROOM:
				if (pArgs.length <= ROOM_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren eines Raums.");
				}
				Room oldr = DBHandler.getRoomByTitle(pArgs[ROOM_TITLE]);
				if (oldr == null) {
					throw new NeedUserCorrectionException(
							"Kein solcher Raum vorhanden. Haben Sie versucht den Namen zu ändern?");
				}
				oldInfo = oldr.clone();
				newInfo = initRoom(pArgs);
				break;
			case SUBJECT:
				if (pArgs.length <= SUBJECT_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren eines Stundeninhaltes.");
				}
				Subject olds = DBHandler.getSubjectByName(pArgs[SUB_TITLE]);
				if (olds == null) {
					throw new NeedUserCorrectionException(
							"Kein solcher Planungsinhalt vorhanden. Haben Sie versucht den Namen zu ändern?");
				}
				oldInfo = olds.clone();
				newInfo = initSubject(pArgs);
				break;
			case TEACHER:
				if (pArgs.length <= TEACHER_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Lehrerin.");
				}
				Teacher oldt = DBHandler.getTeacherByShort(pArgs[PERS_SHORT]);
				if(oldt == null) {
					throw new NeedUserCorrectionException(
							"Kein solcher Lehrer vorhanden. Haben Sie versucht das Kürzel zu ändern?");
				}
				oldInfo = oldt.clone();
				newInfo = initTeacher(pArgs);
				break;
			case EDUC:
				if (pArgs.length <= EDUC_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren eines paedagogischen Mitarbeiters.");
				}
				EducEmployee olde = DBHandler.getEducEmployeeByShort(pArgs[PERS_SHORT]);
				if(olde == null) {
					throw new NeedUserCorrectionException(
							"Kein solcher päd. Mitaerbeiter 0vorhanden. Haben Sie versucht das Kürzel zu ändern?");
				}
				oldInfo = olde
						.clone();
				newInfo = initEduc(pArgs);
				break;
			case YEARINFO:
				if (pArgs.length <= YEARINFO_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Wochenstundenzahl fuer einen Stundeninhalt und Jahrgang.");
				}

				String[] newYear = initYearInfo(pArgs);

				Subject yearsub = DBHandler.getSubjectByName(pArgs[YEAR_SUB]);
				if (yearsub == null) {
					throw new IllegalArgumentException(
							"Es existiert kein solcher Stundeninhalt: "
									+ pArgs[YEAR_SUB]);
				}

				// pArgs-Kopie editieren um Funktion initYearInfo
				// wiederzuverwenden
				String[] oldYear = pArgs.clone();
				oldYear[YEAR_WORK] = String.valueOf(DBHandler
						.getIntendedWorkloadPerSubjectPerYear(yearsub,
								Integer.parseInt(pArgs[YEAR_YEAR])));

				// oldYear wirklich setzen
				oldYear = initYearInfo(oldYear);

				oldInfo = oldYear;
				newInfo = newYear;
				break;
			case CLASS:
				if (pArgs.length <= CLASS_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Klasse.");
				}

				Class cla = DBHandler.getClassByName(pArgs[CLASS_YEAR]
						+ pArgs[CLASS_TITLE]);
				if (cla == null) {
					throw new NeedUserCorrectionException("Eine Klasse "
							+ pArgs[CLASS_YEAR] + pArgs[CLASS_TITLE]
							+ " existiert nicht. Vielleicht haben Sie"
							+ " versucht den Jahrgang oder die "
							+ "Bezeichnung zu editieren?");
				}
				oldInfo = cla.clone();
				newInfo = initClass(pArgs);
				break;
			case EXTERN:
				if (pArgs.length <= EXTERN_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einee externen Planungseinheit.");
				}

				ExternUnit oldex = (ExternUnit) DBHandler.getUnitByID(
						ExternUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));

				// Kopie speichern, Original wird von der Datenbank aktualisiert
				oldInfo = oldex.clone();

				ExternUnit ex = initExtern(pArgs);
				ex.setId(Integer.parseInt(pArgs[UNIT_ID]));
				newInfo = ex;
				break;
			case TEACHING:
				if (pArgs.length <= TEACHING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren einer Unterrichtseinheit.");
				}

				TeachingUnit oldtea = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));

				// Kopie speichern, Original wird von der Datenbank aktualisiert
				oldInfo = oldtea.clone();
				TeachingUnit tu = initTeaching(pArgs);

				Set<PersonTime> pt = tu.getPersons();

				// Falls mehr als ein Lehrer bleibt, sollen indivviduelle Zeiten
				// bleiben, ausser der Timeslot wurde insgesamt veraendert.
				if (pt.size() > 1
						&& oldtea.getTimeslot().equals(tu.getTimeslot())) {
					// alle entfernen, die bereits vorhanden waren
					pt.removeIf(x -> oldtea.containsPerson(x.getPerson()
							.getShortName()));
					// und um die alten Infos ueber Timeslots ergaenzen
					pt.addAll(oldtea.getPersons());
					tu.setPersonTime(pt);
				}
				tu.setId(Integer.parseInt(pArgs[UNIT_ID]));

				DBHandler.updateTeachingUnit(tu);
				for (PersonTime p : tu.getPersons()) {
					DistanceWarning dist = new DistanceWarning("", tu
							.getTimeslot().getDay(), p.getPerson());
					if (dist.check()) {
						DBHandler.updateTeachingUnit((TeachingUnit) oldInfo);
						throw new NeedUserCorrectionException(
								"Es ist ein Gebäudewechsel für die Person "
										+ p.getPerson().getShortName()
										+ "\n notwendig, dafür muss Zeit eingeplant werden.");
					}
				}
				for (Class c : tu.getClasses()) {
					DistanceWarning dist = new DistanceWarning("", tu
							.getTimeslot().getDay(), c);
					if (dist.check()) {
						DBHandler.updateTeachingUnit((TeachingUnit) oldInfo);
						throw new NeedUserCorrectionException(
								"Es ist ein Gebäudewechsel für die Klasse "
										+ c.getName()
										+ "\n notwendig, dafür muss Zeit eingeplant werden.");
					}
				}
				DBHandler.updateTeachingUnit((TeachingUnit) oldInfo);

				newInfo = tu;
				break;
			case TEACHPERS:
				if (pArgs.length <= TP_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum"
									+ " Editieren einer PersonTime.");
				}

				TeachingUnit tea = ((TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[TP_UID])));
				if (tea == null) {
					throw new IllegalArgumentException(
							"Diese TeachingUnit existiert nicht: "
									+ pArgs[TP_UID]);
				}

				Object[] oldPt = { Integer.parseInt(pArgs[TP_UID]),
						new HashSet<PersonTime>(tea.getPersons()) };
				oldInfo = oldPt;
				Object[] newPt = { Integer.parseInt(pArgs[TP_UID]),
						initPersonTime(pArgs) };
				newInfo = newPt;

				break;
			case CLASSINFO:
				if (pArgs.length <= CLAINF_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Editieren eines individuellen Klassenbedarfs.");
				}
				String[] oldclass = pArgs.clone();
				Class c = DBHandler.getClassByName(pArgs[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(pArgs[CLASSINFO_SUB]);
				if (c == null || s == null) {
					throw new NeedUserCorrectionException("Die Klasse ("
							+ pArgs[CLASSINFO_CLASS]
							+ ") oder der Stundeninhalt ("
							+ pArgs[CLASSINFO_SUB] + ") existiert nicht.");
				}
				int hours = c.getHoursPerSubject(s);
				if (hours < 0) {
					throw new NeedUserCorrectionException(
							"Ein solcher Eintrag existiert noch nicht: "
									+ pArgs[CLASSINFO_SUB]);
				}
				try {
					Integer.parseInt(pArgs[CLASSINFO_WORK]);
				} catch (final NumberFormatException n) {
					throw new NeedUserCorrectionException(
							"Zahl erwartet als neuer Bedarf: "
									+ pArgs[CLASSINFO_WORK]);
				}
				oldclass[CLASSINFO_WORK] = String.valueOf(hours);
				oldInfo = oldclass;
				newInfo = pArgs.clone();
				break;
			default:
				throw new IllegalArgumentException("Edit: Unbekannter Typ: "
						+ type);
			}

			if (oldInfo == null) {
				throw new NeedUserCorrectionException(
						"Es existiert kein passendes Objekt, das verändert werden könnte.");
			}

		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException("Edit (" + type
					+ "): Arraygrenzen ueberschritten: " + a.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("Edit (" + type
					+ "): NULL wird nicht gerne gesehen... " + np.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Editieren nicht möglich: "
					+ db.getMessage());
		}
	}

	/**
	 * Macht diesen Befehl rueckgaengig und stellt somit den alten
	 * Datenbankzustand wieder her.
	 */
	public void undo() {
		try {
			switch (type) {
			case ROOM:
				DBHandler.updateRoom((Room) oldInfo);
				// Distanzpruefungen fuer alle...
				for (WEEKDAYS w : DBHandler.getConfig().getPlannedDays()) {
					for (Teacher t : DBHandler.getAllTeachers()) {
						Warner.WARNER.warn(new DistanceWarning("", w, t));
					}
					for (EducEmployee e : DBHandler.getAllEducEmployees()) {
						Warner.WARNER.warn(new DistanceWarning("", w, e));
					}
					for (Class c : DBHandler.getAllClasses()) {
						Warner.WARNER.warn(new DistanceWarning("", w, c));
					}
				}
				break;
			case SUBJECT:
				DBHandler.updateSubject((Subject) oldInfo);
				break;
			case TEACHER:
				DBHandler.updateTeacher((Teacher) oldInfo);
				// Falls workload geaendert
				Warner.WARNER.warn(new OverloadWarning("", (Teacher) newInfo));
				break;
			case EDUC:
				DBHandler.updateEducEmployee((EducEmployee) oldInfo);
				// Falls workload geaendert
				Warner.WARNER.warn(new OverloadWarning("",
						(EducEmployee) newInfo));
				break;
			case YEARINFO:
				String[] unEditedYear = (String[]) oldInfo;
				Subject sub = DBHandler
						.getSubjectByName(unEditedYear[YEAR_SUB - 1]);
				int year = Integer.parseInt(unEditedYear[YEAR_YEAR - 1]);
				DBHandler.updateHoursPerSubjectYear(sub, year,
						Integer.parseInt(unEditedYear[YEAR_WORK - 1]));
				for (Class c : DBHandler.getClassesByYear(year)) {
					Warner.WARNER.warn(new OverloadWarning(sub, c));
				}
				break;
			case CLASS:
				DBHandler.updateClass((Class) oldInfo);
				break;
			case EXTERN:
				ExternUnit ex = (ExternUnit) oldInfo;
				DBHandler.updateExternUnit(ex);
				for (Class c : ex.getClasses()) {
					Warner.WARNER.warn(new MultipleReservationWarning("", ex
							.getTimeslot(), c));
				}
				for (Class c : ex.getClasses()) {
					Warner.WARNER.warn(new OverloadWarning("", c));
					Warner.WARNER.warn(new MultipleReservationWarning("", ex
							.getTimeslot(), c));
				}
				break;
			case TEACHING:
				TeachingUnit tu = (TeachingUnit) oldInfo;
				DBHandler.updateTeachingUnit(tu);
				tu = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(), tu.getId());
				for (Class c : tu.getClasses()) {
					for (Subject subj : tu.getSubjects()) {
						Warner.WARNER.warn(new OverloadWarning(subj, c));
					}
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), c));
					Warner.WARNER.warn(new DistanceWarning("", tu.getTimeslot()
							.getDay(), c));
				}

				for (PersonTime pt : tu.getPersons()) {
					Warner.WARNER.warn(new OverloadWarning("", pt.getPerson()));
					Warner.WARNER.warn(new MultipleReservationWarning("", pt
							.getTimeslot(), pt.getPerson()));
					Warner.WARNER.warn(new DistanceWarning("", tu.getTimeslot()
							.getDay(), pt.getPerson()));
					if (pt.getPerson() instanceof Teacher) {
						Teacher t = (Teacher) pt.getPerson();
						Warner.WARNER.warn(new ConflictWishesWarning(
								tu.getId(), t, pt.getTimeslot()));
					}
				}
				for (Room r : tu.getRooms()) {
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), r));
				}
				break;
			case TEACHPERS:
				Object[] oldPt = (Object[]) oldInfo;
				TeachingUnit tea = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(), (int) oldPt[0]);
				PersonTime pt = (PersonTime) oldPt[1];
				Set<PersonTime> spt = tea.getPersons();
				spt.removeIf(x -> x.getPerson().getShortName()
						.equals(pt.getPerson().getShortName()));
				tea.setPersonTime(spt);
				DBHandler.updateTeachingUnit(tea);
				Warner.WARNER.warn(new MultipleReservationWarning("", tea
						.getTimeslot(), pt.getPerson()));
				Warner.WARNER.warn(new OverloadWarning("", pt.getPerson()));
				break;
			case CLASSINFO:
				String[] oldclass = (String[]) oldInfo;
				Class c = DBHandler.getClassByName(oldclass[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(oldclass[CLASSINFO_SUB]);
				int hours = Integer.parseInt(oldclass[CLASSINFO_WORK]);
				c.setHours(s, hours);
				DBHandler.updateClass(c);
				Warner.WARNER.warn(new OverloadWarning(s, c));
				break;
			default:
				throw new IllegalArgumentException(
						"Edit(Undo): Unbekannter Typ (" + type + ")");
			}
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"Edit(Undo): fuer den Typ "
							+ type
							+ " wurde an einer Stelle eine Zahl erwartet, aber nicht gefunden: "
							+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException(
					"Edit(Undo): Die Arraygrenzen im Typ: " + type
							+ " wurden ueberschritten: " + a.getMessage());
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException(
					"Edit(Undo): Gespeicherter Typ und eig. Typ stimmen nicht ueberein. Erwarte "
							+ type + " habe aber (neu) "
							+ newInfo.getClass().getSimpleName()
							+ " und (alt) "
							+ oldInfo.getClass().getSimpleName());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException(
					"Rückgängig machen von Änderung nicht möglich: "
							+ db.getMessage());
		}
	}

	/**
	 * Fuehrt den Befehl erneut aus.
	 */
	public void redo() {
		execute();
	}

	/**
	 * Fuehrt den Befehl aus und ersetzt die alten durch die neuen Werte.
	 */
	public void execute() {
		try {
			switch (type) {
			case ROOM:
				DBHandler.updateRoom((Room) newInfo);
				// Distanzpruefungen fuer alle...
				for (WEEKDAYS w : DBHandler.getConfig().getPlannedDays()) {
					for (Teacher t : DBHandler.getAllTeachers()) {
						Warner.WARNER.warn(new DistanceWarning("", w, t));
					}
					for (EducEmployee e : DBHandler.getAllEducEmployees()) {
						Warner.WARNER.warn(new DistanceWarning("", w, e));
					}
					for (Class c : DBHandler.getAllClasses()) {
						Warner.WARNER.warn(new DistanceWarning("", w, c));
					}
				}
				break;
			case SUBJECT:
				DBHandler.updateSubject((Subject) newInfo);
				break;
			case TEACHER:
				DBHandler.updateTeacher((Teacher) newInfo);
				// Falls workload geaendert
				Warner.WARNER.warn(new OverloadWarning("", (Teacher) newInfo));
				break;
			case EDUC:
				DBHandler.updateEducEmployee((EducEmployee) newInfo);
				// Falls workload geaendert
				Warner.WARNER.warn(new OverloadWarning("",
						(EducEmployee) newInfo));
				break;
			case YEARINFO:
				String[] editedYear = (String[]) newInfo;

				Subject s = DBHandler
						.getSubjectByName(editedYear[YEAR_SUB - 1]);
				int year = Integer.parseInt(editedYear[YEAR_YEAR - 1]);
				int work = Integer.parseInt(editedYear[YEAR_WORK - 1]);

				// Falls jetzt mehr soll
				if (work > DBHandler.getIntendedWorkloadPerSubjectPerYear(s,
						year)) {
					DBHandler.updateHoursPerSubjectYear(s, year, work);
					// Warnungen DANACH generieren, sonst bei internem check auf
					// altem Stand
					for (Class c : DBHandler.getClassesByYear(year)) {
						Warner.WARNER.warn(new OverloadWarning(s, c));
					}
				} else {
					DBHandler.updateHoursPerSubjectYear(s, year, work);
				}
				break;
			case CLASS:
				DBHandler.updateClass((Class) newInfo);
				break;
			case EXTERN:
				ExternUnit ex = (ExternUnit) newInfo;
				DBHandler.updateExternUnit(ex);
				for (Class c : ex.getClasses()) {
					Warner.WARNER.warn(new OverloadWarning("", c));
					Warner.WARNER.warn(new MultipleReservationWarning("", ex
							.getTimeslot(), c));
				}
				break;
			case TEACHING:
				TeachingUnit tu = (TeachingUnit) newInfo;
				DBHandler.updateTeachingUnit(tu);
				tu = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(), tu.getId());
				for (Class c : tu.getClasses()) {
					for (Subject sub : tu.getSubjects()) {
						Warner.WARNER.warn(new OverloadWarning(sub, c));
					}
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), c));
					Warner.WARNER.warn(new DistanceWarning("", tu.getTimeslot()
							.getDay(), c));
				}

				for (PersonTime pt : tu.getPersons()) {
					Warner.WARNER.warn(new OverloadWarning("", pt.getPerson()));
					Warner.WARNER.warn(new MultipleReservationWarning("", pt
							.getTimeslot(), pt.getPerson()));
					Warner.WARNER.warn(new DistanceWarning("", tu.getTimeslot()
							.getDay(), pt.getPerson()));
					if (pt.getPerson() instanceof Teacher) {
						Teacher t = (Teacher) pt.getPerson();
						Warner.WARNER.warn(new ConflictWishesWarning(
								tu.getId(), t, pt.getTimeslot()));
					}
				}
				for (Room r : tu.getRooms()) {
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), r));
				}

				break;
			case TEACHPERS:
				Object[] newPt = (Object[]) newInfo;
				TeachingUnit tea = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(), (int) newPt[0]);
				PersonTime pt = (PersonTime) newPt[1];
				Set<PersonTime> spt = tea.getPersons();
				spt.removeIf(x -> x.getPerson().getShortName()
						.equals(pt.getPerson().getShortName()));
				spt.add(pt);
				tea.setPersonTime(spt);
				DBHandler.updateTeachingUnit(tea);
				Warner.WARNER.warn(new MultipleReservationWarning("", tea
						.getTimeslot(), pt.getPerson()));
				Warner.WARNER.warn(new OverloadWarning("", pt.getPerson()));
				break;
			case CLASSINFO:
				String[] newclass = (String[]) newInfo;
				Class c = DBHandler.getClassByName(newclass[CLASSINFO_CLASS]);
				Subject sub = DBHandler
						.getSubjectByName(newclass[CLASSINFO_SUB]);
				int hours = Integer.parseInt(newclass[CLASSINFO_WORK]);
				c.setHours(sub, hours);
				DBHandler.updateClass(c);
				Warner.WARNER.warn(new OverloadWarning(sub, c));
				break;
			default:
				throw new IllegalArgumentException(
						"Edit(Exec): Unbekannter Typ (" + type + ")");
			}
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"Edit(Exec): fuer den Typ "
							+ type
							+ " wurde an einer Stelle eine Zahl erwartet, aber nicht gefunden: "
							+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException(
					"Edit(Exec): Die Arraygrenzen im Typ: " + type
							+ " wurden ueberschritten: " + a.getMessage());
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException(
					"Edit(Exec): Gespeicherter Typ und eig. Typ stimmen nicht ueberein. Erwarte "
							+ type + " habe aber (neu) "
							+ newInfo.getClass().getSimpleName()
							+ " und (alt) "
							+ oldInfo.getClass().getSimpleName());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException(
					"Übernehmen von Änderung nicht möglich: " + db.getMessage());
		}
	}
}
