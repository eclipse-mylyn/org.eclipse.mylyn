/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.feed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.notifications.feed.ServiceMessageManager;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Robert Elves
 */
@SuppressWarnings("nls")
public class ServiceMessageManagerTest {

	private static final String MESSAGE_XML_URL = "http://mylyn.org/message.xml";

	@Before
	public void setUp() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}

	@Test
	@Ignore("No CI Server")
	public void testRetrievingMessage() throws Exception {
		ServiceMessageManager manager = new ServiceMessageManager(MESSAGE_XML_URL, "", "", 0l);
		int status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpURLConnection.HTTP_OK, status);
		assertEquals(2, manager.getServiceMessages().size());

		ServiceMessage message = manager.getServiceMessages().get(1);
		assertEquals("1", message.getId());
		assertEquals("140 character description here....", message.getDescription());
		assertEquals("Mylyn 3.4 now available!", message.getTitle());
		assertEquals("http://eclipse.org/mylyn/downloads", message.getUrl());
		assertEquals("dialog_messasge_info_image", message.getImage());
	}

	@Test
	@Ignore("No CI Server")
	public void testETag() throws Exception {
		ServiceMessageManager manager = new ServiceMessageManager(MESSAGE_XML_URL, "", "", 0l);
		int status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpURLConnection.HTTP_OK, status);
		ServiceMessage message = manager.getServiceMessages().get(0);

		assertNotNull(message.getLastModified());
		assertNotNull(message.getETag());

		status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpURLConnection.HTTP_NOT_MODIFIED, status);
	}

}
