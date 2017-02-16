package Yellowstone.YellowTable.logic.components;

/**
 * Wird genutzt, um zwischen Schwierigkeitsgraden von Stundeninhalten zu
 * unterscheiden und die Rhythmisierung des Unterrichts darzustellen.
 * 
 * @author apag
 */
public enum DIFFICULTY {
	SOFT("Weich"), HARD("Hart");

	private final String title;

	DIFFICULTY(final String pTitle) {
		title = pTitle;
	}

	/**
	 * Gibt einen bezeichnenden String fuer diesen Schwierigkeitgrad zurueck.
	 * 
	 * @return den Titel der Schwierigkeit
	 */
	public String getTitle() {
		return title;
	}
}
