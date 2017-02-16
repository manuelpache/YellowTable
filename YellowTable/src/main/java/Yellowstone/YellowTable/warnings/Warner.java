package Yellowstone.YellowTable.warnings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Yellowstone.YellowTable.db.DBHandler;
import Yellowstone.YellowTable.exceptions.DatabaseException;

/**
 * Sammelt alle Warnungen und kann nicht mehr relevante Warnungen entfernen.
 * 
 * @author apag
 *
 */
public class Warner {

	/**
	 * Einzige Instanz dieser Klasse.
	 */
	public static Warner WARNER = new Warner();

	/**
	 * Liste der gesammelten Warnungen.
	 */
	private List<Warning> warnings;

	/**
	 * Privater Konstruktor, der das Erzeugen weiterer Instanzen durch externe
	 * Aufrufe verhindert. Bereits in der Datenbank vorhandene Warnungen werden
	 * initial in die Liste geladen.
	 */
	private Warner() {
		try {
			Collection<Warning> savedWarnings = DBHandler.getAllWarnings();
			if (savedWarnings != null) {
				warnings = new ArrayList<>(savedWarnings);
				Collections.sort(warnings);
				return;
			}
			warnings = new ArrayList<>();
		} catch (final DatabaseException db) {
			throw new IllegalArgumentException("Datenbankfehler: "
					+ db.getMessage());
		}
	}

	/**
	 * Fuegt eine Warnung hinzu, falls sie aktuell ist.
	 * 
	 * @param pWarning
	 *            die neue Warnung.
	 */
	public void warn(final Warning pWarning) {
		if (pWarning != null) {
			if (pWarning.check() && !warnings.contains(pWarning)) {
				try {
					DBHandler.addWarning(pWarning);
					warnings.add(pWarning);
				} catch (final DatabaseException db) {
					throw new IllegalArgumentException(
							"Warnung konnte nicht gespeichert werden: "
									+ db.getMessage());
				}
			}
		}
		Collections.sort(warnings);
	}

	/**
	 * Zu Testzwecken ueberschriebene {@link #toString()}-Methode, die den
	 * Inhalt der Liste aller Warnungen in der richtigen Reihenfolge
	 * zurueckgibt.
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		if (warnings.size() > 0) {
			for (int i = 0; i < warnings.size(); i++) {
				s.append(warnings.get(i));
				s.append("\n");
			}
		}

		return s.toString();
	}

	/**
	 * Ueberprueft, ob die gegebene Warnung noch aktuell ist und entfernt diese,
	 * sollte sie es nicht mehr sein.
	 * 
	 * @param pWarning
	 *            die zu pruefende Nachricht
	 * @throws IllegalArgumentException
	 *             wenn pWarning <code>null</code> oder gar nicht in der Liste
	 *             ist.
	 */
	private void checkWarning(final Warning pWarning) {
		if (pWarning != null && warnings.contains(pWarning)) {
			if (!pWarning.check()) {
				warnings.remove(pWarning);
				try {
					DBHandler.deleteWarning(pWarning);
				} catch (final DatabaseException db) {
					// fliegt wenn die Unit mit dem von dieser Warnung
					// verwendeten Timeslot entfernt wird, da die Warnung damit
					// nicht mehr aktuell ist, ist das kein Problem
				}
			}
		} else {
			throw new IllegalArgumentException("Invalides Warning-Objekt");
		}
	}

	/**
	 * Iteriert ueber alle Warnungen und entfernt jene, die nicht mehr aktuell
	 * sind. Die Liste sollte nach diesem Aufruf weiterhin sortiert sein.
	 */
	public void checkAll() {
		// Ueber Kopie iterieren, da Elemente entfernt werden
		for (Warning w : new ArrayList<>(warnings)) {
			checkWarning(w);
		}
		Collections.sort(warnings);
	}

	/**
	 * Gibt eine Kopie der Liste aller Warnungen zurueck.
	 * 
	 * @return eine Liste aller Warnungen.
	 */
	public List<Warning> getWarnings() {
		return new ArrayList<Warning>(warnings);
	}
}
