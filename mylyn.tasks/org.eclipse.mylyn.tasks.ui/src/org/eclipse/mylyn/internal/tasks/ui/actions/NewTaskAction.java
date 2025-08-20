/*******************************************************************************
 * Copyright (c) 2004, 2015 Mylyn project committers and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.commons.ui.TaskListImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoriesSorter;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
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
public class NewTaskAction extends BaseSelectionListenerAction
		implements IMenuCreator, IViewActionDelegate, IExecutableExtension {

	private static final String LABEL_NEW_TASK = Messages.NewTaskAction_new_task;

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create"; //$NON-NLS-1$

	private boolean skipRepositoryPage = false;

	private boolean localTask = false;

	private Menu dropDownMenu;

	public NewTaskAction(String label, boolean alwaysShowWizard) {
		super(label);
		if (!alwaysShowWizard) {
			setMenuCreator(this);
		}
		setText(label);
		setToolTipText(label);
		setId(ID);
		setEnabled(true);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	public NewTaskAction() {
		this(LABEL_NEW_TASK, false);
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			if (localTask) {
				TasksUiUtil.openNewLocalTaskEditor(shell, null);
			} else if (skipRepositoryPage) {
				TasksUiUtil.openNewTaskEditor(shell, null, TasksUiUtil.getSelectedRepository());
			} else {
				TasksUiUtil.openNewTaskEditor(shell, null, null);
			}
		}
	}

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void init(IViewPart view) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if ("skipFirstPage".equals(data)) { //$NON-NLS-1$
			skipRepositoryPage = true;
		}
		if ("local".equals(data)) { //$NON-NLS-1$
			localTask = true;
		}
	}

	@Override
	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	@Override
	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	private void addActionsToMenu() {
		NewTaskAction newTaskAction = new NewTaskAction(LABEL_NEW_TASK, true);
		newTaskAction.setText(Messages.NewTaskAction_Show_Wizard_Label);
		new ActionContributionItem(newTaskAction).fill(dropDownMenu, -1);
		new Separator().fill(dropDownMenu, -1);

		Set<TaskRepository> includedRepositories = new HashSet<>();
		TaskRepository localRepository = TasksUi.getRepositoryManager()
				.getRepository(LocalRepositoryConnector.CONNECTOR_KIND, LocalRepositoryConnector.REPOSITORY_URL);

		addRepositoryAction(localRepository);

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
					TaskRepository repository = TasksUi.getRepositoryManager()
							.getRepository(connectorKind, repositoryUrl);
					markForInclusion(includedRepositories, repository);

				}
			}
		}

		if (includedRepositories.isEmpty()) {
			// No repositories were added from working sets so show all
			for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
				markForInclusion(includedRepositories, repository);
			}
		}

		if (!includedRepositories.isEmpty()) {
			//new Separator().fill(dropDownMenu, -1);
			ArrayList<TaskRepository> listOfRepositories = new ArrayList<>(includedRepositories);
			final TaskRepositoriesSorter comparator = new TaskRepositoriesSorter();
			Collections.sort(listOfRepositories, (arg0, arg1) -> comparator.compare(null, arg0, arg1));
			for (TaskRepository taskRepository : listOfRepositories) {
				addRepositoryAction(taskRepository);
			}
		}
		new Separator().fill(dropDownMenu, -1);
		new ActionContributionItem(new NewQueryAction()).fill(dropDownMenu, -1);
		new ActionContributionItem(new NewCategoryAction()).fill(dropDownMenu, -1);
		new Separator().fill(dropDownMenu, -1);
		AddRepositoryAction action = new AddRepositoryAction();
		action.setText(Messages.NewTaskAction_Add_Repository);
		action.setImageDescriptor(null);
		new ActionContributionItem(action).fill(dropDownMenu, -1);
		new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
	}

	private void markForInclusion(Set<TaskRepository> includedRepositories, TaskRepository repository) {
		if (repository != null && !repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
			AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(repository.getConnectorKind());
			if (connector != null) {
				if (ITaskRepositoryFilter.CAN_CREATE_NEW_TASK.accept(repository, connector)) {
					includedRepositories.add(repository);
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
		ImageDescriptor overlay = TasksUiPlugin.getDefault().getBrandManager().getOverlayIcon(repository);
		ImageDescriptor compositeDescriptor = new TaskListImageDescriptor(TasksUiImages.TASK_NEW, overlay, false,
				false);
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
