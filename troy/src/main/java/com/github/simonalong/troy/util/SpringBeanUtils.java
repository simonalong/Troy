package com.github.simonalong.troy.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 下午12:14
 */
public class SpringBeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name){
        if (null != applicationContext) {
            return applicationContext.getBean(name);
        }
        return null;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (null != applicationContext) {
            return applicationContext.getBean(clazz);
        }
        return null;
    }

    public static String[] getAllBean() {
        if (null != applicationContext) {
            return applicationContext.getBeanDefinitionNames();
        }
        return null;
    }

    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }
}
