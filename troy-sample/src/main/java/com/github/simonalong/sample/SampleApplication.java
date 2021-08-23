package com.github.simonalong.sample;

import com.simonalong.mikilin.annotation.EnableMikilin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shizi
 * @since 2021-02-07 22:54:14
 */
@EnableMikilin
@SpringBootApplication
public class SampleApplication {

    public static void main(String... args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
