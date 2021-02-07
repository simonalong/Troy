package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.endpoint.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static com.github.simonalong.autologger.AutoLoggerConstant.*;

/**
 * 不存在{@code autoLogger.enable}或者该配置为true时候生效
 * @author shizi
 * @since 2021-02-02 23:45:24
 */
@ConditionalOnExpression("#{''.equals('${autoLogger.enable:}') or 'true'.equals('${autoLogger.enable}')}")
@Configuration
public class AutoLoggerAutoConfiguration {

    @Bean
    public AutoLoggerBeanPostProcessor beanPostProcessor() {
        List<String> endpointList = Arrays.asList(AUTO_FUN, AUTO_GROUP, ADD_APPENDER_CONSOLE, ADD_APPENDER_FILE, LOGGER_SEARCH, LOGGER_ROOT_SET);
        System.setProperty("management.endpoints.web.exposure.include", String.join(", ", endpointList));
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

        @Bean
        @ConditionalOnMissingBean
        public AddAppenderOfConsoleEndpoint addAppenderOfConsoleEndpoint() {
            return new AddAppenderOfConsoleEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public AddAppenderOfFileEndpoint addAppenderOfFileEndpoint() {
            return new AddAppenderOfFileEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public LoggerRootSetEndpoint loggerRootSetEndpoint() {
            return new LoggerRootSetEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public LoggerSearchEndpoint loggerSearchEndpoint() {
            return new LoggerSearchEndpoint();
        }
    }
}
