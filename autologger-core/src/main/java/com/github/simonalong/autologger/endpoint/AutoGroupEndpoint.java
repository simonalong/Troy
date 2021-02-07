package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.log.LoggerInvoker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.github.simonalong.autologger.AutoLoggerConstant.AUTO_GROUP;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Endpoint(id = AUTO_GROUP)
public class AutoGroupEndpoint {

    @ReadOperation
    public Set<String> groups() {
        return LoggerInvoker.getGroupSet();
    }

    /**
     * 更新组的所有函数
     *
     * @param arg0 分组
     * @param arg1 日志级别
     * @param arg2 激活标示
     * @return 更改结果：0-没有变更，n-变更个数
     */
    @WriteOperation
    public Integer updateLoggerBeanLog(@Selector String arg0, @Selector String arg1, @Selector String arg2) {
        return LoggerInvoker.updateLoggerBeanLog(arg0, arg1, Boolean.valueOf(arg2));
    }
}
