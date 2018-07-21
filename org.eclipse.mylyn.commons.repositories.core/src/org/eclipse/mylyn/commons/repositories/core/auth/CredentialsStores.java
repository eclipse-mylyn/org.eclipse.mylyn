/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;

/**
 * Utility for accessing {@link ICredentialsStore}
 * 
 * @author David Green
 * @since 1.1
 */
public class CredentialsStores {

	private CredentialsStores() {
	}

	/**
	 * Create an ICredentialsStore that is isolated and transient. The returned instance is suitable for use when
	 * creating a {@link RepositoryLocation} that should not persist changes to its credentials.
	 */
	public static ICredentialsStore createInMemoryStore() {
		return new InMemoryCredentialsStore();
	}

	/**
	 * get the default credentials store
	 * 
	 * @see ILocationService#getCredentialsStore(String)
	 */
	public static ICredentialsStore getDefaultCredentialsStore(String id) {
		return LocationService.getDefault().getCredentialsStore(id);
	}
}
