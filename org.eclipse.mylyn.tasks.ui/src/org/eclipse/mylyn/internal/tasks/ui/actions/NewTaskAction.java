/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.commons.ui.CompositeElementImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 * @author Rob Elves
 */
@SuppressWarnings("restriction")
public class NewTaskAction extends BaseSelectionListenerAction implements IMenuCreator, IViewActionDelegate,
		IExecutableExtension {

	private static final String LABEL_NEW_TASK = Messages.NewTaskAction_new_task;

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create"; //$NON-NLS-1$

	private boolean skipRepositoryPage = false;

	private boolean localTask = false;

	private Menu dropDownMenu;

	public NewTaskAction() {
		super(LABEL_NEW_TASK);
		setMenuCreator(this);
		setText(LABEL_NEW_TASK);
		setToolTipText(LABEL_NEW_TASK);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			if (localTask) {
				TasksUiUtil.openNewLocalTaskEditor(shell, null);
			} else {
				if (skipRepositoryPage) {
					TasksUiUtil.openNewTaskEditor(shell, null, TasksUiUtil.getSelectedRepository());
				} else {
					TasksUiUtil.openNewTaskEditor(shell, null, null);
				}
			}
		}
	}

	public void run(IAction action) {
		run();
	}

	public void init(IViewPart view) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if ("skipFirstPage".equals(data)) { //$NON-NLS-1$
			this.skipRepositoryPage = true;
		}
		if ("local".equals(data)) { //$NON-NLS-1$
			this.localTask = true;
		}
	}

	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	private void addActionsToMenu() {

		TaskRepository localRepository = TasksUi.getRepositoryManager().getRepository(
				LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL);

		if (localRepository != null) {
			RepositorySelectionAction action = addRepositoryAction(localRepository);
			if (action != null) {
				action.setChecked(true);
				new Separator(LocalRepositoryConnector.CONNECTOR_KIND).fill(dropDownMenu, -1);
			}
		}
		IWorkingSet workingSet = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getAggregateWorkingSet();
		if (workingSet != null && !workingSet.isEmpty()) {
			// only add repositories in working set 
			for (IAdaptable iterable_element : workingSet.getElements()) {
				if (iterable_element instanceof RepositoryQuery) {
					String repositoryUrl = ((RepositoryQuery) iterable_element).getRepositoryUrl();
					String connectorKind = ((RepositoryQuery) iterable_element).getConnectorKind();
					TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connectorKind,
							repositoryUrl);
					if (repository != null && !repository.isOffline()) {
						addRepositoryAction(repository);
					}

				}
			}
		} else {
			// add all repositories
			for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
				if (repository != null && !repository.isOffline()
						&& !repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
					addRepositoryAction(repository);
				}
			}
		}
	}

	private RepositorySelectionAction addRepositoryAction(TaskRepository repository) {
		if (repository == null) {
			return null;
		}
		RepositorySelectionAction action = new RepositorySelectionAction(repository);
		ActionContributionItem item = new ActionContributionItem(action);
		action.setText(repository.getRepositoryLabel());
		ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(repository.getConnectorKind());
		CompositeImageDescriptor compositeDescriptor = new CompositeElementImageDescriptor(TasksUiImages.REPOSITORY,
				overlay, true);
		action.setImageDescriptor(compositeDescriptor);
		item.fill(dropDownMenu, -1);
		return action;
	}

	private class RepositorySelectionAction extends Action {

		private final TaskRepository repository;

		public RepositorySelectionAction(TaskRepository repository) {
			this.repository = repository;
			setText(repository.getRepositoryLabel());
		}

		@Override
		public void run() {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (repository.getConnectorKind().equalsIgnoreCase(LocalRepositoryConnector.CONNECTOR_KIND)) {
				TasksUiUtil.openNewLocalTaskEditor(shell, null);
			} else {
				TasksUiUtil.openNewTaskEditor(shell, null, repository);
			}
		}
	}

}
