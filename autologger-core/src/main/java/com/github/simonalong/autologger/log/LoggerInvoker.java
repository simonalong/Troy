package com.github.simonalong.autologger.log;

import com.alibaba.fastjson.JSON;
import com.github.simonalong.autologger.annotation.AutoLogger;
import com.github.simonalong.autologger.util.EncryptUtil;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日志回调处理器
 *
 * @author shizi
 * @since 2021-02-04 18:19:10
 */
@Slf4j
@UtilityClass
public class LoggerInvoker {

    private final String LOG_PRE = "[auto-logger] ";
    /**
     * 日志映射管理map，key为group, value为map： key为logName实体（如果logName为空，则为函数的类全限定名加上#，再加上函数名）, value为bean的日志包装类
     */
    private final Map<String, Map<LogFunEntity, LoggerBeanWrapper>> loggerProxyMap = new HashMap<>();
    private final Map<Integer, LogLevel> logLevelIndexMap;
    private final Map<String, LogLevel> logLevelNameMap;

    static {
        logLevelIndexMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::ordinal, e -> e));
        logLevelNameMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::name, e -> e));
    }

    public void put(String[] groups, String logFunName) {
        Arrays.stream(groups).forEach(group -> loggerProxyMap.compute(group, (k, v) -> {
            if (null == v) {
                Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = new HashMap<>();
                loggerBeanWrapperMap.put(new LogFunEntity(EncryptUtil.SHA256(logFunName), logFunName), new LoggerBeanWrapper());
                return loggerBeanWrapperMap;
            } else {
                v.putIfAbsent(new LogFunEntity(EncryptUtil.SHA256(logFunName), logFunName), new LoggerBeanWrapper());
                return v;
            }
        }));
    }

    /**
     * 查看是否激活日志，只要有任何一个group中有激活即可
     *
     * @param groups     分组
     * @param logFunName 日志
     * @return 激活结果
     */
    public Boolean enableLogger(String[] groups, String logFunName) {
        for (String group : groups) {
            if (loggerProxyMap.containsKey(group)) {
                Map<LogFunEntity, LoggerBeanWrapper> beanWrapperMap = loggerProxyMap.get(group);
                LogFunEntity logFunEntity = new LogFunEntity(EncryptUtil.SHA256(logFunName), logFunName);
                if (!beanWrapperMap.containsKey(logFunEntity)) {
                    continue;
                }
                LoggerBeanWrapper beanWrapper = beanWrapperMap.get(logFunEntity);
                if (beanWrapper.openLogger()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<String> getGroupSet() {
        return loggerProxyMap.keySet();
    }

    public Set<String> getFunList(String group) {
        if (loggerProxyMap.containsKey(group)) {
            return loggerProxyMap.get(group).keySet().stream().map(e -> MessageFormat.format("fun: {0} = {1}", e.getLogFunId(), e.getLogFunName())).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public LoggerBeanWrapperRsp getLoggerInfo(String group, String logFunId) {
        if (!loggerProxyMap.containsKey(group)) {
            return null;
        }
        Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        LogFunEntity logFunEntity = new LogFunEntity(logFunId, null);
        if (!loggerBeanWrapperMap.containsKey(logFunEntity)) {
            return null;
        }

        LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logFunEntity);
        LoggerBeanWrapperRsp wrapperRsp = new LoggerBeanWrapperRsp();
        wrapperRsp.setLogLevel(loggerBeanWrapper.getLogLevel());
        wrapperRsp.setLoggerEnable(loggerBeanWrapper.getLoggerEnable());
        return wrapperRsp;
    }

    /**
     * 更新组的所有函数
     *
     * @param group 分组
     * @param logLevelName 日志级别
     * @param enable 激活标示
     * @return 更改结果：0-没有变更，n-变更个数
     */
    public Integer updateLoggerBeanLog(String group, String logLevelName, Boolean enable) {
        if (null == group) {
            return 0;
        }

        if (!loggerProxyMap.containsKey(group)) {
            return 0;
        }

        Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        loggerBeanWrapperMap.values().forEach(loggerBeanWrapper->{
            if (null != logLevelName) {
                loggerBeanWrapper.setLogLevel(parseLogLevel(logLevelName));
            }
            if (null != enable) {
                loggerBeanWrapper.setLoggerEnable(enable);
            }
        });
        return loggerBeanWrapperMap.values().size();
    }

    /**
     * 更新bean的日志
     *
     * @param group        日志分组
     * @param logFunId     日志函数标示
     * @param logLevelName 日志级别
     * @param enable       日志激活标示
     * @return 更新结果标示：0-没有更新成功，1-更新成功
     */
    public Integer updateLoggerBeanLog(String group, String logFunId, String logLevelName, Boolean enable) {
        if (null == group || null == logFunId) {
            return 0;
        }

        if (!loggerProxyMap.containsKey(group)) {
            return 0;
        }

        Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        LogFunEntity logFunEntity = new LogFunEntity(logFunId, null);
        if (!loggerBeanWrapperMap.containsKey(logFunEntity)) {
            return 0;
        }

        LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logFunEntity);
        if (null != logLevelName) {
            loggerBeanWrapper.setLogLevel(parseLogLevel(logLevelName));
        }
        if (null != enable) {
            loggerBeanWrapper.setLoggerEnable(enable);
        }
        return 1;
    }

    /**
     * 正常的打印
     *
     * @param method 方法
     * @param args   参数
     * @param result 返回值
     */
    public void postInvoke(Method method, Object[] args, Object result) {
        AutoLogger autoLogger = null;
        if (method.getDeclaringClass().isAnnotationPresent(AutoLogger.class)) {
            autoLogger = method.getDeclaringClass().getAnnotation(AutoLogger.class);
        }

        if (method.isAnnotationPresent(AutoLogger.class)) {
            autoLogger = method.getAnnotation(AutoLogger.class);
        }

        if (null == autoLogger) {
            return;
        }

        String[] groups = autoLogger.group();
        String logName = autoLogger.value();
        if ("".equals(logName)) {
            logName = generateMethodName(method);
        }

        for (String group : groups) {
            Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
            LogFunEntity logFunEntity = new LogFunEntity(EncryptUtil.SHA256(logName), logName);
            if (!loggerBeanWrapperMap.containsKey(logFunEntity)) {
                continue;
            }

            LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logFunEntity);
            if (!loggerBeanWrapper.openLogger()) {
                return;
            }

            HashMap<String, Object> outInfo = new HashMap<>();
            outInfo.put("response", result);
            printLog(loggerBeanWrapper, outInfo, method, args);
            return;
        }
    }

    /**
     * 异常的打印
     *
     * @param method    方法
     * @param args      参数
     * @param throwable 异常
     */
    @SuppressWarnings("all")
    public void throwableInvoke(Method method, Object[] args, Throwable throwable) {
        AutoLogger autoLogger = null;
        if (method.getDeclaringClass().isAnnotationPresent(AutoLogger.class)) {
            autoLogger = method.getDeclaringClass().getAnnotation(AutoLogger.class);
        }

        if (method.isAnnotationPresent(AutoLogger.class)) {
            autoLogger = method.getAnnotation(AutoLogger.class);
        }

        if (null == autoLogger) {
            return;
        }

        String[] groups = autoLogger.group();
        String logName = autoLogger.value();
        if (null == logName || "".equals(logName)) {
            logName = generateMethodName(method);
        }

        for (String group : groups) {
            Map<LogFunEntity, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
            LogFunEntity logFunEntity = new LogFunEntity(EncryptUtil.SHA256(logName), logName);
            if (!loggerBeanWrapperMap.containsKey(logFunEntity)) {
                continue;
            }

            LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logFunEntity);
            if (!loggerBeanWrapper.openLogger()) {
                return;
            }

            HashMap<String, Object> outInfo = new HashMap<>();
            outInfo.put("throwable", throwable);

            printLog(loggerBeanWrapper, outInfo, method, args);
            return;
        }
    }

    private void printLog(LoggerBeanWrapper beanWrapper, HashMap<String, Object> outInfo, Method method, Object[] args) {
        outInfo.put("fun", method.toString());
        outInfo.put("parameters", Arrays.asList(args));

        LogLevel logLevel = beanWrapper.getLogLevel();
        switch (logLevel) {
            case OFF:
                return;
            case DEBUG:
                log.debug(LOG_PRE + "结果：" + JSON.toJSONString(outInfo));
                break;
            case TRACE:
                log.trace(LOG_PRE + "结果：" + JSON.toJSONString(outInfo));
                break;
            case INFO:
                log.info(LOG_PRE + "结果：" + JSON.toJSONString(outInfo));
                break;
            case WARN:
                log.warn(LOG_PRE + "结果：" + JSON.toJSONString(outInfo));
                break;
            case ERROR:
            case FATAL:
                log.error(LOG_PRE + "结果：" + JSON.toJSONString(outInfo));
                break;
            default:
                break;
        }
    }

    @Data
    private class LoggerBeanWrapper {

        private LogLevel logLevel = LogLevel.INFO;
        private Boolean loggerEnable = false;

        public Boolean openLogger() {
            return loggerEnable;
        }
    }

    public static LogLevel parseLogLevel(Integer index) {
        if (null == index) {
            return LogLevel.OFF;
        }
        if (!logLevelIndexMap.containsKey(index)) {
            throw new RuntimeException("不支持下标: " + index);
        }
        return logLevelIndexMap.get(index);
    }

    public static LogLevel parseLogLevel(String name) {
        if (null == name || "".equals(name)) {
            return LogLevel.OFF;
        }
        if (!logLevelNameMap.containsKey(name.trim().toUpperCase())) {
            throw new RuntimeException("不支持name: " + name.trim().toUpperCase());
        }
        return logLevelNameMap.get(name.trim().toUpperCase());
    }

    public String generateMethodName(Method method) {
        String parameters = Arrays.stream(method.getParameterTypes()).map(Class::getCanonicalName).collect(Collectors.joining(","));
        return MessageFormat.format("{0}#{1}({2})", method.getDeclaringClass().getCanonicalName(), method.getName(), parameters);
    }
}
