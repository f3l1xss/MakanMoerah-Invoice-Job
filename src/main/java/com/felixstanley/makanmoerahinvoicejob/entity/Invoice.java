package com.felixstanley.makanmoerahinvoicejob.entity;

import com.felixstanley.makanmoerahinvoicejob.entity.enums.InvoiceStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Felix
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"invoiceId"})
public class Invoice {

  @EmbeddedId
  private InvoiceId invoiceId;

  @Column(name = "expired_date")
  private Date expiredDate;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "invoice_status")
  @Enumerated
  private InvoiceStatus invoiceStatus;

}
