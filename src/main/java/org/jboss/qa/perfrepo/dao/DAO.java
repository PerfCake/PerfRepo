package org.jboss.qa.perfrepo.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;


/**
 * Common ancestor for DAO objects
 * @author Pavel Drozd
 *
 * @param <T> entity type
 * @param <PK> primary key type
 */
public abstract class DAO<T, PK extends Serializable> implements Serializable {
   
   
   private static final long serialVersionUID = 3174235359562788810L;

   @Inject
   private EntityManager em;
   
   private Class<T> type;
   
   @SuppressWarnings("unchecked")
   public DAO() {      
      if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
         type = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      } else {
         type = (Class<T>)((ParameterizedType)getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
      }
   }   
   
   public T get(final PK id) {      
      return em.find(type, id);      
   }

   public List<T> findAll() {   
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      return em.createQuery(criteria).getResultList();
   }

   public List<T> findAllByProperty(final String propertyName, final Object value) {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      criteria.where(em.getCriteriaBuilder().equal(root.get(propertyName), value));
      return em.createQuery(criteria).getResultList();
   }
   
   public List<T> findByCustomCriteria(final CriteriaQuery<T> criteria) {
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
   
   public List<T> findByQuery(String query, Map<String, Object> queryParams) {
      TypedQuery<T> tq = em.createQuery(query, type);
      for (Entry<String, Object> entry : queryParams.entrySet()) {
         tq.setParameter(entry.getKey(), entry.getValue());
      }
      return tq.getResultList();
   }

   protected CriteriaQuery<T> createCriteria() {      
      return em.getCriteriaBuilder().createQuery(type); 
   }
   
   protected EntityManager getEntityManager() {
      return em;
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

}