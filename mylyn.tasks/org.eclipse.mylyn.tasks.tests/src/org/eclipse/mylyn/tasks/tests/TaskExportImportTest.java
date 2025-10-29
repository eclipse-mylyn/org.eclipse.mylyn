/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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
//FIXME fix test
public class TaskExportImportTest extends TestCase {

	public void testRewriteTestCases() {
	}

//	private File destinationDir;
//
//	@Override
//	protected void setUp() throws Exception {
//		TasksUi.getTaskActivityManager().deactivateActiveTask();
//		super.setUp();
//
//		// Create test export destination directory
//		destinationDir = new File(TasksUiPlugin.getDefault().getDataDirectory(), "TestDir");
//		CommonsTestUtil.deleteFolder(destinationDir);
//		destinationDir.mkdir();
//		assertTrue(destinationDir.exists());
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		CommonsTestUtil.deleteFolder(destinationDir);
//		super.tearDown();
//	}
//
//	public void testTaskContextExport() throws Exception {
//
//		LocalTask task = TasksUiInternal.createNewLocalTask("Test local task");
//		TaskList taskList = TasksUiPlugin.getTaskList();
//		taskList.addTask(task, taskList.getDefaultCategory());
//		assertTrue(taskList.getAllTasks().size() > 0);
//
//		InteractionContext mockContext = (InteractionContext) ContextCorePlugin.getContextStore().loadContext(
//				task.getHandleIdentifier());
//
//		ContextCorePlugin.getContextManager().internalActivateContext(mockContext);
//		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
//		mockContext.parseEvent(event);
//		ContextCore.getContextManager().deactivateContext(mockContext.getHandleIdentifier());
//
//		assertTrue(ContextCorePlugin.getContextStore().getContextDirectory().exists());
//		ContextCorePlugin.getContextStore().saveContext(mockContext);
//		assertTrue(ContextCore.getContextManager().hasContext(task.getHandleIdentifier()));
//
//		File outFile = new File(destinationDir + File.separator + "local-task.xml.zip");
//		TasksUiPlugin.getTaskListWriter().writeTask(task, outFile);
//		assertTrue(outFile.exists());
//
//		// check the content of the archive
//		List<String> files = new ArrayList<String>();
//		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(outFile));
//		ZipEntry entry = null;
//		while ((entry = inputStream.getNextEntry()) != null) {
//			files.add(entry.getName());
//		}
//		inputStream.close();
//
//		assertTrue("exported file contains a file with queries", files.contains(ITasksCoreConstants.OLD_TASK_LIST_FILE));
//
//		String handleIdentifier = mockContext.getHandleIdentifier();
//		String encoded = URLEncoder.encode(handleIdentifier, InteractionContextManager.CONTEXT_FILENAME_ENCODING);
//		String contextName = encoded + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD;
//		assertTrue("exported file contains a file with context", files.contains(contextName));
//
//		// reset all data
//		TaskTestUtil.resetTaskList();
//		assertTrue(taskList.getAllTasks().size() == 0);
//
//		ContextCore.getContextManager().deleteContext(handleIdentifier);
//		assertFalse(ContextCore.getContextManager().hasContext(task.getHandleIdentifier()));
//
//		// load data back
//		List<AbstractTask> tasks = TasksUiPlugin.getTaskListWriter().readTasks(outFile);
//		IInteractionContext loadedContext = ContextCore.getContextStore().importContext(task.getHandleIdentifier(),
//				outFile);
//
//		// check with original one
//		assertEquals("There is 1 task loaded", 1, tasks.size());
//		assertEquals("Loaded task is correct", task, tasks.get(0));
//		assertEquals("Loaded context is correct", mockContext, loadedContext);
//
//		// import data
//		for (AbstractTask loadedTask : tasks) {
//			taskList.addTask(loadedTask);
//		}
//		ContextCore.getContextStore().importContext(task.getHandleIdentifier(), outFile);
////		ContextCorePlugin.getContextStore().importContext(loadedContext);
//
//		// check that context was imported and is the same as original one
//		IInteractionContext savedContext = ContextCorePlugin.getContextStore().loadContext(task.getHandleIdentifier());
//		assertEquals("Saved context is the same as original one", mockContext, savedContext);
//		assertEquals("Saved task is the same as original one", task, taskList.getTask(task.getHandleIdentifier()));
//
//		ContextCorePlugin.getContextManager().deactivateAllContexts();
//	}

}
