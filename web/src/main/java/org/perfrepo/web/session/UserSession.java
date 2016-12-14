package org.perfrepo.web.session;

import org.perfrepo.model.user.User;

/**
 * Interface remembering currently logged user and his session settings.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface UserSession {

    public User getLoggedUser();
}
