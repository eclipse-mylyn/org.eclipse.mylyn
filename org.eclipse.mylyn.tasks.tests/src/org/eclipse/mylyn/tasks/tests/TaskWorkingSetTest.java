/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.resources.tests.ResourceTestUtil;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.Workbench;

public class TaskWorkingSetTest extends TestCase {

	private IProject project;

	private IWorkspaceRoot root;

	private IWorkingSet workingSet;

	private IWorkingSetManager workingSetManager;

	@Override
	protected void setUp() throws Exception {
		workingSetManager = Workbench.getInstance().getWorkingSetManager();
		root = ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	protected void tearDown() throws Exception {
		if (workingSet != null) {
			workingSetManager.removeWorkingSet(workingSet);
		}

		if (project != null) {
			ResourceTestUtil.deleteProject(project);
		}
	}

	public void testDeleteQuery() {
		MockRepositoryQuery query = new MockRepositoryQuery("description");
		ITaskList taskList = TasksUiPlugin.getTaskList();
		taskList.addQuery(query);
		workingSet = createWorkingSet(query);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
		TasksUiPlugin.getTaskList().deleteQuery(query);
		assertFalse(Arrays.asList(workingSet.getElements()).contains(query));
	}

	public void testRenameQuery() {
		MockRepositoryQuery query = new MockRepositoryQuery("description");
		ITaskList taskList = TasksUiPlugin.getTaskList();
		taskList.addQuery(query);
		workingSet = createWorkingSet(query);
		assertTrue(workingSet.getElements().length == 1);
		IAdaptable[] elements = workingSet.getElements();
		assertTrue(elements.length == 1);
		assertTrue(elements[0] instanceof MockRepositoryQuery);
		assertTrue(((MockRepositoryQuery) elements[0]).getHandleIdentifier().equals("description"));
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));

		query.setHandleIdentifier("Test");
		assertTrue(workingSet.getElements().length == 1);
		elements = workingSet.getElements();
		assertTrue(elements.length == 1);
		assertTrue(elements[0] instanceof MockRepositoryQuery);
		assertTrue(((MockRepositoryQuery) elements[0]).getHandleIdentifier().equals("Test"));
		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
	}

	// XXX see bug 212962
//	public void testRenameQuery() {
//		MockRepositoryQuery query = new MockRepositoryQuery("description");
//		TaskList taskList = TasksUiPlugin.getTaskList();
//		taskList.addQuery(query);
//		workingSet = createWorkingSet(query);
//		
//		TasksUiPlugin.getTaskList().deleteQuery(query);
//		query = new MockRepositoryQuery("newDescription");
//		TasksUiPlugin.getTaskList().addQuery(query);
//		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
//	}
//
//	public void testEditQuery() {
//		MockRepositoryQuery query = new MockRepositoryQuery("description");
//		TaskList taskList = TasksUiPlugin.getTaskList();
//		taskList.addQuery(query);
//		workingSet = createWorkingSet(query);
//
//		TasksUiPlugin.getTaskList().deleteQuery(query);
//		TasksUiPlugin.getTaskList().addQuery(query);
//		assertTrue(Arrays.asList(workingSet.getElements()).contains(query));
//	}

	public void testRenameProject() throws Exception {
		createProject("Test Rename");
		workingSet = createWorkingSet(project);

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
				IProjectDescription description = new ProjectDescription();
				description.setName("New Name");
				project.move(description, true, new NullProgressMonitor());
			}
		};
		op.run(new NullProgressMonitor());

		IProject oldProject = root.getProject("Test Rename");
		IProject newProject = root.getProject("New Name");
		assertFalse(Arrays.asList(workingSet.getElements()).contains(oldProject));
		assertTrue(Arrays.asList(workingSet.getElements()).contains(newProject));
	}

	private void createProject(String name) throws CoreException {
		project = root.getProject(name);
		project.create(null);
		project.open(null);
	}

	private IWorkingSet createWorkingSet(IAdaptable element) {
		IWorkingSet workingSet = workingSetManager.createWorkingSet("Task Working Set", new IAdaptable[] { element });
		workingSet.setId(TaskWorkingSetUpdater.ID_TASK_WORKING_SET);
		assertTrue(Arrays.asList(workingSet.getElements()).contains(element));
		workingSetManager.addWorkingSet(workingSet);
		return workingSet;
	}

}
