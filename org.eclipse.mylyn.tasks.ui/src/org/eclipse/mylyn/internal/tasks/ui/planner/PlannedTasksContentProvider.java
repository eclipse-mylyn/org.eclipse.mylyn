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

package org.eclipse.mylyn.internal.tasks.ui.planner;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Rob Elves
 * @author Ken Sueda
 */
public class PlannedTasksContentProvider implements IStructuredContentProvider, ITaskPlannerContentProvider {

	TaskActivityEditorInput editorInput;

	public PlannedTasksContentProvider(TaskActivityEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public Object[] getElements(Object inputElement) {
		return editorInput.getPlannedTasks().toArray();
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

}
