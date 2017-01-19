package org.perfrepo.web.model.to;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a wrapper for retrieved results from DAO layer, because sometimes we need more information than
 * just result value (e.g. date, test execution ID, etc.). This class provides a wrapper so it could be easily extended
 * the amount of retrieved information.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class MultiValueResultWrapper {

   private Map<String, Map<String, Double>> values = new HashMap<>();
   private Long execId;
   private Object indexObject;

   public MultiValueResultWrapper(Long execId, Object indexObject) {
      this.execId = execId;
      this.indexObject = indexObject;
   }

   public void addValue(String parameterName, String parameterValue, Double resultValue) {
      values.putIfAbsent(parameterName, new HashMap<>());
      values.get(parameterName).put(parameterValue, resultValue);
   }

   public Map<String, Map<String, Double>> getValues() {
      return values;
   }

   public Long getExecId() {
      return execId;
   }

   public Object getIndexObject() {
      return indexObject;
   }
}
