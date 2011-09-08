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

package org.eclipse.mylyn.internal.gerrit.ui;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.IOpenIdLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.OpenIdAuthenticationRequest;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.WebBrowserDialog;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryLocationUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
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
	public String requestAuthentication(OpenIdAuthenticationRequest request) {
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

	private String showAuthenticationDialog(final String repositoryUrl, final OpenIdAuthenticationRequest request) {
		final StringBuilder sb = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : request.getProviderArgs().entrySet()) {
				sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")); //$NON-NLS-1$
				sb.append("="); //$NON-NLS-1$
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //$NON-NLS-1$
				sb.append("&"); //$NON-NLS-1$
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		final AtomicReference<String> result = new AtomicReference<String>();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				final WebBrowserDialog dialog = new WebBrowserDialog(WorkbenchUtil.getShell(), "Login", null,
						"Login to OpenID Provider", MessageDialog.NONE, new String[] { IDialogConstants.CANCEL_LABEL },
						0);
				dialog.create();

				// TODO e3.6 replace reflection with call to setUrl(...)
				Method method;
				try {
					method = Browser.class.getDeclaredMethod("setUrl", String.class, String.class, String[].class);
					method.invoke(dialog.getBrowser(), request.getRequestUrl(), sb.toString(), null);
				} catch (Exception e) {
					// POST API not available, fall-back to alternate URL
					dialog.getBrowser().setUrl(request.getAlternateUrl());
				}
				dialog.getBrowser().addLocationListener(new LocationAdapter() {
					@Override
					public void changing(LocationEvent event) {
						if (event.location != null && event.location.startsWith(request.getReturnUrl())) {
							result.set(event.location);
							event.doit = false;
							dialog.close();
						}
					}
				});
				dialog.open();
			}
		});
		return result.get();
	}
}
