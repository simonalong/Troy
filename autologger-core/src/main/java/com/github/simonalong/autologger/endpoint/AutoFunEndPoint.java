package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.log.LoggerBeanWrapperRsp;
import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.github.simonalong.autologger.AutoLoggerConstant.AUTO_FUN;

/**
 * @author shizi
 * @since 2021-02-02 23:48:02
 */
@Endpoint(id = AUTO_FUN)
public class AutoFunEndPoint {

    @ReadOperation
    public Set<String> getFunList(@Selector String arg0) {
        return LoggerInvoker.getFunList(arg0);
    }

    /**
     * 获取日志信息
     *
     * @param arg0 分组
     * @param arg1 funId
     * @return 日志信息
     */
    @ReadOperation
    public LoggerBeanWrapperRsp getLoggerInfo(@Selector String arg0, @Selector String arg1) {
        return LoggerInvoker.getLoggerInfo(arg0, arg1);
    }

    /**
     * 更新bean的日志
     *
     * @param arg0 日志分组
     * @param arg1 日志函数标示
     * @param arg2 日志级别
     * @param arg3 日志激活标示
     * @return 更新结果标示：0-没有更新成功，1-更新成功
     */
    @WriteOperation
    public Integer updateLoggerBeanLog(@Selector String arg0, @Selector String arg1, @Selector String arg2, @Selector String arg3) {
        return LoggerInvoker.updateLoggerBeanLog(arg0, arg1, arg2, Boolean.valueOf(arg3));
    }
}
