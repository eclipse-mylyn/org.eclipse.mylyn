/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Rob Elves
 * @author Ken Sueda
 */
public class PlannedTasksContentProvider implements ITreeContentProvider, ITaskPlannerContentProvider {

	TaskActivityEditorInput editorInput;

	public PlannedTasksContentProvider(TaskActivityEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public Object[] getElements(Object inputElement) {
		return editorInput.getCategories().toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void addTask(AbstractTask task) {
		editorInput.addPlannedTask(task);
	}

	public void removeTask(AbstractTask task) {
		editorInput.removePlannedTask(task);
	}

	public Object[] getChildren(Object parentElement) {
		Set<AbstractTask> plannedChildren = new HashSet<AbstractTask>();
		if (parentElement instanceof AbstractTaskContainer) {
			AbstractTaskContainer parent = (AbstractTaskContainer) parentElement;
			plannedChildren.addAll(editorInput.getPlannedTasks());
			plannedChildren.retainAll(parent.getChildren());
		}
		return plannedChildren.toArray();
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

}
