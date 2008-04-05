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

import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;

/**
 * @author Steffen Pingel
 */
public class TracXmlRpcClientSearchTest extends AbstractTracClientSearchTest {

	public TracXmlRpcClientSearchTest() {
		super(Version.XML_RPC);
	}

	public void testSearchValidateTicket010() throws Exception {
		connect010();
		searchValidateTicket();
	}

	public void testSearchValidateTicket011() throws Exception {
		connect011();
		searchValidateTicket();
	}

	public void searchValidateTicket() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		repository.search(search, result, null);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("component1", result.get(0).getValue(Key.COMPONENT));
		assertEquals("description1", result.get(0).getValue(Key.DESCRIPTION));
		assertEquals("milestone1", result.get(0).getValue(Key.MILESTONE));
		assertEquals("anonymous", result.get(0).getValue(Key.REPORTER));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
		// assertEquals("", result.get(0).getValue(Key.VERSION));
	}

}
