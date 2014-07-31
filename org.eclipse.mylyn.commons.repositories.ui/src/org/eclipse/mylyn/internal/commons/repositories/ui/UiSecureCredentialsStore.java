/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.equinox.internal.security.storage.SecurePreferencesRoot;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;

/**
 * Attempts to detect the deadlock that can occur when opening the secure storage (bug 440918) and fails preemptively.
 * 
 * @author Sam Davis
 */
public class UiSecureCredentialsStore extends SecureCredentialsStore {

	private static AtomicBoolean loggedDeadlockDetectionFailure = new AtomicBoolean();

	public UiSecureCredentialsStore(String id) {
		super(id);
	}

	@Override
	protected ISecurePreferences getSecurePreferences() {
		ILock lock = getSecurePreferencesRootLock();
		if (lock != null && lock.getDepth() > 0) {
			try {
				Thread.sleep(200);// wait and try one more time
			} catch (InterruptedException e) {
				StatusHandler.log(new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
			if (lock.getDepth() > 0) {
				throw new RuntimeException("Aborting request to prevent deadlock accessing secure storage"); //$NON-NLS-1$
			}
		}
		return super.getSecurePreferences();
	}

	protected static ILock getSecurePreferencesRootLock() {
		try {
			@SuppressWarnings("restriction")
			Field lockField = SecurePreferencesRoot.class.getDeclaredField("lock"); //$NON-NLS-1$
			lockField.setAccessible(true);
			return (ILock) lockField.get(null);
		} catch (Exception e) {
			if (!loggedDeadlockDetectionFailure.getAndSet(true)) {// log only once per session
				StatusHandler.log(new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN,
						"Deadlock detection failed", e)); //$NON-NLS-1$
			}
		}
		return null;
	}
}
