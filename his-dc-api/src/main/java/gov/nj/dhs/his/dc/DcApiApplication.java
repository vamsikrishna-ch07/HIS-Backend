package gov.nj.dhs.his.dc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@ComponentScan(basePackages = "gov.nj.dhs.his")
public class DcApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcApiApplication.class, args);
    }

}
