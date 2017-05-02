package org.perfrepo.web.security.authentication;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;
import org.perfrepo.web.adapter.exceptions.UnauthorizedException;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.UserServiceBean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@ApplicationScoped
public class AuthenticationService {

    private static final String AUTHENTICATION_CACHE_NAME = "authenticationCacheName";

    private Cache<String, AuthenticatedUserInfo> loggedUsers;

    @Inject
    private UserService userService;

    @PostConstruct
    public void init() {
        GlobalConfiguration globalConfiguration = new GlobalConfigurationBuilder()
                .globalJmxStatistics().allowDuplicateDomains(true)
                .build();

        Configuration authenticationCacheConfiguration = new ConfigurationBuilder()
                .expiration()
                    .maxIdle(30, TimeUnit.MINUTES)
                .build();

        EmbeddedCacheManager cacheManager = new DefaultCacheManager(globalConfiguration);
        cacheManager.defineConfiguration(AUTHENTICATION_CACHE_NAME, authenticationCacheConfiguration);
        loggedUsers = cacheManager.getCache(AUTHENTICATION_CACHE_NAME);
    }

    public boolean isAuthenticated(String token) {
        return loggedUsers.containsKey(token);
    }

    public AuthenticationResult login(LoginCredentialParams credentials) {
        if (credentials == null) {
            throw new UnauthorizedException("Bad login.");
        }

        User user = userService.getUser(credentials.getUsername());

        if (user == null) {
            throw new UnauthorizedException("Bad login.");
        }

        //TODO: figure out where to put computeMd5 method
        if (user.getPassword().equals(UserServiceBean.computeMd5(credentials.getPassword()))) {
            AuthenticationResult authenticationDto = new AuthenticationResult();

            //TODO: use real token, probably JWT
            String token = credentials.getUsername();
            authenticationDto.setToken(token);

            AuthenticatedUserInfo userInfo = new AuthenticatedUserInfo();
            userInfo.setUser(user);

            loggedUsers.put(token, userInfo);

            return authenticationDto;
        }

        throw new UnauthorizedException("Bad password.");

    }

    public void logout(String token) {
        loggedUsers.remove(token);
    }

    public Cache<String, AuthenticatedUserInfo> getLoggedUsers() {
        return loggedUsers;
    }
}
