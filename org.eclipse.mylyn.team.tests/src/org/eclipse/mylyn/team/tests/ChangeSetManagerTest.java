/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.team.ui.ContextActiveChangeSetManager;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.resources.tests.AbstractResourceContextTest;
import org.eclipse.mylyn.team.ui.IContextChangeSet;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
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
		changeSetManager = (ContextActiveChangeSetManager) FocusedTeamUiPlugin.getDefault()
				.getContextChangeSetManagers()
				.iterator()
				.next();
		collector = CVSUIPlugin.getPlugin().getChangeSetManager();
		assertNotNull(changeSetManager);
		assertNull(TasksUiPlugin.getTaskActivityManager().getActiveTask());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDisabledMode() {
		ChangeSet[] sets = collector.getSets();
		for (ChangeSet set : sets) {
			collector.remove(set);
		}

		assertEquals(0, collector.getSets().length);
		manager.deactivateContext(taskId);
		changeSetManager.clearActiveChangeSets();
		assertEquals(0, changeSetManager.getActiveChangeSets().size());

		changeSetManager.disable();

		AbstractTask task1 = new LocalTask("task1", "label");
		TasksUiPlugin.getTaskActivityManager().activateTask(task1);
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length);

		TasksUiPlugin.getTaskActivityManager().deactivateTask(task1);
		changeSetManager.enable();
	}

	public void testSingleContextActivation() {
		ChangeSet[] sets = collector.getSets();
		for (ChangeSet set : sets) {
			collector.remove(set);
		}

		assertEquals(0, collector.getSets().length);
		manager.deactivateContext(taskId);
		changeSetManager.clearActiveChangeSets();
		assertEquals(0, changeSetManager.getActiveChangeSets().size());

		AbstractTask task1 = new LocalTask("task1", "label");
		TasksUiPlugin.getTaskActivityManager().activateTask(task1);
		assertEquals(1, changeSetManager.getActiveChangeSets().size());
		assertEquals(1, collector.getSets().length);

		TasksUiPlugin.getTaskActivityManager().deactivateTask(task1);
		assertFalse(ContextCore.getContextManager().isContextActive());
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length); // deleted because no
		// active resources
		TasksUiPlugin.getTaskActivityManager().deactivateTask(task1);

		// TODO: test with resource
	}

	public void testContentsAfterDecay() throws CoreException {
		IFile file = project.getProject().getFile(new Path("foo.txt"));
		file.create(null, true, null);

		AbstractTask task1 = new LocalTask("task1", "label");
		TasksUiPlugin.getTaskActivityManager().activateTask(task1);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		IInteractionElement fileElement = ContextCore.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertTrue(fileElement.getInterest().isInteresting());

		List<IContextChangeSet> changeSets = changeSetManager.getActiveChangeSets();
		assertEquals(1, changeSets.size());
		IContextChangeSet set = changeSets.get(0);
		IResource[] resources = ((ActiveChangeSet) set).getResources();
		// can have .project file in there
		assertTrue("length: " + resources.length, resources.length <= 2);

		for (int i = 0; i < 1 / (scaling.getDecay()) * 3; i++) {
			ContextCore.getContextManager().processInteractionEvent(mockSelection());
		}
		assertTrue("" + fileElement.getInterest().getValue(), fileElement.getInterest().getValue() < 0);
		assertTrue("length: " + resources.length, resources.length <= 2);

		TasksUiPlugin.getTaskActivityManager().deactivateTask(task1);
	}
}
