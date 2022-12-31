/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Provides a user name and password.
 * 
 * @author Steffen Pingel
 */
public class UserCredentials extends AuthenticationCredentials {

	private final String domain;

	private final String password;

	private final String userName;

	private final boolean savePassword;

	private final boolean hasSecrets;

	/**
	 * @param userName
	 *            the user name, must not be null
	 * @param password
	 *            the password, must not be null
	 */
	public UserCredentials(String userName, String password) {
		this(userName, password, null, true, true);
	}

	/**
	 * @param userName
	 *            the user name, must not be null
	 * @param password
	 *            the password, must not be null
	 */
	public UserCredentials(String userName, String password, boolean savePassword) {
		this(userName, password, null, savePassword, true);
	}

	public UserCredentials(String userName, String password, String domain, boolean savePassword) {
		this(userName, password, domain, savePassword, true);
	}

	public UserCredentials(String userName, String password, String domain, boolean savePassword, boolean hasSecrets) {
		Assert.isNotNull(userName);
		Assert.isNotNull(password);
		this.userName = userName;
		this.password = password;
		this.domain = domain;
		this.savePassword = savePassword;
		this.hasSecrets = hasSecrets;
	}

	protected UserCredentials(ICredentialsStore store, String prefix, boolean loadSecrets) throws StorageException {
		this(store.get(prefix + ".user", ""), (loadSecrets) ? store.get(prefix + ".password", "") : "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
				store.get(prefix + ".domain", null), //$NON-NLS-1$
				store.getBoolean(prefix + ".savePassword", false), loadSecrets); //$NON-NLS-1$
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
		UserCredentials other = (UserCredentials) obj;
		if (domain == null) {
			if (other.domain != null) {
				return false;
			}
		} else if (!domain.equals(other.domain)) {
			return false;
		}
		if (hasSecrets != other.hasSecrets) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (savePassword != other.savePassword) {
			return false;
		}
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

	public String getDomain() {
		return domain;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}

	public boolean getSavePassword() {
		return savePassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + (hasSecrets ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + (savePassword ? 1231 : 1237);
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public void clear(ICredentialsStore store, String prefix) {
		store.remove(prefix + ".user"); //$NON-NLS-1$ 
		store.remove(prefix + ".password"); //$NON-NLS-1$ 
		store.remove(prefix + ".domain"); //$NON-NLS-1$
		store.remove(prefix + ".savePassword"); //$NON-NLS-1$
	}

	@Override
	public void save(ICredentialsStore store, String prefix) {
		store.put(prefix + ".user", userName, false); //$NON-NLS-1$ 
		if (hasSecrets) {
			store.put(prefix + ".password", password, true, savePassword); //$NON-NLS-1$ 
		}
		store.put(prefix + ".domain", domain, false); //$NON-NLS-1$
		store.putBoolean(prefix + ".savePassword", savePassword, false); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserCredentials [domain="); //$NON-NLS-1$
		builder.append(domain);
		builder.append(", password="); //$NON-NLS-1$
		builder.append((password == null) ? password : "********"); //$NON-NLS-1$
		builder.append(", userName="); //$NON-NLS-1$
		builder.append(userName);
		builder.append(", savePassword="); //$NON-NLS-1$
		builder.append(savePassword);
		builder.append(", hasSecrets="); //$NON-NLS-1$
		builder.append(hasSecrets);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
