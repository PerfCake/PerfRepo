package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;
import org.perfrepo.web.adapter.AuthenticationAdapter;
import org.perfrepo.web.security.authentication.AuthenticationService;

import javax.inject.Inject;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class AuthenticationAdapterImpl implements AuthenticationAdapter {

    @Inject
    private AuthenticationService authenticationService;

    @Override
    public AuthenticationResult login(LoginCredentialParams credentials) {
        return authenticationService.login(credentials);
    }

    @Override
    public void logout(String token) {
        authenticationService.logout(token);
    }
}
