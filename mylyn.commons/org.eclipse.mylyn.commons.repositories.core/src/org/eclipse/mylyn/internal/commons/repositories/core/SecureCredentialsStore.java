/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UnavailableException;

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

	@Override
	public void clear() {
		//getSecurePreferences().clear();
		getSecurePreferences().removeNode();
		getInMemoryStore().clear();
	}

	@Override
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

	@Override
	public void flush() throws IOException {
		getSecurePreferences().flush();
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public String[] keys() {
		return getSecurePreferences().keys();
	}

	@Override
	public void put(String key, String value, boolean encrypt) {
		put(key, value, encrypt, true);
	}

	@Override
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

	@Override
	public void putBoolean(String key, boolean value, boolean encrypt) {
		try {
			getSecurePreferences().putBoolean(key, value, encrypt);
			getInMemoryStore().remove(key);
		} catch (StorageException e) {
			handle(e);
			getInMemoryStore().putBoolean(key, value, encrypt);
		}
	}

	@Override
	public void putByteArray(String key, byte[] value, boolean encrypt) {
		try {
			getSecurePreferences().putByteArray(key, value, encrypt);
			getInMemoryStore().remove(key);
		} catch (StorageException e) {
			handle(e);
			getInMemoryStore().putByteArray(key, value, encrypt);
		}
	}

	@Override
	public void remove(String key) {
		getSecurePreferences().remove(key);
	}

	@Override
	public void testAvailability() throws UnavailableException {
		try {
			String key = "org.eclipse.mylyn.commons.repositories.core.SecureCredentialsStore"; //$NON-NLS-1$
			// in some cases, we can get the list of keys even though the secure store is broken, so if we just try to get
			// a non-existant key, it won't try to access the secure store and we won't detect that it's broken. So, create a key
			// and try to access it.
			getSecurePreferences().put(key, Boolean.toString(true), true);
			getSecurePreferences().get(key, null);
		} catch (StorageException e) {
			throw new UnavailableException(e);
		}
	}

	protected synchronized InMemoryCredentialsStore getInMemoryStore() {
		if (inMemoryStore == null) {
			inMemoryStore = InMemoryCredentialsStore.getStore(id);
		}
		return inMemoryStore;
	}

	protected ISecurePreferences getSecurePreferences() {
		ISecurePreferences securePreferences = openSecurePreferences().node(ID_NODE);
		securePreferences = securePreferences.node(getEncodedId());
		return securePreferences;
	}

	private String getEncodedId() {
		String id = getId();
		if (containsOnlyValidCharacters(id)) {
			// bug 429094: this was the format used before Mylyn 3.11 so we continue to use it where possible,
			// but we can't use it for URLs with characters outside the allowed range for the secure store
			return EncodingUtils.encodeSlashes(id);
		}
		try {
			return URLEncoder.encode(id, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// should never happen
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN, "Error encoding id", e)); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @return whether id contains only characters that are valid for a path in the secure storage
	 */
	private boolean containsOnlyValidCharacters(String id) {
		char[] chars = id.toCharArray();
		for (char c : chars) {
			if (c < 32 || c > 126) {
				return false;
			}
		}
		return true;
	}

	protected ISecurePreferences openSecurePreferences() {
		return SecurePreferencesFactory.getDefault();
	}

	private void handle(StorageException e) {
		if (!loggedStorageException) {
			loggedStorageException = true;
			StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN,
					"Unexpected error accessing secure storage, falling back to in memory store for credentials. Some credentials may not be saved.", //$NON-NLS-1$
					e));
		}
	}

}
