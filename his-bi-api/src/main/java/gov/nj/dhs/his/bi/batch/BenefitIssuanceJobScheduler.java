package gov.nj.dhs.his.bi.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BenefitIssuanceJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(BenefitIssuanceJobScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job benefitIssuanceJob;

    /**
     * This method is scheduled to run automatically to trigger the benefit issuance batch job.
     * The cron expression "0 0 1 25 * ?" means:
     * - 0 seconds
     * - 0 minutes
     * - 1 AM
     * - on the 25th day of the month
     * - every month (*)
     * - for any day of the week (?)
     */
    @Scheduled(cron = "0 0 1 25 * ?")
    public void runBenefitIssuanceJob() {
        log.info("Scheduled job started: Benefit Issuance");
        try {
            // Use JobParameters to ensure each job run is unique
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(benefitIssuanceJob, jobParameters);
            log.info("Scheduled job finished: Benefit Issuance");
        } catch (Exception e) {
            log.error("Error while running the benefit issuance scheduled job", e);
        }
    }
}
