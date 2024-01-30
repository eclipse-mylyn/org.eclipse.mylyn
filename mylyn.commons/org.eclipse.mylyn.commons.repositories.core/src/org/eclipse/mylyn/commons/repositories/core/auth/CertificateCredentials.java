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

package org.eclipse.mylyn.commons.repositories.core.auth;

import java.util.Objects;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class CertificateCredentials extends AuthenticationCredentials {

	private final String keyStoreFileName;

	private final String keyStoreType;

	private final String password;

	private final boolean savePassword;

	private final boolean hasSecrets;

	public CertificateCredentials(String keyStoreFileName, String password, String keyStoreType) {
		this(keyStoreFileName, password, keyStoreType, true, true);
	}

	public CertificateCredentials(String keyStoreFileName, String password, String keyStoreType, boolean savePassword) {
		this(keyStoreFileName, password, keyStoreType, savePassword, true);
	}

	CertificateCredentials(String keyStoreFileName, String password, String keyStoreType, boolean savePassword,
			boolean hasSecrets) {
		Assert.isNotNull(password);
		this.keyStoreFileName = keyStoreFileName;
		this.password = password;
		this.keyStoreType = keyStoreType;
		this.savePassword = savePassword;
		this.hasSecrets = hasSecrets;
	}

	protected CertificateCredentials(ICredentialsStore store, String prefix, boolean loadSecrets) {
		this(store.get(prefix + ".keyStoreFileName", null), loadSecrets ? store.get(prefix + ".password", "") : "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
				store.get(prefix + ".keyStoreType", null), store.getBoolean(prefix + ".savePassword", false), //$NON-NLS-1$//$NON-NLS-2$
				loadSecrets);
	}

	@Override
	public void clear(ICredentialsStore store, String prefix) {
		store.remove(prefix + ".keyStoreFileName"); //$NON-NLS-1$
		store.remove(prefix + ".password"); //$NON-NLS-1$
		store.remove(prefix + ".keyStoreType"); //$NON-NLS-1$
		store.remove(prefix + ".savePassword"); //$NON-NLS-1$
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		CertificateCredentials other = (CertificateCredentials) obj;
		if (hasSecrets != other.hasSecrets) {
			return false;
		}
		if (!Objects.equals(keyStoreFileName, other.keyStoreFileName)) {
			return false;
		}
		if (!Objects.equals(keyStoreType, other.keyStoreType)) {
			return false;
		}
		if (!Objects.equals(password, other.password)) {
			return false;
		}
		if (savePassword != other.savePassword) {
			return false;
		}
		return true;
	}

	public String getKeyStoreFileName() {
		return keyStoreFileName;
	}

	public String getKeyStoreType() {
		return keyStoreType;
	}

	public String getPassword() {
		return password;
	}

	public boolean getSavePassword() {
		return savePassword;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hasSecrets, keyStoreFileName, keyStoreType, password, savePassword);
	}

	@Override
	public void save(ICredentialsStore store, String prefix) {
		store.put(prefix + ".keyStoreFileName", keyStoreFileName, false); //$NON-NLS-1$
		if (hasSecrets) {
			store.put(prefix + ".password", password, true); //$NON-NLS-1$
		}
		store.put(prefix + ".keyStoreType", keyStoreType, false); //$NON-NLS-1$
		store.putBoolean(prefix + ".savePassword", savePassword, false); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CertificateCredentials [keyStoreFileName="); //$NON-NLS-1$
		builder.append(keyStoreFileName);
		builder.append(", password="); //$NON-NLS-1$
		builder.append(password == null ? password : "********"); //$NON-NLS-1$
		builder.append(", keyStoreType="); //$NON-NLS-1$
		builder.append(keyStoreType);
		builder.append(", savePassword="); //$NON-NLS-1$
		builder.append(savePassword);
		builder.append(", hasSecrets="); //$NON-NLS-1$
		builder.append(hasSecrets);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
