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

import org.jboss.qa.perfrepo.model.Entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@javax.persistence.Entity
@Table(name = "report_property")
public class ReportProperty implements Entity<ReportProperty>, Comparable<ReportProperty> {

	private static final long serialVersionUID = -2862333826616822888L;

	@Id
	@SequenceGenerator(name = "REPORT_PROPERTY_ID_GENERATOR", sequenceName = "REPORT_PROPERTY_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_PROPERTY_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	@NotNull
	@Size(max = 2047)
	private String name;

	@Column(name = "value")
	@NotNull
	@Size(max = 2047)
	private String value;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "report_id", referencedColumnName = "id")
	private Report report;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@Override
	public int compareTo(ReportProperty o) {
		return getName().compareTo(o.name);
	}

	@Override
	public ReportProperty clone() {
		try {
			return (ReportProperty) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}