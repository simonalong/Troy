package com.github.simonalong.autologger.autoconfig;


import com.github.simonalong.autologger.endpoint.GroupListEndpoint;
import com.github.simonalong.autologger.endpoint.ServiceEndPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shizi
 * @since 2021-02-02 23:45:24
 */
//@ConditionalOnProperty(value = "isyscore.autoLogger.enable", havingValue = "true")
@Configuration
public class AutoLoggerAutoConfiguration {

    @Bean
    public AutoLoggerBeanPostProcessor beanPostProcessor() {
        return new AutoLoggerBeanPostProcessor();
    }

    @Configuration
    static class EndpointConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public GroupListEndpoint groupListEndpoint() {
            return new GroupListEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public ServiceEndPoint serviceEndPoint() {
            return new ServiceEndPoint();
        }
    }
}
