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

package org.eclipse.mylyn.internal.commons.repositories.ui.auth;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractCredentialsProviderUi<T extends AuthenticationCredentials> {

	/**
	 * Opens the credentials UI. Invoked from the UI thread.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param request
	 *            the authentication request
	 * @return {@link IStatus#OK} on success; {@link IStatus#CANCEL} on cancellation; an error status otherwise
	 */
	public abstract IStatus open(Shell parentShell, AuthenticationRequest<AuthenticationType<T>> request);

	/**
	 * Returns the provided credentials.
	 */
	public abstract T getCredentials();

}
