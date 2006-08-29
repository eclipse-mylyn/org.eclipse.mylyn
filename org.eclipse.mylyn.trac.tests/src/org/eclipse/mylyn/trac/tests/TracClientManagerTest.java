/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracClientManager;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.core.model.TracMilestone;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracClientManagerTest extends TestCase {

	public void testNullCache() throws Exception {
		TaskRepository taskRepository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, Constants.TEST_TRAC_096_URL);
		taskRepository.setVersion(Version.TRAC_0_9.name());
		
		TracClientManager manager = new TracClientManager(null);
		ITracClient client = manager.getRepository(taskRepository);
		assertNull(client.getMilestones()); 
		
		manager.writeCache();
		assertNull(client.getMilestones()); 
	}

	public void testReadCache() throws Exception {
		TaskRepository taskRepository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, Constants.TEST_TRAC_096_URL);
		taskRepository.setVersion(Version.TRAC_0_9.name());
		
		File file = File.createTempFile("mylar", null);
		file.deleteOnExit();
		
		TracClientManager manager = new TracClientManager(file);
		ITracClient client = manager.getRepository(taskRepository);
		assertNull(client.getMilestones()); 
	}
	
	public void testWriteCache() throws Exception {
		TaskRepository taskRepository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, Constants.TEST_TRAC_096_URL);
		taskRepository.setVersion(Version.TRAC_0_9.name());
		
		File file = File.createTempFile("mylar", null);
		file.deleteOnExit();
		
		TracClientManager manager = new TracClientManager(file);
		ITracClient client = manager.getRepository(taskRepository);
		assertNull(client.getMilestones());

		client.updateAttributes(new NullProgressMonitor(), false);
		assertTrue(client.getMilestones().length > 0);
		TracMilestone[] milestones = client.getMilestones();
		
		manager.writeCache();
		manager = new TracClientManager(file);
		assertEquals(Arrays.asList(milestones), Arrays.asList(client.getMilestones()));
	}
	
}
