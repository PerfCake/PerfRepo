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
package org.perfrepo.model;

import org.perfrepo.model.auth.EntityType;
import org.perfrepo.model.auth.SecuredEntity;
import org.perfrepo.model.builder.TestExecutionBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents one execution of a test.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "test_execution")
@SecuredEntity(type = EntityType.TEST, parent = "test")
@NamedQueries({@NamedQuery(name = TestExecution.GET_TEST, query = "SELECT te.test from TestExecution te inner join te.test where te= :entity")})
@XmlRootElement(name = "testExecution")
public class TestExecution implements Entity<TestExecution> {

	private static final long serialVersionUID = -2956845045583534606L;

	public static final String GET_TEST = "TestExecution.getTest";

	@Id
	@SequenceGenerator(name = "TEST_EXECUTION_ID_GENERATOR", sequenceName = "TEST_EXECUTION_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	@NotNull
	@Size(max = 2047)
	private String name;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "test_id", referencedColumnName = "id")
	private Test test;

	@OneToMany(mappedBy = "testExecution")
	private Collection<TestExecutionParameter> parameters;

	@OneToMany(mappedBy = "testExecution")
	private Collection<TestExecutionTag> testExecutionTags;

	@OneToMany(mappedBy = "testExecution")
	private Collection<Value> values;

	@OneToMany(mappedBy = "testExecution")
	private Collection<TestExecutionAttachment> attachments;

	@NotNull
	@Column(name = "started")
	private Date started;

	@Column(name = "comment")
	@Size(max = 10239)
	private String comment;

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

	public void setTest(Test test) {
		this.test = test;
	}

	/**
	 * this atttribute is {@link XmlTransient} because we don't want to return {@link Test}, which
	 * would need to include {@link Metric} objects. The test is determined from xml attribute testId
	 * which is supplied by {@link TestExecution#getTestId()} method.
	 */
	@XmlTransient
	public Test getTest() {
		return this.test;
	}

	@XmlAttribute(name = "testId")
	public String getTestId() {
		return test == null ? null : (test.getId() == null ? null : test.getId().toString());
	}

	public void setTestId(String id) {
		if (test == null) {
			test = new Test();
		}
		test.setId(Long.valueOf(id));
	}

	@XmlAttribute(name = "testUid")
	public String getTestUid() {
		return test == null ? null : test.getUid();
	}

	public void setTestUid(String uid) {
		if (test == null) {
			test = new Test();
		}
		test.setUid(uid);
	}

	public void setParameters(Collection<TestExecutionParameter> testExecutionParameters) {
		this.parameters = testExecutionParameters;
	}

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	public Collection<TestExecutionParameter> getParameters() {
		return this.parameters;
	}

	public void setTestExecutionTags(Collection<TestExecutionTag> testExecutionTags) {
		this.testExecutionTags = testExecutionTags;
	}

	@XmlElementWrapper(name = "tags")
	@XmlElement(name = "tag")
	public Collection<TestExecutionTag> getTestExecutionTags() {
		return this.testExecutionTags;
	}

	public void setValues(Collection<Value> values) {
		this.values = values;
	}

	@XmlElementWrapper(name = "values")
	@XmlElement(name = "value")
	public Collection<Value> getValues() {
		return this.values;
	}

	@XmlAttribute(name = "started")
	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	@XmlElementWrapper(name = "attachments")
	@XmlElement(name = "attachment")
	public Collection<TestExecutionAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<TestExecutionAttachment> attachments) {
		this.attachments = attachments;
	}

	@XmlTransient
	public List<String> getSortedTags() {
		List<String> result = getTags();
		Collections.sort(result);
		return result;
	}

	@XmlTransient
	public List<String> getTags() {
		if (testExecutionTags == null || testExecutionTags.isEmpty()) {
			return new ArrayList<String>(0);
		} else {
			List<String> result = new ArrayList<String>();
			for (TestExecutionTag tet : testExecutionTags) {
				result.add(tet.getTag().getName());
			}
			return result;
		}
	}

	@XmlTransient
	public List<TestExecutionParameter> getSortedParameters() {
		if (parameters == null || parameters.isEmpty()) {
			return new ArrayList<TestExecutionParameter>(0);
		} else {
			List<TestExecutionParameter> result = new ArrayList<TestExecutionParameter>(parameters);
			Collections.sort(result);
			return result;
		}
	}

	@XmlTransient
	public Map<String, String> getParametersAsMap() {
		if (parameters == null || parameters.isEmpty()) {
			return new HashMap<String, String>(0);
		} else {
			Map<String, String> r = new HashMap<String, String>();
			for (TestExecutionParameter p : parameters) {
				r.put(p.getName(), p.getValue());
			}
			return r;
		}
	}

	@Deprecated
	//should be moved to util class
	public TestExecutionParameter findParameter(String name) {
		if (parameters == null || parameters.isEmpty()) {
			return null;
		}
		for (TestExecutionParameter param : parameters) {
			if (name.equals(param.getName())) {
				return param;
			}
		}
		return null;
	}

	public static TestExecutionBuilder builder() {
		return new TestExecutionBuilder();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((started == null) ? 0 : started.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestExecution other = (TestExecution) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (started == null) {
			if (other.started != null)
				return false;
		} else if (!started.equals(other.started))
			return false;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		return true;
	}

	@Override
	public TestExecution clone() {
		try {
			return (TestExecution) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
