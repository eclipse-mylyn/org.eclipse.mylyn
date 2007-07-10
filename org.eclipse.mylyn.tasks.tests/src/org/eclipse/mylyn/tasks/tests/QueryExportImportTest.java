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
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskList;
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
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		removeFiles(dest);
		dest.delete();
		assertFalse(dest.exists());
	}
	
	public void testExportImportQuery() {
		List<AbstractTaskListFactory> externalizers = new ArrayList<AbstractTaskListFactory>();
		externalizers.add(new MockTaskListFactory());
		TasksUiPlugin.getTaskListManager().getTaskListWriter().setDelegateExternalizers(externalizers);

		MockRepositoryQuery query = new MockRepositoryQuery("Test Query");

		File outFile = new File(dest, "test-query.xml.zip");
		
		TasksUiPlugin.getTaskListManager().getTaskListWriter().writeQuery(query, outFile);
		assertTrue(outFile.exists());
		
		TaskList taskList = TasksUiPlugin.getTaskListManager().resetTaskList();

		File inFile = new File(dest, "test-query.xml.zip");
		TasksUiPlugin.getTaskListManager().getTaskListWriter().readQueries(taskList, inFile);		
		Set<AbstractRepositoryQuery> queries = taskList.getQueries();
		assertEquals("1 Query is imported", 1, queries.size());
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
