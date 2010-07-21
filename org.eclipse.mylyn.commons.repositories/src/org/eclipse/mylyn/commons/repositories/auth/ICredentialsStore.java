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

package org.eclipse.mylyn.commons.repositories.auth;

import java.io.IOException;

import org.eclipse.equinox.security.storage.StorageException;

/**
 * @author Steffen Pingel
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICredentialsStore {

	public void clear();

	public void flush() throws IOException;

	public String get(String key, String def) throws StorageException;

	public byte[] getByteArray(String key, byte[] def) throws StorageException;

	public String[] keys();

	public void put(String key, String value, boolean encrypt) throws StorageException;

	public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException;

	public void remove(String key);

}
