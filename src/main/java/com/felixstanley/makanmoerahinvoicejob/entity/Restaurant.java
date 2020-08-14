package com.felixstanley.makanmoerahinvoicejob.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Data;

/**
 * @author Felix
 */
@Entity
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Restaurant extends AbstractEntity implements Serializable {

  private String name;

  private String address;

  private String city;

  @Column(name = "price_category")
  private Short priceCategory;

  @Column(name = "users_id")
  private Integer usersId;

}
