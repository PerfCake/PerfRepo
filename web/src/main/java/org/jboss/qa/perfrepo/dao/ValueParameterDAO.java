package org.jboss.qa.perfrepo.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.ValueParameter;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ValueParameterDAO extends DAO<ValueParameter, Long> {

   // nothing to add

}