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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.TaskArchive;
import org.eclipse.mylyn.tasks.core.UncategorizedCategory;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListTableSorter extends ViewerSorter {

	public enum SortByIndex {
		PRIORITY, SUMMARY, DATE_CREATED;
	}

	private final TaskListView view;

	private TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	private SortByIndex sortByIndex;

	public TaskListTableSorter(TaskListView view, SortByIndex sortByIndex) {
		super();
		this.view = view;
		this.sortByIndex = sortByIndex;
	}

	public void setColumn(String column) {
		if (view.isFocusedMode()) {
			MessageDialog
					.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							ITasksUiConstants.TITLE_DIALOG,
							"Manual sorting is disabled in focused mode, sort order will not take effect until focused mode is disabled.");
		}
	}

	/**
	 * compare - invoked when column is selected calls the actual comparison
	 * method for particular criteria
	 */
	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {
		if (o1 instanceof DateRangeContainer) {
			if (o2 instanceof DateRangeContainer) {
				DateRangeContainer dateRangeTaskContainer1 = (DateRangeContainer) o1;
				DateRangeContainer dateRangeTaskContainer2 = (DateRangeContainer) o2;
				return -1 * dateRangeTaskContainer2.getStart().compareTo(dateRangeTaskContainer1.getStart());
			} else if (o2 instanceof AbstractTask) {
				return 1;
			} else {
				return -1;
			}
		}

		if (o1 instanceof UncategorizedCategory && o2 instanceof AbstractTaskListElement) {
			return -1;
		} else if (o1 instanceof AbstractTaskListElement && o2 instanceof UncategorizedCategory) {
			return 1;
		}

		if (o1 instanceof AbstractTaskListElement && o2 instanceof TaskArchive) {
			return -1;
		} else if (o2 instanceof AbstractTaskListElement && o1 instanceof TaskArchive) {
			return 1;
		}

		if (!(o1 instanceof AbstractTask) && o2 instanceof AbstractTask) {
			return 1;
		}

		if (o1 instanceof AbstractTask && !(o2 instanceof AbstractTaskListElement)) {
			return -1;
		}

		// if (o1 instanceof AbstractTaskContainer || o1 instanceof
		// AbstractRepositoryQuery) {
		if (!(o1 instanceof AbstractTask)) {
			if (o2 instanceof AbstractTaskListElement || o2 instanceof AbstractRepositoryQuery) {

				return this.view.sortDirection
						* ((AbstractTaskListElement) o1).getSummary()
								.compareToIgnoreCase(((AbstractTaskListElement) o2).getSummary());
			} else {
				return -1;
			}
		} else if (o1 instanceof AbstractTaskListElement) {
			if (!(o2 instanceof AbstractTask)) {
				return -1;
			} else if (o2 instanceof AbstractTaskListElement) {
				AbstractTaskListElement element1 = (AbstractTaskListElement) o1;
				AbstractTaskListElement element2 = (AbstractTaskListElement) o2;

				return compareElements(element1, element2);
			}
		} else {
			return 0;
		}
		return 0;
	}

	private int compareElements(AbstractTaskListElement element1, AbstractTaskListElement element2) {
		if (SortByIndex.PRIORITY.equals(sortByIndex)) {
			int result = this.view.sortDirection * element1.getPriority().compareTo(element2.getPriority());
			if (result != 0) {
				return result;
			}
		} else if (SortByIndex.DATE_CREATED.equals(sortByIndex)) {
			AbstractTask t1 = null;
			AbstractTask t2 = null;
			if (element1 instanceof AbstractTask) {
				t1 = (AbstractTask) element1;
			}
			if (element2 instanceof AbstractTask) {
				t2 = (AbstractTask) element2;
			}
			if (t1 != null && t2 != null) {
				if (t1.getCreationDate() != null) {
					return t1.getCreationDate().compareTo(t2.getCreationDate());
				}
			}
		} else {
			String summary1 = getSortableSummaryFromElement(element1);
			String summary2 = getSortableSummaryFromElement(element2);
			element2.getSummary();
			return this.view.sortDirection * taskKeyComparator.compare(summary1, summary2);
		}
		return 0;
	}

	public static String getSortableSummaryFromElement(AbstractTaskListElement element) {
		String summary = element.getSummary();

		if (element instanceof AbstractTask) {
			AbstractTask task1 = (AbstractTask) element;
			if (task1.getTaskKey() != null) {
				summary = task1.getTaskKey() + ": " + summary;
			}
		}
		return summary;
	}
}