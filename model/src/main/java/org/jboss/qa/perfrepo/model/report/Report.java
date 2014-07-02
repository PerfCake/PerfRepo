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
package org.jboss.qa.perfrepo.model.report;

import java.util.Collection;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.User;

/**
 * Represents a report entity.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "report")
@XmlRootElement(name = "report")
@NamedQueries({
              @NamedQuery(name = Report.FIND_BY_USERNAME, query = "SELECT report from Report report join report.user user where user.username = :username"),
              @NamedQuery(name = Report.FIND_MAX_ID, query = "SELECT max(report.id) from Report report")
              })
public class Report implements Entity<Report>, Comparable<Report> {

	private static final long serialVersionUID = -2188625358440509257L;

   public static final String FIND_BY_USERNAME = "Report.findByUserName";
   public static final String FIND_MAX_ID = "Report.findMaxId";

	@Id
	@SequenceGenerator(name = "REPORT_ID_GENERATOR", sequenceName = "REPORT_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	@NotNull
	@Size(max = 255)
	private String name;

	@Column(name = "type")
	@NotNull
	@Size(max = 255)
	private String type;

	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@NotNull
	private User user;
	
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
   @MapKey(name = "name")
	private Map<String, ReportProperty> properties;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

   public Map<String, ReportProperty> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, ReportProperty> properties) {
      this.properties = properties;
   }

   @Override
	public Report clone() {
		try {
			return (Report) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int compareTo(Report o) {
		return this.getName().compareTo(o.getName());
	}
}