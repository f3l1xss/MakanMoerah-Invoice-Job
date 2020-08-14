package com.felixstanley.makanmoerahinvoicejob.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import javax.persistence.Entity;
import lombok.Data;

/**
 * @author Felix
 */
@Entity
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Users extends AbstractEntity implements Serializable {

  private String email;

  private String name;

}
