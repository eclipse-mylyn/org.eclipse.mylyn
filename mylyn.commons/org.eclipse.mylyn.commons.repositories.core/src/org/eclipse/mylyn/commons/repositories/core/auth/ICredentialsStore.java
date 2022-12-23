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

package org.eclipse.mylyn.commons.repositories.core.auth;

import java.io.IOException;

/**
 * @author Steffen Pingel
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICredentialsStore {

	public void clear();

	public void flush() throws IOException;

	public String get(String key, String def);

	public boolean getBoolean(String key, boolean def);

	public byte[] getByteArray(String key, byte[] def);

	public String[] keys();

	public void put(String key, String value, boolean encrypt);

	public void put(String key, String value, boolean encrypt, boolean persist);

	public void putBoolean(String key, boolean value, boolean encrypt);

	public void putByteArray(String key, byte[] value, boolean encrypt);

	public void remove(String key);

	public void copyTo(ICredentialsStore target);

	/**
	 * Test whether this store is available for reading and writing. Throws an {@link UnavailableException} if not
	 */
	public void testAvailability() throws UnavailableException;
}
