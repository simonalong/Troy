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
@ConfigurationProperties("autoLogger")
public class AutoLoggerProperties {

    /**
     * 在配置中心注册的应用名
     */
    private String appName;
    /**
     * 是否启用：true：启用，false：禁用
     */
    private Boolean enable = false;
}
