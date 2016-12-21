package org.perfrepo.web.service.validation;

/**
 * Defines type of validation that should be performed on provided entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public enum ValidationType {

    SEMANTIC_CHECK,

    DUPLICATE_CHECK,

    ID_NULL,

    EXISTS;
}
