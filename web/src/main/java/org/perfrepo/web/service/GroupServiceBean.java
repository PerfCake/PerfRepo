package org.perfrepo.web.service;

import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.Membership;
import org.perfrepo.web.model.user.Membership.MembershipType;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.MembershipDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.validation.annotation.ValidGroup;
import org.perfrepo.web.service.validation.annotation.ValidUser;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static org.perfrepo.web.service.validation.ValidationType.DUPLICATE_CHECK;
import static org.perfrepo.web.service.validation.ValidationType.EXISTS;
import static org.perfrepo.web.service.validation.ValidationType.ID_NULL;
import static org.perfrepo.web.service.validation.ValidationType.SEMANTIC_CHECK;

/**
 * Implementation of {@link GroupService}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GroupServiceBean implements GroupService {

    @Inject
    private GroupDAO groupDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private MembershipDAO membershipDAO;

    @Inject
    private UserService userService;

    @Inject
    private UserSession userSession;

    @Override
    public Group createGroup(@ValidGroup(type = { ID_NULL, SEMANTIC_CHECK, DUPLICATE_CHECK}) Group group) {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        return groupDAO.create(group);
    }

    @Override
    public Group updateGroup(@ValidGroup(type = { EXISTS, SEMANTIC_CHECK, DUPLICATE_CHECK}) Group group) {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        return groupDAO.merge(group);
    }

    @Override
    public void removeGroup(@ValidGroup Group group) {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        Group managedGroup = groupDAO.get(group.getId());
        groupDAO.remove(managedGroup);
    }

    @Override
    public Group getGroup(Long id) {
        return groupDAO.get(id);
    }

    @Override
    public Group getGroup(String name) {
        return groupDAO.findByName(name);
    }

    @Override
    public List<Group> getAllGroups() {
        return groupDAO.getAll();
    }

    @Override
    public boolean isUserInGroup(User user, Group group) {
        Set<Group> groups = userService.getUserGroups(user);
        return groups.contains(group);
    }

    @Override
    public void addUserToGroup(User user, Group group) {
        addUserToGroup(user, group, MembershipType.REGULAR_USER);
    }

    @Override
    public void addUserToGroup(@ValidUser User user, @ValidGroup Group group, MembershipType membershipType) {
        if (!userSession.getLoggedUser().isSuperAdmin() && !userService.isUserGroupAdmin(userSession.getLoggedUser(), group)) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        User managedUser = userDAO.get(user.getId());
        Group managedGroup = groupDAO.get(group.getId());

        Membership membership = new Membership();
        membership.setUser(managedUser);
        membership.setGroup(managedGroup);
        membership.setType(membershipType);

        membershipDAO.create(membership);
    }

    @Override
    public void removeUserFromGroup(@ValidUser User user, @ValidGroup Group group) {
        if (!userSession.getLoggedUser().isSuperAdmin() && !userService.isUserGroupAdmin(userSession.getLoggedUser(), group)) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        User managedUser = userDAO.get(user.getId());
        Group managedGroup = groupDAO.get(group.getId());

        Membership membership = membershipDAO.getMembership(managedUser, managedGroup);
        membershipDAO.remove(membership);
    }
}
