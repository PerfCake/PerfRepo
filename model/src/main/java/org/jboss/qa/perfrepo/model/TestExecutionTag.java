/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.model;

import org.jboss.qa.perfrepo.model.auth.EntityType;
import org.jboss.qa.perfrepo.model.auth.SecuredEntity;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@javax.persistence.Entity
@Table(name = "test_execution_tag")
@NamedQueries({@NamedQuery(name = TestExecutionTag.GET_TEST, query = "SELECT test from TestExecutionTag teg inner join teg.testExecution te inner join te.test test WHERE teg = :entity")})
@XmlRootElement(name = "testExecutionTag")
@SecuredEntity(type = EntityType.TEST, parent = "testExecution")
public class TestExecutionTag implements Entity<TestExecutionTag> {

	private static final long serialVersionUID = 1L;

	public static final String GET_TEST = "TestExecutionTag.getTest";

	@Id
	@SequenceGenerator(name = "TEST_EXECUTION_TAG_ID_GENERATOR", sequenceName = "TEST_EXECUTION_TAG_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_TAG_ID_GENERATOR")
	private Long id;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	private Tag tag;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "test_execution_id", referencedColumnName = "id")
	private TestExecution testExecution;

	public TestExecutionTag() {
		super();
	}

	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlAttribute(name = "name")
	public String getTagName() {
		return tag == null ? null : String.valueOf(tag.getName());
	}

	public void setTagName(String name) {
		if (tag == null) {
			tag = new Tag();
		}
		tag.setName(name);
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@XmlTransient
	public Tag getTag() {
		return this.tag;
	}

	public void setTestExecution(TestExecution testExecution) {
		this.testExecution = testExecution;
	}

	@XmlTransient
	public TestExecution getTestExecution() {
		return this.testExecution;
	}

	@Override
	public TestExecutionTag clone() {
		try {
			return (TestExecutionTag) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public TestExecutionTag cloneWithTag() {
		TestExecutionTag clone = clone();
		clone.setTag(tag == null ? null : tag.clone());
		return clone;
	}
}