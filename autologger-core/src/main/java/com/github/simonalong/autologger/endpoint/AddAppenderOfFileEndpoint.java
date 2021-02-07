package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;

import static com.github.simonalong.autologger.AutoLoggerConstant.ADD_APPENDER_FILE;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Component
@Endpoint(id = ADD_APPENDER_FILE)
public class AddAppenderOfFileEndpoint {

    /**
     * Logger动态添加appender到控制台
     *
     * @param arg0  logger名字
     * @param arg1 日志级别
     * @param arg2 日志路径
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @WriteOperation
    public Integer addAppenderToFile(@Selector String arg0, @Selector String arg1, @Selector String arg2) {
        return DynamicLogUtils.addAppenderToFile(arg0, arg1, arg2);
    }

    /**
     * 删除Logger中的appender
     *
     * @param arg0 logger名字
     * @return 删除结果：0-没有删除成功，1-删除成功
     */
    @DeleteOperation
    public Integer deleteAppenderOfFile(@Selector String arg0) {
        return DynamicLogUtils.deleteAppenderOfFile(arg0);
    }
}
