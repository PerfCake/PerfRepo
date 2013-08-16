package org.jboss.qa.perfrepo.model;

import java.io.Serializable;

/**
 * Interface implemented by perfrepo entities.
 * 
 * {@link Cloneable} with clone method.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public interface Entity<T> extends Cloneable, Serializable {
   /**
    * 
    * @return Entity ID.
    */
   Long getId();

   /**
    * Create shallow copy of entity.
    * 
    * @return The copy
    */
   T clone();
}
