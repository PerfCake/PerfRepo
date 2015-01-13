package org.perfrepo.model.auth;

/**
 * AccessLevel is a attribute of permission. It decides to who the permission is applied.
 * <p/>
 * If it's USER, the permission is applied to the user specified in @link{Permission.userId}.
 * If it's GROUP, the permission is applied to the group specified in @link(Permission.groupId}.
 * If it's PUBLIC, the permission is applied to everybody, therefore neither @link{Permission.userId}
 * nor @link{Permission.groupId} is set.
 */
public enum AccessLevel {

	/**
	 * User Level
	 */
	USER,

	/**
	 * Group Level
	 */
	GROUP,

	/**
	 * Public Level
	 */
	PUBLIC

}
