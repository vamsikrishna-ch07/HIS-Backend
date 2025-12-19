package gov.nj.dhs.his.bi.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job issueBenefitsJob;

    public BatchScheduler(JobLauncher jobLauncher, Job issueBenefitsJob) {
        this.jobLauncher = jobLauncher;
        this.issueBenefitsJob = issueBenefitsJob;
    }

    @Scheduled(cron = "0 0/2 * * * ?") // Every 2 minutes for demonstration
    public void runBatchJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(issueBenefitsJob, params);
    }
}
