/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import java.util.Objects;

/**
 * Provides a user name and password.
 *
 * @author Steffen Pingel
 * @since 2.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class AuthenticationCredentials {

	private final String userName;

	private final String password;

	/**
	 * @param userName
	 *            the user name, must not be null
	 * @param password
	 *            the password, must not be null
	 */
	public AuthenticationCredentials(String userName, String password) {
		if ((userName == null) || (password == null)) {
			throw new IllegalArgumentException();
		}

		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(password, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final AuthenticationCredentials other = (AuthenticationCredentials) obj;
		if (!Objects.equals(password, other.password)) {
			return false;
		}
		if (!Objects.equals(userName, other.userName)) {
			return false;
		}
		return true;
	}

}
