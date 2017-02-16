/**
 * Klasse gehoert zu dem Komponenten-Paket
 */
package Yellowstone.YellowTable.logic.components;

//Importe
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

/**
 * Definition eines Timeslot. Der Konstruktor muss eine Startzeit und eine Dauer
 * (in Minuten) erhalten. Die Uebergabe eines {@linkplain WEEKDAYS} ist optional
 * (er wird dann standardmaessig auf {@link WEEKDAYS#NONE} gesetzt).
 * 
 * @author prohleder, lam-duy
 */

@Entity
public class Timeslot implements ITimeslot, Comparable<Timeslot> {

	// Attribute

	/**
	 * Der Logger dieser Klasse.
	 */
	@Transient
	final static Logger logger = Logger.getLogger(Timeslot.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int ID;

	/**
	 * Die Dauer des Timeslot
	 */
	private int duration;

	/**
	 * Der Wochentage des Timeslot
	 */
	@Enumerated(EnumType.STRING)
	private WEEKDAYS weekday;

	/**
	 * Die Startstunde des Timeslot
	 */
	private int startHour;
	/**
	 * Die Startminute des Timeslot
	 */
	private int startMinute;
	/**
	 * Die Endstunde des Timeslot
	 */
	private int endHour;
	/**
	 * Die Endminute des Timeslot
	 */
	private int endMinute;

	/**
	 * Die Startstunde des Timeslot als String
	 */
	@Transient
	private String beginTimeslotHour;
	/**
	 * Die Startminute des Timeslot als String
	 */
	@Transient
	private String beginTimeslotMinute;
	/**
	 * Die Startzeit des Timeslot als String
	 */
	@Transient
	private String startTime;
	/**
	 * Die Endstunde des Timeslot als String
	 */
	@Transient
	private String endTimeslotHour;
	/**
	 * Die Endminute des Timeslot als String
	 */
	@Transient
	private String endTimeslotMinute;
	/**
	 * Die Endzeit des Timeslots. als String
	 */
	@Transient
	private String endTime;
	/**
	 * Das Format des Timeslot
	 */
	@Transient
	private static SimpleDateFormat formatTimeslot = new SimpleDateFormat(
			"HH:mm");

	// Konstruktoren.

	/**
	 * Kontruktor fuer die Erstellung eines Timeslots.
	 * 
	 * @param pDuration
	 *            Laenge des Timeslots
	 * @param pWeekday
	 *            Wochentag
	 * @param pBeginTimeslotHour
	 *            Start-Stunde
	 * @param pBeginTimeslotMinute
	 *            Start-Minute
	 */
	public Timeslot(final int pDuration, final WEEKDAYS pWeekday,
			final String pBeginTimeslotHour, final String pBeginTimeslotMinute) {
		setDuration(pDuration);
		setDay(pWeekday);
		setBeginTimeslotHour(pBeginTimeslotHour);
		setBeginTimeslotMinute(pBeginTimeslotMinute);
		setStarttime();
		setEndTime();

		if (logger.isInfoEnabled()) {
			logger.info("Ein Timeslot wurde erstellt mit folgender Dauer: "
					+ pWeekday + ", " + pDuration);
		}
	}

	/**
	 * Konstruktor fuer die Datenbank.
	 */
	public Timeslot() {
		if (logger.isInfoEnabled()) {
			logger.info("Leeres Timeslot-Objekt wurde erstellt.");
		}
	}

	// Getter-Methoden.

	/**
	 * Gibt die generierte ID zurueck.
	 * 
	 * @return time_id
	 */
	public int getTime_id() {
		return ID;
	}

	/**
	 * Gibt die Dauer dieses Timeslot zurueck.
	 * 
	 * @return die Dauer dieses Timeslot in Minuten.
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Gibt den Wochentag dieser Zeitslot-Einheit zurueck
	 * 
	 * @return den Wochentag
	 */
	@Override
	public WEEKDAYS getDay() {
		return weekday;
	}

	/**
	 * Gibt die Beginnstunde des Timeslot zurueck.
	 *
	 * @return die Beginnstunde
	 */
	@Override
	public String getTimeBeginHour() {
		return beginTimeslotHour;
	}

	/**
	 * Gibt die Beginnstunde des Timeslot zurueck.
	 *
	 * @return die Beginnstunde
	 */
	public int getStartHour() {
		return startHour;
	}

	/**
	 * Gibt die Beginminute des Timeslot zurueck.
	 * 
	 * @return die Beginnminute
	 */
	@Override
	public String getTimeBeginMinute() {
		return beginTimeslotMinute;
	}

	/**
	 * Gibt die Beginminute des Timeslot zurueck.
	 * 
	 * @return die Beginnminute
	 */
	public int getStartMinute() {
		return startMinute;
	}

	/**
	 * Gibt die Startzeit dieses Timeslot zurueck.
	 * 
	 * @return die Startzeit
	 */
	@Override
	public String getStarttime() {
		setStarttime();
		return startTime;
	}

	/**
	 * Gibt die Endstunde des Timeslot zurueck.
	 * 
	 * @return die Endstunde
	 */
	public String getEndHour() {
		return endTimeslotHour;
	}

	/**
	 * Gibt die Endstunde des Timeslot zurueck.
	 * 
	 * @return die Endstunde
	 */
	public int getEndHour_int() {
		return endHour;
	}

	/**
	 * Gibt die Endminute des Timeslot zurueck.
	 * 
	 * @return die Endminute
	 */
	public String getEndMinute() {
		return endTimeslotMinute;
	}

	/**
	 * Gibt die Endminute des Timeslot zurueck.
	 * 
	 * @return die Endminute
	 */
	public int getEndMinute_int() {
		return endMinute;
	}

	/**
	 * Gibt die E
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * Die Methode getDurationByStartAndEndtime() soll durch Eingabe von einer
	 * Start- und Endzeit die Dauer des Timeslot berechnen
	 * 
	 * @param pStartHour
	 *            die Startstunde
	 * @param pStartMinute
	 *            die Startminute
	 * @param pEndHour
	 *            die Endstunde
	 * @param pEndMinute
	 *            die Endminute
	 * 
	 * @return bei erfolgreicher Berechnung wird die Dauer zurueckgegeben
	 * 
	 * @throws IllegalArgumentException
	 *             wenn die Parameter die Ueberpruefung der Parameter
	 *             fehlschlaegt oder Fehler beim Parsen der Zeit passiert.
	 * 
	 */
	public static int getDurationByStartAndEndtime(final String pStartHour,
			final String pStartMinute, final String pEndHour,
			final String pEndMinute) {

		int durationOfTimes = 0;

		if (isValidHour(pStartHour) && isValidHour(pEndHour)
				&& isValidMinute(pStartMinute) && isValidMinute(pEndMinute)) {
			try {
				Date date1 = formatTimeslot.parse(pStartHour + ":"
						+ pStartMinute);
				Date date2 = formatTimeslot.parse(pEndHour + ":" + pEndMinute);

				if (date1.compareTo(date2) <= 0) {

					Calendar fstDate = GregorianCalendar.getInstance();
					Calendar sndDate = GregorianCalendar.getInstance();

					fstDate.setTime(date1);
					sndDate.setTime(date2);

					int dh1 = fstDate.get(Calendar.HOUR_OF_DAY);
					int dm1 = fstDate.get(Calendar.MINUTE);
					int dh2 = sndDate.get(Calendar.HOUR_OF_DAY);
					int dm2 = sndDate.get(Calendar.MINUTE);

					durationOfTimes = (dm2 - dm1) + (dh2 - dh1) * 60;
				}
			} catch (ParseException e) {
				throw new IllegalArgumentException("Die Dauer ist ungueltig");
			}
		}
		return durationOfTimes;
	}

	/**
	 * Eine Methode, die die Tage als Strings zurueckgeben.
	 * 
	 * @return die Tage als String
	 */
	@Override
	public String getDaysAsString() {
		if (weekday.toString().equals("MONDAY")) {
			return "Mo";
		} else if (weekday.toString().equals("TUESDAY")) {
			return "Di";
		} else if (weekday.toString().equals("WEDNESDAY")) {
			return "Mi";
		} else if (weekday.toString().equals("THURSDAY")) {
			return "Do";
		} else if (weekday.toString().equals("FRIDAY")) {
			return "Fr";
		} else {
			return "NONE";
		}
	}

	// Ueberpruefung

	/**
	 * Die Methode ueberprueft ob es sich um eine valide Zeituebergabe haelt.
	 * 
	 * @param pHour
	 *            die uebergebene Zeitstunde
	 * 
	 * @return true , wenn der uebergebene Parameter valide ist.
	 */
	private static boolean isValidHour(final String pHour) {
		return pHour != null && pHour.trim().length() == 2
				&& pHour.matches("[0-9]+") && Integer.parseInt(pHour) >= 0
				&& Integer.parseInt(pHour) < 24;
	}

	/**
	 * Die Methode ueberprueft ob es sich um eine valide Zeituebergabe haelt.
	 * 
	 * @param pMinute
	 *            die uebergebene Zeitsminute
	 * 
	 * @return true , wenn der uebergebene Parameter valide ist.
	 */
	private static boolean isValidMinute(final String pMinute) {
		return pMinute != null && pMinute.trim().length() == 2
				&& pMinute.matches("[0-9]+") && Integer.parseInt(pMinute) >= 0
				&& Integer.parseInt(pMinute) < 60;
	}

	// Setter-Methoden

	/**
	 * Setzt die Dauer des Timeslot.
	 * 
	 * @param pDuration
	 *            die neue Dauer in Minuten
	 * @throws IllegalArgumentException
	 *             wenn pDuration kleiner als 0, kein Vielfaches der
	 *             Granuleritaet oder nicht mehr im Gesamtplanungszeitraum ist.
	 */
	@Override
	public void setDuration(final int pDuration) {
		if (pDuration > 0) {
			duration = pDuration;
			if (logger.isInfoEnabled()) {
				logger.info("Der Dauer des Timeslots wurde geaendert: "
						+ pDuration);
			}
		} else
			throw new IllegalArgumentException(
					"Die Dauer darf nicht negativ sein.");
	}

	/**
	 * Diese Methode setzt die Startstunde des Timeslot.
	 * 
	 * @param pBeginTimeslotHour
	 *            die Startstunde
	 * 
	 * @throws IllegalArgumentException
	 *             wenn der uebergebene Parameter keine valide Zeitstunde ist
	 */
	@Override
	public void setBeginTimeslotHour(final String pBeginTimeslotHour) {
		if (isValidHour(pBeginTimeslotHour)) {
			beginTimeslotHour = pBeginTimeslotHour;
			startHour = Integer.parseInt(pBeginTimeslotHour);
			if (logger.isInfoEnabled()) {
				logger.info("Der Beginstunde des Timeslots wurde geaendert: "
						+ pBeginTimeslotHour);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter : " + pBeginTimeslotHour);
			}
			throw new IllegalArgumentException(
					"Die Startstunde ist nicht im sinnvollen Bereich");
		}
	}

	/**
	 * Diese Methode setzt die Startminute des Timeslot.
	 * 
	 * @param pBeginTimeslotMinute
	 *            die Startminute
	 * 
	 * @throws IllegalArgumentException
	 *             wenn der uebergebene Parameter keine valide Zeitminute ist
	 */
	@Override
	public void setBeginTimeslotMinute(final String pBeginTimeslotMinute) {
		if (isValidMinute(pBeginTimeslotMinute)) {
			beginTimeslotMinute = pBeginTimeslotMinute;
			startMinute = Integer.parseInt(pBeginTimeslotMinute);
			if (logger.isInfoEnabled()) {
				logger.info("Der Beginstunde des Timeslots wurde geaendert: "
						+ pBeginTimeslotMinute);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter : " + pBeginTimeslotMinute);
			}
			throw new IllegalArgumentException(
					"Die Startminute ist nicht im sinnvollen Bereich");
		}
	}

	/**
	 * Setzt die Startzeit des Timeslot aus der Startstunde und Startminute.
	 * 
	 * @throws IllegalArgumentException
	 *             wenn die Startzeit nicht gesetzt werden kann.
	 */
	@Override
	public void setStarttime() {
		try {
			startTime = addStringHour(startHour) + ":"
					+ addStringMinute(startMinute);
			if (logger.isInfoEnabled()) {
				logger.info("Die Startzeit des Timeslots wurde neu gesetzt");
			}
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Die Startzeit konnte nicht gesetzt werden: "
							+ e.getMessage());
		}
	}

	/**
	 * Die Methode setzt die Endzeit, die Endstunde und die Endminute
	 * 
	 * @throws IllegalArgumentException
	 *             wenn die Endzeit nicht gesetzt werden kann.
	 */
	public void setEndTime() {
		try {
			Date date = formatTimeslot.parse(startTime);
			Calendar fstDate = GregorianCalendar.getInstance();
			fstDate.setTime(date);

			int dateHour = fstDate.get(Calendar.HOUR_OF_DAY);
			int dateMinute = fstDate.get(Calendar.MINUTE) + getDuration();

			while (dateMinute >= 60) {
				dateHour++;
				dateMinute -= 60;
				while (dateHour >= 24) {
					dateHour = 0;
				}
			}

			endTimeslotHour = addStringHour(dateHour);
			endTimeslotMinute = addStringMinute(dateMinute);

			endHour = dateHour;
			endMinute = dateMinute;

			endTime = endTimeslotHour + ":" + endTimeslotMinute;

		} catch (ParseException e) {
			throw new IllegalArgumentException("Fehler beim Parsen der Zeit");
		}
	}

	/**
	 * Setzt den Tag des Timeslot.
	 * 
	 * @param pWeekday
	 *            der Wochentag
	 * 
	 * @throws IllegalArgumentException
	 *             wenn pWeekday {@code null} ist.
	 */
	@Override
	public void setDay(final WEEKDAYS pWeekday) {
		if (pWeekday != null) {
			weekday = pWeekday;
			if (logger.isInfoEnabled()) {
				logger.info("Der Tag des Timeslots wurde geaendert: "
						+ pWeekday);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.error("Fehler im Parameter pWeekday (null) ");
			}
			throw new IllegalArgumentException(
					"Bitte geben Sie einen gueltigen Wochentag an.");
		}
	}

	// Contains Methoden

	/**
	 * Gibt {@code true} zurueck, wenn der uebergebene Zeitslot nicht innerhalb,
	 * sondern mit Start oder Endzeit ueber den Zeitraum dieses Zeitslots hinaus
	 * geht. Bei unterschiedlichen {@linkplain WEEKDAYS} wird {@code false}
	 * zurueckgegeben, ausser einer der beiden WEEKDAYS ist NONE und die erste
	 * Bedingung ist erfuellt.
	 * 
	 * @param pOtherSlot
	 *            der zu vergleichende Timeslot
	 * 
	 * @return true, wenn ein Zeitkonflikt zwischen beiden Zeitslots besteht.
	 */
	@Override
	public boolean containsConflict(final Timeslot pOtherSlot) {
		try {
			if (this.getDay() == WEEKDAYS.NONE
					|| pOtherSlot.getDay() == WEEKDAYS.NONE) {
				return false;
			}
			if (getDay() == pOtherSlot.getDay()) {

				Date dateStart = formatTimeslot.parse(getString(startHour,
						startMinute));
				Date dateEnd = formatTimeslot.parse(getString(endHour,
						endMinute));
				Date dateStart2 = formatTimeslot
						.parse(getString(pOtherSlot.getStartHour(),
								pOtherSlot.getStartMinute()));
				Date dateEnd2 = formatTimeslot.parse(getString(
						pOtherSlot.getEndHour_int(),
						pOtherSlot.getEndMinute_int()));

				return (dateStart2.getTime() < dateStart.getTime() && dateStart
						.getTime() < dateEnd2.getTime())
						|| (dateStart2.getTime() < dateEnd.getTime() && dateEnd
								.getTime() < dateEnd2.getTime())
						|| (dateStart.getTime() <= dateStart2.getTime() && dateEnd
								.getTime() >= dateEnd2.getTime());

			}
			return false;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Fehler bei der Zeit");
		}
	}

	/**
	 * Vergleicht zwei Timeslots miteinander. Falls die Dauer, die Start und
	 * Endzeit, sowie der Wochentag identisch sind, wird true zurueckgegeben.
	 * return boolean ob identisch
	 */
	@Override
	public boolean equals(final Object pObject) {
		if (pObject != null && pObject instanceof Timeslot) {
			if (((Timeslot) pObject).getDuration() == getDuration()) {
				if (((Timeslot) pObject).getStartHour() == getStartHour()
						&& ((Timeslot) pObject).getStartMinute() == getStartMinute()) {
					if (((Timeslot) pObject).getDaysAsString().equals(
							getDaysAsString())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * ueberprueft, ob dieser Zeitslot in dem uebergebenen Liegt. Wochentage
	 * sind außer Acht gelassen.
	 * 
	 * @param pTimeframe
	 *            TimeFrame der Planung
	 * @return boolean, ob er im uebergebenen Timeframe liegt.
	 */
	public boolean isInTimeframe(final Timeslot pTimeframe) {
		try {
			if (pTimeframe == null) {
				throw new IllegalArgumentException(
						"Als Timeframe wurde null uebergeben.");
			}
			if ((pTimeframe.getDay() != WEEKDAYS.NONE && pTimeframe.getDay() == getDay())
					|| pTimeframe.getDay() == WEEKDAYS.NONE) {
				Date timeslotStart = formatTimeslot.parse(getString(startHour,
						startMinute));
				Date timeslotEnd = formatTimeslot.parse(getString(endHour,
						endMinute));
				Date frameStart = formatTimeslot
						.parse(getString(pTimeframe.getStartHour(),
								pTimeframe.getStartMinute()));
				Date frameEnd = formatTimeslot.parse(getString(
						pTimeframe.getEndHour_int(),
						pTimeframe.getEndMinute_int()));

				return (frameStart.getTime() <= timeslotStart.getTime() && timeslotEnd
						.getTime() <= frameEnd.getTime());
			}
			return false;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Fehler beim Eingabe der Zeit");
		}
	}

	/**
	 * Die Zeiten als String
	 */
	public static ArrayList<String> getDayTableSlots(final int pStartHours,
			final int pStartMinutes, final int pEndHours,
			final int pEndMinutes, final int pGranularity) {

		int currentHours = pStartHours;
		int currentMinutes = pStartMinutes;
		ArrayList<String> timeslot = new ArrayList<String>();

		if (currentHours == pStartHours && currentMinutes == pStartMinutes) {
			timeslot.add(getString(currentHours, currentMinutes));
		}

		while (currentHours != pEndHours) {
			currentMinutes += pGranularity;

			if (currentMinutes >= 60) {
				currentHours++;
				currentMinutes -= 60;
			}
			if (currentHours >= 24) {
				currentHours = 0;
			}
			timeslot.add(getString(currentHours, currentMinutes));
		}
		while (currentMinutes < pEndMinutes) {
			currentMinutes += pGranularity;

			timeslot.add(getString(currentHours, currentMinutes));
		}

		return timeslot;
	}

	private static String getString(final int pHours, final int pMinutes) {
		String curHours = addStringHour(pHours);
		String curMinutes = addStringMinute(pMinutes);

		return curHours + ":" + curMinutes;
	}

	public static String addStringHour(final int pHours) {
		String curHours = String.valueOf(pHours);

		if (pHours < 10 && pHours >= 0) {
			curHours = "0" + String.valueOf(pHours);
			if (isValidHour(curHours)) {
				return curHours;
			} else {
				throw new IllegalArgumentException("Die Stunde ist ungueltig");
			}
		}
		return curHours;
	}

	public static String addStringMinute(final int pMinutes) {
		String curMinutes = String.valueOf(pMinutes);

		if (pMinutes < 10 && pMinutes >= 0) {
			curMinutes = "0" + String.valueOf(pMinutes);
			if (isValidMinute(curMinutes)) {
				return curMinutes;
			} else {
				throw new IllegalArgumentException("Die Minute ist ungueltig");
			}
		}
		return curMinutes;
	}

	@Override
	public boolean equals(final Timeslot pTimeslot) {
		if (this.startMinute == pTimeslot.getStartMinute()
				&& this.startHour == pTimeslot.getStartHour()
				&& this.duration == pTimeslot.getDuration()
				&& this.getDay() == pTimeslot.getDay()) {
			return true;
		} else {
			return false;
		}
	}

	public int compareTo(final Timeslot t) {
		int thisMinutes = getStartHour() * 60 + getStartMinute();
		int thatMinutes = t.getStartHour() * 60 + t.getStartMinute();
		if (thatMinutes > thisMinutes) {

			return -1;
		}

		if (thisMinutes > thatMinutes) {
			return 1;
		}

		if (thisMinutes == thatMinutes) {
			return 0;
		}

		return this.compareTo(t);
	}

}
