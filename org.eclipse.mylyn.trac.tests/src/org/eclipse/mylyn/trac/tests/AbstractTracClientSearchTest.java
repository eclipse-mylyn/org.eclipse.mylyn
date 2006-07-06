/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;
import org.eclipse.mylar.trac.tests.support.TestFixture;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.Ticket;

/**
 * Test cases that validate search results for classes that implement
 * {@link ITracRepositor}.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractTracClientSearchTest extends TestCase {

	protected AbstractTracRepositoryFactory factory;

	protected List<Ticket> tickets;

	public AbstractTracClientSearchTest(AbstractTracRepositoryFactory factory) {
		this.factory = factory;
	}

	protected void setUp() throws Exception {
		super.setUp();

		TestData data = TestFixture.initializeRepository1();
		tickets = data.tickets;

		factory.connectRepository1();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
 
		// TestFixture.cleanupRepository1();
	}

	protected void assertTicketEquals(Ticket ticket, TracTicket tracTicket) throws Exception {
		assertTrue(tracTicket.isValid());

		Hashtable expectedValues = ticket.getValues();
		Map<String, String> values = tracTicket.getValues();
		for (String key : values.keySet()) {
			assertEquals("Values for key '" + key + "' did not match", expectedValues.get(key), values.get(key));
		}
	}

	public void testGetTicket() throws Exception {
		TracTicket ticket = factory.repository.getTicket(tickets.get(0).getId());
		assertTicketEquals(tickets.get(0), ticket);

		ticket = factory.repository.getTicket(tickets.get(1).getId());
		assertTicketEquals(tickets.get(1), ticket);
	}

	public void testGetTicketInvalidId() throws Exception {
		try {
			factory.repository.getTicket(Integer.MAX_VALUE);
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testSearchAll() throws Exception {
		TracSearch search = new TracSearch();
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(tickets.size(), result.size());
	}

	public void testSearchEmpty() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "does not exist");
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(0, result.size());
	}

	public void testSearchMilestone1() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "m1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
	}

	public void testSearchMilestone2() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "m1");
		search.addFilter("milestone", "m2");
		search.setOrderBy("id");
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(3, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertTicketEquals(tickets.get(1), result.get(1));
		assertTicketEquals(tickets.get(2), result.get(2));
	}

	public void testSearchExactMatch() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "m1");
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("m1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
	}

}
