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

import java.util.EnumSet;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class TestConfiguration {

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

}
