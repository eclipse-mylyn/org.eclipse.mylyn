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

import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Mik Kersten
 */
public class TaskListFilteredTree extends AbstractMylarFilteredTree {

	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
	}

	public static final String LABEL_NO_ACTIVE = "<no active task>";

	private Hyperlink activeTaskLabel;

	private WorkweekProgressBar taskProgressBar;

	private int totalTasks;

	private int completeTime;

	private int completeTasks;

	private int incompleteTime;

	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		// Use a single Composite for the Tree to being able to use the
		// TreeColumnLayout. See Bug 177891 for more details.
		Composite container = new Composite(parent, SWT.None);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.verticalIndent = 0;
		gridData.horizontalIndent = 0;
		container.setLayoutData(gridData);
		container.setLayout(new TreeColumnLayout());
		return super.doCreateTreeViewer(container, style);
	}

	@Override
	protected Composite createProgressComposite(Composite container) {
		Composite progressComposite = new Composite(container, SWT.NONE);
		GridLayout progressLayout = new GridLayout(1, false);
		progressLayout.marginWidth = 4;
		progressLayout.marginHeight = 0;
		progressLayout.marginBottom = 0;
		progressLayout.horizontalSpacing = 0;
		progressLayout.verticalSpacing = 0;
		progressComposite.setLayout(progressLayout);
		progressComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 4, 1));

		taskProgressBar = new WorkweekProgressBar(progressComposite);
		taskProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		updateTaskProgressBar();

		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(new ITaskListChangeListener() {

			public void containerAdded(AbstractTaskListElement container) {
			}

			public void containerDeleted(AbstractTaskListElement container) {
			}

			public void containerInfoChanged(AbstractTaskListElement container) {
			}

			public void localInfoChanged(AbstractTask task) {
				updateTaskProgressBar();
			}

			public void repositoryInfoChanged(AbstractTask task) {
			}

			public void taskAdded(AbstractTask task) {
			}

			public void taskDeleted(AbstractTask task) {
			}

			public void taskMoved(AbstractTask task, AbstractTaskListElement fromContainer, AbstractTaskListElement toContainer) {
			}
		});

		TasksUiPlugin.getTaskListManager().addActivityListener(new ITaskActivityListener() {

			public void activityChanged(DateRangeContainer week) {
				updateTaskProgressBar();
			}

			public void calendarChanged() {
			}

			public void taskActivated(AbstractTask task) {
			}

			public void taskDeactivated(AbstractTask task) {
			}

			public void taskListRead() {
			}

			public void tasksActivated(List<AbstractTask> tasks) {
			}

		});
		return progressComposite;
	}

	private void updateTaskProgressBar() {
		if (taskProgressBar.isDisposed()) {
			return;
		}

		Set<AbstractTask> tasksThisWeek = TasksUiPlugin.getTaskListManager().getScheduledForThisWeek();
		totalTasks = tasksThisWeek.size();
		completeTime = 0;
		completeTasks = 0;
		incompleteTime = 0;
		for (AbstractTask task : tasksThisWeek) {
			if (task.isCompleted()) {
				completeTasks++;
				if (task.getEstimateTimeHours() > 0) {
					completeTime += task.getEstimateTimeHours();
				} else {
					completeTime++;
				}
			} else {
				if (task.getEstimateTimeHours() > 0) {
					incompleteTime += task.getEstimateTimeHours();
				} else {
					incompleteTime++;
				}
			}
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (PlatformUI.isWorkbenchRunning() && !taskProgressBar.isDisposed()) {
					taskProgressBar.reset(completeTime, (completeTime + incompleteTime));
					taskProgressBar.setToolTipText("Workweek Progress" + "\n     Hours: " + completeTime + " of "
							+ (completeTime + incompleteTime) + " estimated" + "\n     Tasks: " + completeTasks
							+ " of " + totalTasks + " scheduled");
				}
			}
		});
	}

	@Override
	protected Composite createStatusComposite(Composite container) {

		activeTaskLabel = new Hyperlink(container, SWT.LEFT);
		activeTaskLabel.setText(LABEL_NO_ACTIVE);
		AbstractTask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}

		activeTaskLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (TaskListFilteredTree.super.filterText.getText().length() > 0) {
					TaskListFilteredTree.super.filterText.setText("");
					TaskListFilteredTree.this.textChanged();
				}
				if (TaskListView.getFromActivePerspective().getDrilledIntoCategory() != null) {
					TaskListView.getFromActivePerspective().goUpToRoot();
				}
				TasksUiUtil.refreshAndOpenTaskListElement((TasksUiPlugin.getTaskListManager().getTaskList()
						.getActiveTask()));
			}

		});
		return activeTaskLabel;
	}

	public void indicateActiveTask(AbstractTask task) {
		if (filterComposite.isDisposed()) {
			return;
		}

		String text = task.getSummary();
		activeTaskLabel.setText(text);
		activeTaskLabel.setUnderlined(true);
		activeTaskLabel.setToolTipText(task.getSummary());
		filterComposite.layout();
	}

	public String getActiveTaskLabelText() {
		return activeTaskLabel.getText();
	}

	public void indicateNoActiveTask() {
		if (filterComposite.isDisposed()) {
			return;
		}

		activeTaskLabel.setText(LABEL_NO_ACTIVE);
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText("");
		filterComposite.layout();
	}

	@Override
	public void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}
}
