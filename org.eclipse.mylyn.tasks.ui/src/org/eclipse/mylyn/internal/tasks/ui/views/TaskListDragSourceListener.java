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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskListElement;
import org.eclipse.mylyn.tasks.ui.TaskTransfer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * @author Mik Kersten
 */
class TaskListDragSourceListener implements DragSourceListener {

	static final String DELIM = ", ";

	private final TaskListView view;

//	static final String ID_DATA_TASK_DRAG = "task-drag";
	
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
		if (TaskTransfer.getInstance().isSupportedType(event.dataType)) {
			List<ITask> tasks = new ArrayList<ITask>();
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				ITaskListElement element = (ITaskListElement) iter.next();
				if (element instanceof ITask) {
					tasks.add((ITask)element);
				}
			}
			event.data = tasks.toArray();
		} else if (FileTransfer.getInstance().isSupportedType(event.dataType) && selectedElement != null) {	
			File file = ContextCorePlugin.getContextManager().getFileForContext(selectedElement.getHandleIdentifier());
			if (file != null) {
				event.data = new String[] { file.getAbsolutePath() };
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = CopyTaskDetailsAction.getTextForTask(selectedElement);
		}
	}

	public void dragFinished(DragSourceEvent event) {
		// don't care if the drag is done
	}
}
