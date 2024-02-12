/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.cdt.tests;

import java.io.File;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.cdt.tests.support.AbstractCdtContextTest;
import org.eclipse.mylyn.internal.cdt.ui.CDTUIBridgePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.filesystem.FileSystemStructureProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * @author Sam Davis
 */
@SuppressWarnings("nls")
public class CdtStructureBridgeTest extends AbstractCdtContextTest {
	private IProject importedProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		importedProject = ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
		ImportOperation importOperation = new ImportOperation(importedProject.getFullPath(),
				new File("data/TestProject/"), new FileSystemStructureProvider(), file -> IOverwriteQuery.ALL);
		
		importOperation.run(null);
	}

	public void testBridgePresent() {
		// see super.setUp()
	}

	public void testFolding() throws Exception {
		TasksUi.getTaskActivityManager().activateTask(new TaskTask("kind", "http://mylyn.org", "1"));
		IFile file = importedProject.getFile("Test.cpp");
		String editorId = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(".cpp").getId();
		PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.openEditor(new FileEditorInput(file), editorId);
		CUIPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		CDTUIBridgePlugin.getDefault().getPreferenceStore().setValue(CDTUIBridgePlugin.AUTO_FOLDING_ENABLED, true);
	}
}
