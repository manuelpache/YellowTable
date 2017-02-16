package Yellowstone.YellowTable.db;

import Yellowstone.YellowTable.exceptions.DatabaseException;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Diese Klasse dient zur Erstellung und Einladen eines Backups.
 * In ihr wird ein Thread erstellt, der während der GUI Ausführung läuft und Backups erstellt.
 * 
 * @author philiprohleder
 *
 */
public class Backup  implements Runnable{
	
	private static final Logger LOGGER = Logger.getLogger(DBHandler.class.getName());

	/**
	 * Run Methode des Backup Threads
	 */
	public void run() {		
		while(!Thread.interrupted()) {
			try {
				int min = DBHandler.getConfig().getBackupTimer();
				Thread.sleep(min * 60000);
				createBackup();
			}
			catch (InterruptedException e)
		    {
		   		Thread.currentThread().interrupt();
		    } 
			catch (DatabaseException e) {
				LOGGER.error("BackupThread: Backup fehlgeschlagen." + e);
			} 	
			
		}
		if(DBHandler.getConfig().isBackupOnClose()){
			try {
				createBackup();
			} 
			catch (DatabaseException e) {
				LOGGER.error("BackupThread: Backup fehlgeschlagen." + e);
			}
		}
	}
	
	/**
	 * Diese Methode erstellt ein Backup des aktuellen Projekts im definierten Zeitintervall
	 * Zeitintervall wird in der Config / in den Standardeinstellungen durchgeführt.
	 * @throws DatabaseException 
	 */
	private void createBackup() throws DatabaseException{
		try {
	    	  Calendar cal = Calendar.getInstance();
	    	  Date date = new Date(cal.getTimeInMillis());
	    	  DateFormat df1 = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
	    	  String formDat = df1.format(date);
//	    	  String name = DBHandler.getConfig().getBackupTitle() + File.separator;
	    	
	    	  DBHandler.createBackup("Backups/"+formDat );
	    	  LOGGER.info("BackupThread: Backup abgeschlossen");
		}
		catch(Exception e){
			LOGGER.error("BackupThread: Backup fehlgeschlagen." + e);
			throw new DatabaseException("Fehler beim Erstellen eines Backups");
		}
		
	}
}

