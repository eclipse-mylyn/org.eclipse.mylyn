/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.context.tests.support;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylyn.context.tests.ContextTestsPlugin;

/**
 * @author Steffen Pingel
 */
public class TestUtil {

	public static final String KEY_CREDENTIALS_FILE = "mylyn.credentials";

	public enum PrivilegeLevel {
		ANONYMOUS, GUEST, USER, ADMIN
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
			return getClass().getName() + " [username=" + username + ",password=" + password + "]";
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
			String filename = System.getProperty(KEY_CREDENTIALS_FILE);
			if (filename == null) {
				URL localURL = FileLocator.toFileURL(ContextTestsPlugin.getDefault().getBundle().getEntry(
						"credentials.properties"));
				filename = localURL.getFile();
			}
			properties.load(new FileInputStream(new File(filename)));
		} catch (Exception e) {
			throw new AssertionFailedError("must define credentials in <plug-in dir>/credentials.properties");
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

}
