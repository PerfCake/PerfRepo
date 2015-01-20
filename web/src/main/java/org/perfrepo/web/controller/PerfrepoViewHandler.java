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
package org.perfrepo.web.controller;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link ViewHandler} that makes forms to post to original url.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class PerfrepoViewHandler extends ViewHandlerWrapper {

	private ViewHandler wrapped;

	public PerfrepoViewHandler(ViewHandler wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * We always post back to the original Request URL, not the viewID since we sometimes encode
	 * state in the Request URL such as object id, page number, etc.
	 */
	@Override
	public String getActionURL(FacesContext faces, String viewID) {
		HttpServletRequest request = (HttpServletRequest) faces.getExternalContext().getRequest();
		String formActionURL = (String) request.getAttribute("formActionURL");
		if (formActionURL == null) {
			return super.getActionURL(faces, viewID);
		} else {
			return request.getContextPath() + formActionURL;
		}
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}
}
