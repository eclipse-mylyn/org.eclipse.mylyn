/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Caches {@link ITracClient} objects.
 * 
 * @author Steffen Pingel
 */
public class TracClientManager implements ITaskRepositoryListener {

	private Map<String, ITracClient> clientByUrl = new HashMap<String, ITracClient>();

	private Map<String, TracClientData> clientDataByUrl = new HashMap<String, TracClientData>();

	private File cacheFile;

	public TracClientManager(File cacheFile) {
		this.cacheFile = cacheFile;

		readCache();
	}

	public synchronized ITracClient getRepository(TaskRepository taskRepository) throws MalformedURLException {
		ITracClient repository = clientByUrl.get(taskRepository.getUrl());
		if (repository == null) {
			repository = TracClientFactory.createClient(taskRepository.getUrl(),
					Version.fromVersion(taskRepository.getVersion()), taskRepository.getUserName(),
					taskRepository.getPassword(), taskRepository.getProxy());
			clientByUrl.put(taskRepository.getUrl(), repository);

			TracClientData data = clientDataByUrl.get(taskRepository.getUrl());
			if (data == null) {
				data = new TracClientData();
				clientDataByUrl.put(taskRepository.getUrl(), data);
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
		clientByUrl.remove(repository.getUrl());
		clientDataByUrl.remove(repository.getUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
		clientDataByUrl.remove(repository.getUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
		// if url is changed a stale data object will be left in
		// clientDataByUrl, bug #149939
	}

	@SuppressWarnings("unchecked")
	public void readCache() {
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
			TracCorePlugin.log(e);
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

	public void writeCache() {
		if (cacheFile == null) {
			return;
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(cacheFile));
			out.writeInt(clientDataByUrl.size());
			for (String url : clientDataByUrl.keySet()) {
				out.writeObject(url);
				out.writeObject(clientDataByUrl.get(url));
			}
		} catch (IOException e) {
			TracCorePlugin.log(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

}
