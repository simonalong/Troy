package com.github.simonalong.autologger.endpoint;


import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Component
@Endpoint(id = "logger-group")
public class GroupListEndpoint {

    @ReadOperation
    public Set<String> groups() {
        return LoggerInvoker.getGroupSet();
    }

    @WriteOperation
    public Integer updateLoggerBeanLog(@Selector String arg0, @Selector String arg1, @Selector String arg2) {
        return LoggerInvoker.updateLoggerBeanLog(arg0, arg1, Boolean.valueOf(arg2));
    }
}
