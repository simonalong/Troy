package com.github.simonalong.autologger.log;

import com.alibaba.fastjson.JSON;
import com.github.simonalong.autologger.annotation.AutoLogger;
import com.github.simonalong.autologger.util.TimeRangeStrUtil;
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
     * 日志映射管理map，key为group, value为map： key为logName（如果指定空，则为函数的类全限定名加上#，再加上函数名）, value为bean的日志包装类
     */
    private final Map<String, Map<String, LoggerBeanWrapper>> loggerProxyMap = new HashMap<>();
    private final Map<Integer, LogLevel> logLevelIndexMap;
    private final Map<String, LogLevel> logLevelNameMap;
    /**
     * 耗时线程上下文
     */
    private final ThreadLocal<Long> timeoutContext = new ThreadLocal<>();

    static {
        logLevelIndexMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::ordinal, e -> e));
        logLevelNameMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::name, e -> e));
    }

    public void put(String[] groups, String logName, Object value) {
        Arrays.stream(groups).forEach(group -> loggerProxyMap.compute(group, (k, v) -> {
            if (null == v) {
                Map<String, LoggerBeanWrapper> loggerBeanWrapperMap = new HashMap<>();
                loggerBeanWrapperMap.put(logName, new LoggerBeanWrapper(value));
                return loggerBeanWrapperMap;
            } else {
                v.putIfAbsent(logName, new LoggerBeanWrapper(value));
                return v;
            }
        }));
    }

    public Set<String> getGroupSet() {
        return loggerProxyMap.keySet();
    }

    public Set<String> getLogNameSet(String group) {
        if (loggerProxyMap.containsKey(group)) {
            return loggerProxyMap.get(group).keySet();
        }
        return Collections.emptySet();
    }

    /**
     * 更新bean的日志
     *
     * @param group 日志分组
     * @param logName 日志
     * @param logLevelName 日志级别
     * @param enable 日志激活标示
     * @return 更新结果标示：0-没有更新成功，1-更新成功
     */
    public Integer updateLoggerBeanLog(String group, String logName, String logLevelName, Boolean enable) {
        if(!loggerProxyMap.containsKey(group)) {
            return 0;
        }

        Map<String, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        if (!loggerBeanWrapperMap.containsKey(logLevelName)) {
            return 0;
        }

        LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logLevelName);
        loggerBeanWrapper.setLogLevel(parseLogLevel(logName));
        loggerBeanWrapper.setLoggerEnable(enable);
        return 1;
    }

    public void preInvoke() {
        timeoutContext.set(System.currentTimeMillis());
    }

    /**
     * 正常的打印
     *
     * @param method 方法
     * @param args   参数
     * @param result 返回值
     */
    public void postInvoke(Method method, Object[] args, Object result) {
        try {
            if (!loggerProxyMap.containsKey(method.toGenericString())) {
                return;
            }

            AutoLogger autoLogger = method.getDeclaredAnnotation(AutoLogger.class);
            if (null == autoLogger) {
                return;
            }

            String[] groups = autoLogger.group();
            String logName = autoLogger.value();
            if ("".equals(logName)) {
                logName = generateMethodName(method);
            }

            for (String group : groups) {
                Map<String, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
                if (!loggerBeanWrapperMap.containsKey(logName)) {
                    continue;
                }

                LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logName);
                if (!loggerBeanWrapper.openLogger()) {
                    return;
                }

                HashMap<String, Object> outInfo = new HashMap<>();
                outInfo.put("response", result);
                printLog(loggerBeanWrapper, outInfo, method, args);
                return;
            }
        } finally {
            timeoutContext.remove();
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
        try {
            if (!loggerProxyMap.containsKey(method.toGenericString())) {
                return;
            }

            AutoLogger autoLogger = method.getDeclaredAnnotation(AutoLogger.class);
            if (null == autoLogger) {
                return;
            }

            String[] groups = autoLogger.group();
            String logName = autoLogger.value();
            if (null == logName || "".equals(logName)) {
                logName = generateMethodName(method);
            }

            for (String group : groups) {
                Map<String, LoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
                if (!loggerBeanWrapperMap.containsKey(logName)) {
                    continue;
                }

                LoggerBeanWrapper loggerBeanWrapper = loggerBeanWrapperMap.get(logName);
                if (!loggerBeanWrapper.openLogger()) {
                    return;
                }

                HashMap<String, Object> outInfo = new HashMap<>();
                outInfo.put("throwable", throwable);

                printLog(loggerBeanWrapper, outInfo, method, args);
                return;
            }
        } finally {
            timeoutContext.remove();
        }
    }

    private void printLog(LoggerBeanWrapper beanWrapper, HashMap<String, Object> outInfo, Method method, Object[] args) {
        outInfo.put("fun", method.toString());
        outInfo.put("parameters", Arrays.asList(args));
        outInfo.put("timeout", TimeRangeStrUtil.parseTime(System.currentTimeMillis() - timeoutContext.get()));

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
        private Object beanProxy;

        public LoggerBeanWrapper(Object beanProxy) {
            this.beanProxy = beanProxy;
        }

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
        if (!logLevelNameMap.containsKey(name)) {
            throw new RuntimeException("不支持name: " + name);
        }
        return logLevelNameMap.get(name.trim().toUpperCase());
    }


    public String generateMethodName(Method method) {
        return MessageFormat.format("{1}#{2}", method.getClass().getCanonicalName(), method.getName());
    }
}
