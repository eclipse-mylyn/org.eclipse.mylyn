/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.mylar.tasklist.repositories.TaskRepositoryManager;

/**
 * @author Mik Kersten
 */
public class RepositoryManagerTest extends TestCase {

	private TaskRepositoryManager manager;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		assertNotNull(manager);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testRepositoryPersistance() throws MalformedURLException {
//		System.err.println" + MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		assertEquals("", MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		
		TaskRepository repository = new TaskRepository(new URL("http://eclipse.org"));
		manager.addRepository(repository);
		
		assertNotNull(MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		
		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository);
		assertEquals(repositoryList, manager.readRepositories());
	}
}
