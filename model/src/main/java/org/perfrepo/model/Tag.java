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
package org.perfrepo.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "tag")
@XmlRootElement(name = "tag")
public class Tag implements Entity<Tag>, Comparable<Tag> {

	private static final long serialVersionUID = -5239043908577304531L;

	public static final String FIND_BY_PREFIX = "Tag.findByPrefix";

	@Id
	@SequenceGenerator(name = "TAG_ID_GENERATOR", sequenceName = "TAG_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	private String name;

	@OneToMany(mappedBy = "tag")
	private Collection<TestExecutionTag> testExecutionTags;

	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlID
	@XmlAttribute(name = "id")
	public String getStringId() {
		return id == null ? null : String.valueOf(id);
	}

	public void setStringId(String id) {
		this.id = Long.valueOf(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return this.name;
	}

	public void setTestExecutionTags(Collection<TestExecutionTag> testExecutionTags) {
		this.testExecutionTags = testExecutionTags;
	}

	@XmlTransient
	public Collection<TestExecutionTag> getTestExecutionTags() {
		return this.testExecutionTags;
	}

   @Override
	public int compareTo(Tag o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public Tag clone() {
		try {
			return (Tag) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}