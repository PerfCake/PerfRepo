package org.perfrepo.web.session;

import org.perfrepo.web.model.user.User;

/**
 * Interface remembering currently logged user and his session settings.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface UserSession {

    User getLoggedUser();
}
