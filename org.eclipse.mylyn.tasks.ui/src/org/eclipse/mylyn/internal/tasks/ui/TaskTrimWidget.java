package org.eclipse.mylyn.internal.tasks.ui;

/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.List;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.tasks.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListFilteredTree;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class TaskTrimWidget extends WorkbenchWindowControlContribution {

	private Composite composite = null;

	private Hyperlink activeTaskLabel;

	private PreviousTaskDropDownAction navigateAction;

	private final ITaskActivityListener TASK_CHANGE_LISTENER = new ITaskActivityListener() {

		public void taskActivated(ITask task) {
			indicateActiveTask(task);
		}

		public void taskDeactivated(ITask task) {
			indicateNoActiveTask();
		}

		public void activityChanged(DateRangeContainer week) {
		}

		public void calendarChanged() {
		}

		public void taskListRead() {
		}

		public void tasksActivated(List<ITask> tasks) {
		}
		
	};

	public TaskTrimWidget() {
		super();
		TasksUiPlugin.getTaskListManager().addActivityListener(TASK_CHANGE_LISTENER);
	}

	@Override
	public void dispose() {
		if (composite != null && !composite.isDisposed())
			composite.dispose();
		composite = null;
		TasksUiPlugin.getTaskListManager().removeActivityListener(TASK_CHANGE_LISTENER);
		super.dispose();
	}

	@Override
	protected Control createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginRight = 5;
		composite.setLayout(layout);

		GridData gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		composite.setLayoutData(gridData);

		navigateAction = new PreviousTaskDropDownAction(TasksUiPlugin.getTaskListManager().getTaskActivationHistory());
		
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		manager.add(navigateAction);
		manager.createControl(composite);
		
		createStatusComposite(composite);
		
		return composite;
	}

	private Composite createStatusComposite(Composite container) {
		GC gc = new GC(container);
		Point p = gc.textExtent("WWWWWWWWWWWWWWW");
		gc.dispose();

		activeTaskLabel = new Hyperlink(container, SWT.RIGHT);
		activeTaskLabel.setLayoutData(new GridData(p.x, SWT.DEFAULT));
		activeTaskLabel.setText(TaskListFilteredTree.LABEL_NO_ACTIVE);

		ITask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}

		activeTaskLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// if (TaskListFilteredTree.super.filterText.getText().length()
				// > 0) {
				// TaskListFilteredTree.super.filterText.setText("");
				// TaskListFilteredTree.this.textChanged();
				// }
				if (TaskListView.getFromActivePerspective().getDrilledIntoCategory() != null) {
					TaskListView.getFromActivePerspective().goUpToRoot();
				}
				TasksUiUtil.refreshAndOpenTaskListElement((TasksUiPlugin.getTaskListManager().getTaskList()
						.getActiveTask()));
			}
		});

		return activeTaskLabel;
	}

	public void indicateActiveTask(ITask task) {
		if (activeTaskLabel.isDisposed()) {
			return;
		}
		
		activeTaskLabel.setText(task.getSummary());
		activeTaskLabel.setUnderlined(true);
		activeTaskLabel.setToolTipText(task.getSummary());
	}

	public void indicateNoActiveTask() {
		if (activeTaskLabel.isDisposed()) {
			return;
		}

		activeTaskLabel.setText(TaskListFilteredTree.LABEL_NO_ACTIVE);
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText("");
	}
}