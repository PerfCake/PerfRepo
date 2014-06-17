package org.jboss.qa.perfrepo.model.auth;

import java.util.List;

import org.jboss.qa.perfrepo.model.User;

public class Group {
	
	private String name;
	
	private List<User> users;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}	

}
