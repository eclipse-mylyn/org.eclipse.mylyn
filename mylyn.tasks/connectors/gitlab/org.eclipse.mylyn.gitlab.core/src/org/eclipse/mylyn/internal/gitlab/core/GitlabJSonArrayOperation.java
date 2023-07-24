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
	@Override
	protected JsonArray execute(IOperationMonitor monitor) throws IOException, GitlabException {
		JsonArray result = null;
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		result = processAndRelease(response, monitor);
		 Header[] linkHeader = response.getResponse().getHeaders("Link");
		if (linkHeader.length > 0) {
//			System.out.print("Page Act: ");
//			System.out.println(response.getResponse().getHeaders("X-Page")[0].getValue());
//			System.out.print("Page Count: ");
//			System.out.println(response.getResponse().getHeaders("X-Total-Pages")[0].getValue());
			Header firstLinkHeader = linkHeader[0];
			for (String linkHeaderEntry : firstLinkHeader.getValue().split(", ")) {
				String[] linkHeaderElements = linkHeaderEntry.split("; ");
//				System.out.print(lh3[1]);
//				System.out.print("  ");
//				System.out.println(lh3[0]);
				if ("rel=\"next\"".equals(linkHeaderElements[1])) {
//					System.out.println("process "+lh3[0].substring(1, lh3[0].length()-1));
					HttpRequestBase looprequest = new HttpGet(linkHeaderElements[0].substring(1, linkHeaderElements[0].length()-1));
					addHttpRequestEntities(looprequest);
					CommonHttpResponse loopresponse = execute(looprequest, monitor);
					JsonArray loopresult = processAndRelease(loopresponse, monitor);
					result.addAll(loopresult);
				break;
				}
			}
		}		
		
		return result;
	}
}
