/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;

/**
 * Mix-in interface to {@link AbstractWebLocation}s that support OpenID authentication.
 *
 * @author Steffen Pingel
 */
public interface IOpenIdLocation {

	/**
	 * Returns a URL if this location is configured for OpenID authentication.
	 */
	String getProviderUrl();

	/**
	 * Handles OpenID authentication in a blocking way.
	 *
	 * @param monitor
	 * @param providerUrl
	 *            the Open ID provider URL
	 * @param providerArgs
	 *            the request parameters for the POST request
	 * @return the intercepted response URL from the Open ID provider
	 * @throws OperationCanceledException
	 *             if the authentication was canceled
	 */
	OpenIdAuthenticationResponse requestAuthentication(OpenIdAuthenticationRequest request, IProgressMonitor monitor)
			throws UnsupportedRequestException;

}
