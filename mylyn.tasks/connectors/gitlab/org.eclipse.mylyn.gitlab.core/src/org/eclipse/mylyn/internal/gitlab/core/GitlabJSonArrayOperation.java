/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.core;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.gitlab.core.GitlabException;

import com.google.gson.JsonArray;

public abstract class GitlabJSonArrayOperation extends GitlabOperation<JsonArray> {

	public GitlabJSonArrayOperation(CommonHttpClient client, String urlSuffix) {
		super(client, urlSuffix);
	}

	private String nextPage(Header[] linkHeader) {
		if (linkHeader.length > 0) {
			Header firstLinkHeader = linkHeader[0];
			for (String linkHeaderEntry : firstLinkHeader.getValue().split(", ")) { //$NON-NLS-1$
				String[] linkHeaderElements = linkHeaderEntry.split("; "); //$NON-NLS-1$
				if ("rel=\"next\"".equals(linkHeaderElements[1])) { //$NON-NLS-1$
					return linkHeaderElements[0].substring(1, linkHeaderElements[0].length() - 1);
				}
			}
		}

		return null;
	}

	@Override
	protected JsonArray execute(IOperationMonitor monitor) throws IOException, GitlabException {
		JsonArray result = null;
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		result = processAndRelease(response, monitor);
		Header[] linkHeader = response.getResponse().getHeaders("Link"); //$NON-NLS-1$
		String nextPageValue = nextPage(linkHeader);
		while (nextPageValue != null) {
			HttpRequestBase looprequest = new HttpGet(nextPageValue);
			addHttpRequestEntities(looprequest);
			CommonHttpResponse loopresponse = execute(looprequest, monitor);
			JsonArray loopresult = processAndRelease(loopresponse, monitor);
			result.addAll(loopresult);
			linkHeader = loopresponse.getResponse().getHeaders("Link"); //$NON-NLS-1$
			nextPageValue = nextPage(linkHeader);
		}

		return result;
	}
}
