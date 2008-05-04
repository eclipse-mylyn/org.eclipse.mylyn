/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryAdapter;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientManager extends TaskRepositoryAdapter {

	private Map<String, BugzillaClient> clientByUrl = new HashMap<String, BugzillaClient>();

	public BugzillaClientManager() {
	}

	public synchronized BugzillaClient getClient(TaskRepository taskRepository, IProgressMonitor monitor) throws MalformedURLException, CoreException {
		BugzillaClient client = clientByUrl.get(taskRepository.getRepositoryUrl());
		if (client == null) {

			String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
			if (language == null || language.equals("")) {
				language = IBugzillaConstants.DEFAULT_LANG;
			}
			client = BugzillaClientFactory.createClient(taskRepository);
			clientByUrl.put(taskRepository.getRepositoryUrl(), client);
			client.setRepositoryConfiguration(BugzillaCorePlugin.getRepositoryConfiguration(taskRepository, false, monitor));
		}
		return client;
	}

	public void repositoriesRead() {
		// ignore
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		clientByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		clientByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		clientByUrl.remove(repository.getRepositoryUrl());
	}
}
