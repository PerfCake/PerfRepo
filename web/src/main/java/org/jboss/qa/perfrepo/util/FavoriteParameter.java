package org.jboss.qa.perfrepo.util;

import java.io.Serializable;

/**
 * Represents user's favorite parameter for a test.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class FavoriteParameter implements Serializable {
   private long testId;
   private String label;
   private String parameterName;

   public long getTestId() {
      return testId;
   }

   public void setTestId(long testId) {
      this.testId = testId;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getParameterName() {
      return parameterName;
   }

   public void setParameterName(String parameterName) {
      this.parameterName = parameterName;
   }

   public static FavoriteParameter fromString(String value) {
      if (value == null) {
         return null;
      }
      String[] tokens = value.split("\\|");
      if (tokens.length != 3) {
         throw new IllegalArgumentException("bad fav param format");
      }
      FavoriteParameter r = new FavoriteParameter();
      r.testId = Long.valueOf(tokens[0]);
      r.parameterName = tokens[1];
      r.label = tokens[2];
      return r;
   }

   public String toString() {
      return Long.toString(testId) + "|" + parameterName + "|" + label;
   }
}
