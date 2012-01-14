/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * @author Steffen Pingel
 */
public class CertificateCredentials extends AuthenticationCredentials {

	private final String keyStoreFileName;

	private final String password;

	public CertificateCredentials(String keyStoreFileName, String password) {
		Assert.isNotNull(password);
		this.keyStoreFileName = keyStoreFileName;
		this.password = password;
	}

	protected CertificateCredentials(ICredentialsStore store, String prefix, boolean loadSecrets)
			throws StorageException {
		this(store.get(prefix + ".keyStoreFileName", null), (loadSecrets) ? store.get(prefix + ".password", "") : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CertificateCredentials other = (CertificateCredentials) obj;
		if (keyStoreFileName == null) {
			if (other.keyStoreFileName != null) {
				return false;
			}
		} else if (!keyStoreFileName.equals(other.keyStoreFileName)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		return true;
	}

	public String getKeyStoreFileName() {
		return keyStoreFileName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyStoreFileName == null) ? 0 : keyStoreFileName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public void clear(ICredentialsStore store, String prefix) {
		store.remove(prefix + ".keyStoreFileName"); //$NON-NLS-1$
		store.remove(prefix + ".password"); //$NON-NLS-1$
	}

	@Override
	public void save(ICredentialsStore store, String prefix) {
		store.put(prefix + ".keyStoreFileName", keyStoreFileName, false); //$NON-NLS-1$
		store.put(prefix + ".password", password, true); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CertificateCredentials [keyStoreFileName="); //$NON-NLS-1$
		builder.append(keyStoreFileName);
		builder.append(", password="); //$NON-NLS-1$
		builder.append((password == null) ? password : "********"); //$NON-NLS-1$
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
