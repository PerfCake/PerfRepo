package org.jboss.qa.perfrepo.dao;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecutionAttachment;

/**
 * 
 * DAO for {@link TestExecutionAttachment}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@Stateless
public class TestExecutionAttachmentDAO extends DAO<TestExecutionAttachment, Long> {

   // nothing to add

}