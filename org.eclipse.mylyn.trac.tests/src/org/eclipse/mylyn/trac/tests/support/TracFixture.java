/*******************************************************************************
 * Copyright (c) 2009, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * Initializes Trac repositories to a defined state. This is done once per test run, since cleaning and initializing the
 * repository for each test method would take too long.
 * 
 * @author Steffen Pingel
 */
public class TracFixture extends TestFixture {

	public static String TAG_MISC = "misc";

	public static final String TAG_TEST = "test";

	private static TracFixture current;

	public static TracFixture current() {
		if (current == null) {
			current = TestConfiguration.getDefault().discoverDefault(TracFixture.class, "trac");
			current.activate();
		}
		return current;
	}

	private final Version accessMode;

	private final String version;

	private final Set<String> tags;

	private final boolean excluded;

	public TracFixture(Version accessMode, String url, String version, String info) {
		super(TracCorePlugin.CONNECTOR_KIND, url);
		Assert.isNotNull(accessMode);
		Assert.isNotNull(info);
		this.accessMode = accessMode;
		this.version = version;
		this.tags = new HashSet<String>();
		this.excluded = info.startsWith("Test");
		setInfo("Trac", version, info);
	}

	public TracFixture(FixtureConfiguration configuration) {
		this(Version.fromVersion(configuration.getProperties().get("version")), configuration.getUrl(),
				configuration.getVersion(), configuration.getInfo());
		if (configuration.getTags() != null) {
			this.tags.addAll(configuration.getTags());
		}
	}

	@Override
	public TracFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected TestFixture getDefault() {
		return TestConfiguration.getDefault().discoverDefault(TracFixture.class, "trac");
	}

	public ITracClient connect() throws Exception {
		return connect(repositoryUrl);
	}

	public ITracClient connectXmlRpc(PrivilegeLevel level) throws Exception {
		UserCredentials credentials = CommonTestUtil.getCredentials(level);
		return connect(repositoryUrl, credentials.getUserName(), credentials.getPassword(),
				getDefaultProxy(repositoryUrl), Version.XML_RPC);
	}

	private Proxy getDefaultProxy(String url) {
		return WebUtil.getProxyForUrl(url);
	}

	public ITracClient connect(String url) throws Exception {
		return connect(url, getDefaultProxy(url), PrivilegeLevel.USER);
	}

	public ITracClient connect(String url, Proxy proxy, PrivilegeLevel level) throws Exception {
		UserCredentials credentials = CommonTestUtil.getCredentials(level);
		return connect(url, credentials.getUserName(), credentials.getPassword(), proxy);
	}

	public ITracClient connect(String url, String username, String password) throws Exception {
		return connect(url, username, password, getDefaultProxy(url));
	}

	public ITracClient connect(String url, String username, String password, Proxy proxy) throws Exception {
		return connect(url, username, password, proxy, accessMode);
	}

	public ITracClient connect(String url, String username, String password, final Proxy proxy, Version version)
			throws Exception {
		WebLocation location = new WebLocation(url, username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		return TracClientFactory.createClient(location, version);
	}

	public Version getAccessMode() {
		return accessMode;
	}

	public String getVersion() {
		return version;
	}

	public boolean isXmlRpcEnabled() {
		return Version.XML_RPC.name().equals(getAccessMode());
	}

	public TaskRepository singleRepository(TracRepositoryConnector connector) {
		connector.getClientManager().writeCache();
		TaskRepository repository = super.singleRepository();

		// XXX avoid failing test due to stale client
		connector.getClientManager().clearClients();

		connector.getClientManager().readCache();
		return repository;
	}

	@Override
	public TaskRepository singleRepository() {
		return singleRepository(connector());
	}

	@Override
	protected void configureRepository(TaskRepository repository) {
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(accessMode.name());
	}

	@Override
	protected void resetRepositories() {
		TracCorePlugin.getDefault().getConnector().getClientManager().clearClients();
	}

	@Override
	public TracRepositoryConnector connector() {
		return (TracRepositoryConnector) super.connector();
	}

	public TracHarness createHarness() {
		return new TracHarness(this);
	}

	@Override
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	public boolean requiresAuthentication() {
		return getInfo().contains("AllBasicAuth");
	}

	@Override
	public boolean isExcluded() {
		return super.isExcluded() || excluded;
	}

}
