package com.felixstanley.makanmoerahinvoicejob.writer;

import com.felixstanley.makanmoerahinvoicejob.constant.Constants;
import com.felixstanley.makanmoerahinvoicejob.entity.Booking;
import com.felixstanley.makanmoerahinvoicejob.entity.Restaurant;
import com.felixstanley.makanmoerahinvoicejob.entity.Users;
import com.felixstanley.makanmoerahinvoicejob.utility.Utility;
import com.xendit.Xendit;
import com.xendit.model.Invoice;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Felix
 */
@Slf4j
public class XenditInvoiceWriter implements ItemWriter<Booking> {

  private StepExecution stepExecution;

  private String xenditApiKey;
  private Restaurant restaurant;
  private Users users;
  private Integer month;
  private Integer year;

  public XenditInvoiceWriter(String xenditApiKey,
      Restaurant restaurant, Users users, Integer month, Integer year) {
    this.xenditApiKey = xenditApiKey;
    this.restaurant = restaurant;
    this.users = users;
    this.month = month;
    this.year = year;
  }

  @BeforeStep
  public void saveStepExecution(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public void write(List<? extends Booking> bookings) throws Exception {
    Xendit.apiKey = xenditApiKey;
    Integer totalInvoiceAmount = bookings.size() * getBookingFeePerBooking();
    String email = users.getEmail();
    if (Utility.isFBUserWithoutEmail(users)) {
      log.info("Users {} is FB User without email, sending invoice to his alternate email: {}",
          users, users.getAlternateEmail());
      email = users.getAlternateEmail();
    }

    Invoice invoice = Invoice.builder().externalId(getInvoiceId()).amount(totalInvoiceAmount)
        .payerEmail(email).shouldSendEmail(true)
        .description(getInvoiceDescription(bookings, totalInvoiceAmount)).build();
    invoice = Invoice.create(createInvoiceParams(invoice));

    // Add Invoice to Execution Context for future step(s)
    addInvoiceToExecutionContext(invoice);
  }

  private String getInvoiceId() {
    return Constants.INVOICE_PREFIX + "-" + restaurant.getId() + "-" + month + "-" + year;
  }

  private Integer getBookingFeePerBooking() {
    return restaurant.getPriceCategory() * Constants.BASE_BOOKING_FEE;
  }

  private String getInvoiceDescription(List<? extends Booking> bookings,
      Integer totalInvoiceAmount) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "Invoice for Restaurant: " + restaurant.getName() + " - " + restaurant.getAddress() + " "
            + restaurant.getCity() + " - " + Utility.toMonthString(month) + " " + year);
    stringBuilder.append(", ");
    stringBuilder.append(bookings.size() + " bookings with following codes:");
    stringBuilder.append(System.lineSeparator());

    for (Booking booking : bookings) {
      stringBuilder.append(booking.getBookingCode());
      stringBuilder.append(", ");
    }

    Integer feePerBooking = getBookingFeePerBooking();
    stringBuilder.append("Base Amount Fee: " + feePerBooking);
    stringBuilder.append(", ");
    stringBuilder.append(
        "Total Amount = " + bookings.size() + " * " + feePerBooking + " = " + totalInvoiceAmount);
    return stringBuilder.toString();
  }

  private Map<String, Object> createInvoiceParams(Invoice invoice) {
    Map<String, Object> params = new HashMap<>();
    params.put("external_id", invoice.getExternalId());
    params.put("amount", invoice.getAmount());
    params.put("payer_email", invoice.getPayerEmail());
    params.put("should_send_email", invoice.getShouldSendEmail());
    params.put("description", invoice.getDescription());
    params.put("invoice_duration", Constants.MONTH_IN_SECONDS);
    params.put("reminder_time", Constants.INVOICE_REMINDER_TIME);
    return params;
  }

  private void addInvoiceToExecutionContext(Invoice invoice)
      throws ParseException {
    ExecutionContext stepContext = stepExecution.getExecutionContext();
    stepContext.put(Constants.INVOICE_EXPIRATION_DATE_CONTEXT_KEY_NAME,
        Utility.toDate(invoice.getExpiryDate()));
    stepContext.put(Constants.INVOICE_AMOUNT_CONTEXT_KEY_NAME, invoice.getAmount().intValue());
  }

}
