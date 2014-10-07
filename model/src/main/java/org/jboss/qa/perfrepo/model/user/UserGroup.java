package org.jboss.qa.perfrepo.model.user;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.auth.EntityType;
import org.jboss.qa.perfrepo.model.auth.SecuredEntity;

@javax.persistence.Entity
@Table(name = "user_group")
@SecuredEntity(type=EntityType.USER)
public class UserGroup implements Entity<UserGroup>, Comparable<UserGroup> {
	
	private static final long serialVersionUID = -8000351522778414322L;

	@Id
	@SequenceGenerator(name = "USER_GROUP_ID_GENERATOR", sequenceName = "USER_GROUP_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_GROUP_ID_GENERATOR")
	private Long id;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "group_id", referencedColumnName = "id")
	private Group group;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int compareTo(UserGroup o) {
		return 0;
	}
	
	@Override
	public UserGroup clone() {
		try {
			return (UserGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
