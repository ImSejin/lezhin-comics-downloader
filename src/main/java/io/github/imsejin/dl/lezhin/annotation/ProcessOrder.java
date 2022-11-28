package io.github.imsejin.dl.lezhin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessOrder {

    int HIGHEST_ORDER = 0;

    int LOWEST_ORDER = Integer.MAX_VALUE;

    int value();

}
