/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
import java.util.EnumSet;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.Assert;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Steffen Pingel
 */
public class TestConfiguration {

	private static final String URL_SERVICES_LOCALHOST = "http://localhost:2080";

	private static final String URL_SERVICES_DEFAULT = "http://mylyn.org";

	public enum TestKind {
		UNIT, COMPONENT, INTEGRATION, SYSTEM
	};

	public static TestConfiguration defaultConfiguration;

	private static final String SERVER = System.getProperty("mylyn.test.server", "mylyn.org");

	public static TestConfiguration getDefault() {
		if (defaultConfiguration == null) {
			defaultConfiguration = new TestConfiguration(TestKind.UNIT);
			defaultConfiguration.setDefaultOnly(CommonTestUtil.runHeartbeatTestsOnly());
		}
		return defaultConfiguration;
	}

	public static String getRepositoryUrl(String service) {
		return getDefault().getUrl(service);
	}

	public static String getRepositoryUrl(String service, boolean secure) {
		return getDefault().getUrl(service, secure);
	}

	public static void setDefault(TestConfiguration defaultConfiguration) {
		TestConfiguration.defaultConfiguration = defaultConfiguration;
	}

	private final EnumSet<TestKind> kinds;

	private boolean localOnly;

	private boolean defaultOnly;

	private boolean headless;

	public TestConfiguration(TestKind firstKind, TestKind... moreKinds) {
		Assert.isNotNull(firstKind);
		this.kinds = EnumSet.of(firstKind, moreKinds);
	}

	public String getUrl(String service) {
		return getUrl(service, false);
	}

	public String getUrl(String service, boolean secure) {
		return ((secure) ? "https://" : "http://") + SERVER + "/" + service;
	}

	public boolean hasKind(TestKind kind) {
		return kinds.contains(kind);
	}

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
		List<T> fixtures = Collections.emptyList();

		try {
			File file = CommonTestUtil.getFile(clazz, "local.json");
			fixtures = discover("file://" + file.getAbsolutePath(), "", clazz, fixtureType);
		} catch (AssertionFailedError e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}

		if (fixtures.isEmpty()) {
			fixtures = discover(URL_SERVICES_LOCALHOST + "/cgi-bin/services", URL_SERVICES_LOCALHOST, clazz,
					fixtureType);
		}

		if (fixtures.isEmpty()) {
			fixtures = discover(URL_SERVICES_DEFAULT + "/cgi-bin/services", URL_SERVICES_DEFAULT, clazz, fixtureType);
		}

		return fixtures;
	}

	public static <T> List<T> discover(String location, String baseUrl, Class<T> clazz, String fixtureType) {
		Assert.isNotNull(fixtureType);
		List<FixtureConfiguration> configurations = getConfigurations(location);
		if (configurations != null) {
			for (FixtureConfiguration configuration : configurations) {
				if (configuration != null) {
					configuration.setUrl(baseUrl + configuration.getUrl());
				}
			}
			return loadFixtures(configurations, clazz, fixtureType);
		}
		return Collections.emptyList();
	}

	private static <T> List<T> loadFixtures(List<FixtureConfiguration> configurations, Class<T> clazz,
			String fixtureType) {
		List<T> result = new ArrayList<T>();
		for (FixtureConfiguration configuration : configurations) {
			if (configuration != null && fixtureType.equals(configuration.getType())) {
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

	private static List<FixtureConfiguration> getConfigurations(String url) {
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
			return null;
		}
	}

}
