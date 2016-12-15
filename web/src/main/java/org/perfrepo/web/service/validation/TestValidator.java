package org.perfrepo.web.service.validation;

import org.perfrepo.model.Test;
import org.perfrepo.web.dao.TestDAO;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for {@link Test} entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestValidator implements ConstraintValidator<ValidTest, Test> {

    private Set<ValidationType> type;

    @Inject
    private Validator validator;

    @Inject
    private TestDAO testDAO;

    @Override
    public void initialize(ValidTest constraintAnnotation) {
        type = new HashSet<>(Arrays.asList(constraintAnnotation.type()));
    }

    @Override
    public boolean isValid(Test test, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (test == null) {
            context.buildConstraintViolationWithTemplate("{test.notNull}")
                    .addConstraintViolation();
            return false;
        }

        if (type.contains(ValidationType.ID_NULL) && test.getId() != null) {
            context.buildConstraintViolationWithTemplate("{test.idNotNull}")
                    .addConstraintViolation();
            return false;
        }

        if (type.contains(ValidationType.EXISTS) && (test.getId() == null || testDAO.get(test.getId()) == null)) {
            context.buildConstraintViolationWithTemplate("{test.doesntExist}")
                    .addConstraintViolation();
            return false;
        }

        if (type.contains(ValidationType.SEMANTIC_CHECK)) {
            Set<ConstraintViolation<Test>> violations = validator.validate(test);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<Test> violation : violations) {
                    context.buildConstraintViolationWithTemplate(violation.getMessageTemplate())
                            .addConstraintViolation();
                }

                return false;
            }
        }

        return true;
    }
}
