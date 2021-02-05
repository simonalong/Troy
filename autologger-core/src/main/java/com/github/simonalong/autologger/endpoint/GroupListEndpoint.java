package com.github.simonalong.autologger.endpoint;


import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
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
}
