/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.client.TracRemoteException;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.model.TracAction;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.Ticket;

/**
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class TracXmlRpcClientTest extends TestCase {

	protected List<Ticket> tickets;

	private TestData data;

	private ITracClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		client = TracFixture.current().connect();
		data = TracFixture.init010();
		tickets = data.tickets;
	}

	public void testValidateFailNoAuth() throws Exception {
		client = TracFixture.current().connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		try {
			client.validate(new NullProgressMonitor());
			fail("Expected TracPermissiongDeniedException");
		} catch (TracPermissionDeniedException e) {
		}
	}

	public void testMulticallExceptions() throws Exception {
		try {
			((TracXmlRpcClient) client).getTickets(new int[] { 1, Integer.MAX_VALUE }, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testSingleCallExceptions() throws Exception {
		try {
			((TracXmlRpcClient) client).getTicketLastChanged(Integer.MAX_VALUE, null);
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
		TracSearch search = new TracSearch();
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		client.search(search, result, null);
		assertEquals(1, result.size());
		TracTestUtil.assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("component1", result.get(0).getValue(Key.COMPONENT));
		assertEquals("description1", result.get(0).getValue(Key.DESCRIPTION));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("anonymous", result.get(0).getValue(Key.REPORTER));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
		// assertEquals("", result.get(0).getValue(Key.VERSION));
	}

	public void testGetTicketActions() throws Exception {
		if (client.getUrl().equals(TracTestConstants.TEST_TRAC_010_URL)) {
			TracTicket ticket = client.getTicket(tickets.get(0).getId(), null);
			TracAction[] actions = ticket.getActions();
			assertNotNull(actions);
			assertEquals(4, actions.length);
			assertEquals("leave", actions[0].getId());
			assertNull(actions[0].getLabel());
			assertEquals(0, actions[0].getFields().size());
			assertEquals("resolve", actions[1].getId());
			assertNull(actions[1].getLabel());
			assertEquals(0, actions[1].getFields().size());
			assertEquals("reassign", actions[2].getId());
			assertNull(actions[2].getLabel());
			assertEquals(0, actions[2].getFields().size());
			assertEquals("accept", actions[3].getId());
			assertNull(actions[3].getLabel());
			assertEquals(0, actions[3].getFields().size());

			ticket = client.getTicket(tickets.get(1).getId(), null);
			actions = ticket.getActions();
			assertNotNull(actions);
			assertEquals(2, actions.length);
			assertEquals("leave", actions[0].getId());
			assertEquals("reopen", actions[1].getId());
		} else {
			TracTicket ticket = client.getTicket(tickets.get(0).getId(), null);
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

			ticket = client.getTicket(tickets.get(1).getId(), null);
			actions = ticket.getActions();
			assertNotNull(actions);
			assertEquals(2, actions.length);
			assertEquals("leave", actions[0].getId());
			assertEquals("reopen", actions[1].getId());
		}
	}

	public void testWikiToHtml() throws Exception {
		String tracUrl = client.getUrl();
		String html = ((TracXmlRpcClient) client).wikiToHtml("", null);
		assertEquals("", html);

		html = ((TracXmlRpcClient) client).wikiToHtml("A simple line of text.", null);
		assertEquals("<p>\nA simple line of text.\n</p>\n", html);

		String source = "= WikiFormattingTesting =\n" + " * '''bold''', '''!''' can be bold too''', and '''! '''\n"
				+ " * ''italic''\n" + " * '''''bold italic'''''\n" + " * __underline__\n"
				+ " * {{{monospace}}} or `monospace`\n" + " * ~~strike-through~~\n" + " * ^superscript^ \n"
				+ " * ,,subscript,,\n" + "= Heading =\n" + "== Subheading ==\n";

		String expectedHtml = "<h1 id=\"WikiFormattingTesting\"><a class=\"missing wiki\" href=\""
				+ tracUrl
				+ "/wiki/WikiFormattingTesting\" rel=\"nofollow\">WikiFormattingTesting?</a></h1>\n<ul><li><strong>bold</strong>, <strong>\'\'\' can be bold too</strong>, and <strong>! </strong>\n</li><li><i>italic</i>\n</li><li><strong><i>bold italic</i></strong>\n</li><li><span class=\"underline\">underline</span>\n</li><li><tt>monospace</tt> or <tt>monospace</tt>\n</li><li><del>strike-through</del>\n</li><li><sup>superscript</sup> \n</li><li><sub>subscript</sub>\n</li></ul><h1 id=\"Heading\">Heading</h1>\n<h2 id=\"Subheading\">Subheading</h2>\n";

		html = ((TracXmlRpcClient) client).wikiToHtml(source, null);
		assertEquals(expectedHtml, html);
	}

	public void testValidateWikiAPI() throws Exception {
		((TracXmlRpcClient) client).validateWikiRpcApi(null);
	}

	public void testGetAllWikiPageNames() throws Exception {
		String[] names = ((TracXmlRpcClient) client).getAllWikiPageNames(null);
		List<String> all = Arrays.asList(names);
		assertTrue(all.contains("Test"));
	}

	public void testGetWikiPage() throws Exception {
		TracWikiPage page = ((TracXmlRpcClient) client).getWikiPage("TestGetPage", null);
		assertEquals("TestGetPage", page.getPageInfo().getPageName());
		assertEquals("tests@mylyn.eclipse.org", page.getPageInfo().getAuthor());
		assertEquals(2, page.getPageInfo().getVersion());
		// XXX The Date returned from Wiki API seems to have a problem with the Time Zone
		//String date = "Sat Nov 11 18:10:56 EST 2006";
		//assertEquals(date, page.getPageVersion().getLastModified().toString());
		assertEquals("Version 2", page.getContent());
		assertTrue(page.getPageHTML().startsWith("<html>"));

		page = ((TracXmlRpcClient) client).getWikiPage("TestGetPage", 1, null);
		assertEquals("TestGetPage", page.getPageInfo().getPageName());
		assertEquals("anonymous", page.getPageInfo().getAuthor());
		assertEquals(1, page.getPageInfo().getVersion());
		assertEquals("Version 1", page.getContent());
		assertTrue(page.getPageHTML().startsWith("<html>"));
	}

	public void testGetWikiPageInvalid() throws Exception {
		// get info -- non-existing version
		try {
			((TracXmlRpcClient) client).getWikiPageInfo("Test", 10, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get info -- non-existing page name
		try {
			((TracXmlRpcClient) client).getWikiPageInfo("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get info -- null parameter
		try {
			((TracXmlRpcClient) client).getWikiPageInfo(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}

		// get content -- non-existing version
		try {
			((TracXmlRpcClient) client).getWikiPageContent("Test", 10, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get content -- non-existing page name
		try {
			((TracXmlRpcClient) client).getWikiPageContent("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get content -- null parameter
		try {
			((TracXmlRpcClient) client).getWikiPageContent(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}

		// get HTML -- non-existing version
		try {
			((TracXmlRpcClient) client).getWikiPageHtml("Test", 10, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get HTML -- non-existing page name
		try {
			((TracXmlRpcClient) client).getWikiPageHtml("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get HTML -- null parameter
		try {
			((TracXmlRpcClient) client).getWikiPageHtml(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}

		// get a page -- non-existing version
		try {
			((TracXmlRpcClient) client).getWikiPage("Test", 10, null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get a page -- non-existing page name
		try {
			((TracXmlRpcClient) client).getWikiPage("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get a page -- null parameter
		try {
			((TracXmlRpcClient) client).getWikiPage(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}

		// get all versions of a page -- non-existing page name
		try {
			((TracXmlRpcClient) client).getWikiPageInfoAllVersions("NoSuchPage", null);
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}

		// get all versions of a page -- null parameter
		try {
			((TracXmlRpcClient) client).getWikiPageInfoAllVersions(null, null);
			fail("Expected RuntimeException");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetWikiPageInfoAllVersions() throws Exception {
		String pageName = "Test";

		TracWikiPageInfo[] versions = ((TracXmlRpcClient) client).getWikiPageInfoAllVersions(pageName, null);
		assertTrue(versions.length >= 1);
		int counter = 1;
		for (TracWikiPageInfo version : versions) {
			assertTrue(version.getPageName().equals(pageName));
			assertTrue(version.getVersion() == counter++); // assuming versions are ordered increasingly
		}
	}

	public void testGetRecentWikiChanges() throws Exception {
		// FIXME 3.4 re-enable for trunk
		if (TracFixture.current() == TracFixture.TRAC_TRUNK_XML_RPC) {
			return;
		}

		TracWikiPageInfo[] changes = ((TracXmlRpcClient) client).getRecentWikiChanges(new Date(0), null);
		TracWikiPageInfo testPage = null;
		for (TracWikiPageInfo item : changes) {
			assertTrue(item.getPageName() != null);
			if (item.getPageName().equals("Test")) {
				testPage = item;
			}
		}
		assertTrue(testPage != null);
	}

	public void testPutWikiPage() throws Exception {
		// TODO testing wiki.putPage()
	}

}
