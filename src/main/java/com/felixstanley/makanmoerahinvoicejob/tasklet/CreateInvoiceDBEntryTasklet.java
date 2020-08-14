package com.felixstanley.makanmoerahinvoicejob.tasklet;

import com.felixstanley.makanmoerahinvoicejob.dao.InvoiceDao;
import com.felixstanley.makanmoerahinvoicejob.entity.Invoice;
import com.felixstanley.makanmoerahinvoicejob.entity.InvoiceId;
import com.felixstanley.makanmoerahinvoicejob.entity.Restaurant;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.InvoiceStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author Felix
 */
@AllArgsConstructor
@Slf4j
public class CreateInvoiceDBEntryTasklet implements Tasklet {

  private InvoiceDao invoiceDao;

  private Date invoiceExpirationDate;
  private Integer invoiceAmount;
  private Restaurant restaurant;
  private Integer month;
  private Integer year;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InvoiceId invoiceId = createInvoiceId();
    Invoice existingInvoice = invoiceDao.findById(invoiceId).orElse(null);
    // Allow New Invoice Creation if Existing Invoice For current Month And Year is null or it has expired
    if (existingInvoice == null || existingInvoice.getInvoiceStatus()
        .equals(InvoiceStatus.EXPIRED)) {

      if (existingInvoice != null) {
        invoiceDao.delete(existingInvoice);
      }
      createNewInvoice(invoiceId);
    } else {
      //  Invalid State, Existing Invoice might not be expired yet or might have been paid
      log.error("Invoice with Id {} has existed: {}", invoiceId, existingInvoice);
      contribution.setExitStatus(ExitStatus.FAILED);
    }
    return RepeatStatus.FINISHED;
  }

  private void createNewInvoice(InvoiceId invoiceId) {
    invoiceDao.saveAndFlush(
        new Invoice(invoiceId, invoiceExpirationDate, invoiceAmount.doubleValue(),
            InvoiceStatus.NEW));
  }

  private InvoiceId createInvoiceId() {
    InvoiceId invoiceId = new InvoiceId();
    invoiceId.setMonths(month.shortValue());
    invoiceId.setRestaurantId(restaurant.getId());
    invoiceId.setYears(year.shortValue());
    return invoiceId;
  }

}
