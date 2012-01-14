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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;

/**
 * @author Steffen Pingel
 */
public class InMemoryCredentialsStore implements ICredentialsStore {

	private static class Item {

		boolean encrypted;

		boolean persisted;

		Object value;

	}

	static Map<String, InMemoryCredentialsStore> storeById = new HashMap<String, InMemoryCredentialsStore>();

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
	 * {@link #copyTo(ICredentialsStore)}. If the flag is set to <code>true</code> removed keys will also be removed in
	 * target store.
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
		this.store = new HashMap<String, Item>();
	}

	InMemoryCredentialsStore(String id) {
		this(null, id, false);
	}

	public synchronized void clear() {
		store.clear();
	}

	public void copyTo(ICredentialsStore target) {
		synchronized (target) {
			synchronized (this) {
				for (Map.Entry<String, Item> entry : store.entrySet()) {
					Item item = entry.getValue();
					if (item != null) {
						if (item.value instanceof String) {
							target.put(entry.getKey(), (String) item.value, item.encrypted, item.persisted);
						} else if (item.value instanceof byte[]) {
							target.putByteArray(entry.getKey(), (byte[]) item.value, item.encrypted);
						} else if (item.value instanceof Boolean) {
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
	public synchronized void flush() throws IOException {
		for (Iterator<Map.Entry<String, Item>> it = store.entrySet().iterator(); it.hasNext();) {
			if (it.next().getValue() == null) {
				it.remove();
			}
		}
	}

	public synchronized String get(String key, String def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.get(key, def);
		}
		return (item != null && item.value instanceof String) ? (String) item.value : def;
	}

	public synchronized boolean getBoolean(String key, boolean def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.getBoolean(key, def);
		}
		return (item != null && item.value instanceof Boolean) ? (Boolean) item.value : def;
	}

	public synchronized byte[] getByteArray(String key, byte[] def) {
		Item item = store.get(key);
		if (item == null && parent != null) {
			return parent.getByteArray(key, def);
		}
		return (item != null && item.value instanceof byte[]) ? (byte[]) item.value : def;
	}

	public String getId() {
		return id;
	}

	public synchronized boolean hasKey(String key) {
		// store.containsKey(key) would return true for removed items that were set to null
		return store.get(key) != null;
	}

	public synchronized String[] keys() {
		List<String> keys = new ArrayList<String>(store.keySet().size());
		for (Entry<String, Item> entry : store.entrySet()) {
			if (entry.getValue() != null) {
				keys.add(entry.getKey());
			}
		}
		return keys.toArray(new String[0]);
	}

	public synchronized void put(String key, String value, boolean encrypt) {
		put(key, value, encrypt, true);
	}

	public synchronized void put(String key, String value, boolean encrypt, boolean persist) {
		store.put(key, createItem(value, encrypt, persist));
	}

	public synchronized void putBoolean(String key, boolean value, boolean encrypt) {
		store.put(key, createItem(value, encrypt, true));
	}

	public synchronized void putByteArray(String key, byte[] value, boolean encrypt) {
		store.put(key, createItem(value, encrypt, encrypt));
	}

	public synchronized void remove(String key) {
		if (keepRemovedKeys) {
			store.put(key, null);
		} else {
			store.remove(key);
		}
	}

	private Item createItem(Object value, boolean encrypt, boolean persist) {
		Item item = new Item();
		item.value = value;
		item.encrypted = encrypt;
		item.persisted = persist;
		return item;
	}

}
