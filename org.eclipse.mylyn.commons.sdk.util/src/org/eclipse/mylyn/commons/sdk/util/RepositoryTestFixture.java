/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.net.Proxy;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public abstract class RepositoryTestFixture {

	private final class Activation extends TestCase {

		private final boolean activate;

		private Activation(String name, boolean activate) {
			super(name);
			this.activate = activate;
		}

		@Override
		protected void runTest() throws Throwable {
			if (activate) {
				activate();
			} else {
				getDefault().activate();
			}
		}

	}

	private final String connectorKind;

	private String description;

	private String repositoryName;

	private final String repositoryUrl;

	private String simpleInfo;

	private TestSuite suite;

	private boolean useCertificateAuthentication;

	private boolean useShortUsernames;

	public RepositoryTestFixture(String connectorKind, String repositoryUrl) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.useCertificateAuthentication = repositoryUrl.contains("/secure/");
	}

	public void add(Class<? extends TestCase> clazz) {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTestSuite(clazz);
	}

	public TestSuite createSuite(TestSuite parentSuite) {
		suite = new TestSuite("Testing on " + getInfo());
		parentSuite.addTest(suite);
		suite.addTest(new Activation("repository: " + getRepositoryUrl() + " [@" + getSimpleInfo() + "]", true));
		return suite;
	}

	public void done() {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTest(new Activation("done", false));
		suite = null;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getDescription() {
		return description;
	}

	public String getInfo() {
		return repositoryName + " " + simpleInfo;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getSimpleInfo() {
		return simpleInfo;
	}

	public boolean isExcluded() {
		String excludeFixture = System.getProperty("mylyn.test.exclude", "");
		String[] excludeFixtureArray = excludeFixture.split(",");
		return new HashSet<String>(Arrays.asList(excludeFixtureArray)).contains(getRepositoryUrl());
	}

	public boolean isUseCertificateAuthentication() {
		return useCertificateAuthentication;
	}

	public boolean isUseShortUsernames() {
		return useShortUsernames;
	}

	public RepositoryLocation location() throws Exception {
		return location(PrivilegeLevel.USER);
	}

	public RepositoryLocation location(PrivilegeLevel level) throws Exception {
		return location(level, NetUtil.getProxyForUrl(repositoryUrl));
	}

	public RepositoryLocation location(PrivilegeLevel level, Proxy proxy) throws Exception {
		if (level == PrivilegeLevel.ANONYMOUS) {
			return location(null, null, proxy);
		} else {
			UserCredentials credentials = CommonTestUtil.getCredentials(level);
			String userName = credentials.getUserName();
			if (isUseShortUsernames() && userName.contains("@")) {
				userName = userName.substring(0, userName.indexOf("@"));
			}
			return location(userName, credentials.getPassword(), proxy);
		}
	}

	public RepositoryLocation location(String username, String password) throws Exception {
		return location(username, password, NetUtil.getProxyForUrl(repositoryUrl));
	}

	public RepositoryLocation location(String username, String password, final Proxy proxy) throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(repositoryUrl);
		location.setProxy(proxy);
		if (username != null && password != null) {
			location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials(username, password));
		}
		if (isUseCertificateAuthentication() && !forceDefaultKeystore(proxy)) {
			location.setCredentials(AuthenticationType.CERTIFICATE, CommonTestUtil.getCertificateCredentials());
		}
		return location;
	}

	/**
	 * Don't set certificate credentials if behind proxy. See bug 369805 for details.
	 */
	private boolean forceDefaultKeystore(Proxy proxy) {
		if (proxy != null && System.getProperty("javax.net.ssl.keyStore") != null) {
			return true;
		}
		return false;
	}

	public void setUseCertificateAuthentication(boolean useCertificateAuthentication) {
		this.useCertificateAuthentication = useCertificateAuthentication;
	}

	public void setUseShortUsernames(boolean useShortUsernames) {
		this.useShortUsernames = useShortUsernames;
	}

	protected abstract RepositoryTestFixture activate();

	protected abstract RepositoryTestFixture getDefault();

	protected void setInfo(String repositoryName, String version, String description) {
		Assert.isNotNull(repositoryName);
		Assert.isNotNull(version);
		this.repositoryName = repositoryName;
		this.simpleInfo = version;
		this.description = description;
		if (description != null && description.length() > 0) {
			this.simpleInfo += "/" + description;
		}
	}

}
