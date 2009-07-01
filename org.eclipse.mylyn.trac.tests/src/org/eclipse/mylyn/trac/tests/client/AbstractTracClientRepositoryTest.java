/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.Ticket;

/**
 * Test cases for classes that implement {@link ITracClient}.
 * 
 * @author Steffen Pingel
 */
public class AbstractTracClientRepositoryTest extends AbstractTracClientTest {

	protected List<Ticket> tickets;

	private TestData data;

	public AbstractTracClientRepositoryTest(Version version) {
		super(version);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();
		tickets = data.tickets;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	public void testValidate010() throws Exception {
		validate(TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testValidate010DigestAuth() throws Exception {
		validate(TracTestConstants.TEST_TRAC_010_DIGEST_AUTH_URL);
	}

	public void testValidate011() throws Exception {
		validate(TracTestConstants.TEST_TRAC_011_URL);
	}

	public void testValidate010FormAuth() throws Exception {
		validate(TracTestConstants.TEST_TRAC_010_FORM_AUTH_URL);
	}

	protected void validate(String url) throws Exception {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		// standard connect
		connect(url);
		client.validate(callback);

		// invalid url
		connect("http://non.existant/repository");
		try {
			client.validate(callback);
			fail("Expected TracException");
		} catch (TracException e) {
		}

		// invalid password
		connect(url, credentials.username, "wrongpassword");
		try {
			client.validate(callback);
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}

		// invalid username
		connect(url, "wrongusername", credentials.password);
		try {
			client.validate(callback);
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testProxy() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "", new Proxy(Type.HTTP, new InetSocketAddress(
				"invalidhostname", 8080)));
		try {
			client.validate(callback);
			fail("Expected IOException");
		} catch (TracException e) {
		}
	}

	public void testGetTicket096() throws Exception {
		if (version == Version.XML_RPC) {
			return;
		}

		connect096();
		getTicket();
	}

	public void testGetTicket010() throws Exception {
		connect010();
		getTicket();
	}

	public void testGetTicket011() throws Exception {
		if (version == Version.TRAC_0_9) {
			// XXX web mode is broken for Trac 0.11: need to fix bug 175211
			return;
		}

		connect011();
		getTicket();
	}

	private void getTicket() throws Exception {
		TracTicket ticket = client.getTicket(tickets.get(0).getId(), null);
		assertTicketEquals(tickets.get(0), ticket);

		ticket = client.getTicket(tickets.get(1).getId(), null);
		assertTicketEquals(tickets.get(1), ticket);
	}

	public void testGetTicketInvalidId096() throws Exception {
		if (version == Version.XML_RPC) {
			return;
		}

		connect096();
		getTicketInvalidId();
	}

	public void testGetTicketInvalidId010() throws Exception {
		connect010();
		getTicketInvalidId();
	}

	public void testGetTicketInvalidId011() throws Exception {
		connect011();
		getTicketInvalidId();
	}

	private void getTicketInvalidId() throws Exception {
		try {
			client.getTicket(Integer.MAX_VALUE, null);
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testGetTicketUmlaute010() throws Exception {
		connect010();
		getTicketUmlaute();
	}

	public void testGetTicketUmlaute011() throws Exception {
		if (version == Version.TRAC_0_9) {
			// XXX need to fix bug 175211
			return;
		}

		connect011();
		getTicketUmlaute();
	}

	private void getTicketUmlaute() throws Exception {
		TracTicket ticket = client.getTicket(data.htmlEntitiesTicketId, null);
		assertEquals("test html entities: \u00E4\u00F6\u00FC", ticket.getValue(Key.SUMMARY));
		if (version == Version.XML_RPC) {
			assertEquals("\u00C4\u00D6\u00DC\n\nmulti\nline\n\n'''bold'''\n", ticket.getValue(Key.DESCRIPTION));
		} else {
			assertEquals(null, ticket.getValue(Key.DESCRIPTION));
		}
	}

	public void testSearchAll010() throws Exception {
		connect010();
		searchAll();
	}

	public void testSearchAll011() throws Exception {
		connect011();
		searchAll();
	}

	private void searchAll() throws Exception {
		TracSearch search = new TracSearch();
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		//assertEquals(tickets.size(), result.size());
		assertTrue(result.size() >= tickets.size());
	}

	public void testSearchEmpty010() throws Exception {
		connect010();
		searchEmpty();
	}

	public void testSearchEmpty011() throws Exception {
		connect011();
		searchEmpty();
	}

	private void searchEmpty() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "does not exist");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(0, result.size());
	}

	public void testSearchMilestone1_010() throws Exception {
		connect010();
		searchMilestone1();
	}

	public void testSearchMilestone1_011() throws Exception {
		connect011();
		searchMilestone1();
	}

	private void searchMilestone1() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
	}

	public void testSearchMilestone2_0_10() throws Exception {
		connect010();
		searchMilestone2();
	}

	public void testSearchMilestone2_0_11() throws Exception {
		connect011();
		searchMilestone2();
	}

	private void searchMilestone2() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(3, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertTicketEquals(tickets.get(1), result.get(1));
		assertTicketEquals(tickets.get(2), result.get(2));
	}

	public void testSearchExactMatch010() throws Exception {
		connect010();
		searchExactMatch();
	}

	public void testSearchExactMatch011() throws Exception {
		connect011();
		searchExactMatch();
	}

	private void searchExactMatch() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
	}

	public void testStatusClosed010() throws Exception {
		connect010();
		statusClosed();
	}

	public void testStatusClosed011() throws Exception {
		connect011();
		statusClosed();
	}

	protected void statusClosed() throws Exception {
		TracTicket ticket = client.getTicket(data.offlineHandlerTicketId, null);
		assertEquals("closed", ticket.getValue(Key.STATUS));
		assertEquals("fixed", ticket.getValue(Key.RESOLUTION));
	}

}
