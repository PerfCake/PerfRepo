package org.perfrepo.web.model.to;

import java.util.Date;

/**
 * When retrieving values for chart computation, we usually need more info than just a simple double value, e.g. date of
 * the test execution, even ID of the test execution, so we could retrieve more info about it. This object encapsulates
 * all the info needed. It's extracted to extra class, so if needed, the information retrieved could be easily
 * extended.
 * <p>
 * Immutable class.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class SingleValueResultWrapper {

   private final Double value;
   private final Long execId;
   private final Date startedDate;

   public SingleValueResultWrapper(Double value, Long execId, Date startedDate) {
      this.value = value;
      this.execId = execId;
      this.startedDate = new Date(startedDate.getTime());
   }

   public Double getValue() {
      return value;
   }

   public Long getExecId() {
      return execId;
   }

   public Date getStartedDate() {
      return new Date(startedDate.getTime());
   }
}
