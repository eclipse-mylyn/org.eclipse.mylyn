/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.trac.tests.support;

import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.trac.tests.client.AbstractTracClientTest;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * Utility that cleans up artifacts created by the Trac test suite. This class should be run periodically to speed up
 * execution of (attachment) tests.
 * 
 * @author Steffen Pingel
 */
public class TracTestCleanupUtil extends AbstractTracClientTest {

	private TestData data;

	public TracTestCleanupUtil() {
		super(Version.XML_RPC, PrivilegeLevel.ADMIN);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();
	}

	public void testCleanup010() throws Exception {
		connect010();
		cleanup();
	}

	public void testCleanup011() throws Exception {
		connect011();
		cleanup();
	}

	private void cleanup() throws TracException {
		TracTicket ticket = repository.getTicket(data.attachmentTicketId, null);
		TracAttachment[] attachments = ticket.getAttachments();
		// skips the first attachment
		for (int i = 1; i < attachments.length; i++) {
			repository.deleteAttachment(data.attachmentTicketId, attachments[i].getFilename(), null);
		}
	}

}
