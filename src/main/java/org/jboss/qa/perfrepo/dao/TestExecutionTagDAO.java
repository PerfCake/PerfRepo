package org.jboss.qa.perfrepo.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecutionTag;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TestExecutionTagDAO extends DAO<TestExecutionTag, Long> {

   private static final long serialVersionUID = 1L;

}