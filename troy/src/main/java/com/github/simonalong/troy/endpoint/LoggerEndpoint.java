package com.github.simonalong.troy.endpoint;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.simonalong.troy.log.AppenderEntity;
import com.github.simonalong.troy.log.LoggerAllRspEntity;
import com.github.simonalong.troy.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.simonalong.troy.TroyConstants.LOGGER;

/**
 * @author shizi
 * @since 2021-04-16 16:06:02
 */
@Endpoint(id = LOGGER)
public class LoggerEndpoint {

    /**
     * 获取所有的logger信息
     *
     * @return 所有的logger信息
     */
    @ReadOperation
    public List<LoggerAllRspEntity> getAllLoggerList() {
        return DynamicLogUtils.getAllLoggerList().stream().map(this::loggerToEntity).collect(Collectors.toList());
    }

    /**
     * 获取服务的root的日志级别
     * @param arg0 日志名
     */
    @ReadOperation
    public List<LoggerAllRspEntity> getLevelInfo(@Selector String arg0) {
        return DynamicLogUtils.getAllLoggerList().stream().filter(e->e.getName().toLowerCase().contains(arg0)).map(this::loggerToEntity).collect(Collectors.toList());
    }

    /**
     * 将logger级别变更
     *
     * @param loggerName 日志名
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer updateLevelOfOne(String loggerName, String logLevel) {
        if (logLevel.endsWith("\b")) {
            logLevel = logLevel.substring(0, logLevel.length() - 1);
        }
        if ("root".equals(loggerName)) {
            return DynamicLogUtils.setLevelOfRoot(logLevel);
        } else {
            return DynamicLogUtils.setLevelOfLogger(loggerName, logLevel);
        }
    }

    /**
     * 将logger级别变更并打印
     *
     * @param arg0 appender
     * @param arg1 console、file或者all
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer addLevelOfOneAndPrint(@Selector String arg0, @Selector String arg1, String loggerName, String logLevel) {
        DynamicLogUtils.setLevelOfLogger(loggerName, logLevel);

        if ("console".equals(arg1)) {
            return DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
        } else if ("file".equals(arg1)) {
            return DynamicLogUtils.addAppenderToFile(loggerName, logLevel);
        } else if ("all".equals(arg1)) {
            Integer count = DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
            return count + DynamicLogUtils.addAppenderToFile(loggerName, logLevel);
        } else {
            return 0;
        }
    }

    /**
     * 将logger信息恢复到初始
     *
     * @param arg0 restore
     * @param arg1 all
     * @param arg2 info
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer restoreAllInfo(@Selector String arg0, @Selector String arg1, @Selector String arg2, String loggerName, String logLevel) {
        DynamicLogUtils.setLevelOfLogger(loggerName, logLevel, true);

        DynamicLogUtils.deleteAppenderOfConsole(loggerName);
        return DynamicLogUtils.deleteAppenderOfFile(loggerName);
    }

    private LoggerAllRspEntity loggerToEntity(Logger logger) {
        LoggerAllRspEntity rspEntity = new LoggerAllRspEntity();

        rspEntity.setLoggerName(logger.getName());
        // 该logger是否继承Root，如果继承，则将该级别设置
        if (logger.isAdditive()) {
            rspEntity.setLogLevelStr(logger.getLoggerContext().getLogger("ROOT").getLevel().levelStr);
        } else {
            if (null != logger.getLevel()) {
                rspEntity.setLogLevelStr(logger.getLevel().levelStr);
            }
        }
        rspEntity.setAppenderList(getAppenderList(logger));
        return rspEntity;
    }

    private List<AppenderEntity> getAppenderList(Logger logger) {
        List<AppenderEntity> appenderList = new ArrayList<>();
        Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();

        while (iterator.hasNext()) {
            Appender<ILoggingEvent> appender = iterator.next();
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(appender));
            if (jsonObject.containsKey("encoder")) {
                if (jsonObject.getJSONObject("encoder").containsKey("pattern")) {
                    appenderList.add(new AppenderEntity(appender.getName(), jsonObject.getJSONObject("encoder").getString("pattern")));
                }
            } else {
                appenderList.add(new AppenderEntity(appender.getName(), ""));
            }
        }
        return appenderList;
    }
}
