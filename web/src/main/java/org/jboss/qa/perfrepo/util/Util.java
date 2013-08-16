package org.jboss.qa.perfrepo.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.qa.perfrepo.model.TestExecutionParameter;

/**
 * 
 * Various utility methods.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class Util {

   /**
    * Parses space separated tags into a list.
    * 
    * @param tags
    * @return List of tags
    */
   public static List<String> parseTags(String tags) {
      if (tags == null) {
         return Collections.emptyList();
      } else {
         String trimmed = tags.trim();
         if ("".equals(trimmed)) {
            return Collections.emptyList();
         } else {
            return Arrays.asList(trimmed.split(" "));
         }
      }
   }

   public static String rawTags(List<String> tags) {
      if (tags == null || tags.isEmpty()) {
         return "";
      }
      StringBuffer s = new StringBuffer(tags.get(0));
      for (int i = 1; i < tags.size(); i++) {
         s.append(" ");
         s.append(tags.get(1));
      }
      return s.toString();
   }

   public static String displayValue(TestExecutionParameter param) {
      if (param == null) {
         return null;
      }
      String value = param.getValue();
      if (value == null) {
         return null;
      }
      if (value.startsWith("http://") || value.startsWith("https://")) {
         if (value.length() > 100) {
            return "<a href=\"" + value + "\">" + value.substring(0, 96) + " ...</a>";
         } else {
            return "<a href=\"" + value + "\">" + value + "</a>";
         }
      } else if (value.length() > 100) {
         return "<a href=\"/repo/param/" + param.getId() + "\">" + value.substring(0, 96) + " ...</a>";
      } else {
         return value;
      }
   }
}
