package com.github.simonalong.autologger.autoconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shizi
 * @since 2021-02-02 23:33:19
 */
@Getter
@Setter
@ConfigurationProperties("log.auto-logger")
public class AutoLoggerProperties {

    /**
     * api前缀，比如：/api/test/actuator
     */
    private String prefix;
}
