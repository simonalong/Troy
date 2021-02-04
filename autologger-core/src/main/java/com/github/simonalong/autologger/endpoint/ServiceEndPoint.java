package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author shizi
 * @since 2021-02-02 23:48:02
 */
@Endpoint(id = "logger-service")
public class ServiceEndPoint {

    @ReadOperation
    public Set<String> logNameSet(@Selector String group) {
        return LoggerInvoker.getLogNameSet(group);
    }

    @WriteOperation
    public Integer update(@Selector String group) {
        return 0;
    }
}
