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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;

public abstract class GitlabPostOperation<T> extends GitlabOperation<T> {

	public GitlabPostOperation(CommonHttpClient client, String urlSuffix) {
		super(client, urlSuffix);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpRequestBase request = new HttpPost(url);
		return request;
	}

}
