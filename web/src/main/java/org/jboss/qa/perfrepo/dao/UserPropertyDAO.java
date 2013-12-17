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
package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;

/**
 * DAO for {@link UserProperty}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class UserPropertyDAO extends DAO<UserProperty, Long> {

   public List<UserProperty> findByUserId(Long userId) {
      return findAllByProperty("user", userId);
   }

   public UserProperty findByUserIdAndName(Long userId, String name) {
      CriteriaQuery<UserProperty> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      Root<UserProperty> rUserProperty = criteria.from(UserProperty.class);
      Join<UserProperty, User> rUser = rUserProperty.join("user");

      Predicate pUser = cb.equal(rUser.get("id"), cb.parameter(Long.class, "userId"));
      Predicate pName = cb.equal(rUserProperty.get("name"), cb.parameter(String.class, "name"));
      criteria.select(rUserProperty);
      criteria.where(cb.and(pUser, pName));

      TypedQuery<UserProperty> query = query(criteria);
      query.setParameter("userId", userId);
      query.setParameter("name", name);

      List<UserProperty> props = query.getResultList();
      if (props.isEmpty()) {
         return null;
      } else {
         return props.get(0);
      }
   }

}