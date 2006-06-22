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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.internal.trac.core.ITracRepository;
import org.eclipse.mylar.internal.trac.core.TracXmlRpcRepository;
import org.eclipse.mylar.internal.trac.core.ITracRepository.Version;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * @author Steffen Pingel
 */
public class TracXmlRpcRepositorySearchTest extends AbstractTracRepositorySearchTest {

	public TracXmlRpcRepositorySearchTest() {
		super(new AbstractTracRepositoryFactory() {
			protected ITracRepository createRepository(String url, String username, String password) throws Exception {
				return new TracXmlRpcRepository(new URL(url), Version.XML_RPC, username, password);
			}
		});
	}

	public void testSearchValidateTicket() throws Exception {
		TracSearch search = new TracSearch();
		search.addFilter("summary", "summary1");
		List<TracTicket> result = new ArrayList<TracTicket>();
		factory.repository.search(search, result);
		assertEquals(1, result.size());
		assertTicketEquals(tickets.get(0), result.get(0));
		assertEquals("component1", result.get(0).getValue(Key.COMPONENT));
		assertEquals("description1", result.get(0).getValue(Key.DESCRIPTION));
		assertEquals("m1", result.get(0).getValue(Key.MILESTONE));
		assertEquals(factory.username, result.get(0).getValue(Key.REPORTER));
		assertEquals("summary1", result.get(0).getValue(Key.SUMMARY));
		assertEquals("", result.get(0).getValue(Key.VERSION));
	}

}
