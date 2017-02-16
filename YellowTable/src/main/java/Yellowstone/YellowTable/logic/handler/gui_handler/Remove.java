package Yellowstone.YellowTable.logic.handler.gui_handler;

import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;
import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.exceptions.NeedUserCorrectionException;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.warnings.ConflictWishesWarning;
import Yellowstone.YellowTable.warnings.DistanceWarning;
import Yellowstone.YellowTable.warnings.MultipleReservationWarning;
import Yellowstone.YellowTable.warnings.OverloadWarning;
import Yellowstone.YellowTable.warnings.Warner;

/**
 * Entfernt ein Objekt aus dem Datenbestand.
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * ROOM, SUBJECT, TEACHER, EDUCEMPLOYEE, CLASS, EXTERN, TEACHING, CLASSINFO
 * 
 * @author apag
 *
 */
public class Remove extends UndoRedoCommand {

	/**
	 * Die Art des Objekts
	 */
	private final String type;

	/**
	 * Objekt, das entfernt wird.
	 */
	private final Object toRemove;

	Remove(final String[] pArgs) {
		if (pArgs.length <= 0) {
			throw new IllegalArgumentException(
					"Nicht genuegend Informationen gegeben zum Entfernen.");
		}
		type = pArgs[0];
		try {
			switch (type) {
			case ROOM:
				toRemove = DBHandler.getRoomByTitle(pArgs[ROOM_TITLE]);
				break;
			case SUBJECT:
				toRemove = DBHandler.getSubjectByName(pArgs[SUB_TITLE]);
				break;
			case TEACHER:
				toRemove = DBHandler.getTeacherByShort(pArgs[PERS_SHORT]);
				break;
			case EDUC:
				toRemove = DBHandler.getEducEmployeeByShort(pArgs[PERS_SHORT]);
				break;
			case CLASS:
				toRemove = DBHandler.getClassByName(pArgs[CLASS_YEAR]
						+ pArgs[CLASS_TITLE]);
				System.out.println("Jahr " + pArgs[CLASS_YEAR]);
				System.out.println("Bez. " + pArgs[CLASS_TITLE]);
				break;
			case EXTERN:
				toRemove = (ExternUnit) DBHandler.getUnitByID(
						ExternUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));
				break;
			case TEACHING:
				toRemove = (TeachingUnit) DBHandler.getUnitByID(
						TeachingUnit.class.getSimpleName(),
						Integer.parseInt(pArgs[UNIT_ID]));
				break;
			case CLASSINFO:
				if (pArgs.length < MIN_SIZE) {
					throw new IllegalArgumentException(
							"Nicht genuegend Infornationen gegeben zum Entfernen eines individuellen Klassenbedarfs.");
				}
				Class c = DBHandler.getClassByName(pArgs[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(pArgs[CLASSINFO_SUB]);
				if (c == null || s == null) {
					throw new NeedUserCorrectionException("Die Klasse ("
							+ pArgs[CLASSINFO_CLASS] + ") oder Stundeninhalt ("
							+ pArgs[CLASSINFO_SUB] + ") existieren nicht.");
				}
				if (c.getHoursPerSubject(s) < 0) {
					throw new NeedUserCorrectionException(
							"Die Klasse "
									+ pArgs[CLASSINFO_CLASS]
									+ " besitzt keinen individuellen Bedarf im Stundeninhalt "
									+ pArgs[CLASSINFO_SUB]);
				}
				String[] rem = new String[pArgs.length + 1];
				for (int i = 0; i < pArgs.length; i++) {
					rem[i] = pArgs[i];
				}
				rem[CLASSINFO_WORK] = String.valueOf(c.getHoursPerSubject(s));
				toRemove = rem;
				break;
			default:
				throw new IllegalArgumentException("Remove: Unbekannter Typ : "
						+ type);
			}

			if (toRemove == null) {
				throw new IllegalArgumentException(
						"Remove: Es existiert kein solches Objekt.");
			}
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new NeedUserCorrectionException("Entfernen nicht möglich: "
					+ db.getMessage());
		}
	}

	/**
	 * Macht diesen Befehl rueckgaengig.
	 */
	public void undo() {
		try {
			switch (type) {
			case TEACHER:
				DBHandler.addTeacher((Teacher) toRemove);
				break;
			case EDUC:
				DBHandler.addEducEmployee((EducEmployee) toRemove);
				break;
			case ROOM:
				DBHandler.addRoom((Room) toRemove);
				break;
			case CLASS:
				DBHandler.addClass((Class) toRemove);
				break;
			case EXTERN:
				ExternUnit ex = (ExternUnit) toRemove;
				DBHandler.addExternUnit(ex);
				for (Class c : ex.getClasses()) {
					Warner.WARNER.warn(new OverloadWarning("", c));
					Warner.WARNER.warn(new MultipleReservationWarning("", ex
							.getTimeslot(), c));
					Warner.WARNER.warn(new DistanceWarning("", ex.getTimeslot()
							.getDay(), c));
				}
				break;
			case SUBJECT:
				DBHandler.addSubject((Subject) toRemove);
				break;
			case TEACHING:
				TeachingUnit tu = (TeachingUnit) toRemove;
				DBHandler.addTeachingUnit(tu);
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
				for (Class c : tu.getClasses()) {
					for (Subject sub : tu.getSubjects()) {
						Warner.WARNER.warn(new OverloadWarning(sub, c));
					}
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), c));
					Warner.WARNER.warn(new DistanceWarning("", tu.getTimeslot()
							.getDay(), c));
				}
				for (Room r : tu.getRooms()) {
					Warner.WARNER.warn(new MultipleReservationWarning("", tu
							.getTimeslot(), r));
				}
				break;
			case CLASSINFO:
				String[] removed = (String[]) toRemove;
				Class c = DBHandler.getClassByName(removed[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(removed[CLASSINFO_SUB]);
				int hours = Integer.parseInt(removed[CLASSINFO_WORK]);
				c.setHours(s, hours);
				DBHandler.updateClass(c);
				Warner.WARNER.warn(new OverloadWarning(s, c));
				break;
			default:
				throw new IllegalArgumentException(
						"Remove (Undo) unbekannter Typ: " + type);
			}
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Fuehrt diesen Befehl erneut aus.
	 */
	public void redo() {
		execute();
	}

	/**
	 * Fuehrt diesen Befehl aus.
	 * 
	 * @throws DatabaseException
	 */
	public void execute() {
		try {
			switch (type) {
			case TEACHER:
				DBHandler.deleteTeacher((Teacher) toRemove);
				break;
			case EDUC:
				DBHandler.deleteEducEmployee((EducEmployee) toRemove);
				break;
			case ROOM:
				DBHandler.deleteRoom((Room) toRemove);
				break;
			case CLASS:
				DBHandler.deleteClass((Class) toRemove);
				break;
			case EXTERN:
				DBHandler.deleteExternUnit((ExternUnit) toRemove);
				break;
			case SUBJECT:
				DBHandler.deleteSubject((Subject) toRemove);
				break;
			case TEACHING:
				DBHandler.deleteTeachingUnit((TeachingUnit) toRemove);
				break;
			case CLASSINFO:
				String[] remove = (String[]) toRemove;
				Class c = DBHandler.getClassByName(remove[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(remove[CLASSINFO_SUB]);
				c.deleteIntendedWorkloadForSubject(s);
				DBHandler.updateClass(c);
				break;
			default:
				throw new IllegalArgumentException(
						"Removen eines unbekannten Typs: " + type);
			}
		} catch (final DatabaseException e) {
			e.printStackTrace();
			throw new NeedUserCorrectionException("Entfernen nicht möglich: "
					+ e.getMessage());
		}
	}

}
