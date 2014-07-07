package org.jboss.qa.perfrepo.rest.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.util.Base64;

/**
 * Filter used to authenticate rest requests
 * The request should contain basic authentication header
 * @author Pavel Drozd
 *
 */
@WebFilter(filterName = "RestAuthenticationFilter", urlPatterns = {"/rest/*"})
public class RestAuthenticationFilter implements Filter {

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			//TODO: use some library to parse basic authentication header
			String basicLogin = req.getHeader("Authorization");
			String username = null;
			String password = null;
			if ((basicLogin != null) && (basicLogin.indexOf("Basic") != -1)) {
				String loginPassword = new String(Base64.decode(basicLogin.substring("Basic ".length()).trim()));
				username = loginPassword.substring(0, loginPassword.indexOf(":"));
				password = loginPassword.substring(loginPassword.indexOf(":") + 1);
				try {
					req.login(username, password);
					chain.doFilter(request, response);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		}
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {

	}
}
