package com.felixstanley.makanmoerahinvoicejob.taskexecutor;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.core.task.SyncTaskExecutor;

/**
 * @author Felix
 * <p>
 * Needed because of Spring Batch inability to pass Job Execution to partitioned steps
 * <p>
 * // TODO Delete this
 * <p>
 * https://github.com/spring-projects/spring-batch/issues/1335
 */
public class RegisterJobExecutionSyncTaskExecutor extends SyncTaskExecutor {

  @Override
  public void execute(Runnable task) {
    JobExecution jobExecution = JobSynchronizationManager.getContext().getJobExecution();
    super.execute(() -> {
      JobSynchronizationManager.register(jobExecution);
      try {
        task.run();
      } finally {
        JobSynchronizationManager.close();
      }
    });
  }

}
