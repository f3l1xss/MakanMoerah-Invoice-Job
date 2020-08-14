package com.felixstanley.makanmoerahinvoicejob.configuration;

import com.felixstanley.makanmoerahinvoicejob.constant.Constants;
import com.felixstanley.makanmoerahinvoicejob.dao.BookingDao;
import com.felixstanley.makanmoerahinvoicejob.dao.InvoiceDao;
import com.felixstanley.makanmoerahinvoicejob.dao.RestaurantDao;
import com.felixstanley.makanmoerahinvoicejob.dao.UsersDao;
import com.felixstanley.makanmoerahinvoicejob.entity.Booking;
import com.felixstanley.makanmoerahinvoicejob.entity.Restaurant;
import com.felixstanley.makanmoerahinvoicejob.entity.Users;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.BookingStatus;
import com.felixstanley.makanmoerahinvoicejob.listener.LoggingJobListener;
import com.felixstanley.makanmoerahinvoicejob.partitioner.RestaurantIdPartitioner;
import com.felixstanley.makanmoerahinvoicejob.rowmapper.BookingRowMapper;
import com.felixstanley.makanmoerahinvoicejob.tasklet.CreateInvoiceDBEntryTasklet;
import com.felixstanley.makanmoerahinvoicejob.validator.IssueInvoiceJobParametersValidator;
import com.felixstanley.makanmoerahinvoicejob.writer.XenditInvoiceWriter;
import java.util.Calendar;
import java.util.Date;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Felix
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  private DataSource dataSource;

  @Value("${xendit.api.key}")
  private String xenditApiKey;

  @Autowired
  BookingDao bookingDao;

  @Autowired
  RestaurantDao restaurantDao;

  @Autowired
  UsersDao usersDao;

  @Autowired
  InvoiceDao invoiceDao;

  @Bean(name = "issueInvoiceJob")
  public Job issueInvoiceJob(Step issueInvoiceManager) {
    return this.jobBuilderFactory.get("issueInvoiceJob").listener(loggingJobListener())
        .validator(issueInvoiceJobParametersValidator())
        .start(issueInvoiceManager).build();
  }

  @Bean
  public Step issueInvoiceManager(Partitioner restaurantIdPartitioner, Step issueInvoice) {
    return this.stepBuilderFactory.get("issueInvoice.manager")
        .partitioner(issueInvoice.getName(), restaurantIdPartitioner)
        .step(issueInvoice).build();
  }

  @Bean
  @JobScope
  public Partitioner restaurantIdPartitioner(
      @Value("#{jobParameters[" + Constants.ISSUE_INVOICE_JOB_MONTH_TO_INVOICE_PARAMETER_KEY
          + "]}") Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return new RestaurantIdPartitioner(bookingDao, restaurantDao, usersDao,
        calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
  }

  @Bean
  public Step issueInvoice(Step createXenditInvoice, Step addInvoiceDBEntry) {
    Flow issueInvoiceFlow = new FlowBuilder<SimpleFlow>("issueInvoiceFlow")
        .start(createXenditInvoice).next(addInvoiceDBEntry).build();
    return this.stepBuilderFactory.get("issueInvoice").flow(issueInvoiceFlow).build();
  }

  @Bean
  public Step createXenditInvoice(ItemReader bookingItemReader,
      ItemWriter xenditInvoiceWriter,
      ExecutionContextPromotionListener xenditInvoicePromotionListener) {
    return ((SimpleStepBuilder) this.stepBuilderFactory
        .get("createXenditInvoice")
        .chunk(Integer.MAX_VALUE)
        .reader(bookingItemReader).writer(xenditInvoiceWriter)
        .listener(xenditInvoicePromotionListener)).build();
  }

  @Bean
  public Step addInvoiceDBEntry(Tasklet createInvoiceDBEntryTasklet) {
    return this.stepBuilderFactory.get("addInvoiceDBEntry")
        .tasklet(createInvoiceDBEntryTasklet).build();
  }

  public RowMapper<Booking> bookingRowMapper() {
    return new BookingRowMapper();
  }

  @Bean
  @StepScope
  public JdbcCursorItemReader<Booking> bookingItemReader(@Value(
      "#{stepExecutionContext[" + Constants.RESTAURANT_EXECUTION_CONTEXT_KEY_NAME
          + "]}") Restaurant restaurant,
      @Value("#{stepExecutionContext[" + Constants.MONTH_CONTEXT_KEY_NAME + "]}") Integer month,
      @Value("#{stepExecutionContext[" + Constants.YEAR_CONTEXT_KEY_NAME + "]}") Integer year) {
    return new JdbcCursorItemReaderBuilder<Booking>().name("bookingItemReader")
        .dataSource(dataSource).rowMapper(bookingRowMapper())
        .sql(
            "SELECT confirmed_date, timeslot, booking_code, num_of_people FROM booking "
                + "WHERE booking_status = ? AND date_part('month', confirmed_date) = ? "
                + "AND date_part('year', confirmed_date) = ? AND restaurant_id = ?")
        .queryArguments(BookingStatus.COMPLETED.ordinal(), month, year, restaurant.getId())
        .driverSupportsAbsolute(true).build();
  }

  @Bean
  @StepScope
  public XenditInvoiceWriter xenditInvoiceWriter(
      @Value("#{stepExecutionContext[" + Constants.RESTAURANT_EXECUTION_CONTEXT_KEY_NAME
          + "]}") Restaurant restaurant,
      @Value("#{stepExecutionContext[" + Constants.USERS_EXECUTION_CONTEXT_KEY_NAME
          + "]}") Users users,
      @Value("#{stepExecutionContext[" + Constants.MONTH_CONTEXT_KEY_NAME + "]}") Integer month,
      @Value("#{stepExecutionContext[" + Constants.YEAR_CONTEXT_KEY_NAME + "]}") Integer year) {
    return new XenditInvoiceWriter(xenditApiKey, restaurant, users, month, year);
  }

  @Bean
  public ExecutionContextPromotionListener xenditInvoicePromotionListener() {
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[]{Constants.INVOICE_EXPIRATION_DATE_CONTEXT_KEY_NAME,
        Constants.INVOICE_AMOUNT_CONTEXT_KEY_NAME});
    return listener;
  }

  @Bean
  @StepScope
  public Tasklet createInvoiceDBEntryTasklet(
      @Value("#{jobExecutionContext[" + Constants.INVOICE_EXPIRATION_DATE_CONTEXT_KEY_NAME
          + "]}") Date invoiceExpirationDate,
      @Value("#{jobExecutionContext[" + Constants.INVOICE_AMOUNT_CONTEXT_KEY_NAME
          + "]}") Integer invoiceAmount,
      @Value("#{stepExecutionContext[" + Constants.RESTAURANT_EXECUTION_CONTEXT_KEY_NAME
          + "]}") Restaurant restaurant,
      @Value("#{stepExecutionContext[" + Constants.MONTH_CONTEXT_KEY_NAME + "]}") Integer month,
      @Value("#{stepExecutionContext[" + Constants.YEAR_CONTEXT_KEY_NAME + "]}") Integer year) {
    return new CreateInvoiceDBEntryTasklet(invoiceDao, invoiceExpirationDate, invoiceAmount,
        restaurant, month, year);
  }

  public JobExecutionListener loggingJobListener() {
    return new LoggingJobListener();
  }

  public JobParametersValidator issueInvoiceJobParametersValidator() {
    return new IssueInvoiceJobParametersValidator();
  }

}
