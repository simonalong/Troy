package com.github.simonalong.troy.log;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

/**
 * @author shizi
 * @since 2021-04-13 10:19:51
 */
@Data
public class FunLoggerBeanWrapper {

    /**
     * 函数的全限定名，比如：com.github.simonalong.sample.controller.BusinessController#troyTest(com.github.simonalong.sample.vo.req.Fun1Req)
     */
    private String logFunName;
    /**
     * 函数所在的loggerName，这里默认用函数的类名，比如：com.github.simonalong.sample.controller.BusinessController
     */
    private String loggerName;
    private LogLevel logLevel = LogLevel.INFO;
    private Boolean loggerEnable = false;

    public FunLoggerBeanWrapper(String className, String logFunName) {
        this.logFunName = logFunName;
        this.loggerName = className;
    }

    public Boolean openLogger() {
        return loggerEnable;
    }
}
