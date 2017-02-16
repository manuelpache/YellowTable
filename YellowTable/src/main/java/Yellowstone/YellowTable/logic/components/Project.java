/**
 * Klasse gehört zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 * {@inheritDoc}
 * 
 * @author tomlewandowski (+Logger)
 *
 */
@Entity
public class Project implements IProject {

	// Attribute
	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Project.class);

	/**
	 * Die Standardeinstellungen des Projekts
	 */
	@ManyToOne
	private Config config;

	/**
	 * Alle Planungseinheiten unseres Projekts.
	 */
	private Unit[] allUnits;

	/**
	 * Der Titel des Projekts.
	 */
	@Id
	private String title;

	// Konstruktoren

	/**
	 * Konstruktor, der Klasse Projekt.
	 * 
	 * Jedes Projekt bekommt initial eine Config mit allen Standardeinstellungen
	 * und alle Planungseinheiten, die in dem Projekt existieren übergeben.
	 * 
	 * @param pTitle
	 * @param pConfig
	 * @param pAllUnits
	 *
	 */
	public Project(final String pTitle, final Config pConfig,
			final Unit[] pAllUnits) {
		setTitle(pTitle);
		setConfig(pConfig);
		allUnits = pAllUnits;
		if (logger.isInfoEnabled()) {
			logger.info("Folgendes Project wurde erstellt: " + pTitle);
		}
	}

	// Leerer Konstruktor fuer Datenbank und GUI
	public Project() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Project-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Gibt die Config und somit die Parameter der Standardeinstellungen.
	 * 
	 * @return die Config.
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * Gibt eine Liste aller Planungseinheiten zurueck.
	 * 
	 * @return eine Liste aller Planungseinheiten.
	 */
	public Unit[] getAllUnits() {
		return allUnits;
	}

	/**
	 * Gibt den Titel fuer das Projekt zurueck, der als PK fuer die Persistenz
	 * dient.
	 * 
	 * @return den Titel der Klasse.
	 */
	public String getTitle() {
		return title;
	}

	// Setter-Methoden

	/**
	 * Setzt die Config und somit die Parameter der Standardeinstellungen neu.
	 * 
	 * @param pConfig
	 *            eine Config, die ueber die Parameter aus den
	 *            Standardeinstellungen verfuegt.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setConfig(final Config pConfig) {
		if (pConfig != null) {
			config = pConfig;
			if (logger.isInfoEnabled()) {
				logger.info("Die Config des Projects wurde geaendert: "
						+ pConfig);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pConfig);
			}
			throw new IllegalArgumentException(
					"Die Konfiguration ist ungueltig.");
		}
	}

	/**
	 * Setzt den Titel fuer das Projekt neu.
	 * 
	 * @param pTitle
	 *            der neue Titel.
	 * @throws IllegalArgumentException
	 *             falls der Titel eine ungültige Zeichenkette enthält, oder
	 *             schon in der DB enthalten ist.
	 */
	public void setTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;
			if (logger.isInfoEnabled()) {
				logger.info("Der Titel des Projects wurde geaendert: " + pTitle);
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Der Titel des Projektes ist ungueltig.");
		}
	}
}