package org.perfrepo.model;

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

/**
 * Represents alert condition defined on test
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "alert")
public class Alert implements Entity<Alert> {

	@Id
	@SequenceGenerator(name = "ALERT_ID_GENERATOR", sequenceName = "ALERT_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALERT_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	@NotNull
	@Size(max = 500)
	private String name;

	@Column(name = "description")
	@NotNull
	@Size(max = 2097)
	private String description;

	@Column(name = "condition")
	@NotNull
	@Size(max = 2097)
	private String condition;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "metric_id", referencedColumnName = "id")
	@NotNull
	private Metric metric;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "test_id", referencedColumnName ="id")
	@NotNull
	private Test test;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	@Override
	public Alert clone() {
		try {
			return (Alert) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Alert{" +
				"id=" + id +
				", condition='" + condition + '\'' +
				", metric=" + metric +
				'}';
	}
}
