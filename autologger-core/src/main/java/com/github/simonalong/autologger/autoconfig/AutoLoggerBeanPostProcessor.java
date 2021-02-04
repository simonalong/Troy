package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.annotation.AutoLogger;
import com.github.simonalong.autologger.log.LoggerInvoker;
import com.github.simonalong.autologger.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author jfWu
 * @version 1.0
 * @date 2020/9/24 20:58
 */
@Component
@Slf4j
public class AutoLoggerBeanPostProcessor implements BeanPostProcessor {

    @Override
    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return beanWrapper(bean);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object beanWrapper(Object bean) {
        AutoLogger autoLogger;
        Map<String, Pair<String[], String>> methodAutoLoggerMap = new HashMap<>();
        for (Method declaredMethod : bean.getClass().getDeclaredMethods()) {
            autoLogger = declaredMethod.getDeclaredAnnotation(AutoLogger.class);
            if (null == autoLogger) {
                autoLogger = bean.getClass().getDeclaredAnnotation(AutoLogger.class);
                if (null == autoLogger) {
                    continue;
                }
            }

            if("".equals(autoLogger.value())) {
                methodAutoLoggerMap.putIfAbsent(declaredMethod.toGenericString(), new Pair(autoLogger.group(), LoggerInvoker.generateMethodName(declaredMethod)));
            } else {
                methodAutoLoggerMap.putIfAbsent(declaredMethod.toGenericString(), new Pair(autoLogger.group(), autoLogger.value()));
            }
        }

        if (!methodAutoLoggerMap.isEmpty()) {
            Object wrapperBean = Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new InvokeFactory(bean, methodAutoLoggerMap));
            methodAutoLoggerMap.forEach((k, v) -> LoggerInvoker.put(v.getKey(), v.getValue(), wrapperBean));
        }
        return bean;
    }

    static class InvokeFactory implements InvocationHandler {

        private final Object target;
        private final Map<String, Pair<String[], String>> methodAutoLoggerMap;

        public InvokeFactory(Object target, Map<String, Pair<String[], String>> methodAutoLoggerMap) {
            this.target = target;
            this.methodAutoLoggerMap = methodAutoLoggerMap;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodAutoLoggerMap.containsKey(method.toGenericString())) {
                try {
                    preInvoke();
                    Object result = Proxy.getInvocationHandler(target).invoke(proxy, method, args);
                    postInvoke(method, args, result);
                    return result;
                } catch (Throwable e) {
                    throwableInvoke(method, args, e);
                    throw e;
                }
            }
            return Proxy.getInvocationHandler(target).invoke(proxy, method, args);
        }

        private void preInvoke() {
            LoggerInvoker.preInvoke();
        }

        private void postInvoke(Method method, Object[] args, Object result) {
            LoggerInvoker.postInvoke(method, args, result);
        }

        private void throwableInvoke(Method method, Object[] args, Throwable throwable) {
            LoggerInvoker.throwableInvoke(method, args, throwable);
        }
    }
}
