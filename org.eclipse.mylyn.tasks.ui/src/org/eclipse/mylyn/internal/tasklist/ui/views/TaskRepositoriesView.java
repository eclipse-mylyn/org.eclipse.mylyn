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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.tasklist.ui.actions.AddRepositoryAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.DeleteTaskRepositoryAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.EditRepositoryPropertiesAction;
import org.eclipse.mylar.provisional.tasklist.ITaskRepositoryListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesView extends ViewPart {

	public static final String ID = "org.eclipse.mylar.tasklist.repositories";

	public static final String NAME = "Task Repositories View";
	
	private TableViewer viewer;

	private Action addRepositoryAction = new AddRepositoryAction();

	private Action deleteRepositoryAction = new DeleteTaskRepositoryAction(this);

	private Action repositoryPropertiesAction = new EditRepositoryPropertiesAction(this);

	private final ITaskRepositoryListener REPOSITORY_LISTENER = new ITaskRepositoryListener() {

		public void repositoriesRead() {
			refresh();
		}

		public void repositoryAdded(TaskRepository repository) {
			refresh();	
		}

		public void repositoryRemoved(TaskRepository repository) {
			refresh();			
		} 
	};

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return MylarTaskListPlugin.getRepositoryManager().getAllRepositories().toArray();
		}
	}

	public TaskRepositoriesView() {
		MylarTaskListPlugin.getRepositoryManager().addListener(REPOSITORY_LISTENER);
	}

	public static TaskRepositoriesView getFromActivePerspective() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null)
			return null;
		IViewPart view = activePage.findView(ID);
		if (view instanceof TaskRepositoriesView)
			return (TaskRepositoriesView) view;
		return null;
	}

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new TaskRepositoryLabelProvider());
		viewer.setSorter(new ViewerSorter() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof TaskRepository && e2 instanceof TaskRepository) {
					TaskRepository t1 = (TaskRepository)e1;
					TaskRepository t2 = (TaskRepository)e2;
					return (t1.getKind() + t1.getUrl()).compareTo(t2.getKind() + t2.getUrl());
				} else {
 					return super.compare(viewer, e1, e2);
				}
			}
		});
		viewer.setInput(getViewSite());

		hookContextMenu();
		contributeToActionBars();
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
		manager.add(deleteRepositoryAction);
		manager.add(new Separator());
		manager.add(repositoryPropertiesAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(addRepositoryAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
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
}