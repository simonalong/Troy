package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.endpoint.AutoGroupEndpoint;
import com.github.simonalong.autologger.endpoint.AutoFunEndPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shizi
 * @since 2021-02-02 23:45:24
 */
@ConditionalOnProperty(value = "autoLogger.enable", havingValue = "true")
@Configuration
public class AutoLoggerAutoConfiguration {

    @Bean
    public AutoLoggerBeanPostProcessor beanPostProcessor() {
        System.setProperty("management.endpoints.web.exposure.include", "auto-group, auto-fun");
        return new AutoLoggerBeanPostProcessor();
    }

    @Bean
    public AutoLoggerAop autoLoggerAop() {
        return new AutoLoggerAop();
    }

    @Configuration
    static class EndpointConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AutoGroupEndpoint groupListEndpoint() {
            return new AutoGroupEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public AutoFunEndPoint serviceEndPoint() {
            return new AutoFunEndPoint();
        }
    }
}
