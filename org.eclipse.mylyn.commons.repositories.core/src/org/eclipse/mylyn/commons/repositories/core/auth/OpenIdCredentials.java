/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

/**
 * @author Steffen Pingel
 */
public class OpenIdCredentials extends AuthenticationCredentials {

	private final String responseUrl;

	private final String token;

	public OpenIdCredentials(String responseUrl, String token) {
		this.responseUrl = responseUrl;
		this.token = token;
	}

	protected OpenIdCredentials(ICredentialsStore store, String prefix, boolean loadSecrets) {
		this(store.get(prefix + ".responseUrl", null), store.get(prefix + ".token", null)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OpenIdCredentials other = (OpenIdCredentials) obj;
		if (responseUrl == null) {
			if (other.responseUrl != null) {
				return false;
			}
		} else if (!responseUrl.equals(other.responseUrl)) {
			return false;
		}
		if (token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!token.equals(other.token)) {
			return false;
		}
		return true;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public String getToken() {
		return token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((responseUrl == null) ? 0 : responseUrl.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public void clear(ICredentialsStore store, String prefix) {
		store.remove(prefix + ".responseUrl"); //$NON-NLS-1$
		store.remove(prefix + ".token"); //$NON-NLS-1$
	}

	@Override
	public void save(ICredentialsStore store, String prefix) {
		store.put(prefix + ".responseUrl", responseUrl, true); //$NON-NLS-1$
		store.put(prefix + ".token", token, true); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpenIdCredentials [responseUrl="); //$NON-NLS-1$
		builder.append(responseUrl);
		builder.append(", token="); //$NON-NLS-1$
		builder.append(token);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
