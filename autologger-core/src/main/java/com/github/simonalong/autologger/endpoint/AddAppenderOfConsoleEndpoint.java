package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;

import static com.github.simonalong.autologger.AutoLoggerConstant.ADD_APPENDER_CONSOLE;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Component
@Endpoint(id = ADD_APPENDER_CONSOLE)
public class AddAppenderOfConsoleEndpoint {


    /**
     * Logger动态添加appender到控制台
     *
     * @param arg0  logger名字
     * @param arg1 日志级别
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @WriteOperation
    public Integer addAppenderToConsole(@Selector String arg0, @Selector String arg1) {
        return DynamicLogUtils.addAppenderToConsole(arg0, arg1);
    }

    /**
     * 删除Logger中的appender
     *
     * @param arg0 logger名字
     * @return 删除结果：0-没有删除成功，1-删除成功
     */
    @DeleteOperation
    public Integer deleteAppenderOfConsole(@Selector String arg0) {
        return DynamicLogUtils.deleteAppenderOfConsole(arg0);
    }
}
