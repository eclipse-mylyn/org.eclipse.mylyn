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

package org.eclipse.mylyn.commons.repository;

import java.net.Proxy;

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.repository.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repository.auth.AuthenticationType;

/**
 * @author Steffen Pingel
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILocationService {

	public abstract Proxy getProxyForHost(String host, String proxyType);

	public abstract X509TrustManager getTrustManager();

	public abstract <T extends AuthenticationCredentials> T requestCredentials(AuthenticationType type,
			Class<T> credentialsKind, String message, IProgressMonitor monitor);

}
