/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class WebSearchResultParserTest {

	private final WebSearchResultParser parser = new WebSearchResultParser();

	@Test
	public void testParseEmptyValue() throws Exception {
		String input = "id\tsummary\tmilestone\towner\ttype\tstatus\tpriority\n";
		input += "32\tsearchMilestone2 Ls9JM2\tmilestone1\t\tdefect\tnew\tmajor\n";
		parser.parse(new BufferedReader(new StringReader(input)));
		assertEquals(1, parser.getTickets().size());
		TracTicket ticket = parser.getTickets().get(0);
		assertEquals(32, ticket.getId());
		assertEquals("searchMilestone2 Ls9JM2", ticket.getValue(Key.SUMMARY));
		assertEquals("milestone1", ticket.getValue(Key.MILESTONE));
		assertEquals("", ticket.getValue(Key.OWNER));
		assertEquals("new", ticket.getValue(Key.STATUS));
		assertEquals("major", ticket.getValue(Key.PRIORITY));
	}

	@Test
	public void testParseEmptyValue_Trac0_11() throws Exception {
		String input = "id\towner\ttype\tstatus\tpriority\tcomponent\tversion\n";
		input += "58\t< default >\tdefect\tnew\tmajor\t--\t--\n";
		parser.parse(new BufferedReader(new StringReader(input)));
		assertEquals(1, parser.getTickets().size());
		TracTicket ticket = parser.getTickets().get(0);
		assertEquals(58, ticket.getId());
		assertEquals("< default >", ticket.getValue(Key.OWNER));
		assertEquals("defect", ticket.getValue(Key.TYPE));
		assertEquals("new", ticket.getValue(Key.STATUS));
		assertEquals("major", ticket.getValue(Key.PRIORITY));
		assertEquals(null, ticket.getValue(Key.COMPONENT));
		assertEquals(null, ticket.getValue(Key.VERSION));
	}

}
