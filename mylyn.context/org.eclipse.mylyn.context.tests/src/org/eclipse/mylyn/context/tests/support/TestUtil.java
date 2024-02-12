/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.tests.support;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import junit.framework.AssertionFailedError;

/**
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.sdk.util.CommonTestUtil} instead
 */
@Deprecated
@SuppressWarnings("nls")
public class TestUtil {

	public static final String KEY_CREDENTIALS_FILE = "mylyn.credentials";

	public enum PrivilegeLevel {
		ANONYMOUS, GUEST, USER, ADMIN
	}

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
				if (Platform.isRunning()) {
					URL localURL = FileLocator.toFileURL(
							Platform.getBundle("org.eclipse.mylyn.context.tests").getEntry("credentials.properties"));
					filename = localURL.getFile();
				} else {
					URL localURL = TestUtil.class.getResource("");
					filename = localURL.getFile() + "../../../../../../../credentials.properties";
				}
			}
			properties.load(new FileInputStream(new File(filename)));
		} catch (Exception e) {
			throw new AssertionFailedError("must define credentials in <plug-in dir>/credentials.properties");
		}

		String defaultPassword = properties.getProperty("pass");

		realm = realm != null ? realm + "." : "";
		return switch (level) {
			case ANONYMOUS -> createCredentials(properties, realm + "anon.", "", "");
			case GUEST -> createCredentials(properties, realm + "guest.", "guest@mylyn.eclipse.org", defaultPassword);
			case USER -> createCredentials(properties, realm, "tests@mylyn.eclipse.org", defaultPassword);
			case ADMIN -> createCredentials(properties, realm + "admin.", "admin@mylyn.eclipse.org", null);
			default -> throw new AssertionFailedError("invalid privilege level");
		};


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

	public static IViewPart openView(String id) throws PartInitException {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id);
	}

}
