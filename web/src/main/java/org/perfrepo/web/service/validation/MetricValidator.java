package org.perfrepo.web.service.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.service.validation.annotation.ValidMetric;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for {@link org.perfrepo.web.model.Test} entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class MetricValidator implements ConstraintValidator<ValidMetric, Metric> {

    private Set<ValidationType> type;

    @Inject
    private Validator validator;

    @Inject
    private MetricDAO metricDAO;

    @Override
    public void initialize(ValidMetric constraintAnnotation) {
        type = new HashSet<>(Arrays.asList(constraintAnnotation.type()));
    }

    @Override
    public boolean isValid(Metric metric, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        boolean isValid = true;
        if (metric == null) {
            context.buildConstraintViolationWithTemplate("{metric.notNull}")
                    .addConstraintViolation();
            return false; // in case entity is null, we cannot check anything else, return immediately
        }

        if (type.contains(ValidationType.ID_NULL) && metric.getId() != null) {
            context.buildConstraintViolationWithTemplate("{metric.idNotNull}")
                    .addConstraintViolation();
            isValid = false;
        }

        if (type.contains(ValidationType.DUPLICATE_CHECK)) {
            Metric possibleDuplicate = metricDAO.getByName(metric.getName());
            if (possibleDuplicate != null && !possibleDuplicate.getId().equals(metric.getId())) {
                context.buildConstraintViolationWithTemplate("{metric.duplicateName}")
                        .addPropertyNode("name")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (type.contains(ValidationType.EXISTS) && (metric.getId() == null || metricDAO.get(metric.getId()) == null)) {
            context.buildConstraintViolationWithTemplate("{metric.doesntExist}")
                    .addConstraintViolation();
            isValid =  false;
        }

        if (type.contains(ValidationType.SEMANTIC_CHECK)) {
            Set<ConstraintViolation<Metric>> violations = validator.validate(metric);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<Metric> violation : violations) {
                    context.buildConstraintViolationWithTemplate(violation.getMessageTemplate())
                            .addPropertyNode(((PathImpl) violation.getPropertyPath()).getLeafNode().getName())
                            .addConstraintViolation();
                }

                isValid =  false;
            }
        }

        return isValid;
    }
}
