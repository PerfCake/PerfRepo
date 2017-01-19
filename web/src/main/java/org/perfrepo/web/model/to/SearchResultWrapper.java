package org.perfrepo.web.model.to;

import java.util.List;

/**
 * This is a wrapper class for retrieving search results from forms. Usually we need more info
 * than just simply the result (e.g. total number of entities matched by the search query to compute pagination).
 * This class encapsulates all such data, so it could be easily extended if needed.
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
