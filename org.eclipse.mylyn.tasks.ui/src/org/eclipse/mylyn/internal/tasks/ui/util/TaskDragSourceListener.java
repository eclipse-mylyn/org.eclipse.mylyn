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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
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

	static final String DELIM = ", ";

	private IStructuredSelection structuredSelection;

	private final ISelectionProvider selectionProvider;

	public TaskDragSourceListener(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		ISelection selection = selectionProvider.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			this.structuredSelection = (IStructuredSelection) selection;
			Iterator<?> itr = structuredSelection.iterator();
			while (itr.hasNext()) {
				Object o = itr.next();
				if (o instanceof AbstractTask) {
					AbstractTask task = ((AbstractTask) o);
					for (AbstractTaskContainer container : task.getParentContainers()) {
						if (container instanceof UnsubmittedTaskContainer) {
							event.doit = false;
							return;
						}

					}
				}
			}
		} else {
			this.structuredSelection = null;
			event.doit = false;
		}
	}

	private List<File> createTaskFiles(IStructuredSelection selection) {
		// prepare temporary directory 
		File tempDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "temp");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		// extract queries and tasks from selection
		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
		List<AbstractTask> tasks = new ArrayList<AbstractTask>();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof IRepositoryQuery) {
				queries.add((RepositoryQuery) element);
			} else if (element instanceof ITask) {
				tasks.add((AbstractTask) element);
			}
		}

		List<File> taskFiles = new ArrayList<File>(queries.size() + tasks.size());
		try {
			for (RepositoryQuery query : queries) {
				String encodedName = URLEncoder.encode(query.getHandleIdentifier(),
						ITasksCoreConstants.FILENAME_ENCODING);
				File file = File.createTempFile(encodedName, ITasksCoreConstants.FILE_EXTENSION, tempDir);
				file.deleteOnExit();
				TasksUiPlugin.getTaskListWriter().writeQueries(Collections.singletonList(query), file);
				taskFiles.add(file);
			}

			for (AbstractTask task : tasks) {
				String encodedName = URLEncoder.encode(task.getHandleIdentifier(),
						ITasksCoreConstants.FILENAME_ENCODING);
				File file = File.createTempFile(encodedName, ITasksCoreConstants.FILE_EXTENSION, tempDir);
				file.deleteOnExit();
				TasksUiPlugin.getTaskListWriter().writeTask(task, file);
				taskFiles.add(file);
			}

			return taskFiles;
		} catch (IOException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Cannot create a temp query file for Drag&Drop", e));
			return null;
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (structuredSelection == null || structuredSelection.isEmpty()) {
			return;
		}

		if (TaskTransfer.getInstance().isSupportedType(event.dataType)) {
			List<AbstractTask> tasks = new ArrayList<AbstractTask>();
			for (Iterator<?> it = structuredSelection.iterator(); it.hasNext();) {
				Object element = it.next();
				if (element instanceof AbstractTask) {
					tasks.add((AbstractTask) element);
				}
			}
			event.data = tasks.toArray(new AbstractTask[0]);
		} else if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			List<File> files = createTaskFiles(structuredSelection);
			if (files != null && !files.isEmpty()) {
				String[] paths = new String[files.size()];
				int i = 0;
				for (File file : files) {
					paths[i++] = file.getAbsolutePath();
				}
				event.data = paths;
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			// FIXME 3.1 reimplement
			//			if (structuredSelection.getFirstElement() instanceof RepositoryTaskData) {
//				RepositoryTaskData taskData = (RepositoryTaskData) structuredSelection.getFirstElement();
//				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
//						taskData.getConnectorKind());
//				if (connector != null) {
//					event.data = connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskId());
//				} else {
//					event.data = taskData.getSummary();
//				}
//			} else {
			event.data = CopyTaskDetailsAction.getTextForTask(structuredSelection.getFirstElement());
//			}
		}
	}

}
