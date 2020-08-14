package com.felixstanley.makanmoerahinvoicejob.validator;

import com.felixstanley.makanmoerahinvoicejob.constant.Constants;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

/**
 * @author Felix
 */
public class IssueInvoiceJobParametersValidator implements JobParametersValidator {

  @Override
  public void validate(JobParameters parameters) throws JobParametersInvalidException {
    try {
      if (parameters.getDate(Constants.ISSUE_INVOICE_JOB_MONTH_TO_INVOICE_PARAMETER_KEY) == null) {
        throw new JobParametersInvalidException("Cannot Start Issue Invoice Job as "
            + Constants.ISSUE_INVOICE_JOB_MONTH_TO_INVOICE_PARAMETER_KEY + " param is null");
      }
    } catch (ClassCastException e) {
      throw new JobParametersInvalidException("Cannot Start Issue Invoice Job as "
          + Constants.ISSUE_INVOICE_JOB_MONTH_TO_INVOICE_PARAMETER_KEY + " param is invalid");
    }
  }
}
