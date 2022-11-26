/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Guy Perron - add Windows support
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.AssertionFailedError;

/**
 * @author Steffen Pingel
 */
public class TestConfiguration {

	private static final String URL_SERVICES_LOCALHOST = System.getProperty("localhost.test.server",
			"http://localhost:2080");

	private static final String URL_SERVICES_DEFAULT = System.getProperty("mylyn.test.server", "https://mylyn.org");

	public static TestConfiguration defaultConfiguration;

	public static TestConfiguration getDefault() {
		if (defaultConfiguration == null) {
			defaultConfiguration = new TestConfiguration();
			defaultConfiguration.setDefaultOnly(CommonTestUtil.runHeartbeatTestsOnly());
		}
		return defaultConfiguration;
	}

	public static void setDefault(TestConfiguration defaultConfiguration) {
		TestConfiguration.defaultConfiguration = defaultConfiguration;
	}

	private boolean localOnly;

	private boolean defaultOnly;

	private boolean headless;

	public boolean isDefaultOnly() {
		return defaultOnly;
	}

	public boolean isHeadless() {
		return headless;
	}

	public boolean isLocalOnly() {
		return localOnly;
	}

	public void setDefaultOnly(boolean heartbeat) {
		this.defaultOnly = heartbeat;
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public void setLocalOnly(boolean localOnly) {
		this.localOnly = localOnly;
	}

	public <T> List<T> discover(Class<T> clazz, String fixtureType) {
		return discover(clazz, fixtureType, isDefaultOnly());
	}

	public <T> T discoverDefault(Class<T> clazz, String fixtureType) {
		List<T> fixtures = discover(clazz, fixtureType, true);
		if (fixtures.isEmpty()) {
			throw new RuntimeException(NLS.bind("No default fixture available for {0}", fixtureType));
		}
		return fixtures.get(0);
	}

	public <T> List<T> discover(Class<T> clazz, String fixtureType, boolean defaultOnly) {
		List<T> fixtures = Collections.emptyList();
		Exception[] exception = new Exception[1];

		if (!CommonTestUtil.ignoreLocalTestServices()) {
			try {
				File file = CommonTestUtil.getFile(clazz, "local.json");
				fixtures = discover(file.toURI().toASCIIString(), "", clazz, fixtureType, defaultOnly, exception);
			} catch (AssertionFailedError e) {
				// ignore
			} catch (IOException e) {
				// ignore
			}

			if (fixtures.isEmpty()) {
				fixtures = discover(URL_SERVICES_LOCALHOST + "/cgi-bin/services", URL_SERVICES_LOCALHOST, clazz,
						fixtureType, defaultOnly, exception);
			}
		}
		if (fixtures.isEmpty()) {
			fixtures = discover(URL_SERVICES_DEFAULT + "/cgi-bin/services", URL_SERVICES_DEFAULT, clazz, fixtureType,
					defaultOnly, exception);
		}

		if (fixtures.isEmpty()) {
			throw new RuntimeException(
					NLS.bind("Failed to discover any fixtures for kind {0} with defaultOnly={1} ({2} and {3})",
							new Object[] { fixtureType, Boolean.toString(defaultOnly), URL_SERVICES_LOCALHOST,
									URL_SERVICES_DEFAULT }),
					exception[0]);
		}

		return fixtures;
	}

	private static <T> List<T> discover(String location, String baseUrl, Class<T> clazz, String fixtureType,
			boolean defaultOnly, Exception[] result) {
		Assert.isNotNull(fixtureType);
		List<FixtureConfiguration> configurations = getConfigurations(location, result);
		if (configurations != null) {
			for (FixtureConfiguration configuration : configurations) {
				if (configuration != null) {
					configuration.setUrl(baseUrl + configuration.getUrl());
				}
			}
			return loadFixtures(configurations, clazz, fixtureType, defaultOnly);
		}
		return Collections.emptyList();
	}

	private static <T> List<T> loadFixtures(List<FixtureConfiguration> configurations, Class<T> clazz,
			String fixtureType, boolean defaultOnly) {
		List<T> result = new ArrayList<T>();
		String defaultOverwriteUrl = System.getProperty("mylyn.tests.configuration.url", "");
		for (FixtureConfiguration configuration : configurations) {
			if (configuration != null && fixtureType.equals(configuration.getType())
					&& (!defaultOnly || (defaultOverwriteUrl.equals("") && configuration.isDefault())
							|| (configuration.url.equals(defaultOverwriteUrl)))) {
				try {
					Constructor<T> constructor = clazz.getConstructor(FixtureConfiguration.class);
					result.add(constructor.newInstance(configuration));
				} catch (Exception e) {
					throw new RuntimeException("Unexpected error creating test fixture", e);
				}
			}
		}
		return result;
	}

	private static List<FixtureConfiguration> getConfigurations(String url, Exception[] result) {
		try {
			URLConnection connection = new URL(url).openConnection();
			InputStreamReader in = new InputStreamReader(connection.getInputStream());
			try {
				TypeToken<List<FixtureConfiguration>> type = new TypeToken<List<FixtureConfiguration>>() {
				};
				return new Gson().fromJson(in, type.getType());
			} finally {
				in.close();
			}
		} catch (IOException e) {
			result[0] = new IOException("IOException accessing " + url, e);
			return null;
		}
	}

}
