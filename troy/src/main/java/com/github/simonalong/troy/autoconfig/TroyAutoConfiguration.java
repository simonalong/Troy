package com.github.simonalong.troy.autoconfig;

import com.github.simonalong.troy.endpoint.*;
import com.github.simonalong.troy.util.SpringBeanUtils;
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

import static com.github.simonalong.troy.TroyConstants.*;

/**
 * @author shizi
 * @since 2021-02-02 23:45:24
 */
@Slf4j
@EnableConfigurationProperties(TroyProperties.class)
@ConditionalOnExpression("#{''.equals('${troy.log.enable:}') or 'true'.equals('${troy.log.enable}')}")
@Configuration
public class TroyAutoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public TroyBeanPostProcessor beanPostProcessor(TroyProperties troyProperties) {
        List<String> endpointList = Arrays.asList(HELP, GROUP, LOGGER, APPENDER, ENVIRONMENT, BEAN);
        System.setProperty("management.endpoints.web.exposure.include", String.join(", ", endpointList));
        System.setProperty("management.endpoints.web.basePath", DEFAULT_API_PREFIX);

        if (null != troyProperties.getPrefix() && !"".equals(troyProperties.getPrefix())) {
            System.setProperty("management.endpoints.web.basePath", troyProperties.getPrefix());
        }

        return new TroyBeanPostProcessor();
    }

    @Bean
    public TroyAop troyAop() {
        return new TroyAop();
    }

    @Bean
    public SpringBeanUtils springBeanUtils() {
        return new SpringBeanUtils();
    }

    @Configuration
    static class EndpointConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public HelpEndpoint troyHelpEndpoint() {
            return new HelpEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public GroupEndpoint troyGroupEndpoint() {
            return new GroupEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public LoggerEndpoint troyLoggerEndpoint() {
            return new LoggerEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public AppenderEndpoint troyAppenderEndpoint() {
            return new AppenderEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public EnvEndpoint troyEnv() {
            return new EnvEndpoint();
        }

        @Bean
        @ConditionalOnMissingBean
        public BeanEndpoint troyBean() {
            return new BeanEndpoint();
        }
    }
}
