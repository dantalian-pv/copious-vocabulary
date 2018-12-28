package ru.dantalian.copvoc.persist.elastic.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.dantalian.copvoc.persist.elastic.model.codecs.DefaultCodec;
import ru.dantalian.copvoc.persist.elastic.model.codecs.FieldCodec;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Field {

	String name() default "";

	String type() default "keyword";

	boolean index() default true;

	boolean nested() default false;

	Class<? extends FieldCodec> codec() default DefaultCodec.class;

}
