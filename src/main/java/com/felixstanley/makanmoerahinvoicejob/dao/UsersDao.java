package com.felixstanley.makanmoerahinvoicejob.dao;

import com.felixstanley.makanmoerahinvoicejob.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Felix
 */
public interface UsersDao extends JpaRepository<Users, Integer> {

}
