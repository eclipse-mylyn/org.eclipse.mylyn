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

package org.eclipse.mylyn.internal.gerrit.core.client;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;

/**
 * Mix-in interface to {@link AbstractWebLocation}s that support OpenID authentication.
 * 
 * @author Steffen Pingel
 */
public interface IOpenIdLocation {

	/**
	 * Returns a URL if this location is configured for OpenID authentication.
	 */
	public String getProviderUrl();

	/**
	 * Handles OpenID authentication in a blocking way.
	 * 
	 * @param providerUrl
	 *            the Open ID provider URL
	 * @param providerArgs
	 *            the request parameters for the POST request
	 * @return the intercepted response URL from the Open ID provider
	 * @throws OperationCanceledException
	 *             if the authentication was canceled
	 */
	public OpenIdAuthenticationResponse requestAuthentication(OpenIdAuthenticationRequest request);

}
