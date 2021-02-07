package com.github.simonalong.autologger.endpoint;

import com.alibaba.fastjson.JSON;
import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import static com.github.simonalong.autologger.AutoLoggerConstant.LOGGER_SEARCH;

/**
 * @author shizi
 * @since 2021-02-02 23:36:28
 */
@Component
@Endpoint(id = LOGGER_SEARCH)
public class LoggerSearchEndpoint {

    /**
     * 模糊匹配获取logger集合
     *
     * @param arg0 logger名字的前缀
     * @return logger集合的json展示
     */
    @ReadOperation
    public String getLoggerList(@Selector String arg0) {
        return JSON.toJSONString(DynamicLogUtils.getLoggerList(arg0));
    }
}
