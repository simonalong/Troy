package com.github.simonalong.autologger.log;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

/**
 * @author shizi
 * @since 2021-04-13 10:19:51
 */
@Data
public class FunLoggerBeanWrapper {

    private String logFunName;
    private LogLevel logLevel = LogLevel.INFO;
    private Boolean loggerEnable = false;

    public FunLoggerBeanWrapper(String logFunName) {
        this.logFunName = logFunName;
    }

    public Boolean openLogger() {
        return loggerEnable;
    }
}
