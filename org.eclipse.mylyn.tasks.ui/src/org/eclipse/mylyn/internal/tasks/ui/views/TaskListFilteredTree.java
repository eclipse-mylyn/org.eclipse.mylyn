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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.actions.PreviousTaskDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskWorkingSetAction;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos - Task Working Set UI
 */
public class TaskListFilteredTree extends AbstractFilteredTree {

	public TaskListFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
	}

	public static final String LABEL_NO_ACTIVE = "... ";

	public static final String LABEL_NO_SETS = "All";

	public static final String LABEL_MULTIPLE_SETS = "<multiple sets>";

	private Hyperlink activeSetLabel;

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

			public void containersChanged(Set<TaskContainerDelta> containers) {
				for (TaskContainerDelta taskContainerDelta : containers) {
					if (taskContainerDelta.getContainer() instanceof AbstractTask) {
						updateTaskProgressBar();
						break;
					}
				}
			}
		});

		TasksUiPlugin.getTaskListManager().addActivityListener(new ITaskActivityListener() {

			public void activityChanged(ScheduledTaskContainer week) {
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
	protected Composite createWorkingSetComposite(Composite container) {
		final Button workingSetButton = new Button(container, SWT.ARROW | SWT.RIGHT);
		workingSetButton.setImage(TasksUiImages.getImage(TasksUiImages.BLANK_TINY));

		activeSetLabel = new Hyperlink(container, SWT.LEFT);
		activeSetLabel.setText(LABEL_NO_SETS);
		indicateActiveTaskWorkingSet();

		final TaskWorkingSetAction action = new TaskWorkingSetAction();
//		action.setImageDescriptor(TasksUiImages.BLANK_TINY);
//		action.setText(null);

		workingSetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action.getMenu(workingSetButton).setVisible(true);
			}
		});

		activeSetLabel.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				action.run();
			}
		});

//		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
//		manager.add(action);
//		manager.createControl(container);

		return activeSetLabel;
	}

	@Override
	protected Composite createStatusComposite(Composite container) {
		final Button activeTaskButton = new Button(container, SWT.ARROW | SWT.RIGHT);
		activeTaskButton.setImage(TasksUiImages.getImage(TasksUiImages.BLANK_TINY));

		activeTaskLabel = new Hyperlink(container, SWT.LEFT);
		activeTaskLabel.setText(LABEL_NO_ACTIVE);
		AbstractTask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask(activeTask);
		}

		final PreviousTaskDropDownAction action = new PreviousTaskDropDownAction(TasksUiPlugin.getTaskListManager()
				.getTaskActivationHistory());
//		action.setImageDescriptor(TasksUiImages.BLANK_TINY);
//		action.setText(null);

		activeTaskButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action.getMenu(activeTaskButton).setVisible(true);
			}
		});

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
				TasksUiUtil.refreshAndOpenTaskListElement((TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask()));
			}

		});

//		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
//		manager.add(action);
//		manager.createControl(container);

		return activeTaskLabel;
	}

	private Set<IWorkingSet> getActiveTaskWorkingSets() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
			Set<IWorkingSet> allSets = new HashSet<IWorkingSet>(Arrays.asList(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.getWorkingSets()));
			for (IWorkingSet workingSet : allSets) {
				if (!workingSet.getId().equalsIgnoreCase(TaskWorkingSetAction.ID_TASK_WORKING_SET)) {
					allSets.remove(workingSet);
				}
			}
			return allSets;
		} else {
			return Collections.emptySet();
		}
	}

	public void indicateActiveTaskWorkingSet() {
		Set<IWorkingSet> activeSets = getActiveTaskWorkingSets();
		if (filterComposite.isDisposed() || activeSets == null) {
			return;
		}

		if (activeSets.size() == 0) {
			activeSetLabel.setText(LABEL_NO_SETS);
			activeSetLabel.setToolTipText(LABEL_NO_SETS);
		} else if (activeSets.size() > 1) {
			activeSetLabel.setText(LABEL_MULTIPLE_SETS);
			activeSetLabel.setToolTipText(LABEL_MULTIPLE_SETS);
		} else {
			Object[] array = activeSets.toArray();
			IWorkingSet theSet = (IWorkingSet) array[0];
			activeSetLabel.setText(theSet.getLabel());
			activeSetLabel.setToolTipText(theSet.getLabel());
		}
		filterComposite.layout();
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
