/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Pawel Niewiadomski - fixes for bug 288347
 *******************************************************************************/

package org.eclipse.mylyn.tests.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.AssertionFailedError;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;

/**
 * @author Steffen Pingel
 * @deprecated use {@link CommonTestUtil} instead
 */
@Deprecated
public class TestUtil {

	public static final String KEY_CREDENTIALS_FILE = "mylyn.credentials";

	public enum PrivilegeLevel {
		ANONYMOUS, GUEST, USER, ADMIN, READ_ONLY
	};

	public static class Credentials {

		public final String username;

		public final String password;

		public Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " [username=" + username + ",password=" + password + "]";
		}

		public String getShortUserName() {
			if (username.contains("@")) {
				return username.substring(0, username.indexOf("@"));
			}
			return username;
		}

	}

	public static Credentials readCredentials() {
		return readCredentials(PrivilegeLevel.USER, null);
	}

	public static Credentials readCredentials(PrivilegeLevel level) {
		return readCredentials(level, null);
	}

	public static Credentials readCredentials(PrivilegeLevel level, String realm) {
		Properties properties = new Properties();
		try {
			File file;
			String filename = System.getProperty(KEY_CREDENTIALS_FILE);
			if (filename == null) {
				try {
					file = getFile(TestUtil.class, "credentials.properties");
					if (!file.exists()) {
						throw new AssertionFailedError();
					}
				} catch (AssertionFailedError e) {
					file = new File(new File(System.getProperty("user.home"), ".mylyn"), "credentials.properties");
				}
			} else {
				file = new File(filename);
			}
			properties.load(new FileInputStream(file));
		} catch (Exception e) {
			AssertionFailedError error = new AssertionFailedError(
					"must define credentials in $HOME/.mylyn/credentials.properties");
			error.initCause(e);
			throw error;
		}

		String defaultPassword = properties.getProperty("pass");

		realm = (realm != null) ? realm + "." : "";
		switch (level) {
		case ANONYMOUS:
			return createCredentials(properties, realm + "anon.", "", "");
		case GUEST:
			return createCredentials(properties, realm + "guest.", "guest@mylyn.eclipse.org", defaultPassword);
		case USER:
			return createCredentials(properties, realm, "tests@mylyn.eclipse.org", defaultPassword);
		case READ_ONLY:
			return createCredentials(properties, realm, "read-only@mylyn.eclipse.org", defaultPassword);
		case ADMIN:
			return createCredentials(properties, realm + "admin.", "admin@mylyn.eclipse.org", null);
		}

		throw new AssertionFailedError("invalid privilege level");
	}

	private static Credentials createCredentials(Properties properties, String prefix, String defaultUsername,
			String defaultPassword) {
		String username = properties.getProperty(prefix + "user");
		String password = properties.getProperty(prefix + "pass");

		if (username == null) {
			username = defaultUsername;
		}

		if (password == null) {
			password = defaultPassword;
		}

		if (username == null || password == null) {
			throw new AssertionFailedError(
					"username or password not found in <plug-in dir>/credentials.properties, make sure file is valid");
		}

		return new Credentials(username, password);
	}

	public static File getFile(Object source, String filename) throws IOException {
		return CommonTestUtil.getFile(source, filename);
	}

	/**
	 * @deprecated use {org.eclipse.mylyn.commons.sdk.util.CommonTestUtil#runHeartbeatTestsOnly()} instead
	 */
	@Deprecated
	public static boolean runHeartbeatTestsOnly() {
		return !Boolean.parseBoolean(System.getProperty("org.eclipse.mylyn.tests.all"));
	}

}
