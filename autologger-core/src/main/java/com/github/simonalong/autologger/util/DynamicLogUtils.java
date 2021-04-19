package com.github.simonalong.autologger.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * logback的日志动态变更工具
 *
 * @author shizi
 * @since 2020-11-24 23:37:23
 */
@UtilityClass
public class DynamicLogUtils {

    private final HashSet<String> logLevelSet = new HashSet<String>() {{
        add(Level.TRACE.toString());
        add(Level.DEBUG.toString());
        add(Level.INFO.toString());
        add(Level.WARN.toString());
        add(Level.ERROR.toString());
        add(Level.OFF.toString());
        add(Level.ALL.toString());
    }};

    public String getLevelOfRoot() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger("root");
        return logger.getLevel().levelStr;
    }

    /**
     * 设置根目录的日志级别
     *
     * @param logLevel logback的日志级别对应的字符：ALL(all)，TRACE(trace)，DEBUG(debug)，INFO(info)，WARN(warn)，ERROR(error)，OFF(off)
     * @return 操作结果：0-没有修改，1-修改完成
     */
    public Integer setLevelOfRoot(String logLevel) {
        if (null == logLevel || "".equals(logLevel) || !logLevelSet.contains(logLevel.toUpperCase())) {
            return 0;
        }
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger("root");
        logger.setLevel(Level.toLevel(logLevel));
        return 1;
    }

    /**
     * 获取所有的logger列表
     *
     * @param loggerNamePre logger名字前缀
     * @return 返回所有的logger集合
     */
    public List<Logger> getLoggerList(String loggerNamePre) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLoggerList().stream().filter(logger -> logger.getName().startsWith(loggerNamePre)).collect(Collectors.toList());
    }

    public List<Logger> getAllLoggerList() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLoggerList();
    }

    /**
     * Logger动态添加appender
     *
     * @param loggerName  logger名字
     * @param logLevelStr 日志级别
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    public Integer addAppenderToConsole(String loggerName, String logLevelStr) {
        if (null == loggerName || "".equals(loggerName) || null == logLevelStr || "".equals(logLevelStr)) {
            return 0;
        }

        if (!logLevelSet.contains(logLevelStr.toUpperCase())) {
            return 0;
        }

        // 获取logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        if (null == logger) {
            return 0;
        }

        // 设置不继承上层日志级别
        logger.setAdditive(false);

        Level level = Level.toLevel(logLevelStr);
        // 设置当前日志级别
        logger.setLevel(level);
        logger.addAppender(generateConsoleAppender(loggerContext, loggerName, level));
        return 1;
    }

    public Integer deleteAppender(String loggerName) {
        if (null == loggerName || "".equals(loggerName)) {
            return 0;
        }

        // 获取logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        if (null == logger) {
            return 0;
        }

        logger.detachAppender(loggerName);
        return 1;
    }

    /**
     * 删除Logger中的appender
     *
     * @param loggerName logger名字
     * @return 删除结果：0-没有删除成功，1-删除成功
     */
    public Integer deleteAppenderOfConsole(String loggerName) {
        if (null == loggerName || "".equals(loggerName)) {
            return 0;
        }

        // 获取logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        if (null == logger) {
            return 0;
        }

        logger.detachAppender(generateAppenderNameOfConsole(loggerName));
        return 1;
    }

    /**
     * Logger动态添加appender到文件中
     *
     * @param loggerName  logger名字
     * @param logLevelStr 日志级别
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    public Integer addAppenderToFile(String loggerName, String logLevelStr) {
        if (null == loggerName || "".equals(loggerName) || null == logLevelStr || "".equals(logLevelStr)) {
            return 0;
        }

        if (!logLevelSet.contains(logLevelStr.toUpperCase())) {
            return 0;
        }

        // 获取logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        if (null == logger) {
            return 0;
        }

        // 设置不继承上层日志级别
        logger.setAdditive(false);

        Level level = Level.toLevel(logLevelStr);
        // 设置当前日志级别
        logger.setLevel(level);
        logger.addAppender(generateFileAppender(loggerContext, loggerName, level, getLogHome(loggerContext)));
        return 1;
    }

    private String getLogHome(LoggerContext loggerContext) {
        return loggerContext.getCopyOfPropertyMap().get("LOG_HOME");
    }

    /**
     * 删除Logger中的appender
     *
     * @param loggerName logger名字
     * @return 添加结果：0-没有删除成功，1-删除成功
     */
    public Integer deleteAppenderOfFile(String loggerName) {
        if (null == loggerName || "".equals(loggerName)) {
            return 0;
        }

        // 获取logger
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        if (null == logger) {
            return 0;
        }

        logger.detachAppender(generateAppenderNameOfFile(loggerName));

        // 恢复继承ROOT级别
        logger.setAdditive(true);
        return 1;
    }

    private Appender<ILoggingEvent> generateConsoleAppender(LoggerContext loggerContext, String loggerName, Level level) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName(generateAppenderNameOfConsole(loggerName));
        appender.setContext(loggerContext);

        appender.setLayout(generateLayout(loggerContext));
        appender.addFilter(generateFilter(level));
        appender.start();
        return appender;
    }

    private Appender<ILoggingEvent> generateFileAppender(LoggerContext loggerContext, String loggerName, Level level, String logHome) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName(generateAppenderNameOfFile(loggerName));
        appender.setContext(loggerContext);

        appender.setFile(getLogHome(logHome) + "/app-" + level.levelStr.toLowerCase() + ".log");
        appender.addFilter(generateFilter(level));
        appender.setEncoder(generateEncoder(loggerContext));
        appender.setRollingPolicy(generateRollingPolicy(loggerContext, level, logHome, appender));
        appender.start();
        return appender;
    }

    private Layout<ILoggingEvent> generateLayout(LoggerContext loggerContext) {
        PatternLayout patternLayout = new PatternLayout();

        patternLayout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{traceId}] [%X{userId}] [%X{source}] %p %c %M [%t@${PID}] : %m%n");
        patternLayout.setContext(loggerContext);
        patternLayout.start();
        return patternLayout;
    }

    private Encoder<ILoggingEvent> generateEncoder(LoggerContext loggerContext) {
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);

        encoder.setLayout(generateLayout(loggerContext));
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();
        return encoder;
    }

    private RollingPolicy generateRollingPolicy(LoggerContext loggerContext, Level level, String logHome, RollingFileAppender<ILoggingEvent> rollingFileAppender) {
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(getLogHome(logHome) + "/app-" + level.levelStr.toLowerCase() + ".log.%d{yyyy-MM-dd}.%i");
        rollingPolicy.setMaxFileSize(FileSize.valueOf("100mb"));
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.start();
        return rollingPolicy;
    }

    private Filter<ILoggingEvent> generateFilter(Level level) {
        EvaluatorFilter<ILoggingEvent> filter = new EvaluatorFilter<>();

        filter.setEvaluator(generateEvaluator(level));
        filter.setOnMatch(FilterReply.ACCEPT);
        filter.setOnMismatch(FilterReply.NEUTRAL);
        filter.start();
        return filter;
    }

    private EventEvaluator<ILoggingEvent> generateEvaluator(Level level) {
        JaninoEventEvaluator eventEvaluator = new JaninoEventEvaluator();

        String express = "if(level == %s) {\n" + "            return true;\n" + "        }\n" + "        return false;";
        eventEvaluator.setExpression(String.format(express, level.levelStr));
        eventEvaluator.start();
        return eventEvaluator;
    }

    private String generateAppenderNameOfConsole(String loggerName) {
        return loggerName + ":dynamic-appender:console";
    }

    private String generateAppenderNameOfFile(String loggerName) {
        return loggerName + ":dynamic-appender:file";
    }

    private String getLogHome(String logHome) {
        if (null == logHome || "".equals(logHome)) {
            return "/home/default/logs";
        }
        return logHome;
    }
}
