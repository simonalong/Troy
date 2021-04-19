package com.github.simonalong.autologger.log;

import com.alibaba.fastjson.JSON;
import com.github.simonalong.autologger.annotation.WatchLogger;
import com.github.simonalong.autologger.util.EncryptUtil;
import lombok.Getter;
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
    @Getter
    private final Map<String, Map<String, FunLoggerBeanWrapper>> loggerProxyMap = new HashMap<>();
    private final Map<Integer, LogLevel> logLevelIndexMap;
    private final Map<String, LogLevel> logLevelNameMap;

    static {
        logLevelIndexMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::ordinal, e -> e));
        logLevelNameMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::name, e -> e));
    }

    public void put(String[] groups, String logFunName) {
        Arrays.stream(groups).forEach(group -> loggerProxyMap.compute(group, (k, v) -> {
            if (null == v) {
                Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = new HashMap<>();
                loggerBeanWrapperMap.put(EncryptUtil.SHA256(logFunName), new FunLoggerBeanWrapper(logFunName));
                return loggerBeanWrapperMap;
            } else {
                v.putIfAbsent(EncryptUtil.SHA256(logFunName), new FunLoggerBeanWrapper(logFunName));
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
                Map<String, FunLoggerBeanWrapper> beanWrapperMap = loggerProxyMap.get(group);
                String funId = EncryptUtil.SHA256(logFunName);
                if (!beanWrapperMap.containsKey(funId)) {
                    continue;
                }
                FunLoggerBeanWrapper beanWrapper = beanWrapperMap.get(funId);
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

    public Set<String> getFunSet(String group) {
        if (loggerProxyMap.containsKey(group)) {
            return loggerProxyMap.get(group).entrySet().stream().map(e -> MessageFormat.format("fun: {0} = {1}", e.getKey(), e.getValue().getLogFunName())).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public com.github.simonalong.autologger.log.LoggerBeanWrapperRsp getLoggerInfo(String group, String logFunId) {
        if (!loggerProxyMap.containsKey(group)) {
            return null;
        }
        Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        if (!loggerBeanWrapperMap.containsKey(logFunId)) {
            return null;
        }

        FunLoggerBeanWrapper funLoggerBeanWrapper = loggerBeanWrapperMap.get(logFunId);
        com.github.simonalong.autologger.log.LoggerBeanWrapperRsp wrapperRsp = new com.github.simonalong.autologger.log.LoggerBeanWrapperRsp();
        wrapperRsp.setLogLevel(funLoggerBeanWrapper.getLogLevel());
        wrapperRsp.setLoggerEnable(funLoggerBeanWrapper.getLoggerEnable());
        wrapperRsp.setLogFunName(funLoggerBeanWrapper.getLogFunName());
        return wrapperRsp;
    }

    /**
     * 更新组的所有函数
     *
     * @param group 分组
     * @param logLevel 日志级别
     * @param enable 激活标示
     * @return 更改结果：0-没有变更，n-变更个数
     */
    public Integer updateLoggerBeanLog(String group, String logLevel, Boolean enable) {
        if (null == group) {
            return 0;
        }

        if (!loggerProxyMap.containsKey(group)) {
            return 0;
        }

        Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        loggerBeanWrapperMap.values().forEach(funLoggerBeanWrapper ->{
            if (null != logLevel) {
                funLoggerBeanWrapper.setLogLevel(parseLogLevel(logLevel));
            }
            if (null != enable) {
                funLoggerBeanWrapper.setLoggerEnable(enable);
            }
        });
        return loggerBeanWrapperMap.values().size();
    }

    /**
     * 更新bean的日志
     *
     * @param group        日志分组
     * @param funId     日志函数标示
     * @param logLevelName 日志级别
     * @param enable       日志激活标示
     * @return 更新结果标示：0-没有更新成功，1-更新成功
     */
    public Integer updateLoggerBeanLog(String group, String funId, String logLevelName, Boolean enable) {
        if (null == group || null == funId) {
            return 0;
        }

        if (!loggerProxyMap.containsKey(group)) {
            return 0;
        }

        Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
        if (!loggerBeanWrapperMap.containsKey(funId)) {
            return 0;
        }

        FunLoggerBeanWrapper funLoggerBeanWrapper = loggerBeanWrapperMap.get(funId);
        if (null != logLevelName) {
            funLoggerBeanWrapper.setLogLevel(parseLogLevel(logLevelName));
        }
        if (null != enable) {
            funLoggerBeanWrapper.setLoggerEnable(enable);
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
        WatchLogger watchLogger = null;
        if (method.getDeclaringClass().isAnnotationPresent(WatchLogger.class)) {
            watchLogger = method.getDeclaringClass().getAnnotation(WatchLogger.class);
        }

        if (method.isAnnotationPresent(WatchLogger.class)) {
            watchLogger = method.getAnnotation(WatchLogger.class);
        }

        if (null == watchLogger) {
            return;
        }

        String[] groups = watchLogger.group();
        String logName = watchLogger.value();
        if ("".equals(logName)) {
            logName = generateMethodName(method);
        }

        for (String group : groups) {
            Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
            String funId = EncryptUtil.SHA256(logName);
            if (!loggerBeanWrapperMap.containsKey(funId)) {
                continue;
            }

            FunLoggerBeanWrapper funLoggerBeanWrapper = loggerBeanWrapperMap.get(funId);
            if (!funLoggerBeanWrapper.openLogger()) {
                return;
            }

            HashMap<String, Object> outInfo = new HashMap<>();
            outInfo.put("response", result);
            printLog(funLoggerBeanWrapper, outInfo, method, args);
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
        WatchLogger watchLogger = null;
        if (method.getDeclaringClass().isAnnotationPresent(WatchLogger.class)) {
            watchLogger = method.getDeclaringClass().getAnnotation(WatchLogger.class);
        }

        if (method.isAnnotationPresent(WatchLogger.class)) {
            watchLogger = method.getAnnotation(WatchLogger.class);
        }

        if (null == watchLogger) {
            return;
        }

        String[] groups = watchLogger.group();
        String logName = watchLogger.value();
        if (null == logName || "".equals(logName)) {
            logName = generateMethodName(method);
        }

        for (String group : groups) {
            Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(group);
            String funId = EncryptUtil.SHA256(logName);
            if (!loggerBeanWrapperMap.containsKey(funId)) {
                continue;
            }

            FunLoggerBeanWrapper funLoggerBeanWrapper = loggerBeanWrapperMap.get(funId);
            if (!funLoggerBeanWrapper.openLogger()) {
                return;
            }

            HashMap<String, Object> outInfo = new HashMap<>();
            outInfo.put("throwable", throwable);

            printLog(funLoggerBeanWrapper, outInfo, method, args);
            return;
        }
    }

    private void printLog(FunLoggerBeanWrapper beanWrapper, HashMap<String, Object> outInfo, Method method, Object[] args) {
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

    public static LogLevel parseLogLevel(Integer index) {
        if (null == index) {
            return LogLevel.OFF;
        }
        if (!logLevelIndexMap.containsKey(index)) {
            throw new RuntimeException("不支持下标: " + index);
        }
        return logLevelIndexMap.get(index);
    }

    public static LogLevel parseLogLevel(String logLevel) {
        if (null == logLevel || "".equals(logLevel)) {
            return LogLevel.OFF;
        }
        if (!logLevelNameMap.containsKey(logLevel.trim().toUpperCase())) {
            throw new RuntimeException("不支持name: " + logLevel.trim().toUpperCase());
        }
        return logLevelNameMap.get(logLevel.trim().toUpperCase());
    }

    public String generateMethodName(Method method) {
        String parameters = Arrays.stream(method.getParameterTypes()).map(Class::getCanonicalName).collect(Collectors.joining(","));
        return MessageFormat.format("{0}#{1}({2})", method.getDeclaringClass().getCanonicalName(), method.getName(), parameters);
    }
}
