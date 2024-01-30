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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.CertificateCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.OpenIdCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class RepositoryAuthenticator<T extends AuthenticationCredentials> {

	private final static Object lock = new Object();

	private final AuthenticationRequest<AuthenticationType<T>> request;

	public RepositoryAuthenticator(AuthenticationRequest<AuthenticationType<T>> request) {
		this.request = request;
	}

	public T open(IProgressMonitor monitor) {
		AuthenticationCredentials oldCredentials = request.getLocation()
				.getCredentials(request.getAuthenticationType());
		// synchronize on a static lock to ensure that only one password dialog is displayed at a time
		synchronized (lock) {
			// check if the credentials changed while the thread was waiting for the lock
			T newCredentials = request.getLocation().getCredentials(request.getAuthenticationType());
			if (!CoreUtil.areEqual(oldCredentials, newCredentials)) {
				return newCredentials;
			}

			// check if the workbench was closed meanwhile
			if (PlatformUI.getWorkbench().getDisplay().isDisposed()) {
				throw new UnsupportedOperationException();
			}

			final AtomicReference<IStatus> status = new AtomicReference<>();
			final AtomicReference<T> requestedCredentials = new AtomicReference<>();

			// show dialog
			PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
				AbstractCredentialsProviderUi<T> provider = getCredentialsProviderUi();

				// open prompt
				IStatus result = provider.open(WorkbenchUtil.getShell(), request);
				status.set(result);

				requestedCredentials.set(provider.getCredentials());
			});

			if (status.get() == null) {
				throw new IllegalStateException();
			}
			if (status.get().getSeverity() == IStatus.CANCEL) {
				throw new OperationCanceledException();
			}
			if (!status.get().isOK()) {
				StatusHandler.log(status.get());
				throw new UnsupportedOperationException();
			}

			return requestedCredentials.get();
		}
	}

	@SuppressWarnings("unchecked")
	protected AbstractCredentialsProviderUi<T> getCredentialsProviderUi() {
		Class<T> credentialsType = request.getAuthenticationType().getCredentialsType();
		if (credentialsType == UserCredentials.class) {
			return (AbstractCredentialsProviderUi<T>) new UserCredentialsProviderUi();
		} else if (credentialsType == CertificateCredentials.class) {
			return (AbstractCredentialsProviderUi<T>) new CertificateCredentialsProviderUi();
		} else if (credentialsType == OpenIdCredentials.class) {
			return (AbstractCredentialsProviderUi<T>) new OpenIdCredentialsProviderUi();
		}
		return null;
	}

}
