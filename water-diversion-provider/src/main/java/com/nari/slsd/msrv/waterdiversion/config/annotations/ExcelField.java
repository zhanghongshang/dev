package com.nari.slsd.msrv.waterdiversion.config.annotations;


import com.nari.slsd.msrv.waterdiversion.processer.CommonConverter;
import com.nari.slsd.msrv.waterdiversion.processer.interfaces.Converter;

import java.lang.annotation.*;

/**
 * @author 86180
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelField {
    boolean ignore() default false;
    Class<? extends Converter> converter() default CommonConverter.class;
}
