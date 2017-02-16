package Yellowstone.YellowTable.warnings;

import javax.persistence.*;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.logic.components.*;

/**
 * Repraesentiert eine Warnung wegen Ueberlastung einer Ressource.
 * Beruecksichtigte Ressourcen:
 * 
 * <li>Personen</li>
 * 
 * <li>Klassen</li>
 * 
 * @author apag
 *
 */
@Entity
public class OverloadWarning extends Warning {

	/**
	 * Der Schluessel der ueberlasteten Ressource
	 */
	private String what;

	/**
	 * Die Art der Ressource
	 */
	private String type;

	/**
	 * Weitergehende Informationen, werden zur Zeit nur im Fall von Klassen
	 * benutzt um das betroffene Fach zu identifizieren.
	 */
	private String reason = "";

	/**
	 * Erstellen einer Warnung fuer Personen, deren aktuelle Arbeitslast hoeher
	 * ist als geplant.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pPerson
	 *            die betroffene Ressource
	 */
	public OverloadWarning(final String pDescription, final Person pPerson) {
		this("Person (" + pPerson.getShortName() + ") " + pDescription);

		what = pPerson.getShortName();
		type = Person.class.getSimpleName();
	}

	/**
	 * leerer Konstruktor für die DB
	 */
	public OverloadWarning() {

	}

	/**
	 * Erstellen einer Warnung fuer Klassen, deren aktuelle Arbeitslast in
	 * irgendeinem Planungsinhalt hoeher ist, als erwartet.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 * @param pClass
	 *            die betroffene Ressource
	 */
	public OverloadWarning(final String pDescription, final Class pClass) {
		this("Klasse (" + pClass.getName() + ") " + pDescription);

		what = pClass.getName();
		type = Class.class.getSimpleName();
	}

	/**
	 * Erstellen einer Warnung fuer Klassen, deren aktuelle Arbeitslast in einem
	 * bestimmten Planungsinhalt hoeher ist, als erwartet.
	 * 
	 * @param pSubject
	 * @param pClass
	 *            die betroffene Ressource
	 */
	public OverloadWarning(final Subject pSubject, final Class pClass) {
		this("in " + pSubject.getTitle(), pClass);

		what = pClass.getName();
		reason = pSubject.getTitle();
		type = Class.class.getSimpleName();
	}

	/**
	 * Interner Konstruktor, der die Prioritaet als niedrig festlegt (
	 * {@link Warning#PRIORITY_LOW}), indem der Standardkonstruktor der
	 * Superklasse verwendet wird.
	 * 
	 * @param pDescription
	 *            die Erlaeuterung zu dieser Warnung, die der Nutzer zu sehen
	 *            bekommt.
	 */
	private OverloadWarning(final String pDescription) {
		super("Überlastung: " + pDescription);
	}

	/**
	 * Gibt den Schlüssel des Objekts zurück.
	 * 
	 * @return Schlüssel
	 */
	public String getWhat() {
		return what;
	}

	/**
	 * Gibt den Typ dieser Warnung zurück.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Diese Warnung ist aktuell, wenn
	 * 
	 * <li>eine Person mehr Stunden arbeitet, als sie maximal sollte.</li>
	 * 
	 * <li>eine Klasse in irgendeinem Fach mehr Unterricht hat als geplant.</li>
	 * 
	 * <li>die Person oder Klasse nicht mehr existiert</li>
	 */
	@Override
	public boolean check() {
		try {
			if (type.equals(Person.class.getSimpleName())) {
				Person oldP = DBHandler.getTeacherByShort(what);
				if (oldP != null) {
					return DBHandler.getHoursPerTeacher((Teacher) oldP) > oldP
							.getMaxWorkload();
				}
				oldP = DBHandler.getEducEmployeeByShort(what);
				if (oldP != null) {
					return oldP.getCurWorkload() > oldP.getMaxWorkload();
				}

				return false;
			} else if (type.equals(Class.class.getSimpleName())) {
				Class cla = DBHandler.getClassByName(what);
				if (cla != null) {
					if (reason.equals("")) {
						for (Subject s : DBHandler.getAllSubjects()) {
							if (DBHandler.getHoursPerSubjectPerClass(s, cla) > cla
									.getHoursPerSubject(s)) {
								return true;
							}
						}
					} else {
						Subject sub = DBHandler.getSubjectByName(reason);
						if (sub != null) {
							return DBHandler.getHoursPerSubjectPerClass(sub,
									cla) > DBHandler
									.getIntendedWorkloadPerSubjectPerClass(sub,
											cla);
						}
					}
				}
				return false;
			}

			throw new IllegalArgumentException("Unbekannte Instanz: " + type);
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankproblem: " + what
					+ reason + db.getMessage());
		}
	}

	/**
	 * Testet Warnungen auf Gleichheit.
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			if (obj instanceof OverloadWarning) {
				OverloadWarning w = (OverloadWarning) obj;
				return type.equals(w.type) && what.equals(w.what);
			}
		}
		return false;
	}
}
