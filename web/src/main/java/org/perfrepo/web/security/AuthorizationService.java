package org.perfrepo.web.security;

import org.perfrepo.model.Entity;
import org.perfrepo.model.auth.AccessType;

/**
 * Authorization Service is responsible for permission verification on entities
 *
 * @author Pavel Drozd
 */
public interface AuthorizationService {

	/**
	 * Verifies if the specific user is authorized to entity
	 *
	 * @param userId
	 * @param accessType
	 * @param entity
	 * @return boolean
	 */
	public boolean isUserAuthorizedFor(Long userId, AccessType accessType, Entity<?> entity);

	/**
	 * Verifies if the logged user is authorized to entity
	 *
	 * @param userId
	 * @param accessType
	 * @param entity
	 * @return boolean
	 */
	public boolean isUserAuthorizedFor(AccessType accessType, Entity<?> entity);
}
