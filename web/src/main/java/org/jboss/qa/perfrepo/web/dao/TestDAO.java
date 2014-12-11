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
package org.jboss.qa.perfrepo.web.dao;

import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.model.userproperty.GroupFilter;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link Test}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
public class TestDAO extends DAO<Test, Long> {

	public Test findByUid(String uid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);

		List<Test> result = findByNamedQuery(Test.FIND_BY_UID, params);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	public List<Test> searchTests(TestSearchTO search, List<String> userGroupNames) {
		CriteriaQuery<Test> criteria = createCriteria();
		Root<Test> root = criteria.from(Test.class);
		criteria.select(root);
		CriteriaBuilder cb = criteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (search.getName() != null && !"".equals(search.getName())) {
			predicates.add(cb.equal(root.get("name"), search.getName()));
		}
		if (search.getUid() != null && !"".equals(search.getUid())) {
			predicates.add(cb.equal(root.get("uid"), search.getUid()));
		}
		if (search.getGroupId() != null && !"".equals(search.getGroupId())) {
			predicates.add(cb.equal(root.get("groupId"), search.getGroupId()));
		}
		if (GroupFilter.MY_GROUPS.equals(search.getGroupFilter())) {
			predicates.add(cb.and(root.get("groupId").in(userGroupNames)));
		}
		if (predicates.size() > 0) {
			criteria.where(predicates.toArray(new Predicate[0]));
		}
		// sorting by name
		criteria.orderBy(cb.asc(root.get("name")));
		return query(criteria).getResultList();
	}

	public Test getTestByRelation(Entity<?> entity) {
		Query q = createNamedQuery(entity.getClass().getSimpleName() + ".getTest");
		q.setParameter("entity", entity);
		return ((Test) q.getSingleResult());
	}

	public List<Test> findByUIDPrefix(String prefix) {
		CriteriaQuery<Test> criteria = createCriteria();
		Root<Test> root = criteria.from(Test.class);
		criteria.select(root);
		CriteriaBuilder cb = criteriaBuilder();
		criteria.where(cb.like(root.<String>get("uid"), prefix + "%"));
		return query(criteria).getResultList();
	}
}