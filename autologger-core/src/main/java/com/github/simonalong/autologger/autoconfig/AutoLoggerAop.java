package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.annotation.WatchLogger;
import com.github.simonalong.autologger.log.LoggerInvoker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author robot
 */
@Slf4j
@Aspect
@Component
public class AutoLoggerAop {

    /**
     * 拦截方法中添加注解{@link WatchLogger}的类和方法
     */
    @Around("@annotation(com.github.simonalong.autologger.annotation.WatchLogger) || @within(com.github.simonalong.autologger.annotation.WatchLogger)")
    public Object aroundParamFun1(ProceedingJoinPoint pjp) throws Throwable {
        Signature sig = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;

        Method currentMethod;
        try {
            currentMethod = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw e;
        }

        WatchLogger watchLogger = null;
        if (currentMethod.getDeclaringClass().isAnnotationPresent(WatchLogger.class)) {
            watchLogger = currentMethod.getDeclaringClass().getAnnotation(WatchLogger.class);
        }

        if (currentMethod.isAnnotationPresent(WatchLogger.class)) {
            watchLogger = currentMethod.getAnnotation(WatchLogger.class);
        }

        Object result;
        try {
            if (null != watchLogger) {
                String[] groups = watchLogger.group();
                String logName = watchLogger.value();
                if ("".equals(logName)) {
                    logName = LoggerInvoker.generateMethodName(currentMethod);
                }

                if (LoggerInvoker.enableLogger(groups, logName)) {
                    result = pjp.proceed();
                    LoggerInvoker.postInvoke(currentMethod, pjp.getArgs(), result);
                    return result;
                }
            }
            return pjp.proceed();
        } catch (Throwable e) {
            LoggerInvoker.throwableInvoke(currentMethod, pjp.getArgs(), e);
            throw e;
        }
    }
}
