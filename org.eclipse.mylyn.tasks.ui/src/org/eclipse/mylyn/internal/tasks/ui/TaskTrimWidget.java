/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.reflect.MethodUtils;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.ui.SelectionProviderAdapter;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.actions.RepositoryElementActionGroup;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class TaskTrimWidget extends WorkbenchWindowControlContribution {

	public static String ID_CONTAINER = "org.eclipse.mylyn.tasks.ui.trim.container"; //$NON-NLS-1$

	public static String ID_CONTROL = "org.eclipse.mylyn.tasks.ui.trim.control"; //$NON-NLS-1$

	private Composite composite;

	private ITask activeTask;

	private MenuManager menuManager;

	private Menu menu;

	private TaskScalingHyperlink activeTaskLabel;

	private final ITaskActivationListener taskActivationListener = new TaskActivationAdapter() {

		@Override
		public void taskActivated(ITask task) {
			activeTask = task;
			indicateActiveTask();
		}

		@Override
		public void taskDeactivated(ITask task) {
			activeTask = null;
			indicateNoActiveTask();
		}

	};

	private final ITaskListChangeListener taskListListener = new ITaskListChangeListener() {
		public void containersChanged(Set<TaskContainerDelta> containers) {
			// update label in case task changes
			if (activeTask != null) {
				for (TaskContainerDelta taskContainerDelta : containers) {
					if (activeTask.equals(taskContainerDelta.getElement())) {
						if (taskContainerDelta.getKind().equals(TaskContainerDelta.Kind.CONTENT)) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									if (activeTask != null && activeTask.isActive()) {
										indicateActiveTask();
									}
								}
							});
							return;
						}
					}
				}
			}
		}
	};

	private SelectionProviderAdapter activeTaskSelectionProvider;

	private RepositoryElementActionGroup actionGroup;

	public TaskTrimWidget() {
		TasksUi.getTaskActivityManager().addActivationListener(taskActivationListener);
		TasksUiPlugin.getTaskList().addChangeListener(taskListListener);
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

		actionGroup.setSelectionProvider(null);

		TasksUi.getTaskActivityManager().removeActivationListener(taskActivationListener);
		TasksUiPlugin.getTaskList().removeChangeListener(taskListListener);
	}

	@Override
	protected Control createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);

		//composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));

		createStatusComposite(composite);

		return composite;
	}

	private Composite createStatusComposite(final Composite container) {
		GC gc = new GC(container);
		Point p = gc.textExtent("WWWWWWWWWWWWWWW"); //$NON-NLS-1$
		gc.dispose();

		activeTaskLabel = new TaskScalingHyperlink(container, SWT.RIGHT);
		// activeTaskLabel.setLayoutData(new GridData(p.x, SWT.DEFAULT));
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		gridData.widthHint = p.x;
		gridData.minimumWidth = p.x;
		gridData.horizontalIndent = 0;
		activeTaskLabel.setLayoutData(gridData);
		activeTaskLabel.setText(Messages.TaskTrimWidget__no_task_active_);

		activeTask = TasksUi.getTaskActivityManager().getActiveTask();
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
			@Override
			public void linkActivated(HyperlinkEvent e) {
				TaskListView taskListView = TaskListView.getFromActivePerspective();
				if (taskListView != null && taskListView.getDrilledIntoCategory() != null) {
					taskListView.goUpToRoot();
				}
				TasksUiInternal.refreshAndOpenTaskListElement((TasksUi.getTaskActivityManager().getActiveTask()));
			}
		});

		activeTaskLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// only handle left clicks, context menu is handled by platform
				if (e.button == 1) {
					if (activeTask == null) {
						return;
					}

					TaskListView taskListView = TaskListView.getFromActivePerspective();
					if (taskListView != null && taskListView.getDrilledIntoCategory() != null) {
						taskListView.goUpToRoot();
					}

					TasksUiInternal.refreshAndOpenTaskListElement(activeTask);
				}
			}
		});

		return activeTaskLabel;
	}

	private void hookContextMenu() {
		activeTaskSelectionProvider = new SelectionProviderAdapter();

		actionGroup = new RepositoryElementActionGroup();
		actionGroup.setSelectionProvider(activeTaskSelectionProvider);

		menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
				// trims do not have a workbench part so there is no simple way of registering the
				// context menu
				if (!contributeObjectActionsOld(manager)) {
					contributeObjectActionsNew(manager);
				}
			}
		});
	}

	public void indicateActiveTask() {
		if (activeTaskLabel == null || activeTaskLabel.isDisposed()) {
			return;
		}

		//activeTaskLabel.setText(shortenText(activeTask.getSummary()));
		activeTaskLabel.setText(activeTask.getSummary());
		activeTaskLabel.setUnderlined(true);
		activeTaskLabel.setToolTipText(activeTask.getSummary());
		activeTaskSelectionProvider.setSelection(new StructuredSelection(activeTask));
	}

	public void indicateNoActiveTask() {
		if (activeTaskLabel == null || activeTaskLabel.isDisposed()) {
			return;
		}

		activeTaskLabel.setText(Messages.TaskTrimWidget__no_active_task_);
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText(""); //$NON-NLS-1$
		activeTaskSelectionProvider.setSelection(StructuredSelection.EMPTY);
	}

	private boolean contributeObjectActionsOld(IMenuManager manager) {
		try {
			MethodUtils.invokeExactMethod(ObjectActionContributorManager.getManager(), "contributeObjectActions", //$NON-NLS-1$
					new Object[] { null, manager, activeTaskSelectionProvider }, new Class[] { IWorkbenchPart.class,
				IMenuManager.class, ISelectionProvider.class });
		} catch (NoSuchMethodException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		}
		return true;
	}

	private boolean contributeObjectActionsNew(IMenuManager manager) {
		try {
			MethodUtils.invokeExactMethod(ObjectActionContributorManager.getManager(), "contributeObjectActions", //$NON-NLS-1$
					new Object[] { null, manager, activeTaskSelectionProvider, Collections.EMPTY_SET }, new Class[] {
				IWorkbenchPart.class, IMenuManager.class, ISelectionProvider.class, Set.class });
		} catch (NoSuchMethodException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		}
		return true;
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
