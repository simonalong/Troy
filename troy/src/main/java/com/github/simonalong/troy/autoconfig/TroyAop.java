package com.github.simonalong.troy.autoconfig;

import com.github.simonalong.troy.annotation.Watcher;
import com.github.simonalong.troy.log.LoggerInvoker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author robot
 */
@Slf4j
@Aspect
@Component
public class TroyAop {

    /**
     * 拦截方法中添加注解{@link Watcher}的类和方法
     */
    @Around("@annotation(com.github.simonalong.troy.annotation.Watcher) || @within(com.github.simonalong.troy.annotation.Watcher)")
    public Object aroundParamFun1(ProceedingJoinPoint pjp) throws Throwable {
        Signature sig = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;

        Method currentMethod = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        Object result;
        try {
            Set<String> groups = new HashSet<>();
            Watcher watcher = null;
            if (currentMethod.getDeclaringClass().isAnnotationPresent(Watcher.class)) {
                watcher = currentMethod.getDeclaringClass().getAnnotation(Watcher.class);
            }

            if (null != watcher) {
                groups.addAll(Arrays.asList(watcher.group()));
                groups.addAll(Arrays.asList(watcher.value()));
            }

            if (currentMethod.isAnnotationPresent(Watcher.class)) {
                watcher = currentMethod.getAnnotation(Watcher.class);
            }

            if (null != watcher) {
                groups.addAll(Arrays.asList(watcher.group()));
                groups.addAll(Arrays.asList(watcher.value()));
            }

            String logName = LoggerInvoker.generateMethodName(currentMethod);
            if (LoggerInvoker.enableLogger(groups, logName)) {
                result = pjp.proceed();
                LoggerInvoker.postInvoke(currentMethod, pjp.getArgs(), result);
                return result;
            }
            return pjp.proceed();
        } catch (Throwable e) {
            LoggerInvoker.throwableInvoke(currentMethod, pjp.getArgs(), e);
            throw e;
        }
    }
}
