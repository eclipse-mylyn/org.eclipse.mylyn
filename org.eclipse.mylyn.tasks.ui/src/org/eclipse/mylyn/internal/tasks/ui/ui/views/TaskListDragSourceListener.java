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

package org.eclipse.mylar.internal.tasks.ui.ui.views;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.ui.actions.CopyDetailsAction;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;

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
		StructuredSelection selection = (StructuredSelection) this.view.getViewer().getSelection();
		ITaskListElement selectedElement = null;
		if (((IStructuredSelection) selection).getFirstElement() instanceof ITaskListElement) {
			selectedElement = (ITaskListElement)((IStructuredSelection) selection).getFirstElement();
		}
		if (FileTransfer.getInstance().isSupportedType(event.dataType)) {			
			File file = ContextCorePlugin.getContextManager().getFileForContext(selectedElement.getHandleIdentifier());
			if (file != null) {
				event.data = new String[] { file.getAbsolutePath() };
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = CopyDetailsAction.getTextForTask(selectedElement);
		} else {
			event.data = ID_DATA_TASK_DRAG;
		}
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
