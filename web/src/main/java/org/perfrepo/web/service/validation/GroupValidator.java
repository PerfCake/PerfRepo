package org.perfrepo.web.service.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.service.validation.annotation.ValidGroup;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for {@link org.perfrepo.web.model.user.Group} entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class GroupValidator implements ConstraintValidator<ValidGroup, Group> {

    private Set<ValidationType> type;

    @Inject
    private Validator validator;

    @Inject
    private GroupDAO groupDAO;

    @Override
    public void initialize(ValidGroup constraintAnnotation) {
        type = new HashSet<>(Arrays.asList(constraintAnnotation.type()));
    }

    @Override
    public boolean isValid(Group group, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        boolean isValid = true;
        if (group == null) {
            context.buildConstraintViolationWithTemplate("{group.notNull}")
                    .addConstraintViolation();
            return false; // in case entity is null, we cannot check anything else, return immediately
        }

        if (type.contains(ValidationType.ID_NULL) && group.getId() != null) {
            context.buildConstraintViolationWithTemplate("{group.idNotNull}")
                    .addConstraintViolation();
            isValid = false;
        }

        if (type.contains(ValidationType.DUPLICATE_CHECK)) {
            Group possibleDuplicate = groupDAO.findByName(group.getName());
            if (possibleDuplicate != null && !possibleDuplicate.getId().equals(group.getId())) {
                context.buildConstraintViolationWithTemplate("{group.duplicateNname}")
                        .addPropertyNode("name")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (type.contains(ValidationType.EXISTS) && (group.getId() == null || groupDAO.get(group.getId()) == null)) {
            context.buildConstraintViolationWithTemplate("{group.doesntExist}")
                    .addConstraintViolation();
            isValid =  false;
        }

        if (type.contains(ValidationType.SEMANTIC_CHECK)) {
            Set<ConstraintViolation<Group>> violations = validator.validate(group);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<Group> violation : violations) {
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
