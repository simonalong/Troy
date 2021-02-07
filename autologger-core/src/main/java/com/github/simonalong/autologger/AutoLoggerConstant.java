package com.github.simonalong.autologger;

/**
 * @author shizi
 * @since 2021-02-07 22:37:20
 */
public interface AutoLoggerConstant {

    /**
     * 给某个函数自动生成logger
     */
    String AUTO_FUN = "auto-fun";
    /**
     * 给某个group自动生成logger
     */
    String AUTO_GROUP = "auto-group";
    /**
     * 给logger添加控制台appender
     */
    String ADD_APPENDER_CONSOLE = "add-appender-console";
    /**
     * 给logger添加文件appender
     */
    String ADD_APPENDER_FILE = "add-appender-file";
    /**
     * logger搜索
     */
    String LOGGER_SEARCH = "logger-search";
    /**
     * 设置root的logger
     */
    String LOGGER_ROOT_SET = "logger-root-set";
}
