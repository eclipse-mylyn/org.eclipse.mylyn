/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.mylyn.commons.tests.support.CommonTestUtil;

/**
 * @author Steffen Pingel
 */
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
			File file;
			String filename = System.getProperty(KEY_CREDENTIALS_FILE);
			if (filename == null) {
				try {
					file = getFile(TestUtil.class, "credentials.properties");
					if (!file.exists()) {
						throw new AssertionFailedError();
					}
				} catch (AssertionFailedError e) {
					file = getFile(TestUtil.class, "../org.eclipse.mylyn.context.tests/credentials.properties");
					// lookup may have reverted to this plug-in, try to lookup file in org.eclipse.context.tests plug-in
					//File path = new File(file.getParentFile().getParentFile(), "org.eclipse.mylyn.context.tests");
					//file = new File(path, file.getName());
				}
			} else {
				file = new File(filename);
			}
			properties.load(new FileInputStream(file));
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

//	public static File getFile(String bundleId, Class<?> clazz, String filename) throws IOException {
//		Bundle bundle = Platform.getBundle(bundleId);
//		if (bundle != null) {
//			URL localURL = FileLocator.toFileURL(bundle.getEntry(filename));
//			filename = localURL.getFile();
//		} else {
//			URL localURL = clazz.getResource("");
//			String path = localURL.getFile();
//			int i = path.indexOf("!");
//			if (i != -1) {
//				int j = path.lastIndexOf(File.separatorChar, i);
//				if (j != -1) {
//					path = path.substring(0, j) + File.separator;
//				} else {
//					Assert.fail("Unable to determine location for '" + filename + "' at '" + path + "'");
//				}
//				// class file is nested in jar, use jar path as base
//				if (path.startsWith("file:")) {
//					path = path.substring(5);
//				}
//			} else {
//				// create relative path to base of class file location
//				String[] tokens = clazz.getName().split("\\.");
//				for (int j = 0; j < tokens.length - 1; j++) {
//					path += ".." + File.separator;
//				}
//				if (path.contains("bin" + File.separator)) {
//					path += ".." + File.separator;
//				}
//			}
//			filename = path + filename.replaceAll("/", File.separator);
//		}
//		return new File(filename).getCanonicalFile();
//	}
}
