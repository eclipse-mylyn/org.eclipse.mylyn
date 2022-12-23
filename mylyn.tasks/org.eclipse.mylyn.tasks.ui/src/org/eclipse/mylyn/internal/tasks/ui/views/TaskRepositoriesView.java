/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.workbench.GradientDrawer;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.Category;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryModelListener;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryAdapter;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DisconnectRepositoryAction;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesView extends ViewPart {

	/**
	 * @deprecated Use {@link ITasksUiConstants#ID_VIEW_REPOSITORIES} instead
	 */
	@Deprecated
	public static final String ID = ITasksUiConstants.ID_VIEW_REPOSITORIES;

	private TreeViewer viewer;

	private final Action addRepositoryAction = new AddRepositoryAction();

	private BaseSelectionListenerAction deleteRepositoryAction;

	private BaseSelectionListenerAction resetConfigurationAction;

	private DisconnectRepositoryAction offlineAction;

	private void asyncExec(Runnable runnable) {
		if (Display.getCurrent() != null) {
			runnable.run();
		} else {
			Display.getDefault().asyncExec(runnable);
		}
	}

	private final IRepositoryModelListener MODEL_LISTENER = new IRepositoryModelListener() {

		public void loaded() {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}
	};

	private final IRepositoryListener REPOSITORY_LISTENER = new TaskRepositoryAdapter() {

		@Override
		public void repositoryAdded(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}

		@Override
		public void repositoryRemoved(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}

		@Override
		public void repositorySettingsChanged(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}
	};

	private final TaskRepositoryManager manager;

	private TaskRepositoriesContentProvider contentProvider;

	public TaskRepositoriesView() {
		manager = ((TaskRepositoryManager) TasksUi.getRepositoryManager());
		manager.addListener(REPOSITORY_LISTENER);
		TasksUiPlugin.getDefault().addModelListener(MODEL_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		TasksUiPlugin.getRepositoryManager().removeListener(REPOSITORY_LISTENER);
		TasksUiPlugin.getDefault().removeModelListener(MODEL_LISTENER);
	}

	public static TaskRepositoriesView getFromActivePerspective() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage == null) {
				return null;
			}
			IViewPart view = activePage.findView(ITasksUiConstants.ID_VIEW_REPOSITORIES);
			if (view instanceof TaskRepositoriesView) {
				return (TaskRepositoriesView) view;
			}
		}
		return null;
	}

	public static TaskRepositoriesView openInActivePerspective() {
		try {
			return (TaskRepositoriesView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.showView(ITasksUiConstants.ID_VIEW_REPOSITORIES);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		contentProvider = new TaskRepositoriesContentProvider();
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(contentProvider);
		viewer.setUseHashlookup(true);
		ViewerFilter[] filters = { new EmptyCategoriesFilter(contentProvider) };
		viewer.setFilters(filters);
		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		viewer.setSorter(new TaskRepositoriesViewSorter());

		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				WorkbenchUtil.openProperties(getSite());
			}
		});

		final IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		new GradientDrawer(themeManager, getViewer()) {
			@Override
			protected boolean shouldApplyGradient(org.eclipse.swt.widgets.Event event) {
				return event.item.getData() instanceof Category;
			}
		};

		makeActions();
		hookContextMenu();
		hookGlobalActions();
		contributeToActionBars();
		getViewer().expandAll();
		getSite().setSelectionProvider(getViewer());
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteRepositoryAction);
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), resetConfigurationAction);
	}

	private void makeActions() {
		deleteRepositoryAction = new DeleteTaskRepositoryAction();
		viewer.addSelectionChangedListener(deleteRepositoryAction);

		resetConfigurationAction = new UpdateRepositoryConfigurationAction();
		resetConfigurationAction.setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$
		viewer.addSelectionChangedListener(resetConfigurationAction);

		offlineAction = new DisconnectRepositoryAction();
		viewer.addSelectionChangedListener(offlineAction);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskRepositoriesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(addRepositoryAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator("new")); //$NON-NLS-1$
		manager.add(addRepositoryAction);
		manager.add(new Separator("edit")); //$NON-NLS-1$
		manager.add(deleteRepositoryAction);
		manager.add(resetConfigurationAction);
		manager.add(new Separator("operations")); //$NON-NLS-1$
		manager.add(offlineAction);
		manager.add(new Separator("repository")); //$NON-NLS-1$
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator());
		manager.add(new Separator("properties")); //$NON-NLS-1$
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addRepositoryAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refresh() {
		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.refresh();
			viewer.expandAll();
		}
	}

	public TreeViewer getViewer() {
		return viewer;
	}

}
