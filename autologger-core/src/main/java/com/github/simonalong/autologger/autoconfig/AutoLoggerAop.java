package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.annotation.AutoLogger;
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
 * @author shizi
 * @since 2021-02-02 23:55:54
 */
@Slf4j
@Aspect
@Component
public class AutoLoggerAop {

    /**
     * 拦截方法中添加注解{@link com.github.simonalong.autologger.annotation.AutoLogger}的类和方法
     */
    @Around("@annotation(com.github.simonalong.autologger.annotation.AutoLogger) || @within(com.github.simonalong.autologger.annotation.AutoLogger)")
    public Object aroundParamFun(ProceedingJoinPoint pjp) throws Throwable {
        Signature sig = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method currentMethod = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        AutoLogger autoLogger = null;
        if (currentMethod.getDeclaringClass().isAnnotationPresent(AutoLogger.class)) {
            autoLogger = currentMethod.getDeclaringClass().getAnnotation(AutoLogger.class);
        }

        if (currentMethod.isAnnotationPresent(AutoLogger.class)) {
            autoLogger = currentMethod.getAnnotation(AutoLogger.class);
        }

        Object result;
        try {
            if (null != autoLogger) {
                String[] groups = autoLogger.group();
                String logName = autoLogger.value();
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
