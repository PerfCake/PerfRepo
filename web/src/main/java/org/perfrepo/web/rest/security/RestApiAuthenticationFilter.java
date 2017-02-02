package org.perfrepo.web.rest.security;

import org.perfrepo.web.security.authentication.AuthenticatedUser;
import org.perfrepo.web.security.authentication.AuthenticationService;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@WebFilter(filterName = "RestApiAuthenticationFilter", urlPatterns = {"/rest/json/*"})
public class RestApiAuthenticationFilter implements Filter {

   @Inject
   private AuthenticationService authenticationService;

   @Inject
   @AuthenticatedUser
   private Event<String> userAuthenticatedEvent;

   @Override
   public void init(FilterConfig filterConfig) throws ServletException {

   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
           ServletException {

      HttpServletRequest req = (HttpServletRequest) request;
      String authorization = req.getHeader("Authorization");

      if (((HttpServletRequest) request).getRequestURI().equals("/rest/json/authentication")) {
          chain.doFilter(request, response);
          return;
      }

      if (authorization != null && authorization.contains("Bearer ")) {
         String token = authorization.substring("Bearer ".length()).trim();

         if (authenticationService.isAuthenticated(token)) {
            userAuthenticatedEvent.fire(token);
            chain.doFilter(request, response);
         } else {
            unauthorized((HttpServletResponse) response, "Invalid credentials.");
         }

      } else {
         unauthorized((HttpServletResponse) response, "Invalid credentials.");
      }
   }

   private void unauthorized(HttpServletResponse httpResponse, String message) throws IOException {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
   }

   @Override
   public void destroy() {

   }
}
