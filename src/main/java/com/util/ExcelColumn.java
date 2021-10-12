package com.util;
import java.lang.annotation.*;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  22:11
 * @desc: excel的注解，配合excelUtils使用
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {

    String value() default "";

    int col() default 0;
}
