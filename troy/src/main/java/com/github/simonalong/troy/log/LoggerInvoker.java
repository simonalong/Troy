package com.github.simonalong.troy.log;

import com.alibaba.fastjson.JSON;
import com.github.simonalong.troy.annotation.Watcher;
import com.github.simonalong.troy.util.EncryptUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.simonalong.troy.TroyConstants.DEFAULT_GROUP;

/**
 * 日志回调处理器
 *
 * @author shizi
 * @since 2021-02-04 18:19:10
 */
@Slf4j
@UtilityClass
public class LoggerInvoker {

    private final String LOG_PRE = "[troy] ";
    /**
     * 日志映射管理map，key为group, value为map： key为logName实体（如果logName为空，则为函数的类全限定名加上#，再加上函数名）, value为bean的日志包装类
     */
    @Getter
    private final Map<String, Map<String, FunLoggerBeanWrapper>> loggerProxyMap = new HashMap<>();
    private final Map<String, LogLevel> logLevelNameMap;

    static {
        logLevelNameMap = Arrays.stream(LogLevel.values()).collect(Collectors.toMap(LogLevel::name, e -> e));
    }

    public void put(String[] groups, String className, String logFunName) {
        String hashKey = EncryptUtil.SHA256(logFunName);
        FunLoggerBeanWrapper beanWrapper = new FunLoggerBeanWrapper(className, logFunName);

        List<String> groupList = new ArrayList<>(Arrays.asList(groups));
        groupList.add(DEFAULT_GROUP);
        groupList.forEach(group -> loggerProxyMap.compute(group, (k, v) -> {
            if (null == v) {
                Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = new HashMap<>();
                loggerBeanWrapperMap.put(hashKey, beanWrapper);
                return loggerBeanWrapperMap;
            } else {
                v.putIfAbsent(hashKey, beanWrapper);
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

    public LoggerBeanWrapperRsp getLoggerInfo(String logFunId) {
        FunLoggerBeanWrapper funLoggerBeanWrapper = null;
        for (Map.Entry<String, Map<String, FunLoggerBeanWrapper>> entry : loggerProxyMap.entrySet()) {
            Map<String, FunLoggerBeanWrapper> beanWrapperMap = entry.getValue();
            if (beanWrapperMap.containsKey(logFunId)) {
                funLoggerBeanWrapper = beanWrapperMap.get(logFunId);
                break;
            }
        }

        if (null == funLoggerBeanWrapper) {
            return null;
        }

        LoggerBeanWrapperRsp wrapperRsp = new LoggerBeanWrapperRsp();
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
     * @param funId     日志函数标示
     * @param logLevelName 日志级别
     * @param enable       日志激活标示
     * @return 更新结果标示：0-没有更新成功，1-更新成功
     */
    public Integer updateLoggerBeanLogOfFunId(String funId, String logLevelName, Boolean enable) {
        if (null == funId) {
            return 0;
        }

        Map<String, FunLoggerBeanWrapper> loggerBeanWrapperMap = loggerProxyMap.get(DEFAULT_GROUP);
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
        Watcher watcher = null;
        if (method.getDeclaringClass().isAnnotationPresent(Watcher.class)) {
            watcher = method.getDeclaringClass().getAnnotation(Watcher.class);
        }

        if (method.isAnnotationPresent(Watcher.class)) {
            watcher = method.getAnnotation(Watcher.class);
        }

        if (null == watcher) {
            return;
        }

        String[] groups = watcher.group();
        String logName = generateMethodName(method);

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

            TreeMap<String, Object> outInfo = new TreeMap<>();
            outInfo.put("response", result);
            printLog(group, funLoggerBeanWrapper, outInfo, method, args);
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
    public void throwableInvoke(Method method, Object[] args, Throwable throwable) {
        Watcher watcher = null;
        if (method.getDeclaringClass().isAnnotationPresent(Watcher.class)) {
            watcher = method.getDeclaringClass().getAnnotation(Watcher.class);
        }

        if (method.isAnnotationPresent(Watcher.class)) {
            watcher = method.getAnnotation(Watcher.class);
        }

        if (null == watcher) {
            return;
        }

        String[] groups = watcher.group();
        String logName = generateMethodName(method);

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

            TreeMap<String, Object> outInfo = new TreeMap<>();
            outInfo.put("throwable", throwable);

            printLog(group, funLoggerBeanWrapper, outInfo, method, args);
            return;
        }
    }

    private void printLog(String group, FunLoggerBeanWrapper beanWrapper, TreeMap<String, Object> outInfo, Method method, Object[] args) {
        outInfo.put("group", group);
        outInfo.put("fun", method.toString());
        outInfo.put("parameters", Arrays.asList(args));

        if (!beanWrapper.openLogger()) {
            return;
        }
        LogLevel logLevel = beanWrapper.getLogLevel();
        switch (logLevel) {
            case OFF:
                return;
            case DEBUG:
                log.debug(LOG_PRE + "result：" + JSON.toJSONString(outInfo));
                break;
            case TRACE:
                log.trace(LOG_PRE + "result：" + JSON.toJSONString(outInfo));
                break;
            case INFO:
                log.info(LOG_PRE + "result：" + JSON.toJSONString(outInfo));
                break;
            case WARN:
                log.warn(LOG_PRE + "result：" + JSON.toJSONString(outInfo));
                break;
            case ERROR:
            case FATAL:
                log.error(LOG_PRE + "result：" + JSON.toJSONString(outInfo));
                break;
            default:
                break;
        }
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
