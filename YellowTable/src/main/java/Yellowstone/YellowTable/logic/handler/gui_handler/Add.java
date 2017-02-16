package Yellowstone.YellowTable.logic.handler.gui_handler;

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
import static Yellowstone.YellowTable.logic.handler.gui_handler.CommandParser.*;

/**
 * Fuegt ein Objekt dem Datenbestand hinzu.
 * 
 * Gueltige Objekttypen (erster Eintrag im String[]):
 * 
 * ROOM, SUBJECT, TEACHER, EDUCEMPLOYEE, CLASS, EXTERN, TEACHING, CLASSINFO
 * 
 * @author apag
 *
 */
public class Add extends UndoRedoCommand {

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
	 * @param ppArgs
	 *            die zu interpretierenden Argumente
	 */
	Add(final String[] pArgs) {
		if (pArgs == null || pArgs.length < MIN_SIZE) {
			throw new IllegalArgumentException(
					"Nicht genuegend oder keine Informationen gegeben zum Hinzufuegen.");
		}
		type = pArgs[0];
		try {
			switch (type) {
			case ROOM:
				if (pArgs.length <= ROOM_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen eines Raums.");
				}
				if (DBHandler.getRoomByTitle(pArgs[ROOM_TITLE]) != null) {
					throw new NeedUserCorrectionException(
							"Ein Raum mit dieser Bezeichnung existiert bereits.");
				}
				toAdd = initRoom(pArgs);
				break;
			case SUBJECT:
				if (pArgs.length <= SUBJECT_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen eines Stundeninhaltes.");
				}
				if (DBHandler.getSubjectByName(pArgs[SUB_TITLE]) != null) {
					// Korrektur durch Nutzer noetig.
					throw new NeedUserCorrectionException(
							": Ein Stundeninhalt mit dem Titel "
									+ pArgs[SUB_TITLE] + " existiert bereits.");
				}
				toAdd = initSubject(pArgs);
				break;
			case TEACHER:
				if (pArgs.length <= TEACHER_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer Lehrerin.");
				}
				if (DBHandler.getTeacherByShort(pArgs[PERS_SHORT]) != null
						|| DBHandler.getEducEmployeeByShort(pArgs[PERS_SHORT]) != null) {
					throw new NeedUserCorrectionException(
							"Eine Person mit dem Kürzel " + pArgs[PERS_SHORT]
									+ " existiert bereits.");
				}
				toAdd = initTeacher(pArgs);
				break;
			case EDUC:
				if (pArgs.length <= EDUC_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen eines paedagogischen Mitarbeiters.");
				}
				if (DBHandler.getTeacherByShort(pArgs[PERS_SHORT]) != null
						|| DBHandler.getEducEmployeeByShort(pArgs[PERS_SHORT]) != null) {
					throw new NeedUserCorrectionException(
							"Eine Person mit dem Kürzel " + pArgs[PERS_SHORT]
									+ " existiert bereits.");
				}
				toAdd = initEduc(pArgs);
				break;
			case CLASS:
				if (pArgs.length <= CLASS_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer Klasse.");
				}
				if (DBHandler.getClassByName(pArgs[CLASS_YEAR]
						+ pArgs[CLASS_TITLE]) != null) {
					throw new NeedUserCorrectionException(
							"Eine Klasse mit dem Namen " + pArgs[CLASS_YEAR]
									+ pArgs[CLASS_TITLE]
									+ " existiert bereits.");
				}
				toAdd = initClass(pArgs);
				break;
			case EXTERN:
				if (pArgs.length <= EXTERN_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer externen Planungseinheit.");
				}
				// "doppelte" Units moeglich, intern unterschiedliche IDs
				toAdd = initExtern(pArgs);
				break;
			case TEACHING:
				if (pArgs.length <= TEACHING_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen gegeben zum Hinzufuegen einer Unterrichtseinheit.");
				}
				// "doppelte" Units moeglich, intern unterschiedliche IDs
				TeachingUnit tu = initTeaching(pArgs);
				int id = (DBHandler.addTeachingUnit(tu));
				tu.setId(id);
				System.out.println("Vorlaeufig added " + id);
				System.out.println("Unit by id "
						+ DBHandler.getUnitByID(
								TeachingUnit.class.getSimpleName(), id));
				System.out.println("Equals "
						+ DBHandler.getUnitByID(
								TeachingUnit.class.getSimpleName(), id).equals(
								tu));
				System.out.println("getTeachingUnit "
						+ DBHandler.getTeachingUnit(tu));
				for (PersonTime pt : tu.getPersons()) {
					DistanceWarning dist = new DistanceWarning("", tu
							.getTimeslot().getDay(), pt.getPerson());
					if (dist.check()) {
						DBHandler.deleteTeachingUnit(tu);
						throw new NeedUserCorrectionException(
								"Es ist ein Gebäudewechsel für die Person "
										+ pt.getPerson().getShortName()
										+ "\n notwendig, dafür muss Zeit eingeplant werden.");
					}
				}
				for (Class c : tu.getClasses()) {
					DistanceWarning dist = new DistanceWarning("", tu
							.getTimeslot().getDay(), c);
					if (dist.check()) {
						DBHandler.deleteTeachingUnit(tu);
						throw new NeedUserCorrectionException(
								"Es ist ein Gebäudewechsel für die Klasse "
										+ c.getName()
										+ "\n notwendig, dafür muss Zeit eingeplant werden.");
					}
				}
				System.out.println("Vorerst wieder entfernen " + tu.getId());
				DBHandler.deleteTeachingUnit(tu);
				toAdd = tu;
				break;
			case CLASSINFO:
				if (pArgs.length <= CLAINF_MIN) {
					throw new IllegalArgumentException(
							"Nicht genuegend Informationen zum Hinzufuegen gegeben.");
				}

				Class c = DBHandler.getClassByName(pArgs[CLASSINFO_CLASS]);
				Subject s = DBHandler.getSubjectByName(pArgs[CLASSINFO_SUB]);
				if (c == null || s == null) {
					throw new NeedUserCorrectionException("Die Klasse ("
							+ pArgs[CLASSINFO_CLASS]
							+ ") oder der Stundeninhalt ("
							+ pArgs[CLASSINFO_SUB] + ") existieren nicht.");
				}
				try {
					Integer.parseInt(pArgs[CLASSINFO_WORK]);
				} catch (final NumberFormatException n) {
					throw new NeedUserCorrectionException(
							"Keine Zahl übergeben als neuer Wochenbedarf.");
				}

				String[] classinfo = { pArgs[CLASSINFO_CLASS],
						pArgs[CLASSINFO_SUB], pArgs[CLASSINFO_WORK] };
				toAdd = classinfo;
				break;
			default:
				throw new IllegalArgumentException("Add: Unbekannter Typ "
						+ pArgs[0]);
			}
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException("Add (" + type
					+ "): Arraygrenzen ueberschritten: " + a.getMessage());
		} catch (final NullPointerException np) {
			np.printStackTrace();
			throw new IllegalArgumentException("Add (" + type
					+ "): NULL wird nicht gerne gesehen... " + np.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Speichern nicht möglich: "
					+ db.getMessage());
		} catch (final Exception e) {
			System.out.println("Add (" + type + "): Exception ->");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Macht diesen Befehl rueckgaengig.
	 */
	public void undo() {
		try {
			switch (type) {
			case SUBJECT:
				DBHandler.deleteSubject((Subject) toAdd);
				break;
			case ROOM:
				DBHandler.deleteRoom((Room) toAdd);
				break;
			case TEACHER:
				DBHandler.deleteTeacher((Teacher) toAdd);
				break;
			case EDUC:
				DBHandler.deleteEducEmployee((EducEmployee) toAdd);
				break;
			case CLASS:
				DBHandler.deleteClass((Class) toAdd);
				break;
			case EXTERN:
				DBHandler.deleteExternUnit((ExternUnit) toAdd);
				break;
			case TEACHING:
				DBHandler.deleteTeachingUnit((TeachingUnit) toAdd);
				break;
			case CLASSINFO:
				String[] classinfo = (String[]) toAdd;
				Class c = DBHandler
						.getClassByName(classinfo[CLASSINFO_CLASS - 1]);
				Subject s = DBHandler
						.getSubjectByName(classinfo[CLASSINFO_SUB - 1]);
				c.deleteIntendedWorkloadForSubject(s);
				DBHandler.updateClass(c);
				break;
			default:
				throw new IllegalArgumentException(
						"Add(Undo): unbekannter Typ (" + type + ")");
			}
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException(
					"Add(Undo): Gespeicherter Typ und eig. Typ stimmen nicht ueberein. Erwarte "
							+ type + " habe aber "
							+ toAdd.getClass().getSimpleName());
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"Add(Undo): fuer den Typ "
							+ type
							+ " wurde an einer Stelle eine Zahl erwartet, aber nicht gefunden: "
							+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException(
					"Add(Undo): Die Arraygrenzen im Typ: " + type
							+ " wurden ueberschritten: " + a.getMessage());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException(
					"Rückgängig machen von Hinzufügen nicht möglich: " + type
							+ db.getMessage());
		} catch (final Exception e) {
			System.out.println("Add (Undo): Exception ->");
			e.printStackTrace();
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
	 */
	public void execute() {
		try {
			switch (type) {
			case TEACHER:
				DBHandler.addTeacher((Teacher) toAdd);
				break;
			case EDUC:
				DBHandler.addEducEmployee((EducEmployee) toAdd);
				break;
			case ROOM:
				DBHandler.addRoom((Room) toAdd);
				break;
			case CLASS:
				DBHandler.addClass((Class) toAdd);
				break;
			case EXTERN:
				ExternUnit ex = (ExternUnit) toAdd;
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
				DBHandler.addSubject((Subject) toAdd);
				break;
			case TEACHING:
				TeachingUnit tu = (TeachingUnit) toAdd;
				// Hinzufuegen und ID auf die des hinzugefuegten setzen
				tu.setId(DBHandler.addTeachingUnit(tu));

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
				String[] classinfo = (String[]) toAdd;
				Class c = DBHandler
						.getClassByName(classinfo[CLASSINFO_CLASS - 1]);
				Subject s = DBHandler
						.getSubjectByName(classinfo[CLASSINFO_SUB - 1]);
				int hours = Integer.parseInt(classinfo[CLASSINFO_WORK - 1]);
				c.setHours(s, hours);

				DBHandler.updateClass(c);
				Warner.WARNER.warn(new OverloadWarning(s, c));
				break;
			default:
				throw new IllegalArgumentException(
						"Add(Exec): unbekannter Typ (" + type + ")");
			}
		} catch (final NumberFormatException n) {
			throw new IllegalArgumentException(
					"Add(Exec): fuer den Typ "
							+ type
							+ " wurde an einer Stelle eine Zahl erwartet, aber nicht gefunden: "
							+ n.getMessage());
		} catch (final ArrayIndexOutOfBoundsException a) {
			throw new IllegalArgumentException(
					"Add(Exec): Die Arraygrenzen im Typ: " + type
							+ " wurden ueberschritten: " + a.getMessage());
		} catch (final ClassCastException c) {
			throw new IllegalArgumentException(
					"Add(Exec): Gespeicherter Typ und eig. Typ stimmen nicht ueberein. Erwarte "
							+ type + " habe aber "
							+ toAdd.getClass().getSimpleName());
		} catch (final DatabaseException db) {
			throw new NeedUserCorrectionException("Hinzufügen nicht möglich: "
					+ db.getMessage());
		} catch (final Exception e) {
			System.out.println("Add (Exec): Exception ->");
			e.printStackTrace();
		}
	}
}
