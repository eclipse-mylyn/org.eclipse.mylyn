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
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientManager implements ITaskRepositoryListener {

	private Map<String, BugzillaClient> clientByUrl = new HashMap<String, BugzillaClient>();

	public BugzillaClientManager() {
	}

	public synchronized BugzillaClient getClient(TaskRepository taskRepository) throws MalformedURLException, CoreException {
		BugzillaClient client = clientByUrl.get(taskRepository.getRepositoryUrl());
		if (client == null) {

			String htUser = taskRepository.getHttpUser() != null ? taskRepository.getHttpUser() : "";
			String htPass = taskRepository.getHttpPassword() != null ? taskRepository.getHttpPassword() : "";

			String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
			if (language == null || language.equals("")) {
				language = IBugzillaConstants.DEFAULT_LANG;
			}
			BugzillaLanguageSettings languageSettings = BugzillaCorePlugin.getDefault().getLanguageSetting(language);

			client = BugzillaClientFactory.createClient(taskRepository.getRepositoryUrl(), taskRepository.getUserName(),
					taskRepository.getPassword(), htUser, htPass, taskRepository.getProxy(),
					taskRepository.getCharacterEncoding(), taskRepository.getProperties(), languageSettings);
			clientByUrl.put(taskRepository.getRepositoryUrl(), client);
			client.setRepositoryConfiguration(BugzillaCorePlugin.getRepositoryConfiguration(taskRepository, false));
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
