/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.context.tests.support;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylar.context.tests.MylarCoreTestsPlugin;

/**
 * @author Steffen Pingel
 */
public class MylarTestUtils {

	public enum PrivilegeLevel {
		ANONYMOUS, USER, ADMIN
	};

	public static class Credentials {

		public final String username;

		public final String password;

		public Credentials(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public Credentials(Properties properties, String prefix) {
			this.username = properties.getProperty(prefix + "username");
			this.password = properties.getProperty(prefix + "password");

			if (username == null || password == null) {
				throw new AssertionFailedError(
						"username or password not found in <plug-in dir>/credentials.properties, make sure file is valid");
			}
		}

	}

	public static Credentials readCredentials() {
		return readCredentials(PrivilegeLevel.USER, null);
	}

	public static Credentials readCredentials(PrivilegeLevel level) {
		return readCredentials(level, null);
	}

	public static Credentials readCredentials(PrivilegeLevel level, String realm) {
		if (level == PrivilegeLevel.ANONYMOUS) {
			return new Credentials("", "");
		}

		Properties properties = new Properties();
		try {
			URL localURL = FileLocator.toFileURL(MylarCoreTestsPlugin.getDefault().getBundle().getEntry(
					"credentials.properties"));
			properties.load(new FileInputStream(new File(localURL.getFile())));
		} catch (Exception e) {
			throw new AssertionFailedError("must define credentials in <plug-in dir>/credentials.properties");
		}

		realm = (realm != null) ? realm + "." : "";
		if (level == PrivilegeLevel.ADMIN) {
			return new Credentials(properties, realm + "admin.");
		} else {
			return new Credentials(properties, "");
		}
	}

}
