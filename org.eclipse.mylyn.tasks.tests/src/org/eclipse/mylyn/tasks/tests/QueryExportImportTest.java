/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTaskListFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Jevgeni Holodkov 
 */
public class QueryExportImportTest extends 	TestCase {

	private File dest;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		removeFiles(new File(TasksUiPlugin.getDefault().getDataDirectory()));
		
		// Create test export destination directory
		dest = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "TestDir");
		if (dest.exists()) {
			removeFiles(dest);
		} else {
			dest.mkdir();
		}
		assertTrue(dest.exists());
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		removeFiles(dest);
		dest.delete();
		assertFalse(dest.exists());
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(false);
	}
	
	public void testExportImportQuery() {
		List<AbstractTaskListFactory> externalizers = new ArrayList<AbstractTaskListFactory>();
		externalizers.add(new MockTaskListFactory());
		TasksUiPlugin.getTaskListManager().getTaskListWriter().setDelegateExternalizers(externalizers);

		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		
		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
		MockRepositoryQuery query2 = new MockRepositoryQuery("Test Query 2");
		queries.add(query1);
		queries.add(query2);

		File outFile = new File(dest, "test-query.xml.zip");
		
		TasksUiPlugin.getTaskListManager().getTaskListWriter().writeQueries(queries, outFile);
		assertTrue(outFile.exists());
		
		File inFile = new File(dest, "test-query.xml.zip");
		List<AbstractRepositoryQuery> resultQueries = TasksUiPlugin.getTaskListManager().getTaskListWriter().readQueries(
				inFile);		
		assertEquals("2 Queries is imported", 2, resultQueries.size());
	}
	
	public void testImportedQueriesNameConflictResolving1() {
		// prepare for test
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
		
		// create test data
		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		
		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
		query1.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query2 = new MockRepositoryQuery("Test Query");
		query2.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query3 = new MockRepositoryQuery("Test Query");
		query3.setRepositoryUrl(repository.getUrl());
		
		queries.add(query1);
		queries.add(query2);
		queries.add(query3);
		
		// run tested functionality
		TasksUiPlugin.getTaskListManager().insertQueries(queries);
		
		// extract results
		Set<AbstractRepositoryQuery> queriesSet = TasksUiPlugin.getTaskListManager().getTaskList().getQueries();
		Map<String, AbstractRepositoryQuery> queriesMap = new HashMap<String, AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queriesSet) {
			queriesMap.put(query.getHandleIdentifier(), query);
		}
		
		// check that the actual result is correct
		assertTrue("'Test Query' query inserted", queriesMap.containsKey("Test Query"));
		assertTrue("'Test Query[1]' query inserted", queriesMap.containsKey("Test Query[1]"));
		assertTrue("'Test Query[2]' query inserted", queriesMap.containsKey("Test Query[2]"));
	}
	

	public void testImportedQueriesNameConflictResolving2() {
		// prepare for test
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
		
		// create test data
		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		
		MockRepositoryQuery query1 = new MockRepositoryQuery("Test2");
		query1.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query2 = new MockRepositoryQuery("Test2[1]");
		query2.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query3 = new MockRepositoryQuery("Test2");
		query3.setRepositoryUrl(repository.getUrl());
		
		queries.add(query1);
		queries.add(query2);
		queries.add(query3);
		
		// run tested functionality
		TasksUiPlugin.getTaskListManager().insertQueries(queries);
		
		// extract results
		Set<AbstractRepositoryQuery> queriesSet = TasksUiPlugin.getTaskListManager().getTaskList().getQueries();
		Map<String, AbstractRepositoryQuery> queriesMap = new HashMap<String, AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queriesSet) {
			queriesMap.put(query.getHandleIdentifier(), query);
		}
		
		// check that the actual result is correct
		assertTrue("'Test2' query inserted", queriesMap.containsKey("Test2"));
		assertTrue("'Test2[1]' query inserted", queriesMap.containsKey("Test2[1]"));
		assertTrue("'Test2[2]' query inserted", queriesMap.containsKey("Test2[2]"));
		
	}
	

	public void testImportedBadQueriesNameConflictResolving() {
		// prepare for test
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
		
		// create test data
		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		
		MockRepositoryQuery query1 = new MockRepositoryQuery("Test[-10]");
		query1.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query2 = new MockRepositoryQuery("Test[ABC]");
		query2.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query3 = new MockRepositoryQuery("Test[1]");
		query3.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query4 = new MockRepositoryQuery("Test");
		query4.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query5 = new MockRepositoryQuery("Test");
		query5.setRepositoryUrl(repository.getUrl());
		
		queries.add(query1);
		queries.add(query2);
		queries.add(query3);
		queries.add(query4);
		queries.add(query5);
		
		// run tested functionality
		TasksUiPlugin.getTaskListManager().insertQueries(queries);
		
		// extract results
		Set<AbstractRepositoryQuery> queriesSet = TasksUiPlugin.getTaskListManager().getTaskList().getQueries();
		Map<String, AbstractRepositoryQuery> queriesMap = new HashMap<String, AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queriesSet) {
			queriesMap.put(query.getHandleIdentifier(), query);
		}
		
		// check that the actual result is correct
		assertTrue("'Test[-10]' query inserted", queriesMap.containsKey("Test[-10]"));
		assertTrue("'Test[ABC]' query inserted", queriesMap.containsKey("Test[ABC]"));
		assertTrue("'Test[1]' query inserted", queriesMap.containsKey("Test[1]"));
		assertTrue("'Test' query inserted", queriesMap.containsKey("Test"));
		assertTrue("Another 'Test' query inserted", queriesMap.containsKey("Test[2]"));
	}
	
	public void testImportedBadQueries() {
		// prepare for test
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
		
		// create test data
		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		
		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
		query1.setRepositoryUrl(repository.getUrl());
		MockRepositoryQuery query2 = new MockRepositoryQuery("Bad Query");
		query2.setRepositoryUrl("bad url");
		
		queries.add(query1);
		queries.add(query2);
		
		// run tested functionality
		List<AbstractRepositoryQuery> badQueries = TasksUiPlugin.getTaskListManager().insertQueries(queries);
		
		// extract results
		Set<AbstractRepositoryQuery> queriesSet = TasksUiPlugin.getTaskListManager().getTaskList().getQueries();
		Map<String, AbstractRepositoryQuery> queriesMap = new HashMap<String, AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queriesSet) {
			queriesMap.put(query.getHandleIdentifier(), query);
		}
		
		// check that the actual result is correct
		assertEquals("There is a bad query", 1, badQueries.size());
		assertEquals(query2, badQueries.get(0));
		assertTrue("'Test Query' query inserted", queriesMap.containsKey("Test Query"));
	}
	
	
	private void removeFiles(File root) {
		if (root.isDirectory()) {
			for (File file : root.listFiles()) {
				if (file.isDirectory()) {
					removeFiles(file);
				}
				file.delete();
			}
		}
	}

}
