package com.felixstanley.makanmoerahinvoicejob.entity;

import static javax.persistence.EnumType.STRING;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.Provider;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.ToString;

/**
 * @author Felix
 */
@Entity
@Data
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Users extends AbstractEntity implements Serializable {

  private String email;

  private String name;

  @Column(name = "alternate_email")
  private String alternateEmail;

  @Enumerated(STRING)
  private Provider provider;

}
