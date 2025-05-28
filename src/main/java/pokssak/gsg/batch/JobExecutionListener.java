package pokssak.gsg.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobExecutionListener implements org.springframework.batch.core.JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[JOB START] name={} id={} at={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobId(),
                jobExecution.getStartTime()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("[JOB END]   name={} id={} status={} at={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobId(),
                jobExecution.getStatus(),
                jobExecution.getEndTime()
        );
    }
}
