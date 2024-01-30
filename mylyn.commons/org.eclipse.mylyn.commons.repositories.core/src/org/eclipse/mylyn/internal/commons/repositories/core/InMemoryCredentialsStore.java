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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UnavailableException;

/**
 * @author Steffen Pingel
 */
public class InMemoryCredentialsStore implements ICredentialsStore {

	private static class Item {

		boolean encrypted;

		boolean persisted;

		Object value;

		Class<?> type;

	}

	static Map<String, InMemoryCredentialsStore> storeById = new HashMap<>();

	public synchronized static InMemoryCredentialsStore getStore(String id) {
		Assert.isNotNull(id);
		InMemoryCredentialsStore store = storeById.get(id);
		if (store == null) {
			store = new InMemoryCredentialsStore(id);
			storeById.put(id, store);
		}
		return store;
	}

	private final String id;

	private final ICredentialsStore parent;

	/**
	 * Protected by <code>this</code>.
	 */
	private final Map<String, Item> store;

	/**
	 * Flag that controls if removed values are set to null or if the key is removed. This affects the behavior of
	 * {@link #copyTo(ICredentialsStore)}. If the flag is set to <code>true</code> removed keys will also be removed in target store.
	 */
	private final boolean keepRemovedKeys;

	public InMemoryCredentialsStore() {
		this(null, null, true);
	}

	public InMemoryCredentialsStore(ICredentialsStore parent) {
		this(parent, null, true);
	}

	InMemoryCredentialsStore(ICredentialsStore parent, String id, boolean keepRemovedKeys) {
		this.parent = parent;
		this.id = id;
		this.keepRemovedKeys = keepRemovedKeys;
		store = new HashMap<>();
	}

	InMemoryCredentialsStore(String id) {
		this(null, id, false);
	}

	@Override
	public synchronized void clear() {
		store.clear();
	}

	@Override
	public void copyTo(ICredentialsStore target) {
		synchronized (target) {
			synchronized (this) {
				for (Map.Entry<String, Item> entry : store.entrySet()) {
					Item item = entry.getValue();
					if (item != null) {
						if (item.type == String.class) {
							target.put(entry.getKey(), (String) item.value, item.encrypted, item.persisted);
						} else if (item.type == byte[].class) {
							target.putByteArray(entry.getKey(), (byte[]) item.value, item.encrypted);
						} else if (item.type == boolean.class) {
							target.putBoolean(entry.getKey(), (Boolean) item.value, item.encrypted);
						}
					} else {
						target.remove(entry.getKey());
					}
				}
			}
		}
	}

	/**
	 * Clears removed items.
	 */
	@Override
	public synchronized void flush() throws IOException {
		for (Iterator<Map.Entry<String, Item>> it = store.entrySet().iterator(); it.hasNext();) {
			if (it.next().getValue() == null) {
				it.remove();
			}
		}
	}

	@Override
	public synchronized String get(String key, String def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.get(key, def);
		}
		return item != null && item.type == String.class ? (String) item.value : def;
	}

	@Override
	public synchronized boolean getBoolean(String key, boolean def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.getBoolean(key, def);
		}
		return item != null && item.type == boolean.class ? (Boolean) item.value : def;
	}

	@Override
	public synchronized byte[] getByteArray(String key, byte[] def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.getByteArray(key, def);
		}
		return item != null && item.type == byte[].class ? (byte[]) item.value : def;
	}

	public String getId() {
		return id;
	}

	public synchronized boolean hasKey(String key) {
		// store.containsKey(key) would return true for removed items that were set to null
		return store.get(key) != null;
	}

	@Override
	public synchronized String[] keys() {
		List<String> keys = new ArrayList<>(store.size());
		for (Entry<String, Item> entry : store.entrySet()) {
			if (entry.getValue() != null) {
				keys.add(entry.getKey());
			}
		}
		return keys.toArray(new String[0]);
	}

	@Override
	public synchronized void put(String key, String value, boolean encrypt) {
		put(key, value, encrypt, true);
	}

	@Override
	public synchronized void put(String key, String value, boolean encrypt, boolean persist) {
		store.put(key, createItem(String.class, value, encrypt, persist));
	}

	@Override
	public synchronized void putBoolean(String key, boolean value, boolean encrypt) {
		store.put(key, createItem(boolean.class, value, encrypt, true));
	}

	@Override
	public synchronized void putByteArray(String key, byte[] value, boolean encrypt) {
		store.put(key, createItem(byte[].class, value, encrypt, encrypt));
	}

	@Override
	public synchronized void remove(String key) {
		if (keepRemovedKeys) {
			store.put(key, null);
		} else {
			store.remove(key);
		}
	}

	@Override
	public void testAvailability() throws UnavailableException {
		// always available
	}

	private Item createItem(Class<?> type, Object value, boolean encrypt, boolean persist) {
		Item item = new Item();
		item.value = value;
		item.encrypted = encrypt;
		item.persisted = persist;
		item.type = type;
		return item;
	}

}
