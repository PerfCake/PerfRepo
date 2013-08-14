package org.jboss.qa.perfrepo.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
