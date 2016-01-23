/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import com.google.gson.reflect.TypeToken;

public class BugzillaRestUnauthenticatedGetRequest<T> extends BugzillaRestAuthenticatedGetRequest<T> {

	public BugzillaRestUnauthenticatedGetRequest(BugzillaRestHttpClient client, String urlSuffix,
			TypeToken<?> responseType) {
		super(client, urlSuffix, responseType);
	}

	@Override
	protected boolean needsAuthentication() {
		return false;
	}

	@Override
	protected String createHttpRequestURL() {
		String bugUrl = getUrlSuffix();
		return baseUrl() + bugUrl;
	}

}