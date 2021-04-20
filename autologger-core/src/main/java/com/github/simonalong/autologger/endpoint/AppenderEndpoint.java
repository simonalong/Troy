package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import static com.github.simonalong.autologger.AutoLoggerConstants.APPENDER;

/**
 * @author shizi
 * @since 2021-04-16 16:08:05
 */
@Endpoint(id = APPENDER)
public class AppenderEndpoint {

    /**
     * Logger动态添加appender
     *
     * @param arg0        console或者file
     * @param loggerName  日志名
     * @param logLevel 日志级别
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @WriteOperation
    public Integer addAppenderToConsole(@Selector String arg0, String loggerName, String logLevel) {
        if ("console".equals(arg0)) {
            return DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
        } else if ("file".equals(arg0)) {
            return DynamicLogUtils.addAppenderToFile(loggerName, logLevel);
        } else {
            return 0;
        }
    }

    /**
     * 删除Logger中的appender
     *
     * @param loggerName 日志名
     * @return 删除结果：0-没有删除成功，1-删除成功
     */
    @DeleteOperation
    public Integer deleteAppenderOfConsole(String loggerName) {
        return DynamicLogUtils.deleteAppender(loggerName);
    }
}
