/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
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
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserAuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.ui.dialogs.CredentialsDialog;
import org.eclipse.mylyn.commons.ui.dialogs.CredentialsDialog.Mode;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Steffen Pingel
 */
public class UserCredentialsProviderUi extends AbstractCredentialsProviderUi<UserCredentials> {

	private UserCredentials credentials;

	@Override
	public UserCredentials getCredentials() {
		return credentials;
	}

	@Override
	public IStatus open(Shell shell, AuthenticationRequest<AuthenticationType<UserCredentials>> request) {
		CredentialsDialog dialog = new CredentialsDialog(shell, Mode.USER);

		UserCredentials oldCredentials = request.getLocation().getCredentials(request.getAuthenticationType());
		if (oldCredentials != null) {
			dialog.setUsername(oldCredentials.getUserName());
			dialog.setDomain((oldCredentials.getDomain() != null) ? oldCredentials.getDomain() : ""); //$NON-NLS-1$
			dialog.setPassword(oldCredentials.getPassword());
			dialog.setSavePassword(oldCredentials.getSavePassword());
		}

		if (request instanceof UserAuthenticationRequest) {
			dialog.setNeedsDomain(((UserAuthenticationRequest) request).needsDomain());
		}

		dialog.create();

		dialog.setTitle(NLS.bind(Messages.UserCredentialsProviderUi_Credentials_for, request.getLocation().getLabel()));
		// caller provided message takes precedence
		if (request.getMessage() != null) {
			dialog.setMessage(request.getMessage());
		} else {
			dialog.setMessage(getDefaultMessage(request));
		}

		int resultCode = dialog.open();
		if (resultCode == Window.OK) {
			credentials = new UserCredentials(dialog.getUserName(), dialog.getPassword(), dialog.getDomain(),
					dialog.getSavePassword());
			request.getLocation().setCredentials(request.getAuthenticationType(), credentials);
			if (request.getAuthenticationType() == AuthenticationType.REPOSITORY) {
				request.getLocation().setUserName(dialog.getUserName());
			}
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}

	private String getDefaultMessage(AuthenticationRequest<AuthenticationType<UserCredentials>> request) {
		AuthenticationType<UserCredentials> authType = request.getAuthenticationType();
		if (AuthenticationType.REPOSITORY == authType) {
			return Messages.UserCredentialsProviderUi_Enter_repository_credentials;
		} else if (AuthenticationType.HTTP == authType) {
			return Messages.UserCredentialsProviderUi_Enter_HTTP_credentials;
		} else if (AuthenticationType.PROXY == authType) {
			return Messages.UserCredentialsProviderUi_Enter_proxy_credentials;
		}
		return null;
	}

}
