package org.perfrepo.model.auth;

/**
 * AccessType represents a type of permission, it's its property. If the permission is READ, the user (or group) can only
 * read the entity, therefore cannot modify it.
 * <p/>
 * If the permission is WRITE, user (or group) can also modify the entity.
 */
public enum AccessType {

	/**
	 * Read Access Type
	 */
	READ,

	/**
	 * Write Access Type
	 */
	WRITE
}
