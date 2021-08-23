package com.github.simonalong.troy.autoconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shizi
 * @since 2021-02-02 23:33:19
 */
@Getter
@Setter
@ConfigurationProperties("troy.log")
public class TroyProperties {

    /**
     * api前缀，比如：/api/troy/log/
     */
    private String prefix;
    /**
     * 是否启用：true：启用，false：禁用
     */
    private Boolean enable;
}
