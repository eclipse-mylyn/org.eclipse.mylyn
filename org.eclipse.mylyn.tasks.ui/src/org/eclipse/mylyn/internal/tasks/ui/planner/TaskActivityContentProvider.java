/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Rob Elves
 */
public class TaskActivityContentProvider implements IStructuredContentProvider, ITaskPlannerContentProvider {

	private TaskActivityEditorInput editorInput;

	public TaskActivityContentProvider(TaskActivityEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public Object[] getElements(Object inputElement) {
		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
		allTasks.addAll(editorInput.getCompletedTasks());
		allTasks.addAll(editorInput.getInProgressTasks());
		return allTasks.toArray();
	}

	public void dispose() {
		// ignore

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	public void removeTask(AbstractTask task) {
		editorInput.removeCompletedTask(task);
		editorInput.removeInProgressTask(task);
	}

	public void addTask(AbstractTask task) {
		// ignore
	}

}
