package org.lwjgl.util.mapped;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MappedType {
   int padding() default 0;

   boolean cacheLinePadding() default false;

   int align() default 4;

   boolean autoGenerateOffsets() default true;
}
