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
@Component
@Endpoint(id = AUTO_FUN)
public class AutoFunEndPoint {

    @ReadOperation
    public Set<String> getFunList(@Selector String arg0) {
        return LoggerInvoker.getFunList(arg0);
    }

    @ReadOperation
    public LoggerBeanWrapperRsp getLoggerInfo(@Selector String arg0, @Selector String arg1) {
        return LoggerInvoker.getLoggerInfo(arg0, arg1);
    }

    @WriteOperation
    public Integer updateLoggerBeanLog(@Selector String arg0, @Selector String arg1, @Selector String arg2, @Selector String arg3) {
        return LoggerInvoker.updateLoggerBeanLog(arg0, arg1, arg2, Boolean.valueOf(arg3));
    }
}
