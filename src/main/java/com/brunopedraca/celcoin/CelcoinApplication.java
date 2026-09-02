package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.bff.MobileBffProperties;
import com.brunopedraca.celcoin.bff.MobileBffFeatureProperties;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableRetry
@EnableAsync
@EnableConfigurationProperties({CelcoinProperties.class, MobileBffProperties.class, MobileBffFeatureProperties.class})
public class CelcoinApplication {
    public static void main(String[] args) {
        SpringApplication.run(CelcoinApplication.class, args);
    }
}
