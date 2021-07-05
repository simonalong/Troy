package com.github.simonalong.troy.log;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

/**
 * @author shizi
 * @since 2021-02-05 11:00:45
 */
@Data
public class LoggerBeanWrapperRsp {

    private String logFunName;
    private LogLevel logLevel = LogLevel.INFO;
    private Boolean loggerEnable = false;
}
