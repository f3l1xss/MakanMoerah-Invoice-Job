package com.felixstanley.makanmoerahinvoicejob.utility;

import com.felixstanley.makanmoerahinvoicejob.entity.Users;
import com.felixstanley.makanmoerahinvoicejob.entity.enums.Provider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * @author Felix
 */
public class Utility {

  public static String toMonthString(Integer month) {
    return Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH);
  }

  public static Date toDate(String xenditDateString) throws ParseException {
    // Xendit Date String is in format yyyy-mm-ddTHH:MM:SS so split and get the date portion
    return new SimpleDateFormat("yyyy-MM-dd").parse(xenditDateString.split("T")[0]);
  }

  public static Boolean isFBUserWithoutEmail(Users users) {
    // Check if Email is Invalid and User Log in through Facebook
    return !EmailValidator.getInstance().isValid(users.getEmail()) && users
        .getProvider().equals(Provider.FACEBOOK);
  }

}
