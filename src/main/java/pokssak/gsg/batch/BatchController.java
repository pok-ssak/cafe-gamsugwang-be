package pokssak.gsg.batch;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {
    private final JobLauncher jobLauncher;
    private final Job cafeJob;

    @PostMapping("/run")
    public ResponseEntity<String> runBatch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(cafeJob, params);
            return ResponseEntity.ok("Batch job started successfully.");
        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 |
                 JobParametersInvalidException e) {
            return ResponseEntity.status(500)
                    .body("Batch job failed: " + e.getMessage());
        }
    }
}
