/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import java.io.File;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.tasks.ui.TaskListDataMigration;

/**
 * @author Rob Elves
 */
public class TaskListDataMigrationTest extends TestCase {

	private String sourceDir = "testdata/tasklistdatamigrationtest";

	private File sourceDirFile;

	private TaskListDataMigration migrator;

	protected void setUp() throws Exception {
		super.setUp();
		sourceDirFile = TaskTestUtil.getLocalFile(sourceDir);
		assertNotNull(sourceDirFile);
		deleteAllFiles(sourceDirFile);
		migrator = new TaskListDataMigration(sourceDirFile);
		assertTrue(sourceDirFile.exists());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		deleteAllFiles(sourceDirFile);
	}

	public void testOldTasklistMigration() throws Exception {
		File oldTasklistFile = new File(sourceDirFile, "tasklist.xml");
		oldTasklistFile.createNewFile();
		assertTrue(new File(sourceDirFile, "tasklist.xml").exists());
		assertTrue(!new File(sourceDirFile, "tasklist.xml.zip").exists());
		assertTrue(migrator.migrateTaskList(new NullProgressMonitor()));
		assertFalse(new File(sourceDirFile, "tasklist.xml").exists());
		assertFalse(!new File(sourceDirFile, "tasklist.xml.zip").exists());
	}

	public void testOldRepositoriesMigration() throws Exception {
		File oldRepositoriesFile = new File(sourceDirFile, "repositories.xml");
		oldRepositoriesFile.createNewFile();
		assertTrue(new File(sourceDirFile, "repositories.xml").exists());
		assertTrue(!new File(sourceDirFile, "repositories.xml.zip").exists());
		assertTrue(migrator.migrateRepositoriesData(new NullProgressMonitor()));
		assertFalse(new File(sourceDirFile, "repositories.xml").exists());
		assertTrue(new File(sourceDirFile, "repositories.xml.zip").exists());
	}

	public void testOldContextMigration() throws Exception {
		String contextFileName1 = URLEncoder.encode("http://oldcontext1.xml",
				MylarContextManager.CONTEXT_FILENAME_ENCODING);
		String contextFileName2 = URLEncoder.encode("http://oldcontext2.xml",
				MylarContextManager.CONTEXT_FILENAME_ENCODING);
		String contextFileName3 = "task-1.xml";
		File oldContextFile1 = new File(sourceDirFile, contextFileName1);
		oldContextFile1.createNewFile();
		File oldContextFile2 = new File(sourceDirFile, contextFileName2);
		oldContextFile2.createNewFile();
		File oldContextFile3 = new File(sourceDirFile, contextFileName3);
		oldContextFile3.createNewFile();
		File contextFolder = new File(sourceDirFile, MylarContextManager.CONTEXTS_DIRECTORY);
		assertTrue(!contextFolder.exists());
		assertTrue(migrator.migrateTaskContextData(new NullProgressMonitor()));
		assertFalse(oldContextFile1.exists());
		assertFalse(oldContextFile2.exists());
		assertFalse(oldContextFile3.exists());
		assertTrue(contextFolder.exists());
		assertTrue(new File(contextFolder, contextFileName1 + ".zip").exists());
		assertTrue(new File(contextFolder, contextFileName2 + ".zip").exists());
		assertTrue(new File(contextFolder, contextFileName3 + ".zip").exists());
	}

	public void testOldActivityMigration() throws Exception {
		File oldActivityFile = new File(sourceDirFile, MylarContextManager.OLD_CONTEXT_HISTORY_FILE_NAME
				+ MylarContextManager.CONTEXT_FILE_EXTENSION_OLD);
		oldActivityFile.createNewFile();
		File contextFolder = new File(sourceDirFile, MylarContextManager.CONTEXTS_DIRECTORY);
		assertTrue(!contextFolder.exists());
		assertTrue(migrator.migrateActivityData(new NullProgressMonitor()));
		assertFalse(oldActivityFile.exists());
		assertTrue(contextFolder.exists());
		assertTrue(new File(contextFolder, MylarContextManager.CONTEXT_HISTORY_FILE_NAME
				+ MylarContextManager.CONTEXT_FILE_EXTENSION).exists());
	}

	private void deleteAllFiles(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				if (!file.getName().equals("CVS")) {
					deleteAllFiles(file);
					file.delete();
				}
			} else if (!file.getName().equals("empty.txt")) {
				file.delete();
			}
		}
	}
}
