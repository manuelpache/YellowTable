package Yellowstone.YellowTable.logic.components;

/**
 * Spiegelt in unserer Logik ein Projekt wieder.
 * 
 * Projekte lassen sich ueber die grafische Oberflaeche laden und speichern.
 * 
 * Dabei bekommt der Konstruktor eines Projekts eine Config mit den
 * Standardeinstellungen und alle Planungseinheiten, die in dem Projekt
 * existieren uebergeben.
 * 
 * @author tomlewandowski, prohleder
 *
 */
public interface IProject {

	/**
	 * Gibt die Config und somit die Parameter der Standardeinstellungen.
	 * 
	 * @return die Config.
	 */
	public Config getConfig();

	/**
	 * Gibt eine Liste aller Planungseinheiten zurueck.
	 * 
	 * @return eine Liste aller Planungseinheiten.
	 */
	public Unit[] getAllUnits();

	/**
	 * Gibt den Titel fuer das Projekt zurueck, die entscheidend fuer die
	 * Persistenz ist.
	 * 
	 * @return den Titel der Klasse.
	 */
	public String getTitle();

	/**
	 * Setzt die Config und somit die Parameter der Standardeinstellungen neu.
	 * 
	 * @param pConfig
	 *            eine Config, die ueber die Parameter aus den
	 *            Standardeinstellungen verfuegt.
	 * @throws IllegalArgumentException
	 *             wenn {@code null} uebergeben wird.
	 */
	public void setConfig(final Config pConfig) throws IllegalArgumentException;

	/**
	 * Setzt den Titel fuer das Projekt neu.
	 * 
	 * @param pTitle
	 *            der neue Titel.
	 * @throws IllegalArgumentException
	 *             falls der Titel eine ungültige Zeichenkette enthält, oder
	 *             schon in der DB enthalten ist.
	 */
	public void setTitle(final String pTitle) throws IllegalArgumentException;

}
