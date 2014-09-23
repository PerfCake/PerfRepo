package org.jboss.qa.perfrepo.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TagUtils {
	
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
	         s.append(tags.get(i));
	      }
	      return s.toString();
	   }

}
