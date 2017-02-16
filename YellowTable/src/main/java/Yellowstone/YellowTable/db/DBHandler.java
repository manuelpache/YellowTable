package Yellowstone.YellowTable.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import Yellowstone.YellowTable.exceptions.DatabaseException;
import Yellowstone.YellowTable.logic.components.*;
import Yellowstone.YellowTable.logic.components.Class;
import Yellowstone.YellowTable.warnings.DistanceWarning;
import Yellowstone.YellowTable.warnings.MultipleReservationWarning;
import Yellowstone.YellowTable.warnings.OverloadWarning;
import Yellowstone.YellowTable.warnings.Warning;

/**
 * Diese Klasse stellt die Verbindung zur Datenbank für das gesamte Projekt zur Verfügung.
 * Die Klasse bietet ausschließlich statische Methoden an und kann nicht instanziiert werden.
 * @author prohleder
 *
 */
public class DBHandler {

	
	/**
	 * Logger dieser Klasse zum Protokollieren von Ereignissen und
	 * Informationen.
	 */
	private static final Logger LOGGER = Logger.getLogger(DBHandler.class.getName());

	/*
	 * Statischer Initialisierer. Wird vor der ersten Verwendung dieser Klasse
	 * ausgeführt.
	 */
	static {
		try {
			LOGGER.debug("Creating new persistence manager");
			EntityManagerFactory factory;
			factory = Persistence.createEntityManagerFactory("stundenplan");
			entityManager = factory.createEntityManager();
		} catch (Exception e) {
			LOGGER.error("Exception while creating new data object!", e);
			throw new PersistenceException(
					"Could not initialize persistence component: "
							+ e.getMessage());
		}

	}
	
	private static EntityManager entityManager;

	/**
	 * Instanziierung verhindern.
	 */
	private DBHandler(){
	}
		
	/*
	 * Backups
	 */
	/**
	 * Diese Methode erstellt ein Backup der Datenbank an dem übergebenen Pfad.
	 * @param pPath Pfad zur Backup-Erstellung
	 * @throws DatabaseException 
	 */
	public static void createBackup(final String pPath) throws DatabaseException{
		if(pPath != null){
			try{
				StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SYSCS_UTIL.SYSCS_BACKUP_DATABASE");
				query.registerStoredProcedureParameter(1, java.lang.String.class, ParameterMode.IN);
				query.setParameter(1, pPath);
				query.execute();
				LOGGER.info("Backup wurde erfolgreich erstellt.");
			}
			catch(Exception e){
				LOGGER.error("Fehler beim Erstellen des Backups!", e);
				throw new DatabaseException("Fehler beim Erstellen des Backups am Pfad: " + pPath);
			}
		}
	}
	
	/**
	 * Diese Methode stellt ein Backup der Datenbank an dem übergebenen Pfad wieder her.
	 * @param pPath	OrdnerName des Backups, also der Timestamp-Ordner
	 * @pBackupArt	1:	Initial, 2:	Backup, 3:  Projekt
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unused")
	public static boolean restoreBackup(final String pPath, final int pBackupArt) throws DatabaseException{
		Connection con;
		boolean restored = false;
		boolean shutdown = false;
		boolean reconnected = false;
		String restorePath = "";
		if(pPath != null){	
			String dbpath = "Datenbank/Yellowtable.db";
			
			if(pBackupArt == 1){//Initial
				restorePath = pPath+"/Yellowtable.db";
			}
			if(pBackupArt == 2){//Backup laden
				restorePath = "Backups/"+pPath+"/Yellowtable.db";
			}
			if(pBackupArt == 3){//Projekt laden
				restorePath = "Projekte/"+pPath+"/Yellowtable.db";
			}
			
			try {
				System.out.println("### DBHAndler : "+restorePath);
					//shutdown
					con = DriverManager.getConnection("jdbc:derby:"+dbpath+";shutdown=true", "yellow", "yellow");
					con.close();
			}
			//SQLException bei Erfolg
			catch(SQLException e){
				if (e.getSQLState().equals("08006")){
					shutdown= true; 
					LOGGER.info("Datenbank wurde erfolgreich Heruntergefahren.");
				}
				else{
					LOGGER.error("Fehler beim Herunterfahren der Datenbank.");
					throw new DatabaseException("Fehler beim Herunterfahren der Datenbank.");
				}
				try{
					//Restore
					con = DriverManager.getConnection("jdbc:derby:"+dbpath+";restoreFrom="+ restorePath, "yellow",  "yellow");		
					restored = true;
					LOGGER.info("Datenbank wurde erfolgreich Wiederhergestellt.");
				}
				catch (SQLException e1) {
					LOGGER.error("Fehler beim Wiederherstellen der Datenbank.", e1);
					throw new DatabaseException("Fehler beim Wiederherstellen der Datenbank.");	
				}
				//GBCollector räumt die Verbindung auf
				System.gc();
				try {
					//Wiederverbinden
					con = DriverManager.getConnection("jdbc:derby:"+dbpath+";create=true", "yellow",  "yellow");
					LOGGER.info("Datenbank wurde erfolgreich Wiederverbunden nach Wiederherstellung.");
				} 
				catch (SQLException e2) {
					LOGGER.error("Fehler beim Verbinden mit der wiederhergestellten Datenbank.", e2);
					throw new DatabaseException("Fehler beim Verbinden mit der wiederhergestellten Datenbank.");	
				}
				reconnected = true;
			}
		}
		return reconnected;
	}
		
	/**
	 * Setzt die Datenbank auf den Initialzustand zurück.
	 * Lädt quasi ein initiales Backup.
	 */
	public static void resetDatabase(){
		try {
			restoreBackup(Config.DBInitPath, 1);
		} catch (DatabaseException e) {
			LOGGER.error("Fehler beim Zurücksetzen der Datenbank auf den Initialzustand.", e);
		}
	}
	
	/*
	 * Backups Ende
	 */
	

	/*
	 * Liste von allen dieses Typs laden:
	 */
	
	/**
	 * Diese Methode liefert alle Units aus der Datenbank zurück.
	 * @return	Collection von allen Units.
	 * @throws DatabaseException
	 */
	public static Collection<Unit> getAll() throws DatabaseException{
		Collection<Unit> collection = new ArrayList<Unit>();
		Collection<TeachingUnit> collTeach = getAllTeachingUnits();
		Collection<MeetingUnit> collMeet = getAllMeetingUnits();
		Collection<ExternUnit> collExt = getAllExternUnits();
		Collection<BreakUnit> collBreakU = getAllBreakUnits();
		Collection<Break> collBreak = getAllBreaks();
		
		collection.addAll(collTeach);
		collection.addAll(collMeet);
		collection.addAll(collExt);
		collection.addAll(collBreakU);
		collection.addAll(collBreak);
		return collection;
	}
	
	/**
	 * Ruft eine Collection aller in der DB gespeicherten Lehrer ab.
	 * @return Collection aller gespeicherten Lehrer
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Teacher> getAllTeachers() throws DatabaseException {
			try {
				final Query query = entityManager
						.createQuery("SELECT t FROM Teacher t");
				return (Collection<Teacher>) query.getResultList();
			} catch (Exception e) {
				LOGGER.error("DB: Fehler beim Abfragen aller Lehrer!", e);
				throw new DatabaseException("Fehler beim Abfragen aller Lehrer. "
						+ e.getMessage());
			}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Päd. Mitarbeiter ab.
	 * @return Collection aller gespeicherten Päd. Mitarbeiter
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<EducEmployee> getAllEducEmployees() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM EducEmployee t");
			return (Collection<EducEmployee>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Päd. Mitarbeiter!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Päd. Mitarbeiter. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Pausen ab.
	 * @return Collection aller gespeicherten Pausen
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Break> getAllBreaks() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM Break t");
			return (Collection<Break>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Pausen-Objekte!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Pausen-Objekte. "
					+ e.getMessage());
		}
	}
	
	/**
	 * Ruft eine Collection aller in der DB gespeicherten BreakUnits ab.
	 * @return Collection aller gespeicherten BreakUnits
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<BreakUnit> getAllBreakUnits() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM BreakUnit t");
			return (Collection<BreakUnit>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller BreakUnit-Objekte!", e);
			throw new DatabaseException("Fehler beim Abfragen aller BreakUnit-Objekte. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Kategorien ab.
	 * @return Collection aller gespeicherten Kategorien
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Category> getAllCategories() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM Category t");
			return (Collection<Category>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Kategorien!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Kategorien. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Klassen ab.
	 * @return Collection aller gespeicherten KLassen
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class> getAllClasses() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM Class t");
			return (Collection<Class>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Klassen!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Klassen. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten externen Aktivitäten ab.
	 * @return Collection aller gespeicherten externen Aktivitäten
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ExternUnit> getAllExternUnits() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM ExternUnit t");
			return (Collection<ExternUnit>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller externen Aktivitäten!", e);
			throw new DatabaseException("Fehler beim Abfragen aller externen Aktivitäten. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Meetings ab.
	 * @return Collection aller gespeicherten Meetings
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<MeetingUnit> getAllMeetingUnits() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM MeetingUnit t");
			return (Collection<MeetingUnit>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Meetings!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Meetings. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Unterrichts-Einheiten ab.
	 * @return Collection aller gespeicherten Unterrichts-Einheiten
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<TeachingUnit> getAllTeachingUnits() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM TeachingUnit t");
			return (Collection<TeachingUnit>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Unterichts-Einheiten!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Unterichts-Einheiten. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Räume ab.
	 * @return Collection aller gespeicherten Räume
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Room> getAllRooms() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM Room t");
			return (Collection<Room>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Räume!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Räumen. "
					+ e.getMessage());
		}
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Fächer ab.
	 * @return Collection aller gespeicherten Fächer
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Subject> getAllSubjects() throws DatabaseException{
		try {
			final Query query = entityManager
					.createQuery("SELECT t FROM Subject t");
			return (Collection<Subject>) query.getResultList();
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Fächer!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Fächer. "
					+ e.getMessage());
		}
	}

	/*
	 * Arbeiten nicht direkt auf DB - Attribute von Config
	 */
	public static Collection<String> getAllBuildings() throws DatabaseException{
		Collection<String> collString = new ArrayList<String>();
		try{
			for(Map.Entry<String, Integer> entry: getConfig().getBuildings().entrySet()){
				collString.add(entry.getKey());
			};
			return collString;
		}
		catch(Exception e){
			LOGGER.error("DB: Fehler bei Abruf aller Geäude.");
			throw new DatabaseException("Fehler bei Abruf aller Gebäude.");
			
		}
	}
	
	/**
	 * Gibt die Jährgänge der Config als Collection zurück
	 * @return Collection der Jahrgänge.
	 * @throws DatabaseException
	 */
	public static Collection<Years> getAllYears() throws DatabaseException{
		return getConfig().getYears();
	}

	/**
	 * Ruft eine Collection aller in der DB gespeicherten Warnungen ab.
	 * @return Collection aller gespeicherten Warnungen
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Warning> getAllWarnings() throws DatabaseException{
		try {
			Collection<Warning> collwarning = new ArrayList<Warning>();
			
			final Query query = entityManager
					.createQuery("SELECT t FROM Warning t");
			collwarning = query.getResultList();
			
			if(collwarning.size() > 0){
				return collwarning;
			}
			return null;			
		} catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Warnungen!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Warnungen. "
					+ e.getMessage());
		}
	}
	
	
	// Spezielle Abfragen aus der Datenbank anhand von selbst erstellten Queries.
	
	/**
	 * Gibt zurück, wieviele Stunden eine Klasse bereits in einem Fach unterrichtet wird.
	 * @param pSubject	Fach
	 * @param pClass	Klasse
	 * @return	double-Wert an Std
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerSubjectPerClass(final Subject pSubject, final Class pClass) throws DatabaseException{
		if(pSubject == null && pClass == null){
			throw new IllegalArgumentException("leere Parameter-Eingabe");
		}
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.timeslot ti JOIN u.subjects su JOIN u.classes cl "
							+ "WHERE ?1 IN (su.title) AND ?2 IN (cl.name)");		
			query.setParameter(1, pSubject.getTitle());
			query.setParameter(2, pClass.getName());
			Collection<Long> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer min = getConfig().getMinPerHourTeacher();
			if(intarray[0] != null){
				Long l = (long)intarray[0];
				Double d = l.doubleValue() / min.doubleValue();	
				return d;		
			}
			else{
				return 0.0;
			}
		}	
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Stunden pro Fach pro Klasse!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Stunden pro Fach pro Klasse. "
				+ e.getMessage());
		}
	}
	
	/**
	 * Gibt zurück, wieviele Stunden ein Lehrer bereits in einem Fach unterrichtet.
	 * @param pSubject	Fach
	 * @param pTeacher	Lehrer
	 * @return	long-Wert an Std
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerSubjectPerTeacher(final Subject pSubject, final Teacher pTeacher) throws DatabaseException{
		if(pSubject == null && pTeacher == null){
			throw new IllegalArgumentException("leere Parameter-Eingabe");
		}
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.timeslot ti JOIN u.subjects su JOIN u.persons pe "
							+ "WHERE ?1 IN (su.title) AND pe.person.shortName = ?2");		
			query.setParameter(1, pSubject.getTitle());
			query.setParameter(2, pTeacher.getShortName());
			Collection<Long> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer minutes = getConfig().getMinPerHourTeacher();
			Long l = (long)intarray[0];
			Double d = l.doubleValue() / minutes.doubleValue();
			return d;
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Stunden pro Fach pro Lehrer!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Stunden pro Fach pro Lehrer. "
				+ e.getMessage());
		}
	}

	/**
	 * Gibt die Stundenanzahl eines Lehrers zurück, die er/sie in einem Fach in einer Klasse unterrichtet.
	 * @param pSubject	Fach
	 * @param pTeacher	Lehrer
	 * @param pClass	Klasse
	 * @return	Stundenanzahl in long
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerSubjectPerTeacherPerClass(final Subject pSubject, final Teacher pTeacher, final Class pClass) throws DatabaseException{
		if(pSubject == null || pTeacher == null || pClass == null){
			throw new IllegalArgumentException("Fehlerhafte leere Parameter-Eingabe");
		}
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.subjects su JOIN u.persons pe JOIN pe.timeslot ti JOIN u.classes cl "
							+ "WHERE ?1 IN (su.title) AND pe.person.shortName = ?2 AND cl.name = ?3");
			query.setParameter(1, pSubject.getTitle());
			query.setParameter(2, pTeacher.getShortName());
			query.setParameter(3, pClass.getName());
			Collection<Integer> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer minutes = getConfig().getMinPerHourTeacher();
			if(intarray[0] != null){
				Long l = (long)intarray[0];
				Double d = l.doubleValue() / minutes.doubleValue();
				return d;
			}
			return 0;
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Stunden pro Fach pro Klasse pro Lehrer!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Stunden pro Klasse pro Fach pro Lehrer. "
				+ e.getMessage());
		}
	}
	
	/**
	 * Gibt die Stundenanzahl eines Päd. Mitarbeiter zurück, die er/sie in einem Fach in einer Klasse unterrichtet.
	 * @param pSubject	Fach
	 * @param pEduc	Päd. Mitarbeiter
	 * @param pClass	Klasse
	 * @return	Stundenanzahl in long
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerSubjectPerEducPerClass(final Subject pSubject, final EducEmployee pEduc, final Class pClass) throws DatabaseException{
		if(pSubject == null || pEduc == null || pClass == null){
			throw new IllegalArgumentException("Fehlerhafte leere Parameter-Eingabe");
		}
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.subjects su JOIN u.persons pe JOIN pe.timeslot ti JOIN u.classes cl "
							+ "WHERE ?1 IN (su.title) AND pe.person.shortName = ?2 AND cl.name = ?3");
			query.setParameter(1, pSubject.getTitle());
			query.setParameter(2, pEduc.getShortName());
			query.setParameter(3, pClass.getName());
			Collection<Integer> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer minutes = getConfig().getMinPerHourEduc();
			if(intarray[0] != null){
				Long l = (long)intarray[0];
				Double d = l.doubleValue() / minutes.doubleValue();
				return d;
			}
			return 0;
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller Stunden pro Fach pro Klasse pro Päd. Mitarbeiter!", e);
			throw new DatabaseException("Fehler beim Abfragen aller Stunden pro Klasse pro Fach pro Päd. Mitarbeiter. "
				+ e.getMessage());
		}
	}
	
	
	/**
	 * Gibt alle momentan ausgeführen Ist-Stunden eines übergebenen Lehrers zurück.
	 * @param pTeacher	Lehrer
	 * @return	int 
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerTeacher(final Teacher pTeacher) throws DatabaseException{
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.persons pe JOIN u.timeslot ti WHERE pe.person.shortName = ?1");		
			query.setParameter(1, pTeacher.getShortName());
			Collection<Integer> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer minutes = getConfig().getMinPerHourTeacher();		
			if(intarray[0] != null){
				Long l = (long)intarray[0];
				Double d = l.doubleValue() / minutes.doubleValue();
				return d;
			}
			else{
				return 0;
			}
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller IST-Stunden pro Lehrer!", e);
			throw new DatabaseException("Fehler beim Abfragen aller IST-Stunden pro Lehrer. "
				+ e.getMessage());
		}
	}
	
	/**
	 * Gibt alle momentan ausgeführen Ist-Stunden eines übergebenen Päd. Mitarbeiters zurück.
	 * @param pEduc	Päd. Mitarbeiters
	 * @return	int 
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static double getHoursPerEduc(final EducEmployee pEduc) throws DatabaseException{
		try{
			final Query query = entityManager
					.createQuery("SELECT SUM(ti.duration) FROM TeachingUnit u JOIN u.persons pe JOIN u.timeslot ti WHERE pe.person.shortName = ?1");		
			query.setParameter(1, pEduc.getShortName());
			Collection<Integer> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			Integer minutes = getConfig().getMinPerHourEduc();		
			if(intarray[0] != null){
				Long l = (long)intarray[0];
				Double d = l.doubleValue() / minutes.doubleValue();
				return d;
			}
			else{
				return 0;
			}
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen aller IST-Stunden pro Päd. Mitarbeiter!", e);
			throw new DatabaseException("Fehler beim Abfragen aller IST-Stunden pro Päd. Mitarbeiter. "
				+ e.getMessage());
		}
	}
	
	/**
	 * Die Methode gibt für einen Lehrer und ein FAch eine Map zurück, wieviele Stunden er in dieser Klasse unterrichtet.
	 * @param pTeacher	der Lehrer
	 * @param pSubject	das Fach
	 * @return	Die Map mit der Zuordnung: Klasse, Std
	 * @throws DatabaseException
	 */
	public static Map<Class, Double> getClassHoursPerSubjectPerTeacher(final Teacher pTeacher, final Subject pSubject) throws DatabaseException{
		try{
			final TypedQuery<Object[]> query = entityManager
					.createQuery("SELECT c, SUM(ti.duration) FROM TeachingUnit u JOIN u.persons pe JOIN u.timeslot ti JOIN u.classes c "
							+ "WHERE pe.person.shortName = ?1 GROUP BY c", Object[].class);		
			query.setParameter(1, pTeacher.getShortName());
			Collection<Object[]> collectionUnits = query.getResultList();
			Map<Class, Double> mapUnits = new HashMap<Class, Double>(collectionUnits.size());
			Integer minutes = getConfig().getMinPerHourTeacher();		
			for (Object[] result : collectionUnits){
				Long v = (long)result[1];
				Double d = v.doubleValue() / minutes.doubleValue();
				mapUnits.put((Class)result[0], d);
			}
			return mapUnits;
		}
		catch (Exception e) {
			LOGGER.error("DB: Fehler beim Abfragen der Stunden in einer Klasse pro Lehrer!", e);
			throw new DatabaseException("Fehler beim Abfragen der Stunden in einer Klasse pro Lehrer!"
				+ e.getMessage());
		}
		
	}
	
	/**
	 * Gibt einen Integer-Wert an den vorgesehenen Unterrichtsstunden eines Faches pro Jahrgang.
	 * @param pSubject	Fach
	 * @param pYear	Jahrgang
	 * @return	Zahlwert
	 * @throws DatabaseException 
	 */
	public static int getIntendedWorkloadPerSubjectPerYear(final Subject pSubject, final int pYear) throws DatabaseException{
		if(pSubject == null || pYear == 0){
			LOGGER.error("Fehlerhafte Parametereingaben");
			throw new IllegalArgumentException("Das Fach oder der Jahrgang besitzt einen Fehlerhaften Eingabewert!");
		}
		else{
			if(getConfig().getIntendedWorkload(pYear).containsKey(pSubject)){
				try{
					return getConfig().getIntendedWorkload(pYear).get(pSubject);
				}
				catch(Exception e){
					LOGGER.error("Fehler beim Abrufen der Soll-Stunden eines Faches für einen Jahrgang.");
					throw new DatabaseException("Fehler beim Abrufen der Soll-Stunden eines Faches für einen Jahrgang.");
				}	
			}
			else{
				return 0;
			}
			
		}
	}
	
	/**
	 * Gibt einen Integer-Wert an den vorgesehenen Unterrichtsstunden eines Faches pro Jahrgang.
	 * @param pSubject	Fach
	 * @param pYear	Jahrgang
	 * @return	Zahlwert
	 */
	@SuppressWarnings("unchecked")
	public static int getIntendedWorkloadPerSubjectPerClass(final Subject pSubject, final Class pClass){
		if(pSubject == null || pClass == null){
			LOGGER.error("Fehlerhafte Parametereingaben");
			throw new IllegalArgumentException("Das Fach oder die Klasse besitzt einen Fehlerhaften Eingabewert!");
		}
		else{
			final Query query = entityManager
					.createQuery("SELECT VALUE(i) FROM Class cl JOIN cl.intendedWorkload i WHERE cl.name = ?1 AND KEY(i) = ?2");		
			query.setParameter(1, pClass.getName());
			query.setParameter(2, pSubject);
			Collection<Integer> collectionUnits = query.getResultList();
			Object[] intarray = collectionUnits.toArray();
			if(collectionUnits.size() != 0){
				return (int)intarray[0];
			}
			else{
				if(getConfig().getIntendedWorkload(pClass.getYear()).containsKey(pSubject)){
					return getConfig().getIntendedWorkload(pClass.getYear()).get(pSubject);
				}
				else{
					return 0;
				}
			}
		}
	}
	
	
	/*
	 * Erste selektive Anfragen, trennen nach Klasse statt Object
	 */
	
	/**
	 * Diese Abfrage gibt für eine Person den gesamten Stundenplan in Form einer Collection
	 * von Units zurück.
	 * 
	 * @param pPerson Person-Objekt, dessen Stundenplan gewünscht wird
	 * @return	Collection mit Stundenplan-Objekten
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Unit> getWeekTableByPerson(final Person pPerson) {
		if(pPerson == null){
			throw new IllegalArgumentException("Leeres Personen Parameter Objekt");
		}
		Collection<Unit> collectionUnits;
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.persons ob WHERE ob.person.shortName = ?1");		
		query.setParameter(1, pPerson.getShortName());
		collectionUnits = query.getResultList();
		
		//Abfrage nach MeetingUnitTeacher
		final Query query2 = entityManager
				.createQuery("SELECT u FROM MeetingUnit u, Teacher t WHERE u.years = t.yearTeam AND t.shortName = ?1");	
		query2.setParameter(1, pPerson.getShortName());
		collectionUnits.addAll(query2.getResultList());
		return collectionUnits;
	}
	
	/**
	 * Diese Abfrage gibt für eine Klasse den gesamten Stundenplan in Form einer Collection
	 * von Units zurück.
	 * @param pClass Klassen-Objekt, dessen Stundenplan gewünscht wird
	 * @return	Collection mit Stundenplan-Objekten
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Unit> getWeekTableByClass(final Class pClass) {
		if(pClass == null){
			throw new IllegalArgumentException("Leeres Klassen Parameter Objekt");
		}
		Collection<Unit> collectionUnits;
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");		
		query.setParameter(1, pClass.getName());
		collectionUnits = query.getResultList();
		
		//Abfrage nach ExternUnit
		final Query query2 = entityManager
				.createQuery("SELECT u FROM ExternUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");	
		query2.setParameter(1, pClass.getName());
		collectionUnits.addAll(query2.getResultList());
		
		//Abfrage nach Break
		final Query query3b = entityManager
				.createQuery("SELECT u FROM Break u WHERE ?1 IN (u.years)");	
		query3b.setParameter(1, pClass.getYear());
		collectionUnits.addAll(query3b.getResultList());
		
		//Abfrage nach BreakUnit
		final Query query4 = entityManager
				.createQuery("SELECT u FROM BreakUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");	
		query4.setParameter(1, pClass.getName());
		collectionUnits.addAll(query4.getResultList());
		
		return collectionUnits;
	}

	/**
	 * Diese Abfrage gibt für einen Raum den gesamten Stundenplan in Form einer Collection
	 * von Units zurück.
	 * @param pRoom Person-Objekt, desser Stundenplan gewünscht wird
	 * @return	Collection mit Stundenplan-Objekten
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Unit> getWeekTableByRoom(final Room pRoom) {
		if(pRoom == null){
			throw new IllegalArgumentException("Leeres Raum Parameter Objekt");
		}
		Collection<Unit> collectionUnits;
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.rooms ro WHERE ro.name = ?1");		
		query.setParameter(1, pRoom.getName());
		collectionUnits = query.getResultList();
		
		//Abfrage nach ExternUnit
		final Query query2 = entityManager
				.createQuery("SELECT u FROM ExternUnit u JOIN u.rooms ro WHERE ?1 IN (ro.name)");	
		query2.setParameter(1, pRoom.getName());
		collectionUnits.addAll(query2.getResultList());
	
		//Abfrage nach Break
		final Query query3 = entityManager
				.createQuery("SELECT u FROM Break u JOIN u.rooms ro WHERE ?1 IN (ro.name)");	
		query3.setParameter(1, pRoom.getName());
		collectionUnits.addAll(query3.getResultList());
		
		//Abfrage nach BreakUnit
		final Query query4 = entityManager
				.createQuery("SELECT u FROM BreakUnit u JOIN u.rooms ro WHERE ?1 IN (ro.name)");	
		query4.setParameter(1, pRoom.getName());
		collectionUnits.addAll(query4.getResultList());
		
		//Abfrage nach MeetingUnit
		final Query query5 = entityManager
				.createQuery("SELECT u FROM MeetingUnit u");
		Collection<Unit> collectionMeeting = query5.getResultList();
		for(Unit m : collectionMeeting){
			if(m.getRooms().contains(pRoom)){
				collectionUnits.add(m);
			}
		}
				
		return collectionUnits;
	}
	
	/**
	 * Die Methode liefern eine Collection von Units zurück, in denen ein übergebener Lehrer an einem übergebenen
	 * Wochentag beteiligt ist.
	 * 
	 * @param pWeekday	gewünschter Wochentag
	 * @param pTeacher	gewünschter Lehrer
	 * @return	Collection<Unit> mit Einheiten
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Unit> getDayTablePerTeacher(final WEEKDAYS pWeekday, final Teacher pTeacher){
		if(pWeekday == null && pTeacher == null){
			throw new IllegalArgumentException("Leeres Wochentag oder Lehrer Parameter Objekt");
		}
		Collection<Unit> collectionUnits;
		//Abfrage nach TeachingUnit
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.timeslot ti JOIN u.persons ob WHERE ti.weekday = ?1 AND ob.person.shortName = ?2");		
		query.setParameter(1, pWeekday);
		query.setParameter(2, pTeacher.getShortName());		
		collectionUnits = query.getResultList();
		
		//Abfrage nach MeetingUnit
		final Query query2 = entityManager
				.createQuery("SELECT u FROM MeetingUnit u JOIN u.timeslot ti JOIN u.persons ob WHERE ti.weekday = ?1 AND ?2 IN (ob.shortName)");	
		query2.setParameter(1, pWeekday);
		query2.setParameter(2, pTeacher.getShortName());	
		collectionUnits.addAll(query2.getResultList());
	
		return collectionUnits;
	}
	
	/**
	 * Die Methode gibt alle Klassen zurück, in der sich eine Person an einem bestimmten Tag zu einer bestimmten Start und Endzeit befindet.
	 * @param pPerson	Person
	 * @param pWeekday	Wochentag
	 * @return	Collection von Klassen sortiert nach Uhrzeit
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class> getClassesPerPersonPerTime(final Person pPerson, final WEEKDAYS pWeekday, 
			final int pStartHour, final int pStartMinute, final int pEndHour, final int pEndMinute){
		Collection<Class> collectionClass = new ArrayList<Class>();
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.timeslot ti JOIN u.persons ob JOIN u.classes c WHERE ti.weekday = ?1 AND ob.person.shortName = ?2 "
						+ "ORDER BY ti.startHour, ti.startMinute");		
		query.setParameter(1, pWeekday);
		query.setParameter(2, pPerson.getShortName());	
		Collection<TeachingUnit> collectionUnit = query.getResultList();
		for(TeachingUnit c:collectionUnit){
			int dur = Timeslot.getDurationByStartAndEndtime(Timeslot.addStringHour(pStartHour), Timeslot.addStringMinute(pStartMinute), Timeslot.addStringHour(pEndHour), Timeslot.addStringMinute(pEndMinute));
			Timeslot t = new Timeslot(dur, c.getTimeslot().getDay(), Timeslot.addStringHour(pStartHour), Timeslot.addStringMinute(pStartMinute));
			c.getStarttime();
			c.getTimeslot().setEndTime();
			if(c.getTimeslot().containsConflict(t)){
				collectionClass.addAll(c.getClasses());
			}
		}
		return collectionClass;
	}

	/**
	 * Gibt eine Collection von Klassen zurück, in denen dieser Lehrer unterrichtet.
	 * @param pTeacher	Lehrer
	 * @return Collection von Klassen
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class> getClassesByTeacher(final Teacher pTeacher) throws DatabaseException{
		if(pTeacher == null){
			throw new IllegalArgumentException("Leeres Parameter-Objekt");
		}
		try{
			final Query query = entityManager
				.createQuery("SELECT DISTINCT c FROM TeachingUnit u JOIN u.persons pe JOIN u.classes c WHERE pe.person.shortName = ?1");	
			query.setParameter(1, pTeacher.getShortName());
			Collection<Class> collectionClass = query.getResultList();
			return collectionClass;
		}
		catch(Exception e){
			LOGGER.error("Fehler beim Abrufen der Klassen eines Lehrers.");
			throw new DatabaseException("Fehler beim Abrufen der Klassen eines Lehrers:" + e);
		}
	}
	
	/**
	 * Gibt eine Collection von Klassen zurück, in denen dieser Päd. Mitarbeiter unterrichtet.
	 * @param pEduc	Päd. Mitarbeiter
	 * @return Collection von Klassen
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class> getClassesByEduc(final EducEmployee pEduc) throws DatabaseException{
		if(pEduc == null){
			throw new IllegalArgumentException("Leeres Parameter-Objekt");
		}
		try{
			final Query query = entityManager
				.createQuery("SELECT DISTINCT c FROM TeachingUnit u JOIN u.persons pe JOIN u.classes c WHERE pe.person.shortName = ?1");	
			query.setParameter(1, pEduc.getShortName());
			Collection<Class> collectionClass = query.getResultList();
			return collectionClass;
		}
		catch(Exception e){
			LOGGER.error("Fehler beim Abrufen der Klassen eines Päd. Mitarbeiter.");
			throw new DatabaseException("Fehler beim Abrufen der Klassen eines Päd. Mitarbeiter:" + e);
		}
	}
	
	/**
	 * Diese Methode liefert alle Klassen zurück, die von einem Jahrgang sind.
	 * @param pYear	Jahrgang als int
	 * @return	Collection von Klassen
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class> getClassesByYear(final int pYear) throws DatabaseException{
		try{
			final Query query = entityManager
				.createQuery("SELECT c FROM Class c WHERE c.years = ?1");		
			query.setParameter(1, pYear);
			Collection<Class> collectionClass = query.getResultList();
			return collectionClass;
		}
		catch(Exception e){
			LOGGER.error("Für diesen Jahrgang existieren keine Klassen.");
			throw new DatabaseException("Für diesen Jahrgang existieren keine Klassen.");
		}
	}

	/**
	 * Speziell, da pro Datenbank einmalig.
	 */
	@SuppressWarnings("unchecked")
	public static Config getConfig(){
		final Query query = entityManager
				.createQuery("SELECT c FROM Config c");
		Collection<Config> collectionConfig = query.getResultList();
		if(collectionConfig.size() == 0 || collectionConfig.toArray()[0] == null){
			return null;
		}
		return (Config)collectionConfig.toArray()[0];
	}
	
	/*
	 *	Methoden zur Überprüfen, ob Objekte zu einem Bestimmten Timeslot mehrfach in einer Unit vorkommen.
	 * boolean return true, sofern ein Objekt zu diesem Zeitslot mind. 2 fach belegt ist.
	 * boolean return false, sofern ein Objekt zu diesem Zeitslot 0 - 1 fach belegt ist.
	 */
	
	/**
	 * Diese Methode überprüft in der DB, ob es für eine beliebige Person zu einem bestimmten Timeslot einen Zeitkonflikt gibt.
	 * Es wird überprüft, ob die Person zu einem Timeslot 2 Aktivitäten besitzt:
	 * (TeachingUnit, MeetingUnit), 
	 * @param pPerson	zu überprüfende Person
	 * @param pTimeslot	zu überprüfender Zeitslot
	 * @return	boolean, ob aktuell ein Zeitkonflikt existiert
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkConflictPerson(final Person pPerson, final Timeslot pTimeslot){
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.persons ob WHERE ob.person.shortName = ?1");		
		query.setParameter(1, pPerson.getShortName());
		Collection<TeachingUnit> collectionTeaching = query.getResultList();
		if(collectionTeaching.size() > 1){
			int counter = 0;
			for(TeachingUnit t:collectionTeaching){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		
		//Abfrage nach MeetingUnits
		final Query query2 = entityManager
				.createQuery("SELECT u FROM MeetingUnit u JOIN u.persons ob WHERE ?1 IN (ob.shortName)");	
		query2.setParameter(1, pPerson.getShortName());
		Collection<MeetingUnit> collectionMeeting = query2.getResultList();
		if(collectionMeeting.size() > 1){
			int counter = 0;
			for(MeetingUnit t:collectionMeeting){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Diese Methode überprüft in der DB, ob es für einen Raum zu einem bestimmten Timeslot einen Zeitkonflikt gibt.
	 * Es wird überprüft, ob der Raum, zu einem Timeslot 2 Aktivitäten besitzt:
	 * (TeachingUnit, ExternUnit, Break, BreakUnit, MeetingUnits), 
	 * @param pRoom	zu überprüfender Raum
	 * @param pTimeslot	zu überprüfender Zeitslot
	 * @return	boolean, ob aktuell ein Zeitkonflikt existiert
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkConflictRoom(final Room pRoom, final Timeslot pTimeslot) throws DatabaseException{
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.rooms ob WHERE ?1 IN (ob.name)");		
		query.setParameter(1, pRoom.getName());
		Collection<TeachingUnit> collectionTeaching = query.getResultList();
		if(collectionTeaching.size() > 1){
			int counter = 0;
			for(TeachingUnit t:collectionTeaching){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		//Abfrage nach ExternUnits
		final Query query2 = entityManager
				.createQuery("SELECT u FROM ExternUnit u JOIN u.rooms ob WHERE ?1 IN (ob.name)");	
		query2.setParameter(1, pRoom.getName());
		Collection<ExternUnit> collectionExtern = query2.getResultList();
		if(collectionExtern.size() > 1){
			int counter = 0;
			for(ExternUnit t:collectionExtern){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		//Abfrage nach Break
		final Query query3 = entityManager
				.createQuery("SELECT u FROM Break u JOIN u.rooms ob WHERE ?1 IN (ob.name)");	
		query3.setParameter(1, pRoom.getName());
		Collection<Break> collectionBreak = query3.getResultList();
		if(collectionBreak.size() > 1){
			int counter = 0;
			for(Break t:collectionBreak){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		//Abfrage nach BreakUnits
		final Query query4 = entityManager
				.createQuery("SELECT u FROM BreakUnit u JOIN u.rooms ob WHERE ?1 IN (ob.name)");	
		query4.setParameter(1, pRoom.getName());
		Collection<BreakUnit> collectionBreakUnit = query4.getResultList();
		if(collectionBreakUnit.size() > 1){
			int counter = 0;
			for(BreakUnit t:collectionBreakUnit){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		
		//Abfrage nach MeetingUnits
		final Query query5 = entityManager
				.createQuery("SELECT u FROM MeetingUnit u");	
		Collection<MeetingUnit> collectionMeeting = query5.getResultList();
		int counter = 0;
		for(MeetingUnit m : collectionMeeting){
			if(m.getRooms().contains(pRoom)){
				if(m.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
		}
		if(counter==2){
			return true;
		}			
		return false;
	}
	
	/**
	 * Diese Methode überprüft in der DB, ob es für eine Klasse zu einem bestimmten Timeslot einen Zeitkonflikt gibt.
	 * Es wird überprüft, ob die Klasse, zu einem Timeslot 2 Aktivitäten besitzt:
	 * (TeachingUnit, ExternUnit, Break, BreakUnit), 
	 * @param pClass	zu überprüfende Klasse
	 * @param pTimeslot	zu überprüfender Zeitslot
	 * @return	boolean, ob aktuell ein Zeitkonflikt existiert
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkConflictClass(final Class pClass, final Timeslot pTimeslot){
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");		
		query.setParameter(1, pClass.getName());
		Collection<TeachingUnit> collectionTeaching = query.getResultList();
		if(collectionTeaching.size() > 1){
			int counter = 0;
			for(TeachingUnit t:collectionTeaching){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		//Abfrage nach ExternUnits
		final Query query2 = entityManager
				.createQuery("SELECT u FROM ExternUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");	
		query2.setParameter(1, pClass.getName());
		Collection<ExternUnit> collectionExtern = query2.getResultList();
		if(collectionExtern.size() > 1){
			int counter = 0;
			for(ExternUnit t:collectionExtern){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		
		//Abfrage nach Break
		final Query query3b = entityManager
				.createQuery("SELECT u FROM Break u WHERE ?1 IN (u.years)");	
		query3b.setParameter(1, pClass.getYear());
		Collection<Break> collectionBreak2 = query3b.getResultList();
		if(collectionBreak2.size() > 1){
			int counter = 0;
			for(Break t:collectionBreak2){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
				
				
		//Abfrage nach BreakUnits
		final Query query4 = entityManager
				.createQuery("SELECT u FROM BreakUnit u JOIN u.classes ob WHERE ?1 IN (ob.name)");	
		query4.setParameter(1, pClass.getName());
		Collection<Break> collectionBreakUnit = query4.getResultList();
		if(collectionBreakUnit.size() > 1){
			int counter = 0;
			for(Break t:collectionBreakUnit){
				if(t.getTimeslot().containsConflict(pTimeslot)){
					counter++;
				}
			}
			if(counter==2){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Methode überprüft, ob es eine Unit gibt, die nicht innerhalb des übergebenen TimeFrame-Timeslot liegt.
	 * @param pTimeslot
	 * @return boolean-Wert, ob es eine Unit gibt, oder nicht.
	 * @throws DatabaseException
	 * 
	 */
	public static boolean checkConflictTimeFrameByTimeslot(final Timeslot pTimeslot) throws DatabaseException{
		if(pTimeslot != null){
			Collection<Unit> collection = new ArrayList<Unit>();
			Collection<TeachingUnit> collTeach = getAllTeachingUnits();
			Collection<MeetingUnit> collMeet = getAllMeetingUnits();
			Collection<ExternUnit> collExt = getAllExternUnits();
			Collection<BreakUnit> collBreakU = getAllBreakUnits();
			Collection<Break> collBreak = getAllBreaks();
			
			collection.addAll(collTeach);
			collection.addAll(collMeet);
			collection.addAll(collExt);
			collection.addAll(collBreakU);
			collection.addAll(collBreak);
			
			
			for(Unit u:collection){
				if(!u.getTimeslot().isInTimeframe(pTimeslot)){
					return true;
				}
			}
			return false;
		}
		else{
			throw new DatabaseException("Fehlerhafter Parameter-Wert");
		}
	}
	
	/**
	 * Gibt zurück, ob ein Konflikt mit den zu planenden Wochentagen existiert.
	 * @return
	 * @throws DatabaseException 
	 */
	public static boolean checkConflictPlannedWeekdays() throws DatabaseException{
		Collection<WEEKDAYS> wd = getConfig().getPlannedDays();
		for(Unit u:getAll()){
			if(!wd.contains(u.getTimeslot().getDay())){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Überprüft, ob ein Konflikt bei den Wegzeiten für eine Person für einen Wochentag bestehen.
	 * @param pPerson	Person
	 * @param pWeekday	Wochentag
	 * @return	boolean, ob Konflikt besteht
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkTravelTimePerPersonPerDay(final Person pPerson, final WEEKDAYS pWeekday) throws DatabaseException{
		//TeachingUnit
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.persons ob JOIN u.timeslot t JOIN u.rooms r "
						+ "WHERE ob.person.shortName = ?1 AND t.weekday = ?2 ORDER BY t.startHour, t.startMinute");	
		query.setParameter(1, pPerson.getShortName());
		query.setParameter(2, pWeekday);
		Collection<TeachingUnit> collectionTeachingUnit = query.getResultList();
		
		Object[] arrayTU = collectionTeachingUnit.toArray();
		for(int i=1; i< arrayTU.length;i++){
			String buildingA = ((Room)((TeachingUnit)arrayTU[i-1]).getRooms().toArray()[0]).getBuilding();
			String buildingB = ((Room)((TeachingUnit)arrayTU[i]).getRooms().toArray()[0]).getBuilding();
			//Überprüfe ob Unterschiedliche Standorte
			if(!buildingB.equals(buildingA)){
				try {
					((TeachingUnit)arrayTU[i-1]).setTravelNeed(true);
					updateTeachingUnit((TeachingUnit)arrayTU[i-1]);
					//falls ja, dann hole Wegzeiten
					int distanceA = getConfig().getBuildings().get(buildingA);
					int distanceB = getConfig().getBuildings().get(buildingB);
					int sum = distanceA + distanceB;
					
					//neuen Timeslot erstellen.
					// Duration holen und zeit drauf addieren.
					Timeslot t1 = new Timeslot(((TeachingUnit)arrayTU[i-1]).getTimeslot().getDuration() + sum, 
							((TeachingUnit)arrayTU[i-1]).getTimeslot().getDay(), 
							Timeslot.addStringHour(((TeachingUnit)arrayTU[i-1]).getTimeslot().getStartHour()), 
							Timeslot.addStringHour(((TeachingUnit)arrayTU[i-1]).getTimeslot().getStartMinute()));
					t1.setStarttime();
					t1.setEndTime();
					//Überprüfe, ob ein Zeitkonflikt zwischen beiden Zeitslots existiert.
					if(((TeachingUnit)arrayTU[i]).getTimeslot().containsConflict(t1)){
						return true;
					}
				} catch (Exception e) {
					LOGGER.error("Fehler beim Abrufen der Konflikte der Wegzeiten eines Lehrers. ", e);
					throw new DatabaseException("Fehler beim Abrufen der Konflikte der Wegzeiten eines Lehrers. "
							+ e.getMessage());
				}
			}
		}
		return false;
	}
	
	/**
	 * Überprüft, ob ein Konflikt bei den Wegzeiten für eine Klasse für einen Wochentag bestehen.
	 * @param pClass	Klasse
	 * @param pWeekday	Wochentag
	 * @return	boolean, ob Konflikt besteht
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkTravelTimePerClassPerDay(final Class pPClass, final WEEKDAYS pWeekday) throws DatabaseException{
		//TeachingUnit
		final Query query = entityManager
				.createQuery("SELECT u FROM TeachingUnit u JOIN u.timeslot t JOIN u.rooms r JOIN u.classes cl "
						+ "WHERE ?1 IN (cl.name) AND t.weekday = ?2 ORDER BY t.startHour, t.startMinute");	
		query.setParameter(1, pPClass.getName());
		query.setParameter(2, pWeekday);
		Collection<TeachingUnit> collectionTeachingUnit = query.getResultList();
		
		Object[] arrayTU = collectionTeachingUnit.toArray();
		for(int i=1; i< arrayTU.length;i++){
			String buildingA = ((Room)((TeachingUnit)arrayTU[i-1]).getRooms().toArray()[0]).getBuilding();
			String buildingB = ((Room)((TeachingUnit)arrayTU[i]).getRooms().toArray()[0]).getBuilding();
			//Überprüfe ob Unterschiedliche Standorte
			if(!buildingB.equals(buildingA)){
				try {
					((TeachingUnit)arrayTU[i-1]).setTravelNeed(true);
					updateTeachingUnit((TeachingUnit)arrayTU[i-1]);
					//falls ja, dann hole Wegzeiten
					int distanceA = getConfig().getBuildings().get(buildingA);
					int distanceB = getConfig().getBuildings().get(buildingB);
					int sum = distanceA + distanceB;
					
					//neuen Timeslot erstellen.
					// Duration holen und zeit drauf addieren.
					Timeslot t1 = new Timeslot(((TeachingUnit)arrayTU[i-1]).getTimeslot().getDuration() + sum, 
							((TeachingUnit)arrayTU[i-1]).getTimeslot().getDay(), 
							Timeslot.addStringHour(((TeachingUnit)arrayTU[i-1]).getTimeslot().getStartHour()), 
							Timeslot.addStringHour(((TeachingUnit)arrayTU[i-1]).getTimeslot().getStartMinute()));
					t1.setStarttime();
					t1.setEndTime();
					//Überprüfe, ob ein Zeitkonflikt zwischen beiden Zeitslots existiert.
					if(((TeachingUnit)arrayTU[i]).getTimeslot().containsConflict(t1)){
						return true;
					}
				} catch (Exception e) {
					LOGGER.error("Fehler beim Abrufen der Konflikte der Wegzeiten eines Lehrers. ", e);
					throw new DatabaseException("Fehler beim Abrufen der Konflikte der Wegzeiten eines Lehrers. "
							+ e.getMessage());
				}
			}
		}
		return false;
	}
	
	/*
	 * Neuen Eintrag hinzufuegen
	 */
	
	/**
	 * Hinzufügen eines Lehrers zur Datenbank
	 * @param pTeacher das Lehrer-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addTeacher(final Teacher pTeacher) throws DatabaseException {
		if (pTeacher == null) {
			throw new IllegalArgumentException(
					"Leeres Teacher Objekt übergeben");
		}
		try {
			if(entityManager.find(Teacher.class, pTeacher.getShortName()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pTeacher);
				entityManager.getTransaction().commit();
				LOGGER.info("Lehrer wurde persistiert:" + pTeacher.getShortName());
			}
			else{
				LOGGER.error("Lehrer existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Lehrer: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Lehrer: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen eines EducEmployee zur Datenbank
	 * @param pEducEmployee das EducEmployee-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addEducEmployee(final EducEmployee pEducEmployee) throws DatabaseException {
		if (pEducEmployee == null) {
			throw new IllegalArgumentException(
					"Leeres Päd. Mitarbeiter Objekt übergeben");
		}
		try {
			if(entityManager.find(EducEmployee.class, pEducEmployee.getShortName()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pEducEmployee);
				entityManager.getTransaction().commit();
				LOGGER.info("Päd. Mitarbeiter wurde persistiert:" + pEducEmployee.getShortName());
			}
			else{
				LOGGER.info("Päd. Mitarbeiter existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Päd. Mitarbeiter: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Päd. Mitarbeiter: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen einer Pause zur Datenbank
	 * @param pBreak das Pausen-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addBreak(final Break pBreak) throws DatabaseException {
		if (pBreak == null) {
			throw new IllegalArgumentException(
					"Leeres Pausen Objekt übergeben");
		}
		try {
			if(getBreak(pBreak) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pBreak);
				entityManager.getTransaction().commit();
				LOGGER.info("Pause wurde persistiert:" + pBreak.getTitle());
			}
			else{
					LOGGER.info("Pause existiert bereits in DB.");
			}	
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Pause: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Pause: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Hinzufügen einer BreakUnit zur Datenbank
	 * @param pBreak das BreakUnit-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addBreakUnit(final BreakUnit pBreakUnit) throws DatabaseException {
		if (pBreakUnit == null) {
			throw new IllegalArgumentException(
					"Leeres Pausen Objekt übergeben");
		}
		try {
			if(getBreakUnit(pBreakUnit) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pBreakUnit);
				entityManager.getTransaction().commit();
				LOGGER.info("PausenObjekt wurde persistiert:" + pBreakUnit.toString());
			}
			else{
					LOGGER.info("PausenObjekt existiert bereits in DB.");
			}	
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von PausenObjekt: ", e);
			throw new DatabaseException("Fehler beim Persistieren von PausenObjekt: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen einer Kategorie zur Datenbank
	 * @param pCategory das Kategorie-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addCategory(final Category pCategory) throws DatabaseException {
		if (pCategory == null) {
			throw new IllegalArgumentException(
					"Leeres Kategorie Objekt übergeben");
		}
		try {
			if(entityManager.find(Category.class, pCategory.getName()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pCategory);
				entityManager.getTransaction().commit();
				LOGGER.info("Kategorie wurde persistiert:" + pCategory.getName());
			}
			else{
				LOGGER.info("Kategorie existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Kategorie: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Kategorie: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen einer Klasse zur Datenbank
	 * @param pClass das Klassen-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addClass(final Class pClass) throws DatabaseException {
		if (pClass == null) {
			throw new IllegalArgumentException(
					"Leeres Klassen Objekt übergeben");
		}
		try {
			if(entityManager.find(Class.class, pClass.getName()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pClass);
				entityManager.getTransaction().commit();
				LOGGER.info("Klasse wurde persistiert:" + pClass.getYear() + pClass.getName());
			}
			else{
				LOGGER.info("Klasse existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Klasse: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Klasse: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen einer externen Aktivität zur Datenbank
	 * @param pUnit das externe Aktivitäts Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addExternUnit(final ExternUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"Leeres externe Aktivität Objekt übergeben");
		}
		try {
			if(getExternUnit(pUnit) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pUnit);
				entityManager.getTransaction().commit();
				LOGGER.info("Externe Aktivität wurde persistiert:" + pUnit.getTitle());
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von externer Aktivität: ", e);
			throw new DatabaseException("Fehler beim Persistieren von externe Aktivität: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen eines Meetings zur Datenbank
	 * @param pUnit das Meeting-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addMeetingUnit(final MeetingUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"Leeres Meeting Objekt übergeben");
		}
		try {
			if(getMeetingUnit(pUnit) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pUnit);
				entityManager.getTransaction().commit();
				LOGGER.info("Meeting wurde persistiert:" + pUnit.getTitle());
			}
			else{
				LOGGER.info("Meeting existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Meeting: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Meeting: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen einer Unterrichts-Einheit zur Datenbank
	 * @param pUnit das Unterrichts-Einheit-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static int addTeachingUnit(final TeachingUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"Leeres Unterrichts-Einheit Objekt übergeben");
		}
		try {
			if(getTeachingUnit(pUnit) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pUnit);
				entityManager.getTransaction().commit();
				LOGGER.info("Unterrichts-Einheit wurde persistiert:" + pUnit.toString());
				return entityManager.find(TeachingUnit.class, pUnit.getId()).getId();
			}
			else{
				LOGGER.info("Unterrichts-Einheit existiert bereits in DB.");
				return 0;
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Unterrichts-Einheit: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Unterrichts-Einheit: "
					+ e.getMessage());
		}
	}

	/**
	 * Hinzufügen eines Raums zur Datenbank
	 * @param pRoom das Raum-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addRoom(final Room pRoom) throws DatabaseException {
		if (pRoom == null) {
			throw new IllegalArgumentException(
					"Leeres Raum Objekt übergeben");
		}
		try {
			if(entityManager.find(Room.class, pRoom.getName()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pRoom);
				entityManager.getTransaction().commit();
				LOGGER.info("Raum wurde persistiert:" + pRoom.getName());
			}
			else{
				LOGGER.info("Raum existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Raum: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Raum: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Hinzufügen eines Fachs zur Datenbank
	 * @param pSubject das Fachs-Objekt, welches persistiert werden soll.
	 * @throws DatabaseException 
	 */
	public static void addSubject(final Subject pSubject) throws DatabaseException {
		if (pSubject == null) {
			throw new IllegalArgumentException(
					"Leeres Fach Objekt übergeben");
		}
		try {
			if(entityManager.find(Subject.class, pSubject.getTitle()) == null){
				entityManager.getTransaction().begin();
				entityManager.persist(pSubject);
				entityManager.getTransaction().commit();
				LOGGER.info("Fach wurde persistiert:" + pSubject.getTitle());
			}
			else{
				LOGGER.info("Fach existiert bereits in DB.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Persistieren von Fach: ", e);
			throw new DatabaseException("Fehler beim Persistieren von Fach: "
					+ e.getMessage());
		}
	}
	

	/**
	 * Fügt ein Gebäude mit Entfernung der Config hinzu.
	 * @param pBuilding Gebäude
	 * @param pDistance	Entfernung
	 */
	public static void addBuilding(final String pBuilding, final int pDistance) {
		if(pBuilding != null && !pBuilding.equals("") && pBuilding != null){
			Config con = getConfig();
			con.addBuilding(pBuilding, pDistance);
			entityManager.merge(con);
		}
		else{
			LOGGER.error("Fehler beim Hinzufügen von Gebäude:" + pBuilding);
		}
	}
	
	/**
	 * Fügt eine Warnung der Datenbank hinzu.
	 * @param pWarning	Die Warnung
	 * @return Warnung die Persistiert wurde.
	 * @throws DatabaseException
	 */
	public static int addWarning(final Warning pWarning) throws DatabaseException{
		if(pWarning != null){
			try{
				if(getWarningByAttribute(pWarning) == null){
					entityManager.getTransaction().begin();
					entityManager.persist(pWarning);
					entityManager.getTransaction().commit();
					return entityManager.find(Warning.class, pWarning.getId()).getId();
				}
				return 0;
			}
			catch(Exception e){
				LOGGER.error("Warnung konnte nicht persistiert werden.");
				throw new DatabaseException("Warnung konnte nicht persistiert werden.");
			}
		}
		else{
			LOGGER.error("Fehlerhafter Parameter.");
			throw new IllegalArgumentException("Fehlerhafte Eingabe");
		}
	}
	
	/**
	 * Fügt eine DistanzWarnung der Datenbank hinzu.
	 * @param pWarning	Die Warnung
	 * @return Warnung die Persistiert wurde.
	 * @throws DatabaseException
	 */
	public static int addDistanceWarning(final DistanceWarning pWarning) throws DatabaseException{
		if(pWarning != null){
			try{
				if(getDistanceWarningByAttribute(pWarning) == null){
					entityManager.getTransaction().begin();
					entityManager.persist(pWarning);
					entityManager.getTransaction().commit();
					return entityManager.find(DistanceWarning.class, pWarning.getId()).getId();
				}
				return 0;
			}
			catch(Exception e){
				LOGGER.error("Warnung konnte nicht persistiert werden.");
				throw new DatabaseException("Warnung konnte nicht persistiert werden.");
			}
		}
		else{
			LOGGER.error("Fehlerhafter Parameter.");
			throw new IllegalArgumentException("Fehlerhafte Eingabe");
		}
	}
	
	/**
	 * Fügt eine Warnung der Datenbank hinzu.
	 * @param pWarning	Die Warnung
	 * @return Warnung die Persistiert wurde.
	 * @throws DatabaseException
	 */
	public static int addOverloadWarning(final OverloadWarning pWarning) throws DatabaseException{
		if(pWarning != null){
			try{
				if(getOverloadWarningByAttribute(pWarning) == null){
					entityManager.getTransaction().begin();
					entityManager.persist(pWarning);
					entityManager.getTransaction().commit();
					return entityManager.find(OverloadWarning.class, pWarning.getId()).getId();
				}
				return 0;
			}
			catch(Exception e){
				LOGGER.error("Warnung konnte nicht persistiert werden.");
				throw new DatabaseException("Warnung konnte nicht persistiert werden.");
			}
		}
		else{
			LOGGER.error("Fehlerhafter Parameter.");
			throw new IllegalArgumentException("Fehlerhafte Eingabe");
		}
	}
	
	/**
	 * Fügt eine Warnung der Datenbank hinzu.
	 * @param pWarning	Die Warnung
	 * @return Warnung die Persistiert wurde.
	 * @throws DatabaseException
	 */
	public static int addMultipleReservationWarning(final MultipleReservationWarning pWarning) throws DatabaseException{
		if(pWarning != null){
			try{
				if(getMultipleReservationWarningByAttribute(pWarning) == null){
					entityManager.getTransaction().begin();
					entityManager.persist(pWarning);
					entityManager.getTransaction().commit();
					return entityManager.find(MultipleReservationWarning.class, pWarning.getId()).getId();
				}
				return 0;
			}
			catch(Exception e){
				LOGGER.error("Warnung konnte nicht persistiert werden.");
				throw new DatabaseException("Warnung konnte nicht persistiert werden.");
			}
		}
		else{
			LOGGER.error("Fehlerhafter Parameter.");
			throw new IllegalArgumentException("Fehlerhafte Eingabe");
		}
	}
	
	
	/**
	 * 	
	 * Setzt die SollStunden eines Jahrgangs in der Config fest.
	 * @param pSubject
	 * @param pYear
	 * @param pHours
	 */
	public static void updateHoursPerSubjectYear(final Subject pSubject,
			final int pYear, final int pHours) {
		getConfig().addIntendedWorkload(pYear, pSubject, pHours);
	}

	/*
	 * Aktualisieren bzw. Editieren.
	 */
	
	/**
	 * Ändern einer Konfiguration in der Datenbank
	 * 
	 * @param pConfig zu änderndes Konfiguration-Objekt in der DB.
	 * @throws DatabaseException, wenn Config nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateConfig(final Config pConfig) throws DatabaseException {
		if (pConfig == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pConfig);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim ändern der Config:" + pConfig.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern des Config:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Projekts in der Datenbank.
	 * 
	 * @param pProject zu änderndes Projekt-Objekt in der DB.
	 * @throws DatabaseException, wenn Projekt nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateProject(final Project pProject) throws DatabaseException {
		if (pProject == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pProject);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim ändern des Projekts:" + pProject.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern des Projekts:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Lehrers in der Datenbank.
	 * 
	 * @param pTeacher	zu änderndes Lehrer-Objekt in der DB.
	 * @throws DatabaseException, wenn Lehrer nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateTeacher(final Teacher pTeacher) throws DatabaseException{
		if (pTeacher == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pTeacher);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim ändern des Lehrers:" + pTeacher.getShortName(), e);
			throw new DatabaseException("Fehler beim Ändern des Lehrers:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Päd. Mitarbeiters in der Datenbank.
	 * @param pEducEmployee zu änderndes Päd. Mitarbeiter-Objekt in der DB.
	 * @throws DatabaseException, wenn Päd. Mitarbeiter nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateEducEmployee(final EducEmployee pEducEmployee) throws DatabaseException {
		if (pEducEmployee == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pEducEmployee);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern des Päd. Mitarbeiter:" + pEducEmployee.getShortName(), e);
			throw new DatabaseException("Fehler beim Ändern des Päd. Mitarbeiters:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern einer Pause in der Datenbank.
	 * @param pBreak zu änderndes Pausen-Objekt in der DB.
	 * @throws DatabaseException, wenn Pause nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateBreak(final Break pBreak) throws DatabaseException {
		if (pBreak == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pBreak);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der Pause:" + pBreak.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern der Pause:"
					+ e.getMessage());
		}
	}
	
	/**
	 * Ändern einer BreakUnit in der Datenbank.
	 * @param pBreak zu änderndes BreakUnit-Objekt in der DB.
	 * @throws DatabaseException, wenn Pause nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateBreakUnit(final BreakUnit pBreakUnit) throws DatabaseException {
		if (pBreakUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pBreakUnit);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der BreakUnit:" + pBreakUnit.toString(), e);
			throw new DatabaseException("Fehler beim Ändern der BreakUnit:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern einer Kategorie in der Datenbank.
	 * @param pCategory zu änderndes Kategorie-Objekt in der DB.
	 * @throws DatabaseException, wenn Kategorie nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateCategory(final Category pCategory)  throws DatabaseException {
		if (pCategory == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pCategory);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der Kategorie:" + pCategory.getName(), e);
			throw new DatabaseException("Fehler beim Ändern der Kategorie:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern einer Klasse in der Datenbank.
	 * @param pClass zu änderndes Klassen-Objekt in der DB.
	 * @throws DatabaseException, wenn Klasse nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateClass(final Class pClass)  throws DatabaseException {
		if (pClass == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pClass);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der Klasse:" + pClass.getYear() + pClass.getName(), e);
			throw new DatabaseException("Fehler beim Ändern der Klasse:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern einer ext. Aktivität in der Datenbank.
	 * @param pUnit zu änderndes ext. Aktivität-Objekt in der DB.
	 * @throws DatabaseException, wenn ext. Aktivität nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateExternUnit(final ExternUnit pUnit)  throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pUnit);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der ext. Aktivität:" + pUnit.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern der ext. Aktivität:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Meetings in der Datenbank.
	 * @param pUnit zu änderndes Meeting-Objekt in der DB.
	 * @throws DatabaseException, wenn Meeting nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateMeetingUnit(final MeetingUnit pUnit)  throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pUnit);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern des Meetings:" + pUnit.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern des Meetings:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern einer Unterrichtseinheit in der Datenbank.
	 * @param pUnit zu änderndes Unterrichtseinheit-Objekt in der DB.
	 * @throws DatabaseException, wenn Unterrichtseinheit nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateTeachingUnit(final TeachingUnit pUnit)  throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pUnit);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern der Unterrichts-Einheit:" + pUnit.getId(), e);
			throw new DatabaseException("Fehler beim Ändern der Unterrichts-Einheit:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Raumes in der Datenbank.
	 * @param pRoom zu änderndes Raum-Objekt in der DB.
	 * @throws DatabaseException, wenn Raum nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateRoom(final Room pRoom)  throws DatabaseException {
		if (pRoom == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pRoom);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern des Raums:" + pRoom.getName(), e);
			throw new DatabaseException("Fehler beim Ändern des Raums:"
					+ e.getMessage());
		}
	}

	/**
	 * Ändern eines Faches in der Datenbank.
	 * @param pSubject zu änderndes Fach-Objekt in der DB.
	 * @throws DatabaseException, wenn Fach nicht in DB vorhanden, oder
	 * 			bei der Änderung ein Fehler auftritt.
	 */
	public static void updateSubject(final Subject pSubject)  throws DatabaseException {
		if (pSubject == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(pSubject);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("Exception beim Ändern des Fachs:" + pSubject.getTitle(), e);
			throw new DatabaseException("Fehler beim Ändern des Fachs:"
					+ e.getMessage());
		}
	}

	
	
	/*
	 * Entfernen.
	 */
	/**
	 * Entfernt einen Lehrer aus der Datenbank.
	 * @param pTeacher 
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteTeacher(final Teacher pTeacher) throws DatabaseException {
		if (pTeacher == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getTeacherByShort(pTeacher.getShortName()) != null){
					entityManager.getTransaction().begin();
					Teacher temp = entityManager.merge(pTeacher);
					entityManager.remove(temp);
					entityManager.getTransaction().commit();
					LOGGER.info("Lehrer wurde entfernt: " +  pTeacher.getShortName());
			}
			else{
				LOGGER.info("Lehrer "+pTeacher.getShortName() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Lehrer wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Lehrer wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Lehrer: ", e);
			throw new DatabaseException("Fehler beim Löschen von Lehrer: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt einen Päd. Mitarbeiter Objekt aus der Datenbank.
	 * @param pEducEmployee übergebenes Päd. Mitarbeiter Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteEducEmployee(final EducEmployee pEducEmployee) throws DatabaseException {
		if (pEducEmployee == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getEducEmployeeByShort(pEducEmployee.getShortName()) != null){
					entityManager.getTransaction().begin();
					EducEmployee temp = entityManager.merge(pEducEmployee);
					entityManager.remove(temp);
					entityManager.getTransaction().commit();
					LOGGER.info("Päd. Mitarbeiter wurde entfernt: " +  pEducEmployee.getShortName());
			}
			else{
				LOGGER.info("Päd. Mitarbeiter "+pEducEmployee.getShortName() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Päd. Mitarbeiter wird noch in der Planung verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Päd. Mitarbeiter wird noch in der Planung verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Päd. Mitarbeiter: ", e);
			throw new DatabaseException("Fehler beim Löschen von Päd. Mitarbeiter: "
					+ e.getMessage());
		}
	}

	
	
	/**
	 * Entfernt ein Pausen Objekt aus der Datenbank.
	 * @param pBreak übergebenes Pausen Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteBreak(final Break pBreak) throws DatabaseException {
		if (pBreak == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getBreak(pBreak) != null){
				entityManager.getTransaction().begin();
				Break temp = entityManager.merge(pBreak);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Pause wurde entfernt: " +  pBreak.getTitle());
			}
			else{
				LOGGER.info("Pause existiert nicht in DB");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Pause: ", e);
			throw new DatabaseException("Fehler beim Löschen von Pause: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Entfernt ein BreakUnit Objekt aus der Datenbank.
	 * @param pBreak übergebenes BreakUnit Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteBreakUnit(final BreakUnit pBreakUnit) throws DatabaseException {
		if (pBreakUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getBreakUnit(pBreakUnit) != null){
				entityManager.getTransaction().begin();
				BreakUnit temp = entityManager.merge(pBreakUnit);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("PausenObjekt wurde entfernt: " +  pBreakUnit.toString());
			}
			else{
				LOGGER.info("PausenObjekt existiert nicht in DB");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von PausenObjekt: ", e);
			throw new DatabaseException("Fehler beim Löschen von PausenObjekt: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Kategorie Objekt aus der Datenbank.
	 * @param pCategory übergebenes Kategorie Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteCategory(final Category pCategory) throws DatabaseException {
		if (pCategory == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getCategoryByName(pCategory.getName()) != null){
				entityManager.getTransaction().begin();
				Category temp = entityManager.merge(pCategory);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Kategorie wurde entfernt: " +  pCategory.getName());
			}
			else{
				LOGGER.info("Kategorie "+pCategory.getName() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Kategorie wird bereits in Räumen verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Kategorie wird bereits in Räumen verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Kategorie: ", e);
			throw new DatabaseException("Fehler beim Löschen von Kategorie: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Klassen Objekt aus der Datenbank.
	 * @param pClass übergebenes Klassen Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteClass(final Class pClass) throws DatabaseException {
		if (pClass == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getClassByName(pClass.getName()) != null){
				entityManager.getTransaction().begin();
				Class temp = entityManager.merge(pClass);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Klasse wurde entfernt: " +  pClass.getYear() + pClass.getName());
			}
			else{
				LOGGER.info("Klasse "+pClass.getName() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Klasse wird bereits im Stundenplan verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Klasse wird bereits im Stundenplan verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Klasse: ", e);
			throw new DatabaseException("Fehler beim Löschen von Klasse: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Entfernt ein ext. Aktivität Objekt aus der Datenbank.
	 * @param pUnit übergebenes ext. Aktivität Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteExternUnit(final ExternUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getExternUnit(pUnit) != null){
				entityManager.getTransaction().begin();
				ExternUnit temp = entityManager.merge(pUnit);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("ext. Aktivität wurde entfernt: " +  pUnit.getTitle());
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von ext. Aktivität: ", e);
			throw new DatabaseException("Fehler beim Löschen von ext. Aktivität: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Meeting Objekt aus der Datenbank.
	 * @param pUnit übergebenes Meeting Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteMeetingUnit(final MeetingUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getMeetingUnit(pUnit) != null){
				entityManager.getTransaction().begin();
				MeetingUnit temp = entityManager.merge(pUnit);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Meeting wurde entfernt: " +  pUnit.getTitle());
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Meeting: ", e);
			throw new DatabaseException("Fehler beim Löschen von Meeting: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Entfernt eine PersonTime aus der DB
	 * @param pUnit
	 * @throws DatabaseException
	 */
	public static void deletePersonTime(final PersonTime pPersonTime) throws DatabaseException {
		if (pPersonTime == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getPersonTimeByPersonByTimeslot(pPersonTime.getPerson(), pPersonTime.getTimeslot()) != null){
				entityManager.getTransaction().begin();
				PersonTime temp = entityManager.merge(pPersonTime);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("PersonTime wurde entfernt: " + pPersonTime.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Meeting: ", e);
			throw new DatabaseException("Fehler beim Löschen von Meeting: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Unterrichts-Einheit Objekt aus der Datenbank.
	 * @param pUnit übergebenes Unterrichts-Einheit Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteTeachingUnit(final TeachingUnit pUnit) throws DatabaseException {
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getTeachingUnit(pUnit) != null){
				entityManager.getTransaction().begin();
				TeachingUnit temp = entityManager.merge(pUnit);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Unterrichts-Einheit wurde entfernt: " +  pUnit.getId());
			}
			else{
				throw new DatabaseException("Die Unterrichtseinheit war nicht gespeichert.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Unterrichts-Einheit: ", e);
			throw new DatabaseException("Fehler beim Löschen von Unterrichts-Einheit: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Raum Objekt aus der Datenbank.
	 * @param pRoom übergebenes Raum Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteRoom(final Room pRoom) throws DatabaseException {
		if (pRoom == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getRoomByTitle(pRoom.getName()) != null){
				entityManager.getTransaction().begin();
				Room temp = entityManager.merge(pRoom);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Raum wurde entfernt: " +  pRoom.getName());
			}
			else{
				LOGGER.info("Raum "+pRoom.getName() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Raum wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Raum wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Raum: ", e);
			throw new DatabaseException("Fehler beim Löschen von Raum: "
					+ e.getMessage());
		}
	}

	/**
	 * Entfernt ein Fach Objekt aus der Datenbank.
	 * @param pSubject übergebenes Fach Objekt, welches gelöscht werden soll.
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteSubject(final Subject pSubject) throws DatabaseException {
		if (pSubject == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getSubjectByName(pSubject.getTitle()) != null){
				entityManager.getTransaction().begin();
				Subject temp = entityManager.merge(pSubject);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Fach wurde entfernt: " +  pSubject.getTitle());
			}
			else{
			LOGGER.info("Fach "+pSubject.getTitle() + " existiert nicht in der DB.");
			}
		} catch(RollbackException b){
			LOGGER.error("Fach wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
			throw new DatabaseException("Fach wird bereits in der Planung verwendet und kann nicht gelöscht werden.");
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Fach: ", e);
			throw new DatabaseException("Fehler beim Löschen von Fach: "
					+ e.getMessage());
		}
	}

	/**
	 * Diese Methode entfernt ein Gebäude vom Typ String aus der aktuellen Config
	 * @param pBuilding Gebäude String
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static void deleteBuilding(final String pBuilding) throws DatabaseException{
		Collection<String> collbuilding = new ArrayList<String>();
		collbuilding = getAllBuildings();
		if(collbuilding.contains(pBuilding)){
			//Abfrage, ob ein Raum mit diesem Building existiert
			final Query query = entityManager
					.createQuery("SELECT r FROM Room r WHERE r.building = ?1");
			query.setParameter(1, pBuilding);
			Collection<String> collection = query.getResultList();
			if(collection.size() == 0){
				//es gibt keinen raum
				Config con = getConfig();
				con.removeBuilding(pBuilding);
				entityManager.getTransaction().begin();
				entityManager.merge(con);
				entityManager.getTransaction().commit();
				LOGGER.info("Gebäude wurde entfernt: " +  pBuilding);
			}
			else{
				//es gibt einen raum
				LOGGER.info("Gebäude kann nicht gelöscht werden, da der noch Räume existieren. " +  pBuilding);
				throw new DatabaseException("Gebäude kann nicht gelöscht werden, da der noch Räume existieren."+  pBuilding);
			}
		}
		else{
			LOGGER.info("Gebäude nicht vorhanden: " +  pBuilding);
			throw new DatabaseException("Gebäude kann nicht gelöscht werden, da es nicht existiert."+  pBuilding);
		}
	}

	/**
	 * Entfernt ein Warning Objekt aus der Datenbank.
	 * @param pWarning Warnung die gelöscht werden soll
	 * @throws DatabaseException, falls ein Fehler in der DB beim Entfernen auftritt.
	 */
	public static void deleteWarning(final Warning pWarning) throws DatabaseException {
		if (pWarning == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getWarningByAttribute(pWarning) != null){
				entityManager.getTransaction().begin();
				Warning temp3 = entityManager.merge(pWarning);
				entityManager.remove(temp3);
				entityManager.getTransaction().commit();
				LOGGER.info("Warnung wurde entfernt: " +  pWarning.getId());
			}
			else{
				throw new DatabaseException("Warnung war nicht in DB gespeichert.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Warnung: ", e);
			throw new DatabaseException("Fehler beim Löschen von Warnung: "
					+ e.getMessage());
		}
	}
	
	/**
	 * 	 * @param pWarning
	 * @throws DatabaseException
	 */
	public static void deleteOverloadWarning(final OverloadWarning pWarning) throws DatabaseException {
		if (pWarning == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getOverloadWarningByAttribute(pWarning) != null){
					entityManager.getTransaction().begin();
					OverloadWarning temp2 = entityManager.merge(pWarning);
					entityManager.remove(temp2);
					entityManager.getTransaction().commit();
					LOGGER.info("OverloadWarning wurde entfernt: " +  pWarning.getId());
			}
			else{
				throw new DatabaseException("OverloadWarnung war nicht in DB gespeichert.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von OverloadWarnung: ", e);
			throw new DatabaseException("Fehler beim Löschen von OverloadWarnung: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Entfernt eine MultipleReservation-Warnung aus der DB
	 * @param pWarning
	 * @throws DatabaseException
	 */
	public static void deleteMultipleReservationWarning(final MultipleReservationWarning pWarning) throws DatabaseException {
		if (pWarning == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getMultipleReservationWarningByAttribute(pWarning) != null){
				entityManager.getTransaction().begin();
				MultipleReservationWarning temp = entityManager.merge(pWarning);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Warnung wurde entfernt: " +  pWarning.getId());
			}
			else{
				throw new DatabaseException("MultipleReservationWarnung war nicht in DB gespeichert.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von MultipleReservationWarnung: ", e);
			throw new DatabaseException("Fehler beim Löschen von MultipleReservationWarnung: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Entfernt eine Distanz-Warnung aus der DB
	 * @param pWarning
	 * @throws DatabaseException
	 */
	public static void deleteDistanceWarning(final DistanceWarning pWarning) throws DatabaseException {
		if (pWarning == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			if(getDistanceWarningByAttribute(pWarning) != null){
				entityManager.getTransaction().begin();
				DistanceWarning temp = entityManager.merge(pWarning);
				entityManager.remove(temp);
				entityManager.getTransaction().commit();
				LOGGER.info("Warnung wurde entfernt: " +  pWarning.getId());
			}
			else{
				throw new DatabaseException("Warnung war nicht in DB gespeichert.");
			}
		} catch (Exception e) {
			LOGGER.error("Fehler beim Löschen von Warnung: ", e);
			throw new DatabaseException("Fehler beim Löschen von Warnung: "
					+ e.getMessage());
		}
	}
	
	/*
	 * Selektion nach Schluessel
	 */
	/**
	 * Sucht anhand des Kürzels eines Lehrers nach diesem in der DB.
	 * @param pShort	Kürzel des Lehrers
	 * @return	Lehrer-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static Teacher getTeacherByShort(final String pShort) throws DatabaseException {
		if (pShort == null || pShort.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(Teacher.class, pShort);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Lehrers "
					+ pShort, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Lehrers " + pShort
							+ ": " + e.getMessage());
		}
	}

	/**
	 * Sucht anhand des Kürzels eines Päd. Mitarbeiters nach diesem in der DB.
	 * @param pShort	Kürzel des Päd. Mitarbeiters
	 * @return	Päd. Mitarbeiter-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static EducEmployee getEducEmployeeByShort(final String pShort) throws DatabaseException {
		if (pShort == null || pShort.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(EducEmployee.class, pShort);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Päd. Mitarbeiters "
					+ pShort, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Päd. Mitarbeiters " + pShort
							+ ": " + e.getMessage());
		}
	}

	/**
	 * Sucht anhand des Namen einer Kategorie nach diesem in der DB.
	 * @param pName	Name der Kategorie
	 * @return	Kategorie-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static Category getCategoryByName(final String pName) throws DatabaseException {
		if (pName == null || pName.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(Category.class, pName);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Kategorie "
					+ pName, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Kategorie " + pName
							+ ": " + e.getMessage());
		}
	}

	/**
	 * Sucht anhand des Namen einer Klasse nach diesem in der DB.
	 * @param pName	Name der Klasse
	 * @return	Klasse-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static Class getClassByName(final String pName) throws DatabaseException {
		if (pName == null || pName.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(Class.class, pName);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Klasse "
					+ pName, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Klasse " + pName
							+ ": " + e.getMessage());
		}
	}

	/**
	 * Sucht anhand des Titel eines Raums nach diesem in der DB.
	 * @param pTitle Titel des Raums
	 * @return Raum-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static Room getRoomByTitle(final String pTitle) throws DatabaseException {
		if (pTitle == null || pTitle.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(Room.class, pTitle);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Raums "
					+ pTitle, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Raums " + pTitle
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Sucht anhand des Titel eines Raums nach diesem in der DB.
	 * @param pTitle Titel des Raums
	 * @return Raum-Objekt aus der DB
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static PersonTime getPersonTimeByPersonByTimeslot(final Person pPerson, final Timeslot pTimeslot) throws DatabaseException {
		if (pPerson == null || pTimeslot == null) {
			return null;
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT m FROM PersonTime m JOIN m.timeslot t JOIN m.person p WHERE t.duration=?1 AND t.startHour=?2 AND t.startMinute=?3"
							+ " AND p.shortName = ?4");
			query.setParameter(1, pTimeslot.getDuration());
			query.setParameter(2, pTimeslot.getStartHour());
			query.setParameter(3, pTimeslot.getStartMinute());
			query.setParameter(4, pPerson.getShortName());
			Collection<PersonTime> collectionPT = query.getResultList();
			if(collectionPT.size() == 1){
				return (PersonTime)collectionPT.toArray()[0];
			}
			return null;
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen einer PersonTime "
					+ pPerson.getShortName() + " " + pTimeslot.getStarttime(), e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer PersonTime: " + pPerson.getShortName() + " " + pTimeslot.getStarttime()
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Gibt eine Liste von PersonTimes für eine Person zurück.
	 * @param pPerson
	 * @return
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Collection<PersonTime> getPersonTimeByPerson(final Person pPerson) throws DatabaseException {
		if (pPerson == null) {
			return null;
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT m FROM PersonTime m JOIN m.person p WHERE p.shortName = ?1");
			query.setParameter(1, pPerson.getShortName());
			Collection<PersonTime> collectionPT = query.getResultList();
			return collectionPT;
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen einer PersonTime "
					+ pPerson.getShortName(), e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer PersonTime: " + pPerson.getShortName() 
					+ ": " + e.getMessage());
		}
	}

	/**
	 * Sucht anhand des Namen eines Fachs nach diesem in der DB.
	 * @param pName Name des Fachs
	 * @return Fach-Objekt aus der DB
	 * @throws DatabaseException
	 */
	public static Subject getSubjectByName(final String pTitle) throws DatabaseException {
		if (pTitle == null || pTitle.length() == 0) {
			return null;
		}
		try {
			return entityManager.find(Subject.class, pTitle);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Fachs "
					+ pTitle, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Fachs " + pTitle
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Sucht anhand der ID einer Unit nach dieser in der DB.
	 * @param pUnitType	Name der Klasse die erwartet wird. z.B. MeetingUnit
	 * @param pID	die ID der Klasse
	 * @return die gesuchte Klasse
	 * @throws DatabaseException
	 */
	public static Unit getUnitByID(String pUnitType, final int pID) throws DatabaseException {
		if (pID < 0) {
			return null;
		}
		try {
			switch(pUnitType){
				case "MeetingUnit":
					return (Unit)entityManager.find(MeetingUnit.class, pID);
				case "Break":
					return (Unit)entityManager.find(Break.class, pID);
				case "BreakUnit":
					return (Unit)entityManager.find(BreakUnit.class, pID);
				case "ExternUnit":
					return (Unit)entityManager.find(ExternUnit.class, pID);
				case "TeachingUnit":
					return (Unit)entityManager.find(TeachingUnit.class, pID);
			}
			return null;
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Unit-Objekts mit der ID: "
					+ pID, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Unit-Objekts " + pID
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Sucht anhand der ID einer Unit nach dieser in der DB.
	 * @param pUnitType	Name der Klasse die erwartet wird. z.B. MeetingUnit
	 * @param pID	die ID der Klasse
	 * @return die gesuchte Klasse
	 * @throws DatabaseException
	 */
	public static Timeslot getTimeslotByID(final int pID) throws DatabaseException {
		try {
			return (Timeslot)entityManager.find(Timeslot.class, pID);
		} catch (Exception e) {
			LOGGER.error("Fehler beim Abrufen eines Timeslot-Objekts mit der ID: "
					+ pID, e);
			throw new DatabaseException(
					"Fehler beim Abrufen eines Timeslot-Objekts " + pID
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Die Methode ruft aus der Datenbank Warnungen ab und gibt sie zurück.
	 * @param pWarning	Warnung vom Superklassen-Typ Warning
	 * @return	Warnung
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Warning getWarningByAttribute(final Warning pWarning) throws DatabaseException{
		if(pWarning != null && pWarning instanceof Warning){
			try{
				final Query query = entityManager
						.createQuery("SELECT m FROM Warning m WHERE m.description = ?1 AND m.priority = ?2");
				query.setParameter(1, pWarning.getDescription());
				query.setParameter(2, pWarning.getPriority());
				Collection<Warning> collectionWarning = query.getResultList();
				if(collectionWarning.size() == 1){
					return (Warning)collectionWarning.toArray()[0];
				}
				return null;	
			}
			catch(Exception e){
				LOGGER.error("Warnung wurde nicht gefunden.");
				throw new DatabaseException("Warnung wurde in DB nicht gefunden.");
			}
		}
		else{
			throw new IllegalArgumentException("Leerer Warning Parameter.");
		}
	}
	
	/**
	 * Die Methode ruft aus der Datenbank Warnungen ab und gibt sie zurück.
	 * @param pWarning	Warnung vom Superklassen-Typ Warning
	 * @return	Warnung
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static OverloadWarning getOverloadWarningByAttribute(final OverloadWarning pWarning) throws DatabaseException{
		if(pWarning != null && pWarning instanceof OverloadWarning){
			try{
				final Query query = entityManager
						.createQuery("SELECT m FROM OverloadWarning m WHERE m.what = ?1 AND m.type = ?2");
				query.setParameter(1, pWarning.getWhat());
				query.setParameter(2, pWarning.getType());
				Collection<OverloadWarning> collectionWarning = query.getResultList();
				if(collectionWarning.size() == 1){
					return (OverloadWarning)collectionWarning.toArray()[0];
				}
				return null;
			}
			catch(Exception e){
				LOGGER.error("Warnung wurde nicht gefunden.");
				throw new DatabaseException("Warnung wurde in DB nicht gefunden.");
			}
		}
		else{
			throw new IllegalArgumentException("Leerer Warning Parameter.");
		}
	}
	
	/**
	 * Die Methode ruft aus der Datenbank Warnungen ab und gibt sie zurück.
	 * @param pWarning	Warnung vom Superklassen-Typ Warning
	 * @return	Warnung
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static MultipleReservationWarning getMultipleReservationWarningByAttribute(final MultipleReservationWarning pWarning) throws DatabaseException{
		if(pWarning != null && pWarning instanceof MultipleReservationWarning){
			try{
				final Query query = entityManager
						.createQuery("SELECT m FROM MultipleReservationWarning m JOIN m.slot t "
								+ "WHERE m.what = ?1 AND m.type = ?2 AND t.duration = ?3 AND t.startHour = ?4 AND t.startMinute = ?5");
				query.setParameter(1, ((MultipleReservationWarning)pWarning).getWhat());
				query.setParameter(2, ((MultipleReservationWarning)pWarning).getType());
				query.setParameter(3, ((MultipleReservationWarning)pWarning).getTimeslot().getDuration());
				query.setParameter(4, ((MultipleReservationWarning)pWarning).getTimeslot().getStartHour());
				query.setParameter(5, ((MultipleReservationWarning)pWarning).getTimeslot().getStartMinute());
				Collection<MultipleReservationWarning> collectionWarning = query.getResultList();
				if(collectionWarning.size() == 1){
					return (MultipleReservationWarning)collectionWarning.toArray()[0];
				}
				return null;
			}
			catch(Exception e){
				LOGGER.error("Warnung wurde nicht gefunden.");
				throw new DatabaseException("Warnung wurde in DB nicht gefunden.");
			}
		}
		else{
			throw new IllegalArgumentException("Leerer Warning Parameter.");
		}
	}
	

	/**
	 * Die Methode ruft aus der Datenbank Warnungen ab und gibt sie zurück.
	 * @param pWarning	Warnung vom Superklassen-Typ Warning
	 * @return	Warnung
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static DistanceWarning getDistanceWarningByAttribute(final DistanceWarning pWarning) throws DatabaseException{
		if(pWarning != null && pWarning instanceof DistanceWarning){
			try{
				final Query query = entityManager
						.createQuery("SELECT m FROM DistanceWarning m WHERE m.what = ?1 AND m.type = ?2 AND m.day = ?3");
				query.setParameter(1, pWarning.getWhat());
				query.setParameter(2, pWarning.getType());
				query.setParameter(3, pWarning.getDay());
				Collection<DistanceWarning> collectionWarning = query.getResultList();
				if(collectionWarning.size() == 1){
					return (DistanceWarning)collectionWarning.toArray()[0];
				}
				return null;
			}
			catch(Exception e){
				LOGGER.error("Warnung wurde nicht gefunden.");
				throw new DatabaseException("Warnung wurde in DB nicht gefunden.");
			}
		}
		else{
			throw new IllegalArgumentException("Leerer Warning Parameter.");
		}
	}
	
	/*
	 * Selektion anhand von Objekt ob bereits in DB vorhanden
	 */
	
	/**
	 * Gibt ein MeetingUnit-Objekt aus der DB zurück, welches dem übergebenen in Form seiner
	 * Attribute entspricht.
	 * 
	 * @param pUnit
	 * @return MeetingUnit-Objekt aus DB
	 * 
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static MeetingUnit getMeetingUnit(final MeetingUnit pUnit) throws DatabaseException{
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT m FROM MeetingUnit m JOIN m.timeslot t WHERE m.title=?1 AND m.years=?2 AND t.duration=?3 AND t.startHour=?4 AND t.startMinute=?5");
			query.setParameter(1, pUnit.getTitle());
			query.setParameter(2, pUnit.getYear());
			query.setParameter(3, pUnit.getTimeslot().getDuration());
			query.setParameter(4, pUnit.getTimeslot().getStartHour());
			query.setParameter(5, pUnit.getTimeslot().getStartMinute());
			Collection<MeetingUnit> collectionMeeting = query.getResultList();
			Object[] arraymeeting = (Object[])collectionMeeting.toArray();	
			if(collectionMeeting.size() != 0 && pUnit.equals(arraymeeting[0])){
				return (MeetingUnit)arraymeeting[0];
			}
			return null;
		}
		catch(Exception e) {
			LOGGER.error("Fehler beim Abrufen einer MeetingUnit "
					+ pUnit, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer MeetingUnit " + pUnit
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Gibt ein TeachingUnit-Objekt aus der DB zurück, welches dem übergebenen in Form seiner
	 * Attribute entspricht.
	 * 
	 * @param pUnit
	 * @return	TeachingUnit-Objekt aus der DB
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static TeachingUnit getTeachingUnit(final TeachingUnit pUnit) throws DatabaseException{
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT tu FROM TeachingUnit tu JOIN tu.timeslot t WHERE t.duration=?1 AND t.startHour=?2 AND t.startMinute=?3");
			query.setParameter(1, pUnit.getTimeslot().getDuration());
			query.setParameter(2, pUnit.getTimeslot().getStartHour());
			query.setParameter(3, pUnit.getTimeslot().getStartMinute());
			Collection<TeachingUnit> collectionTeaching = query.getResultList();
			
			if(collectionTeaching.size() != 0){
				for(TeachingUnit p:collectionTeaching){
					if(p.equals(pUnit)){
						return p;
					}
				}
			}
			LOGGER.info("TeachingUnit nicht in DB vorhanden.");
			return null;
		}
		catch(Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Unterrichts Aktivität "
					+ pUnit, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Unterrichts Aktivität  " + pUnit
							+ ": " + e.getMessage());
		}
	}
	 
	/**
	 * Gibt ein ExternUnit-Objekt aus der DB zurück, welches dem übergebenen in Form seiner
	 * Attribute entspricht.
	 * @param pUnit
	 * @return ExternUnit-Objekt aus der DB
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static ExternUnit getExternUnit(final ExternUnit pUnit) throws DatabaseException{
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT e FROM ExternUnit e JOIN e.timeslot t WHERE e.title=?1 AND t.duration=?2 AND t.startHour=?3 AND t.startMinute=?4");
			query.setParameter(1, pUnit.getTitle());
			query.setParameter(2, pUnit.getTimeslot().getDuration());
			query.setParameter(3, pUnit.getTimeslot().getStartHour());
			query.setParameter(4, pUnit.getTimeslot().getStartMinute());
			Collection<Break> collectionExtern = query.getResultList();
			Object[] arrayextern = (Object[])collectionExtern.toArray();
			if(collectionExtern.size() != 0 && pUnit.equals(arrayextern[0])){
				return (ExternUnit)arrayextern[0];
			}
			return null;
		}
		catch(Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Externen Aktivität "
					+ pUnit, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Externen Aktivität  " + pUnit
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Gibt ein PausenEinheit-Objekt aus der DB zurück, welches dem übergebenen in Form seiner
	 * Attribute entspricht.
	 * 
	 * @param pUnit	PausenEinheit-Objekt
	 * @return PausenEinheit-Objekt aus der DB
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static BreakUnit getBreakUnit(final BreakUnit pUnit) throws DatabaseException{
		if (pUnit == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT b FROM BreakUnit b JOIN b.timeslot t WHERE t.duration=?1 AND t.startHour=?2 AND t.startMinute=?3");
			query.setParameter(1, pUnit.getTimeslot().getDuration());
			query.setParameter(2, pUnit.getTimeslot().getStartHour());
			query.setParameter(3, pUnit.getTimeslot().getStartMinute());
			Collection<Break> collectionBreakUnit = query.getResultList();
			Object[] arrayBreakUnit = (Object[])collectionBreakUnit.toArray();
			if(collectionBreakUnit.size() != 0 && pUnit.equals(arrayBreakUnit[0])){
				return (BreakUnit)arrayBreakUnit[0];
			}
			return null;
		}
		catch(Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Pausen-Einheit "
					+ pUnit, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Pausen-Einhei  " + pUnit
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Gibt ein Pausen-Objekt aus der DB zurück, welches dem übergebenen in Form seiner
	 * Attribute entspricht.
	 * 
	 * @param pBreak
	 * @return Pausen-Objekt aus der DB
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Break getBreak(final Break pBreak) throws DatabaseException{
		if (pBreak == null) {
			throw new IllegalArgumentException(
					"leeres Parameter Objekt!");
		}
		try {
			final Query query = entityManager
					.createQuery("SELECT b FROM Break b JOIN b.timeslot t WHERE b.title=?1 AND t.duration=?2 AND t.startHour = ?3 AND t.startMinute=?4");
			query.setParameter(1, pBreak.getTitle());
			query.setParameter(2, pBreak.getTimeslot().getDuration());
			query.setParameter(3, pBreak.getTimeslot().getStartHour());
			query.setParameter(4, pBreak.getTimeslot().getStartMinute());
			Collection<Break> collectionBreak = query.getResultList();
			Object[] arraybreak = (Object[])collectionBreak.toArray();
			if(collectionBreak.size() != 0 && pBreak.equals(arraybreak[0])){
				return (Break)arraybreak[0];
			}
			return null;
		}
		catch(Exception e) {
			LOGGER.error("Fehler beim Abrufen einer Pause "
					+ pBreak, e);
			throw new DatabaseException(
					"Fehler beim Abrufen einer Pause " + pBreak
							+ ": " + e.getMessage());
		}
	}
	
	/**
	 * Liefert ein Jahrgangs-Objekt aus der DB anhand eines Integer-Wertes zurück.
	 * @param pYears
	 * @return Jahrgang-Objekt (Years)
	 * @throws DatabaseException
	 */
	@SuppressWarnings("unchecked")
	public static Years getYearsByInt(final int pYears) throws DatabaseException{
		if(pYears <= 0){
			throw new IllegalArgumentException("leeres Parameter-Objekt.");
		}
		else{
			final Query query = entityManager
					.createQuery("SELECT y FROM Years y WHERE y.year_int = ?1");
			query.setParameter(1, pYears);
			Collection<Years> collection = query.getResultList();
			if(collection.size() == 1){
				return (Years)collection.toArray()[0];
			}
			else{				
				LOGGER.info("Year-Objekt nicht in DB vorhanden");
				throw new DatabaseException("Year-Objekt nicht in DB vorhanden.");
			}
		}
	}
}
