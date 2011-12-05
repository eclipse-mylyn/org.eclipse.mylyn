/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.feed;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.notifications.feed.ServiceMessageManager;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;

/**
 * @author Robert Elves
 */
public class ServiceMessageManagerTest extends TestCase {

	private static final String MESSAGE_XML_URL = "http://mylyn.eclipse.org/message.xml";

	public void testRetrievingMessage() throws Exception {
		ServiceMessageManager manager = new ServiceMessageManager(MESSAGE_XML_URL, "", "", 0l);
		int status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpStatus.SC_OK, status);
		assertEquals(2, manager.getServiceMessages().size());

		ServiceMessage message = manager.getServiceMessages().get(1);
		assertEquals("1", message.getId());
		assertEquals("140 character description here....", message.getDescription());
		assertEquals("Mylyn 3.4 now available!", message.getTitle());
		assertEquals("http://eclipse.org/mylyn/downloads", message.getUrl());
		assertEquals("Mylyn 3.4 now available!", message.getTitle());
		assertEquals("dialog_messasge_info_image", message.getImage());
	}

	public void testETag() throws Exception {
		ServiceMessageManager manager = new ServiceMessageManager(MESSAGE_XML_URL, "", "", 0l);
		int status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpStatus.SC_OK, status);
		ServiceMessage message = manager.getServiceMessages().get(0);

		assertNotNull(message.getLastModified());
		assertNotNull(message.getETag());

		status = manager.refresh(new NullProgressMonitor());
		assertEquals(HttpStatus.SC_NOT_MODIFIED, status);
	}

}
