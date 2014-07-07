package org.jboss.qa.perfrepo.model.auth;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.report.Report;

@javax.persistence.Entity
@Table(name = "report_permission")
public class ReportPermission implements Entity<ReportPermission>, Comparable<ReportPermission> {
	
	private static final long serialVersionUID = 419671779913830497L;

	@Id
	@SequenceGenerator(name = "USER_GROUP_ID_GENERATOR", sequenceName = "USER_GROUP_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_GROUP_ID_GENERATOR")
	private Long id;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "report_id", referencedColumnName = "id")
	private Report report;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "permission_id", referencedColumnName = "id")
	private Permission pemission;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Permission getPemission() {
		return pemission;
	}

	public void setPemission(Permission pemission) {
		this.pemission = pemission;
	}

	@Override
	public int compareTo(ReportPermission o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ReportPermission clone() {
		try {
			return (ReportPermission) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
