/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
import org.eclipse.swt.widgets.Display;

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
		boolean acquiredLock = false;
		ILock lock = getSecurePreferencesRootLock();
		try {
			if (lock != null && lock.getDepth() > 0) {
				// wait and try one more time in case another thread was retrieving the master password from the cache
				sleep(200);
				if (lock.getDepth() > 0) {
					acquiredLock = acquire(lock);
					// if we acquired the lock, either the thread that alreadly held it is the current thread, or it was released 
					// in either case, we can safely proceed
					if (!acquiredLock) {
						throw new RuntimeException("Aborting request to prevent deadlock accessing secure storage"); //$NON-NLS-1$
					}
				}
			}
			return super.getSecurePreferences();
		} finally {
			if (lock != null && acquiredLock) {
				lock.release();
			}
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
	}

	/**
	 * Check whether the current thread already holds the lock. This can only be true if we're on the main thread.
	 */
	private boolean acquire(ILock lock) {
		if (Display.getCurrent() != null) {
			try {
				return lock.acquire(1);
			} catch (InterruptedException e) {
				StatusHandler.log(new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}
		return false;
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
