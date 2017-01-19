package org.perfrepo.web.service.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.service.validation.annotation.ValidTest;

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

        boolean isValid = true;
        if (test == null) {
            context.buildConstraintViolationWithTemplate("{test.notNull}")
                    .addConstraintViolation();
            return false; // in case entity is null, we cannot check anything else, return immediately
        }

        if (type.contains(ValidationType.ID_NULL) && test.getId() != null) {
            context.buildConstraintViolationWithTemplate("{test.idNotNull}")
                    .addConstraintViolation();
            isValid = false;
        }

        if (type.contains(ValidationType.DUPLICATE_CHECK)) {
            Test possibleDuplicate = testDAO.findByUid(test.getUid());
            if (possibleDuplicate != null && !possibleDuplicate.getId().equals(test.getId())) {
                context.buildConstraintViolationWithTemplate("{test.duplicateUid}")
                        .addPropertyNode("uid")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (type.contains(ValidationType.EXISTS) && (test.getId() == null || testDAO.get(test.getId()) == null)) {
            context.buildConstraintViolationWithTemplate("{;}")
                    .addConstraintViolation();
            isValid =  false;
        }

        if (type.contains(ValidationType.SEMANTIC_CHECK)) {
            Set<ConstraintViolation<Test>> violations = validator.validate(test);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<Test> violation : violations) {
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
