/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests.support;

import java.io.IOException;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * @author Steffen Pingel
 */
public class DelegatingSecurePreferences implements ISecurePreferences {

	ISecurePreferences delegate;

	StorageException exception;

	public DelegatingSecurePreferences(ISecurePreferences delegate) {
		this.delegate = delegate;
	}

	@Override
	public String absolutePath() {
		return delegate.absolutePath();
	}

	@Override
	public String[] childrenNames() {
		return delegate.childrenNames();
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public void flush() throws IOException {
		delegate.flush();
	}

	@Override
	public String get(String key, String def) throws StorageException {
		checkException();
		return delegate.get(key, def);
	}

	@Override
	public boolean getBoolean(String key, boolean def) throws StorageException {
		checkException();
		return delegate.getBoolean(key, def);
	}

	@Override
	public byte[] getByteArray(String key, byte[] def) throws StorageException {
		checkException();
		return delegate.getByteArray(key, def);
	}

	public ISecurePreferences getDelegate() {
		return delegate;
	}

	@Override
	public double getDouble(String key, double def) throws StorageException {
		checkException();
		return delegate.getDouble(key, def);
	}

	public StorageException getException() {
		return exception;
	}

	@Override
	public float getFloat(String key, float def) throws StorageException {
		checkException();
		return delegate.getFloat(key, def);
	}

	@Override
	public int getInt(String key, int def) throws StorageException {
		checkException();
		return delegate.getInt(key, def);
	}

	@Override
	public long getLong(String key, long def) throws StorageException {
		checkException();
		return delegate.getLong(key, def);
	}

	@Override
	public boolean isEncrypted(String key) throws StorageException {
		checkException();
		return delegate.isEncrypted(key);
	}

	@Override
	public String[] keys() {
		return delegate.keys();
	}

	@Override
	public String name() {
		return delegate.name();
	}

	@Override
	public ISecurePreferences node(String pathName) {
		return delegate.node(pathName);
	}

	@Override
	public boolean nodeExists(String pathName) {
		return delegate.nodeExists(pathName);
	}

	@Override
	public ISecurePreferences parent() {
		return delegate.parent();
	}

	@Override
	public void put(String key, String value, boolean encrypt) throws StorageException {
		checkException();
		delegate.put(key, value, encrypt);
	}

	@Override
	public void putBoolean(String key, boolean value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putBoolean(key, value, encrypt);
	}

	@Override
	public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putByteArray(key, value, encrypt);
	}

	@Override
	public void putDouble(String key, double value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putDouble(key, value, encrypt);
	}

	@Override
	public void putFloat(String key, float value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putFloat(key, value, encrypt);
	}

	@Override
	public void putInt(String key, int value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putInt(key, value, encrypt);
	}

	@Override
	public void putLong(String key, long value, boolean encrypt) throws StorageException {
		checkException();
		delegate.putLong(key, value, encrypt);
	}

	@Override
	public void remove(String key) {
		delegate.remove(key);
	}

	@Override
	public void removeNode() {
		delegate.removeNode();
	}

	public void setDelegate(ISecurePreferences delegate) {
		this.delegate = delegate;
	}

	public void setException(StorageException exception) {
		this.exception = exception;
	}

	private void checkException() throws StorageException {
		if (exception != null) {
			throw exception;
		}
	}

}
