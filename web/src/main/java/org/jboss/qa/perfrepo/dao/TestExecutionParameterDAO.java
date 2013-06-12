package org.jboss.qa.perfrepo.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecutionParameter;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TestExecutionParameterDAO extends DAO<TestExecutionParameter, Long> {

   // nothing to add

}