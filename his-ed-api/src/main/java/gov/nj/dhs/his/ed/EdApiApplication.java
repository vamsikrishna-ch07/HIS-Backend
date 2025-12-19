package gov.nj.dhs.his.ed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = "gov.nj.dhs.his")
public class EdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdApiApplication.class, args);
    }

}