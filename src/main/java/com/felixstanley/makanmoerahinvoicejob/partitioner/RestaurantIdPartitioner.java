package com.felixstanley.makanmoerahinvoicejob.partitioner;

import com.felixstanley.makanmoerahinvoicejob.constant.Constants;
import com.felixstanley.makanmoerahinvoicejob.dao.BookingDao;
import com.felixstanley.makanmoerahinvoicejob.dao.RestaurantDao;
import com.felixstanley.makanmoerahinvoicejob.dao.UsersDao;
import com.felixstanley.makanmoerahinvoicejob.entity.Restaurant;
import com.felixstanley.makanmoerahinvoicejob.entity.Users;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.BookingStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

/**
 * @author Felix
 */
@AllArgsConstructor
@Slf4j
public class RestaurantIdPartitioner implements Partitioner {

  private static final String PARTITION_KEY = "partition";

  private BookingDao bookingDao;
  private RestaurantDao restaurantDao;
  private UsersDao usersDao;

  private Integer month;
  private Integer year;

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    log.info("Obtaining RestaurantId from Completed Bookings of Month: {}, Year: {}", month, year);
    List<Integer> restaurantIds = bookingDao
        .findRestaurantIdByMonthYear(BookingStatus.COMPLETED, month, year);
    log.info("Obtained following restaurant ids: {}", restaurantIds);
    Map<String, ExecutionContext> map = new HashMap<>(gridSize);
    int i = 0;
    for (Integer id : restaurantIds) {
      log.info("Obtaining Restaurant with Id: {}", id);
      Restaurant restaurant = restaurantDao.findById(id).orElse(null);
      log.info("Obtained Restaurant: {}", restaurant);
      log.info("Obtaining Users with Id: {}", restaurant.getUsersId());
      Users users = usersDao.findById(restaurant.getUsersId()).orElse(null);
      log.info("Obtained Users: {}", users);
      map.put(PARTITION_KEY + i, getExecutionContext(restaurant, users, i));
      i++;
    }
    return map;
  }

  private ExecutionContext getExecutionContext(Restaurant restaurant, Users users,
      Integer partitionNumber) {
    ExecutionContext executionContext = new ExecutionContext();
    executionContext
        .put(Constants.PARTITION_STRING_CONTEXT_KEY_NAME, PARTITION_KEY + partitionNumber);
    executionContext.put(Constants.RESTAURANT_EXECUTION_CONTEXT_KEY_NAME, restaurant);
    executionContext.put(Constants.USERS_EXECUTION_CONTEXT_KEY_NAME, users);
    executionContext.put(Constants.MONTH_CONTEXT_KEY_NAME, month);
    executionContext.put(Constants.YEAR_CONTEXT_KEY_NAME, year);
    return executionContext;
  }

}
