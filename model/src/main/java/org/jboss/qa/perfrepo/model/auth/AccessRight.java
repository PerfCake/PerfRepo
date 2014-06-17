package org.jboss.qa.perfrepo.model.auth;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.User;

public class AccessRight implements Entity<AccessRight>, Comparable<AccessRight> {

	private static final long serialVersionUID = 5637370080321126750L;
	private Permission permission;
	private Level level;
	private User user;
	private Group group;
	
	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

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

	@Override
	public int compareTo(AccessRight o) {
		return 0;
	}

	@Override
	public AccessRight clone() {
		try {
			return (AccessRight) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
