package org.jboss.qa.perfrepo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@ApplicationScoped
public class EntityManagerProducer {

   @PersistenceContext(unitName = "PerfRepoPU", type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Produces
   @RequestScoped
   public EntityManager getEntityManager() {
      return entityManager;
   }

   // public void closeEntityManager(@Disposes EntityManager em) {
   // // if (em != null && em.getTransaction().isActive()) {
   // // em.getTransaction().rollback();
   // // }
   // if (em != null && em.isOpen()) {
   // em.close();
   // }
   // }

}
