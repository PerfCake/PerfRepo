package org.jboss.qa.perfrepo.auth;

import javax.inject.Inject;

import org.jboss.qa.perfrepo.dao.TestDAO;
import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.auth.AccessType;
import org.jboss.qa.perfrepo.model.auth.SecuredEntity;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.service.UserService;

public class AuthorizationService {
	
	@Inject
	TestDAO testDAO;

	@Inject
	UserService userService;
	
	private boolean isUserAuthorizedForReport(Long userId, AccessType accessType, Report report) {
		//add implementation
		return true;
	}

	private boolean isUserAuthorizedForTest(Long userId, AccessType accessType, Test test) {
		return userService.isLoggedUserInGroup(test.getGroupId());
	}
	
	private Report getParentReportEntity(Entity<?> entity) {
		if (entity instanceof Report) {
			return (Report)entity;
		}
		return null;
	}
	
	private Test getParentTestEntity(Entity<?> entity) {
		if (entity instanceof Test) {
			return (Test)entity;
		}
		return testDAO.getTestByRelation(entity);
	}

	public boolean isUserAuthorizedFor(Long userId, AccessType accessType, Entity<?> entity) {
		SecuredEntity securedEntityAnnotation = entity.getClass().getAnnotation(SecuredEntity.class);
		if (securedEntityAnnotation != null) {
			switch (securedEntityAnnotation.type()) {
				case TEST:
					return isUserAuthorizedForTest(userId, accessType, getParentTestEntity(entity));
				case REPORT:
					return isUserAuthorizedForReport(userId, accessType, getParentReportEntity(entity));
				case USER:
					//TODO: add implementation
					return true;				
				default:
					return false;
			}
		}
		return true;
	}

	public boolean isUserAuthorizedFor(AccessType accessType, Entity<?> entity) {
		return isUserAuthorizedFor(userService.getLoggedUserId(), accessType, entity);
	}
}
