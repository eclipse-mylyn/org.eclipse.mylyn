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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class TracTicketTest extends TestCase {

	public void testValid() {
		TracTicket ticket = new TracTicket();
		assertFalse(ticket.isValid());

		ticket.setId(1);
		assertTrue(ticket.isValid());
	}

	public void testPutTracValue() throws InvalidTicketException {
		TracTicket ticket = new TracTicket(1);
		ticket.putTracValue("summary", "a");
		assertEquals("a", ticket.getValue(Key.SUMMARY));
		assertEquals(null, ticket.getCustomValue("summary"));
		assertEquals(null, ticket.getCustomValue("a"));

		ticket.putTracValue("summary", "b");
		ticket.putTracValue("custom", "c");
		assertEquals("b", ticket.getValue(Key.SUMMARY));
		assertEquals(null, ticket.getCustomValue("summary"));
		assertEquals("c", ticket.getCustomValue("custom"));
	}

	public void testPutTracValueId() throws InvalidTicketException {
		TracTicket ticket = new TracTicket();
		try {
			ticket.putTracValue("id", "1");
			fail("Expected RuntimeException");
		} catch (RuntimeException e) {
		}
	}

	public void testSetCreated() throws InvalidTicketException {
		TracTicket ticket = new TracTicket(1);
		ticket.setCreated(0);
		assertEquals(TimeZone.getTimeZone("GMT").getOffset(0) * 1000, ticket.getCreated().getTime());

		Date date = new Date();
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utc.setTime(date);
		ticket.setCreated((int) (utc.getTimeInMillis() / 1000));

		assertEquals(date.getTime() / 1000, ticket.getCreated().getTime() / 1000);
	}

}
