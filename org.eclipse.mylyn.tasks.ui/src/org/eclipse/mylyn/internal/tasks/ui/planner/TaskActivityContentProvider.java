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
 */
public class TaskActivityContentProvider implements ITreeContentProvider, ITaskPlannerContentProvider {

	private TaskActivityEditorInput editorInput;

	public TaskActivityContentProvider(TaskActivityEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public Object[] getElements(Object inputElement) {
		return editorInput.getCategories().toArray();
//		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
//		allTasks.addAll(editorInput.getCompletedTasks());
//		allTasks.addAll(editorInput.getInProgressTasks());
//		return allTasks.toArray();
	}

	public void removeTask(AbstractTask task) {
		editorInput.removeCompletedTask(task);
		editorInput.removeInProgressTask(task);
	}

	public void addTask(AbstractTask task) {
		// ignore
	}

	public Object[] getChildren(Object parentElement) {
		Set<AbstractTask> result = new HashSet<AbstractTask>();
		if (parentElement instanceof AbstractTaskContainer) {
			AbstractTaskContainer parent = (AbstractTaskContainer) parentElement;
			Set<AbstractTask> completedChildren = new HashSet<AbstractTask>();
			completedChildren.addAll(editorInput.getCompletedTasks());
			completedChildren.retainAll(parent.getChildren());
			result.addAll(completedChildren);

			Set<AbstractTask> inProgressChildren = new HashSet<AbstractTask>();
			inProgressChildren.addAll(editorInput.getInProgressTasks());
			inProgressChildren.retainAll(parent.getChildren());
			result.addAll(inProgressChildren);
		}
		return result.toArray();
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

}
