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

package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/**
 * @author Mik Kersten
 */
class TaskListDragSourceListener implements DragSourceListener {

	static final String ID_DATA_TASK_DRAG = "task-drag";

	static final String DELIM = ", ";

	private final TaskListView view;

	/**
	 * @param view
	 */
	public TaskListDragSourceListener(TaskListView view) {
		this.view = view;
	}

	public void dragStart(DragSourceEvent event) {
		if (((StructuredSelection) this.view.getViewer().getSelection()).isEmpty()) {
			event.doit = false;
		}
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = ID_DATA_TASK_DRAG;
		// StructuredSelection selection = (StructuredSelection)
		// this.view.getViewer().getSelection();
		// String data = "task-drag";
		// for (Object selectedObject : ((IStructuredSelection)
		// selection).toList()) {
		// if (selectedObject instanceof ITaskListElement) {
		// ITaskListElement element = (ITaskListElement) selectedObject;
		// if (element.isDragAndDropEnabled()) {
		// data += "task"
		// data += element.getHandleIdentifier() + DELIM;
		// }
		// }
		// }
		// if (data != null) {
		// event.data = data;
		// } else {
		// event.data = "null";
		// }
	}

	public void dragFinished(DragSourceEvent event) {
		// don't care if the drag is done
	}
}
