package Yellowstone.YellowTable.warnings;

import javax.persistence.Entity;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.PersonTime;
import Yellowstone.YellowTable.logic.components.TeachingUnit;
import Yellowstone.YellowTable.logic.components.Timeslot;
import Yellowstone.YellowTable.logic.components.Teacher;

/**
 * Eine Warnung fuer den Fall, dass eine Lehrerin ausserhalb ihrer Wunschzeiten
 * eingesetzt wird.
 * 
 * @author apag
 *
 */
@Entity
public class ConflictWishesWarning extends Warning {

	/**
	 * Die Lehrerin, die einen Konflikt mit ihren Wunschzeiten hat.
	 */
	private String who;

	/**
	 * Der Zeitraum mit dem Konflikt.
	 */
	private Timeslot slot;

	private int refId;

	/**
	 * Konstruktor
	 * 
	 * @param pTeacher
	 * @param pSlot
	 */
	public ConflictWishesWarning(final int pId, final Teacher pTeacher,
			final Timeslot pSlot) {
		super(PRIORITY_LOW, "Konflikt in Wunschzeiten ("
				+ pTeacher.getShortName() + "): " + pSlot.getDay().getTitle()
				+ ", " + Timeslot.addStringHour(pSlot.getStartHour()) + ":"
				+ Timeslot.addStringMinute(pSlot.getStartMinute()) + ", "
				+ pSlot.getDuration() + " Minuten");
		if (pTeacher != null && pSlot != null) {
			who = pTeacher.getShortName();
			slot = pSlot;
			refId = pId;
		} else {
			throw new IllegalArgumentException(
					"Null als Lehrerin oder Timeslot uebergeben.");
		}
	}

	/**
	 * Leerer Konstruktor fuer die Datenbbank
	 */
	public ConflictWishesWarning() {

	}

	/**
	 * Gibt den betroffenen Lehrer zurueck.
	 * 
	 * @return den Lehrer
	 */
	public String getWho() {
		return who;
	}

	/**
	 * Setzt den Lehrer
	 * 
	 * @param who
	 */
	public void setWho(final String who) {
		this.who = who;
	}

	/**
	 * Gibt den betrachteten Zeitraum zurueck.
	 * 
	 * @return
	 */
	public Timeslot getSlot() {
		return slot;
	}

	/**
	 * Setzt den Zeitraum
	 * 
	 * @param slot
	 */
	public void setSlot(final Timeslot slot) {
		this.slot = slot;
	}

	/**
	 * Aktuell u.a. , wenn Person und Unit noch existieren.
	 */
	@Override
	public boolean check() {
		try {
			TeachingUnit tu = (TeachingUnit) DBHandler.getUnitByID(
					TeachingUnit.class.getSimpleName(), refId);
			// Zugeordnete Unit existiert noch
			if (tu != null) {
				// Person ist weiter zugeordnet
				if (tu.containsPerson(who)) {
					for (PersonTime pt : tu.getPersons()) {
						if (pt.getPerson().getShortName().equals(who)) {
							// wenn individuelle Zeit anders, nicht aktuell
							if (!pt.getTimeslot().equals(slot)) {
								return false;
							}
						}
					}
					Teacher t = DBHandler.getTeacherByShort(who);
					if (t != null) {
						boolean somewhere = false;
						for (Timeslot ts : t.getPreferences()) {
							somewhere = somewhere || slot.isInTimeframe(ts);
						}
						return !somewhere;
					}
				}
			}

			return false;
		} catch (final DatabaseException db) {
			db.printStackTrace();
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}
}
