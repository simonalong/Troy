package com.github.simonalong.autologger.endpoint;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.simonalong.autologger.log.AppenderEntity;
import com.github.simonalong.autologger.log.LoggerAllRspEntity;
import com.github.simonalong.autologger.util.DynamicLogUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.simonalong.autologger.AutoLoggerConstants.LOGGER;

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
        return DynamicLogUtils.getAllLoggerList().stream().filter(e -> e.getName().equals("ROOT")).map(this::loggerToEntity).collect(Collectors.toList());
    }

    /**
     * 获取服务的root的日志级别
     * @param arg0 root
     */
    @ReadOperation
    public String getLevelOfRoot(@Selector String arg0) {
        return DynamicLogUtils.getLevelOfRoot();
    }

    /**
     * 模糊匹配获取logger集合
     *
     * @param arg0 search
     * @param arg1 list
     * @param loggerName logger名字的前缀
     * @return logger集合的json展示
     */
    @ReadOperation
    public List<LoggerAllRspEntity> getLoggerListFromSearch(@Selector String arg0, @Selector String arg1, String loggerName) {
        return DynamicLogUtils.getLoggerList(loggerName).stream().map(this::loggerToEntity).collect(Collectors.toList());
    }

    /**
     * 将root级别变更
     *
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer updateLevelOfRoot(String logLevel) {
        return DynamicLogUtils.setLevelOfRoot(logLevel);
    }

    /**
     * 将logger级别变更
     *
     * @param arg0 name
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer updateLevelOfOne(@Selector String arg0, String loggerName, String logLevel) {
        return DynamicLogUtils.setLevelOfLogger(loggerName, logLevel);
    }

    /**
     * 将logger级别变更并打印
     *
     * @param arg0 name
     * @param arg1 console、file或者all
     * @param logLevel 日志级别
     * @return 操作结果：0-没有修改，1-修改完成
     */
    @WriteOperation
    public Integer updateLevelOfOneAndPrint(@Selector String arg0, @Selector String arg1, String loggerName, String logLevel) {
        DynamicLogUtils.setLevelOfLogger(loggerName, logLevel);

        if ("console".equals(arg1)) {
            return DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
        } else if ("file".equals(arg1)) {
            return DynamicLogUtils.addAppenderToFile(loggerName, logLevel);
        } else if ("all".equals(arg1)) {
            DynamicLogUtils.addAppenderToConsole(loggerName, logLevel);
            return DynamicLogUtils.addAppenderToFile(loggerName, logLevel);
        } else {
            return 0;
        }
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
