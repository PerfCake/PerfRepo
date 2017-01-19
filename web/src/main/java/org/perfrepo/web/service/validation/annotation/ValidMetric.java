package org.perfrepo.web.service.validation.annotation;

import org.perfrepo.web.service.validation.MetricValidator;
import org.perfrepo.web.service.validation.ValidationType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation marker for {@link org.perfrepo.web.model.Metric}.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Constraint(validatedBy = MetricValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMetric {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ValidationType[] type() default { ValidationType.EXISTS };

}
