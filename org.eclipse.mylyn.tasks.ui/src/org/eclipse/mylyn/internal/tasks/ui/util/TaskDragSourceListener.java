/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 * @author Leo Dos Santos
 * @author Steffen Pingel
 */
public class TaskDragSourceListener extends DragSourceAdapter {

	static final String DELIM = ", ";

	private IStructuredSelection selection;

	private final ISelectionProvider selectionProvider;

	public TaskDragSourceListener(ISelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		ISelection selection = selectionProvider.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			this.selection = (IStructuredSelection) selection;
		} else {
			this.selection = null;
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
		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		List<AbstractTask> tasks = new ArrayList<AbstractTask>();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof AbstractRepositoryQuery) {
				queries.add((AbstractRepositoryQuery) element);
			} else if (element instanceof AbstractTask) {
				tasks.add((AbstractTask) element);
			}
		}

		List<File> taskFiles = new ArrayList<File>(queries.size() + tasks.size());
		try {
			for (AbstractRepositoryQuery query : queries) {
				String encodedName = URLEncoder.encode(query.getHandleIdentifier(), ITasksCoreConstants.FILENAME_ENCODING);
				File file = File.createTempFile(encodedName, ITasksCoreConstants.FILE_EXTENSION, tempDir);
				file.deleteOnExit();

				TasksUiPlugin.getTaskListManager().getTaskListWriter().writeQueries(Collections.singletonList(query),
						file);
				taskFiles.add(file);
			}

			for (AbstractTask task : tasks) {
				String encodedName = URLEncoder.encode(task.getHandleIdentifier(), ITasksCoreConstants.FILENAME_ENCODING);
				File file = File.createTempFile(encodedName, ITasksCoreConstants.FILE_EXTENSION, tempDir);
				file.deleteOnExit();

				TasksUiPlugin.getTaskListManager().getTaskListWriter().writeTask(task, file);
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
		if (selection == null || selection.isEmpty()) {
			return;
		}

		if (TaskTransfer.getInstance().isSupportedType(event.dataType)) {
			List<AbstractTask> tasks = new ArrayList<AbstractTask>();
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				AbstractTaskContainer element = (AbstractTaskContainer) it.next();
				if (element instanceof AbstractTask) {
					tasks.add((AbstractTask) element);
				}
			}
			event.data = tasks.toArray(new AbstractTask[0]);
		} else if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			List<File> files = createTaskFiles(selection);
			if (files != null && !files.isEmpty()) {
				String[] paths = new String[files.size()];
				int i = 0;
				for (File file : files) {
					paths[i++] = file.getAbsolutePath();
				}
				event.data = paths;
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			if (selection.getFirstElement() instanceof RepositoryTaskData) {
				RepositoryTaskData taskData = (RepositoryTaskData) selection.getFirstElement();
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						taskData.getConnectorKind());
				if (connector != null) {
					event.data = connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskId());
				} else {
					event.data = taskData.getSummary();
				}
			} else {
				event.data = CopyTaskDetailsAction.getTextForTask(selection.getFirstElement());
			}
		}
	}

}
