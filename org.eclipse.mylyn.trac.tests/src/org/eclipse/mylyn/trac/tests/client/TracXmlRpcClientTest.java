/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     Xiaoyang Guan - improvements
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.client.TracRemoteException;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.model.TracAction;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracHarness;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;

/**
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracXmlRpcClientTest extends TestCase {

	private static final int VERY_HIGH_REVISION = 100000;

	private TracXmlRpcClient client;

	private TracHarness harness;

	@Override
	protected void setUp() throws Exception {
		TracFixture fixture = TracFixture.current();
		harness = fixture.createHarness();
		client = (TracXmlRpcClient) fixture.connect();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testValidateFailNoAuth() throws Exception {
		if (harness.getFixture().requiresAuthentication()) {
			return;
		}
		client = (TracXmlRpcClient) TracFixture.current().connect(client.getUrl(), "", "");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracPermissionDeniedException");
		} catch (TracPermissionDeniedException e) {
		}
	}

	public void testMulticallExceptions() throws Exception {
		try {
			client.getTickets(new int[] { 1, Integer.MAX_VALUE }, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testSingleCallExceptions() throws Exception {
		try {
			client.getTicketLastChanged(Integer.MAX_VALUE, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testUpdateAttributes() throws Exception {
		assertNull(client.getMilestones());
		client.updateAttributes(new NullProgressMonitor(), true);
		TracVersion[] versions = client.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("", versions[0].getDescription());
		assertEquals(new Date(0), versions[0].getTime());
		assertEquals("2.0", versions[1].getName());
		assertEquals("", versions[1].getDescription());
		assertEquals(new Date(0), versions[1].getTime());
	}

	public void testSearchValidateTicket() throws Exception {
		String uniqueTag = RandomStringUtils.randomAlphanumeric(6);
		TracTicket ticket = harness.createTicket("searchValidateTicket " + uniqueTag);
		ticket.putBuiltinValue(Key.COMPONENT, "component1");
		ticket.putBuiltinValue(Key.DESCRIPTION, "description1");
		ticket.putBuiltinValue(Key.MILESTONE, "milestone1");
		ticket.putBuiltinValue(Key.REPORTER, "anonymous");
		client.updateTicket(ticket, "", null);

		TracSearch search = new TracSearch();
		search.addFilter(new TracSearchFilter("summary", CompareOperator.CONTAINS, uniqueTag));
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		// the value varies between Trac versions, e.g. "somebody", "< default >"
		ticket.putBuiltinValue(Key.OWNER, result.get(0).getValue(Key.OWNER));
		TracTestUtil.assertTicketEquals(ticket, result.get(0));
	}

	public void testGetTicketActions() throws Exception {
		TracTicket ticket = harness.createTicket("getTicketActions");
		TracAction[] actions = ticket.getActions();
		assertNotNull(actions);
		assertEquals(4, actions.length);
		assertEquals("leave", actions[0].getId());
		assertEquals("resolve", actions[1].getId());
		assertEquals("resolve", actions[1].getLabel());
		assertNotNull(actions[1].getHint());
		List<TracTicketField> fields = actions[1].getFields();
		assertEquals(1, fields.size());
		assertEquals(5, fields.get(0).getOptions().length);
		assertEquals("fixed", fields.get(0).getOptions()[0]);
		assertEquals("reassign", actions[2].getId());
		fields = actions[2].getFields();
		assertEquals(1, fields.size());
		assertNull(fields.get(0).getOptions());
		assertEquals("accept", actions[3].getId());
	}

	public void testGetTicketActionsClosed() throws Exception {
		TracTicket ticket = harness.createTicket("getTicketActionsClosed");
		ticket.putBuiltinValue(Key.STATUS, "closed");
		ticket.putBuiltinValue(Key.RESOLUTION, "fixed");
		client.updateTicket(ticket, "", null);

		ticket = client.getTicket(ticket.getId(), null);
		TracAction[] actions = ticket.getActions();
		assertNotNull(actions);
		assertEquals(2, actions.length);
		assertEquals("leave", actions[0].getId());
		assertEquals("reopen", actions[1].getId());
	}

	public void testWikiToHtml() throws Exception {
		String tracUrl = client.getUrl();
		if (tracUrl.endsWith("/")) {
			tracUrl = tracUrl.substring(0, tracUrl.length() - 1);
		}
		String html = client.wikiToHtml("", null);
		assertEquals("", html);

		html = client.wikiToHtml("A simple line of text.", null);
		assertEquals("<p>\nA simple line of text.\n</p>\n", html);

		String source = "= WikiFormattingTesting =\n" + " * '''bold''', '''!''' can be bold too''', and '''! '''\n"
				+ " * ''italic''\n" + " * '''''bold italic'''''\n" + " * __underline__\n"
				+ " * {{{monospace}}} or `monospace`\n" + " * ~~strike-through~~\n" + " * ^superscript^ \n"
				+ " * ,,subscript,,\n" + "= Heading =\n" + "== Subheading ==\n";

		String expectedHtml;
		if (TracFixture.current().getVersion().compareTo("0.12") >= 0) {
			// the output format has changed in Trac 0.12
			expectedHtml = "<h1 id=\"WikiFormattingTesting\"><a class=\"missing wiki\" href=\""
					+ tracUrl
					+ "/wiki/WikiFormattingTesting\" rel=\"nofollow\">WikiFormattingTesting?</a></h1>\n<ul><li><strong>bold</strong>, <strong>\'\'\' can be bold too</strong>, and <strong>! </strong>\n</li><li><em>italic</em>\n</li><li><strong><em>bold italic</em></strong>\n</li><li><span class=\"underline\">underline</span>\n</li><li><tt>monospace</tt> or <tt>monospace</tt>\n</li><li><del>strike-through</del>\n</li><li><sup>superscript</sup> \n</li><li><sub>subscript</sub>\n</li></ul><h1 id=\"Heading\">Heading</h1>\n<h2 id=\"Subheading\">Subheading</h2>\n";
		} else {
			expectedHtml = "<h1 id=\"WikiFormattingTesting\"><a class=\"missing wiki\" href=\""
					+ tracUrl
					+ "/wiki/WikiFormattingTesting\" rel=\"nofollow\">WikiFormattingTesting?</a></h1>\n<ul><li><strong>bold</strong>, <strong>\'\'\' can be bold too</strong>, and <strong>! </strong>\n</li><li><i>italic</i>\n</li><li><strong><i>bold italic</i></strong>\n</li><li><span class=\"underline\">underline</span>\n</li><li><tt>monospace</tt> or <tt>monospace</tt>\n</li><li><del>strike-through</del>\n</li><li><sup>superscript</sup> \n</li><li><sub>subscript</sub>\n</li></ul><h1 id=\"Heading\">Heading</h1>\n<h2 id=\"Subheading\">Subheading</h2>\n";
		}
		html = client.wikiToHtml(source, null);
		assertEquals(expectedHtml, html);
	}

	public void testValidateWikiAPI() throws Exception {
		client.validateWikiRpcApi(null);
	}

	public void testGetAllWikiPageNames() throws Exception {
		String[] names = client.getAllWikiPageNames(null);
		List<String> all = Arrays.asList(names);
		assertTrue(all.contains("Test"));
	}

	public void testGetWikiPage() throws Exception {
		String pageName = "TestGetPage" + RandomStringUtils.randomAlphanumeric(6);
		harness.createWikiPage(pageName, "Version 1");
		harness.createWikiPage(pageName, "Version 2");

		TracWikiPage page = client.getWikiPage(pageName, null);
		assertEquals(pageName, page.getPageInfo().getPageName());
		assertEquals("tests@mylyn.eclipse.org", page.getPageInfo().getAuthor());
		assertEquals(2, page.getPageInfo().getVersion());
		// XXX The Date returned from Wiki API seems to have a problem with the Time Zone
		//String date = "Sat Nov 11 18:10:56 EST 2006";
		//assertEquals(date, page.getPageVersion().getLastModified().toString());
		assertEquals("Version 2", page.getContent());
		assertTrue(page.getPageHTML().startsWith("<html>"));

		page = client.getWikiPage(pageName, 1, null);
		assertEquals(pageName, page.getPageInfo().getPageName());
		assertEquals("tests@mylyn.eclipse.org", page.getPageInfo().getAuthor());
		assertEquals(1, page.getPageInfo().getVersion());
		assertEquals("Version 1", page.getContent());
		assertTrue(page.getPageHTML().startsWith("<html>"));
	}

	public void testGetWikiPageInfoInvalidRevision() throws Exception {
		try {
			client.getWikiPageInfo("Test", VERY_HIGH_REVISION, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageInfoInvalidPage() throws Exception {
		try {
			client.getWikiPageInfo("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageInfoNull() throws Exception {
		try {
			client.getWikiPageInfo(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetWikiPageContentInvalidRevision() throws Exception {
		try {
			client.getWikiPageContent("Test", VERY_HIGH_REVISION, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageContentInvalidPage() throws Exception {
		try {
			client.getWikiPageContent("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageContentContentNull() throws Exception {
		try {
			client.getWikiPageContent(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetWikiPageHtmlInvalidRevision() throws Exception {
		try {
			client.getWikiPageHtml("Test", VERY_HIGH_REVISION, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageHtmlInvalidPage() throws Exception {
		try {
			client.getWikiPageHtml("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageHtmlNull() throws Exception {
		try {
			client.getWikiPageHtml(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetWikiPageInvalidRevision() throws Exception {
		try {
			client.getWikiPage("Test", VERY_HIGH_REVISION, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageInvalidPage() throws Exception {
		try {
			client.getWikiPage("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiPageNull() throws Exception {
		try {
			client.getWikiPage(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetWikiInfoAllVersionsInvalidPage() throws Exception {
		try {
			client.getWikiPageInfoAllVersions("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testGetWikiInfoAllVersionsInvalidPageNull() throws Exception {
		try {
			client.getWikiPageInfoAllVersions(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

//	public void testGetWikiPageInfoAllVersions() throws Exception {
//		String pageName = "Test";
//		harness.createWikiPage(pageName, "test content " + RandomStringUtils.randomAlphanumeric(6));
//
//		TracWikiPageInfo[] versions = client.getWikiPageInfoAllVersions(pageName, null);
//		assertTrue(versions.length >= 1);
//		int counter = 1;
//		for (TracWikiPageInfo version : versions) {
//			assertTrue(version.getPageName().equals(pageName));
//			assertTrue(version.getVersion() == counter++); // assuming versions are ordered increasingly
//		}
//	}

	public void testGetRecentWikiChanges() throws Exception {
		harness.createWikiPage("Test", "test content " + RandomStringUtils.randomAlphanumeric(6));
		TracWikiPageInfo[] changes = client.getRecentWikiChanges(new Date(0), null);
		TracWikiPageInfo testPage = null;
		for (TracWikiPageInfo item : changes) {
			assertTrue(item.getPageName() != null);
			if (item.getPageName().equals("Test")) {
				testPage = item;
			}
		}
		assertTrue(testPage != null);
	}

	public void testInvalidCharacters() throws Exception {
		TracTicket ticket = harness.createTicket("invalid characters");
		ticket.putBuiltinValue(Key.DESCRIPTION, "Control Character: \u0002");
		try {
			client.updateTicket(ticket, "set invalid characters in description", null);
			fail("Expected TracException");
		} catch (TracException e) {
			// expected to cause parse error
		}
	}

}
