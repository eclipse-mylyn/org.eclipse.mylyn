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

package org.eclipse.mylyn.tasks.tests.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessageManager;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessageXmlHandler;

/**
 * @author Robert Elves
 */
public class ServiceMessageManagerTest extends TestCase {

	private static final String MESSAGE_XML_URL = "http://mylyn.eclipse.org/message.xml";

	public void testRetrievingMessage() throws Exception {
		ServiceMessageManager manager = new ServiceMessageManager(MESSAGE_XML_URL, "", "", 0l);
		int status = manager.updateServiceMessage(new NullProgressMonitor());
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
		int status = manager.updateServiceMessage(new NullProgressMonitor());
		assertEquals(HttpStatus.SC_OK, status);
		ServiceMessage message = manager.getServiceMessages().get(0);

		assertNotNull(message.getLastModified());
		assertNotNull(message.getETag());

		status = manager.updateServiceMessage(new NullProgressMonitor());
		assertEquals(HttpStatus.SC_NOT_MODIFIED, status);
	}

	public void testParsingMessageXml() throws Exception {
		String messageXml = "<ServiceMessage> <id>1</id><description>140 character description here....</description><title>Mylyn 3.4 now available!</title><url>http://eclipse.org/mylyn/downloads</url><image>dialog_messasge_info_image</image></ServiceMessage>";
		InputStream is = new ByteArrayInputStream(messageXml.getBytes("UTF-8"));
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		SAXParser parser = factory.newSAXParser();
		ServiceMessageXmlHandler handler = new ServiceMessageXmlHandler();
		parser.parse(is, handler);
		ServiceMessage message = handler.getMessages().get(0);

		assertEquals("1", message.getId());
		assertEquals("140 character description here....", message.getDescription());
		assertEquals("Mylyn 3.4 now available!", message.getTitle());
		assertEquals("http://eclipse.org/mylyn/downloads", message.getUrl());
		assertEquals("Mylyn 3.4 now available!", message.getTitle());
		assertEquals("dialog_messasge_info_image", message.getImage());
	}

}
