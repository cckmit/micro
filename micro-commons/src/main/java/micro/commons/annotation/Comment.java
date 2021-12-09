package micro.commons.annotation;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Comment {

	String message() default EMPTY;
}
