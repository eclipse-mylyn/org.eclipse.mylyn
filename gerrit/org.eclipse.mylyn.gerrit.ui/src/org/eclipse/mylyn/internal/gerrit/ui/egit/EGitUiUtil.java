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

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;

/**
 * Provides common UI utilities.
 * 
 * @author Steffen Pingel
 */
public class EGitUiUtil {

	private static boolean credentialsProviderWarningLogged;

	public static void setCredentialsProvider(FetchOperationUI op) {
		// TODO EGit1.1 replace with op.setCredentialsProvider(new EGitCredentialsProvider())
		try {
			Class clazz = Class.forName("org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider");
			Method method = FetchOperationUI.class.getDeclaredMethod("setCredentialsProvider", clazz);
			method.invoke(op, clazz.newInstance());
		} catch (Exception e) {
			if (!credentialsProviderWarningLogged) {
				credentialsProviderWarningLogged = true;
				StatusHandler.log(new Status(
						IStatus.WARNING,
						GerritUiPlugin.PLUGIN_ID,
						"Fetch operation may fail: EGit credentials provider not available. EGit 1.1 or later is required.",
						e));
			}
		}
	}

}
