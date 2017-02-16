package Yellowstone.YellowTable.logic.components;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Realisiert die Werte, die der Benutzer in den Standardeinstellungen festlegt
 * und somit nur selten geaendert werden und als Grundlage fuer die weitere
 * Planung gelten.
 * 
 * @author tomlewandowski, prohleder, apag, lamduy
 *
 */
public interface IConfig {

	/**
	 * Konstante, die die maximale Anzahl von Jahrgaengen angibt.
	 */
	public static final int MAX_YEAR = 4;

	/**
	 * Gibt zurueck, wie detailliert eine Planungseinheit angezeigt werden soll.
	 * 
	 * @return der Detaillierungsgrad einer Planungseinheit.
	 */
	public int getGranularity();

	/**
	 * Gibt zurueck in welchem Zeitslot ein Schultag stattfindet.
	 * 
	 * @return der Zeitrahmen eines Schultags.
	 */
	public Timeslot getTimeframe();

	/**
	 * Gibt fixe Planungseinheiten wie Pausen und Meetings zurueck.
	 * 
	 * @return eine Liste mit fixen Planungseinheiten.
	 */
	public Unit[] getFixUnits();

	/**
	 * Gibt zurueck in welchen Minutenabstaenden ein Backup durchgefuehrt werden
	 * soll.
	 * 
	 * @return Zeitabstand eines Backups
	 */
	public int getBackupTimer();

	/**
	 * Gibt den Titel eines Backups zurueck
	 * 
	 * @return den Titel eines Backups
	 */
	public String getBackupTitle();

	/**
	 * Gibt den Titel der Konfiguration zurück.
	 * 
	 * @return den Titel der Konfiguration.
	 */
	public String getTitle();
	
	/**
	 * Gibt zurueck, ob ein Backup beim Schliessen erzeugt werden soll.
	 * 
	 * @return Backup beim Schliessen.
	 */
	public boolean isBackupOnClose();

	/**
	 * Gibt zurueck wie lange eine Planungseinheit im Regelfall dauern soll.
	 * 
	 * @return Die Dauer einer Planungseinheit im Regelfall.
	 */
	public int getStdDuration();

	/**
	 * Gibt eine Map zurueck, die fuer alle Jahrgaenge deren Bedarf an
	 * Unterrichtsstunden pro Fach innehaelt.
	 * 
	 * @return Jahrgaenge mit deren Bedarf an Stunden pro Fach.
	 */
	public List<Years> getYears();

	/**
	 * Gibt zurueck, welche Tage verplant werden.
	 * 
	 * @return Die Tage, die verplant werden.
	 */
	public Collection<WEEKDAYS> getPlannedDays();

	/**
	 * Gibt die Gebaeude, sowie deren Abstaende zum Hauptgebaeude zurueck.
	 * 
	 * @return Map mit Gebaueden und deren Abstaende zum Hauptgebaude.
	 */
	public Map<String, Integer> getBuildings();

	/**
	 * Gibt eine Menge mit allen Raumkategorien zurueck.
	 * 
	 * @return Die Raumkategorien.
	 */
	public Set<Category> getCategories();
	

	/**
	 * 
	 * @param pTitle
	 * 			den Titel der Konfiguration
	 * @throws IllegalArgumentException
	 *             wenn der Titel ungueltig ist.
	 */
	void setTitle(String pTitle) throws IllegalArgumentException;
	

	/**
	 * Setzt den Detaillierungsgrad einer Planungseinheit
	 * 
	 * @param pGranularity
	 *            den Detaillierungsgrad einer Planungseinheit.
	 * @throws IllegalArgumentException
	 *             wenn ein negativer Wert oder ein Wert ueber 60 uebergeben
	 *             wird.
	 */
	public void setGranularity(final int pGranularity)
			throws IllegalArgumentException;

	/**
	 * Setzt die allgemeine Dauer eines Schultages.
	 * 
	 * @param pTimeFrame
	 *            die Dauer eines Schultages.
	 * @throws IllegalArgumentException
	 */
	public void setTimeframe(final Timeslot pTimeframe)
			throws IllegalArgumentException;

	/**
	 * Setzt feste Einheiten wie Pausen und Meetings
	 * 
	 * @param pUnits
	 *            feste Einheiten.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setFixUnits(final Unit[] pUnits)
			throws IllegalArgumentException;

	/**
	 * Setzt den Zeitabstand eines Backups, also der zeitliche Abstand indem ein
	 * Backup durchgefuehrt werden soll.
	 * 
	 * @param pBackupTimer
	 *            der zeitliche Abstand eines Backups.
	 * @throws IllegalArgumentException
	 *             wenn ein Wert kleiner als 1 und groesser als 1000 eingegeben
	 *             wird.
	 */
	public void setBackupTimer(final int pBackupTimer)
			throws IllegalArgumentException;

	/**
	 * Setzt den Titel eines Backups
	 * 
	 * @param pBackupTitle
	 *            der neue Titel eines Backups
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setBackupTitle(final String pBackupTitle)
			throws IllegalArgumentException;

	/**
	 * Methode, die bestimmt ob beim Schliessen des Programms ein Backup
	 * stattfinden soll.
	 * 
	 * @param pBackupOnClose
	 *            Backup beim Schliessen des Programms?
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setBackupOnClose(final boolean pBackupOnClose)
			throws IllegalArgumentException;

	/**
	 * Methode, die die Standarddauer einer Planungseinheit festlegt.
	 * 
	 * @param pStdDuration
	 *            die Standarddauer einer Planungseinheit.
	 * @throws IllegalArgumentException
	 *             bei einem Wert kleiner als 0 und groesser als 120.
	 */
	public void setStdDuration(final int pStdDuration)
			throws IllegalArgumentException;

	/**
	 * Setzt den Workload fuer alle Jahrgaenge neu.
	 * 
	 * @param pIntendendWorkload
	 *            der neue Workload fuer die Jahrgaenge.
	 * @throws IllegalArgumentException
	 *             falls {@code null} uebergeben wird.
	 */
	public void setYears(
			final List<Years> pIntendendWorkload)
			throws IllegalArgumentException;

	/**
	 * Setzt die Tage neu, die im weiteren Planungsprozess verplant werden
	 * duerfen
	 * 
	 * @param pPlannedDays
	 *            Tage, die verplant werden duerfen.
	 * @throws IllegalArgumentException
	 *             Wenn {@code null} uebergeben wird.
	 */
	public void setPlannedDays(final Collection<WEEKDAYS> pPlannedDays)
			throws IllegalArgumentException;

	/**
	 * Setzt die Gebaeude neu.
	 * 
	 * @param pBuildings
	 *            die neuen Gebaude samt ihrer Entfernung zum Hauptstandort.
	 * @throws IllegalArgumentException
	 *             falls {@code null} uebergeben wird.
	 */
	public void setBuildings(final Map<String, Integer> pBuildings)
			throws IllegalArgumentException;

	/**
	 * Setzt die Kategorien fuer Raeume neu.
	 * 
	 * @param pCategory
	 *            die Kategorien, die spaeter zu Raeumen hinzugefuegt werden
	 *            koennen.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setCategories(final Set<Category> pCategory)
			throws IllegalArgumentException;

	/**
	 * Fuegt der Map den Bedarf an Untterichtsstunden pro Fach fuer die Jahrgaenge hinzu.
	 * 
	 * @param pYear
	 * 			ist der Jahrgang, welches man dem Bedarf hinzufuegen moechte.
	 * @param pSubject
	 * 			ist das Fach 
	 * @param pValue
	 * 			ist Stundenanzahl an Bedarf fuer das jeweilige Fach
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void addIntendedWorkload(final int pYear , final Subject pSubject, final int pValue)
			throws IllegalArgumentException;
	
	/**
	 * Loescht aus der Map den Bedarf an Untterichtsstunden pro Fach von den Jahrgaenge hinzu.
	 * @param pYear
	 * 			ist der Jahrgang, wo der Bedarf entfernt werden soll
	 * @param pSubject
	 * 			ist das Fach, das aus dem Bedarf entfernt werdne soll
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void removeIntendedWorkload(final int pYear, final Subject pSubject)
			throws IllegalArgumentException;
	
	/**
	 * Ein Gebaeude soll editiert werden koennen.
	 * 
	 * @param pBuilding
	 * 			das Gebaeude was editiert werden soll.
	 * @param distance
	 * 			die Distanz des Gebaeudes.
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void editBuilding(final String pBuilding, final int distance)
			throws IllegalArgumentException;
	
	/**
	 * Loescht das Gebaeude aus der Map.
	 * 
	 * @param pBuilding
	 * 			das Gebaeude, dass gelöscht werden soll.
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void  removeBuilding(final String pBuilding)
			throws IllegalArgumentException;
	
	/**
	 * Eine Kategorie soll hinzugefuegt werden.
	 * 
	 * @param pCategory
	 * 			die Kategorie die hinzugefuegt werden soll.
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void addCategory(Category pCategory)
			throws IllegalArgumentException;
	
	/**
	 * Eine Kategorie die entfernt werden soll.
	 * 
	 * @param pCategory
	 * 			die Kategorie die entfernt werden soll.
	 * @throws IllegalArgumentException
	 * 			wenn {@code null} uebergeben wird.
	 */
	public void removeCategory(Category pCategory)
			throws IllegalArgumentException;
}