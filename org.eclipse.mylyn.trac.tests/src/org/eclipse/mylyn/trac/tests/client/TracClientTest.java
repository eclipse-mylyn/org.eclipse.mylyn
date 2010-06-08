/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.Ticket;

/**
 * Test cases for classes that implement {@link ITracClient}.
 * 
 * @author Steffen Pingel
 */
public class TracClientTest extends TestCase {

	private ITracClient client;

	private TestData data;

	private TracFixture fixture;

	protected List<Ticket> tickets;

	public TracClientTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		fixture = TracFixture.current();
		client = fixture.connect();
		data = TracFixture.init010();
		tickets = data.tickets;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	public void testGetTicket() throws Exception {
		TracTicket ticket = client.getTicket(tickets.get(0).getId(), null);
		TracTestUtil.assertTicketEquals(tickets.get(0), ticket);

		ticket = client.getTicket(tickets.get(1).getId(), null);
		TracTestUtil.assertTicketEquals(tickets.get(1), ticket);
	}

	public void testGetTicketInvalidId() throws Exception {
//		if (version == Version.XML_RPC) {
//			return;
//		}
		try {
			client.getTicket(Integer.MAX_VALUE, null);
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testGetTicketUmlaute() throws Exception {
//		if (version == Version.TRAC_0_9) {
//			// XXX need to fix bug 175211
//			return;
//		}
		TracTicket ticket = client.getTicket(data.htmlEntitiesTicketId, null);
		assertEquals("test html entities: \u00E4\u00F6\u00FC", ticket.getValue(Key.SUMMARY));
		if (client.getAccessMode() == Version.XML_RPC) {
			assertEquals("\u00C4\u00D6\u00DC\n\nmulti\nline\n\n'''bold'''\n", ticket.getValue(Key.DESCRIPTION));
		} else {
			assertEquals(null, ticket.getValue(Key.DESCRIPTION));
		}
	}

	public void testProxy() throws Exception {
		client = fixture.connect(fixture.getRepositoryUrl(), "", "", new Proxy(Type.HTTP, new InetSocketAddress(
				"invalidhostname", 8080)));
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected IOException");
		} catch (TracException e) {
		}
	}

	public void testSearchAll() throws Exception {
		TracSearch search = new TracSearch();
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		//assertEquals(tickets.size(), result.size());
		assertTrue(result.size() >= tickets.size());
	}

	public void testSearchEmpty() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "does not exist");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(0, result.size());
	}

	public void testSearchExactMatch() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
	}

	public void testSearchMilestone1() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(tickets.get(0), result.get(0));
	}

	public void testSearchMilestone2() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(3, result.size());
		TracTestUtil.assertTicketEquals(tickets.get(0), result.get(0));
		TracTestUtil.assertTicketEquals(tickets.get(1), result.get(1));
		TracTestUtil.assertTicketEquals(tickets.get(2), result.get(2));
	}

	public void testSearchMilestoneAmpersand() throws Exception {
		// XXX re-enable for XML-RPC when bug 162094 is resolved
		if (client.getAccessMode() == Version.XML_RPC) {
			return;
		}
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "mile&stone");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(tickets.get(7), result.get(0));
	}

	public void testStatusClosed() throws Exception {
		TracTicket ticket = client.getTicket(data.offlineHandlerTicketId, null);
		assertEquals("closed", ticket.getValue(Key.STATUS));
		assertEquals("fixed", ticket.getValue(Key.RESOLUTION));
	}

	public void testUpdateAttributesAnonymous() throws Exception {
		client = fixture.connect(fixture.getRepositoryUrl(), "", "");
		assertNull(client.getMilestones());
		try {
			client.updateAttributes(new NullProgressMonitor(), true);
			if (fixture.getAccessMode() == Version.XML_RPC) {
				fail("Expected anonymous access to be denied");
			}
		} catch (TracPermissionDeniedException e) {
			if (fixture.getAccessMode() == Version.XML_RPC) {
				return; // expected exception, done here
			}
			throw e;
		}
		TracVersion[] versions = client.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("2.0", versions[1].getName());
	}

	public void testValidate() throws Exception {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		// standard connect
		client.validate(new NullProgressMonitor());

		// invalid url
		client = TracFixture.current().connect("http://non.existant/repository");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracException");
		} catch (TracException e) {
		}

		String url = TracFixture.current().getRepositoryUrl();

		// invalid password
		client = TracFixture.current().connect(url, credentials.username, "wrongpassword");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}

		// invalid username
		client = TracFixture.current().connect(url, "wrongusername", credentials.password);
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testValidateAnonymousLogin() throws Exception {
		client = fixture.connect(fixture.getRepositoryUrl(), "", "");
		try {
			client.validate(new NullProgressMonitor());
			if (fixture.getAccessMode() == Version.XML_RPC) {
				fail("Expected anonymous access to be denied");
			}
		} catch (TracPermissionDeniedException e) {
			if (fixture.getAccessMode() == Version.TRAC_0_9) {
				fail("Expected anonymous access to be allowed");
			}
		}
	}

	public void testValidateAnyPage() throws Exception {
		client = fixture.connect("http://mylyn.eclipse.org/");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

}
