package Yellowstone.YellowTable.logic.components;

import java.text.ParseException;

/**
 * Definition eines Timeslot. Der Konstruktor muss eine Startzeit und eine Dauer
 * (in Minuten) erhalten. Die Uebergabe eines {@linkplain WEEKDAYS} ist optional
 * (er wird dann standardmaessig auf {@link WEEKDAYS#NONE} gesetzt).
 * 
 * @author apag, prohleder, lam-duy
 */
public interface ITimeslot {

	/**
	 * Gibt die Dauer dieses Timeslot zurueck.
	 * 
	 * @return die Dauer dieses Timeslot in Minuten.
	 */
	public int getDuration();

	/**
	 * Gibt die Startzeit dieses Timeslot zurueck.
	 * 
	 * @return die Startzeit
	 */
	public String getStarttime();

	/**
	 * Gibt den Wochentag dieser Zeitslot-Einheit zurück
	 * 
	 * @return den Wochentag
	 */
	public WEEKDAYS getDay();
	
	/**
	 * Setzt die Dauer des Timeslot.
	 * 
	 * @param pDuration
	 *            die neue Dauer in Minuten
	 * @throws IllegalArgumentException
	 *             wenn pDuration kleiner als 0, kein Vielfaches der
	 *             Granuleritaet oder nicht mehr im Gesamtplanungszeitraum ist.
	 */
	public void setDuration(final int pDuration)
			throws IllegalArgumentException;

	/**
	 * Setzt die neue Startzeit.
	 * 
	 * @param pStarttime
	 *            die neue Startzeit.
	 * @throws IllegalArgumentException
	 *             wenn dieser Timeslot nicht im Gesamtplanungszeitraum liegt.
	 */
	public void setStarttime()
			throws IllegalArgumentException;

	/**
	 * Setzt den Wochentag dieser Zeitslot-Einheit.
	 * 
	 * @return den Wochentag	 
	 * @throws IllegalArgumentException
	 *             wenn dieser Wochentag nicht im Planungszeitraum liegt.
	 */
	public void setDay(final WEEKDAYS pWeekday) throws IllegalArgumentException;
	
	
	/**
	 * Gibt {@code true} zurueck, wenn pOtherSlot komplett von diesem Timeslot
	 * abgedeckt wird. Bei unterschiedlichen {@linkplain WEEKDAYS} wird
	 * {@code false} zurueckgegeben, ausser einer der beiden WEEKDAYS ist NONE
	 * und die erste Bedingung ist erfuellt.
	 * 
	 * @param pOtherSlot
	 * @return boolean-Wert, ob der übergebene Zeitslot in diesem Zeitslot liegt.
	 * @throws ParseException 
	 */
	//public boolean contains(final Timeslot pOtherSlot);
	
	/**
	 * Gibt {@code true} zurueck, wenn der übergebene Zeitslot nicht innerhalb,
	 * sondern mit Start oder Endzeit über den Zeitraum dieses Zeitslots 
	 * hinaus geht. Bei unterschiedlichen {@linkplain WEEKDAYS} wird
	 * {@code false} zurueckgegeben, ausser einer der beiden WEEKDAYS ist NONE
	 * und die erste Bedingung ist erfuellt.
	 * 
	 * @param pOtherSlot
	 * @return boolean-Wert, ob ein Zeitkonflikt zwischen beiden Zeitslots besteht.
	 */
	public boolean containsConflict(final Timeslot pOtherSlot);

	
	/** TODO: NEU
	 * Gibt die Startstunde dieser Zeitslot-Einheit zurück
	 * 
	 * @return die Startstunde
	 */
	public String getTimeBeginHour();

	/** TODO: NEU
	 * Gibt den Startminute dieser Zeitslot-Einheit zurück
	 * 
	 * @return die Startminute
	 * @throws ParseException 
	 */
	public String getTimeBeginMinute();

	/** TODO:NEU
	 * Setzt die Startstunde dieser Zeitslot-Einheit.
	 * 
	 * @param pBeginTimeslotHour
	 * 			die Startstunde
	 * @throws IllegalArgumentException
	 */
	public void setBeginTimeslotHour(String pBeginTimeslotHour)
			throws IllegalArgumentException;

	/** TODO:NEU
	 * Setzt die Startminute dieser Zeitslot-Einheit.
	 * 
	 * @param pBeginTimeslotMinute
	 * 			die Startminute
	 * @throws IllegalArgumentException
	 */
	public void setBeginTimeslotMinute(String pBeginTimeslotMinute)
			throws IllegalArgumentException;


	/**TODO: NEU
	 * Gibt den Tag als String zurueck.
	 * 
	 * @return den Tag als String
	 */
	public String getDaysAsString();

	boolean equals(Timeslot pTimeslot);
	
}
