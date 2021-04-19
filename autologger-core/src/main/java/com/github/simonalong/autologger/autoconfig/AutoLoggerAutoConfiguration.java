package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.endpoint.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static com.github.simonalong.autologger.AutoLoggerConstants.*;

/**
 * @author shizi
 * @since 2021-02-02 23:45:24
 */
@Slf4j
@EnableConfigurationProperties(AutoLoggerProperties.class)
@ConditionalOnExpression("#{''.equals('${log.auto-logger.enable:}') or 'true'.equals('${log.auto-logger.enable}')}")
@Configuration
public class AutoLoggerAutoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public AutoLoggerBeanPostProcessor beanPostProcessor(AutoLoggerProperties autoLoggerProperties) {
        List<String> endpointList = Arrays.asList(GROUP, LOGGER, APPENDER);
        System.setProperty("management.endpoints.web.exposure.include", String.join(", ", endpointList));
        String apiPrefix = autoLoggerProperties.getPrefix();
        if (null != apiPrefix && !"".equals(apiPrefix)) {
            System.setProperty("management.endpoints.web.basePath", autoLoggerProperties.getPrefix());
        }

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
        public GroupEndpoint groupEndpoint() {
            return new GroupEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public LoggerEndpoint loggerEndpoint() {
            return new LoggerEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public AppenderEndpoint appenderEndpoint() {
            return new AppenderEndpoint();
        }
    }
}
