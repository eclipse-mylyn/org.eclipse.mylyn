/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 * @since 3.8
 */
public abstract class RepositoryClientManager<T, C extends Serializable> implements IRepositoryListener {

	/**
	 * Delegates to <code>repositoryConfigurationClass</code>'s class loader for accessing classes.
	 */
	private class OsgiAwareObjectInputStream extends ObjectInputStream {

		public OsgiAwareObjectInputStream(InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			try {
				return Class.forName(desc.getName(), true, repositoryConfigurationClass.getClassLoader());
			} catch (Exception e) {
				return super.resolveClass(desc);
			}
		}

	}

	private final Map<String, T> clientByUrl = new HashMap<String, T>();

	private final Map<String, C> respoitoryConfigurationByUrl = new HashMap<String, C>();

	private final File cacheFile;

	private TaskRepositoryLocationFactory locationFactory;

	private final Class<C> repositoryConfigurationClass;

	public RepositoryClientManager(File cacheFile, Class<C> repositoryConfigurationClass) {
		Assert.isNotNull(cacheFile);
		this.cacheFile = cacheFile;
		this.repositoryConfigurationClass = repositoryConfigurationClass;
		readCache();
	}

	public synchronized T getClient(TaskRepository repository) {
		Assert.isNotNull(repository);
		T client = clientByUrl.get(repository.getRepositoryUrl());
		if (client == null) {
			C data = respoitoryConfigurationByUrl.get(repository.getRepositoryUrl());
			if (data == null) {
				data = createRepositoryConfiguration(repository);
				respoitoryConfigurationByUrl.put(repository.getRepositoryUrl(), data);
			}

			client = createClient(repository, data);
			clientByUrl.put(repository.getRepositoryUrl(), client);
		}
		return client;
	}

	protected C createRepositoryConfiguration(TaskRepository repository) {
		try {
			return repositoryConfigurationClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract T createClient(TaskRepository taskRepository, C data);

	public void repositoriesRead() {
		// ignore
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		removeClient(repository);
		respoitoryConfigurationByUrl.remove(repository.getRepositoryUrl());
	}

	private void removeClient(TaskRepository repository) {
		clientByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		removeClient(repository);
		respoitoryConfigurationByUrl.remove(repository.getRepositoryUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		removeClient(repository);
	}

	@SuppressWarnings("unchecked")
	public void readCache() {
		if (cacheFile == null || !cacheFile.exists()) {
			return;
		}

		ObjectInputStream in = null;
		try {
			in = new OsgiAwareObjectInputStream(new FileInputStream(cacheFile));
			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				String url = (String) in.readObject();
				C data = (C) in.readObject();
				if (url != null && data != null) {
					respoitoryConfigurationByUrl.put(url, data);
				}
			}
		} catch (Throwable e) {
			handleError("The respository configuration cache could not be read", e); //$NON-NLS-1$
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

	protected void handleError(String message, Throwable e) {
		StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, message, e));
	}

	public void writeCache() {
		if (cacheFile == null) {
			return;
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(cacheFile));
			out.writeInt(respoitoryConfigurationByUrl.size());
			for (String url : respoitoryConfigurationByUrl.keySet()) {
				out.writeObject(url);
				out.writeObject(respoitoryConfigurationByUrl.get(url));
			}
		} catch (IOException e) {
			handleError("The respository configuration cache could not be written", e); //$NON-NLS-1$
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

	public TaskRepositoryLocationFactory getLocationFactory() {
		return locationFactory;
	}

	public void setLocationFactory(TaskRepositoryLocationFactory locationFactory) {
		this.locationFactory = locationFactory;
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

}
