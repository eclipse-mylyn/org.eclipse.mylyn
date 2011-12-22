/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
public class CommonHttpResponse {

	private final HttpRequest request;

	private final HttpResponse response;

	public CommonHttpResponse(HttpRequest request, HttpResponse response) {
		Assert.isNotNull(request);
		Assert.isNotNull(response);
		this.request = request;
		this.response = response;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	public InputStream getResponseEntityAsStream(IProgressMonitor monitor) throws IOException {
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			throw new IOException("Expected entity"); //$NON-NLS-1$
		}
		return HttpUtil.getResponseBodyAsStream(entity, monitor);
	}

	public void release(IProgressMonitor monitor) {
		HttpUtil.release(request, response, monitor);
	}

	public String getRequestPath() {
		if (request instanceof HttpUriRequest) {
			return ((HttpUriRequest) request).getURI().getPath();
		} else {
			return null;
		}
	}

	public String getResponseCharSet() {
		return EntityUtils.getContentCharSet(response.getEntity());
	}

}
