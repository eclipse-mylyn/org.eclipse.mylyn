/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.core;

import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;

/**
 * @author Steffen Pingel
 */
public class SecureCredentialsStore implements ICredentialsStore {

	private static final String ID_NODE = "org.eclipse.mylyn.commons.repository"; //$NON-NLS-1$

	private final String id;

	private InMemoryCredentialsStore inMemoryStore;

	private boolean loggedStorageException;

	public SecureCredentialsStore(String id) {
		this.id = id;
	}

	public void clear() {
		//getSecurePreferences().clear();
		getSecurePreferences().removeNode();
		getInMemoryStore().clear();
	}

	public synchronized void copyTo(ICredentialsStore target) {
		Assert.isNotNull(target);
		if (!(target instanceof SecureCredentialsStore)) {
			throw new IllegalArgumentException(
					"SecureCredentialsStore may only by copied to stores of the same type: " + target.getClass()); //$NON-NLS-1$
		}

		ISecurePreferences preferences = getSecurePreferences();
		for (String key : preferences.keys()) {
			try {
				String value = preferences.get(key, null);
				boolean encrypted = preferences.isEncrypted(key);
				target.put(key, value, encrypted);
			} catch (StorageException e) {
				handle(e);
			}
		}
		if (inMemoryStore != null) {
			inMemoryStore.copyTo(target);
		}
	}

	public void flush() throws IOException {
		getSecurePreferences().flush();
	}

	public String get(final String key, final String def) {
		InMemoryCredentialsStore memoryStore = getInMemoryStore();
		synchronized (memoryStore) {
			if (memoryStore.hasKey(key)) {
				return memoryStore.get(key, def);
			}
		}
		try {
			return getSecurePreferences().get(key, def);
		} catch (StorageException e) {
			handle(e);
			return memoryStore.get(key, def);
		}
	}

	public boolean getBoolean(String key, boolean def) {
		InMemoryCredentialsStore memoryStore = getInMemoryStore();
		synchronized (memoryStore) {
			if (memoryStore.hasKey(key)) {
				return memoryStore.getBoolean(key, def);
			}
		}
		try {
			return getSecurePreferences().getBoolean(key, def);
		} catch (StorageException e) {
			handle(e);
			return memoryStore.getBoolean(key, def);
		}
	}

	public byte[] getByteArray(String key, byte[] def) {
		InMemoryCredentialsStore memoryStore = getInMemoryStore();
		synchronized (memoryStore) {
			if (memoryStore.hasKey(key)) {
				return memoryStore.getByteArray(key, def);
			}
		}
		try {
			return getSecurePreferences().getByteArray(key, def);
		} catch (StorageException e) {
			handle(e);
			return memoryStore.getByteArray(key, def);
		}
	}

	public String getId() {
		return id;
	}

	public String[] keys() {
		return getSecurePreferences().keys();
	}

	public void put(String key, String value, boolean encrypt) {
		put(key, value, encrypt, true);
	}

	public void put(String key, String value, boolean encrypt, boolean persist) {
		if (persist) {
			try {
				getSecurePreferences().put(key, value, encrypt);
				getInMemoryStore().remove(key);
			} catch (StorageException e) {
				handle(e);
				getInMemoryStore().put(key, value, encrypt, true);
			}
		} else {
			getInMemoryStore().put(key, value, encrypt, false);
			getSecurePreferences().remove(key);
		}
	}

	public void putBoolean(String key, boolean value, boolean encrypt) {
		try {
			getSecurePreferences().putBoolean(key, value, encrypt);
			getInMemoryStore().remove(key);
		} catch (StorageException e) {
			handle(e);
			getInMemoryStore().putBoolean(key, value, encrypt);
		}
	}

	public void putByteArray(String key, byte[] value, boolean encrypt) {
		try {
			getSecurePreferences().putByteArray(key, value, encrypt);
			getInMemoryStore().remove(key);
		} catch (StorageException e) {
			handle(e);
			getInMemoryStore().putByteArray(key, value, encrypt);
		}
	}

	public void remove(String key) {
		getSecurePreferences().remove(key);
	}

	protected synchronized InMemoryCredentialsStore getInMemoryStore() {
		if (inMemoryStore == null) {
			inMemoryStore = InMemoryCredentialsStore.getStore(id);
		}
		return inMemoryStore;
	}

	protected ISecurePreferences getSecurePreferences() {
		ISecurePreferences securePreferences = openSecurePreferences().node(ID_NODE);
		securePreferences = securePreferences.node(EncodingUtils.encodeSlashes(getId()));
		return securePreferences;
	}

	protected ISecurePreferences openSecurePreferences() {
		return SecurePreferencesFactory.getDefault();
	}

	private void handle(StorageException e) {
		if (!loggedStorageException) {
			loggedStorageException = true;
			StatusHandler.log(new Status(
					IStatus.ERROR,
					RepositoriesCoreInternal.ID_PLUGIN,
					"Unexpected error accessing secure storage, falling back to in memory store for credentials. Some credentials may not be saved.", //$NON-NLS-1$
					e));
		}
	}

}
