/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.mylyn.commons.tests.support.CommonsTestUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizardPage;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Export Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (fixes)
 */
public class TaskDataExportTest extends AbstractContextTest {

	private TaskDataExportWizard wizard;

	private TaskDataExportWizardPage wizardPage;

	private File destinationDir;

	private AbstractTask task1;

	private InteractionContext mockContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage(Messages.TaskDataExportWizardPage_Export_Mylyn_Task_Data);
		assertNotNull(wizardPage);

		// Create test export destination directory
		destinationDir = new File(TasksUiPlugin.getDefault().getDataDirectory(), "TestDir");
		CommonsTestUtil.deleteFolder(destinationDir);
		destinationDir.mkdir();
		assertTrue(destinationDir.exists());

		// Create a task and context with an interaction event to be saved
		task1 = TasksUiInternal.createNewLocalTask("Export Test Task");
		TasksUiPlugin.getTaskList().addTask(task1);

		// Save the context file and check that it exists
		mockContext = (InteractionContext) ContextCorePlugin.getContextStore().loadContext(task1.getHandleIdentifier());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);
		ContextCorePlugin.getContextStore().saveContext(mockContext);
		File taskFile = ContextCorePlugin.getContextStore().getFileForContext(task1.getHandleIdentifier());
		assertTrue(ContextCore.getContextManager().hasContext(task1.getHandleIdentifier()));
		assertTrue(taskFile.exists());

		// do this last to make sure tearDown() runs
		ContextCorePlugin.getContextManager().internalActivateContext(mockContext);
	}

	@Override
	protected void tearDown() throws Exception {
		ContextCore.getContextManager().deactivateContext(mockContext.getHandleIdentifier());
		CommonsTestUtil.deleteFolder(destinationDir);
		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to export all task data to a zip file.
	 */
	public void testExportAllToZip() throws Exception {
		// set parameters in the wizard to simulate a user setting them and clicking "Finish"
		wizardPage.setParameters(true, true, true, true, true, destinationDir.getPath());
		wizard.performFinish();

		// check that the task list file was exported
		File[] files = destinationDir.listFiles();
		assertEquals(1, files.length);
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(files[0]));
		try {
			ArrayList<String> entries = new ArrayList<String>();
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				entries.add(entry.getName());
				entry = zipInputStream.getNextEntry();
			}
			assertEquals(3, entries.size());
			Collections.sort(entries);
			assertEquals("contexts/local-1.xml.zip", entries.get(0));
			assertEquals("repositories.xml.zip", entries.get(1));
			assertEquals("tasks.xml.zip", entries.get(2));
		} finally {
			zipInputStream.close();
		}
	}
}
