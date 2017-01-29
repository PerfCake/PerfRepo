package org.perfrepo.web.service;

import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.Membership.MembershipType;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.validation.annotation.ValidGroup;
import org.perfrepo.web.service.validation.annotation.ValidUser;

import java.util.List;

import static org.perfrepo.web.service.validation.ValidationType.DUPLICATE_CHECK;
import static org.perfrepo.web.service.validation.ValidationType.EXISTS;
import static org.perfrepo.web.service.validation.ValidationType.ID_NULL;
import static org.perfrepo.web.service.validation.ValidationType.SEMANTIC_CHECK;

/**
 * Service for {@link Group} entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */

public interface GroupService {

    /**
     * Create new group.
     *
     * @param group
     * @return group
     */
    Group createGroup(@ValidGroup(type = {ID_NULL, SEMANTIC_CHECK, DUPLICATE_CHECK}) Group group);

    /**
     * Updates group
     *
     * @param group
     * @return group
     */
    Group updateGroup(@ValidGroup(type = {EXISTS, SEMANTIC_CHECK, DUPLICATE_CHECK}) Group group);

    /**
     * Deletes group
     *
     * @param group
     */
    void removeGroup(@ValidGroup Group group);

    /**
     * Retrieves group
     *
     * @param id
     * @return group
     */
    Group getGroup(Long id);

    /**
     * Retrieves group by name
     *
     * @param name
     * @return group
     */
    Group getGroup(String name);

    /**
     * Retrieves all groups
     *
     * @return
     */
    List<Group> getAllGroups();

    /**
     * Retrieves if user is assign in defined group
     *
     * @param user
     * @param group
     * @return boolean
     */
    boolean isUserInGroup(User user, Group group);

    /**
     * Adds user to group as a regular user.
     *
     * @param user
     * @param group
     */
    void addUserToGroup(User user, Group group);

    /**
     * Adds user to group.
     *
     * @param user
     * @param group
     */
    void addUserToGroup(@ValidUser User user, @ValidGroup Group group, MembershipType membershipType);

    /**
     * Removes user from group.
     *
     * @param user
     * @param group
     */
    void removeUserFromGroup(@ValidUser User user, @ValidGroup Group group);
}
