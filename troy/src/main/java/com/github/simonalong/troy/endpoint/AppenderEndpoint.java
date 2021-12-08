package com.github.simonalong.troy.endpoint;

import com.github.simonalong.troy.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import static com.github.simonalong.troy.TroyConstants.APPENDER;

/**
 * @author shizi
 * @since 2021-04-16 16:08:05
 */
@Endpoint(id = APPENDER)
public class AppenderEndpoint {

    /**
     * Logger动态添加appender
     *
     * @param arg0        console、file或者all
     * @param loggerName  日志名
     * @param logLevel 日志级别
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @WriteOperation
    public Integer addAppenderToSome(@Selector String arg0, String loggerName, String logLevel) {
        if ("console".equals(arg0)) {
            return DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
        } else if ("all".equals(arg0)) {
            return DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
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
    public Integer deleteAppender(String loggerName) {
        return DynamicLogUtils.deleteAppender(loggerName);
    }

    /**
     * 删除Logger中的appender
     *
     * @param arg0        console、file或者all
     * @param loggerName 日志名
     * @return 删除结果：0-没有删除成功，1-删除成功
     */
    @DeleteOperation
    public Integer deleteAppenderOfSome(@Selector String arg0, String loggerName) {
        if ("console".equals(arg0)) {
            return DynamicLogUtils.deleteAppenderOfConsole(loggerName);
        } else if ("all".equals(arg0)) {
            return DynamicLogUtils.deleteAppenderOfConsole(loggerName);
        } else {
            return 0;
        }
    }
}
