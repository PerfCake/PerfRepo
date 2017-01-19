/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.util;

import org.perfrepo.web.model.Tag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagUtils {

   private TagUtils() { }

   /**
    * Parses space separated tags into a list.
    *
    * @param tags
    * @return List of tags
    */
   public static Set<Tag> parseTags(String tags) {
      if (tags == null) {
         return Collections.emptySet();
      } else {
         String trimmed = tags.trim();
         if ("".equals(trimmed)) {
            return Collections.emptySet();
         } else {
            List<String> tagsStrings = Arrays.asList(trimmed.split(" "));
            Set<Tag> tagsObjects = tagsStrings.stream()
                    .map(tagString -> {
                       Tag tag = new Tag();
                       tag.setName(tagString);
                       return tag;
                    })
                    .collect(Collectors.toSet());
            return tagsObjects;
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
