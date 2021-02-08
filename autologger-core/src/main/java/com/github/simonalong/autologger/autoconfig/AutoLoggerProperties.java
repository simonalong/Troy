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
@ConfigurationProperties("auto-logger")
public class AutoLoggerProperties {

    /**
     * 在配置中心注册的应用名
     */
    private String appName;
    /**
     * 对应的actuator对应的前缀路径，请以"/"为前缀
     */
    private String basePath;
    /**
     * 是否启用：true：启用，false：禁用
     */
    private Boolean enable = false;
}
