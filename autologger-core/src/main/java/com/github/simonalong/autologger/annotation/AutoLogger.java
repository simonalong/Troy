package com.github.simonalong.autologger.annotation;

import java.lang.annotation.*;

/**
 * @author shizi
 * @since 2021-02-02 23:13:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoLogger {

    /**
     * 分组
     *
     * @return 分组
     */
    String[] group() default "default";

    /**
     * 对日志的名字标示
     * <p>
     *    如果不填，则默认会使用修饰的类的全限定名，和修饰的函数的全限定名
     *
     * @return 名字标示
     */
    String value() default "";
}
