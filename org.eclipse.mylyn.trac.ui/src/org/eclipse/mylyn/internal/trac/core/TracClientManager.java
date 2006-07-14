/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Caches {@link ITracClient} objects.
 * 
 * @author Steffen Pingel
 */
public class TracClientManager implements ITaskRepositoryListener {

	Map<String, ITracClient> clientByUrl = new HashMap<String, ITracClient>();

	public TracClientManager() {
		TasksUiPlugin.getRepositoryManager().addListener(this);
	}

	public synchronized ITracClient getRepository(TaskRepository taskRepository) throws MalformedURLException {
		ITracClient repository = clientByUrl.get(taskRepository.getUrl());
		if (repository == null) {
			repository = TracClientFactory.createClient(taskRepository.getUrl(), Version.fromVersion(taskRepository
					.getVersion()), taskRepository.getUserName(), taskRepository.getPassword());
			// TODO read cached client attributes, see bug #150670
			clientByUrl.put(taskRepository.getUrl(), repository);
		}
		return repository;
	}

	public void repositoriesRead() {
		// ignore
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		clientByUrl.remove(repository.getUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
	}

}
