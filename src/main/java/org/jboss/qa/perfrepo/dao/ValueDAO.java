package org.jboss.qa.perfrepo.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Value;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ValueDAO extends DAO<Value, Long> {

   private static final long serialVersionUID = 1L;
   
}