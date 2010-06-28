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

import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * Initializes Trac repositories to a defined state. This is done once per test run, since cleaning and initializing the
 * repository for each test method would take too long.
 * 
 * @author Steffen Pingel
 */
public class TracFixture extends TestFixture {

	private static TracFixture current;

	public static XmlRpcServer.TestData data010;

	public static TracFixture TRAC_INVALID = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_INVALID_URL,
			"0.11", "Invalid URL");

//	public static TracFixture TRAC_0_9_WEB = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_096_URL,
//			"0.9", "Web");

	public static TracFixture TRAC_0_10_WEB = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_010_URL,
			"0.10", "Web");

	public static TracFixture TRAC_0_10_XML_RPC = new TracFixture(Version.XML_RPC, TracTestConstants.TEST_TRAC_010_URL,
			"0.10", "XML-RPC");

	public static TracFixture TRAC_0_10_XML_RPC_DIGEST_AUTH = new TracFixture(Version.XML_RPC,
			TracTestConstants.TEST_TRAC_010_DIGEST_AUTH_URL, "0.10", "XML-RPC/DigestAuth");

	public static TracFixture TRAC_0_10_XML_RPC_FORM_AUTH = new TracFixture(Version.XML_RPC,
			TracTestConstants.TEST_TRAC_010_FORM_AUTH_URL, "0.10", "XML-RPC/FormAuth");

	public static TracFixture TRAC_0_10_XML_RPC_SSL = new TracFixture(Version.XML_RPC,
			TracTestConstants.TEST_TRAC_010_SSL_URL, "0.10", "XML-RPC/SSL");

	public static TracFixture TRAC_0_11_WEB = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_011_URL,
			"0.11", "Web");

	public static TracFixture TRAC_0_11_XML_RPC = new TracFixture(Version.XML_RPC, TracTestConstants.TEST_TRAC_011_URL,
			"0.11", "XML-RPC");

	public static TracFixture TRAC_0_12_WEB = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_012_URL,
			"0.12", "Web");

	public static TracFixture TRAC_0_12_XML_RPC = new TracFixture(Version.XML_RPC, TracTestConstants.TEST_TRAC_012_URL,
			"0.12", "XML-RPC");

	public static TracFixture TRAC_TRUNK_WEB = new TracFixture(Version.TRAC_0_9, TracTestConstants.TEST_TRAC_TRUNK_URL,
			"0.12dev-r0", "Web");

	public static TracFixture TRAC_TRUNK_XML_RPC = new TracFixture(Version.XML_RPC,
			TracTestConstants.TEST_TRAC_TRUNK_URL, "0.12dev-r0", "XML-RPC");

	public static TracFixture DEFAULT = TRAC_0_12_XML_RPC;

	//public static TracFixture DEFAULT = TRAC_0_11_WEB;

	/**
	 * Standard configurations for running all test against.
	 */
	public static final TracFixture[] ALL = new TracFixture[] { TRAC_0_10_WEB, TRAC_0_11_WEB, TRAC_0_12_WEB,
			TRAC_TRUNK_WEB, TRAC_0_10_XML_RPC, TRAC_0_11_XML_RPC, TRAC_0_12_XML_RPC, TRAC_TRUNK_XML_RPC, /* TRAC_0_10_XML_RPC_SSL, */};

//	public static final TracFixture[] ALL = new TracFixture[] { TRAC_TRUNK_XML_RPC };

	/**
	 * Misc configurations for running a limited number of test against.
	 */
	public static final TracFixture[] MISC = new TracFixture[] { TRAC_0_10_XML_RPC_DIGEST_AUTH,
			TRAC_0_10_XML_RPC_FORM_AUTH, };

//	public static final TracFixture[] MISC = new TracFixture[] {};

	public static void cleanup010() throws Exception {
		if (data010 != null) {
			// data010.cleanup();
			data010 = null;
		}
	}

	public static TracFixture current(TracFixture fixture) {
		if (current == null) {
			fixture.activate();
		}
		return current;
	}

	public static TracFixture current() {
		return current(DEFAULT);
	}

	public static XmlRpcServer.TestData init010() throws Exception {
		if (data010 == null) {
			Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
			XmlRpcServer server = new XmlRpcServer(TracTestConstants.TEST_TRAC_010_URL, credentials.username,
					credentials.password);

			initializeTestData(server);
			data010 = server.getData();
		}
		return data010;
	}

	/**
	 * Adds the existing repository content to the test data of <code>server</code>.
	 */
	protected static void initializeTestData(XmlRpcServer server) throws Exception {
		server.ticketMilestone("milestone1").itemCreated();
		server.ticketMilestone("milestone2").itemCreated();
		server.ticketMilestone("milestone3").itemCreated();
		server.ticketMilestone("milestone4").itemCreated();
		server.ticketMilestone("mile&stone").itemCreated();

		server.ticketVersion("1.0").itemCreated();
		server.ticketVersion("2.0").itemCreated();

		server.ticket(1).itemCreated();
		server.ticket(2).itemCreated();
		server.ticket(3).itemCreated();
		server.ticket(4).itemCreated();
		server.ticket(5).itemCreated();
		server.ticket(6).itemCreated();
		server.ticket(7).itemCreated();
		server.ticket(8).itemCreated();
	}

	private final Version accessMode;

	private final String version;

	public TracFixture(Version accessMode, String url, String version, String info) {
		super(TracCorePlugin.CONNECTOR_KIND, url);
		this.accessMode = accessMode;
		this.version = version;
		setInfo("Trac", version, info);
	}

	@Override
	public TracFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected TestFixture getDefault() {
		return DEFAULT;
	}

	public ITracClient connect() throws Exception {
		return connect(repositoryUrl);
	}

	public ITracClient connect(PrivilegeLevel level) throws Exception {
		return connect(repositoryUrl, Proxy.NO_PROXY, level);
	}

	public ITracClient connect(String url) throws Exception {
		return connect(url, Proxy.NO_PROXY, PrivilegeLevel.USER);
	}

	public ITracClient connect(String url, Proxy proxy, PrivilegeLevel level) throws Exception {
		Credentials credentials = TestUtil.readCredentials(level);
		return connect(url, credentials.username, credentials.password, proxy);
	}

	public ITracClient connect(String url, String username, String password) throws Exception {
		return connect(url, username, password, Proxy.NO_PROXY);
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
		return singleRepository(TracCorePlugin.getDefault().getConnector());
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

//	private static void initializeRepository(XmlRpcServer server) throws Exception {
//	server.ticketVersion(null).deleteAll();
//	server.ticketVersion("1.0").create(0, "");
//	server.ticketVersion("2.0").create(0, "");
//
//	server.ticketMilestone(null).deleteAll();
//	server.ticketMilestone("milestone1").create();
//	server.ticketMilestone("milestone2").create();
//	server.ticketMilestone("milestone3").create();
//	server.ticketMilestone("milestone4").create();
//
//	server.ticket().deleteAll();
//	Ticket ticket = server.ticket().create("summary1", "description1");
//	ticket.update("comment", "milestone", "milestone1");
//	ticket = server.ticket().create("summary2", "description2");
//	ticket.update("comment", "milestone", "milestone2");
//	ticket = server.ticket().create("summary3", "description3");
//	ticket.update("comment", "milestone", "milestone2");
//	ticket = server.ticket().create("summary4", "description4");
//
//    ticket = server.ticket().create("test html entities: ���", "���\n\nmulti\nline\n\n'''bold'''\n");
//	    ticket = server.ticket().create("offline handler test", "");
//}

}
