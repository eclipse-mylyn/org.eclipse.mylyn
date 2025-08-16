/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.browser.WebBrowserDialog;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.IOpenIdLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.OpenIdAuthenticationRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.OpenIdAuthenticationResponse;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryLocationUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * @author Steffen Pingel
 */
public class GerritRepositoryLocationUi extends TaskRepositoryLocationUi implements IOpenIdLocation {

	private static Object lock = new Object();

	private static volatile int version = 1;

	public GerritRepositoryLocationUi(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public String getProviderUrl() {
		if (Boolean.parseBoolean(taskRepository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_ENABLED))) {
			return taskRepository.getProperty(GerritConnector.KEY_REPOSITORY_OPEN_ID_PROVIDER);
		}
		return null;
	}

	@Override
	public OpenIdAuthenticationResponse requestAuthentication(OpenIdAuthenticationRequest request,
			IProgressMonitor monitor) throws UnsupportedRequestException {
		if (Policy.isBackgroundMonitor(monitor)) {
			throw new UnsupportedRequestException();
		}

		final String repositoryUrl = taskRepository.getUrl();

		int currentVersion = version;
		// synchronize on a static lock to ensure that only one password dialog is displayed at a time
		synchronized (lock) {
			// check if the credentials changed while the thread was waiting for the lock
			if (currentVersion != version) {
				// another password prompt was shown, exit to try again
				return null;
			}

			return showAuthenticationDialog(repositoryUrl, request);
		}
	}

	private OpenIdAuthenticationResponse showAuthenticationDialog(final String repositoryUrl,
			final OpenIdAuthenticationRequest request) {
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

		final AtomicReference<OpenIdAuthenticationResponse> result = new AtomicReference<>();
		Display.getDefault().syncExec(() -> {
			final WebBrowserDialog dialog = new WebBrowserDialog(WorkbenchUtil.getShell(),
					Messages.GerritRepositoryLocationUi_Login, null,
					Messages.GerritRepositoryLocationUi_Login_to_OpenID_Provider, MessageDialog.NONE,
					new String[] { IDialogConstants.CANCEL_LABEL }, 0) {
				@Override
				protected Point getInitialSize() {
					return new Point(780, 580);
				}
			};
			dialog.create();

			dialog.getBrowser().addLocationListener(new LocationAdapter() {
				@Override
				public void changing(LocationEvent event) {
					if (event.location != null && event.location.startsWith(request.getReturnUrl())) {
						result.set(new OpenIdAuthenticationResponse(event.location, null));
					}
					// alternatively check cookies since IE does not notify listeners of redirects
					String value = Browser.getCookie(request.getCookie(), request.getCookieUrl());
					if (value != null) {
						result.set(new OpenIdAuthenticationResponse(event.location, value));
					}
					if (result.get() != null) {
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
			dialog.getBrowser().setUrl(request.getRequestUrl() + "?" + sb.toString()); //$NON-NLS-1$
			dialog.open();
		});
		return result.get();
	}
}
