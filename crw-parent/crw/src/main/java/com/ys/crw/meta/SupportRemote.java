package com.ys.crw.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author oscar.wu
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SupportRemote {
	/**
     * 是否是写中心区域
     *
     * @return
     */
    boolean center() default true;
    
    /**
     * 远程访问超时时间
     * @return
     */
    int timeout() default 2000;
    
}
