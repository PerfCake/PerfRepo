/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.exceptions;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.perfrepo.web.controller.BaseController;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Exception handler.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class PerfRepoExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger log = Logger.getLogger(PerfRepoExceptionHandler.class);

	private ExceptionHandler wrapped;

	public PerfRepoExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			try {
				Throwable e = context.getException();
				log.error("Unhandled exception", e);
				Map<String, Object> sm = ec.getSessionMap();
				@SuppressWarnings("unchecked")
				List<FacesMessage> sessionMsgs = (List<FacesMessage>) sm.get(BaseController.SESSION_MESSAGES_KEY);
				if (sessionMsgs == null) {
					sessionMsgs = new ArrayList<FacesMessage>();
					sm.put(BaseController.SESSION_MESSAGES_KEY, sessionMsgs);
				}
				Throwable cause = ExceptionUtils.getRootCause(e);
				if (cause != null && cause instanceof SecurityException) {
					sessionMsgs.add(new FacesMessage(FacesMessage.SEVERITY_FATAL, cause.getMessage(), cause
							.getMessage()));
				} else {
					sessionMsgs.add(new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getMessage(), e.getMessage()));
					try {
						ec.redirect(ec.getRequestContextPath() + "/");
					} catch (IOException e1) {
						throw new RuntimeException("redirect failed", e1);
					}
				}
				fc.renderResponse();
			} finally {
				//remove it from queue
				i.remove();
			}
		}
		//parent handle
		getWrapped().handle();
	}
}