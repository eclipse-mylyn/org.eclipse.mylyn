/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenWithBrowserAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.layout.IWindowTrim;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class TaskTrimWidget extends WorkbenchWindowControlContribution {

	public static String ID_CONTAINER = "org.eclipse.mylyn.tasks.ui.trim.container"; //$NON-NLS-1$

	public static String ID_CONTROL = "org.eclipse.mylyn.tasks.ui.trim.control"; //$NON-NLS-1$

	private Composite composite = null;

	private ITask activeTask = null;

	private MenuManager menuManager = null;

	private Menu menu = null;

	private TaskHyperlink activeTaskLabel;

	private final OpenWithBrowserAction openWithBrowserAction = new OpenWithBrowserAction();

	private final CopyTaskDetailsAction copyTaskDetailsAction = new CopyTaskDetailsAction();

	private Point p;

	private final ITaskActivationListener TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

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

	private final IPropertyChangeListener SHOW_TRIM_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			String property = event.getProperty();
			if (property.equals(ITasksUiPreferenceConstants.SHOW_TRIM)) {
				setTrimVisible((Boolean) event.getNewValue());
			}
		}
	};

	public TaskTrimWidget() {
		TasksUi.getTaskActivityManager().addActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(SHOW_TRIM_LISTENER);
		hookContextMenu();
	}

	private void setTrimVisible(boolean visible) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window instanceof WorkbenchWindow) {
			IWindowTrim trim = ((WorkbenchWindow) window).getTrimManager().getTrim(ID_CONTAINER);
			if (trim != null) {
				((WorkbenchWindow) window).getTrimManager().setTrimVisible(trim, visible);
			}
		}
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

		TasksUi.getTaskActivityManager().removeActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(SHOW_TRIM_LISTENER);
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

		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));

		createStatusComposite(composite);

		parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
				setTrimVisible(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));
			}
		});

		return composite;
	}

	private Composite createStatusComposite(final Composite container) {
		GC gc = new GC(container);
		p = gc.textExtent("WWWWWWWWWWWWWWW"); //$NON-NLS-1$
		gc.dispose();

		activeTaskLabel = new TaskHyperlink(container, SWT.RIGHT);
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
				if (activeTask == null) {
					return;
				}

				TaskListView taskListView = TaskListView.getFromActivePerspective();
				if (taskListView != null && taskListView.getDrilledIntoCategory() != null) {
					taskListView.goUpToRoot();
				}

				TasksUiInternal.refreshAndOpenTaskListElement(activeTask);
			}
		});

		return activeTaskLabel;
	}

	private void hookContextMenu() {
		menuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
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
				@Override
				public void run() {
					TasksUiInternal.refreshAndOpenTaskListElement(activeTask);
				}
			});

			manager.add(openWithBrowserAction);
			if (TasksUiInternal.isValidUrl(activeTask.getUrl())) {
				openWithBrowserAction.setEnabled(true);
			} else {
				openWithBrowserAction.setEnabled(false);
			}

			if (activeTask.isActive()) {
				manager.add(new TaskDeactivateAction() {
					@Override
					public void run() {
						super.run(activeTask);
					}
				});
			} else {
				manager.add(new TaskActivateAction() {
					@Override
					public void run() {
//						TasksUiPlugin.getTaskListManager().getTaskActivationHistory().addTask(activeTask);
						super.run(activeTask);
					}
				});
			}

			manager.add(new Separator());

			for (String menuPath : TasksUiPlugin.getDefault().getDynamicMenuMap().keySet()) {
				for (IDynamicSubMenuContributor contributor : TasksUiPlugin.getDefault().getDynamicMenuMap().get(
						menuPath)) {
					List<IRepositoryElement> selectedElements = new ArrayList<IRepositoryElement>();
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

		activeTaskLabel.setText(Messages.TaskTrimWidget__no_active_task_);
		activeTaskLabel.setUnderlined(false);
		activeTaskLabel.setToolTipText(""); //$NON-NLS-1$
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
