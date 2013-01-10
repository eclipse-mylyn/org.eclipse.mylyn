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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.mylyn.internal.commons.repositories.core.SecureCredentialsStore;
import org.eclipse.swt.widgets.Display;

/**
 * When this class is accessed on the UI thread, access to the secure store will occur on a background thread and the
 * event loop will continue to run. This prevents a deadlock that can occur when accessing the secure store on the UI
 * thread.
 * <p>
 * Clients should be extremely careful when accessing this class on the UI thread while holding onto a lock (such as
 * from within synchronized methods). It is possible that the event loop will process an event that spawns another job
 * (such as when the user opens a dialog which runs a background operation). If that job also tries to acquire the lock,
 * there will be a deadlock. For this reason, clients should never hold a lock while accessing this class on the UI
 * thread, unless they can be certain that nothing can cause the UI thread to wait for another job that attempts to
 * acquire the lock.
 * 
 * @author Sam Davis
 */
public class UiSecureCredentialsStore extends SecureCredentialsStore {

	public UiSecureCredentialsStore(String id) {
		super(id);
	}

	@Override
	protected ISecurePreferences getSecurePreferences() {
		if (Display.getCurrent() != null) {
			// ensure we don't open the secure preferences on the UI thread as this can cause deadlock
			final ISecurePreferences securePreferences[] = new ISecurePreferences[1];
			try {
				ModalContext.run(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						securePreferences[0] = UiSecureCredentialsStore.super.getSecurePreferences();
					}
				}, true, new NullProgressMonitor(), Display.getCurrent());
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return securePreferences[0];
		}
		return super.getSecurePreferences();
	}

	@Override
	protected ISecurePreferences openSecurePreferences() {
		Assert.isTrue(Display.getCurrent() == null);
		return super.openSecurePreferences();
	}
}
