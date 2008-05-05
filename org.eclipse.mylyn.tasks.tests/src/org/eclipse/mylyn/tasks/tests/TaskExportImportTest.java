/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * 
 * @author Jevgeni Holodkov
 */
public class TaskExportImportTest extends AbstractContextTest {

	private File dest;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		removeFiles(new File(TasksUiPlugin.getDefault().getDataDirectory()));
		ContextCore.getContextStore().init();

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
		removeFiles(dest);
		dest.delete();
		assertFalse(dest.exists());

		super.tearDown();
	}

	public void testTaskContextExport() throws IOException {

		LocalTask task = TasksUiInternal.createNewLocalTask("Test local task");
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		taskList.addTask(task, taskList.getDefaultCategory());
		assertTrue(taskList.getAllTasks().size() > 0);

		InteractionContext mockContext = ContextCorePlugin.getContextManager().loadContext(task.getHandleIdentifier());

		ContextCorePlugin.getContextManager().internalActivateContext(mockContext);
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);
		ContextCore.getContextManager().deactivateContext(mockContext.getHandleIdentifier());

		assertTrue(ContextCore.getContextStore().getContextDirectory().exists());
		ContextCore.getContextManager().saveContext(mockContext.getHandleIdentifier());
		assertTrue(ContextCore.getContextManager().hasContext(task.getHandleIdentifier()));

		File outFile = new File(dest + File.separator + "local-task.xml.zip");
		TasksUiPlugin.getTaskListManager().getTaskListWriter().writeTask(task, outFile);
		assertTrue(outFile.exists());

		// check the content of the archive
		List<String> files = new ArrayList<String>();
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(outFile));
		ZipEntry entry = null;
		while ((entry = inputStream.getNextEntry()) != null) {
			files.add(entry.getName());
		}
		inputStream.close();

		assertTrue("exported file contains a file with queries", files.contains(ITasksCoreConstants.OLD_TASK_LIST_FILE));

		String handleIdentifier = mockContext.getHandleIdentifier();
		String encoded = URLEncoder.encode(handleIdentifier, IInteractionContextManager.CONTEXT_FILENAME_ENCODING);
		String contextName = encoded + IInteractionContextManager.CONTEXT_FILE_EXTENSION_OLD;
		assertTrue("exported file contains a file with context", files.contains(contextName));

		// reset all data
		TasksUiPlugin.getTaskListManager().resetTaskList();
		assertTrue(taskList.getAllTasks().size() == 0);

		ContextCore.getContextManager().deleteContext(handleIdentifier);
		assertFalse(ContextCore.getContextManager().hasContext(task.getHandleIdentifier()));

		// load data back
		List<AbstractTask> tasks = TasksUiPlugin.getTaskListManager().getTaskListWriter().readTasks(outFile);
		InteractionContext loadedContext = ContextCorePlugin.getContextManager().loadContext(
				task.getHandleIdentifier(), outFile);

		// check with original one
		assertEquals("There is 1 task loaded", 1, tasks.size());
		assertEquals("Loaded task is correct", task, tasks.get(0));
		assertEquals("Loaded context is correct", mockContext, loadedContext);

		// import data
		for (AbstractTask loadedTask : tasks) {
			taskList.addTask(loadedTask);
		}
		ContextCorePlugin.getContextManager().importContext(loadedContext);

		// check that context was imported and is the same as original one
		InteractionContext savedContext = ContextCorePlugin.getContextManager().loadContext(task.getHandleIdentifier());
		assertEquals("Saved context is the same as original one", mockContext, savedContext);
		assertEquals("Saved task is the same as original one", task, taskList.getTask(task.getHandleIdentifier()));

		ContextCore.getContextManager().deactivateAllContexts();
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
