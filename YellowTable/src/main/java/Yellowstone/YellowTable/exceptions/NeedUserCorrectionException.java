package Yellowstone.YellowTable.exceptions;

/**
 * Exception die auf einen Fehler deutet, der nur vom Benutzer behoben werden
 * kann.
 * 
 * @author apag
 *
 */
public class NeedUserCorrectionException extends RuntimeException {

	/**
	 * Die eineindeutige ID. Oder so.
	 */
	private static final long serialVersionUID = 161241419420037946L;

	/**
	 * Erzeugt eine Exception mit einer Meldung, die dem Nutzer angezeigt wird.
	 * Sie sollte einen Hinweis auf das Problem und/oder dessen Lösung
	 * beinhalten.
	 * 
	 * @param pDescription
	 *            die anzuzeigende Meldung
	 */
	public NeedUserCorrectionException(final String pDescription) {
		super(pDescription);
	}
}
