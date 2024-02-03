/*******************************************************************************
 * Copyright (c) 2014, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UnavailableException;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.commons.repositories.core.RepositoriesCoreInternal;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

public class RepositoryUiUtil {

	private RepositoryUiUtil() {
	}

	/**
	 * Sets an error message on the given page if the store with the given ID is not available.
	 */
	public static void testCredentialsStore(String id, final DialogPage page) {
		ICredentialsStore store = LocationService.getDefault().getCredentialsStore(id);
		try {
			store.testAvailability();
		} catch (UnavailableException e) {
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN,
					NLS.bind("Credential store not available with ID {0}", id), e)); //$NON-NLS-1$
			page.setErrorMessage(Messages.RepositoryUiUtil_secure_storage_unavailable);
		}
	}

	public static <T> T adapt(Object sourceObject, Class<T> adapter) {
		try {
			if (PlatformUiUtil.isNeonOrLater()) {
				Bundle bundle = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
				Class<?> clazz = bundle.loadClass("org.eclipse.core.runtime.Adapters"); //$NON-NLS-1$
				Method adaptMethod = clazz.getMethod("adapt", Object.class, Class.class); //$NON-NLS-1$
				Object result = adaptMethod.invoke(clazz, sourceObject, adapter);
				return adapter.cast(result);
			} else {
				Bundle bundle = Platform.getBundle("org.eclipse.ui.workbench"); //$NON-NLS-1$
				Class<?> clazz = bundle.loadClass("org.eclipse.ui.internal.util.Util"); //$NON-NLS-1$
				Method adaptMethod = clazz.getMethod("getAdapter", Object.class, Class.class); //$NON-NLS-1$
				Object result = adaptMethod.invoke(clazz, sourceObject, adapter);
				return adapter.cast(result);
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesUiPlugin.ID_PLUGIN, "Could not adapt", e)); //$NON-NLS-1$
		}
		return null;
	}
}
