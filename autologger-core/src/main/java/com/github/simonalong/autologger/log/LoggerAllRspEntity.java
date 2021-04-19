package com.github.simonalong.autologger.log;

import lombok.Data;

import java.util.List;

/**
 * @author shizi
 * @since 2021-04-13 11:02:39
 */
@Data
public class LoggerAllRspEntity {

    private String loggerName;
    private String logLevelStr;
    private List<AppenderEntity> appenderList;
}
