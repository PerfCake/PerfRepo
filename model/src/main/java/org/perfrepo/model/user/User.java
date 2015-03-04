/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.model.user;

import org.perfrepo.model.Entity;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.UserProperty;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Collection;

@javax.persistence.Entity
@Table(name = "\"user\"")
@NamedQueries({
    @NamedQuery(name = User.GET_SUBSCRIBERS_FOR_TEST, query = "SELECT distinct user from User user join user.subscribedTests test where test.id = :testId")
})
public class User implements Entity<User>, Comparable<User> {

	private static final long serialVersionUID = 4616015836066622075L;

   public static final String GET_SUBSCRIBERS_FOR_TEST = "User.getSubscribersForTest";

	@Id
	@SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "USER_SEQUENCE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
	private Long id;

	@Column(name = "username")
	@NotNull
	@Size(max = 2047)
	private String username;

	@Column(name = "password")
	@NotNull
	@Size(max = 300)
	private String password;

	@Column(name = "first_name")
	@NotNull
	@Size(max = 2047)
	private String firstName;

	@Column(name = "last_name")
	@NotNull
	@Size(max = 2047)
	private String lastName;

	@Column(name = "email")
	@NotNull
	@Email
	@Size(max = 2047)
	private String email;

	@OneToMany(mappedBy = "user")
	private Collection<UserProperty> properties;

	@OneToMany(mappedBy = "user")
	private Collection<FavoriteParameter> favoriteParameters;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_group",
			joinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)},
			inverseJoinColumns = {@JoinColumn(name = "group_id", nullable = false, updatable = false)}
	)
	private Collection<Group> groups;

	@ManyToMany(mappedBy = "subscribers")
	private Collection<Test> subscribedTests;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<UserProperty> getProperties() {
		return properties;
	}

	public void setProperties(Collection<UserProperty> properties) {
		this.properties = properties;
	}

	public Collection<FavoriteParameter> getFavoriteParameters() {
		return favoriteParameters;
	}

	public void setFavoriteParameters(Collection<FavoriteParameter> favoriteParameters) {
		this.favoriteParameters = favoriteParameters;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public Collection<Test> getSubscribedTests() {
		return subscribedTests;
	}

	public void setSubscribedTests(Collection<Test> subscribedTests) {
		this.subscribedTests = subscribedTests;
	}

	@Override
	public int compareTo(User o) {
		return this.username.compareTo(o.username);
	}

	@Override
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}