package com.felixstanley.makanmoerahinvoicejob.entity;

import com.felixstanley.makanmoerahinvoicejob.entity.enums.BookingStatus;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 * @author Felix
 */
@Entity
@Data
public class Booking extends AbstractEntity implements Serializable {

  @Column(name = "restaurant_id")
  private Integer restaurantId;

  @Column(name = "confirmed_date")
  private LocalDate confirmedDate;

  private Short timeslot;

  @Column(name = "booking_code")
  private String bookingCode;

  @Column(name = "num_of_people")
  private Short numOfPeople;

  @Column(name = "booking_status")
  @Enumerated
  private BookingStatus bookingStatus;

}
