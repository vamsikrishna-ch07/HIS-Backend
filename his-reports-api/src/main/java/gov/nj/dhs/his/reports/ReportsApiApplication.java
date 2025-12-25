package gov.nj.dhs.his.reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = "gov.nj.dhs.his")
public class ReportsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportsApiApplication.class, args);
    }

}
