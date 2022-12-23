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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Reads all data from the old "org.eclipse.mylyn.tasks.core" secure store node and writes it to the
 * {@link ICredentialsStore} provided by the {@link ILocationService}.
 * 
 * @author Sam Davis
 */
public class TaskRepositorySecureStoreMigrator {
	private final ILocationService service = LocationService.getDefault();

	public void migrateCredentials(Collection<TaskRepository> repositories) {
		if (!SecurePreferencesFactory.getDefault().nodeExists(ITasksCoreConstants.ID_PLUGIN)) {
			// check that the old node exists so that we don't create an empty node
			return;
		}
		StatusHandler.log(new Status(IStatus.INFO, ITasksCoreConstants.ID_PLUGIN,
				"Migrating task repository credentials from old secure store node.")); //$NON-NLS-1$
		Set<String> repositoryUrls = getRepositoryUrls(repositories);
		ISecurePreferences oldRootNode = SecurePreferencesFactory.getDefault().node(ITasksCoreConstants.ID_PLUGIN);
		for (String child : oldRootNode.childrenNames()) {
			ISecurePreferences repositoryNode = oldRootNode.node(child);
			String repositoryUrl = EncodingUtils.decodeSlashes(repositoryNode.name());
			if (repositoryUrls.contains(repositoryUrl)) {
				ICredentialsStore store = service.getCredentialsStore(repositoryUrl);
				for (String key : repositoryNode.keys()) {
					try {
						String value = repositoryNode.get(key, null);
						store.put(key, value, repositoryNode.isEncrypted(key));
					} catch (StorageException e) {
						StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
								"Error migrating secure store credentials for " + repositoryUrl, e)); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private Set<String> getRepositoryUrls(Collection<TaskRepository> repositories) {
		Set<String> repositoryUrls = new HashSet<String>();
		for (TaskRepository taskRepository : repositories) {
			repositoryUrls.add(taskRepository.getRepositoryUrl());
		}
		return repositoryUrls;
	}
}
