package org.perfrepo.web.security.authentication;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Producer for AuthenticatedUserInfo, basically handles authentication.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RequestScoped
public class AuthenticatedUserInfoProducer {

    @Inject
    private AuthenticationService authenticationService;

    @Produces
    @RequestScoped
    @AuthenticatedUser
    private AuthenticatedUserInfo userInfo;

    public void handleAuthenticatedEvent(@Observes @AuthenticatedUser String token) {
        this.userInfo = authenticationService.getLoggedUsers().get(token);
    }
}
