package gov.nj.dhs.his.ed.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic eligibilityApprovedTopic() {
        return TopicBuilder.name("eligibility-approved").build();
    }

    @Bean
    public NewTopic eligibilityDeniedTopic() {
        return TopicBuilder.name("eligibility-denied").build();
    }
}
