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

package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListTableSorter extends ViewerSorter {

	private final TaskListView view;

	private String column;

	private TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	public TaskListTableSorter(TaskListView view, String column) {
		super();
		this.view = view;
		this.column = column;
	}

	public void setColumn(String column) {
		this.column = column;
		if (view.isFocusedMode()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TasksUiPlugin.TITLE_DIALOG, 
					"Manual sorting is disabled in focused mode, sort order will not take effect until focused mode is disabled.");
		}
	}

	/**
	 * compare - invoked when column is selected calls the actual comparison
	 * method for particular criteria
	 */
	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {
		if (o1 instanceof AbstractTaskContainer && o2 instanceof TaskArchive) {
			return -1;
		} else if (o2 instanceof AbstractTaskContainer && o1 instanceof TaskArchive) {
			return 1;
		}

		if (o1 instanceof AbstractTaskContainer && o2 instanceof ITask) {
			return 1;
		}
		if (o1 instanceof AbstractTaskContainer || o1 instanceof AbstractRepositoryQuery) {
			if (o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				
				return this.view.sortDirection
						* ((ITaskListElement) o1).getSummary().compareToIgnoreCase(((ITaskListElement) o2).getSummary());
			} else {
				return -1;
			}
		} else if (o1 instanceof ITaskListElement) {
			if (o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				return -1;
			} else if (o2 instanceof ITaskListElement) {
				ITaskListElement element1 = (ITaskListElement) o1;
				ITaskListElement element2 = (ITaskListElement) o2;

				return compareElements(element1, element2);
			}
		} else {
			return 0;
		}
		return 0;
	}

	private int compareElements(ITaskListElement element1, ITaskListElement element2) {
		if (column != null && column.equals(this.view.columnNames[1])) {
			return 0;
		} else if (column == this.view.columnNames[2]) {
			return this.view.sortDirection * element1.getPriority().compareTo(element2.getPriority());
		} else if (column == this.view.columnNames[4]) {
			String summary1 = element1.getSummary();
			String summary2 = element2.getSummary();
			
			if (element1 instanceof AbstractQueryHit) {
				AbstractRepositoryTask task1 = ((AbstractQueryHit)element1).getCorrespondingTask();
				if (task1 != null && task1.getIdLabel() != null) {
					summary1 = task1.getIdLabel() + ": " + summary1;
				}
			} else if (element1 instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask task1 = (AbstractRepositoryTask)element1;
				if (task1.getIdLabel() != null) {
					summary1 = task1.getIdLabel() + ": " + summary1;
				}
			}
			if (element2 instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask task2 = (AbstractRepositoryTask)element2;
				if (task2.getIdLabel() != null) {
					summary2 = task2.getIdLabel() + ": " + summary2;
				}
			} else if (element2 instanceof AbstractQueryHit) {
				AbstractRepositoryTask task2 = ((AbstractQueryHit)element2).getCorrespondingTask();
				if (task2 != null && task2.getIdLabel() != null) {
					summary2 = task2.getIdLabel() + ": " + summary2;
				}
			}
			return this.view.sortDirection * taskKeyComparator.compare(summary1, summary2);
		} else {
			return 0;
		}
	}
}