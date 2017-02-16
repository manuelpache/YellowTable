package Yellowstone.YellowTable.logic.components;

/**
 * Hiermit werden im gesamten System die Wochentage verwaltet. NONE wird
 * verwendet, wenn in Timeslots nur der Zeitraum wichtig ist und nicht der Tag.
 * 
 * @author apag
 */
public enum WEEKDAYS {
	NONE(" "), MONDAY("Montag"), TUESDAY("Dienstag"), WEDNESDAY("Mittwoch"), THURSDAY(
			"Donnerstag"), FRIDAY("Freitag"), SATURDAY("Samstag"), SUNDAY(
			"Sonntag");

	private final String title;

	WEEKDAYS(final String pTitle) {
		title = pTitle;
	}

	/**
	 * Gibt einen String mit dem Namen des Wochentags zurueck.
	 * 
	 * @return den Namen des Wochentags.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gibt den Wochentag mit dem gegebenen Titel zurueck, sonst
	 * <code>null</code>.
	 * 
	 * @param pTitle
	 *            der Titel des gesuchten Tages
	 * @return den Wochentag zu dem Titel oder <code>null</code> falls kein
	 *         passender Tag existiert
	 */
	public static WEEKDAYS getByTitle(final String pTitle) {
		for (WEEKDAYS w : values()) {
			if (w.getTitle().equals(pTitle)) {
				return w;
			}
		}

		return null;
	}
}
