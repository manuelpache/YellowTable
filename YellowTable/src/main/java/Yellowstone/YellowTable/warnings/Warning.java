package Yellowstone.YellowTable.warnings;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.*;

/**
 * Warnungen sind keine Fehler, die unverzueglich behoben werden muessen.
 * Stattdessen werden sie alle im {@link Warner} registriert und koennen dem
 * Nutzer gebuendelt angezeigt werden. Das erlaubt ihm weiter zu planen und
 * umzustrukturieren, bevor die Warnungen behoben werden.
 * 
 * <p>
 * Fuer den Gebrauch sollten Subklassen instanziiert werden.
 * </p>
 * 
 * <p>
 * Warnungen haben eine natuerliche Ordnung gemaess ihrer Prioritaet. Fuer
 * naeheres siehe {@link #compareTo(Warning)}
 * </p>
 * 
 * @author apag
 *
 */
@Entity
public class Warning implements Comparable<Warning> {

	/*
	 * Prioritaeten als Konstanten, wobei kleinere Zahlen auf hoehere
	 * Prioritaeten deuten.
	 */
	public static final int PRIORITY_LOW = 2;
	public static final int PRIORITY_MIDDLE = 1;
	public static final int PRIORITY_HIGH = 0;

	@Id
	@GeneratedValue(strategy = AUTO)
	private int id;

	/**
	 * Die Beschreibung zu dieser Warnung.
	 */
	private String description;

	/**
	 * Die Prioritaet dieser Warnung.
	 */
	private int priority;

	/**
	 * 
	 * <li>unvollstaendig? was</li>
	 * 
	 * <li></li>
	 * 
	 */
	public Warning(final int pPriority, final String pDescription) {
		if (pPriority < PRIORITY_HIGH) {
			throw new IllegalArgumentException(
					"Die hoechste Prioritaet kann nicht unterschritten werden"
							+ " (Prioritaet unter 0).");
		}
		if (pDescription == null || pDescription.trim().length() <= 0) {
			throw new IllegalArgumentException(
					"Die gegebene Erlaeuterung ist unzureichend fuer die Benutzerschnittstelle: "
							+ pDescription);
		}
		priority = pPriority;
		description = pDescription;
	}

	/**
	 * Öffentlicher Konstruktor ohne Parameter für die DB.
	 */
	public Warning() {
	}

	/**
	 * Ein zusaetzlicher Konstruktor zum Erstellen einer Warnung mit mit
	 * Standardprioritaet.
	 * 
	 * @param pDescription
	 *            der beschreibende Text zu dieser Warnung.
	 */
	public Warning(final String pDescription) {
		this(PRIORITY_LOW, pDescription);
	}

	/**
	 * Gibt den beschreibenden Text zurueck.
	 * 
	 * @return die Beschreibung
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gibt die Prioritaet dieser Warnung zurueck. Der Wert 0 steht dabei fuer
	 * die hoechste Prioritaet.
	 * 
	 * @return die Prioritaet
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Eine Warnung ist "groesser" als eine andere, wenn sie eine niedrigere
	 * Prioritaet (also einen hoeheren Prioritaetswert!) hat.
	 */
	@Override
	public int compareTo(final Warning o) {
		if (o != null) {
			if (o.priority == priority) {
				return 0;
			}
			if (priority < o.priority) {
				return -1;
			}
			if (priority > o.priority) {
				return 1;
			}
		}

		throw new IllegalArgumentException(
				"Vergleich mit null nicht vorgesehen.");
	}

	/**
	 * Gibt zurueck, ob diese Warnung noch aktuell ist.
	 * 
	 * Da diese allgemeine Warnung {@link Warning} nicht ueberprueft werden
	 * kann, wird hier immer {@code true} zurueckgegeben. Subklassen sollten
	 * diese Methode <b>unbedingt ueberschreiben!</b>
	 * 
	 * @return {@code true} wenn diese Warnung noch aktuell ist, {@code false}
	 *         sonst.
	 */
	public boolean check() {
		return true;
	}

	/**
	 * Ueberschriebene toString fuer Testzwecke
	 */
	@Override
	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Warning) {
			Warning w = (Warning) obj;
			return description.equals(w.description) && priority == w.priority;
		}
		return false;
	}

	/**
	 * Gibt die von der DB zugeordnete ID zurück.
	 * 
	 * @return ID
	 */
	public int getId() {
		return id;
	}
}
