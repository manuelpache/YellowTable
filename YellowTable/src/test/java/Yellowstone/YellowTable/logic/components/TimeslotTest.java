package Yellowstone.YellowTable.logic.components;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Den Blackbox-Test der Klasse Timeslot von lamduy.
 * 
 * @author erikke , Le
 *
 */
public class TimeslotTest {

	/**
	 * 5 verschiedene Timeslots zum Testen der einzelnen Methoden Alle 5
	 * Timeslot haben unterschiedliche Startzeiten und Dauer.
	 */
	Timeslot time1 = new Timeslot(90, WEEKDAYS.MONDAY, "08", "00");
	Timeslot time2 = new Timeslot(30, WEEKDAYS.MONDAY, "09", "00");
	Timeslot time3 = new Timeslot(100, WEEKDAYS.MONDAY, "09", "30");
	Timeslot time4 = new Timeslot(50, WEEKDAYS.MONDAY, "09", "20");
	Timeslot time5 = new Timeslot();

	/**
	 * Der Test soll ueberpruefen, ob die Methode getDuration(), die richtige
	 * Dauer zurueckgibt.
	 */
	@Test
	public void testGetDuration() {
		assertTrue(90 == time1.getDuration());
		assertTrue(30 == time2.getDuration());

		assertFalse(50 == time3.getDuration());
		assertFalse(80 == time4.getDuration());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getDay(), den richtigen
	 * Wochentag zurueckgibt.
	 */
	@Test
	public void testGetDay() {
		assertTrue(time1.getDay().equals(WEEKDAYS.MONDAY));
		assertTrue(time2.getDay().equals(WEEKDAYS.MONDAY));
	
		assertFalse(time3.getDay().equals(WEEKDAYS.FRIDAY));
		assertFalse(time4.getDay().equals(WEEKDAYS.WEDNESDAY));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode getStarttime(), die richtige
	 * Startzeit zurueckgibt.
	 */
	@Test
	public void testGetStarttime() {
		assertTrue(time1.getStarttime().equals("08:00"));
		assertTrue(time2.getStarttime().equals("09:00"));

		assertFalse(time3.getStarttime().equals("09:20"));
		assertFalse(time4.getStarttime().equals("09:30"));
	}

	/**
	 * Der Test ueberprueft, ob die Methode getEndTime() die richtige Endzeit
	 * zurueckgibt.
	 */
	@Test
	public void testGetEndTime() {
		assertEquals("09:30", time1.getEndTime());
		assertEquals("09:30", time2.getEndTime());
		assertEquals("11:10", time3.getEndTime());
		assertEquals("10:10", time4.getEndTime());
	
		assertEquals("09", time1.getEndHour());
		assertEquals("30", time1.getEndMinute());
		assertEquals("09", time2.getEndHour());
		assertEquals("30", time2.getEndMinute());
		assertEquals("11", time3.getEndHour());
		assertEquals("10", time3.getEndMinute());
		assertEquals("10", time4.getEndHour());
		assertEquals("10", time4.getEndMinute());
	
	}

	/**
	 * Der Test ueberprueft, ob die Methode getDurationByStartAndEnd() die
	 * richtige Dauer zurueckgibt.
	 */
	@Test
	public void testGetDurationByStartAndEnd() {
		assertEquals(70,
				Timeslot.getDurationByStartAndEndtime("10", "30", "11", "40"));
		assertEquals(50,
				Timeslot.getDurationByStartAndEndtime("10", "30", "11", "20"));
		assertEquals(60,
				Timeslot.getDurationByStartAndEndtime("10", "30", "11", "30"));
		assertEquals(130,
				Timeslot.getDurationByStartAndEndtime("10", "30", "12", "40"));
		assertEquals(0,
				Timeslot.getDurationByStartAndEndtime("10", "30", "10", "30"));
	}

	/**
	 * Die Methode getDaysAsString() gibt die Wochentage als deutsche Abkuerzung
	 * zurueck.
	 */
	@Test
	public void testGetDaysAsString() {
		time1.setDay(WEEKDAYS.TUESDAY);
		assertEquals("Di", time1.getDaysAsString());
		time1.setDay(WEEKDAYS.THURSDAY);
		assertEquals("Do", time1.getDaysAsString());
		time2.setDay(WEEKDAYS.MONDAY);
		assertEquals("Mo", time2.getDaysAsString());
		time3.setDay(WEEKDAYS.WEDNESDAY);
		assertEquals("Mi", time3.getDaysAsString());
		time4.setDay(WEEKDAYS.FRIDAY);
		assertEquals("Fr", time4.getDaysAsString());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setDuration(), die richtige
	 * Dauer setzt.
	 */
	@Test
	public void testSetDuration() {
		time1.setDuration(20);
		time2.setDuration(30);
		time3.setDuration(40);
		time4.setDuration(50);
		assertTrue(20 == time1.getDuration());
		assertTrue(30 == time2.getDuration());
		assertFalse(10 == time3.getDuration());
		assertFalse(30 == time4.getDuration());
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setDuration() bei einer
	 * Uebergabe von 0 eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDuration_zero_IllegalArgumentException() {
		time5.setDuration(0);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setDuration() bei einer
	 * Uebergabe von einer negativen Zahl eine {@link IllegalArgumentException}
	 * ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetDuration_negative_IllegalArgumentException() {
		time5.setDuration(-4);
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), die richtige
	 * Startzeit setzt. Diese setzt.
	 */
	@Test
	public void testSetStarttime() {
		time1.setBeginTimeslotHour("10");
		time1.setBeginTimeslotMinute("30");
		time1.setStarttime();
		time2.setBeginTimeslotHour("08");
		time2.setBeginTimeslotMinute("10");
		time2.setStarttime();
		time3.setBeginTimeslotHour("14");
		time3.setBeginTimeslotMinute("45");
		time3.setStarttime();
		time4.setBeginTimeslotHour("08");
		time4.setBeginTimeslotMinute("03");
		time4.setStarttime();

		assertEquals("10", time1.getTimeBeginHour());
		assertEquals("08", time2.getTimeBeginHour());
		assertEquals("14", time3.getTimeBeginHour());
		assertEquals("08", time4.getTimeBeginHour());

		assertEquals("30", time1.getTimeBeginMinute());
		assertEquals("10", time2.getTimeBeginMinute());
		assertEquals("45", time3.getTimeBeginMinute());
		assertEquals("03", time4.getTimeBeginMinute());

		assertTrue(time1.getStarttime().equals("10:30"));
		assertTrue(time2.getStarttime().equals("08:10"));
		assertTrue(time3.getStarttime().equals("14:45"));
		assertTrue(time4.getStarttime().equals("08:03"));
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), bei einer null
	 * Eingabe eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStarttime_null_IllegalArgumentException() {
		time5.setBeginTimeslotHour(null);
		time5.setBeginTimeslotMinute(null);
		time5.setStarttime();
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), bei einer leeren
	 * Eingabe eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStarttime_empty_IllegalArgumentException() {
		time5.setBeginTimeslotHour("");
		time5.setBeginTimeslotMinute("");
		time5.setStarttime();
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), bei einer Eingabe
	 * von nicht Zahlen eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStarttime_notNumber_IllegalArgumentException() {
		time5.setBeginTimeslotHour("ab");
		time5.setBeginTimeslotMinute("bc");
		time5.setStarttime();
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), bei einer Eingabe
	 * von nur einer Zahl eine {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStarttime_toShort_IllegalArgumentException() {
		time5.setBeginTimeslotHour("1");
		time5.setBeginTimeslotMinute("5");
		time5.setStarttime();
	}

	/**
	 * Der Test soll ueberpruefen, ob die Methode setStarttime(),
	 * setBeginTimeslotHour() und setBeginTimeslotMinute(), bei einer Eingabe
	 * von Zahlen ueber 23 bei Stunden und ueber 60 bei Minuten eine
	 * {@link IllegalArgumentException} ausloest.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetStarttime_notBetween_IllegalArgumentException() {
		time5.setBeginTimeslotHour("45");
		time5.setBeginTimeslotMinute("71");
		time5.setStarttime();
	}

	/**
	 * Der test soll ueberpruefen, ob die Methode setDay() die Tage richtig
	 * gesetzt hat.
	 */
	@Test
	public void testSetDay() {
		time1.setDay(WEEKDAYS.TUESDAY);
		time2.setDay(WEEKDAYS.FRIDAY);
		time3.setDay(WEEKDAYS.WEDNESDAY);
		time4.setDay(WEEKDAYS.THURSDAY);
		assertTrue(time1.getDay().equals(WEEKDAYS.TUESDAY));
		assertTrue(time2.getDay().equals(WEEKDAYS.FRIDAY));
		assertFalse(time3.getDay().equals(WEEKDAYS.THURSDAY));
		assertFalse(time4.getDay().equals(WEEKDAYS.WEDNESDAY));

	}

	/**
	 * Der Test ueberprueft, ob die Methode containsConflict() einen
	 * Zeitkonflikt findet.
	 */
	@Test
	public void testContainsConflict() {
		assertTrue(time2.containsConflict(time1));
		assertFalse(time3.containsConflict(time1));
		assertTrue(time4.containsConflict(time1));
		assertTrue(time4.containsConflict(time2));
		assertFalse(time2.containsConflict(time3));
		assertTrue(time2.containsConflict(time2));
	}

	/**
	 * Der Test ueberprueft, ob bei der Eingabe von {@code NULL} in der Methode
	 * setWeekday() eine IllegalArgumentException ausgeloest wird.
	 */

	@Test(expected = IllegalArgumentException.class)
	public void testSetWeekday_null_IllegalArgumentException() {
		time5.setDay(null);
	}

	/**
	 * Der Test ueberprueft, ob die Methode getDayTableSots() die richtigen
	 * Zeitlslots zurueckgibt.
	 */
	@Test
	public void testGetDayTableSlots() {
		ArrayList<String> timeslot = Timeslot
				.getDayTableSlots(7, 0, 17, 00, 15);
		assertTrue(timeslot.get(0).equals("07:00"));
		assertTrue(timeslot.get(1).equals("07:15"));
		assertTrue(timeslot.get(2).equals("07:30"));
		assertTrue(timeslot.get(3).equals("07:45"));
		assertTrue(timeslot.get(4).equals("08:00"));
	}
}
