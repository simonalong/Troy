package com.github.simonalong.autologger.autoconfig;

import com.github.simonalong.autologger.annotation.AutoLogger;
import com.github.simonalong.autologger.log.LoggerInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author shizi
 * @since 2021-02-02 23:33:19
 */
@Component
@Slf4j
public class AutoLoggerBeanPostProcessor implements BeanPostProcessor {

    @Override
    @Nullable
    public Object postProcessAfterInitialization(@NonNull Object bean,@NonNull String beanName) throws BeansException {
        return beanWrapper(bean);
    }

    private Object beanWrapper(Object bean) {
        try {
            Object target = bean;
            if (AopUtils.isCglibProxy(bean)) {
                target = getCglibProxyTargetObject(bean);
            }

            AutoLogger classLogger = target.getClass().getAnnotation(AutoLogger.class);
            AutoLogger methodLogger;
            for (Method declaredMethod : target.getClass().getDeclaredMethods()) {
                methodLogger = declaredMethod.getDeclaredAnnotation(AutoLogger.class);
                if (null == methodLogger) {
                    methodLogger = classLogger;
                }

                if (null == methodLogger) {
                    continue;
                }

                if ("".equals(methodLogger.value())) {
                    LoggerInvoker.put(methodLogger.group(), LoggerInvoker.generateMethodName(declaredMethod));
                } else {
                    LoggerInvoker.put(methodLogger.group(), methodLogger.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取cglib代理对象的被代理对象
     */
    private Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }
}
