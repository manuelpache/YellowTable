/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 * Realisiert einen Stunden/Unterrichtsinhalt in unserer Logik. Kann zum
 * Beispiel ein Fach sein.
 * 
 * @author prohleder
 *
 * @author Erik k. (+Logger)
 */
@Entity
public class Subject implements ISubject {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Subject.class);

	/**
	 * Der Titel eines Schulfachs.
	 */
	@Id
	private String title;

	/**
	 * Die Schwierigkeit eines Schulfaches
	 */
	@Enumerated
	private DIFFICULTY difficulty;

	/**
	 * Die Raumkaetegorie, die benoetigt wird um das Schulfach auszufuehren.
	 */
	@ManyToOne
	private Category category;

	/**
	 * Die allgmeneine Dauer des Schulfaches.
	 */
	private int duration;

	// Konstruktoren

	/**
	 * Der Konstrukter der Klasse..
	 * 
	 * @param pTitle
	 * @param pDifficulty
	 * @param pCategory
	 * @param pDuration
	 */
	public Subject(final String pTitle, final DIFFICULTY pDifficulty,
			final Category pCategory, final int pDuration) {
		setTitle(pTitle);
		setDifficulty(pDifficulty);
		setCategory(pCategory);
		setDuration(pDuration);
		// Logger
		if (logger.isInfoEnabled()) {
			logger.info("Folgendes Subject wurde erstellt: " + pTitle + ", "
					+ pDifficulty.toString() + pCategory + pDuration);
		}
	}

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Subject() {
		// Logger
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Subject-Objekt wurde erstellt.");
		}
	}

	// Getter-Methode

	/**
	 * Gibt den Namen dieses Stundeninhaltes zurueck.
	 * 
	 * @return den Namen des Stundeninhalts.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gibt den Schwierigkeitsgrad des Stundeninhaltes zurueck.
	 * 
	 * @return den Schwierigkeitsgrad.
	 */
	public DIFFICULTY getDifficulty() {
		return difficulty;
	}

	/**
	 * Gibt die Kategorie des Faches zurueck.
	 * 
	 * @return die Kategorie diese Faches
	 */
	@ManyToOne
	public Category getCategory() {
		return category;
	}

	/**
	 * Gibt die Standarddauer dieses Stundeninhaltes zurueck.
	 * 
	 * @return die Standarddauer
	 */
	public int getDuration() {
		return duration;
	}

	// Setter-Methoden mit Logger

	/**
	 * Setzt den Titel des Stundeninhaltes neu.
	 * 
	 * @param pTitle
	 *            den neuen Titel des Stundeninhaltes.
	 *
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setTitle(final String pTitle) {
		if (pTitle != null && pTitle.trim().length() != 0) {
			title = pTitle;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Der Titel des Subject wurde geaendert: " + pTitle);
			}

		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pTitle);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Titel fuer das Fach an.");
		}
	}

	/**
	 * Setzt die Kategorie des Faches neu.
	 * 
	 * @param pCategory
	 *            den neuen Kategorie des Stundeninhaltes.
	 *
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setCategory(final Category pCategory) {
		if (pCategory != null) {
			category = pCategory;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die category des subject-objekts wurde geaendert: "
						+ pCategory.toString());
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCategory);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie eine gueltige Kategorie an.");
		}

	}

	/**
	 * Setzt den Schwierigkeitsgrad des Stundeninhalts neu.
	 * 
	 * @param pDifficulty
	 *            der neue Schwierigkeitsgrad.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 * 
	 */
	public void setDifficulty(final DIFFICULTY pDifficulty) {
		if (pDifficulty != null) {
			difficulty = pDifficulty;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die DIFFICULTY des subject wurde geaendert: "
						+ pDifficulty.toString());
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pDifficulty);
			}
			throw new IllegalArgumentException(
					"Bitte geben sie einen Schwierigkeitsgrad an");
		}
	}

	/**
	 * Setzt die Dauer eines Faches
	 * 
	 * @param pDuration
	 * 
	 * @throws IllegalArgumentException
	 *             bei einer Eingabe von einer negativen Zahl
	 */
	public void setDuration(final int pDuration) {
		if (pDuration >= 0) {
			duration = pDuration;
		} else
			throw new IllegalArgumentException(
					"Die Dauer des Faches darf nicht negativ sein.");
	}

	/**
	 * Gibt ein neues Subject zurueck, das inhaltlich komplett mit diesem
	 * uebereinstimmt.
	 * 
	 * @return eine Kopie dieses Subject
	 */
	@Override
	public Subject clone() {
		Subject copy = new Subject(getTitle(), getDifficulty(), getCategory(),
				getDuration());
		return copy;
	}
}