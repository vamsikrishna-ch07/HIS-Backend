package gov.nj.dhs.his.ar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "gov.nj.dhs.his")
public class ArApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArApiApplication.class, args);
    }

}