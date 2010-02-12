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

package org.eclipse.mylyn.oslc.cm.tests;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.client.AbstractOslcClient;
import org.eclipse.mylyn.internal.oslc.core.cm.AbstractChangeRequest;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Holds tests that exercise the Mylyn OSLC library against Rational Team Concert (RTC) OSLC api
 * <ul>
 * <li>To be run against a new RTC instance with BASIC auth enabled. BASE_URL need to be update.</li>
 * <li>A workitem exists with title "my first work item"</li>
 * </ul>
 * 
 * @author Robert Elves
 */
public class RtcOlscTest extends TestCase {
	private TaskRepository repository;

	private AbstractWebLocation location;

	private AbstractOslcClient client;

	private static final String BASE_URL = "https://172.16.166.130:9443/jazz/oslc/workitems/catalog";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = new TaskRepository("rtc", BASE_URL);
		this.repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("ADMIN", "ADMIN"),
				false);
		this.location = new TaskRepositoryLocationFactory().createWebLocation(repository);
		this.client = new AbstractOslcClient(location, new OslcServiceDescriptor(BASE_URL)) {

			@Override
			public RepositoryResponse putTaskData(TaskData taskData, Set<TaskAttribute> oldValues,
					IProgressMonitor monitor) throws CoreException {
				// ignore
				return null;
			}

			@Override
			public String getUserAgent() {
				// ignore
				return null;
			}

			@Override
			public TaskData getTaskData(String encodedTaskId, TaskAttributeMapper mapper, IProgressMonitor monitor)
					throws CoreException {
				// ignore
				return null;
			}

			@Override
			protected AbstractChangeRequest createChangeRequest(String id, String title) {
				return new AbstractChangeRequest(id, title) {
				};
			}
		};
	}

	/**
	 * Service Discovery
	 */
	public void testServiceCatalogParsing() throws Exception {
		List<OslcServiceProvider> services = client.getAvailableServices(BASE_URL, null);
		assertEquals(1, services.size());
		OslcServiceProvider desc = services.get(0);
		OslcServiceDescriptor serviceDescriptor = client.getServiceDescriptor(desc, null);
		assertTrue(serviceDescriptor.getSimpleQueryUrl().endsWith("/workitems"));
	}

	/**
	 * Simple Query for ChangeRequests
	 */
	public void testSimpleQuery() throws Exception {
		List<OslcServiceProvider> services = client.getAvailableServices(BASE_URL, null);
		OslcServiceProvider desc = services.get(0);
		OslcServiceDescriptor serviceDescriptor = client.getServiceDescriptor(desc, null);
		Collection<AbstractChangeRequest> result = client.performQuery(serviceDescriptor.getSimpleQueryUrl()
				+ "?oslc_cm.query=" + URLEncoder.encode("dc:title=\"my first work item\"", "UTF-8"), null);
		assertEquals(1, result.size());
		AbstractChangeRequest request = result.iterator().next();
		request.getTitle().equals("my first work item");

	}
}
