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

package org.eclipse.mylyn.internal.tasks.core.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Extends {@link KeyringMigrator} to migrate TaskRepository credentials, including usernames which are stored in the
 * repository properties.
 * 
 * @author Sam Davis
 */
public class TaskRepositoryKeyringMigrator extends KeyringMigrator<TaskRepository> {
	private static final String KEY_USERNAME = "org.eclipse.mylyn.tasklist.repositories.username"; //$NON-NLS-1$

	protected static URL defaultUrl;
	static {
		try {
			defaultUrl = new URL("http://eclipse.org/mylyn"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public TaskRepositoryKeyringMigrator(String authRealm, String authScheme) {
		super(authRealm, authScheme);
	}

	@Override
	public void migrateCredentials(Collection<TaskRepository> locations) {
		StatusHandler.log(new Status(IStatus.INFO, ITasksCoreConstants.ID_PLUGIN,
				"Migrating task repository credentials from keyring.")); //$NON-NLS-1$
		super.migrateCredentials(locations);
	}

	@Override
	protected void migrateCredentials(TaskRepository location) {
		super.migrateCredentials(location);
		// clear the cachedUserName in case it was set before the migration ran
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			location.setCredentials(AuthenticationType.REPOSITORY, credentials,
					location.getSavePassword(AuthenticationType.REPOSITORY));
		}
	}

	@Override
	protected Map<String, String> getAuthorizationInfo(String url) throws MalformedURLException {
		try {
			return super.getAuthorizationInfo(url);
		} catch (MalformedURLException e) {
			return Platform.getAuthorizationInfo(defaultUrl, url, authScheme);
		}
	}

	@Override
	protected String getUrl(TaskRepository location) {
		return location.getRepositoryUrl();
	}

	@Override
	protected void putKeyValue(TaskRepository location, String key, String value, ICredentialsStore store) {
		if (KEY_USERNAME.equals(key)) {
			location.setProperty(key, value);
		} else {
			super.putKeyValue(location, key, value, store);
		}
	}
}
