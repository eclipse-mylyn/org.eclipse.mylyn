/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.core.util;

import java.net.Proxy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.auth.UsernamePasswordCredentials;

/**
 * @author Steffen Pingel
 */
public class RepositoryWebLocation extends AbstractWebLocation {

	private final RepositoryLocation location;

	public RepositoryWebLocation(RepositoryLocation location) {
		super(location.getUri().toString());
		this.location = location;
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationType type) {
		UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) location
				.getCredentials(org.eclipse.mylyn.commons.repositories.auth.AuthenticationType.REPOSITORY);
		if (credentials != null) {
			return new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword());
		}
		return null;
	}

	@Override
	public Proxy getProxyForHost(String host, String proxyType) {
		return location.getService().getProxyForHost(host, proxyType);
	}

	@Override
	public void requestCredentials(AuthenticationType type, String message, IProgressMonitor monitor) {
		location.getService().requestCredentials(
				org.eclipse.mylyn.commons.repositories.auth.AuthenticationType.REPOSITORY,
				UsernamePasswordCredentials.class, message, monitor);
	}

	@Override
	public String getUrl() {
		return location.getUri().toString();
	}

}
