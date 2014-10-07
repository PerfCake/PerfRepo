package org.jboss.qa.perfrepo.model.auth;

import javax.persistence.*;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.report.Report;

import java.util.List;

@javax.persistence.Entity
@Table(name = "permission")
public class Permission implements Entity<Permission>, Comparable<Permission> {

	private static final long serialVersionUID = 5637370080321126750L;
	
	@Id
	@SequenceGenerator(name = "PERMISSION_ID_GENERATOR", sequenceName = "PERMISSION_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISSION_ID_GENERATOR")
	private Long id;

	@Column(name = "access_type")
	@Enumerated(EnumType.STRING)
	private AccessType accessType;

	@Column(name = "access_level")
	@Enumerated(EnumType.STRING)
	private AccessLevel level;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "user_id")
	private Long userId;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "report_id", referencedColumnName = "id")
   private Report report;

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType permission) {
		this.accessType = permission;
	}

	public AccessLevel getLevel() {
		return level;
	}

	public void setLevel(AccessLevel level) {
		this.level = level;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

   public Report getReport() {
      return report;
   }

   public void setReport(Report report) {
      this.report = report;
   }

   @Override
	public int compareTo(Permission o) {
      //TODO: is this really ok?
		return 0;
	}

	@Override
	public Permission clone() {
		try {
			return (Permission) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
