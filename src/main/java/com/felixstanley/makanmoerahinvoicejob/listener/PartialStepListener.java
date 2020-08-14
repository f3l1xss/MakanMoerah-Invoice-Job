package com.felixstanley.makanmoerahinvoicejob.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;

/**
 * @author Felix // TODO Delete this
 */
public class PartialStepListener implements StepExecutionListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    JobSynchronizationManager.register(stepExecution.getJobExecution());
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    JobSynchronizationManager.release();
    JobSynchronizationManager.close();
    return stepExecution.getExitStatus();
  }
}