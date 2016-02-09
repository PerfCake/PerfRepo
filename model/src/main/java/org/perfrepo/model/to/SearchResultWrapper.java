package org.perfrepo.model.to;

import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class SearchResultWrapper<T> {

   private List<T> result;
   private int totalSearchResultsCount;

   public SearchResultWrapper(List<T> result, int totalSearchResultsCount) {
      this.result = result;
      this.totalSearchResultsCount = totalSearchResultsCount;
   }

   public List<T> getResult() {
      return result;
   }

   public int getTotalSearchResultsCount() {
      return totalSearchResultsCount;
   }
}
