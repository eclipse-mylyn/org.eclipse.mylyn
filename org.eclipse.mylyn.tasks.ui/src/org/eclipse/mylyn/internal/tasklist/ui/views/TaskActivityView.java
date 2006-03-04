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

import java.util.Calendar;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.tasklist.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylar.provisional.tasklist.DateRangeTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Rob Elves
 */
public class TaskActivityView extends ViewPart {

	public static final String ID = "org.eclipse.mylar.tasklist.activity";

	private static TaskActivityView INSTANCE;

	private OpenTaskListElementAction openTaskEditor;

	// private OpenTaskInExternalBrowserAction openUrlInExternal;

	private String[] columnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated", "Reminder" };

	private int[] columnWidths = new int[] { 60, 30, 340, 90, 90, 100 };

	private TreeColumn[] columns;

	private TaskActivityLabelProvider taskHistoryTreeLabelProvider;

	private TreeViewer treeViewer;

	private TaskActivityContentProvider taskActivityTableContentProvider;

	public static TaskActivityView openInActivePerspective() {
		try {
			return (TaskActivityView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (Exception e) {
			return null;
		}
	}

	public TaskActivityView() {
		INSTANCE = this;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {

		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		getViewer().getTree().setHeaderVisible(true);
		getViewer().getTree().setLinesVisible(true);
		getViewer().setColumnProperties(columnNames);
		getViewer().setUseHashlookup(true);

		columns = new TreeColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new TreeColumn(getViewer().getTree(), SWT.LEFT);
			columns[i].setText(columnNames[i]);
			columns[i].setWidth(columnWidths[i]);
			// final int index = i;
			// columns[i].addSelectionListener(new SelectionAdapter() {
			// @Override
			// public void widgetSelected(SelectionEvent e) {
			// sortIndex = index;
			// sortDirection *= DEFAULT_SORT_DIRECTION;
			// getViewer().setSorter(new TaskActivityTableSorter());
			// }
			// });
			columns[i].addControlListener(new ControlListener() {
				public void controlResized(ControlEvent e) {
					for (int j = 0; j < columnWidths.length; j++) {
						if (columns[j].equals(e.getSource())) {
							columnWidths[j] = columns[j].getWidth();
						}
					}
				}

				public void controlMoved(ControlEvent e) {
					// don't care if the control is moved
				}
			});
		}

		taskHistoryTreeLabelProvider = new TaskActivityLabelProvider(new TaskElementLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator(), parent.getBackground());

		getViewer().setSorter(new TaskActivityTableSorter());
		taskActivityTableContentProvider = new TaskActivityContentProvider(getViewer());
		getViewer().setContentProvider(taskActivityTableContentProvider);
		getViewer().setLabelProvider(taskHistoryTreeLabelProvider);
		getViewer().setInput(getViewSite());

		makeActions();
		hookOpenAction();

	}

	private void makeActions() {
		openTaskEditor = new OpenTaskListElementAction(this.getViewer());
		// openUrlInExternal = new OpenTaskInExternalBrowserAction();
	}

	private void hookOpenAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openTaskEditor.run();
			}
		});
	}

	public static TaskActivityView getDefault() {
		return INSTANCE;
	}

	public TreeViewer getViewer() {
		return treeViewer;
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public ITask getSelectedTask() {
		ISelection selection = getViewer().getSelection();
		if (selection.isEmpty())
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
				return (ITask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
		// ignore
	}

	private class TaskActivityTableSorter extends ViewerSorter {

		public TaskActivityTableSorter() {
			super();
		}

		@Override
		public int compare(Viewer compareViewer, Object o1, Object o2) {
			if (o1 instanceof DateRangeTaskContainer) {
				if (o2 instanceof DateRangeTaskContainer) {
					DateRangeTaskContainer dateRangeTaskContainer1 = (DateRangeTaskContainer) o1;
					DateRangeTaskContainer dateRangeTaskContainer2 = (DateRangeTaskContainer) o2;
					return dateRangeTaskContainer2.getStart().compareTo(dateRangeTaskContainer1.getStart());
				} else {
					return 1;
				}
			} else if (o1 instanceof ITask) {
				if (o2 instanceof ITaskContainer) {
					return -1;
				} else if (o2 instanceof ITask) {
					ITask task1 = (ITask) o1;
					ITask task2 = (ITask) o2;
					Calendar calendar1 = taskActivityTableContentProvider
							.getLastOccurrence(task1.getHandleIdentifier());
					Calendar calendar2 = taskActivityTableContentProvider
							.getLastOccurrence(task2.getHandleIdentifier());
					if (calendar1 != null && calendar2 != null) {
						return calendar2.compareTo(calendar1);
					}
				}
			}
			return 0;
		}
	}

}
