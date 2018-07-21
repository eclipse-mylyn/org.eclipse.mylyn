/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientManager implements IRepositoryListener {

	private final Map<String, BugzillaClient> clientByUrl = new HashMap<String, BugzillaClient>();

	private final BugzillaRepositoryConnector connector;

	public BugzillaClientManager(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	public BugzillaClient getClient(TaskRepository taskRepository, IProgressMonitor monitor) throws CoreException {
		BugzillaClient client;
		synchronized (clientByUrl) {
			client = clientByUrl.get(taskRepository.getRepositoryUrl());
			if (client == null) {
				String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
				if (language == null || language.equals("")) { //$NON-NLS-1$
					language = IBugzillaConstants.DEFAULT_LANG;
				}
				try {
					client = createClient(taskRepository);
				} catch (MalformedURLException e) {
					throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
							"Malformed Repository Url", e)); //$NON-NLS-1$
				}
				clientByUrl.put(taskRepository.getRepositoryUrl(), client);
			}
			RepositoryConfiguration config = connector.getRepositoryConfiguration(taskRepository.getUrl());
			client.setRepositoryConfiguration(config);
		}
		return client;
	}

	protected BugzillaClient createClient(TaskRepository taskRepository) throws MalformedURLException {
		return BugzillaClientFactory.createClient(taskRepository, connector);
	}

	public void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		removeClient(repository);
	}

	public void repositoryRemoved(TaskRepository repository) {
		removeClient(repository);
	}

	private void removeClient(TaskRepository repository) {
		synchronized (clientByUrl) {
			clientByUrl.remove(repository.getRepositoryUrl());
		}
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		removeClient(repository);
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

}
