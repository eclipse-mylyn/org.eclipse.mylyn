/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * @author Mik Kersten
 * @author Jevgeni Holodkov
 */
class TaskListDragSourceListener implements DragSourceListener {

	static final String DELIM = ", ";

	private final TaskListView view;

	private File[] queryTempFiles;
	private File[] taskTempFiles;

//	static final String ID_DATA_TASK_DRAG = "task-drag";

	/**
	 * @param view
	 */
	public TaskListDragSourceListener(TaskListView view) {
		this.view = view;
	}

	public void dragStart(DragSourceEvent event) {
		StructuredSelection selection = (StructuredSelection) this.view.getViewer().getSelection();
		if (selection.isEmpty()) {
			event.doit = false;
		} else {
			// prepare temporary directory 
			File tempDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "temp");
			if (!tempDir.exists()) {
				tempDir.mkdir();
			}
			
			// prepare query files
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

			try {
				
				// queries
				{
					int counter = 0;
					queryTempFiles = new File[queries.size()];
					for (AbstractRepositoryQuery query : queries) {
						List<AbstractRepositoryQuery> queryList = new ArrayList<AbstractRepositoryQuery>();
						queryList.add(query);

						String encodedName = "query";
						try {
							encodedName = URLEncoder.encode(query.getHandleIdentifier(),
									ITasksUiConstants.FILENAME_ENCODING);
						} catch (UnsupportedEncodingException e) {
							StatusHandler.fail(e, "Could not determine path for context", false);
						}

						queryTempFiles[counter] = File.createTempFile(encodedName, ITasksUiConstants.FILE_EXTENSION,
								tempDir);
						queryTempFiles[counter].deleteOnExit();
						TasksUiPlugin.getTaskListManager().getTaskListWriter().writeQueries(queryList,
								queryTempFiles[counter]);
						counter++;
					}
				}

				// tasks
				{
					int counter = 0;
					taskTempFiles = new File[tasks.size()];
					for (AbstractTask task : tasks) {
						String encodedName = "task" + counter;
						try {
							encodedName = URLEncoder.encode(task.getHandleIdentifier(),
									ITasksUiConstants.FILENAME_ENCODING);
						} catch (UnsupportedEncodingException e) {
							StatusHandler.fail(e, "Could not determine path for context", false);
						}

						taskTempFiles[counter] = File.createTempFile(encodedName, ITasksUiConstants.FILE_EXTENSION,
								tempDir);
						taskTempFiles[counter].deleteOnExit();
						TasksUiPlugin.getTaskListManager().getTaskListWriter().writeTask(task, taskTempFiles[counter]);
						counter++;
					}
				}
			} catch (IOException e) {
				StatusHandler.fail(e, "Cannot create a temp query file for Drag&Drop", true);
			}
		}
	}

	public void dragSetData(DragSourceEvent event) {
		StructuredSelection selection = (StructuredSelection) this.view.getViewer().getSelection();
		AbstractTaskContainer selectedElement = null;
		if (((IStructuredSelection) selection).getFirstElement() instanceof AbstractTaskContainer) {
			selectedElement = (AbstractTaskContainer) ((IStructuredSelection) selection).getFirstElement();
		}
		
		if (TaskTransfer.getInstance().isSupportedType(event.dataType)) {
			List<AbstractTask> tasks = new ArrayList<AbstractTask>();
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				AbstractTaskContainer element = (AbstractTaskContainer) iter.next();
				if (element instanceof AbstractTask) {
					tasks.add((AbstractTask) element);
				}
			}
			event.data = tasks.toArray();
		} else if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
			// detect context paths
			String[] taskPaths = new String[taskTempFiles.length];
			for (int i = 0; i < taskTempFiles.length; i++) {
				taskPaths[i] = taskTempFiles[i].getAbsolutePath();
			}

			// detect query paths
			String[] queryPaths = new String[queryTempFiles.length];
			for (int i = 0; i < queryTempFiles.length; i++) {
				queryPaths[i] = queryTempFiles[i].getAbsolutePath();
			}
			
			// combine paths if needed
			String[] paths = new String[taskTempFiles.length + queryTempFiles.length];
			System.arraycopy(taskPaths, 0, paths, 0, taskTempFiles.length);
			System.arraycopy(queryPaths, 0, paths, taskTempFiles.length, queryTempFiles.length);
			
			if (paths.length > 0) {
				event.data = paths;
			}
		} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = CopyTaskDetailsAction.getTextForTask(selectedElement);
		}
	}

	public void dragFinished(DragSourceEvent event) {
		// don't care if the drag is done
	}
}
