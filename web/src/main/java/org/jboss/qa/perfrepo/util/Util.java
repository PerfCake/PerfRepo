package org.jboss.qa.perfrepo.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;

/**
 * 
 * Various utility methods.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class Util {

   public static String displayValue(TestExecutionParameter param) {
      if (param == null) {
         return "&nbsp;";
      }
      String value = param.getValue();
      if (value == null) {
         return "&nbsp;";
      }
      if (value.startsWith("http://") || value.startsWith("https://")) {
         if (value.length() > 100) {
            return "<a href=\"" + value + "\">" + value.substring(0, 96) + " ...</a>";
         } else {
            return "<a href=\"" + value + "\">" + value + "</a>";
         }
      } else if (value.length() > 100) {
         return "<a href=\"/repo/param/" + param.getId() + "\">" + StringEscapeUtils.escapeHtml(value.substring(0, 96)) + " ...</a>";
      } else {
         return StringEscapeUtils.escapeHtml(value);
      }
   }
}
