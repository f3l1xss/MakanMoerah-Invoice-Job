package com.felixstanley.makanmoerahinvoicejob.dao;

import com.felixstanley.makanmoerahinvoicejob.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Felix
 */
public interface RestaurantDao extends JpaRepository<Restaurant, Integer> {

}
