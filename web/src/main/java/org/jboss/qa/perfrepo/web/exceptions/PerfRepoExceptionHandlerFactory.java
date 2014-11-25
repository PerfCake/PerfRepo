package org.jboss.qa.perfrepo.web.exceptions;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Factory for {@link PerfRepoExceptionHandler}
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class PerfRepoExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory parent;

	public PerfRepoExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new PerfRepoExceptionHandler(parent.getExceptionHandler());
	}
}
