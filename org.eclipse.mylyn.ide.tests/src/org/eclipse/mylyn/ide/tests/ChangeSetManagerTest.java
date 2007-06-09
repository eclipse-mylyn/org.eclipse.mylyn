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

package org.eclipse.mylyn.ide.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.mylyn.internal.team.ContextChangeSet;
import org.eclipse.mylyn.internal.team.ContextActiveChangeSetManager;
import org.eclipse.mylyn.resources.tests.AbstractResourceContextTest;
import org.eclipse.mylyn.tasks.core.Task;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.team.FocusedTeamPlugin;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.team.internal.core.subscribers.ChangeSet;

/**
 * @author Mik Kersten
 */
public class ChangeSetManagerTest extends AbstractResourceContextTest {

	private ContextActiveChangeSetManager changeSetManager;

	private ActiveChangeSetManager collector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(IdeUiBridgePlugin.getDefault());
		changeSetManager = (ContextActiveChangeSetManager)FocusedTeamPlugin.getDefault().getContextChangeSetManagers().iterator().next();
		collector = CVSUIPlugin.getPlugin().getChangeSetManager();
		assertNotNull(changeSetManager);
		assertEquals(0, TasksUiPlugin.getTaskListManager().getTaskList().getActiveTasks().size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDisabledMode() {
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++)
			collector.remove(sets[i]);

		assertEquals(0, collector.getSets().length);
		manager.deactivateContext(taskId);
		changeSetManager.clearActiveChangeSets();
		assertEquals(0, changeSetManager.getActiveChangeSets().size());

		changeSetManager.disable();
//		MylarTeamPlugin.getDefault().getChangeSetManager().disable();

		Task task1 = new Task("task1", "label");
		TasksUiPlugin.getTaskListManager().activateTask(task1);
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length);

		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
		changeSetManager.enable();
//		MylarTeamPlugin.getDefault().getChangeSetManager().enable();
	}

	public void testSingleContextActivation() {
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++)
			collector.remove(sets[i]);

		assertEquals(0, collector.getSets().length);
		manager.deactivateContext(taskId);
		changeSetManager.clearActiveChangeSets();
		assertEquals(0, changeSetManager.getActiveChangeSets().size());

		Task task1 = new Task("task1", "label");
		TasksUiPlugin.getTaskListManager().activateTask(task1);
		assertEquals(1, changeSetManager.getActiveChangeSets().size());
		assertEquals(1, collector.getSets().length);

		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
		assertFalse(ContextCorePlugin.getContextManager().isContextActive());
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length); // deleted because no
		// active resources
		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
		
		// TODO: test with resource
	}

	public void testContentsAfterDecay() throws CoreException {
		IFile file = project.getProject().getFile(new Path("foo.txt"));
		file.create(null, true, null);

		Task task1 = new Task("task1", "label");
		TasksUiPlugin.getTaskListManager().activateTask(task1);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		IInteractionElement fileElement = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertTrue(fileElement.getInterest().isInteresting());

		List<ContextChangeSet> changeSets = changeSetManager.getActiveChangeSets();
		assertEquals(1, changeSets.size());
		ContextChangeSet set = changeSets.get(0);
		IResource[] resources = set.getResources();
		// can have .project file in there
		assertTrue("length: " + resources.length, resources.length <= 2); 

		for (int i = 0; i < 1 / (scaling.getDecay().getValue()) * 3; i++) {
			ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection());
		}
		assertTrue("" + fileElement.getInterest().getValue(), fileElement.getInterest().getValue() < 0);
		assertTrue("length: " + resources.length, resources.length <= 2); 

		TasksUiPlugin.getTaskListManager().deactivateTask(task1);
	}
}
