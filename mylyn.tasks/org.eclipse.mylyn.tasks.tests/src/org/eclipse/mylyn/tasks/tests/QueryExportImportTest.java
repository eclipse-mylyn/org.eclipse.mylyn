/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jevgeni Holodkov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

/**
 * @author Jevgeni Holodkov
 */
// FIXME fix test
public class QueryExportImportTest extends TestCase {

	public void testRewriteTestCases() {
	}

//	private File dest;
//
//	private TaskListElementImporter taskListWriter;
//
//	@Override
//	protected void setUp() throws Exception {
//		removeFiles(new File(TasksUiPlugin.getDefault().getDataDirectory()));
//
//		// Create test export destination directory
//		dest = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "TestDir");
//		if (dest.exists()) {
//			removeFiles(dest);
//		} else {
//			dest.mkdir();
//		}
//		assertTrue(dest.exists());
//		taskListWriter = TasksUiPlugin.getTaskListWriter();
//
//		TaskTestUtil.resetTaskListAndRepositories();
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		removeFiles(dest);
//		dest.delete();
//		assertFalse(dest.exists());
//
//		TaskTestUtil.resetTaskListAndRepositories();
//	}
//
//	public void testExportImportQuery() throws Exception {
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
//		query1.setRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL);
//		MockRepositoryQuery query2 = new MockRepositoryQuery("Test Query 2");
//		query2.setRepositoryUrl(MockRepositoryConnector.REPOSITORY_URL);
//		queries.add(query1);
//		queries.add(query2);
//
//		File outFile = new File(dest, "test-query.xml.zip");
//
//		taskListWriter.writeQueries(queries, outFile);
//		assertTrue(outFile.exists());
//
//		File inFile = new File(dest, "test-query.xml.zip");
//		List<RepositoryQuery> resultQueries = taskListWriter.readQueries(inFile);
//		assertEquals("2 Queries is imported", 2, resultQueries.size());
//	}
//
//	public void testImportedQueriesNameConflictResolving1() {
//		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
//				MockRepositoryConnector.REPOSITORY_URL);
//		TasksUiPlugin.getRepositoryManager().addRepository(repository);
//
//		// create test data
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
//		query1.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query2 = new MockRepositoryQuery("Test Query");
//		query2.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query3 = new MockRepositoryQuery("Test Query");
//		query3.setRepositoryUrl(repository.getRepositoryUrl());
//
//		queries.add(query1);
//		queries.add(query2);
//		queries.add(query3);
//
//		// run tested functionality
//		QueryImportAction action = new QueryImportAction();
//		action.insertQueries(queries);
//
//		// extract results
//		Set<RepositoryQuery> queriesSet = TasksUiPlugin.getTaskList().getQueries();
//		Map<String, RepositoryQuery> queriesMap = new HashMap<String, RepositoryQuery>();
//		for (RepositoryQuery query : queriesSet) {
//			queriesMap.put(query.getHandleIdentifier(), query);
//		}
//
//		// check that the actual result is correct
//		assertTrue("'Test Query' query inserted", queriesMap.containsKey("Test Query"));
//		assertTrue("'Test Query[1]' query inserted", queriesMap.containsKey("Test Query[1]"));
//		assertTrue("'Test Query[2]' query inserted", queriesMap.containsKey("Test Query[2]"));
//	}
//
//	public void testImportedQueriesNameConflictResolving2() {
//		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
//				MockRepositoryConnector.REPOSITORY_URL);
//		TasksUiPlugin.getRepositoryManager().addRepository(repository);
//
//		// create test data
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test2");
//		query1.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query2 = new MockRepositoryQuery("Test2[1]");
//		query2.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query3 = new MockRepositoryQuery("Test2");
//		query3.setRepositoryUrl(repository.getRepositoryUrl());
//
//		queries.add(query1);
//		queries.add(query2);
//		queries.add(query3);
//
//		// run tested functionality
//		QueryImportAction action = new QueryImportAction();
//		action.insertQueries(queries);
//
//		// extract results
//		Set<RepositoryQuery> queriesSet = TasksUiPlugin.getTaskList().getQueries();
//		Map<String, RepositoryQuery> queriesMap = new HashMap<String, RepositoryQuery>();
//		for (RepositoryQuery query : queriesSet) {
//			queriesMap.put(query.getHandleIdentifier(), query);
//		}
//
//		// check that the actual result is correct
//		assertTrue("'Test2' query inserted", queriesMap.containsKey("Test2"));
//		assertTrue("'Test2[1]' query inserted", queriesMap.containsKey("Test2[1]"));
//		assertTrue("'Test2[2]' query inserted", queriesMap.containsKey("Test2[2]"));
//
//	}
//
//	public void testImportedBadQueriesNameConflictResolving() {
//		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
//				MockRepositoryConnector.REPOSITORY_URL);
//		TasksUiPlugin.getRepositoryManager().addRepository(repository);
//
//		// create test data
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test[-10]");
//		query1.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query2 = new MockRepositoryQuery("Test[ABC]");
//		query2.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query3 = new MockRepositoryQuery("Test[1]");
//		query3.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query4 = new MockRepositoryQuery("Test");
//		query4.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query5 = new MockRepositoryQuery("Test");
//		query5.setRepositoryUrl(repository.getRepositoryUrl());
//
//		queries.add(query1);
//		queries.add(query2);
//		queries.add(query3);
//		queries.add(query4);
//		queries.add(query5);
//
//		// run tested functionality
//		QueryImportAction action = new QueryImportAction();
//		action.insertQueries(queries);
//
//		// extract results
//		Set<RepositoryQuery> queriesSet = TasksUiPlugin.getTaskList().getQueries();
//		Map<String, RepositoryQuery> queriesMap = new HashMap<String, RepositoryQuery>();
//		for (RepositoryQuery query : queriesSet) {
//			queriesMap.put(query.getHandleIdentifier(), query);
//		}
//
//		// check that the actual result is correct
//		assertTrue("'Test[-10]' query inserted", queriesMap.containsKey("Test[-10]"));
//		assertTrue("'Test[ABC]' query inserted", queriesMap.containsKey("Test[ABC]"));
//		assertTrue("'Test[1]' query inserted", queriesMap.containsKey("Test[1]"));
//		assertTrue("'Test' query inserted", queriesMap.containsKey("Test"));
//		assertTrue("Another 'Test' query inserted", queriesMap.containsKey("Test[2]"));
//	}
//
//	public void testImportedBadQueries() {
//		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
//				MockRepositoryConnector.REPOSITORY_URL);
//		TasksUiPlugin.getRepositoryManager().addRepository(repository);
//
//		// create test data
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
//		query1.setRepositoryUrl(repository.getRepositoryUrl());
//		MockRepositoryQuery query2 = new MockRepositoryQuery("Bad Query");
//		query2.setRepositoryUrl("bad url");
//
//		queries.add(query1);
//		queries.add(query2);
//
//		// run tested functionality
//		QueryImportAction action = new QueryImportAction();
//		List<RepositoryQuery> badQueries = action.insertQueries(queries);
//
//		// extract results
//		Set<RepositoryQuery> queriesSet = TasksUiPlugin.getTaskList().getQueries();
//		Map<String, RepositoryQuery> queriesMap = new HashMap<String, RepositoryQuery>();
//		for (RepositoryQuery query : queriesSet) {
//			queriesMap.put(query.getHandleIdentifier(), query);
//		}
//
//		// check that the actual result is correct
//		assertEquals("There is a bad query", 1, badQueries.size());
//		assertEquals(query2, badQueries.get(0));
//		assertTrue("'Test Query' query inserted", queriesMap.containsKey("Test Query"));
//	}
//
//	public void testExportImportQueryWithRepositoryInfo() throws Exception {
//		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
//				MockRepositoryConnector.REPOSITORY_URL);
//		TasksUiPlugin.getRepositoryManager().addRepository(repository);
//
//		// create test data
//		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
//		MockRepositoryQuery query1 = new MockRepositoryQuery("Test Query");
//		query1.setRepositoryUrl(repository.getRepositoryUrl());
//		queries.add(query1);
//
//		File outFile = new File(dest, "test-repository-query.xml.zip");
//
//		taskListWriter.writeQueries(queries, outFile);
//
//		assertTrue(outFile.exists());
//
//		List<String> files = new ArrayList<String>();
//		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(outFile));
//		ZipEntry entry = null;
//		while ((entry = inputStream.getNextEntry()) != null) {
//			files.add(entry.getName());
//		}
//		inputStream.close();
//
//		assertTrue("exported file contains a file with queries", files.contains(ITasksCoreConstants.OLD_TASK_LIST_FILE));
//		assertTrue("exported file contains a file with repositories",
//				files.contains(TaskRepositoryManager.OLD_REPOSITORIES_FILE));
//
//		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
//		assertTrue("There are currently no repositories defined", TasksUiPlugin.getRepositoryManager().getRepository(
//				MockRepositoryConnector.REPOSITORY_URL) == null);
//
//		List<RepositoryQuery> resultQueries = taskListWriter.readQueries(outFile);
//		Set<TaskRepository> repositories = taskListWriter.readRepositories(outFile);
//
//		TasksUiPlugin.getRepositoryManager().insertRepositories(repositories,
//				TasksUiPlugin.getDefault().getRepositoriesFilePath());
//		QueryImportAction action = new QueryImportAction();
//		action.insertQueries(resultQueries);
//
//		// extract results
//		Set<RepositoryQuery> queriesSet = TasksUiPlugin.getTaskList().getQueries();
//		Map<String, RepositoryQuery> queriesMap = new HashMap<String, RepositoryQuery>();
//		for (RepositoryQuery query : queriesSet) {
//			queriesMap.put(query.getHandleIdentifier(), query);
//		}
//
//		// check that the actual result is correct
//		assertTrue("'Test Query' query inserted", queriesMap.containsKey("Test Query"));
//		assertTrue("1 repository is loaded", TasksUiPlugin.getRepositoryManager().getRepository(
//				MockRepositoryConnector.REPOSITORY_URL) != null);
//	}
//
//	private void removeFiles(File root) {
//		if (root.isDirectory()) {
//			for (File file : root.listFiles()) {
//				if (file.isDirectory()) {
//					removeFiles(file);
//				}
//				file.delete();
//			}
//		}
//	}

}
