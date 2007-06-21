package org.eclipse.mylyn.internal.tasks.ui;

/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskHistoryDropDownAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class TaskTrimWidget extends WorkbenchWindowControlContribution {

	public static String ID_CONTAINER = "org.eclipse.mylyn.tasks.ui.trim.container";

	public static String ID_CONTROL = "org.eclipse.mylyn.tasks.ui.trim.control";

	private Composite composite = null;

	private AbstractTask activeTask = null;

	private MenuManager menuManager = null;

	private Menu menu = null;

	private TaskListHyperlink activeTaskLabel;

	private ActivateTaskHistoryDropDownAction navigateAction;

	private OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();

	private CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction(false);

	private Point p;

	private final ITaskActivityListener TASK_CHANGE_LISTENER = new ITaskActivityListener() {

		public void taskActivated(AbstractTask task) {
			activeTask = task;
			indicateActiveTask();
		}

		public void taskDeactivated(AbstractTask task) {
			activeTask = null;
			indicateNoActiveTask();
		}

		public void activityChanged(ScheduledTaskContainer week) {
		}

		public void calendarChanged() {
		}

		public void taskListRead() {
		}

		public void tasksActivated(List<AbstractTask> tasks) {
		}

	};

	public TaskTrimWidget() {
		super();
		TasksUiPlugin.getTaskListManager().addActivityListener(TASK_CHANGE_LISTENER);
		hookContextMenu();
	}

	@Override
	public void dispose() {
		if (composite != null && !composite.isDisposed()) {
			composite.dispose();
		}
		composite = null;

		if (menuManager != null) {
			menuManager.removeAll();
			menuManager.dispose();
		}
		menuManager = null;

		if (menu != null && !menu.isDisposed()) {
			menu.dispose();
		}
		menu = null;

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
		composite.setLayout(layout);

		GridData gridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
		composite.setLayoutData(gridData);

		navigateAction = new ActivateTaskHistoryDropDownAction(TasksUiPlugin.getTaskListManager().getTaskActivationHistory(), false);

		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		manager.add(navigateAction);
		manager.createControl(composite);

		createStatusComposite(composite);

		return composite;
	}

	private Composite createStatusComposite(final Composite container) {
		GC gc = new GC(container);
		p = gc.textExtent("WWWWWWWWWWWWWWW");
		gc.dispose();

		activeTaskLabel = new TaskListHyperlink(container, SWT.RIGHT);
		activeTaskLabel.setLayoutData(new GridData(p.x, SWT.DEFAULT));
		activeTaskLabel.setText("<no task active>");

		activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
		if (activeTask != null) {
			indicateActiveTask();
		}

		activeTaskLabel.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				if (menu != null) {
					menu.dispose();
				}
				menu = menuManager.createContextMenu(container);
				menu.setVisible(true);
			}
		});

		activeTaskLabel.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				if (TaskListView.getFromActivePerspective().getDrilledIntoCategory() != null) {
					TaskListView.getFromActivePerspective().goUpToRoot();
				}
				TasksUiUtil.refreshAndOpenTaskListElement((TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask()));
			}
		});

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

	private void hookContextMenu() {
		menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
	}

	// Inspired by TaskListView, TaskEditorActionContributor.
	private void fillContextMenu(IMenuManager manager) {
		if (activeTask != null) {
			IStructuredSelection selection = new StructuredSelection(activeTask);
			openWithBrowserAction.selectionChanged(selection);
			copyTaskDetailsAction.selectionChanged(selection);

			manager.add(new OpenTaskListElementAction(null) {
				public void run() {
					TasksUiUtil.refreshAndOpenTaskListElement(activeTask);
				}
			});

			manager.add(openWithBrowserAction);
			if (activeTask.hasValidUrl()) {
				openWithBrowserAction.setEnabled(true);
			} else {
				openWithBrowserAction.setEnabled(false);
			}

			if (activeTask.isActive()) {
				manager.add(new TaskDeactivateAction() {
					public void run() {
						super.run(activeTask);
					}
				});
			} else {
				manager.add(new TaskActivateAction() {
					public void run() {
						TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(activeTask);
						super.run(activeTask);
					}
				});
			}

			manager.add(new Separator());

			for (String menuPath : TasksUiPlugin.getDefault().getDynamicMenuMap().keySet()) {
				for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuMap().get(
						menuPath)) {
					List<AbstractTaskContainer> selectedElements = new ArrayList<AbstractTaskContainer>();
					selectedElements.add(activeTask);
					MenuManager subMenuManager = contributor.getSubMenuManager(selectedElements);
					if (subMenuManager != null) {
						manager.add(subMenuManager);
					}
				}
			}

			manager.add(new Separator());
			manager.add(copyTaskDetailsAction);
			manager.add(new Separator());

			ObjectActionContributorManager.getManager().contributeObjectActions(null, manager,
					new ISelectionProvider() {

						public void addSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore
						}

						public ISelection getSelection() {
							return new StructuredSelection(activeTask);
						}

						public void removeSelectionChangedListener(ISelectionChangedListener listener) {
							// ignore
						}

						public void setSelection(ISelection selection) {
							// ignore
						}
					});
		}
	}

	public void indicateActiveTask() {
		if (activeTaskLabel != null && activeTaskLabel.isDisposed()) {
			return;
		}

		//activeTaskLabel.setText(shortenText(activeTask.getSummary()));
		activeTaskLabel.setText(activeTask.getSummary());
		activeTaskLabel.setUnderlined(true);
		activeTaskLabel.setToolTipText(activeTask.getSummary());
	}

	public void indicateNoActiveTask() {
		if (activeTaskLabel != null && activeTaskLabel.isDisposed()) {
			return;
		}

		activeTaskLabel.setText("<no active task>");
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText("");
	}

//	// From PerspectiveBarContributionItem
//	private String shortenText(String taskLabel) {
//		if (taskLabel == null || composite == null || composite.isDisposed()) {
//			return null;
//		}
//
//		String returnText = taskLabel;
//		GC gc = new GC(composite);
//		int maxWidth = p.x;
//
//		if (gc.textExtent(taskLabel).x > maxWidth) {
//			for (int i = taskLabel.length(); i > 0; i--) {
//				String test = taskLabel.substring(0, i);
//				test = test + "...";
//				if (gc.textExtent(test).x < maxWidth) {
//					returnText = test;
//					break;
//				}
//			}
//		}
//
//		gc.dispose();
//		return returnText;
//	}
}
