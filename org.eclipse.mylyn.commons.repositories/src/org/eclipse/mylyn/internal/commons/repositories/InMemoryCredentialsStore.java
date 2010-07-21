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

package org.eclipse.mylyn.internal.commons.repositories;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.auth.ICredentialsStore;

/**
 * @author Steffen Pingel
 */
public class InMemoryCredentialsStore implements ICredentialsStore {

	private final ConcurrentHashMap<String, Object> store;

	public InMemoryCredentialsStore() {
		store = new ConcurrentHashMap<String, Object>();
	}

	public void clear() {
		store.clear();
	}

	public void flush() throws IOException {
		// does nothing
	}

	public String get(String key, String def) throws StorageException {
		String value = (String) store.get(key);
		return (value != null) ? value : def;
	}

	public byte[] getByteArray(String key, byte[] def) throws StorageException {
		byte[] value = (byte[]) store.get(key);
		return (value != null) ? value : def;
	}

	public String[] keys() {
		return store.keySet().toArray(new String[0]);
	}

	public void put(String key, String value, boolean encrypt) throws StorageException {
		store.put(key, value);
	}

	public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException {
		store.put(key, value);
	}

	public void remove(String key) {
		store.remove(key);
	}

}
