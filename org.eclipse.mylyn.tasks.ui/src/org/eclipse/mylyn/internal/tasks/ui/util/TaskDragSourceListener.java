/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TransferList;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 * @author Steffen Pingel
 */
public class TaskDragSourceListener extends DragSourceAdapter {

	private IStructuredSelection currentSelection;

	private final ISelectionProvider selectionProvider;

	public TaskDragSourceListener(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		ISelection selection = selectionProvider.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			this.currentSelection = (IStructuredSelection) selection;
			Iterator<?> it = currentSelection.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				if (item instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) item;
					for (AbstractTaskContainer container : task.getParentContainers()) {
						if (container instanceof UnsubmittedTaskContainer) {
							event.doit = false;
							return;
						}
					}
				}
			}
		} else {
			this.currentSelection = null;
			event.doit = false;
		}
	}

	private File export(IStructuredSelection selection) {
		// extract queries and tasks from selection
		TransferList list = new TransferList();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof AbstractTaskCategory) {
				list.addCategory((TaskCategory) element);
			} else if (element instanceof RepositoryQuery) {
				list.addQuery((RepositoryQuery) element);
			} else if (element instanceof ITask) {
				list.addTask((AbstractTask) element);
			}
		}

		TaskListExternalizer externalizer = TasksUiPlugin.getDefault().createTaskListExternalizer();
		try {
			File file = File.createTempFile(ITasksCoreConstants.EXPORT_FILE_NAME, ITasksCoreConstants.FILE_EXTENSION);
			file.deleteOnExit();
			externalizer.writeTaskList(list, file);
			return file;
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Problems encountered dragging task", //$NON-NLS-1$
					e));
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Problems encountered dragging task", //$NON-NLS-1$
					e));
		}
		return null;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (currentSelection == null || currentSelection.isEmpty()) {
			return;
		}

		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
			LocalSelectionTransfer.getTransfer().setSelection(currentSelection);
		} else if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			File file = export(currentSelection);
			if (file != null) {
				String[] paths = new String[1];
				paths[0] = file.getAbsolutePath();
				event.data = paths;
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = CopyTaskDetailsAction.getTextForTask(currentSelection.getFirstElement());
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
			LocalSelectionTransfer.getTransfer().setSelection(null);
		}
	}

}
