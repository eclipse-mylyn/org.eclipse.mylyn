/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UnavailableException;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.commons.repositories.core.RepositoriesCoreInternal;
import org.eclipse.osgi.util.NLS;

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
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN, NLS.bind(
					"Credential store not available with ID {0}", id), e)); //$NON-NLS-1$
			page.setErrorMessage(Messages.RepositoryUiUtil_secure_storage_unavailable);
		}
	}
}
