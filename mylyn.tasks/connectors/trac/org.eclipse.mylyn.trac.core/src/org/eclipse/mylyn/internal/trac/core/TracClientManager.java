/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracClientData;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * Caches {@link ITracClient} objects.
 *
 * @author Steffen Pingel
 */
public class TracClientManager implements IRepositoryListener {

	private final Map<String, ITracClient> clientByUrl = new HashMap<String, ITracClient>();

	private final Map<String, TracClientData> clientDataByUrl = new HashMap<String, TracClientData>();

	private final File cacheFile;

	private volatile TaskRepositoryLocationFactory taskRepositoryLocationFactory;

	public TracClientManager(File cacheFile, TaskRepositoryLocationFactory taskRepositoryLocationFactory) {
		this.cacheFile = cacheFile;
		this.taskRepositoryLocationFactory = taskRepositoryLocationFactory;

		readCache();
	}

	public synchronized ITracClient getTracClient(TaskRepository taskRepository) {
		ITracClient repository = clientByUrl.get(taskRepository.getRepositoryUrl());
		if (repository == null) {
			AbstractWebLocation location = taskRepositoryLocationFactory.createWebLocation(taskRepository);
			repository = TracClientFactory.createClient(location, Version.fromVersion(taskRepository.getVersion()));
			clientByUrl.put(taskRepository.getRepositoryUrl(), repository);

			TracClientData data = clientDataByUrl.get(taskRepository.getRepositoryUrl());
			if (data == null) {
				data = new TracClientData();
				clientDataByUrl.put(taskRepository.getRepositoryUrl(), data);
			}
			repository.setData(data);
		}
		return repository;
	}

	public void repositoriesRead() {
		// ignore
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		removeClient(repository);
		clientDataByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void clearClients() {
		clientByUrl.clear();
	}

	private synchronized void removeClient(TaskRepository repository) {
		clientByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		removeClient(repository);
		clientDataByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		removeClient(repository);
		// if url is changed a stale data object will be left in
		// clientDataByUrl, bug #149939
	}

	public synchronized void readCache() {
		if (cacheFile == null || !cacheFile.exists()) {
			return;
		}

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(cacheFile));
			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				String url = (String) in.readObject();
				TracClientData data = (TracClientData) in.readObject();
				if (url != null && data != null) {
					clientDataByUrl.put(url, data);
				}
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, TracCorePlugin.ID_PLUGIN,
					"The Trac respository configuration cache could not be read", e)); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

	}

	public synchronized void writeCache() {
		if (cacheFile == null) {
			return;
		}
		try (ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(cacheFile)))) {
			out.writeInt(clientDataByUrl.size());
			for (String url : clientDataByUrl.keySet()) {
				out.writeObject(url);
				out.writeObject(clientDataByUrl.get(url));
			}
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TracCorePlugin.ID_PLUGIN,
					"The Trac respository configuration cache could not be written", e)); //$NON-NLS-1$
		}
	}

	public TaskRepositoryLocationFactory getTaskRepositoryLocationFactory() {
		return taskRepositoryLocationFactory;
	}

	public void setTaskRepositoryLocationFactory(TaskRepositoryLocationFactory taskRepositoryLocationFactory) {
		this.taskRepositoryLocationFactory = taskRepositoryLocationFactory;
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

}
