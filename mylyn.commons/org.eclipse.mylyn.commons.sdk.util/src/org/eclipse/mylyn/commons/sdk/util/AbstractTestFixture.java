/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.net.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;

public abstract class AbstractTestFixture {

	protected final String connectorKind;

	private String description;

	private String repositoryName;

	protected final String repositoryUrl;

	private String simpleInfo;

	protected boolean useCertificateAuthentication;

	private boolean useShortUserNames;

	private final Map<String, String> properties;

	private Map<String, String> defaultproperties;

	public AbstractTestFixture(String connectorKind, String repositoryUrl) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.useCertificateAuthentication = repositoryUrl.contains("/secure/");
		properties = null;
	}

	public AbstractTestFixture(String connectorKind, FixtureConfiguration config) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = config.getUrl();
		this.useCertificateAuthentication = repositoryUrl.contains("/secure/");
		this.properties = config.getProperties();

	}

	protected abstract AbstractTestFixture getDefault();

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

	public boolean isUseShortUserNames() {
		return useShortUserNames;
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
			if (isUseShortUserNames() && userName.contains("@")) {
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
		location.setCredentialsStore(new InMemoryCredentialsStore());

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

	public void setUseShortUserNames(boolean useShortUsernames) {
		this.useShortUserNames = useShortUsernames;
	}

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

	public Map<String, String> getProperties() {
		return properties;
	}

	public Map<String, String> getDefaultproperties() {
		return defaultproperties;
	}

	public String getProperty(String key) {
		String result = null;
		if (getProperties() != null) {
			result = getProperties().get(key);
			if (result == null) {
				result = getDefaultproperties().get(key);
			}
		} else {
			result = getDefaultproperties().get(key);
		}
		return result;
	}

	public void setDefaultproperties(Map<String, String> defaultproperties) {
		this.defaultproperties = defaultproperties;
	}

}