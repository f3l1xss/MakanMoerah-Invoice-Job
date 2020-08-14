package com.felixstanley.makanmoerahinvoicejob.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Felix
 */
@Embeddable
@Data
@EqualsAndHashCode
public class InvoiceId implements Serializable {

  @Column(name = "restaurant_id")
  private Integer restaurantId;

  private Short months;

  private Short years;

}
