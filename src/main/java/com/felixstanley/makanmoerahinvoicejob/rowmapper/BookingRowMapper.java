package com.felixstanley.makanmoerahinvoicejob.rowmapper;

import com.felixstanley.makanmoerahinvoicejob.entity.Booking;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Felix
 */
public class BookingRowMapper implements RowMapper<Booking> {

  // Booking Column Names
  private static final String CONFIRMED_DATE_COLUMN_NAME = "confirmed_date";
  private static final String TIMESLOT_COLUMN_NAME = "timeslot";
  private static final String BOOKING_CODE_COLUMN_NAME = "booking_code";
  private static final String NUM_OF_PEOPLE_COLUMN_NAME = "num_of_people";

  @Override
  public Booking mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Booking booking = new Booking();
    booking.setConfirmedDate(resultSet.getDate(CONFIRMED_DATE_COLUMN_NAME).toLocalDate());
    booking.setTimeslot(resultSet.getShort(TIMESLOT_COLUMN_NAME));
    booking.setBookingCode(resultSet.getString(BOOKING_CODE_COLUMN_NAME));
    booking.setNumOfPeople(resultSet.getShort(NUM_OF_PEOPLE_COLUMN_NAME));
    return booking;
  }
}
