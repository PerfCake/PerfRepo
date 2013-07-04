package org.jboss.qa.perfrepo.model;

/**
 * {@link Cloneable} with clone method.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public interface CloneableEntity<T> extends Cloneable {
   /**
    * Create shallow copy of entity.
    * 
    * @return The copy
    */
   T clone();
}
