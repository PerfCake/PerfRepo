/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.util;

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
