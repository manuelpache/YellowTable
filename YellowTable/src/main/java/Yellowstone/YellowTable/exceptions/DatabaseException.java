package Yellowstone.YellowTable.exceptions;

/**
 * Eine Exception, die ein Problem mit der Datenbank anzeigt.
 * 
 * @author prohlede
 */
public class DatabaseException extends Exception {

    /**
     * Die eineindeutige ID für Serialisierung.
     */
    private static final long serialVersionUID = -7499912411622348365L;

    /**
     * Erzeugt eine neue Exception mit der übergebenen Nachricht.
     * 
     * @param message
     *            die Nachricht der neuen Ausnahme
     */
    public DatabaseException(final String message) {
        super(message);
    }

}
