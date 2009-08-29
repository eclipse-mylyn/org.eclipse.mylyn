/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
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
		client = fixture.connect(PrivilegeLevel.ADMIN);
		cleanup();
	}

	public void testCleanup011() throws Exception {
		TracFixture fixture = TracFixture.TRAC_0_11_XML_RPC.activate();
		client = fixture.connect(PrivilegeLevel.ADMIN);
		cleanup();
	}

	private void cleanup() throws TracException {
		TracTicket ticket = client.getTicket(data.attachmentTicketId, null);
		TracAttachment[] attachments = ticket.getAttachments();
		// skips the first attachment
		for (int i = 1; i < attachments.length; i++) {
			client.deleteAttachment(data.attachmentTicketId, attachments[i].getFilename(), null);
		}
	}

}
