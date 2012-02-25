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

import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.auth.ICredentialsStore;

/**
 * @author Steffen Pingel
 */
public class SecureCredentialsStore implements ICredentialsStore {

	private static final String ID_PLUGIN = "org.eclipse.mylyn.commons.repository"; //$NON-NLS-1$

	private final String url;

	public SecureCredentialsStore(String url) {
		this.url = url;
	}

	public void clear() {
		//getSecurePreferences().clear();
		getSecurePreferences().removeNode();
	}

	public void flush() throws IOException {
		getSecurePreferences().flush();
	}

	public String get(String key, String def) throws StorageException {
		return getSecurePreferences().get(key, def);
	}

	public byte[] getByteArray(String key, byte[] def) throws StorageException {
		return getSecurePreferences().getByteArray(key, def);
	}

	private ISecurePreferences getSecurePreferences() {
		ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault().node(ID_PLUGIN);
		securePreferences = securePreferences.node(EncodingUtils.encodeSlashes(getUrl()));
		return securePreferences;
	}

	public String getUrl() {
		return url;
	}

	public String[] keys() {
		return getSecurePreferences().keys();
	}

	public void put(String key, String value, boolean encrypt) throws StorageException {
		getSecurePreferences().put(key, value, encrypt);
	}

	public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException {
		getSecurePreferences().putByteArray(key, value, encrypt);
	}

	public void remove(String key) {
		getSecurePreferences().remove(key);
	}

	public void copyTo(ICredentialsStore target) throws StorageException {
		ISecurePreferences preferences = getSecurePreferences();
		for (String key : preferences.keys()) {
			target.put(key, preferences.get(key, null), preferences.isEncrypted(key));
		}
	}

}
