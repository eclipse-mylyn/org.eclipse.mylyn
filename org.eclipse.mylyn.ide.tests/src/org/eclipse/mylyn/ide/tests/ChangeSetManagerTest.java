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

package org.eclipse.mylar.ide.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.ide.MylarIdePlugin;
import org.eclipse.mylar.internal.ide.team.MylarChangeSetManager;
import org.eclipse.mylar.internal.ide.team.MylarContextChangeSet;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ChangeSet;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;

/**
 * @author Mik Kersten
 */
public class ChangeSetManagerTest extends AbstractResourceContextTest {

	private MylarChangeSetManager changeSetManager;

	private SubscriberChangeSetCollector collector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(MylarIdePlugin.getDefault());
		changeSetManager = MylarIdePlugin.getDefault().getChangeSetManager();
		collector = CVSUIPlugin.getPlugin().getChangeSetManager();
		assertNotNull(changeSetManager);
		assertEquals(0, MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks().size());
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

		MylarIdePlugin.getDefault().getChangeSetManager().disable();

		Task task1 = new Task("task1", "label", true);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length);

		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		MylarIdePlugin.getDefault().getChangeSetManager().enable();
	}

	public void testSingleContextActivation() {
		ChangeSet[] sets = collector.getSets();
		for (int i = 0; i < sets.length; i++)
			collector.remove(sets[i]);

		assertEquals(0, collector.getSets().length);
		manager.deactivateContext(taskId);
		changeSetManager.clearActiveChangeSets();
		assertEquals(0, changeSetManager.getActiveChangeSets().size());

		Task task1 = new Task("task1", "label", true);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);
		assertEquals(1, changeSetManager.getActiveChangeSets().size());
		assertEquals(1, collector.getSets().length);

		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		assertFalse(MylarPlugin.getContextManager().isContextActive());
		assertEquals(0, changeSetManager.getActiveChangeSets().size());
		assertEquals(0, collector.getSets().length); // deleted because no
		// active resources
		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
		
		// TODO: test with resource
	}

	public void testContentsAfterDecay() throws CoreException {
		IFile file = project.getProject().getFile(new Path("foo.txt"));
		file.create(null, true, null);

		Task task1 = new Task("task1", "label", true);
		MylarTaskListPlugin.getTaskListManager().activateTask(task1);

		monitor.selectionChanged(navigator, new StructuredSelection(file));
		IMylarElement fileElement = MylarPlugin.getContextManager().getElement(
				structureBridge.getHandleIdentifier(file));
		assertTrue(fileElement.getInterest().isInteresting());

		List<MylarContextChangeSet> changeSets = changeSetManager.getActiveChangeSets();
		assertEquals(1, changeSets.size());
		MylarContextChangeSet set = changeSets.get(0);
		IResource[] resources = set.getResources();
		// can have .project file in there
		assertTrue("length: " + resources.length, resources.length <= 2); 

		for (int i = 0; i < 1 / (scaling.getDecay().getValue()) * 3; i++) {
			MylarPlugin.getContextManager().handleInteractionEvent(mockSelection());
		}
		assertTrue("" + fileElement.getInterest().getValue(), fileElement.getInterest().getValue() < 0);
		assertTrue("length: " + resources.length, resources.length <= 2); 

		MylarTaskListPlugin.getTaskListManager().deactivateTask(task1);
	}
}
