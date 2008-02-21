/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteTaskRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.EditRepositoryPropertiesAction;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesView extends ViewPart {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.views.repositories";

	private TableViewer viewer;

	private Action addRepositoryAction = new AddRepositoryAction();

	private BaseSelectionListenerAction deleteRepositoryAction;

	private BaseSelectionListenerAction repositoryPropertiesAction;

	private BaseSelectionListenerAction resetConfigurationAction;

	private DisconnectRepositoryAction offlineAction;

	private final ITaskRepositoryListener REPOSITORY_LISTENER = new ITaskRepositoryListener() {

		public void repositoriesRead() {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}

		private void asyncExec(Runnable runnable) {
			if (Display.getCurrent() != null) {
				runnable.run();
			} else {
				Display.getDefault().asyncExec(runnable);
			}
		}

		public void repositoryAdded(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}

		public void repositoryRemoved(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}

		public void repositorySettingsChanged(TaskRepository repository) {
			asyncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
		}
	};

	static class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return TasksUiPlugin.getRepositoryManager().getAllRepositories().toArray();
		}
	}

	public TaskRepositoriesView() {
		TasksUiPlugin.getRepositoryManager().addListener(REPOSITORY_LISTENER);
	}

	public static TaskRepositoriesView getFromActivePerspective() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage == null)
				return null;
			IViewPart view = activePage.findView(ID);
			if (view instanceof TaskRepositoriesView)
				return (TaskRepositoriesView) view;
		}
		return null;
	}

	public static TaskRepositoriesView openInActivePerspective() {
		try {
			return (TaskRepositoriesView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.showView(ID);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ViewContentProvider());

		viewer.setLabelProvider(new DecoratingLabelProvider(new TaskRepositoryLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		viewer.setSorter(new TaskRepositoriesSorter());

//				new ViewerSorter() {
//
//			@Override
//			public int compare(Viewer viewer, Object e1, Object e2) {
//				if (e1 instanceof TaskRepository && e2 instanceof TaskRepository) {
//					TaskRepository t1 = (TaskRepository) e1;
//					TaskRepository t2 = (TaskRepository) e2;
//					return (t1.getKind() + t1.getUrl()).compareTo(t2.getKind() + t2.getUrl());
//				} else {
//					return super.compare(viewer, e1, e2);
//				}
//			}
//		});
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (repositoryPropertiesAction.isEnabled()) {
					repositoryPropertiesAction.run();
				}
			}
		});

		TasksUiPlugin.getRepositoryManager().addListener(new TaskRepositoryListener());

		makeActions();
		hookContextMenu();
		hookGlobalActions();
		contributeToActionBars();
		getSite().setSelectionProvider(getViewer());
	}

	private void hookGlobalActions() {
		IActionBars bars = getViewSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteRepositoryAction);
		bars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), resetConfigurationAction);
	}

	private void makeActions() {
		deleteRepositoryAction = new DeleteTaskRepositoryAction();
		deleteRepositoryAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		viewer.addSelectionChangedListener(deleteRepositoryAction);

		repositoryPropertiesAction = new EditRepositoryPropertiesAction();
		viewer.addSelectionChangedListener(repositoryPropertiesAction);

		resetConfigurationAction = new ResetRepositoryConfigurationAction();
		resetConfigurationAction.setActionDefinitionId("org.eclipse.ui.file.refresh");
		viewer.addSelectionChangedListener(resetConfigurationAction);

		offlineAction = new DisconnectRepositoryAction();
		viewer.addSelectionChangedListener(offlineAction);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
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
		manager.add(addRepositoryAction);
		manager.add(new Separator());
		manager.add(deleteRepositoryAction);
		manager.add(resetConfigurationAction);
		manager.add(new Separator());
		manager.add(offlineAction);
		manager.add(new Separator("repository"));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator());
		manager.add(repositoryPropertiesAction);
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
		}
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public class TaskRepositoryListener implements ITaskRepositoryListener {

		public void repositoriesRead() {
		}

		public void repositoryAdded(TaskRepository repository) {
		}

		public void repositoryRemoved(TaskRepository repository) {
		}

		public void repositorySettingsChanged(TaskRepository repository) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!getViewer().getControl().isDisposed()) {
						getViewer().refresh(true);
					}
				}
			});
		}

	}

}
