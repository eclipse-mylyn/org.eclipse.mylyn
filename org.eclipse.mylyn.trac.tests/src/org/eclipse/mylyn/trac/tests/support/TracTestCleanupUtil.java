/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * Utility that cleans up artifacts created by the Trac test suite. This class should be run periodically to speed up
 * execution of (attachment) tests.
 * 
 * @author Steffen Pingel
 */
public class TracTestCleanupUtil extends TestCase {

	private TestData data;

	private ITracClient client;

	@Override
	protected void setUp() throws Exception {
		data = TracFixture.init010();
	}

	public void testCleanup010() throws Exception {
		TracFixture fixture = TracFixture.TRAC_0_10_XML_RPC.activate();
		System.err.println("Connected to " + fixture.getRepositoryUrl());
		client = fixture.connect(PrivilegeLevel.ADMIN);
		deleteOldAttachments();
		deleteOldTickets();
	}

	public void testCleanup011() throws Exception {
		TracFixture fixture = TracFixture.TRAC_0_11_XML_RPC.activate();
		System.err.println("Connected to " + fixture.getRepositoryUrl());
		client = fixture.connect(PrivilegeLevel.ADMIN);
		deleteOldAttachments();
		deleteOldTickets();
	}

	public void testCleanup012() throws Exception {
		TracFixture fixture = TracFixture.TRAC_0_12_XML_RPC.activate();
		System.err.println("Connected to " + fixture.getRepositoryUrl());
		client = fixture.connect(PrivilegeLevel.ADMIN);
		deleteOldAttachments();
		deleteOldTickets();
	}

	public void testCleanupTrunk() throws Exception {
		TracFixture fixture = TracFixture.TRAC_TRUNK_XML_RPC.activate();
		System.err.println("Connected to " + fixture.getRepositoryUrl());
		client = fixture.connect(PrivilegeLevel.ADMIN);
		deleteOldAttachments();
		deleteOldTickets();
	}

	private void deleteOldAttachments() throws TracException {
		TracTicket ticket = client.getTicket(data.attachmentTicketId, null);
		TracAttachment[] attachments = ticket.getAttachments();
		System.err.println("Found " + attachments.length + " attachments");
		// skips the first attachment
		System.err.print("Deleting attachment: ");
		for (int i = 1; i < attachments.length; i++) {
			System.err.print(i + ", ");
			client.deleteAttachment(data.attachmentTicketId, attachments[i].getFilename(), null);
			if (i % 20 == 0) {
				System.err.println();
				System.err.print(" ");
			}
		}
		System.err.println();
	}

	public void deleteOldTickets() throws TracException {
		TracSearch query = new TracSearch();
		query.setMax(10000);
		List<Integer> result = new ArrayList<Integer>();
		client.searchForTicketIds(query, result, null);
		System.err.println("Found " + result.size() + " tickets");
		System.err.print("Deleting ticket: ");
		for (Integer i : result) {
			if (i > 10) {
				System.err.print(i + ", ");
				client.deleteTicket(i, null);
				if (i % 20 == 0) {
					System.err.println();
					System.err.print(" ");
				}
			}
		}
		System.err.println();
	}

}
