package com.github.simonalong.troy.autoconfig;

import com.alibaba.fastjson.parser.ParserConfig;
import com.github.simonalong.troy.annotation.Watcher;
import com.github.simonalong.troy.log.LoggerInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author shizi
 * @version 1.0
 */
@Component
@Slf4j
public class TroyBeanPostProcessor implements BeanPostProcessor {

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Override
    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return beanWrapper(bean);
    }

    private Object beanWrapper(Object bean) {
        try {
            Object target = bean;
            if (AopUtils.isCglibProxy(bean)) {
                target = getCglibProxyTargetObject(bean);
            }

            Watcher classLogger = target.getClass().getAnnotation(Watcher.class);
            Watcher methodLogger;
            for (Method declaredMethod : target.getClass().getDeclaredMethods()) {
                methodLogger = declaredMethod.getDeclaredAnnotation(Watcher.class);
                if (null == methodLogger) {
                    methodLogger = classLogger;
                }

                if (null == methodLogger) {
                    continue;
                }

                LoggerInvoker.put(methodLogger.group(), declaredMethod.getDeclaringClass().getCanonicalName(), LoggerInvoker.generateMethodName(declaredMethod));
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
