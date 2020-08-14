package com.felixstanley.makanmoerahinvoicejob.constant;

/**
 * @author Felix
 */
public class Constants {

  // Job Parameters Key
  public final static String ISSUE_INVOICE_JOB_MONTH_TO_INVOICE_PARAMETER_KEY = "monthToInvoice";

  // Context Key Name
  public final static String PARTITION_STRING_CONTEXT_KEY_NAME = "partitionString";
  public final static String RESTAURANT_EXECUTION_CONTEXT_KEY_NAME = "restaurant";
  public final static String USERS_EXECUTION_CONTEXT_KEY_NAME = "users";
  public final static String INVOICE_EXPIRATION_DATE_CONTEXT_KEY_NAME = "invoiceExpirationDate";
  public final static String INVOICE_AMOUNT_CONTEXT_KEY_NAME = "invoiceAmount";
  public final static String MONTH_CONTEXT_KEY_NAME = "month";
  public final static String YEAR_CONTEXT_KEY_NAME = "year";

  // Invoice Prefix
  public final static String INVOICE_PREFIX = "INV";

  // Base Booking Fee (in Rupiah)
  public final static Integer BASE_BOOKING_FEE = 1000;

  // 1 Month in Seconds (Assume 31 days)
  public final static Integer MONTH_IN_SECONDS = 2678400;

  // Default Invoice Reminder (7 days in advance)
  public final static Integer INVOICE_REMINDER_TIME = 7;

}
