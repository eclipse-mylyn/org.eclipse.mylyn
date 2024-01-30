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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.OpenIdAuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.OpenIdCredentials;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Steffen Pingel
 */
public class OpenIdCredentialsProviderUi extends AbstractCredentialsProviderUi<OpenIdCredentials> {

	protected OpenIdCredentials credentials;

	@Override
	public IStatus open(Shell parentShell, AuthenticationRequest<AuthenticationType<OpenIdCredentials>> authRequest) {
		if (!(authRequest instanceof final OpenIdAuthenticationRequest request)) {
			throw new IllegalArgumentException(
					"Extected instanceof OpenIdAuthenticationRequest, got " + authRequest.getClass()); //$NON-NLS-1$
		}
		final WebBrowserDialog dialog = new WebBrowserDialog(WorkbenchUtil.getShell(),
				Messages.OpenIdCredentialsProviderUi_Login, null,
				Messages.OpenIdCredentialsProviderUi_Login_to_OpenID_Provider, MessageDialog.NONE,
				new String[] { IDialogConstants.CANCEL_LABEL }, 0);
		dialog.create();

		dialog.getBrowser().addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				if (event.location != null && event.location.startsWith(request.getReturnUrl())) {
					credentials = new OpenIdCredentials(event.location, null);
				}
				// alternatively check cookies since IE does not notify listeners of redirects
				String value = Browser.getCookie(request.getCookie(), request.getCookieUrl());
				if (value != null) {
					credentials = new OpenIdCredentials(event.location, value);
				}
				if (credentials != null) {
					event.doit = false;
					// delay execution to avoid IE crash
					dialog.getBrowser().getDisplay().asyncExec(() -> {
						if (dialog.getShell() != null && !dialog.getShell().isDisposed()) {
							dialog.close();
						}
					});
				}
			}
		});

		// navigate to login page
		dialog.getBrowser().setUrl(request.getRequestUrl() + "?" + getRequestParameters(request)); //$NON-NLS-1$

		if (dialog.open() == Window.OK) {
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}

	private String getRequestParameters(final OpenIdAuthenticationRequest request) {
		final StringBuilder sb = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : request.getProviderArgs().entrySet()) {
				if (sb.length() > 0) {
					sb.append("&"); //$NON-NLS-1$
				}
				sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")); //$NON-NLS-1$
				sb.append("="); //$NON-NLS-1$
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //$NON-NLS-1$
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	@Override
	public OpenIdCredentials getCredentials() {
		return credentials;
	}

}
