/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * Reads properties from the deprecated Eclipse keyring and writes them to the {@link ICredentialsStore} provided by the
 * {@link ILocationService}.
 *
 * @author Sam Davis
 */
public abstract class KeyringMigrator<T> {
	protected final ILocationService service = LocationService.getDefault();

	protected final String authRealm;

	protected final String authScheme;

	public KeyringMigrator(String authRealm, String authScheme) {
		this.authRealm = authRealm;
		this.authScheme = authScheme;
	}

	/**
	 * Migrate credentials for the given locations
	 */
	public void migrateCredentials(Collection<T> locations) {
		for (T location : locations) {
			migrateCredentials(location);
		}
	}

	protected void migrateCredentials(T location) {
		try {
			Map<String, String> properties = getAuthorizationInfo(getUrl(location));
			putProperties(properties, location);
		} catch (MalformedURLException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Error migrating keyring credentials for " + getUrl(location), e)); //$NON-NLS-1$
		}
	}

	protected Map<String, String> getAuthorizationInfo(String url) throws MalformedURLException {
		return getAuthorizationInfo(new URL(url), authRealm, authScheme);
	}

	@SuppressWarnings("deprecation")
	protected Map<String, String> getAuthorizationInfo(URL url, String realm, String scheme)
			throws MalformedURLException {
		return Platform.getAuthorizationInfo(url, realm, scheme);
	}

	protected void putProperties(Map<String, String> properties, T location) {
		if (properties != null) {
			ICredentialsStore store = service.getCredentialsStore(getUrl(location));
			for (Entry<String, String> entry : properties.entrySet()) {
				putKeyValue(location, entry.getKey(), entry.getValue(), store);
			}
		}
	}

	protected void putKeyValue(T location, String key, String value, ICredentialsStore store) {
		store.put(key, value, key.endsWith(".password")); //$NON-NLS-1$
	}

	protected abstract String getUrl(T location);
}
