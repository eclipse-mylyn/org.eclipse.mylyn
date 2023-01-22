/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.client.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;

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
		ticket.putValue("summary", "a");
		assertEquals("a", ticket.getValue(Key.SUMMARY));
		assertEquals(null, ticket.getCustomValue("summary"));
		assertEquals(null, ticket.getCustomValue("a"));

		ticket.putValue("summary", "b");
		ticket.putValue("custom", "c");
		assertEquals("b", ticket.getValue(Key.SUMMARY));
		assertEquals(null, ticket.getCustomValue("summary"));
		assertEquals("c", ticket.getCustomValue("custom"));
	}

	public void testPutTracValueId() throws InvalidTicketException {
		TracTicket ticket = new TracTicket();
		assertFalse(ticket.putValue("id", "1"));
	}

	public void testSetCreated() throws InvalidTicketException {
		TracTicket ticket = new TracTicket(1);
		ticket.setCreated(TracUtil.parseDate(0));
		assertEquals(TimeZone.getTimeZone("GMT").getOffset(0) * 1000, ticket.getCreated().getTime());

		Date date = new Date();
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utc.setTime(date);
		ticket.setCreated(TracUtil.parseDate((int) (utc.getTimeInMillis() / 1000)));

		assertEquals(date.getTime() / 1000, ticket.getCreated().getTime() / 1000);
	}

}
