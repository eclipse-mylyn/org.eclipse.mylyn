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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 * @author Rob Elves (added URL based task creation support)
 */
public class TaskListDropAdapter extends ViewerDropAdapter {

	private Task newTask = null;

	public TaskListDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(true);
	}

	@Override
	public boolean performDrop(Object data) {

		Object currentTarget = getCurrentTarget();
		List<ITask> tasksToMove = new ArrayList<ITask>();

		if (isUrl(data) && createTaskFromUrl(data)) {
			tasksToMove.add(newTask);
		} else {
			ISelection selection = ((TreeViewer) getViewer()).getSelection();
			for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
				ITask toMove = null;
				if (selectedObject instanceof ITask) {
					toMove = (ITask) selectedObject;
				} else if (selectedObject instanceof AbstractQueryHit) {
					toMove = ((AbstractQueryHit) selectedObject).getOrCreateCorrespondingTask();
				}
				if (toMove != null) {
					tasksToMove.add(toMove);
				}
			}
		}

		for (ITask task : tasksToMove) {
			if (currentTarget instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().getTaskList().moveToContainer((TaskCategory) currentTarget, task);
			} else if (currentTarget instanceof ITask) {
				ITask targetTask = (ITask) currentTarget;
				if (targetTask.getContainer() == null) {
					MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(task);
				} else {
					MylarTaskListPlugin.getTaskListManager().getTaskList().moveToContainer((TaskCategory) targetTask.getContainer(),
							task);
				}
			} else if (currentTarget == null) {
				MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(newTask);
			}
		}

		// Make new task the current selection in the view
		if (newTask != null) {
			StructuredSelection ss = new StructuredSelection(newTask);
			getViewer().setSelection(ss);
			getViewer().refresh();
		}

		return true;

	}

	/**
	 * @return true if string is a http(s) url
	 */
	public boolean isUrl(Object data) {
		String uri = "";
		if (data instanceof String) {
			uri = (String) data;
			if ((uri.startsWith("http://") || uri.startsWith("https://"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param data
	 *            string containing url and title separated by <quote>\n</quote>
	 * @return true if task succesfully created, false otherwise
	 */
	public boolean createTaskFromUrl(Object data) {

		if (!(data instanceof String))
			return false;

		String[] urlTransfer = ((String) data).split("\n");

		String url = "";
		String urlTitle = "<retrieving from URL>";

		if (urlTransfer.length > 0) {
			url = urlTransfer[0];
		} else {
			return false;
		}

		// Removed in order to default to retrieving title from url rather than
		// accepting what was sent by the brower's DnD code. (see bug 114401)
		// If a Title is provided, use it.
		// if (urlTransfer.length > 1) {
		// urlTitle = urlTransfer[1];
		// }
		// if (urlTransfer.length < 2) { // no title provided
		// retrieveTaskDescription(url);
		// }
		retrieveTaskDescription(url);

		newTask = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), urlTitle, true);

		if (newTask == null) {
			return false;
		}

		newTask.setPriority(Task.PriorityLevel.P3.toString());
		newTask.setUrl(url);
		TaskUiUtil.openEditor(newTask);
//		newTask.openTaskInEditor(true);

		return true;

	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer()).getSelection()).getFirstElement();
		if (!(selectedObject instanceof AbstractRepositoryQuery)) {
//		if (selectedObject instanceof ITaskListElement && ((ITaskListElement) selectedObject).isDragAndDropEnabled()) {
			if (getCurrentTarget() instanceof TaskCategory) {
				return true;
			} else if (getCurrentTarget() instanceof ITaskListElement
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else {
				return false;
			}
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {

		try {
			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(url) {
				@Override
				protected void setTitle(final String pageTitle) {
					newTask.setDescription(pageTitle);
					MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(newTask);
				}
			};
			job.schedule();
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not open task web page", false);
		}
	}
}
