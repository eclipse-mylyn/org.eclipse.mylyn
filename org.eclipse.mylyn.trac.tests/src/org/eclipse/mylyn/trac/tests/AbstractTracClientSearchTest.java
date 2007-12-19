/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.Ticket;

/**
 * Test cases that validate search results for classes that implement {@link ITracClient}.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractTracClientSearchTest extends AbstractTracClientTest {

	protected List<Ticket> tickets;

	private TestData data;

	public AbstractTracClientSearchTest(Version version) {
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

	protected void assertTicketEquals(Ticket ticket, TracTicket tracTicket) throws Exception {
		assertTrue(tracTicket.isValid());

		Map<?, ?> expectedValues = ticket.getValues();
		Map<String, String> values = tracTicket.getValues();
		for (String key : values.keySet()) {
			assertEquals("Values for key '" + key + "' did not match", expectedValues.get(key), values.get(key));
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
		TracTicket ticket = repository.getTicket(tickets.get(0).getId());
		assertTicketEquals(tickets.get(0), ticket);

		ticket = repository.getTicket(tickets.get(1).getId());
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
			repository.getTicket(Integer.MAX_VALUE);
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
		TracTicket ticket = repository.getTicket(data.htmlEntitiesTicketId);
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
		repository.search(search, result);
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
		repository.search(search, result);
		assertEquals(0, result.size());
	}

	public void testSearchMilestone1010() throws Exception {
		connect010();
		searchMilestone1();
	}

	public void testSearchMilestone1011() throws Exception {
		connect011();
		searchMilestone1();
	}

	private void searchMilestone1() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		repository.search(search, result);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
	}

	public void testSearchMilestone2010() throws Exception {
		connect010();
		searchMilestone2();
	}

	public void testSearchMilestone2011() throws Exception {
		connect011();
		searchMilestone2();
	}

	private void searchMilestone2() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		repository.search(search, result);
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
		repository.search(search, result);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
	}

}
