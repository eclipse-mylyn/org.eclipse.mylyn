/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.core;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.TracClientManager;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.trac.tests.support.TracFixture;

/**
 * @author Steffen Pingel
 */
public class TracClientManagerTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = TracFixture.current().repository();
	}

	public void testNullCache() throws Exception {
		TracClientManager manager = new TracClientManager(null, new TaskRepositoryLocationFactory());
		ITracClient client = manager.getTracClient(repository);
		assertNull(client.getMilestones());

		manager.writeCache();
		assertNull(client.getMilestones());
	}

	public void testReadCache() throws Exception {
		File file = File.createTempFile("mylyn", null);
		file.deleteOnExit();

		TracClientManager manager = new TracClientManager(file, new TaskRepositoryLocationFactory());
		ITracClient client = manager.getTracClient(repository);
		assertNull(client.getMilestones());
	}

	public void testWriteCache() throws Exception {
		File file = File.createTempFile("mylyn", null);
		file.deleteOnExit();

		TracClientManager manager = new TracClientManager(file, new TaskRepositoryLocationFactory());
		ITracClient client = manager.getTracClient(repository);
		assertNull(client.getMilestones());

		client.updateAttributes(new NullProgressMonitor(), false);
		assertTrue(client.getMilestones().length > 0);
		TracMilestone[] milestones = client.getMilestones();

		manager.writeCache();
		manager = new TracClientManager(file, new TaskRepositoryLocationFactory());
		assertEquals(Arrays.asList(milestones), Arrays.asList(client.getMilestones()));
	}

}
