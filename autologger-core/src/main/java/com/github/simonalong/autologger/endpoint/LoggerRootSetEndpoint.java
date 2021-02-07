package com.github.simonalong.autologger.endpoint;

import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import static com.github.simonalong.autologger.AutoLoggerConstant.LOGGER_ROOT_SET;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Component
@Endpoint(id = LOGGER_ROOT_SET)
public class LoggerRootSetEndpoint {

    /**
     * 将root级别变更
     *
     * @param arg0 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer setLevelOfRoot(@Selector String arg0) {
        return DynamicLogUtils.setLevelOfRoot(arg0);
    }
}
