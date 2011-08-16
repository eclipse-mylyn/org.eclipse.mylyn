/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.client;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.impl.auth.RFC2617Scheme;
import org.apache.http.message.BasicHeader;

/**
 * OAuth2 authorization scheme. Sets an authorization header with a value of the
 * space-separated user principal name and password from credentials.
 */
public class OAuth2Scheme extends RFC2617Scheme {

	private boolean complete = false;

	public void processChallenge(Header header)
			throws MalformedChallengeException {
		super.processChallenge(header);
		complete = true;
	}

	public String getSchemeName() {
		return IGitHubConstants.SCHEME_OAUTH2;
	}

	public boolean isConnectionBased() {
		return false;
	}

	public boolean isComplete() {
		return complete;
	}

	public Header authenticate(Credentials credentials, HttpRequest request)
			throws AuthenticationException {
		if (credentials == null)
			throw new IllegalArgumentException("Credentials cannot be null"); //$NON-NLS-1$
		return new BasicHeader(HttpHeaders.AUTHORIZATION, credentials
				.getUserPrincipal().getName() + ' ' + credentials.getPassword());
	}
}
