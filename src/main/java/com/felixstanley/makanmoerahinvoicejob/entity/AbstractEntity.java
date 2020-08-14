package com.felixstanley.makanmoerahinvoicejob.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Felix
 */
@MappedSuperclass()
@Data
@EqualsAndHashCode
public abstract class AbstractEntity {

  @Id
  private Integer id;

}
