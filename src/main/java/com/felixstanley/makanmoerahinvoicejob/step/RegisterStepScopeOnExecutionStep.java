package com.felixstanley.makanmoerahinvoicejob.step;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

/**
 * @author Felix
 * <p>
 * // TODO Delete this
 */
@AllArgsConstructor
public class RegisterStepScopeOnExecutionStep implements Step {

  private Step delegate;

  @Override
  public String getName() {
    return "RegisterStepScopeOnExecutionStep";
  }

  @Override
  public boolean isAllowStartIfComplete() {
    return false;
  }

  @Override
  public int getStartLimit() {
    return 0;
  }

  @Override
  public void execute(StepExecution stepExecution) throws JobInterruptedException {
    try {
      StepSynchronizationManager.register(stepExecution);
      delegate.execute(stepExecution);
    } finally {
      StepSynchronizationManager.close();
    }
  }
}
