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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.CloneableEntity;

/**
 * Common ancestor for DAO objects
 * 
 * @author Pavel Drozd
 * 
 * @param <T> entity type
 * @param <PK> primary key type
 */
public abstract class DAO<T extends CloneableEntity<T>, PK extends Serializable> {

   @PersistenceContext(unitName = "PerfRepoPU")
   private EntityManager em;

   private Class<T> type;

   @SuppressWarnings("unchecked")
   public DAO() {
      if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
         type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      } else {
         type = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
      }
   }

   /**
    * 
    * Find an entity based on it's primary key
    * 
    * @param id Primary key
    * @return Entity
    */
   public T find(final PK id) {
      return em.find(type, id);
   }

   /**
    * Find an entity and return an unmanaged read-only version - this will be unmanaged only on the
    * root level, the collections will still be lazy-loadable ones.
    * 
    * @param id
    * @return
    */
   public T findReadOnly(final PK id) {
      // TODO: maybe produce some hint for JPA layer
      T obj = em.find(type, id);
      return obj == null ? null : obj.clone();
   }

   public List<T> findAll() {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      return em.createQuery(criteria).getResultList();
   }

   public List<T> findAllReadOnly() {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      List<T> result1 = em.createQuery(criteria).getResultList();
      if (result1.isEmpty()) {
         return result1;
      }
      List<T> result2 = new ArrayList<T>(result1.size());
      for (T item : result1) {
         result2.add(item.clone());
      }
      return result2;
   }

   public List<T> findAllByProperty(final String propertyName, final Object value) {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      criteria.where(em.getCriteriaBuilder().equal(root.get(propertyName), value));
      return em.createQuery(criteria).getResultList();
   }

   public <T1> List<T1> findByCustomCriteria(final CriteriaQuery<T1> criteria) {
      return em.createQuery(criteria).getResultList();
   }

   public T findByCustomCriteriaSingle(final CriteriaQuery<T> criteria) {
      return em.createQuery(criteria).getSingleResult();
   }

   public T update(final T entity) {
      T stored = em.merge(entity);
      em.flush();
      return stored;
   }

   public T create(final T entity) {
      em.persist(entity);
      em.flush();
      return entity;

   }

   public void delete(final T entity) {
      em.remove(entity);
      em.flush();
   }

   protected <T1> TypedQuery<T1> query(CriteriaQuery<T1> criteria) {
      return em.createQuery(criteria);
   }

   public List<T> findByQuery(String query, Map<String, Object> queryParams) {
      TypedQuery<T> tq = em.createQuery(query, type);
      for (Entry<String, Object> entry : queryParams.entrySet()) {
         tq.setParameter(entry.getKey(), entry.getValue());
      }
      return tq.getResultList();
   }

   public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
      TypedQuery<T> tq = em.createNamedQuery(queryName, type);
      for (Entry<String, Object> entry : queryParams.entrySet()) {
         tq.setParameter(entry.getKey(), entry.getValue());
      }
      return tq.getResultList();
   }

   /**
    * 
    * @param queryName
    * @param clones Return clones of root objects returned by this query.
    * @param params
    * @return
    */
   public List<T> findByNamedQuery(String queryName, boolean clones, Object... params) {
      Map<String, Object> queryParams = new TreeMap<String, Object>();
      if (params.length % 2 != 0) {
         throw new IllegalArgumentException("even number of params needed");
      }
      for (int i = 0; i < params.length; i += 2) {
         queryParams.put((String) params[i], params[i + 1]);
      }
      TypedQuery<T> tq = em.createNamedQuery(queryName, type);
      for (Entry<String, Object> entry : queryParams.entrySet()) {
         tq.setParameter(entry.getKey(), entry.getValue());
      }
      List<T> result1 = tq.getResultList();
      if (!clones || result1.isEmpty()) {
         return result1;
      } else {
         List<T> resultClone = new ArrayList<T>(result1.size());
         for (T item : result1) {
            resultClone.add(item.clone());
         }
         return resultClone;
      }
   }

   protected CriteriaQuery<T> createCriteria() {
      return em.getCriteriaBuilder().createQuery(type);
   }

   protected CriteriaBuilder criteriaBuilder() {
      return em.getCriteriaBuilder();
   }

   public T findWithDepth(Object id, String... fetchRelations) {
      CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
      CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
      Root<T> root = criteriaQuery.from(type);
      for (String relation : fetchRelations) {
         FetchParent<T, T> fetch = root;
         for (String pathSegment : relation.split("\\.")) {
            fetch = fetch.fetch(pathSegment, JoinType.LEFT);
         }
      }
      criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
      return getSingleOrNoneResult(em.createQuery(criteriaQuery));
   }

   private T getSingleOrNoneResult(TypedQuery<T> query) {
      query.setMaxResults(1);
      List<T> result = query.getResultList();
      if (result.isEmpty()) {
         return null;
      }
      return result.get(0);
   }

   protected Query createNamedQuery(String name) {
      return em.createNamedQuery(name);
   }

   protected <QueryType> TypedQuery<QueryType> createNamedQuery(String name, Class<QueryType> clazz) {
      return em.createNamedQuery(name, clazz);
   }
}