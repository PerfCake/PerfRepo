package org.perfrepo.web.service.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.service.validation.annotation.ValidUser;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for {@link org.perfrepo.web.model.user.User} entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UserValidator implements ConstraintValidator<ValidUser, User> {

    private Set<ValidationType> type;

    @Inject
    private Validator validator;

    @Inject
    private UserDAO userDAO;

    @Override
    public void initialize(ValidUser constraintAnnotation) {
        type = new HashSet<>(Arrays.asList(constraintAnnotation.type()));
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        boolean isValid = true;
        if (user == null) {
            context.buildConstraintViolationWithTemplate("{user.notNull}")
                    .addConstraintViolation();
            return false; // in case entity is null, we cannot check anything else, return immediately
        }

        if (type.contains(ValidationType.ID_NULL) && user.getId() != null) {
            context.buildConstraintViolationWithTemplate("{user.idNotNull}")
                    .addConstraintViolation();
            isValid = false;
        }

        if (type.contains(ValidationType.DUPLICATE_CHECK)) {
            User possibleDuplicate = userDAO.findByUsername(user.getUsername());
            if (possibleDuplicate != null && !possibleDuplicate.getId().equals(user.getId())) {
                context.buildConstraintViolationWithTemplate("{user.duplicateUsername}")
                        .addPropertyNode("username")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (type.contains(ValidationType.EXISTS) && (user.getId() == null || userDAO.get(user.getId()) == null)) {
            context.buildConstraintViolationWithTemplate("{user.doesntExist}")
                    .addConstraintViolation();
            isValid =  false;
        }

        if (type.contains(ValidationType.SEMANTIC_CHECK)) {
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
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
