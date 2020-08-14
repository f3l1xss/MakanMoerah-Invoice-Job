package com.felixstanley.makanmoerahinvoicejob.dao;

import com.felixstanley.makanmoerahinvoicejob.entity.Booking;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.BookingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Felix
 */
public interface BookingDao extends JpaRepository<Booking, Integer> {

  @Query("SELECT b.restaurantId FROM Booking b WHERE b.bookingStatus = :bookingStatus "
      + "AND month(b.confirmedDate) = :month AND year(b.confirmedDate) = :year "
      + "GROUP BY b.restaurantId ORDER BY b.restaurantId ASC")
  List<Integer> findRestaurantIdByMonthYear(@Param("bookingStatus") BookingStatus bookingStatus,
      @Param("month") Integer month,
      @Param("year") Integer year);

}
