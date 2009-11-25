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
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.tests.support.CommonTestUtil;
import org.eclipse.mylyn.internal.oslc.cm.ui.OslcClient;
import org.eclipse.mylyn.internal.oslc.core.OslcCreationDialogDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcSelectionDialogDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.client.AbstractOslcClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * @author Robert Elves
 */
public class ServiceDiscoveryTest extends TestCase {
	private TaskRepository repository;
	private AbstractWebLocation location;
	private AbstractOslcClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = new TaskRepository("myoslcconnetorkind"
			,
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
	

	public void testServiceCatalogParsing() throws IOException, CoreException  {
		File file = CommonTestUtil.getFile(ServiceDiscoveryTest.class, "xml/service-provider-catalog.xml");
		FileInputStream inStream = new FileInputStream(file);
		ArrayList list = new ArrayList();
		client.parseServices(inStream, list, new NullProgressMonitor());
		assertEquals(1, list.size());
		OslcServiceProvider desc = (OslcServiceProvider)list.get(0);
		assertEquals("http://mylyn.eclipse.org/oslc/cqrest/repo/7.0.0/db/SAMPL", desc.getUrl());
	}
	
	public void testServiceDescriptorParsing() throws IOException, CoreException {
		File file = CommonTestUtil.getFile(ServiceDiscoveryTest.class, "xml/service-descriptor.xml");
		FileInputStream inStream = new FileInputStream(file);
		ArrayList list = new ArrayList();
		OslcServiceDescriptor desc = new OslcServiceDescriptor("http://mylyn.eclipse.org/oslc/cqrest/repo/7.0.0/db/SAMPL");
		client.parseServiceDescriptor(inStream, desc, new NullProgressMonitor());
		
		assertEquals("RCM/CQ OSLC CM Service Description Document", desc.getTitle());
		assertEquals("Rational Change Management/ClearQuest OSLC CM Services available for 7.0.0/SAMPL.", desc.getDescription());
		
		// ServiceHome
		assertEquals("Change Requests", desc.getHome().getTitle());
		assertEquals("http://mylyn.eclipse.org/cqweb/restapi/7.0.0/SAMPL?format=html", desc.getHome().getUrl());
		
		// Creation Dialogs
		OslcCreationDialogDescriptor creationDialog = desc.getDefaultCreationDialog();
		assertEquals("New Defect - IBM Rational ClearQuest", creationDialog.getTitle());
		assertEquals("http://mylyn.eclipse.org/oslc/cqrest/repo/7.0.0/db/SAMPL/record-type/16777224/creationDialog?dc%3Atype=Defect", creationDialog.getUrl());
		
		// Factory
		assertEquals("Unattended location for the creation of ClearQuest Records", desc.getDefaultFactory().getTitle());
		assertEquals("http://mylyn.eclipse.org/oslc/cqrest/repo/7.0.0/db/SAMPL/record", desc.getDefaultFactory().getUrl());
		
		// Selection Dialogs
		
		assertTrue(desc.getSelectionDialogs().size() > 0);
		OslcSelectionDialogDescriptor selectionDialog = desc.getDefaultSelectionDialog();
		assertNotNull(selectionDialog);
		assertEquals("640px", selectionDialog.getHintWidth());
		assertEquals("540px", selectionDialog.getHintHeight());
		assertEquals("Choose ClearQuest Record - IBM Rational ClearQuest", selectionDialog.getTitle());
		assertEquals("ClearQuest Record", selectionDialog.getLabel());
		assertEquals("http://mylyn.eclipse.org/cqweb/chooseRecord.cq?type=cq.repo.cq-rectype%3A16777224%407.0.0%2FSAMPL&restrictType=false", selectionDialog.getUrl());
	}
	


}
