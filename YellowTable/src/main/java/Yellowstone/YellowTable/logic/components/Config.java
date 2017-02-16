/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.apache.log4j.Logger;

/**
 * Realisiert die Werte, die der Benutzer in den Standardeinstellungen festlegt
 * und somit nur selten geaendert werden und als Grundlage fuer die weitere
 * Planung gilt.
 * 
 * @author prohleder, lamduy, tomlewandowski (Logger)
 *
 */
@Entity
public class Config implements IConfig {

	// Attribute..

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Config.class);

	/**
	 * Die Granularitaet einer Planungseinheit
	 */
	private int granularity;

	/**
	 * Der Titel des Configs.
	 */
	@Id
	private String title;

	/**
	 * Der grundlegende Timeslot indem unser Schultag stattfindet.
	 */
	@ManyToOne
	private Timeslot timeframe;

	/**
	 * Alle festen Planungseinheiten
	 */
	@Transient
	private Unit[] fixUnits;

	/**
	 * Der Zeitabstand indem ein Backup ausgefuehrt werden soll.
	 */
	private int backupTimer;

	/**
	 * Anzahl an Minuten, die fuer einen Lehrer eine Std darstellen.
	 */
	private int minPerHourTeacher;

	/**
	 * Anzahl an Minuten, die fuer einen Paed. Mitarbeiter eine Std darstellen.
	 */
	private int minPerHourEduc;

	/**
	 * Der Titel eines Backups
	 */
	private String backupTitle;

	/**
	 * Boolean, ob ein Backup beim Schliessen des Programms ausgefuehrt werden
	 * soll.
	 */
	private boolean backupOnClose;

	/**
	 * Der Pfad fuer die InitialeDatenbank
	 */
	public static final String DBInitPath = "Datenbank" + File.separator
			+ "Initial";

	/**
	 * Die Standarddauer einer Unterrichtseinheit.
	 */
	private int stdDuration;

	/**
	 * Boolean, ob dieses Projekt zum Ersten Mal gestartet wird.
	 */
	private boolean projectExist;

	/**
	 * Titel dieses Projects
	 */
	private String projectTitle;

	/**
	 * Der intended Workload pro Jahrgang
	 */
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Years> years = new ArrayList<Years>();

	/**
	 * Die Wochentage, die verplant werden duerfen.
	 */
	@Enumerated
	private Collection<WEEKDAYS> plannedDays = new ArrayList<WEEKDAYS>();

	/**
	 * Map mit allen Gebaueden und deren Entfernung zum Hauptstandort.
	 */
	@ElementCollection
	private Map<String, Integer> buildings = new HashMap<String, Integer>();

	/**
	 * Eine Menge von verfuegbaren Raumkategorien.
	 */
	@OneToMany
	private Set<Category> categories = new HashSet<Category>();

	// Konstruktoren.

	/**
	 * Der Konstruktor der Klasse
	 * 
	 * @param pTitle
	 * @param pGranularity
	 * @param pTimeframe
	 * @param pBackupTimer
	 * @param pBackupTitle
	 * @param pBackupOnClose
	 * @param pStdDuration
	 */
	public Config(final String pTitle, final int pGranularity,
			final Timeslot pTimeframe, final int pBackupTimer,
			final String pBackupTitle, final boolean pBackupOnClose,
			final int pStdDuration) {
		setTitle(pTitle);
		setGranularity(pGranularity);
		setTimeframe(pTimeframe);
		setBackupTimer(pBackupTimer);
		setBackupTitle(pBackupTitle);
		setBackupOnClose(pBackupOnClose);
		setStdDuration(pStdDuration);
		for (int i = 1; i <= IConfig.MAX_YEAR; i++) {
			years.add(new Years(i));
		}

		if (logger.isInfoEnabled()) {
			logger.info("Es wurde eine Config mit gueltigen Initialwerten erstellt");
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI..
	public Config() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Config-Objekt wurde erstellt.");
		}
		for (int i = 1; i <= IConfig.MAX_YEAR; i++) {
			years.add(new Years(i));
		}
	}

	// Getter-Methoden.

	/**
	 * Gibt die Granularitaet einer Planungseinheit zurueck.
	 */
	@Override
	public int getGranularity() {
		return granularity;
	}

	/**
	 * Gibt den Zeitslot der Schulwoche zurueck.
	 */
	@Override
	public Timeslot getTimeframe() {
		return timeframe;
	}

	/**
	 * Gibt die festen Planungseinheiten in einem Array zurueck.
	 */
	@Override
	public Unit[] getFixUnits() {
		return fixUnits;
	}

	/**
	 * Gibt den Zeitraum indem ein Backup stattfinden soll zurueck.
	 */
	@Override
	public int getBackupTimer() {
		return backupTimer;
	}

	/**
	 * Gibt den Titel eines Backups zurueck.
	 */
	@Override
	public String getBackupTitle() {
		return backupTitle;
	}

	/**
	 * Gibt zurueck, ob ein Backup beim Schliessen erstellt werden soll.
	 */
	@Override
	public boolean isBackupOnClose() {
		return backupOnClose;
	}

	/**
	 * Gibt die Standarddauer einer Unterrichtseinheit zurueck.
	 */
	@Override
	public int getStdDuration() {
		return stdDuration;
	}

	/**
	 * Gibt die Anzahl an Minuten, die fuer einen Paed. Mitarbeiter eine Std
	 * darstellen.
	 * 
	 * @return minPerHourEduc
	 */
	public final int getMinPerHourEduc() {
		return minPerHourEduc;
	}

	/**
	 * Gibt die Anzahl an Minuten, die fuer einen Lehrer eine Std darstellen.
	 * 
	 * @return minPerHourTeacher
	 */
	public final int getMinPerHourTeacher() {
		return minPerHourTeacher;
	}

	/**
	 * Gibt den Title der Konfiguration zurueck.
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Gibt zurueck, ob dieses Project schon existiert.
	 * 
	 * @return projectExist
	 */
	public boolean getProjectExist() {
		return projectExist;
	}

	/**
	 * Gibt den ProjektTitel zurueck
	 * 
	 * @return projecTitle
	 */
	public String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Gibt die Jahrgaenge zurueck.
	 */
	@Override
	public List<Years> getYears() {
		return years;
	}

	/**
	 * Gibt die Wochentage, die verplant werden duerfen zurueck.
	 */
	@Override
	public Collection<WEEKDAYS> getPlannedDays() {
		return plannedDays;
	}

	/**
	 * Gibt eine Map mit allen Gebaueden und deren Entfernung zum Hauptstandort
	 * zurueck.
	 */
	@Override
	public Map<String, Integer> getBuildings() {
		return buildings;
	}

	/**
	 * Gibt eine Menge von moeglichen Raumkategorien zurueck.
	 */
	@Override
	public Set<Category> getCategories() {
		return categories;
	}

	/**
	 * Gibt eine Map von dem Vorgegebenen Wochenstunden eines Faches pro Fach
	 * zurueck.
	 */
	@Transient
	public Map<Subject, Integer> getIntendedWorkload(final int pYear) {
		if (pYear > 0) {
			for (Years y : getYears()) {
				if (y.getYear() == pYear) {
					return getYears().get(getYears().indexOf(y))
							.getIntendedWorkload();
				}
			}
			logger.info("Jahrgang ist nicht enthalten.");
			return null;
		}
		throw new IllegalArgumentException("Fehlerhafter Parameterwert.");
	}

	// Setter-Methoden.
	/**
	 * Setzt den Titel der Konfiguration.
	 */
	@Override
	public void setTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;
			if (logger.isInfoEnabled()) {
				logger.info("Es wurde der Titel des configs gesetzt: " + pTitle);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Der Titel fuer die Konfiguration ist ungueltig.");
		}
	}

	/**
	 * Setzt die Granularitaet einer Unterrichtseinheit neu.
	 */
	@Override
	public void setGranularity(final int pGranularity) {
		if (pGranularity > 0) {
			granularity = pGranularity;
			if (logger.isInfoEnabled()) {
				logger.info("Granularitaet gesetzt auf : " + pGranularity);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pGranularity);
			}
			throw new IllegalArgumentException(
					"Die angegebene Granularitaet ist ungueltig");
		}
	}

	/**
	 * Setzt den Wert, ob dieses Projekt zum ersten Mal gestartet wurde.
	 * 
	 * @param pProjectExist
	 */
	public void setProjectExist(final boolean pProjectExist) {
		projectExist = pProjectExist;
	}

	/**
	 * Setzt den Title des Projekts neu
	 * 
	 * @param pTitle
	 */
	public void setProjectTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			projectTitle = pTitle;
			if (logger.isInfoEnabled()) {
				logger.info("ProjectTitle gesetzt auf : " + pTitle);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Der Titel des Projektes darf nicht leer sein");
		}
	}

	/**
	 * Setzt den Timeslot der Schulwoche neu.
	 */
	@Override
	public void setTimeframe(final Timeslot pTimeframe) {
		if (pTimeframe != null) {
			timeframe = pTimeframe;
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Die Dauer des Schultages wurde geandert: "
						+ pTimeframe);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTimeframe);
			}
			throw new IllegalArgumentException(
					"Der Planungsbereich ist ungueltig");
		}
	}

	/**
	 * Setzt das Array der festen Unterrichtseinheiten neu.
	 */
	@Override
	public void setFixUnits(final Unit[] pUnits) {
		if (pUnits != null) {
			fixUnits = pUnits;
			if (logger.isInfoEnabled()) {
				logger.info("Die fixUnits wurden geanderts: " + pUnits);
			}
		}
		// Logger
		if (logger.isInfoEnabled()) {
			logger.error("Fehler in folgendem Parameter: " + pUnits);
		}
		throw new IllegalArgumentException("Die Einheiten sind ungueltig");
	}

	/**
	 * Setzt die Anzahl an Minuten, die fuer einen Paed. Mitarbeiter eine Std
	 * darstellen.
	 * 
	 * @param pMinPerHourEduc
	 */
	public final void setMinPerHourEduc(final int pMinPerHourEduc) {
		if (pMinPerHourEduc >= 0) {
			minPerHourEduc = pMinPerHourEduc;
			if (logger.isInfoEnabled()) {
				logger.info("minPerHourEduc wurde geandert: " + pMinPerHourEduc);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: "
						+ pMinPerHourEduc);
			}
			throw new IllegalArgumentException(
					"Die Minuteneingabe fuer den paed. Mitarbeiter ist nicht sinnvoll.");
		}
	}

	/**
	 * Setzt die Anzahl an Minuten, die fuer einen Lehrer eine Std darstellen.
	 * 
	 * @param minPerHourTeacher
	 */
	public final void setMinPerHourTeacher(final int pminPerHourTeacher) {
		if (pminPerHourTeacher >= 0) {
			minPerHourTeacher = pminPerHourTeacher;
			if (logger.isInfoEnabled()) {
				logger.info("minPerHourTeacher wurde geandert: "
						+ pminPerHourTeacher);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: "
						+ (pminPerHourTeacher));
			}
			throw new IllegalArgumentException(
					"Die Minuteneingabe fuer den Lehrer ist nicht sinnvoll");
		}
	}

	/**
	 * Setzt den Timer des Backups neu.
	 */
	@Override
	public void setBackupTimer(final int pBackupTimer) {
		if (pBackupTimer > 0) {
			backupTimer = pBackupTimer;
			if (logger.isInfoEnabled()) {
				logger.info("Das BackupIntervall wurde festgelegt: "
						+ pBackupTimer);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pBackupTimer);
			}
			throw new IllegalArgumentException(
					"Die Minuteneingabe fuer die Backups ist nicht sinnvoll");
		}
	}

	/**
	 * Setzt den Titel, den ein Backup besitzen soll neu.
	 */
	@Override
	public void setBackupTitle(final String pBackupTitle) {
		if (pBackupTitle != null && pBackupTitle.trim().length() != 0) {
			backupTitle = pBackupTitle;
			if (logger.isInfoEnabled()) {
				logger.info("Der Backuptitel wurde geandert: " + pBackupTitle);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pBackupTitle);
			}
			throw new IllegalArgumentException(
					"Titel fuer das Backup ist ungueltig");
		}
	}

	/**
	 * Setzt neu, ob ein Backup beim Schliessen erstellt werden soll.
	 */
	@Override
	public void setBackupOnClose(final boolean pBackupOnClose) {
		backupOnClose = pBackupOnClose;
		if (logger.isInfoEnabled()) {
			logger.info("Der Parameter, ob ein Backup beim Schliessen erstellt werden soll, wurde geandert: "
					+ pBackupOnClose);
		}
	}

	/**
	 * Setzt die Standarddauer einer Unterrichtseinheit neu.
	 */
	@Override
	public void setStdDuration(int pStdDuration) {
		if (pStdDuration > 0) {
			stdDuration = pStdDuration;
			if (logger.isInfoEnabled()) {
				logger.info("Die Standarddauer wurde geandert: " + pStdDuration);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pStdDuration);
			}
			throw new IllegalArgumentException(
					"Die Standarddauer muss ueber 0 sein.");
		}
	}

	/**
	 * Setzt den Intended Workload neu.
	 */
	@Override
	public void setYears(final List<Years> pYears) {
		if (pYears != null && !pYears.isEmpty()) {
			years = pYears;
			if (logger.isInfoEnabled()) {
				logger.info("Die Jahrgaenge wurden geandert: " + pYears);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pYears);
			}
			throw new IllegalArgumentException(
					"Es muessen Jahrgaenge ausgewaehlt werden");
		}
	}

	/**
	 * Setzt die Tage, die verplant werden duerfen neu.
	 */
	@Override
	public void setPlannedDays(final Collection<WEEKDAYS> pPlannedDays) {
		if (pPlannedDays != null && !pPlannedDays.isEmpty()) {
			plannedDays = pPlannedDays;
			if (logger.isInfoEnabled()) {
				logger.info("Die Tage die verplant werden duerfen wurden veraender: "
						+ pPlannedDays);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pPlannedDays);
			}
			throw new IllegalArgumentException(
					"Die zu verplanenden Tage sind ungueltig.");
		}
	}

	/**
	 * Setzt die Standorte neu.
	 */
	@Override
	public void setBuildings(final Map<String, Integer> pBuildings) {
		if (pBuildings != null && !pBuildings.isEmpty()) {
			buildings = pBuildings;
			if (logger.isInfoEnabled()) {
				logger.info("Die Standorte wurden veraendert: " + pBuildings);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pBuildings);
			}
			throw new IllegalArgumentException(
					"Es muessen Standorte ausgewaehlt werden.");
		}
	}

	/**
	 * Setzt die Raumkategorien neu.
	 */
	@Override
	public void setCategories(final Set<Category> pCategory) {
		if (pCategory != null && !pCategory.isEmpty()) {
			categories = pCategory;
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Die Raumkategorien wurden geandert auf folgendes Objekt: "
						+ pCategory);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCategory);
			}
			throw new IllegalArgumentException(
					"Es muessen Raumkategorie ausgewaehlt werden.");
		}
	}

	/**
	 * Fuegt einen Jahrgang der Konfiguration hinzu.
	 * 
	 * @param pYear
	 *            Jahrgang
	 */
	public void addYears(final int pYear) {
		if (pYear > 0) {
			Years y = new Years(pYear);
			years.add(y);
			if (logger.isInfoEnabled()) {
				logger.info("Dieser Jahrgang wurde der Konfiguration hinzugefuegt "
						+ pYear);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Der uebergebene Parameter ist fehlerhaft."
						+ pYear);
				throw new IllegalArgumentException(
						"Der Jahrgang darf nicht negativ sein.");
			}
		}
	}

	/**
	 * Fuegt der Config fuer einen Jahrgang die vorgeschriebene StundenAnzahl
	 * hinzu.
	 */
	@Override
	public void addIntendedWorkload(final int pYear, final Subject pSubject,
			final int pValue) {
		if (pValue >= 0 && pYear > 0 && pYear <= IConfig.MAX_YEAR
				&& pSubject != null) {
			for (Years y : getYears()) {
				if (y.getYear() == pYear) {
					getYears().get(getYears().indexOf(y)).setHours(pSubject,
							pValue);
				}
			}
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Der Workload wurde erfolgreich hinzugefuegt: "
						+ pSubject);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pValue);
			}
			throw new IllegalArgumentException(
					"Jahrgang, Fach oder Stundenanzahl sind ungueltig.");
		}
	}

	/**
	 * Entfernt Workload
	 */
	@Override
	public void removeIntendedWorkload(final int pYear, final Subject pSubject) {
		if (pYear > 0 && pYear <= IConfig.MAX_YEAR && pSubject != null) {
			for (Years y : getYears()) {
				if (y.getYear() == pYear) {
					getYears().get(getYears().indexOf(y)).setHours(pSubject, 0);
				}
			}
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Der Workload wurde erfolgreich entfernt: "
						+ pSubject);
			}
		} else {
			throw new IllegalArgumentException(
					"Bitte geben sie einen gueltigen Jahrgang und ein gueltiges Fach an.");
		}
	}

	/**
	 * Editiert einen Standort.
	 */
	@Override
	public void editBuilding(final String pBuilding, final int pDistance) {
		if (pBuilding != null && pBuilding.trim().length() != 0
				&& pDistance >= 0 && buildings.containsKey(pBuilding)) {
			buildings.replace(pBuilding, pDistance);
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Der Standort wurde erfolgreich bearbeitet: "
						+ pBuilding);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pDistance);
			}
			throw new IllegalArgumentException(
					"Bitte geben sie einen gueltigen Titel fuer den Standort und eine gueltige Entfernung an.");
		}
	}

	/**
	 * Fuegt einen Standort hinzu.
	 */
	public void addBuilding(final String pBuilding, final int pDistance) {
		if (pBuilding != null && pBuilding.trim().length() != 0
				&& pDistance >= 0) {
			buildings.put(pBuilding, pDistance);
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Der Standort wurde erfolgreich hinzugefuegt: "
						+ pBuilding);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pDistance);
			}
			throw new IllegalArgumentException(
					"Ungueltiger Standort oder ungueltige Entfernung");
		}
	}

	/**
	 * Entfernt einen Standort
	 */
	@Override
	public void removeBuilding(final String pBuilding) {
		if (pBuilding != null && pBuilding.trim().length() != 0) {
			buildings.remove(pBuilding);

			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Der Standort wurde erfolgreich entfernt: "
						+ pBuilding);
			}
		} else
			throw new IllegalArgumentException(
					"Der angegebener Standort ist ungueltig.");
	}

	/**
	 * Fuegt eine Raumkategorie hinzu.
	 */
	@Override
	public void addCategory(final Category pCategory) {
		if (pCategory != null) {
			categories.add(pCategory);
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Die Raumkategorie wurde erfolgreich hinzugefuegt: "
						+ pCategory);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCategory);
			}
			throw new IllegalArgumentException(
					"Die angegebene Kategorie ist ungueltig.");
		}
	}

	/**
	 * Entfernt eine Raumkategorie.
	 */
	@Override
	public void removeCategory(final Category pCategory) {
		if (pCategory != null) {
			categories.remove(pCategory);
			// Logger..
			if (logger.isInfoEnabled()) {
				logger.info("Die Raumkategorie wurde erfolgreich entfernt: "
						+ pCategory);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCategory);
			}
			throw new IllegalArgumentException(
					"Die angegebene Kategorie ist ungueltig.");
		}
	}

	/**
	 * Die Methode ueberprueft auf Gleichheit zweier Configs
	 */
	@Override
	public boolean equals(final Object pObject) {
		if (pObject != null && pObject instanceof Config) {
			if (((Config) pObject).getTitle().equals(getTitle())) {
				if (((Config) pObject).getGranularity() == getGranularity()) {
					if (((Config) pObject).getTimeframe()
							.equals(getTimeframe())) {
						if (((Config) pObject).getBackupTimer() == getBackupTimer()) {
							if (((Config) pObject).getBackupTitle().equals(
									getBackupTitle())) {
								if (((Config) pObject).getStdDuration() == getStdDuration()) {
									return true;
								}
								return false;
							}
							return false;
						}
						return false;
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return false;
	}
}
