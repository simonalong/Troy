package com.github.simonalong.autologger;

/**
 * @author shizi
 * @since 2021-02-07 22:37:20
 */
public interface AutoLoggerConstants {

    /**
     * 日志前缀
     */
    String AUTO_LOG_PRE = "[autologger] ";
    /**
     * 默认的服务地址
     */
    String DEFAULT_ADDRESS = "http://isc-pivot-platform:31107";
    /**
     * 服务健康检查
     */
    String API_SYSTEM_STATUS = "/api/core/platform/system/status";
    /**
     * 服务不可用，日志打印门限（即多少次打印一次）
     */
    Integer HEARD_BEAN_PRINT_THRESHOLD = 5;
    /**
     * 服务端可用标示
     */
    String SERVER_AVAILABLE = "server_available";

    /**
     * 分组
     */
    String GROUP = "group";
    /**
     * logger
     */
    String LOGGER = "logger";
    /**
     * appender
     */
    String APPENDER = "appender";
}
