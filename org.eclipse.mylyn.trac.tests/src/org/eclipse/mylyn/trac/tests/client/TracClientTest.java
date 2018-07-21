/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.TracAttribute;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.client.TracRemoteException;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracHarness;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;

/**
 * Test cases for classes that implement {@link ITracClient}.
 * 
 * @author Steffen Pingel
 */
public class TracClientTest extends TestCase {

	private ITracClient client;

	private TracFixture fixture;

	private TracHarness harness;

	public TracClientTest() {
	}

	@Override
	protected void setUp() throws Exception {
		fixture = TracFixture.current();
		harness = fixture.createHarness();
		client = fixture.connect();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testGetTicket() throws Exception {
		TracTicket expectedTicket = harness.createTicket("getTicket");
		TracTicket ticket = client.getTicket(expectedTicket.getId(), null);
		TracTestUtil.assertTicketEquals(client.getAccessMode(), expectedTicket, ticket);
	}

	public void testGetTicketInvalidId() throws Exception {
		try {
			client.getTicket(Integer.MAX_VALUE, null);
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testGetTicketUmlaute() throws Exception {
		TracTicket ticket = harness.newTicket("test html entities: \u00E4\u00F6\u00FC");
		ticket.putBuiltinValue(Key.DESCRIPTION, "\u00C4\u00D6\u00DC\n\nmulti\nline\n\n'''bold'''\n");
		ticket = harness.createTicket(ticket);

		ticket = client.getTicket(ticket.getId(), null);
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
		harness.createTicket("searchAllTickets");
		TracSearch search = new TracSearch();
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertTrue(!result.isEmpty());
	}

	public void testSearchEmpty() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "does not exist");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(0, result.size());
	}

	public void testSearchExactMatch() throws Exception {
		String uniqueTag = RandomStringUtils.randomAlphanumeric(6);
		String summary = "searchExactMatch " + uniqueTag;
		TracTicket ticket = harness.createTicketWithMilestone(summary, "milestone1");

		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("summary", summary);
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(ticket, result.get(0));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals(summary, result.get(0).getValue(Key.SUMMARY));
	}

	public void testSearchMilestone1() throws Exception {
		String uniqueTag = RandomStringUtils.randomAlphanumeric(6);
		TracTicket ticket = harness.createTicketWithMilestone("searchMilestone1" + uniqueTag, "milestone1");
		harness.createTicketWithMilestone("searchMilestone1" + uniqueTag, "milestone2");

		TracSearch search = new TracSearch();
		search.addFilter(new TracSearchFilter("summary", CompareOperator.CONTAINS, uniqueTag));
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(ticket, result.get(0));
	}

	public void testSearchMilestone2() throws Exception {
		String uniqueTag = RandomStringUtils.randomAlphanumeric(6);
		TracTicket ticket1 = harness.createTicketWithMilestone("searchMilestone2 " + uniqueTag, "milestone1");
		TracTicket ticket2 = harness.createTicketWithMilestone("searchMilestone2 " + uniqueTag, "milestone1");
		TracTicket ticket3 = harness.createTicketWithMilestone("searchMilestone2 " + uniqueTag, "milestone2");

		TracSearch search = new TracSearch();
		search.addFilter(new TracSearchFilter("summary", CompareOperator.CONTAINS, uniqueTag));
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(3, result.size());
		TracTestUtil.assertTicketEquals(ticket1, result.get(0));
		TracTestUtil.assertTicketEquals(ticket2, result.get(1));
		TracTestUtil.assertTicketEquals(ticket3, result.get(2));
	}

	public void testSearchMilestoneAmpersand() throws Exception {
		if (!harness.hasMilestone("mile&stone")) {
			// ignore test
			return;
		}

		TracTicket ticket = harness.createTicketWithMilestone("searchMilestoneAmpersand", "mile&stone");

		TracSearch search = new TracSearch();
		search.addFilter("milestone", "mile&stone");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		try {
			client.search(search, result, null);
			assertEquals(1, result.size());
			TracTestUtil.assertTicketEquals(ticket, result.get(0));
		} catch (TracRemoteException e) {
			if ("'Query filter requires field and constraints separated by a \"=\"' while executing 'ticket.query()'".equals(e.getMessage())
					&& (fixture.getVersion().equals("0.10") || fixture.getVersion().equals("0.11"))) {
				// ignore upstream problem, see bug 162094
			} else {
				throw e;
			}
		}
	}

	public void testStatusClosed() throws Exception {
		TracTicket ticket = harness.createTicket("statusClosed");
		ticket.putBuiltinValue(Key.STATUS, "closed");
		ticket.putBuiltinValue(Key.RESOLUTION, "fixed");
		harness.udpateTicket(ticket);

		ticket = client.getTicket(ticket.getId(), null);
		assertEquals("closed", ticket.getValue(Key.STATUS));
		assertEquals("fixed", ticket.getValue(Key.RESOLUTION));
	}

	public void testUpdateAttributesAnonymous() throws Exception {
		if (fixture.requiresAuthentication()) {
			return;
		}

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

	public void testUpdateAttributesChangedTicketFields() throws Exception {
		if (fixture.getAccessMode() == Version.TRAC_0_9) {
			// field information is not available in web mode
			return;
		}

		client = fixture.connect(fixture.getRepositoryUrl());
		client.updateAttributes(new NullProgressMonitor(), true);
		// modify field to bogus value
		TracTicketField field = client.getTicketFieldByName(TracAttribute.MILESTONE.getTracKey());
		field.setDefaultValue("modified default value");

		// updating should reset modified field
		client.updateAttributes(new NullProgressMonitor(), true);
		field = client.getTicketFieldByName(TracAttribute.MILESTONE.getTracKey());
		assertEquals("", field.getDefaultValue());
	}

	public void testValidate() throws Exception {
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);

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
		client = TracFixture.current().connect(url, credentials.getUserName(), "wrongpassword");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}

		// invalid username
		client = TracFixture.current().connect(url, "wrongusername", credentials.getPassword());
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testValidateAnonymousLogin() throws Exception {
		if (fixture.requiresAuthentication()) {
			return;
		}

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
