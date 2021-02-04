package com.github.simonalong.autologger.endpoint;


import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Endpoint(id = "logger-group")
public class GroupListEndpoint {

    @ReadOperation
    public Set<String> groups() {
        return LoggerInvoker.getGroupSet();
    }
}
