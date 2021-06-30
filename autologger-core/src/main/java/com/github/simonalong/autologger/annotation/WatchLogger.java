package com.github.simonalong.autologger.annotation;

import java.lang.annotation.*;

/**
 * @author shizi
 * @since 2021-02-02 23:13:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WatchLogger {

    /**
     * 分组
     *
     * @return 分组
     */
    String[] group() default "default";
}
