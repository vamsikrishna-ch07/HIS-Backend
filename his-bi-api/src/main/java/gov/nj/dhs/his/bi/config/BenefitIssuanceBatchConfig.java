package gov.nj.dhs.his.bi.config;

import gov.nj.dhs.his.bi.entity.BenefitIssuance;
import gov.nj.dhs.his.bi.entity.PendingBenefit;
import gov.nj.dhs.his.bi.repository.BenefitIssuanceRepository;
import gov.nj.dhs.his.bi.repository.PendingBenefitRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Configuration
public class BenefitIssuanceBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PendingBenefitRepository pendingBenefitRepository;
    private final BenefitIssuanceRepository benefitIssuanceRepository;

    public BenefitIssuanceBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, PendingBenefitRepository pendingBenefitRepository, BenefitIssuanceRepository benefitIssuanceRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.pendingBenefitRepository = pendingBenefitRepository;
        this.benefitIssuanceRepository = benefitIssuanceRepository;
    }

    @Bean
    public Job issueBenefitsJob(Step issueBenefitsStep) {
        return new JobBuilder("issueBenefitsJob", jobRepository)
                .flow(issueBenefitsStep)
                .end()
                .build();
    }

    @Bean
    public Step issueBenefitsStep(ItemReader<PendingBenefit> reader, ItemProcessor<PendingBenefit, BenefitIssuance> processor, ItemWriter<BenefitIssuance> writer) {
        return new StepBuilder("issueBenefitsStep", jobRepository)
                .<PendingBenefit, BenefitIssuance>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<PendingBenefit> reader() {
        RepositoryItemReader<PendingBenefit> reader = new RepositoryItemReader<>();
        reader.setRepository(pendingBenefitRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<PendingBenefit, BenefitIssuance> processor() {
        return pendingBenefit -> {
            BenefitIssuance issuance = new BenefitIssuance();
            issuance.setAppId(pendingBenefit.getApplicationId());
            issuance.setCitizenId(pendingBenefit.getCitizenId());
            issuance.setPlanName(pendingBenefit.getPlanName());
            issuance.setBenefitAmount(pendingBenefit.getBenefitAmount());
            issuance.setIssuanceDate(LocalDate.now());
            issuance.setStatus("ISSUED");
            return issuance;
        };
    }

    @Bean
    public ItemWriter<BenefitIssuance> writer() {
        return benefitIssuanceRepository::saveAll;
    }
}
