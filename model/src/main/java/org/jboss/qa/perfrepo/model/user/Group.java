package org.jboss.qa.perfrepo.model.user;

import org.jboss.qa.perfrepo.model.Entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import java.util.Collection;

@javax.persistence.Entity
@Table(name = "\"group\"")
public class Group implements Entity<Group>, Comparable<Group> {

	@Id
	@SequenceGenerator(name = "GROUP_ID_GENERATOR", sequenceName = "GROUP_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_ID_GENERATOR")
	private Long id;

	@Column(name = "name")
	private String name;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "groups")
	private Collection<User> users;

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

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	@Override
	public int compareTo(Group group) {
		return this.name.compareTo(group.name);
	}

	@Override
	public Group clone() {
		try {
			return (Group) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
