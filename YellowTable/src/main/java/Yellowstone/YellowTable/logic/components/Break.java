/**
 * Klasse gehört zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Imports.
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Klasse, die eine Pause darstellt. Sie erbt von SchoolUnit.
 * 
 * Logger dokumentiert Objekte zur Laufzeit.
 * 
 * @author lamduy, tomlewandowski(Logger), phil (Persistenz) , erik
 *         getDayAsString()
 *
 */
@Entity
public class Break extends SchoolUnit implements IBreak {

	// Attribute.
	/**
	 * Der Logger dieser Klasse
	 */
	final static Logger logger = Logger.getLogger(Break.class);

	/**
	 * Der Jahrgang der Pause
	 */
	private int years;

	/**
	 * Der Titel der Pause
	 */
	private String title;

	/**
	 * Die Tage an denen die Pause stattfindet
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	private Set<WEEKDAYS> DAYS = new HashSet<WEEKDAYS>();

	// Konstruktoren

	/**
	 * Der Konstruktor der Klasse
	 * 
	 * @param pYear
	 *            der Jahrgang der Pause
	 * @param pTitle
	 *            der Titel der Pause
	 * @param pTimeslot
	 *            der Timeslot der Pause
	 */
	public Break(final int pYear, final String pTitle, final Timeslot pTimeslot) {
		super();
		setYear(pYear);
		setTitle(pTitle);
		setTimeslot(pTimeslot);

		if (logger.isInfoEnabled()) {
			logger.info("Folgende Pause wurde erstellt: " + pTitle + ", "
					+ years + ", " + pTimeslot.toString());
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI
	public Break() {
		super();
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Pausen-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Gibt den Titel einer Pause zurueck
	 * 
	 * @return den Titel der Pause
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Gibt den Jahrgang zurueck, fuer den die Pause stattfindet
	 * 
	 * @return den Jahrgang, dem die Pause zugeordnet ist
	 */
	@Override
	public int getYear() {
		return years;
	}

	/**
	 * Gibt die Tage zurueck an denen die Pause stattfindet
	 * 
	 * @return die Tage an denen die Pause stattfindet
	 */
	@Override
	public Set<WEEKDAYS> getDays() {
		return DAYS;
	}

	/**
	 * Gibt alle Tage an denen die Pause stattfindet, strukturiert als String
	 * zurueck
	 * 
	 * @return die Tage der Pause als String
	 */
	public String getDaysAsString() {
		String weekday = "";
		String[] weekdays = new String[7];

		for (WEEKDAYS d : DAYS) {

			if (d.toString().equals("NONE")) {
				return "Mo,Di,Mi,Do,Fr,Sa,So";
			}

			if (d.toString().equals("MONDAY") && weekdays[0] == null) {
				weekdays[0] = ("Mo,");
			} else if (d.toString().equals("TUESDAY") && weekdays[1] == null) {
				weekdays[1] = ("Di,");
			} else if (d.toString().equals("WEDNESDAY") && weekdays[2] == null) {
				weekdays[2] = ("Mi,");
			} else if (d.toString().equals("THURSDAY") && weekdays[3] == null) {
				weekdays[3] = ("Do,");
			} else if (d.toString().equals("FRIDAY") && weekdays[4] == null) {
				weekdays[4] = ("Fr,");
			} else if (d.toString().equals("SATURDAY") && weekdays[5] == null) {
				weekdays[5] = ("Sa,");
			} else if (d.toString().equals("SUNDAY") && weekdays[6] == null) {
				weekdays[6] = ("So,");
			}
		}
		for (int i = 0; i < weekdays.length; i++) {
			if (weekdays[i] != null) {
				weekday += weekdays[i];
			}

		}
		if (weekday.endsWith(",")) {
			return weekday.substring(0, weekday.length() - 1);
		}
		return weekday;
	}

	// Setter-Methoden mit Logger

	/**
	 * Setzt den Titel einer Pause neu
	 * 
	 * @param pTitle
	 *            der Titel der Pause
	 * @throws IllegalArgumentException
	 *             wenn der Name {@code null} oder leer ist
	 */
	@Override
	public void setTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Der Titel der Pause wurde geaendert: " + pTitle);
			}
		} else {

			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Der Titel fuer die Pause ist ungueltig");
		}
	}

	/**
	 * Setzt den Jahrgang, der mit der Pause referenziert ist, neu
	 * 
	 * @param pYear
	 *            der Jahrgang der Pause
	 * @throws IllegalArgumentException
	 *             wenn der Jahrgang negativ, null oder groesser als
	 *             {@linkplain IConfig#MAX_YEAR} ist
	 */
	@Override
	public void setYear(final int pYear) {
		if (pYear > 0 && pYear <= IConfig.MAX_YEAR) {
			years = pYear;

			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Der Jahrgang der Pause wurde geaendert: " + pYear);
			}
		} else {

			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pYear);
			}
			throw new IllegalArgumentException(
					"Der Jahrgang darf nicht unter 1 oder ueber "
							+ IConfig.MAX_YEAR + " liegen");
		}
	}

	/**
	 * Setzt die Tage an denen die Pause stattfindet. Enthaelt das uebergebene
	 * Set nur {@linkplain WEEKDAYS#NONE}, dann gilt diese Pause fuer alle
	 * Wochentage, die WEEKDAYS bereitstellt. Ansonsten werden NONE-Eintraege
	 * ignoriert.
	 * 
	 * @param pDays
	 *            die Tage an denen die Pause stattfindet.
	 * @throws IllegalArgumentException
	 *             wenn {@Code null} oder ein leeres Set uebergeben wird.
	 */
	@Override
	public void setDays(final Set<WEEKDAYS> pDays) {
		if (pDays != null && !pDays.isEmpty()) {
			DAYS = pDays;

			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die Tage der Pause wurde geaendert: "
						+ pDays.toString());
			}
		} else {

			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pDays);
			}
			throw new IllegalArgumentException(
					"Es trat ein Fehler auf beim Uebergeben der Pausentage");
		}
	}

	/**
	 * Die Methode vergleicht, ob das zu vergleichende Objekt, die selben
	 * Eigenschaften ausweisen
	 * 
	 * @return true wenn das Objekt identisch ist
	 */
	@Override
	public boolean equals(final Object pObject) {
		if (pObject != null && pObject instanceof Break) {
			if (this.getTitle().equals(((Break) pObject).getTitle())) {
				if (this.getYear() == ((Break) pObject).getYear()) {
					if (this.getDays().equals(((Break) pObject).getDays())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gibt ein Set der einzelnen BreakUnits zurueck, die gemeinsam eine Pause
	 * repraesentieren.
	 * 
	 * @return die einzelnen Units dieser Pause
	 */
	public Set<BreakUnit> getBreakUnits() {
		Set<BreakUnit> units = new HashSet<BreakUnit>();
		for (WEEKDAYS w : DAYS) {
			Timeslot ts = new Timeslot(getDuration(), w,
					Timeslot.addStringHour(getTimeslot().getStartHour()),
					Timeslot.addStringMinute(getTimeslot().getStartMinute()));
			BreakUnit b = new BreakUnit(ts);
			b.setClasses(getClasses());
			b.setRooms(getRooms());
			units.add(b);
		}
		return units;
	}
}