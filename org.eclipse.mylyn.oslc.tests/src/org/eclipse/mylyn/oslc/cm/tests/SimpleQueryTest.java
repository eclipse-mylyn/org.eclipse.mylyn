/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.oslc.cm.tests;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.tests.support.CommonTestUtil;
import org.eclipse.mylyn.internal.oslc.cm.ui.OslcClient;
import org.eclipse.mylyn.internal.oslc.core.IOslcCoreConstants;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.client.AbstractOslcClient;
import org.eclipse.mylyn.internal.oslc.core.cm.AbstractChangeRequest;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * @author Robert Elves
 */
public class SimpleQueryTest extends TestCase {
	private TaskRepository repository;
	private AbstractWebLocation location;
	private AbstractOslcClient client;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = new TaskRepository(
				IOslcCoreConstants.ID_PLUGIN,
				"http://mylyn.eclipse.org/oslc/cqrest");
		this.repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials("xxx", "xxx"), false);
		this.location = new TaskRepositoryLocationFactory()
				.createWebLocation(repository);
		this.client = new OslcClient(location,
				new OslcServiceDescriptor("http://mylyn.eclipse.org/oslc/cqrest"));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testParseQueryResponse() throws Exception {
		File file = CommonTestUtil.getFile(ServiceDiscoveryTest.class, "xml/query-response.xml");
		FileInputStream inStream = new FileInputStream(file);
		ArrayList list = new ArrayList();
		client.parseQueryResponse(inStream, list, new NullProgressMonitor());
		assertEquals(1, list.size());
		AbstractChangeRequest desc = (AbstractChangeRequest)list.get(0);
		assertEquals("test bug", desc.getTitle());
		assertEquals("10", desc.getIdentifier());
		assertEquals("", desc.getType()); // rdf:resource
		assertEquals("test description", desc.getDescription());
		assertEquals("", desc.getSubject());
		assertEquals("", desc.getCreator());  // rdf:resource
		assertEquals("2009-10-12T08:58:20.588Z", desc.getModified());
	}

}
