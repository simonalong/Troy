package com.github.simonalong.troy.annotation;

import java.lang.annotation.*;

/**
 * @author shizi
 * @since 2021-02-02 23:13:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Watcher {

    /**
     * 分组名，同group
     *
     * @return 分组
     */
    String[] value() default "default";

    /**
     * 分组
     *
     * @return 分组
     */
    String[] group() default "default";
}
