/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 * Definition eines Raums.
 * 
 * Ein Raum besteht initial aus einem Namen, einer Nummer und dem
 * dazugehoerendem Gebaeude.
 * 
 * Beispiel: "Klassenraum 2A", "Hauptgebaeude", 064 , Klassenraum
 * 
 * Die Nummer ist hierbei eine optionale Eingabe.
 * 
 * @author prohleder, lamduy, Logger (Tom)
 *
 */
@Entity
public class Room implements IRoom {

	// Attribute
	/**
	 * Der Logger dieser Klasse.
	 */
	final static Logger logger = Logger.getLogger(Room.class);

	/**
	 * Der Titel des Raums
	 */
	@Id
	private String name;

	/**
	 * Der Standort an dem sich der Raum befindet.s
	 */
	private String building;

	/**
	 * Die Raumnummer.
	 */
	private String number;

	/**
	 * Die Raumkategorie
	 */
	@ManyToOne
	private Category category;

	// Konstruktoren.

	/**
	 * Konstruktor der Klasse
	 */
	public Room(final String pName, final String pBuilding,
			final String pNumber, final Category pCategory) {
		setName(pName);
		setBuilding(pBuilding);
		setNumber(pNumber);
		setCategory(pCategory);
		// Logger
		if (logger.isInfoEnabled()) {
			logger.info("Folgender Room wurde erstellt: " + pName + ", "
					+ pBuilding + "pTeacherAllowed: " + pNumber + ", "
					+ pCategory);
		}
	}

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Room() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Room-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden

	/**
	 * Gibt den Namen eines Raumes zurueck.
	 * 
	 * @return den Namen eines Raumes.
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gibt das Gebauede zurueck indem sich der Raum befindet.
	 * 
	 * @return das Gebauede indem sich der Raum befindet.
	 * 
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * Gibt die Nummer eines Raums zurueck. Diese ist optional.
	 * 
	 * @return die Nummer eines Raums.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Gibt die Category zurueck, die diesem Raum zugeordnet ist.
	 * 
	 * @return die Category des Raums.
	 */
	@ManyToOne
	public Category getCategory() {
		return category;
	}

	// Setter-Methoden

	/**
	 * Setzt den Namen eines Raums neu.
	 * 
	 * @param pName
	 *            den neuen Namen eines Raums.
	 * @throws IllegalArgumentException
	 *             falls der Name bereits existiert oder {@code null} uebergeben
	 *             wird.
	 */
	public void setName(final String pName) {
		if (pName != null && pName.trim().length() != 0) {
			name = pName;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Der name des room-Objekts wurde geaendert: "
						+ pName);
			}

		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pName);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen Namen fuer den Raum an.");
		}

	}

	/**
	 * Setzt das Gebaeude eines Raums neu.
	 * 
	 * @param pBuilding
	 *            das Gebaude eines Raums.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setBuilding(final String pBuilding) {
		if (pBuilding != null && pBuilding.trim().length() != 0) {
			building = pBuilding;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Das building des room-objekts wurde geaendert: "
						+ pBuilding.toString());
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pBuilding);
			}
			throw new IllegalArgumentException(
					"Bitte geben sie ein Gebaeude fuer den Raum an.");
		}
	}

	/**
	 * Setzt die Nummer eines Raums neu. Achtung: Diese Eingabe ist optional
	 * 
	 * @param pNumber
	 *            setzt die Nummer eines Raums neu.
	 */
	public void setNumber(final String pNumber) {
		number = pNumber;
		// Logger
		if (logger.isInfoEnabled()) {
			logger.info("Die number des room-objekts wurde geaendert: "
					+ pNumber);
		}
	}

	/**
	 * Setzt die Category eines Raums neu.
	 * 
	 * @param pCategory
	 *            setzt die Category eines Raums neu.
	 * @throws IllegalArgumentException
	 */
	public void setCategory(final Category pCategory) {
		if (pCategory != null) {
			category = pCategory;
			// Logger
			if (logger.isInfoEnabled()) {
				logger.info("Die category des room-objekts wurde geaendert: "
						+ pCategory.toString());
			}
		} else {
			// Logger
			if (logger.isInfoEnabled()) {
				logger.error("Fehler in folgendem Parameter: " + pCategory);
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie eine Raumkategorie an.");
		}
	}

	/**
	 * Die equals-Methode vergleicht dieses Objekt mit einem anderen Objekt vom
	 * Typ IRoom.
	 * 
	 * @param pRoom
	 * @return einen boolean-Wert, ob beide Raeume die selben Attribute
	 *         aufweisen (Namen, Gebaeude, Nummer und Category)
	 * 
	 * @throws IllegalArgumentException
	 *             wenn der Parameter null ist
	 */
	public boolean equals(final Room pRoom) {
		if (pRoom != null) {
			return this.name.equals(pRoom.name)
					&& this.building.equals(pRoom.building)
					&& this.number.equals(pRoom.number)
					&& this.category.equals(pRoom.category);
		} else {
			throw new IllegalArgumentException(
					"Der zu vergleichende Raum ist ungueltig");
		}
	}

	/**
	 * Gibt einen neuen Room zurueck, der inhaltlich mit diesem komplett
	 * identisch ist.
	 * 
	 * @return eine Kopie dieses Raums
	 */
	@Override
	public Room clone() {
		Room copy = new Room(getName(), getBuilding(), getNumber(),
				getCategory());
		if (logger.isInfoEnabled()) {
			logger.info("Folgender Raum wurde kopiert: " + copy.getName());
		}
		return copy;
	}
}